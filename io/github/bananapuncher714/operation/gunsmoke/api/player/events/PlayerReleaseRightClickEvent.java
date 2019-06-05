package io.github.bananapuncher714.operation.gunsmoke.api.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerReleaseRightClickEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public PlayerReleaseRightClickEvent( Player player ) {
		super( player );
	}
}
