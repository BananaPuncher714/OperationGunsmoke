package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class PathfinderElevationFast extends PathfinderElevation {

	public PathfinderElevationFast( RegionMap map, Location start, Location end ) {
		super( map, start, end );
	}

	@Override
	public PathRegion calculate( long timeout ) {
		long startTime = System.currentTimeMillis();
		if ( start == null || endRegion == null ) {
			// START OR END DOES NOT EXIST!
			return null;
		}
		
		TreeSet< PathRegion > paths = new TreeSet< PathRegion >();
		PathRegion startPath = new PathRegion( startRegion );
		startPath.setOptimized( new Path( start ), 0 );
		paths.add( startPath );

		while ( !paths.isEmpty() ) {
			PathRegion path = paths.pollFirst();
			Region currentRegion = path.lastRegion();
			ElevationLayer layer = map.getLayer( currentRegion );
			
			if ( currentRegion == endRegion ) {
				Path optimized = optimize( start, end, path );
				path.optimized = optimized;
				return path;
			}
			
			if ( layer.contains( endRegion ) ) {
				PathRegion newPath = elevationCalculate( layer, path, endRegion );
				newPath.optimized.popLast();
				newPath.optimized.addLocation( end );
				newPath.weight = newPath.optimized.getDistance();
				System.out.println( "Direct: " + newPath.weight );
				paths.add( newPath );
			}
			
			for ( ElevationEdge edge : layer.getEdges() ) {
				Region to = edge.getTo();
				// Don't loop back around
				if ( !edge.traversable( 1.25 ) || path.contains( to ) ) {
					continue;
				}
				PathRegion newPath = elevationCalculate( layer, path, to );
				if ( newPath == null ) {
					continue;
				}
				newPath.optimized.popLast();
				newPath.optimized.addLocation( end );
				newPath.weight = newPath.optimized.getDistance();
				paths.add( newPath );
			}
			
			if ( System.currentTimeMillis() - startTime > timeout ) {
				System.out.println( "Timed out" );
				break;
			}
		}
		
		return paths.pollFirst();
	}
	
	@Override
	protected PathRegion elevationCalculate( ElevationLayer layer, PathRegion startPath, Region endRegion ) {
		TreeSet< PathRegion > paths = new TreeSet< PathRegion >();
		Set< Region > closedSet = new HashSet< Region >();
		Path startOptimized = startPath.optimized.copyOf();
		startPath = startPath.copyOf();
		startPath.optimized = startOptimized;
		closedSet.addAll( startPath.regions );
		paths.add( startPath );
		
		while ( !paths.isEmpty() ) {
			PathRegion path = paths.pollFirst();
			Region currentRegion = path.lastRegion();
			
			if ( currentRegion == endRegion ) {
				return path;
			}
			
			closedSet.add( path.lastRegion() );
			// For each neighbor, we want to go through there
			for ( Region neighbor : currentRegion.getNeighbors().keySet() ) {
				// Don't want to iterate over the same region
				if ( closedSet.contains( neighbor ) ) {
					continue;
				}
//				if ( path.contains( neighbor ) ) {
//					continue;
//				}
				if ( neighbor != endRegion && !layer.contains( neighbor ) ) {
					continue;
				}
				
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				
				// Ok, so we have a new path
				// Now fast optimize it
				
				Path cost = fastOptimize( start, newPath );
				Vector closestPoint = VectorUtil.closestPoint( endRegion.getRegion(), end.toVector() );
				cost.addLocation( new Location( end.getWorld(), closestPoint.getX(), closestPoint.getY(), closestPoint.getZ() ) );
				double pathDist = cost.getDistance();
				newPath.setOptimized( cost, pathDist );
				paths.add( newPath );
			}
		}
		
		return paths.pollFirst();
	}
	
	protected Path fastOptimize( Location start, PathRegion path ) {
		Path newPath = new Path( start );
		newPath.addLocation( start );
		Vector last = newPath.last().toVector();
		Region lastRegion = path.regions.get( 0 );
		for ( Region region : path.regions ) {
			Vector closest = VectorUtil.closestPoint( region.getRegion(), last );
			if ( lastRegion.getRegion().minY != region.getRegion().minY ) {
				newPath.addLocation( new Location( start.getWorld(), closest.getX(), lastRegion.getRegion().minY, closest.getZ() ) );
			}
			newPath.addLocation( new Location( start.getWorld(), closest.getX(), region.getRegion().minY, closest.getZ() ) );
			lastRegion = region;
		}
		return newPath;
	}
}
