package io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;

public class GunsmokeProjectileHitBlockEvent extends GunsmokeProjectileHitEvent {
	private static final HandlerList handlers = new HandlerList();
	protected CollisionResultBlock hitBlock;
	
	public GunsmokeProjectileHitBlockEvent( GunsmokeProjectile entity, CollisionResultBlock result ) {
		super( entity, result );
		hitBlock = result;
	}
	
	@Override
	public CollisionResultBlock getCollisionResult() {
		return hitBlock;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
