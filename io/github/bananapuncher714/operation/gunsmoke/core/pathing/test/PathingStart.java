package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Corner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionMap;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.ComparableVec;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.NodeGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class PathingStart {
	private final static File BASE = new File( System.getProperty( "user.dir" ) );
	
	public static void main( String[] args ) {
		File regions = new File( BASE + "/" + "regions" );
		RegionMap map = new RegionMap();
		for ( File file : regions.listFiles() ) {
			map.getRegionFor( RegionLoader.load( file ) );
		}
		for ( Region region : map.getRegions() ) {
			AABB box = region.getRegion();
			for ( Region otherRegion : map.getRegions() ) {
				AABB other = otherRegion.getRegion(); 
				if ( other == box ) {
					continue;
				}
				if ( VectorUtil.touching( box, other ) ) {
					region.addNeighbor( otherRegion );
				}
			}
		}
		RegionGenerator.generateCorners( map );
		System.out.println( "Done loading!" );
		System.out.println( "Loaded " + map.getRegions().size() + " regions" );
		
		Vector playerPos = new Vector( -293.3230759813689, 55.0, -37.6398436185673 );
		
		Vector self = new Vector( -0.5366845635742735,0.0,0.8437829574132742 );
		System.out.println( self.dot( self ) );
		
		RegionPanel.draw( playerPos, new PathRegion( map.getRegion( playerPos ) ), new ComparableVec( new Vector( 1, 0, 0 ), new Vector( 1, 0, 0 ) ), null );
		RegionPanel.pause();
		
		getVisibleCornersFor( map, playerPos );
	}
	
	public static Set< Corner > getVisibleCornersFor( RegionMap map, Vector start ) {
		Set< Corner > visible = new HashSet< Corner >();
		
		Region region = map.getRegion( start );
		if ( region == null  ) {
			return visible;
		}

		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		Map< PathRegion, ComparableVec > vecMap = new HashMap< PathRegion, ComparableVec >();
		
		// This map is in case if you wanted to loop back around
		Map< PathRegion, Vector > starts = new HashMap< PathRegion, Vector >();
		
		// Store the last edges intersected
		Map< PathRegion, Edge > lastEdges = new HashMap< PathRegion, Edge >();
		
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		starts.put( startPath, start.clone().setY( 0 ) );
		visible.addAll( region.getCorners() );

		int iterations = 0;
		while ( !paths.isEmpty() ) {
			iterations++;
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
				int val = 0;
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
					
					int maxRange = cv.getTowards( toMax );
					int minRange = cv.getTowards( toMin );

					int cvMaxRange = newVec.getTowards( cv.getMax() );
					int cvMinRange = newVec.getTowards( cv.getMin() );

					if ( ( maxRange != 0 && maxRange == minRange ) || ( cvMaxRange != 0 && cvMaxRange == cvMinRange ) ) {
						continue;
					}
					
					// First, see if both of the values are within the range
					if ( maxRange == 0 && minRange == 0 ) {
						val = 1;
					} else if ( cvMaxRange == 0 && cvMinRange == 0 ) {
						val = 2;
						newVec = new ComparableVec( cv.getMin(), cv.getMax() );
					} else {
						if ( maxRange == 0 && cvMaxRange == 0 ) {
							val = 3;
							newVec = new ComparableVec( toMax, cv.getMax() );
						} else if ( maxRange == 0 && cvMinRange == 0 ) {
							val = 4;
							newVec = new ComparableVec( toMax, cv.getMin() );
						} else if ( minRange == 0 && cvMaxRange == 0 ) {
							val = 5;
							newVec = new ComparableVec( toMin, cv.getMax() );
						} else {
							val = 6;
							// -0.5366845635742735,0.0,0.8437829574132742 is not a nice vector
							newVec = new ComparableVec( toMin, cv.getMin() );
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
				
				if ( !NodeGenerator.intersects( edge, origin, newVec.getMax() ) || !NodeGenerator.intersects( edge, origin, newVec.getMin() ) ) {
					System.out.println( "ERROR " + val );
				}
				
				RegionPanel.draw( origin, newPath, newVec, edge );
				try {
					Thread.sleep( 40 );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
//				RegionPanel.pause();

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
		
		System.out.println( "Iterations: " + iterations );
		
		return visible;
	}
}
