package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinigameListener implements Listener {
	protected MinigameManager manager;
	
	protected MinigameListener( MinigameManager manager ) {
		this.manager = manager;
	}
	
	@EventHandler
	private void onPlayerQuitEvent( PlayerQuitEvent event ) {
		manager.quit( event.getPlayer() );
	}
}
