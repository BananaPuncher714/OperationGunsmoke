package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class Region {
	protected AABB region;
	protected Map< Region, Edge > neighbors = new HashMap< Region, Edge >();
	protected Set< AABB > solids = new HashSet< AABB >();
	
	protected Set< Corner > corners = new HashSet< Corner >();
	
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
	
	public Set< Corner > getCorners() {
		return corners;
	}
	
	public Set< AABB > getSolids() {
		return solids;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( region == null ) ? 0 : region.hashCode() );
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Region other = ( Region ) obj;
		return other.region == region;
	}
}
