package io.github.bananapuncher714.operation.gunsmoke.api.events.block;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;

public class GunsmokeBlockDamageEvent extends GunsmokeBlockEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancelled = false;
	protected double damage;
	protected GunsmokeRepresentable damager;
	protected DamageType type;
	
	public GunsmokeBlockDamageEvent( GunsmokeBlock block, double damage, GunsmokeRepresentable damager, DamageType type ) {
		super( block );
	}
	
	public double getDamage() {
		return damage;
	}

	public void setDamage( double damage ) {
		this.damage = damage;
	}

	public GunsmokeRepresentable getDamager() {
		return damager;
	}

	public DamageType getType() {
		return type;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
