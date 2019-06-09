package io.github.bananapuncher714.operation.gunsmoke.api.events;

import org.bukkit.event.Event;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;

public abstract class GunsmokeEvent extends Event {
	protected GunsmokeRepresentable item;
	
	public GunsmokeEvent( GunsmokeRepresentable item ) {
		this.item = item;
	}

	public GunsmokeRepresentable getRepresentable() {
		return item;
	}
}
