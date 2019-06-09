package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.events.player.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerProneEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;

public class PlayerManager {
	private final Map< UUID, Long > holdingRC = new HashMap< UUID, Long >();
	private final Map< UUID, ItemStack[] > heldItems = new HashMap< UUID, ItemStack[] >();
	private Gunsmoke plugin;
	
	protected PlayerManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask( plugin, this::updateHolding, 0, 1 );
	}
	
	private void updateHolding() {
		// Detect right clicking and call appropriate events
		for ( Iterator< Entry< UUID, Long > > it = holdingRC.entrySet().iterator(); it.hasNext(); ) {
			Entry< UUID, Long > entry = it.next();
			Player player = Bukkit.getPlayer( entry.getKey() );
			if ( player == null ) {
				it.remove();
				continue;
			}
			HoldRightClickEvent event = new HoldRightClickEvent( player, entry.getValue() );
			Bukkit.getPluginManager().callEvent( event );
		}
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			ItemStack[] items = heldItems.get( player.getUniqueId() );
			if ( items == null ) {
				items = new ItemStack[] { new ItemStack( Material.AIR ), new ItemStack( Material.AIR ) };
				heldItems.put( player.getUniqueId(), items );
			}
			
			if ( player.getEquipment().getItemInMainHand().hashCode() != items[ 0 ].hashCode() ) {
				EntityUpdateItemEvent event = new EntityUpdateItemEvent( player, items[ 0 ], EquipmentSlot.HAND );
				
				plugin.getTaskManager().callEventSync( event );
			}
			
			if ( player.getEquipment().getItemInOffHand().hashCode() != items[ 1 ].hashCode() ) {
				EntityUpdateItemEvent event = new EntityUpdateItemEvent( player, items[ 1 ], EquipmentSlot.OFF_HAND );
				
				plugin.getTaskManager().callEventSync( event );
			}
			
			items[ 0 ] = player.getEquipment().getItemInMainHand();
			items[ 1 ] = player.getEquipment().getItemInOffHand();
		}
	}
	
	public void setHolding( Player entity, boolean isHolding ) {
		GunsmokePlayer gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		gEntity.setRightClicking( isHolding );
		if ( isHolding ) {
			if ( !holdingRC.containsKey( entity.getUniqueId() ) ) {
				holdingRC.put( entity.getUniqueId(), System.currentTimeMillis() );
				// Call the player press right click event if they are not already holding down right click
				RightClickEvent event = new RightClickEvent( entity );
				plugin.getTaskManager().callEventSync( event );
			}
		} else {
			if ( holdingRC.containsKey( entity.getUniqueId() ) ) {
				holdingRC.remove( entity.getUniqueId() );
				// Call the player release right click event
				ReleaseRightClickEvent releaseClickEvent = new ReleaseRightClickEvent( entity );
				plugin.getTaskManager().callEventSync( releaseClickEvent );
			}
		}
	}
	
	public boolean isHolding( Player entity ) {
		return holdingRC.containsKey( entity.getUniqueId() );
	}
	
	public long getHoldingTime( Player player ) {
		return System.currentTimeMillis() - ( holdingRC.containsKey( player.getUniqueId() ) ? holdingRC.get( player.getUniqueId() ) : 0 );
	}
	
	public void leftClick( Player player, Cancellable parent ) {
		LeftClickEvent event = new LeftClickEvent( player );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void rightClickEntity( Player player, Entity clicked, Cancellable parent ) {
		RightClickEntityEvent event = new RightClickEntityEvent( player, clicked );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void leftClickEntity( Player player, Entity clicked, Cancellable parent ) {
		LeftClickEvent event = new LeftClickEntityEvent( player, clicked );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void setProne( Player player, boolean prone ) {
		GunsmokePlayer entity = plugin.getEntityManager().getEntity( player.getUniqueId() );
		if ( entity.isProne() == prone ) {
			return;
		}
		
		PlayerProneEvent event = new PlayerProneEvent( player, prone );
		plugin.getTaskManager().callEventSync( event );
		if ( prone && event.isCancelled() ) {
			return;
		}
		entity.setProne( prone );
		plugin.getProtocol().getHandler().set( player, prone );
		entity.update();
	}
}
