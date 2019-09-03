package io.github.bananapuncher714.operation.gunsmoke.api.events.entity;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

public class GunsmokeEntityUnloadEvent extends GunsmokeEntityEvent {
	private static final HandlerList handlers = new HandlerList();

	public GunsmokeEntityUnloadEvent( GunsmokeEntity entity ) {
		super( entity );
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
