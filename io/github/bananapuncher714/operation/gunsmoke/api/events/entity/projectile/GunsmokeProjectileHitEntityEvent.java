package io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public class GunsmokeProjectileHitEntityEvent extends GunsmokeProjectileEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancelled = false;
	protected GunsmokeEntity hitEntity;
	protected Location intersection;
	
	public GunsmokeProjectileHitEntityEvent( GunsmokeProjectile entity, GunsmokeEntity hitEntity, Location hitLocation ) {
		super( entity );
		this.hitEntity = hitEntity;
		intersection = hitLocation;
	}
	
	public GunsmokeEntity getHitEntity() {
		return hitEntity;
	}
	
	public Location getHitLocation() {
		return intersection;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean arg0 ) {
		cancelled = arg0;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
