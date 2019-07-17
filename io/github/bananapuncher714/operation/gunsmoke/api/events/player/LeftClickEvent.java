package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class LeftClickEvent extends HumanEntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancelled = false;
	
	public LeftClickEvent( HumanEntity player ) {
		super( player );
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean arg0 ) {
		cancelled = arg0;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
