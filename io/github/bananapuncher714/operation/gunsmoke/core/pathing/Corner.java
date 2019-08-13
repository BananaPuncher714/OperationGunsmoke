package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class Corner {
	Region owner;
	AABB corner;
	
	public Corner( Region owner, AABB corner ) {
		this.owner = owner;
		this.corner = corner;
	}
	
	public Region getOwner() {
		return owner;
	}
	
	public AABB getCorner() {
		return corner;
	}
}
