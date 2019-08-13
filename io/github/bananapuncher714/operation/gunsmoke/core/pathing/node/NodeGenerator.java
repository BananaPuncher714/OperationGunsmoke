package io.github.bananapuncher714.operation.gunsmoke.core.pathing.node;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Corner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionMap;

public class NodeGenerator {
	public static Set< Corner > getVisibleCornersFor( RegionMap map, Location location ) {
		Set< Corner > visible = new HashSet< Corner >();
		
		Region region = map.getRegion( location );
		if ( region == null  ) {
			return visible;
		}

		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		Set< Region > passed = new HashSet< Region >();
		passed.add( region );
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		visible.addAll( region.getCorners() );
		
		while ( !paths.isEmpty() ) {
			PathRegion path = paths.poll();
			Region latest = path.lastRegion();

			passed.add( latest );
			for ( Region neighbor : latest.getNeighbors().keySet() ) {
				if ( passed.contains( latest ) ) {
					continue;
				}
				if ( neighbor.getRegion().minY - latest.getRegion().minY > 1.25 ) {
					continue;
				}
				Edge edge = latest.getNeighbors().get( neighbor );
				AABB box = edge.getIntersection();
				if ( box.lenY < .6 ) {
					continue;
				}
				
				
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
			}
		}
		
		return visible;
	}
}
