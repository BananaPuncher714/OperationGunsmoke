package io.github.bananapuncher714.operation.gunsmoke.api.events.block;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;

public class GunsmokeBlockCreateEvent extends GunsmokeBlockEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public GunsmokeBlockCreateEvent( GunsmokeBlock block ) {
		super( block );
	}
	
	public void setBlock( GunsmokeBlock block ) {
		this.block = block;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
