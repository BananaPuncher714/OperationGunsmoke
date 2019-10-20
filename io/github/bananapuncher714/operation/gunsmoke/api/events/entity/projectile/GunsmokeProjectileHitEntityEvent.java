package io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultEntity;

public class GunsmokeProjectileHitEntityEvent extends GunsmokeProjectileHitEvent {
	private static final HandlerList handlers = new HandlerList();
	protected CollisionResultEntity result;
	
	public GunsmokeProjectileHitEntityEvent( GunsmokeProjectile entity, CollisionResultEntity result ) {
		super( entity, result );
		this.result = result;
	}
	
	@Override
	public CollisionResultEntity getCollisionResult() {
		return result;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
