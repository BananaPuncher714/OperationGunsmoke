package io.github.bananapuncher714.operation.gunsmoke.api.events.item;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;

public class GunsmokeItemUnequipEvent extends GunsmokeItemEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public GunsmokeItemUnequipEvent( GunsmokeItem item ) {
		super( item );
	}

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
