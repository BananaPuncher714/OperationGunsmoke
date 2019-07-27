package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class Region {
	protected AABB region;
	protected Map< Region, Edge > neighbors = new HashMap< Region, Edge >();
	protected Set< AABB > walls = new HashSet< AABB >();
	
	public Region( AABB bounds ) {
		this.region = bounds;
	}

	public AABB getRegion() {
		return region;
	}
	
	public void addNeighbor( Region neighbor ) {
		Edge edge = new Edge( this, neighbor );
		neighbors.put( neighbor, edge );
	}

	public Map< Region, Edge > getNeighbors() {
		return neighbors;
	}
	
	public Set< AABB > getWalls() {
		return walls;
	}
}
