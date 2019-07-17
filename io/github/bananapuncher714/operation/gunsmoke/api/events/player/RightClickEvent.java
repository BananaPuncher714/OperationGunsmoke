package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RightClickEvent extends HumanEntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	public RightClickEvent( HumanEntity player ) {
		super( player );
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean cancelled ) {
		this.cancelled = cancelled;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
