package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.List;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class EnclosingRegion {
	List< AABB > solid;
	List< AABB > space;
	
	public EnclosingRegion( List< AABB > solid, List< AABB > space ) {
		this.solid = solid;
		this.space = space;
	}
	
	public List< AABB > getSolid() {
		return solid;
	}
	public List< AABB > getSpace() {
		return space;
	}
}
