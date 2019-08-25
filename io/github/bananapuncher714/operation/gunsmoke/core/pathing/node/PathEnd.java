package io.github.bananapuncher714.operation.gunsmoke.core.pathing.node;

import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Corner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;

public class PathEnd {
	PathRegion path;
	Corner corner;
	
	public PathEnd( PathRegion path, Corner corner ) {
		this.path = path;
		this.corner = corner;
	}
	
	public PathRegion getPath() {
		return path;
	}
	
	public void setPath( PathRegion path ) {
		this.path = path;
	}
	
	public Corner getCorner() {
		return corner;
	}
	
	public void setCorner( Corner corner ) {
		this.corner = corner;
	}
}
