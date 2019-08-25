package io.github.bananapuncher714.operation.gunsmoke.core.pathing.node;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.core.pathing.ComparableVec;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;

public class NodePath {
	ComparableVec vec;
	Vector origin;
	PathRegion path;
	Set< Edge > intersections = new HashSet< Edge >();
	
	public NodePath( ComparableVec vec, Vector origin, PathRegion path ) {
		this.vec = vec;
		this.origin = origin;
		this.path = path;
	}

	public ComparableVec getVec() {
		return vec;
	}

	public Vector getOrigin() {
		return origin;
	}

	public PathRegion getPath() {
		return path;
	}

	public Set<Edge> getIntersections() {
		return intersections;
	}
}
