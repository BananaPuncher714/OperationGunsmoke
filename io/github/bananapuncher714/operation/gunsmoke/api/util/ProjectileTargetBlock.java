package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.block.Block;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public class ProjectileTargetBlock extends ProjectileTarget {
	protected CollisionResultBlock intersection;
	
	public ProjectileTargetBlock( GunsmokeProjectile projectile, CollisionResultBlock intersection ) {
		super( projectile, intersection );
		this.intersection = intersection;
	}
	
	@Override
	public CollisionResultBlock getIntersection() {
		return intersection;
	}
	
	public Block getBlock() {
		return intersection.getBlock();
	}
}
