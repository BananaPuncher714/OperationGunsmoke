package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.HandlerList;

public class AdvancementOpenEvent extends HumanEntityEvent {
	private static final HandlerList handlers = new HandlerList();
	private final String tab;
	
	public AdvancementOpenEvent( HumanEntity who, String tab ) {
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
