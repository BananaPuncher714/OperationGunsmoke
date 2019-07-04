package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	public static final int BLOCK_DAMAGE_COOLDOWN = 100;
	public static final int BLOCK_DAMAGE_REGEN = 1;
	
	public static final int UPDATE_BLOCK_DELAY = 20 * 15;
	
	private Gunsmoke plugin;
	
	private Map< UUID, GunsmokeBlock > blocks = new HashMap< UUID, GunsmokeBlock >();
	
	public BlockManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask( plugin, this::update, 0, 15 * 20 );
	}
	
	private void update() {
		for ( GunsmokeBlock block : blocks.values() ) {
			block.updateBlockStage();
		}
	}
	
	public void damage( Location location, double damage, GunsmokeRepresentable damager, DamageType type ) {
		GunsmokeBlock block = getBlockAt( location );
		
		GunsmokeBlockDamageEvent damageEvent = new GunsmokeBlockDamageEvent( block, damage, damager, type );
		damageEvent.callEvent();
		if ( damageEvent.isCancelled() ) {
			return;
		}
		
		block.damage( damage, type );

		if ( block.getHealth() <= 0 ) {
			GunsmokeBlockBreakEvent breakEvent = new GunsmokeBlockBreakEvent( block, damager );
			breakEvent.callEvent();
			block.destroy();
			blocks.remove( block.getUUID() );
			plugin.getItemManager().remove( block.getUUID() );
		} else {
			block.updateBlockStage();
		}
	}
	
	protected void updateBlockStage( Location location ) {
		GunsmokeBlock block = getBlockAt( location );
		block.updateBlockStage();
	}
	
	public GunsmokeBlock getBlockAt( Location location ) {
		for ( GunsmokeBlock block : blocks.values() ) {
			if ( block.contains( location ) ) {
				return block;
			}
		}
		GunsmokeBlock block = new GunsmokeBlock( location, getDefaultResistanceFor( location.getBlock().getType() ));
		
		GunsmokeBlockCreateEvent event = new GunsmokeBlockCreateEvent( block );
		event.callEvent();
		block = event.getRepresentable();
		
		blocks.put( block.getUUID(), block );
		plugin.getItemManager().register( block );
		return block;
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
