package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class PathfinderElevation extends PathfinderRegion {
	ElevationLayer startLayer;
	ElevationLayer endLayer;
	
	public PathfinderElevation( RegionMap map, Location start, Location end ) {
		super( map, start, end );
		startLayer = map.getLayer( startRegion );
		endLayer = map.getLayer( endRegion );
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
	
	protected PathRegion elevationCalculate( ElevationLayer layer, PathRegion startPath, Region endRegion ) {
		// This appears to form an infinite loop...
		TreeSet< PathRegion > paths = new TreeSet< PathRegion >();
		Path startOptimized = startPath.optimized.copyOf();
		startPath = startPath.copyOf();
		startPath.optimized = startOptimized;
		paths.add( startPath );
		
		while ( !paths.isEmpty() ) {
			PathRegion path = paths.pollFirst();
			Region currentRegion = path.lastRegion();
			
			if ( currentRegion == endRegion ) {
				return path;
			}
			
			// For each neighbor, we want to go through there
			for ( Region neighbor : currentRegion.getNeighbors().keySet() ) {
				// Don't want to iterate over the same region
				if ( path.contains( neighbor ) ) {
					continue;
				}
				if ( neighbor != endRegion && !layer.contains( neighbor ) ) {
					continue;
				}
				
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				Path cost = optimize( start, end, newPath );
				Location last = cost.popLast();
				Vector closestPoint = VectorUtil.closestPoint( endRegion.getRegion(), last.toVector() );
				cost.addLocation( new Location( last.getWorld(), closestPoint.getX(), closestPoint.getY(), closestPoint.getZ() ) );
				double pathDist = cost.getDistance();
				newPath.setOptimized( cost, pathDist );
				paths.add( newPath );
			}
		}
		
		return paths.pollFirst();
	}
}