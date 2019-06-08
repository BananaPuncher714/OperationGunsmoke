package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RightClickEntityEvent extends RightClickEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Entity clicked ;
	private boolean cancelled = false;
	
	public RightClickEntityEvent( Player player, Entity entity ) {
		super( player );
		clicked = entity;
	}
	
	public Entity getClickedEntity() {
		return clicked;
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
