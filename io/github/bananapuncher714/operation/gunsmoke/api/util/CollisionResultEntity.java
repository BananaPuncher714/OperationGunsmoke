package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

public class CollisionResultEntity extends CollisionResult {
	protected GunsmokeEntity hitEntity;
	protected Location exit;
	protected BlockFace exitDirection;
	
	public CollisionResultEntity( GunsmokeEntity entity, Location enter, BlockFace direction, Location exit, BlockFace exitDirection ) {
		super( enter, direction, CollisionType.ENTITY );
		hitEntity = entity;
		
		if ( enter == null ) {
			setLocation( exit );
			setDirection( exitDirection );
		} else {
			System.out.println( exit + ":" + exitDirection );
			this.exit = exit;
			this.exitDirection = exitDirection;
		}
	}
	
	public GunsmokeEntity getEntity() {
		return hitEntity;
	}

	public Location getExit() {
		return exit;
	}

	public void setExit( Location exit ) {
		this.exit = exit;
	}

	public BlockFace getExitDirection() {
		return exitDirection;
	}

	public void setExitDirection( BlockFace exitDirection ) {
		this.exitDirection = exitDirection;
	}
	
	@Override
	public CollisionResultEntity copyOf() {
		return new CollisionResultEntity( hitEntity, location.clone(), direction, exit != null ? exit.clone() : null, exitDirection );
	}
}
