package io.github.bananapuncher714.operation.gunsmoke.api.events.entity;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

/**
 * Gets called whenever an entity takes damage after calculations regarding their armor and stuff have been taken into account. 
 * Should be used for monitoring stuff like entity death or when a player's health drops below a certain level, unless it is a vanilla damage cause.
 */
public class GunsmokeEntityDamageEvent extends GunsmokeEntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancelled = false;
	protected DamageType type;
	protected double amount;
	protected DamageCause cause;
	
	public GunsmokeEntityDamageEvent( GunsmokeEntity entity, DamageType type, double amount, DamageCause cause ) {
		super( entity );
		this.type = type;
		this.amount = amount;
		this.cause = cause;
	}
	
	public double getDamage() {
		return amount;
	}

	public void setDamage( double amount ) {
		this.amount = amount;
	}

	public DamageType getType() {
		return type;
	}

	public DamageCause getCause() {
		return cause;
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
