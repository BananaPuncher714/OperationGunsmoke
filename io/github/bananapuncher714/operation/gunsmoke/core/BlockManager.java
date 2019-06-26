package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class BlockManager {
	public static final int BLOCK_DAMAGE_COOLDOWN = 200;
	public static final int BLOCK_DAMAGE_REGEN = 1;
	
	private Gunsmoke plugin;
	private Map< Location, Double > resistance = new HashMap< Location, Double >();
	private Map< Location, Integer > regenCooldown = new HashMap< Location, Integer >();
	
	public BlockManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
	}
	
	private void update() {
		for ( Iterator< Entry< Location, Integer > > iterator = regenCooldown.entrySet().iterator(); iterator.hasNext(); ) {
			Entry< Location, Integer > entry = iterator.next();
			Location location = entry.getKey();
			int tick = entry.getValue();

			if ( tick-- <= 0 ) {
				double health = resistance.getOrDefault( location, -1.0 );
				
				// Don't want to keep around any dead variables
				if ( health == -1 ) {
					resistance.remove( location );
					iterator.remove();
				}
			
				// This means we need to figure out some kind of unloading system
				if ( ++health >= getDefaultResistanceFor( location.getBlock().getType() ) ) {
					iterator.remove();
				} else {
					entry.setValue( BLOCK_DAMAGE_REGEN );
				}
			}
		}
	}
	
	public void setHealthAt( Location location, double health ) {
		location = BukkitUtil.getBlockLocation( location );
		if ( health <= 0 ) {
			resistance.remove( location );
			location.getBlock().breakNaturally();
		} else {
			resistance.put( location, health );
			plugin.getProtocol().getHandler().damageBlock( location, ( int ) health - 1 ); 
		}
	}
	
	public double getHealthAt( Location location ) {
		return resistance.getOrDefault( location, getDefaultResistanceFor( location.getBlock().getType() ) );
	}
	
	public double getDefaultResistanceFor( Material material ) {
		switch ( material ) {
		case WATER:
		case LAVA:
		case BEDROCK:
		case BARRIER:
		case OBSIDIAN: return -1;
		case STONE: return 5;
		case DIRT:
		case GRASS: return 2;
		case PRISMARINE_BRICKS: return 20;
		default: return 0;
		}
	}
}
