package io.github.bananapuncher714.operation.gunsmoke.implementation;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockBreakEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockCreateEvent;
import io.github.bananapuncher714.operation.gunsmoke.implementation.block.RegeneratingGunsmokeBlock;

public class EventListener implements Listener {
	@EventHandler
	private void onEvent( GunsmokeBlockCreateEvent event ) {
		GunsmokeBlock block = event.getRepresentable();
		RegeneratingGunsmokeBlock regenBlock = new RegeneratingGunsmokeBlock( block.getLocation(), block.getHealth() );
		event.setBlock( regenBlock );
	}
	
	@EventHandler
	private void onEvent( GunsmokeBlockBreakEvent event ) {
		if ( event.getRepresentable() instanceof RegeneratingGunsmokeBlock ) {
			event.setCancelled( true );
		}
	}
}
