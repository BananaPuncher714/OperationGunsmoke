package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.HandlerList;

public class PlayerJumpEvent extends HumanEntityEvent {
	private static final HandlerList handlers = new HandlerList();

	public PlayerJumpEvent( HumanEntity player ) {
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
