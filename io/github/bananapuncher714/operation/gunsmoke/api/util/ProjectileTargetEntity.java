package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public class ProjectileTargetEntity extends ProjectileTarget {
	protected GunsmokeEntity hitEntity;
	
	public ProjectileTargetEntity( GunsmokeProjectile projectile, Location intersection, GunsmokeEntity hitEntity ) {
		super( projectile, intersection );
		this.hitEntity = hitEntity;
	}

	public GunsmokeEntity getHitEntity() {
		return hitEntity;
	}
}
