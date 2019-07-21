package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashSet;
import java.util.Set;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class Region {
	protected AABB region;
	protected Set< AABB > neighbors = new HashSet< AABB >();
	
	public Region( AABB bounds ) {
		this.region = bounds;
	}

	public AABB getRegion() {
		return region;
	}
}
