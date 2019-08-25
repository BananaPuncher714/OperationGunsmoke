package io.github.bananapuncher714.operation.gunsmoke.core.pathing.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Corner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Path;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathfinderNode;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionMap;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.test.PathingStart;

public class PathfinderNodeTest extends PathfinderNode {

	public PathfinderNodeTest( RegionMap map, Vector start, Vector end ) {
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
		TreeSet< PathRegion > paths = new TreeSet< PathRegion >();
		Map< PathRegion, Vector > lastCorners = new HashMap< PathRegion, Vector >();
		Map< PathRegion, Set< Corner > > passedCorners = new HashMap< PathRegion, Set< Corner > >();
		
		Map< Corner, Double > cornerDistances = new HashMap< Corner, Double >();
		
		PathRegion startPath = new PathRegion( startRegion );
		paths.add( startPath );
		lastCorners.put( startPath, start.clone() );
		passedCorners.put( startPath, new HashSet< Corner >() );
		
		int iterations = 0;
		while ( !paths.isEmpty() ) {
			iterations++;
			PathRegion path = paths.pollFirst();
			Vector lastCorner = lastCorners.remove( path );
			Set< Corner > corners = passedCorners.remove( path );
			
			PathRegion result = canSee( map, lastCorner, end );
			if ( result != null ) {
				// Have a list of potential solutions
				System.out.println( "Iterations: " + iterations );
				Path optimized = optimize( start, end, result );
				result.setOptimized( optimized, optimized.getDistance() );
				return result;
			}
			
			for ( PathEnd corner : PathingStart.getVisibleCornersFor( path.lastRegion(), lastCorner ) ) {
				if ( corners.contains( corner.getCorner() ) ) {
					continue;
				}
				Set< Corner > newCornerSet = new HashSet< Corner >( corners );
				newCornerSet.add( corner.getCorner() );
				
				PathRegion newPath = path.copyOf();
				newPath.add( corner.getPath() );
				// TODO find out why the regions aren't connected
				Path cost = optimize( start, end, newPath );
				double pathDist = cost.getDistance();
				
				if ( cornerDistances.getOrDefault( corner.getCorner(), 0.0 ) > pathDist ) {
					continue;
				}
				cornerDistances.put( corner.getCorner(), pathDist );
				
				newPath.setOptimized( cost, pathDist );
				paths.add( newPath );
				Vector newLastCorner = corner.getCorner().getVector();
				lastCorners.put( newPath, newLastCorner );
				passedCorners.put( newPath, newCornerSet );
			}
			
			if ( System.currentTimeMillis() - startTime > timeout ) {
//				System.out.println( "Timed out" );
//				break;
			}
		}
		
		return paths.pollFirst();
	}
}
