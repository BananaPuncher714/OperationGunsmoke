package io.github.bananapuncher714.operation.gunsmoke.core.pathing.node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Corner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionMap;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class NodeGenerator {
	public static Vector get( double deg ) {
		return new Vector( Math.cos( Math.toRadians( deg ) ), 0, Math.sin( Math.toRadians( deg ) ) );
	}
	
	public static Set< Corner > getVisibleCornersFor( RegionMap map, Vector start ) {
		// A set of all visible corners
		Set< Corner > visible = new HashSet< Corner >();
		
		// First ensure that we are in a region
		Region region = map.getRegion( start );
		if ( region == null  ) {
			return visible;
		}

		// A queue of all paths that need to be extended
		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		// A map of regions that contains the field of view that it can see
		Map< PathRegion, ComparableVec > vecMap = new HashMap< PathRegion, ComparableVec >();
		
		// This map is in case if you wanted to loop back around
		Map< PathRegion, Vector > starts = new HashMap< PathRegion, Vector >();

		// Store the last edges intersected
		Map< PathRegion, Edge > lastEdges = new HashMap< PathRegion, Edge >();
		
		// Create the first path
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		starts.put( startPath, start.clone().setY( 0 ) );
		
		visible.addAll( region.getCorners() );

		while ( !paths.isEmpty() ) {
			PathRegion path = paths.poll();
			Region latest = path.lastRegion();
			ComparableVec originalVec = vecMap.remove( path );
			Vector startVector = starts.remove( path );
			Edge lastEdge = lastEdges.remove( path );
			
			for ( Region neighbor : latest.getNeighbors().keySet() ) {
				if ( path.contains( neighbor ) ) {
					continue;
				}
				// Check if we can reach this region
				if ( neighbor.getRegion().minY - latest.getRegion().minY > 1.25 ) {
					continue;
				}
				Edge edge = latest.getNeighbors().get( neighbor );
				AABB box = edge.getIntersection();
				// Check if we can physically fit in the region
				if ( box.lenY < .6 ) {
					continue;
				}
				
				Vector origin = startVector.clone();
				
				Vector toMax = new Vector( box.maxX, 0, box.maxZ ).subtract( origin ).setY( 0 ).normalize();
				Vector toMin = new Vector( box.minX, 0, box.minZ ).subtract( origin ).setY( 0 ).normalize();
				
				// Check if this region is visible
				ComparableVec cv = originalVec == null ? null : new ComparableVec( originalVec.getMin(), originalVec.getMax() );
				ComparableVec newVec = new ComparableVec( toMin, toMax );
				if ( cv != null ) {
					if ( lastEdge != null ) {
						AABB prevBox = lastEdge.getIntersection();
						AABB edgeIntersection = new AABB( Math.min( box.maxX, prevBox.maxX ),
								0,
								Math.min( box.maxZ, prevBox.maxZ ),
								Math.max( box.minX, prevBox.minX ),
								0,
								Math.max( box.minZ, prevBox.minZ ) );
						if ( edgeIntersection.lenX != 0 ^ edgeIntersection.lenZ != 0 ) {
							Vector reverseNormal = edge.getNormal().clone().add( edge.getNormal() ).subtract( new Vector( 1, 1, 1 ) ).multiply( -1 );
							double xDist = ( box.oriX - origin.getX() ) * edge.getNormal().getX() * 2;
							double zDist = ( box.oriZ - origin.getZ() ) * edge.getNormal().getZ() * 2;
							origin.add( new Vector( xDist, 0, zDist ) );
							
							toMax = new Vector( box.maxX, 0, box.maxZ ).subtract( origin ).setY( 0 ).normalize();
							toMin = new Vector( box.minX, 0, box.minZ ).subtract( origin ).setY( 0 ).normalize();
							
							newVec = new ComparableVec( toMin, toMax );
							cv = new ComparableVec( cv.getMin().multiply( reverseNormal ), cv.getMax().multiply( reverseNormal ) );
						}
					}
					
					Vector cvMax = cv.getMax();
					Vector cvMin = cv.getMin();
					int maxRange = cv.getTowards( toMax );
					int minRange = cv.getTowards( toMin );

					int cvMaxRange = newVec.getTowards( cvMax );
					int cvMinRange = newVec.getTowards( cvMin );

					if ( ( maxRange != 0 && maxRange == minRange ) || ( cvMaxRange != 0 && cvMaxRange == cvMinRange ) ) {
						continue;
					}
					
					// First, see if both of the values are within the range
					if ( maxRange == 0 && minRange == 0 ) {
					} else if ( cvMaxRange == 0 && cvMinRange == 0 ) {
						newVec = new ComparableVec( cvMin, cvMax );
					} else {
						if ( maxRange == 0 && cvMaxRange == 0 ) {
							newVec = new ComparableVec( toMax, cvMax );
						} else if ( maxRange == 0 && cvMinRange == 0 ) {
							newVec = new ComparableVec( toMax, cvMin );
						} else if ( minRange == 0 && cvMaxRange == 0 ) {
							newVec = new ComparableVec( toMin, cvMax );
						} else {
							newVec = new ComparableVec( toMin, cvMin );
						}
					}
				}

				// At this point we can be certain we can see the region
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				paths.add( newPath );
				vecMap.put( newPath, newVec );
				starts.put( newPath, origin );
				lastEdges.put( newPath, edge );
				
				for ( Corner corner : neighbor.getCorners() ) {
					AABB point = corner.getCorner();
					Vector cornerVec = new Vector( point.oriX, 0, point.oriZ );
					Vector toCorner = cornerVec.subtract( origin ).setY( 0 ).normalize();
					if ( newVec.getTowards( toCorner ) == 0  ) {
						visible.add( corner );
					}
				}
			}
		}
		
		return visible;
	}
	
	public static List< Edge > getEdgesFor( PathRegion path ) {
		List< Region > regions = path.getRegions();
		List< Edge > edges = new ArrayList< Edge >();
		for ( int i = 0; i < regions.size() - 1; i++ ) {
			Region currentRegion = regions.get( i );
			Region nextRegion = regions.get( i + 1 );
			
			Edge edge = currentRegion.getNeighbors().get( nextRegion );
			if ( edge != null ) {
				edges.add( edge );
			} else {
				System.out.println( "An edge doesn't exist between 2 neighbors!" );
			}
		}
		return edges;
	}
	
	public static boolean intersects( Edge edge, Vector origin, Vector ray ) {
		Vector point = VectorUtil.calculateVector( new Vector( edge.getIntersection().maxX, 0, edge.getIntersection().maxZ ), edge.getNormal(), origin, ray );
		if ( point != null ) {
			return Math.abs( point.getX() - edge.getIntersection().oriX ) <= edge.getIntersection().radX &&
					Math.abs( point.getZ() - edge.getIntersection().oriZ ) <= edge.getIntersection().radZ;
		}
		return false;
	}
}
