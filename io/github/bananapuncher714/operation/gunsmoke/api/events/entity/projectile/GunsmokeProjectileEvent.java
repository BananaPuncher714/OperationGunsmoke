package io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityEvent;

public abstract class GunsmokeProjectileEvent extends GunsmokeEntityEvent {
protected GunsmokeProjectile entity;
	
	public GunsmokeProjectileEvent( GunsmokeProjectile entity ) {
		super( entity );
		this.entity = entity;
	}

	@Override
	public GunsmokeProjectile getRepresentable() {
		return entity;
	}
}
