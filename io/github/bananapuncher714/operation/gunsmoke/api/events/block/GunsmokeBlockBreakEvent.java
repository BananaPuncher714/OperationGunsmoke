package io.github.bananapuncher714.operation.gunsmoke.api.events.block;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;

public class GunsmokeBlockBreakEvent extends GunsmokeBlockEvent {
	private static final HandlerList handlers = new HandlerList();
	protected GunsmokeRepresentable breaker;
	
	public GunsmokeBlockBreakEvent( GunsmokeBlock block, GunsmokeRepresentable breaker ) {
		super( block );
		this.breaker = breaker;
	}
	
	public GunsmokeRepresentable getBreaker() {
		return breaker;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
