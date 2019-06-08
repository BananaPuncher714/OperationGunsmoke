package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerJumpEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	public PlayerJumpEvent( Player player ) {
		super( player );
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
