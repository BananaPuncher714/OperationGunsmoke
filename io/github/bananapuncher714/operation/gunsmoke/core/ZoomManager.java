package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bananapuncher714.operation.gunsmoke.api.ZoomLevel;

public class ZoomManager {
	private Gunsmoke plugin;
	private final Map< UUID, ZoomLevel > zooms = new HashMap< UUID, ZoomLevel >();
	private final Map< UUID, PotionEffect > slowness = new HashMap< UUID, PotionEffect >();

	protected ZoomManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask( plugin, this::update, 0, 1 );
	}

	private synchronized void update() {
		for ( UUID uuid : zooms.keySet() ) {
			Player player = Bukkit.getPlayer( uuid );
			if ( player == null ) {
				continue;
			}
			if ( !player.hasPotionEffect( PotionEffectType.SLOW ) || !player.hasPotionEffect( PotionEffectType.JUMP ) ) {
				ZoomLevel level = zooms.get( uuid );
				if ( level != null ) {
					player.addPotionEffect( new PotionEffect( PotionEffectType.SLOW, 999999999, level.getSlowAmp(), true, false ), true );
					player.addPotionEffect( new PotionEffect( PotionEffectType.JUMP, 999999999, 129, true, false ), true );
				}
			}
		}
	}

	public synchronized void setZoom( LivingEntity player, ZoomLevel level ) {
		if ( player.hasPotionEffect( PotionEffectType.SLOW ) && !zooms.containsKey( player.getUniqueId() ) ) {
			slowness.put( player.getUniqueId(), player.getPotionEffect( PotionEffectType.SLOW ) );
			player.removePotionEffect( PotionEffectType.SLOW );
			player.removePotionEffect( PotionEffectType.JUMP );
		}
		zooms.put( player.getUniqueId(), level );
		if ( level != null ) {
			player.addPotionEffect( new PotionEffect( PotionEffectType.SLOW, 999999999, level.getSlowAmp(), true, false ), true );
			player.addPotionEffect( new PotionEffect( PotionEffectType.JUMP, 999999999, 129, true, false ), true );

			if ( player instanceof Player ) {
				Player entity = ( Player ) player;
				entity.setWalkSpeed( level.getSpeed() );
			}
		}
	}

	public synchronized void removeZoom( LivingEntity player ) {
		if ( player instanceof Player ) {
			Player entity = ( Player ) player;
			// I intend to allow custom walk speed values in the future; For now, it's set to the default player walk speed.
			if ( entity.getWalkSpeed() != 2f ) {
				entity.setWalkSpeed( .2f );
			}
			plugin.getPlayerManager().setHolding( entity, false );
		}
		player.removePotionEffect( PotionEffectType.SLOW );
		player.removePotionEffect( PotionEffectType.JUMP );
		zooms.remove( player.getUniqueId() );
		if ( slowness.containsKey( player.getUniqueId() ) ) {
			player.addPotionEffect( slowness.remove( player.getUniqueId() ) );
		}
	}

	public synchronized ZoomLevel getZoomLevel( Player player ) {
		// Map#containsKey may be redundant here
		return zooms.containsKey( player.getUniqueId() ) ? zooms.get( player.getUniqueId() ) : null;
	}
}
