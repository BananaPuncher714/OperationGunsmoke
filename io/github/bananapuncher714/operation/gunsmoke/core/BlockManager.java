package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockBreakEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockCreateEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockDamageEvent;

public class BlockManager {
	public static final int UPDATE_BLOCK_DELAY = 20 * 15;
	
	private Gunsmoke plugin;
	
	private Map< Location, GunsmokeBlock > blocks = new HashMap< Location, GunsmokeBlock >();
	
	public BlockManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask( plugin, this::update, 0, UPDATE_BLOCK_DELAY );
	}
	
	private void update() {
		for ( GunsmokeBlock block : blocks.values() ) {
			block.updateBlockStage();
		}
	}
	
	public void damage( Location location, double damage, GunsmokeRepresentable damager, DamageType type ) {
		GunsmokeBlock block = getBlockOrCreate( location );

		if ( block == null ) {
			return;
		}
		
		GunsmokeBlockDamageEvent damageEvent = new GunsmokeBlockDamageEvent( block, damage, damager, type );
		damageEvent.callEvent();
		if ( damageEvent.isCancelled() ) {
			return;
		}
		
		if ( block.isInvincible() ) {
			return;
		}
		
		block.damage( damage, type );

		if ( block.getHealth() <= 0 ) {
			GunsmokeBlockBreakEvent breakEvent = new GunsmokeBlockBreakEvent( block, damager );
			breakEvent.callEvent();
			if ( !breakEvent.isCancelled() ) {
				block.destroy();
				blocks.remove( block.getLocation() );
				plugin.getItemManager().remove( block.getUUID() );
			} else {
				block.updateBlockStage();
			}
		} else {
			block.updateBlockStage();
		}
	}
	
	public void destroy( Location location ) {
		for ( Iterator< Entry< Location, GunsmokeBlock > > iterator = blocks.entrySet().iterator(); iterator.hasNext(); ) {
			GunsmokeBlock block = iterator.next().getValue();
			
			if ( block.contains( location ) ) {
				iterator.remove();
				block.destroy();
				blocks.remove( block.getLocation() );
				plugin.getItemManager().remove( block.getUUID() );
			}
		}
	}
	
	public void updateBlockStage( Location location ) {
		GunsmokeBlock block = getBlockAt( location );
		block.updateBlockStage();
	}
	
	public GunsmokeBlock getBlockAt( Location location ) {
		for ( GunsmokeBlock block : blocks.values() ) {
			if ( block.contains( location ) ) {
				return block;
			}
		}
		return null;
	}
	
	// TODO do something about the random floating air blocks
	public GunsmokeBlock getBlockOrCreate( Location location ) {
		for ( GunsmokeBlock block : blocks.values() ) {
			if ( block.contains( location ) ) {
				return block;
			}
		}
		GunsmokeBlock block = new GunsmokeBlock( location, getDefaultResistanceFor( location.getBlock().getType() ));
		
		GunsmokeBlockCreateEvent event = new GunsmokeBlockCreateEvent( block );
		event.callEvent();
		block = event.getRepresentable();
		
		blocks.put( block.getLocation(), block );
		plugin.getItemManager().register( block );
		return block;
	}
	
	public void unregisterBlock( Location location ) {
		GunsmokeBlock block = getBlockAt( location );
		if ( block != null ) {
			unregisterBlock( block );
		}
	}
	
	public void unregisterBlock( GunsmokeBlock block ) {
		blocks.remove( block.getLocation() );
		plugin.getItemManager().remove( block.getUUID() );
	}
	
	public void registerBlock( GunsmokeBlock block ) {
		Location location = block.getLocation();
		GunsmokeBlock old = getBlockAt( location );
		if ( old != null ) {
			unregisterBlock( old );
		}
		
		blocks.put( block.getLocation(), block );
		plugin.getItemManager().register( block );
		block.updateBlockStage();
	}
	
	public double getDefaultResistanceFor( Material material ) {
		switch ( material ) {
		case WATER:
		case LAVA:
		case BEDROCK:
		case BARRIER:
		case WHITE_STAINED_GLASS:
		case JUKEBOX:
		case OBSIDIAN: return -1;
		case STONE: return 5;
		case DIRT:
		case GRASS: return 2;
		case PRISMARINE_BRICKS: return 20;
		default: return 0;
		}
	}
}
