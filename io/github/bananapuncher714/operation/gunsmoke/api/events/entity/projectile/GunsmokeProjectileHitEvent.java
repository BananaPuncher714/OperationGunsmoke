package io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile;

import org.bukkit.event.Cancellable;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult;

public abstract class GunsmokeProjectileHitEvent extends GunsmokeProjectileEvent implements Cancellable {
	protected boolean cancelled = false;
	protected CollisionResult result;
	
	public GunsmokeProjectileHitEvent( GunsmokeProjectile entity, CollisionResult result ) {
		super( entity );
		this.result = result;
	}
	
	public CollisionResult getCollisionResult() {
		return result;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean arg0 ) {
		cancelled = arg0;
	}
}
