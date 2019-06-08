package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DropItemEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public DropItemEvent( Player updater ) {
		super( updater );
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
