package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class PlayerPressRespawnButtonEvent extends HumanEntityEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public PlayerPressRespawnButtonEvent( HumanEntity who ) {
		super( who );
	}
	
	public void callEvent() {
		GunsmokeUtil.callEventSync( this );
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
