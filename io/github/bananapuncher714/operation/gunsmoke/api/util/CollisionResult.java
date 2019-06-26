package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CollisionResult {
	protected Location location;
	protected Vector direction;
	protected CollisionType collisionType;
	
	public CollisionResult( Location location, Vector direction, CollisionType type ) {
		this.location = location;
		this.direction = direction;
		this.collisionType = type;
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation( Location location ) {
		this.location = location;
	}
	
	public Vector getDirection() {
		return direction;
	}
	
	public void setDirection( Vector direction ) {
		this.direction = direction;
	}
	
	public CollisionType getCollisionType() {
		return collisionType;
	}

	public CollisionResult copyOf() {
		return new CollisionResult( location.clone(), direction.clone(), collisionType );
	}
	
	public enum CollisionType {
		ENTITY, BLOCK, MISS;
	}
}
