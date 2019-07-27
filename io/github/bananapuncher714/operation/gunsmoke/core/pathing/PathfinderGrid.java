package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;

import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class PathfinderGrid implements Pathfinder {
	private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
	
	protected Location start;
	protected Location end;
	
	public PathfinderGrid( Location start, Location end ) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public PathRegion calculate( long timeout ) {
		long startTime = System.currentTimeMillis();
		
		Set< Location > checked = new HashSet< Location >();
		checked.add( start );
		List< Path > paths = new ArrayList< Path >();
		Path startPath = new Path( start );
		paths.add( startPath );
		while ( !paths.isEmpty() ) {
			Path path = paths.remove( 0 );
			Location last = path.last();

			for ( int[] direction : DIRECTIONS ) {
				Location newLoc = last.clone().add( direction[ 0 ], 0, direction[ 1 ] );
				
				if ( BukkitUtil.getBlockLocation( newLoc ).equals( BukkitUtil.getBlockLocation( end ) ) ) {
					Path newPath = path.copyOf();
					newPath.addLocation( newLoc );
					return null;
				}
				
				if ( checked.contains( newLoc ) ) {
					continue;
				}
				checked.add( newLoc );
				
				if ( newLoc.getBlock().getType() != Material.AIR ) {
					continue;
				}
				
				Path newPath = path.copyOf();
				newPath.addLocation( newLoc );
				
				paths.add( newPath );
			}
			
			if ( System.currentTimeMillis() - startTime > timeout ) {
				return null;
			}
		}
		return null;
	}
}
