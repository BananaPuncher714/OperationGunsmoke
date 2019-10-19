package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityLoadEvent;

public class MinigameListener implements Listener {
	protected MinigameManager manager;
	
	protected MinigameListener( MinigameManager manager ) {
		this.manager = manager;
	}
	
	@EventHandler
	private void onPlayerQuitEvent( PlayerQuitEvent event ) {
		manager.quit( event.getPlayer() );
	}
	
	@EventHandler
	private void onEntityLoadEvent( GunsmokeEntityLoadEvent event ) {
		manager.loadTracker( event.getRepresentable() );
	}
	
	@EventHandler
	private void onEntitySpawn( EntitySpawnEvent event ) {
		Bukkit.getScheduler().runTaskLater( manager.getPlugin(), new Runnable() {
			@Override
			public void run() {
				manager.loadTracker( manager.getPlugin().getItemManager().getEntityWrapper( event.getEntity() ) );
			}
		}, 1 );
	}
}
