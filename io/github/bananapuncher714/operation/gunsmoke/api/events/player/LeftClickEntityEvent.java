package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.HandlerList;

public class LeftClickEntityEvent extends LeftClickEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Entity hitEntity;
	
	public LeftClickEntityEvent( HumanEntity player, Entity entity ) {
		super( player );
		this.hitEntity = entity;
	}

	public Entity getHitEntity() {
		return hitEntity;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
