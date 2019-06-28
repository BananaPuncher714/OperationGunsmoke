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
	public static final int BLOCK_DAMAGE_COOLDOWN = 100;
	public static final int BLOCK_DAMAGE_REGEN = 1;
	
	public static final int UPDATE_BLOCK_DELAY = 20 * 15;
	
	private Gunsmoke plugin;
	private Map< Location, Double > resistance = new HashMap< Location, Double >();
	private Map< Location, Integer > regenCooldown = new HashMap< Location, Integer >();
	private int tick = 0;
	
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
					continue;
				}
			
				// This means we need to figure out some kind of unloading system
				if ( ++health >= getDefaultResistanceFor( location.getBlock().getType() ) ) {
					iterator.remove();
				} else {
					entry.setValue( BLOCK_DAMAGE_REGEN );
				}
				setHealthAt( location, health );
			} else {
				entry.setValue( tick );
			}
		}
		if ( tick++ % UPDATE_BLOCK_DELAY == 0 ) {
			tick = 0;
			for ( Location location : resistance.keySet() ) {
				updateBlockStage( location );
			}
		}
	}
	
	public void damage( Location location, double damage ) {
		double health = getHealthAt( location );
		health -= damage;
		setHealthAt( location, health );
		regenCooldown.put( location, BLOCK_DAMAGE_COOLDOWN );
	}
	
	protected void updateBlockStage( Location location ) {
		double health = getHealthAt( location );
		boolean usePercent = true;
		if ( usePercent ) {
			double maxHp = getDefaultResistanceFor( location.getBlock().getType() );
			double percent = health / maxHp;
			int stage = 9 - ( int ) ( percent * 10 );
			plugin.getProtocol().getHandler().damageBlock( location, stage );
		} else {
			plugin.getProtocol().getHandler().damageBlock( location, 10 - ( int ) Math.ceil( health ) );
		}
	}
	
	public void setHealthAt( Location location, double health ) {
		location = BukkitUtil.getBlockLocation( location );
		if ( health <= 0 ) {
			resistance.remove( location );
			location.getBlock().setType( Material.AIR, false );
		} else {
			resistance.put( location, health );
			updateBlockStage( location );
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
		case WHITE_STAINED_GLASS:
		case OBSIDIAN: return -1;
		case STONE: return 5;
		case DIRT:
		case GRASS: return 2;
		case PRISMARINE_BRICKS: return 20;
		default: return 0;
		}
	}
}
