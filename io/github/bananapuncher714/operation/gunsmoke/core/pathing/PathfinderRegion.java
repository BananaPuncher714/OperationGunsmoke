package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PathfinderRegion implements Pathfinder {
	protected RegionMap map;
	protected Location start;
	protected Location end;
	protected Region startRegion;
	protected Region endRegion;
	
	
	public PathfinderRegion( RegionMap map, Location start, Location end ) {
		this.map = map;
		this.start = start.clone();
		this.end = end.clone();
		this.startRegion = map.getRegion( start );
		this.endRegion = map.getRegion( end );
	}

	@Override
	public PathRegion calculate( long timeout ) {
		long startTime = System.currentTimeMillis();
		if ( start == null || endRegion == null ) {
			// START OR END DOES NOT EXIST!
			return null;
		}
		Set< Location > checked = new HashSet< Location >();
		checked.add( start );
		TreeSet< PathRegion > paths = new TreeSet< PathRegion >();
		PathRegion startPath = new PathRegion( startRegion );
		paths.add( startPath );

		Path solution = null;
		double distance = 0;
		
		int solutions = 0;
		int iterations = 0;
		while ( !paths.isEmpty() ) {
			iterations++;
			PathRegion path = paths.pollFirst();
			Region currentRegion = path.lastRegion();
			
			if ( currentRegion == endRegion ) {
				// Our path has finally reached the end after a long long time
				// We can end this right here since we're in the same region
				return path;

				// Since we don't overestimate the cost then it means once we've found a solution we can't find a shorter one
//				double newDist = path.weight;
//				if ( solution == null || newDist < distance ) {
//					solution = path.optimized;
//					distance = newDist;
//				}
//				solutions++;
//				continue;
			}
			
			// For each neighbor, we want to go through there
			for ( Region neighbor : currentRegion.getNeighbors().keySet() ) {
				// Don't want to iterate over the same region
				if ( path.contains( neighbor ) ) {
					continue;
				}
				
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				Path cost = optimize( newPath );
				newPath.setOptimized( cost );
				paths.add( newPath );
			}
			
			if ( System.currentTimeMillis() - startTime > timeout ) {
				System.out.println( "Timed out" );
				break;
			}
		}
		System.out.println( "Considered " + iterations + " paths" );

		if ( solution == null ) {
			System.out.println( "No solution found!" );
		} else {
			System.out.println( "Considered " + solutions + " solutions" );
		}
		
		return null;
	}
	
	private Path optimize( PathRegion path ) {
		Vector lastSolid = start.toVector();
		Vector lastClosest = end.toVector();
		Vector solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
		
		Path optimized = new Path( start );
		
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
			lastClosest = end.toVector();
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
					if ( !nextEdge.intersects( lastSolid, solidToClosest ) ) {
						valid = false;
						break;
					}
				}
				if ( valid ) {
					closestEdge = i;
					lastSolid = closest;
					optimized.addLocation( new Location( start.getWorld(), closest.getX(), closest.getY(), closest.getZ() ) );
					if ( i == edges.size() - 1 ) {
						direct = true;
					}
					break;
				}
				lastClosest = closest;
			}
		}
		optimized.addLocation( end );
		return optimized;
		
		// So the way this optimization works is as follows:
		// Construct a vector from the last solid point to the end point
		// The last solid point is a point where it's guaranteed to be the fastest point
		// It should change each time a new point is found
		// With the vector, loop through each edge in reverse order
		// For each edge
		// Get the point closest to the vector
		// Construct a new vector from the last solid point to the new point
		// Loop through each edge in reverse from the point to the last solid point
		// If no edges intersect, then it is a successful point
		// Add this to the list of points on the path, and mark this as the last solid point
		// Otherwise, if an intersection is detected, then skip this
		// Repeat the for loop until there is a path from the start to the stop
		// The path is now optimized
	}
	
}
