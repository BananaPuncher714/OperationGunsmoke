package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.NodeGenerator;

public class PathfinderNode extends PathfinderRegion {
	
	public PathfinderNode( RegionMap map, Vector start, Vector end ) {
		super( map, start, end );
	}

	@Override
	public PathRegion calculate( long timeout ) {
		long startTime = System.currentTimeMillis();
		if ( start == null || endRegion == null ) {
			// START OR END DOES NOT EXIST!
			return null;
		}
		// TODO speed up traversal of regions?
		TreeSet< PathCorner > paths = new TreeSet< PathCorner >();
		
		// Keep track of the shortest paths possible
		Map< Corner, Double > cornerDistances = new HashMap< Corner, Double >();
		
		Set< Corner > traversedCorners = new HashSet< Corner >();
		
		PathCorner startPath = new PathCorner( new Corner( startRegion, start ) );
		paths.add( startPath );
		
		int iterations = 0;
		while ( !paths.isEmpty() ) {
			iterations++;
			PathCorner path = paths.pollFirst();
			Corner lastCorner = path.lastCorner();
			Vector cornerVec = lastCorner == null ? start.clone() : lastCorner.getVector();
			Region lastRegion = path.lastRegion();
			
			traversedCorners.add( lastCorner );
			for ( PathCorner pathCorner : NodeGenerator.getVisibleCornersFor( lastCorner ) ) {
				if ( traversedCorners.contains( pathCorner.lastCorner() ) ) {
					continue;
				}
				
				PathCorner newPath = path.copyOf();

				// This is in case something happens like having corners in the same region
				boolean valid = true;
				for ( int i = 1; i < pathCorner.getRegions().size(); i++ ) {
					if ( newPath.contains( pathCorner.getRegions().get( i ) ) ) {
						valid = false;
						break;
					}
				}
				if ( !valid ) {
					continue;
				}
				
				newPath.add( pathCorner );
				// newPath spans from start to the current corner
				
				// Optimize it all the way to the end
				// TODO fix this optimize since it doesn't take corners into consideration
				Path cost = optimize( start, end, newPath );
				double pathDist = cost.getDistance();
				
				if ( cornerDistances.getOrDefault( pathCorner.lastCorner(), 0.0 ) > pathDist ) {
					continue;
				}
				cornerDistances.put( pathCorner.lastCorner(), pathDist );
				
				
				
				
				
				newPath.setOptimized( cost, pathDist );
				paths.add( newPath );
			}
			
			if ( System.currentTimeMillis() - startTime > timeout ) {
				System.out.println( "Timed out" );
				break;
			}
		}
		
		return paths.pollFirst();
	}

	public static PathRegion canSee( RegionMap map, Vector start, Vector point ) {
		// A set of all visible corners
		Region endRegion = map.getRegion( point );
		
		// First ensure that we are in a region
		Region region = map.getRegion( start );
		if ( region == null || endRegion == null  ) {
			return null;
		}

		// A queue of all paths that need to be extended
		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		// A map of regions that contains the field of view that it can see
		Map< PathRegion, ComparableVec > vecMap = new HashMap< PathRegion, ComparableVec >();
		
		// This map is in case if you wanted to loop back around
		Map< PathRegion, Vector > starts = new HashMap< PathRegion, Vector >();

		// Store the last edges intersected
		Map< PathRegion, Set< Edge > > lastEdges = new HashMap< PathRegion, Set< Edge > >();
		
		// Create the first path
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		starts.put( startPath, start.clone().setY( 0 ) );

		while ( !paths.isEmpty() ) {
			PathRegion path = paths.poll();
			Region latest = path.lastRegion();
			ComparableVec originalVec = vecMap.remove( path );
			Vector startVector = starts.remove( path );
			Set< Edge > lastEdge = lastEdges.remove( path );			
			
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
				Set< Edge > edgeSet = new HashSet< Edge >();
				if ( cv != null ) {
					if ( lastEdge != null ) {
						boolean reversed = false;
						for ( Edge prevEdge : lastEdge ) {
							AABB prevBox = prevEdge.getIntersection();
							AABB edgeIntersection = new AABB( Math.min( box.maxX, prevBox.maxX ),
									0,
									Math.min( box.maxZ, prevBox.maxZ ),
									Math.max( box.minX, prevBox.minX ),
									0,
									Math.max( box.minZ, prevBox.minZ ) );
							if ( ( edgeIntersection.lenX > 0 ^ edgeIntersection.lenZ > 0 ) && ( edgeIntersection.lenX >= 0 && edgeIntersection.lenZ >= 0 ) ) {
								Vector reverseNormal = edge.getNormal().clone().add( edge.getNormal() ).subtract( new Vector( 1, 1, 1 ) ).multiply( -1 );
								double xDist = ( box.oriX - origin.getX() ) * edge.getNormal().getX() * 2;
								double zDist = ( box.oriZ - origin.getZ() ) * edge.getNormal().getZ() * 2;
								origin.add( new Vector( xDist, 0, zDist ) );
								
								toMax = new Vector( box.maxX, 0, box.maxZ ).subtract( origin ).setY( 0 ).normalize();
								toMin = new Vector( box.minX, 0, box.minZ ).subtract( origin ).setY( 0 ).normalize();
								
								newVec = new ComparableVec( toMin, toMax );
								cv = new ComparableVec( cv.getMin().multiply( reverseNormal ), cv.getMax().multiply( reverseNormal ) );
								reversed = true;
								break;
							}
						}
						if ( !reversed ) {
							edgeSet.addAll( lastEdge );
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
				edgeSet.add( edge );

				// At this point we can be certain we can see the region
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				paths.add( newPath );
				vecMap.put( newPath, newVec );
				starts.put( newPath, origin );
				lastEdges.put( newPath, edgeSet );
				
				if ( neighbor == endRegion ) {
					Vector toCorner = point.clone().subtract( origin ).setY( 0 ).normalize();
					if ( newVec.getTowards( toCorner ) == 0  ) {
						return newPath;
					}
				}
			}
		}
		
		return null;
	}
}
