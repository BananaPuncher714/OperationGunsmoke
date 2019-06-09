package io.github.bananapuncher714.operation.gunsmoke.api.events.item;

import io.github.bananapuncher714.operation.gunsmoke.api.events.GunsmokeEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;

public abstract class GunsmokeItemEvent extends GunsmokeEvent {
	protected GunsmokeItem item;
	
	public GunsmokeItemEvent( GunsmokeItem item ) {
		super( item );
		this.item = item;
	}
	
	public GunsmokeItem getItem() {
		return item;
	}
}
