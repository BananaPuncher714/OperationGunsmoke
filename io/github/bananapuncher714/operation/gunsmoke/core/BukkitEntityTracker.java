package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDeathEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityLoadEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityUnloadEvent;

public class BukkitEntityTracker implements Listener {
	private Gunsmoke plugin;
	private Set< Entity > trackedEntities = new HashSet< Entity >();
	
	protected BukkitEntityTracker( Gunsmoke plugin ) {
		this.plugin = plugin;
		Bukkit.getScheduler().runTaskTimer( plugin, this::tick, 1, 1 );
		Bukkit.getPluginManager().registerEvents( this, plugin );
	}
	
	private void tick() {
		Set< Entity > newEntities = new HashSet< Entity >();
		for ( World world : Bukkit.getWorlds() ) {
			newEntities.addAll( world.getEntities() );
		}
		
		for ( Iterator< Entity > iterator = trackedEntities.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			if ( !newEntities.contains( entity ) ) {
				// The entities that we are tracking are vanilla entities. A wrapper for one type is going to be the same as another
				GunsmokeEntityUnloadEvent event = new GunsmokeEntityUnloadEvent( plugin.getItemManager().getEntityWrapper( entity ) );
				event.callEvent();
				
				iterator.remove();
			}
		}
		
		for ( Entity entity : newEntities ) {
			if ( !trackedEntities.contains( entity ) ) {
				trackedEntities.add( entity );
				GunsmokeEntityLoadEvent event = new GunsmokeEntityLoadEvent( plugin.getItemManager().getEntityWrapper( entity ) );
				event.callEvent();
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( EntitySpawnEvent event ) {
		trackedEntities.add( event.getEntity() );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( EntityDeathEvent event ) {
		trackedEntities.remove( event.getEntity() );
		GunsmokeEntityDeathEvent deathEvent = new GunsmokeEntityDeathEvent( plugin.getItemManager().getEntityWrapper( event.getEntity() ) );
		deathEvent.callEvent();
	}
}