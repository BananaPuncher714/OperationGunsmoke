package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class EntityLocationTracker {
	private static final int TICK_THRESHOLD = 20 * 3;
	private static final int DEFAULT_DELAY = 7; // 6 Seems to be accurate
	
	private Gunsmoke plugin;
	
	protected Map< UUID, List< Location > > entityPositions = new HashMap< UUID, List< Location > >();
	protected Map< UUID, Integer > delays = new HashMap< UUID, Integer >();
	
	public EntityLocationTracker( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
	}
	
	private void update() {
		Set< UUID > toRemove = new HashSet< UUID >( entityPositions.keySet() );
		
		for ( World world : Bukkit.getWorlds() ) {
			for ( Entity entity : world.getEntities() ) {
				if ( !entity.isValid() ) {
					continue;
				}
				toRemove.remove( entity.getUniqueId() );
				
				List< Location > list = entityPositions.get( entity.getUniqueId() );
				if ( list == null ) {
					list = new ArrayList< Location >();
					entityPositions.put( entity.getUniqueId(), list );
				}
				
				list.add( entity.getLocation() );
				while ( list.size() > TICK_THRESHOLD ) {
					list.remove( 0 );
				}
			}
		}
		
		for ( UUID uuid : toRemove ) {
			entityPositions.remove( uuid );
		}
	}
	
	public Location getLocationOf( UUID uuid ) {
		return getLocationOf( uuid, DEFAULT_DELAY );
	}
	
	public Location getLocationOf( UUID uuid, int delay ) {
		delay = Math.min( TICK_THRESHOLD, delay );
		
		if ( entityPositions.containsKey( uuid ) ) {
			List< Location > locations = entityPositions.get( uuid );
			return locations.get( locations.size() - Math.min( delay, locations.size() ) ).clone();
		}
		return null;
	}
}
