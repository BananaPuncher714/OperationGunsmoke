package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class Corner {
	Vector point;
	Set< AABB > positions = new HashSet< AABB >();
	
	public Corner( Vector point ) {
		this.point = point.clone();
	}
	
	public void addPosition( AABB position ) {
		positions.add( position );
	}
	
	public Set< AABB > getPositions() {
		return positions;
	}
	
	public boolean isDead() {
		return positions.isEmpty();
	}
}
