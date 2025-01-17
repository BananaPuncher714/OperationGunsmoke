package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.PlayerChunkMap;
import net.minecraft.server.v1_14_R1.PlayerChunkMap.EntityTracker;
import net.minecraft.server.v1_14_R1.WorldServer;

public class NMSTracker {
	private static Field ENTITYTRACKER_ENTITY;
	
	static {
		try {
			ENTITYTRACKER_ENTITY = EntityTracker.class.getDeclaredField( "tracker" );
			ENTITYTRACKER_ENTITY.setAccessible( true );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	protected NMSTracker() {
	}
	
	
	protected void tick() {
		replaceEntityTrackers();
	}
	
	protected CustomEntityTracker getEntityTrackerFor( org.bukkit.entity.Entity bukkitEntity ) {
		Entity entity = ( ( CraftEntity ) bukkitEntity ).getHandle();
		
		PlayerChunkMap tracker = ( ( WorldServer ) entity.getWorld() ).getChunkProvider().playerChunkMap;
		
		EntityTracker entry = tracker.trackedEntities.get( entity.getId() );
		
		CustomEntityTracker customTracker;
		if ( !( entry instanceof CustomEntityTracker ) ) {
			customTracker = new CustomEntityTracker( tracker, entity, entity.getEntityType().getChunkRange() * 16, entity.getEntityType().getUpdateInterval(), entity.getEntityType().isDeltaTracking() );
			
			customTracker.trackedPlayers.addAll( entry.trackedPlayers );
			
			tracker.trackedEntities.put( entity.getId(), customTracker );
		} else {
			customTracker = ( CustomEntityTracker ) entry;
		}
		
		return customTracker;
	}
	
	private void replaceEntityTrackers() {
		for ( World world : Bukkit.getWorlds() ) {
			PlayerChunkMap tracker = ( ( WorldServer ) ( ( CraftWorld ) world ).getHandle() ).getChunkProvider().playerChunkMap;
			
			Set< EntityTracker > previous = new HashSet< EntityTracker >( tracker.trackedEntities.values() );
			for ( EntityTracker tracked : previous ) {
				if ( !( tracked instanceof CustomEntityTracker ) ) {
					Entity entity;
					
					try {
						entity = ( Entity ) ENTITYTRACKER_ENTITY.get( tracked );
					} catch ( Exception exception ) {
						exception.printStackTrace();
						continue;
					}
					CustomEntityTracker customTracker = new CustomEntityTracker( tracker, entity, entity.getEntityType().getChunkRange() * 16, entity.getEntityType().getUpdateInterval(), entity.getEntityType().isDeltaTracking() );
					
					customTracker.trackedPlayers.addAll( tracked.trackedPlayers );
					
					tracker.trackedEntities.put( entity.getId(), customTracker );
				}
			}
		}
	}
}
