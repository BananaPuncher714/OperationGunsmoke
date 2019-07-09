package io.github.bananapuncher714.operation.gunsmoke.api.util;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public class ProjectileTargetEntity extends ProjectileTarget {
	protected CollisionResultEntity collision;
	
	public ProjectileTargetEntity( GunsmokeProjectile projectile, CollisionResultEntity intersection ) {
		super( projectile, intersection );
		this.collision = intersection;
	}

	@Override
	public CollisionResultEntity getIntersection() {
		return collision;
	}
	
	public GunsmokeEntity getEntity() {
		return collision.getEntity();
	}
}
