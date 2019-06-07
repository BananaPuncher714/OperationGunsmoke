package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {
	private Gunsmoke plugin;
	
	protected TaskManager( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	public BukkitTask sync( Runnable runnable ) {
		return Bukkit.getScheduler().runTask( plugin, runnable );
	}
	
	public void callEventSync( Event event ) {
		if ( plugin.getProtocol().getHandler().isCurrentThreadMain() ) {
			Bukkit.getPluginManager().callEvent( event );
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask( plugin, () -> { callEventSync( event ); } );
		}
	}
}
