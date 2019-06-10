package io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public class GunsmokeProjectileHitBlockEvent extends GunsmokeProjectileEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancelled = false;
	protected Block hitBlock;
	protected Location intersection;
	
	public GunsmokeProjectileHitBlockEvent( GunsmokeProjectile entity, Block hitBlock, Location hitLocation ) {
		super( entity );
		this.hitBlock = hitBlock;
		intersection = hitLocation;
	}
	
	public Block getHitBlock() {
		return hitBlock;
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
