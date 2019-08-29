package io.github.bananapuncher714.operation.gunsmoke.api.events.entity;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import io.github.bananapuncher714.operation.gunsmoke.api.RegenType;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

public class GunsmokeEntityRegenEvent extends GunsmokeEntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	protected double amount;
	protected RegenType type;
	protected RegainReason reason;
	protected boolean cancelled = false;
	
	public GunsmokeEntityRegenEvent( GunsmokeEntity entity, double amount, RegenType type, RegainReason reason ) {
		super( entity );
		this.amount = amount;
		this.type = type;
		this.reason = reason;
	}
	
	public GunsmokeEntityRegenEvent( GunsmokeEntity entity, double amount, RegenType type ) {
		this( entity, amount, type, RegainReason.CUSTOM );
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount( double amount ) {
		this.amount = amount;
	}

	public RegenType getType() {
		return type;
	}

	public RegainReason getReason() {
		return reason;
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
