package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class HoldRightClickEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	protected long ms;

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public HoldRightClickEvent( Player player, long ms ) {
		super( player );
		this.ms = ms;
	}
	
	public long getHeldTime() {
		return ms;
	}
}
