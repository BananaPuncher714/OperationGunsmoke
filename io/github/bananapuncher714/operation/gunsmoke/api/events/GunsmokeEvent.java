package io.github.bananapuncher714.operation.gunsmoke.api.events;

import org.bukkit.event.Event;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public abstract class GunsmokeEvent extends Event {
	protected GunsmokeRepresentable item;
	
	public GunsmokeEvent( GunsmokeRepresentable item ) {
		this.item = item;
	}

	public GunsmokeRepresentable getRepresentable() {
		return item;
	}
	
	public void callEvent() {
		GunsmokeUtil.callEventSync( this );
	}
}
