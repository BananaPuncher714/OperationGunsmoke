package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import org.bukkit.Location;

public class PathfinderNodes implements Pathfinder {
	protected Location start;
	protected Location end;
	
	public PathfinderNodes( Location start, Location stop ) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Path calculate( long timeout ) {
		
		return null;
	}

}
