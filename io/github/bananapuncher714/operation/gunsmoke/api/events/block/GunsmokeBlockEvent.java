package io.github.bananapuncher714.operation.gunsmoke.api.events.block;

import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.events.GunsmokeEvent;

public abstract class GunsmokeBlockEvent extends GunsmokeEvent {
	protected GunsmokeBlock block;
	
	public GunsmokeBlockEvent( GunsmokeBlock block ) {
		super( block );
		this.block = block;
	}
	
	@Override
	public GunsmokeBlock getRepresentable() {
		return block;
	}
}
