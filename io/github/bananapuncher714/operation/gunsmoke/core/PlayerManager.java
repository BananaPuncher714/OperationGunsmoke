package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.PlayerHoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.PlayerLeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.PlayerReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.PlayerRightClickEvent;

public class PlayerManager {
	private final Map< UUID, Long > holdingRC = new HashMap< UUID, Long >();
	private Gunsmoke plugin;
	
	protected PlayerManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask( plugin, this::updateHolding, 0, 1 );
	}
	
	private void updateHolding() {
		for ( Iterator< Entry< UUID, Long > > it = holdingRC.entrySet().iterator(); it.hasNext(); ) {
			Entry< UUID, Long > entry = it.next();
			Player player = Bukkit.getPlayer( entry.getKey() );
			if ( player == null ) {
				it.remove();
				continue;
			}
			PlayerHoldRightClickEvent event = new PlayerHoldRightClickEvent( player, entry.getValue() );
			Bukkit.getPluginManager().callEvent( event );
		}
	}
	
	public void setHolding( Player entity, boolean isHolding ) {
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		gEntity.setRightClicking( isHolding );
		if ( isHolding ) {
			if ( !holdingRC.containsKey( entity.getUniqueId() ) ) {
				holdingRC.put( entity.getUniqueId(), System.currentTimeMillis() );
				// Call the player press right click event if they are not already holding down right click
				PlayerRightClickEvent event = new PlayerRightClickEvent( entity );
				Bukkit.getPluginManager().callEvent( event );
			}
		} else {
			if ( holdingRC.containsKey( entity.getUniqueId() ) ) {
				holdingRC.remove( entity.getUniqueId() );
				// Call the player release right click event
				PlayerReleaseRightClickEvent releaseClickEvent = new PlayerReleaseRightClickEvent( entity );
				Bukkit.getPluginManager().callEvent( releaseClickEvent );
			}
		}
	}
	
	public boolean isHolding( Player entity ) {
		return holdingRC.containsKey( entity.getUniqueId() );
	}
	
	public long getHoldingTime( Player player ) {
		return System.currentTimeMillis() - ( holdingRC.containsKey( player.getUniqueId() ) ? holdingRC.get( player.getUniqueId() ) : 0 );
	}
	
	public void leftClick( Player player ) {
		PlayerLeftClickEvent event = new PlayerLeftClickEvent( player );
		Bukkit.getPluginManager().callEvent( event );
	}
}
