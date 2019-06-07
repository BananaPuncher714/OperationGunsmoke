package io.github.bananapuncher714.operation.gunsmoke.api.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AdvancementOpenEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final String tab;
	
	public AdvancementOpenEvent( Player who, String tab ) {
		super( who );
		this.tab = tab;
	}
	
	public String getTab() {
		return tab;
	}

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
