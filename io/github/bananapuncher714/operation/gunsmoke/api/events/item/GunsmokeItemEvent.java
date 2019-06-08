package io.github.bananapuncher714.operation.gunsmoke.api.events.item;

import org.bukkit.event.Event;

import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;

public abstract class GunsmokeItemEvent extends Event {
	protected GunsmokeItem item;
	
	public GunsmokeItemEvent( GunsmokeItem item ) {
		this.item = item;
	}
	
	public GunsmokeItem getItem() {
		return item;
	}
}
