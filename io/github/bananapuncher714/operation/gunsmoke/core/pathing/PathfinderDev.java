package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.NodeGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class PathfinderDev implements Pathfinder {
	protected RegionMap map;
	protected Vector start;
	protected Vector end;
	protected Region startRegion;
	protected Region endRegion;
	protected Corner startCorner;
	protected Corner endCorner;
	
	protected Map< Corner, Double > distances = new HashMap< Corner, Double >();
	
	public PathfinderDev( RegionMap map, Vector start, Vector end ) {
		this.map = map;
		this.start = start.clone();
		this.end = end.clone();
		this.startRegion = map.getRegion( start );
		this.endRegion = map.getRegion( end );
		this.startCorner = new Corner( startRegion, start );
		this.endCorner = new Corner( endRegion, end );
	}
	
	@Override
	public PathCorner calculate( long timeout ) {
		long startTime = System.currentTimeMillis();
		
		// Our organized set of paths, sorted in increasing order of distance
		TreeSet< PathCorner > paths = new TreeSet< PathCorner >();
		
		// Have a closed set
		Set< Corner > closedSet = new HashSet< Corner >();
		
		// Create our start path
		PathCorner startCorner = new PathCorner( this.startCorner );
		startCorner.setOptimized( new Path( start ), 0 );
		paths.add( startCorner );
		
		// While we still have possibilities
		while ( !paths.isEmpty() ) {
			// Remove the shortest path
			PathCorner path = paths.pollFirst();
			Corner lastCorner = path.lastCorner();
			
			// TODO
//			RegionPanel.draw( start, path, null, null );
//			RegionPanel.pause();
			
			if ( lastCorner == endCorner ) {
				// We found the shortest path
				return path;
			}
			
			closedSet.add( lastCorner );
			Set< PathCorner > detectedCorners = getPossibleCornerPaths( map, lastCorner.getVector(), lastCorner, end, endCorner );
			for ( PathCorner cornerPath : detectedCorners ) {
				if ( closedSet.contains( cornerPath.lastCorner() ) ) {
					continue;
				}
				
				// Don't bother checking if the corner visible is in the same region
				boolean valid = true;
				for ( int i = 1; i < cornerPath.getRegions().size(); i++ ) {
					if ( path.contains( cornerPath.getRegions().get( i ) ) ) {
						valid = false;
						break;
					}
				}
				if ( !valid ) {
					continue;
				}
				
				// What we need to do is connect the old path corner to the current path corner
				PathCorner newPath = path.copyOf();
				newPath.add( cornerPath );
				
				Vector pathEndPoint = cornerPath.getPath().last();
				
				// TODO There's definitely something wrong involved with the calculation of distances
				// May not be the best calculation but I must know...
				
				double distance = pathEndPoint.distance( end );
				
				Path oldPath = path.getPath().copyOf();
				
				oldPath.add( cornerPath.getPath() );
				
				// Add the paths together *then* get the new distance in case if the differences in locations mattered
				double pathDist = oldPath.getDistance();
				
				if ( distances.containsKey( newPath.lastCorner() ) ) {
					double cachedDist = distances.get( newPath.lastCorner() );
					if ( pathDist > cachedDist ) {
						continue;
					}
				}
				distances.put( newPath.lastCorner(), pathDist );
				
				distance += oldPath.getDistance();
				
				newPath.setOptimized( oldPath, distance );
				
				paths.add( newPath );
				
				// TODO
//				RegionPanel.addPath( newPath );
//				RegionPanel.draw( start, newPath, null, null );
//				if ( Double.isNaN( distance ) ) {
//					RegionPanel.draw( start, newPath, null, null );
//					RegionPanel.pause();
//				}
//				RegionPanel.pause();
				
			}
//			RegionPanel.pause();
//			RegionPanel.clearPaths();
			
			// Don't want to go into an infinite loop or take too much time
			if ( System.currentTimeMillis() - startTime > timeout ) {
				System.out.println( "Timed out" );
				break;
			}
		}
		
		// Return the best option even though we didn't manage to get the solution
		return paths.pollFirst();
	}
	
	public static Set< PathCorner > getPossibleCornerPaths( RegionMap map, final Vector start, Corner startCorner, final Vector end, Corner endCorner ) {
		Map< Corner, PathCorner > results = new HashMap< Corner, PathCorner >();
		
		Region region = startCorner.getOwner();
		Region endRegion = endCorner.getOwner();
		
		// If our current corner is in the same region as the end corner
		// We know that it's directly accessible
		if ( region == endRegion ) {
			PathCorner result = new PathCorner( startCorner );
			result.add( endCorner );
			
			Path optimized = new Path( start );
			optimized.addLocation( end.clone() );

			result.setOptimized( optimized, optimized.getDistance() );
			
			results.put( endCorner, result );
		}
		
		Queue< PathLead > paths = new ArrayDeque< PathLead >();
		
		PathRegion startPath = new PathRegion( region );
		
		PathLead startLead = new PathLead();
		startLead.currentPath = startPath;
		startLead.origin = start;
		paths.add( startLead );
		
		while ( !paths.isEmpty() ) {
			PathLead pathLead = paths.poll();
			PathRegion pathRegion = pathLead.currentPath;
			Region leadRegion = pathRegion.lastRegion();
			ComparableVec leadVec = pathLead.vector;
			Vector leadOrigin = pathLead.origin;
			Set< Edge > leadEdges = pathLead.edges;
			List< ReversedVector > reversedVectors = pathLead.corners;
			
			for ( Region neighbor : leadRegion.getNeighbors().keySet() ) {
				if ( pathRegion.contains( neighbor ) ) {
					continue;
				}
				// Check if we can reach this region
				if ( neighbor.getRegion().minY - leadRegion.getRegion().minY > 1.25 ) {
					continue;
				}
				Edge edge = leadRegion.getNeighbors().get( neighbor );
				AABB box = edge.getIntersection();
				// Check if we can physically fit in the region
				if ( box.lenY < .6 ) {
					continue;
				}
				
				PathLead newLead = new PathLead();
				
				newLead.corners.addAll( reversedVectors );
				
				Vector origin = leadOrigin.clone();
				
				Vector boxMax = new Vector( box.maxX, box.minY, box.maxZ );
				Vector boxMin = new Vector( box.minX, box.minY, box.minZ );
				
				Vector toMax = boxMax.clone().subtract( origin ).setY( 0 ).normalize();
				Vector toMin = boxMin.clone().subtract( origin ).setY( 0 ).normalize();
				
				// Check if this region is visible
				ComparableVec cv = leadVec == null ? null : new ComparableVec( leadVec.getMin(), leadVec.getMax() );
				ComparableVec newVec = new ComparableVec( toMin, toMax );
				boolean reversed = false;
				boolean canSeeMax = true;
				boolean canSeeMin = true;
				
				if ( cv != null ) {
					if ( leadEdges != null ) {
						for ( Edge prevEdge : leadEdges ) {
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
								Vector oldMid = cv.getMid();
								cv = new ComparableVec( cv.getMin().multiply( reverseNormal ), cv.getMax().multiply( reverseNormal ) );
								cv.mid = oldMid.multiply( reverseNormal );
								
								ReversedVector rVec = new ReversedVector( new Vector( xDist, 0, zDist ) );
								rVec.firstEdge = prevEdge;
								rVec.lastEdge = edge;
								newLead.corners.add( rVec );
								
								reversed = true;
								break;
							}
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
					} else if ( maxRange == 0 && cvMaxRange == 0 ) {
						newVec = new ComparableVec( toMax, cv.getMax() );
						canSeeMax = false;
					} else if ( maxRange == 0 && cvMinRange == 0 ) {
						newVec = new ComparableVec( cv.getMin(), toMax );
						canSeeMin = false;
					} else if ( minRange == 0 && cvMaxRange == 0 ) {
						newVec = new ComparableVec( toMin, cv.getMax() );
						canSeeMax = false;
					} else if ( minRange == 0 && cvMinRange == 0 ) {
						// -0.5366845635742735,0.0,0.8437829574132742 is not a nice vector
						newVec = new ComparableVec( cv.getMin(), toMin );
						canSeeMin = false;
					} else if ( cvMaxRange == 0 && cvMinRange == 0 ) {
						newVec = new ComparableVec( cv.getMin(), cv.getMax() );
						canSeeMax = false;
						canSeeMin = false;
					}
				}
				if ( !reversed ) {
					newLead.edges.addAll( leadEdges );
					newLead.edges.add( edge );
				}
				
				// At this point we can be certain we can see the region
				PathRegion newPath = pathRegion.copyOf();
				newPath.add( neighbor );

				newLead.currentPath = newPath;
				newLead.vector = newVec;
				newLead.origin = origin;
				paths.add( newLead );
				
				boolean[] canSee = canSee( origin, newVec, neighbor );
				
				if ( canSeeMax && !canSee[ 0 ] ) {
					Vector point = VectorUtil.calculateVector( new Vector( edge.getIntersection().maxX, 0, edge.getIntersection().maxZ ), edge.getNormal(), origin, newVec.getMin() ).setY( edge.getIntersection().minY );
					if ( point == null ) {
						point = VectorUtil.closestPoint( edge.getIntersection(), origin ).setY( edge.getIntersection().minY );
					}
					Corner corner = new Corner( neighbor, point );
					PathCorner resultPath = optimize( map, newLead, corner.getVector(), corner, start, startCorner );
					double weight = resultPath.weight;
					PathCorner prev = results.get( corner );
					
					if ( prev == null || prev.weight > weight ) {
						results.put( corner, resultPath );
					}
				}
				if ( canSeeMin && !canSee[ 1 ] ) {
					Vector point = VectorUtil.calculateVector( new Vector( edge.getIntersection().maxX, 0, edge.getIntersection().maxZ ), edge.getNormal(), origin, newVec.getMax() ).setY( edge.getIntersection().minY );
					if ( point == null ) {
						point = VectorUtil.closestPoint( edge.getIntersection(), origin ).setY( edge.getIntersection().minY );
					}
					Corner corner = new Corner( neighbor, point );
					PathCorner resultPath = optimize( map, newLead, corner.getVector(), corner, start, startCorner );
					double weight = resultPath.weight;
					PathCorner prev = results.get( corner );
					
					if ( prev == null || prev.weight > weight ) {
						results.put( corner, resultPath );
					}
				}
				
				if ( neighbor == endRegion ) {
					Vector cornerVec = end.clone();
					Vector toCorner = cornerVec.clone().subtract( origin ).setY( 0 ).normalize();
					if ( newVec.getTowards( toCorner ) == 0  ) {
						PathCorner resultPath = optimize( map, newLead, cornerVec, endCorner, start, startCorner );
						double weight = resultPath.weight;
						PathCorner prev = results.get( endCorner );
						
//						RegionPanel.draw( start, resultPath, null, null );
//						RegionPanel.pause();
						
						if ( prev == null || prev.weight > weight ) {
							results.put( endCorner, resultPath );
						}
					}
				}
			}
		}
		
		return new HashSet< PathCorner >( results.values() );
	}

	// Probably works, as long as the path is 2d
	public static Path optimize( RegionMap map, Vector start, Region startRegion, Vector end, Region endRegion, PathRegion path ) {
		Vector lastSolid = start.clone();
		Vector lastClosest = end.clone();
		Vector solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
		
		Path optimized = new Path( start );
		
		List< Region > regions = path.getRegions();
		List< Edge > edges = new ArrayList< Edge >();
		boolean startEdges = false;
		for ( int i = 0; i < regions.size() - 1; i++ ) {
			Region currentRegion = regions.get( i );
			Region nextRegion = regions.get( i + 1 );
			
			if ( !startEdges && currentRegion != startRegion ) {
				continue;
			}
			startEdges = true;
			
			if ( currentRegion == endRegion ) {
				break;
			}
			
			Edge edge = currentRegion.getNeighbors().get( nextRegion );
			if ( edge != null ) {
				edges.add( edge );
			} else {
				System.out.println( "An edge doesn't exist between 2 neighbors! " + currentRegion );
			}
		}
		
		// First check if we can directly go from start to stop
		boolean direct = true;
		for ( Edge edge : edges ) {
			if ( !edge.intersects( lastSolid, solidToLastClosest ) ) {
				direct = false;
				break;
			}
		}
		
		int closestEdge = -1;
		while ( !direct ) {
			lastClosest = end.clone();
			for ( int i = edges.size() - 1; i > closestEdge; i-- ) {
				// First construct a vector from the last solid to the last closest
				solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
				Edge edge = edges.get( i );
				// Get the edge and the closest point
				Vector closest = edge.getClosestPoint( lastSolid, solidToLastClosest );
				// Construct a new one that goes directly to the solid
				Vector solidToClosest = closest.clone().subtract( lastSolid ).normalize();
				boolean valid = true;
				for ( int j = i - 1; j > closestEdge; j-- ) {
					Edge nextEdge = edges.get( j );
					// This check is just for the fact that minecraft players have to jump
					if ( nextEdge.r1.region.minY != nextEdge.r2.region.minY || !nextEdge.intersects( lastSolid, solidToClosest ) ) {
						valid = false;
						break;
					}
				}
				if ( valid ) {
					double h1 = edge.r1.region.minY;
					double h2 = edge.r2.region.minY;
					closest.setY( h2 );
					
					closestEdge = i;
					lastSolid = closest;
					// Simple peasants can't fly
					if ( h1 == h2 ) {
						optimized.addLocation( new Vector( closest.getX(), h1, closest.getZ() ) );
					} else {
						optimized.addLocation( new Vector( closest.getX(), h1, closest.getZ() ) );
						optimized.addLocation( new Vector( closest.getX(), h2, closest.getZ() ) );
					}
					if ( i == edges.size() - 1 ) {
						direct = true;
					}
					break;
				}
				lastClosest = closest;
			}
		}
		optimized.addLocation( end.clone() );
		return optimized;
	}
	
	public static Path optimize3d( RegionMap map, Vector start, Region startRegion, Vector end, Region endRegion, PathRegion path ) {
		Vector lastSolid = start.clone();
		Vector lastClosest = end.clone();
		Vector solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
		
		Path optimized = new Path( start );
		
		List< Region > regions = path.getRegions();
		List< Edge > edges = new ArrayList< Edge >();
		boolean startEdges = false;
		for ( int i = 0; i < regions.size() - 1; i++ ) {
			Region currentRegion = regions.get( i );
			Region nextRegion = regions.get( i + 1 );
			
			if ( !startEdges && currentRegion != startRegion ) {
				continue;
			}
			startEdges = true;
			
			if ( currentRegion == endRegion ) {
				break;
			}
			
			Edge edge = currentRegion.getNeighbors().get( nextRegion );
			if ( edge != null ) {
				edges.add( edge );
			} else {
				System.out.println( "An edge doesn't exist between 2 neighbors! " + currentRegion );
			}
		}
		
		
		// First check if we can directly go from start to stop
		boolean direct = true;
		for ( Edge edge : edges ) {
			if ( !edge.intersects( lastSolid, solidToLastClosest ) ) {
				direct = false;
				break;
			}
		}
		
		int closestEdge = -1;
		while ( !direct ) {
			lastClosest = end.clone();
			for ( int i = edges.size() - 1; i > closestEdge; i-- ) {
				// First construct a vector from the last solid to the last closest
				solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
				Edge edge = edges.get( i );
				// Get the edge and the closest point
				Vector closest = edge.getClosestPoint( lastSolid, solidToLastClosest );
				// Construct a new one that goes directly to the solid
				Vector solidToClosest = closest.clone().subtract( lastSolid ).normalize();
				boolean valid = true;
				for ( int j = i - 1; j > closestEdge; j-- ) {
					Edge nextEdge = edges.get( j );
					// This check is just for the fact that minecraft players have to jump
					if ( nextEdge.r1.region.minY != nextEdge.r2.region.minY || !nextEdge.intersects( lastSolid, solidToClosest ) ) {
						valid = false;
						break;
					}
				}
				if ( valid ) {
					double h1 = edge.r1.region.minY;
					double h2 = edge.r2.region.minY;
					closest.setY( h2 );
					
					closestEdge = i;
					lastSolid = closest;
					// Simple peasants can't fly
					if ( h1 == h2 ) {
						optimized.addLocation( new Vector( closest.getX(), h1, closest.getZ() ) );
					} else {
						optimized.addLocation( new Vector( closest.getX(), h1, closest.getZ() ) );
						optimized.addLocation( new Vector( closest.getX(), h2, closest.getZ() ) );
					}
					if ( i == edges.size() - 1 ) {
						direct = true;
					}
					break;
				}
				lastClosest = closest;
			}
		}
		optimized.addLocation( end.clone() );
		return optimized;
	}
	
	public static Path fastOptimize( RegionMap map, Vector start, Region startRegion, Vector end, Region endRegion, PathRegion path ) {
		Vector lastSolid = start.clone();
		Vector lastClosest = end.clone();
		Vector solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
		
		Path optimized = new Path( start );
		
		List< Region > regions = path.getRegions();
		List< Edge > edges = new ArrayList< Edge >();
		boolean startEdges = false;
		for ( int i = 0; i < regions.size() - 1; i++ ) {
			Region currentRegion = regions.get( i );
			
			if ( !startEdges && currentRegion != startRegion ) {
				continue;
			}
			startEdges = true;
			
			if ( currentRegion == endRegion ) {
				break;
			}
			
			Region nextRegion = regions.get( i + 1 );
			
			Edge edge = currentRegion.getNeighbors().get( nextRegion );
			if ( edge != null ) {
				edges.add( edge );
			} else {
				System.out.println( "An edge doesn't exist between 2 neighbors! " + currentRegion );
			}
		}
		
		// First check if we can directly go from start to stop
		boolean direct = true;
		for ( Edge edge : edges ) {
			if ( !edge.intersects( lastSolid, solidToLastClosest ) ) {
				direct = false;
				break;
			}
		}
		
		optimized.addLocation( end.clone() );
		return optimized;
	}
	
	// I can only assume this works
	public static PathCorner optimize( RegionMap map, PathLead path, Vector endVector, Corner endCorner, Vector startVector, Corner start ) {
		PathCorner pathToCorner = new PathCorner( start );
		pathToCorner.add( path.currentPath );
		pathToCorner.add( endCorner );
		
		// First check if we can directly see the exit from the start
		if ( path.corners.isEmpty() ) {
			Path optimized = optimize( map, startVector, start.getOwner(), endVector, endCorner.getOwner(), pathToCorner );
			pathToCorner.setOptimized( optimized, 0 );
			return pathToCorner;
		}
		
		// Set up the variables
		// What we want to do is add the locations in backwards
		PathRegion reverseRoute = pathToCorner.reverse();
		
		// startPoint is the modified point that can see the end point
		Vector startPoint = path.origin.clone();
		Region endRegion = endCorner.getOwner();
		Vector endPoint = endVector.clone();
		// Add the last point since we're going to reverse this
		Path toCornerPath = new Path( endVector.clone() );
		// For each set of edges in reverse...
		Region intersectedRegion = null;
		Vector intersection = null;
		for ( int i = path.corners.size() - 1; i >= 0; i-- ) {
			// Get the reversed vector, as well as the edges
			ReversedVector reverseVec = path.corners.get( i );
			Edge first = reverseVec.firstEdge;
			Edge last = reverseVec.lastEdge;
			
			intersectedRegion = first.r2;
			if ( first.r1 == last.r1 || first.r1 == last.r2 ) {
				intersectedRegion = first.r1;
			}
			
			// Get a vector from the last know point to the origin
			// This is guaranteed to be visible
			Vector toCorner = endPoint.clone().subtract( startPoint );
			intersection = NodeGenerator.intersects( last, startPoint, toCorner );
			// Skip if the intersection is null for some reason, it shouldn't normally be
			if ( intersection == null ) {
				continue;
			}
			intersection.setY( last.getIntersection().minY );
			
			// Now, we have our end point and our point in the intersection so we need to get a path
			// Perhaps find a different way to optimize? We know our line absolutely passes through each point without failure
			Path optimizedShort = optimize( map, endPoint, endRegion, intersection, intersectedRegion, reverseRoute );
			
			toCornerPath.add( optimizedShort );
			
			endPoint = intersection.clone().setY( first.getIntersection().minY );
			endRegion = intersectedRegion;
			
			toCornerPath.addLocation( endPoint );
			
			// Undo the reflection piece by piece
			startPoint.subtract( reverseVec.reflectionVec );
		}
		Path lastOptimized = optimize( map, intersection, intersectedRegion, startVector, start.getOwner(), reverseRoute );
		toCornerPath.add( lastOptimized );
		// Add the starting point
		// Reverse itself so it's in order
		toCornerPath.reverseSelf();
		
		pathToCorner.setOptimized( toCornerPath, toCornerPath.getDistance() );
		
		return pathToCorner;
	}
	
	public static boolean[] canSee( Vector origin, ComparableVec vec, Region region ) {
		AABB box = region.getRegion();
		Vector c1 = new Vector( box.minX, origin.getY(), box.minZ ).subtract( origin ).normalize();
		Vector c2 = new Vector( box.minX, origin.getY(), box.maxZ ).subtract( origin ).normalize();
		Vector c3 = new Vector( box.maxX, origin.getY(), box.minZ ).subtract( origin ).normalize();
		Vector c4 = new Vector( box.maxX, origin.getY(), box.maxZ ).subtract( origin ).normalize();
		
		int r1 = vec.getTowards( c1 );
		int r2 = vec.getTowards( c2 );
		int r3 = vec.getTowards( c3 );
		int r4 = vec.getTowards( c4 );
		
		return new boolean[] { r1 != -1 && r2 != -1 && r3 != -1 && r4 != -1, r1 != 1 && r2 != 1 && r3 != 1 && r4 != 1 };
	}
	
	public static class PathLead {
		PathRegion currentPath;
		ComparableVec vector;
		Vector origin;
		Set< Edge > edges = new HashSet< Edge >();
		List< ReversedVector > corners = new ArrayList< ReversedVector >();
	}
	
	public static class ReversedVector {
		Vector reflectionVec;
		Edge firstEdge;
		Edge lastEdge;
		
		protected ReversedVector( Vector vec ) {
			this.reflectionVec = vec;
		}
	}
}
