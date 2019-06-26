package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public class ProjectileTargetBlock extends ProjectileTarget {
	protected Block hitBlock;
	
	public ProjectileTargetBlock( GunsmokeProjectile projectile, CollisionResult intersection, Block hitBlock ) {
		super( projectile, intersection );
		this.hitBlock = hitBlock;
	}
	
	public Block getHitBlock() {
		return hitBlock;
	}
}
