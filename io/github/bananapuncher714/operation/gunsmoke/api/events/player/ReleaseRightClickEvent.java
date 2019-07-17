package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.HandlerList;

public class ReleaseRightClickEvent extends HumanEntityEvent {
	private static final HandlerList handlers = new HandlerList();

	public ReleaseRightClickEvent( HumanEntity player ) {
		super( player );
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
