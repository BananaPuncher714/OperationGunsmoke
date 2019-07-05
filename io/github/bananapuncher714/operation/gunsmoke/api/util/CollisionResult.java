package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class CollisionResult {
	protected Location location;
	protected BlockFace direction;
	protected CollisionType collisionType;
	
	public CollisionResult( Location location, BlockFace direction, CollisionType type ) {
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
	
	public BlockFace getDirection() {
		return direction;
	}
	
	public void setDirection( BlockFace direction ) {
		this.direction = direction;
	}
	
	public CollisionType getCollisionType() {
		return collisionType;
	}

	public CollisionResult copyOf() {
		return new CollisionResult( location.clone(), direction, collisionType );
	}
	
	public enum CollisionType {
		ENTITY, BLOCK, MISS;
	}
}
