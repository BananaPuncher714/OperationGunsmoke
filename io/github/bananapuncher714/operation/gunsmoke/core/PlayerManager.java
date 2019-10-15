package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerProneEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class PlayerManager {
	private final Map< UUID, Long > holdingRC = new HashMap< UUID, Long >();
	private final Map< UUID, ItemStack[] > heldItems = new HashMap< UUID, ItemStack[] >();
	private Gunsmoke plugin;
	
	protected PlayerManager( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	// Determines if the player's held item has changed and calls the appropriate event for it
	protected void tick() {
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
				items = new ItemStack[] { new ItemStack( Material.AIR ),
						new ItemStack( Material.AIR ),
						new ItemStack( Material.AIR ),
						new ItemStack( Material.AIR ),
						new ItemStack( Material.AIR ),
						new ItemStack( Material.AIR )};
				heldItems.put( player.getUniqueId(), items );
			}
			
			int index = 0;
			for ( EquipmentSlot slot : EquipmentSlot.values() ) {
				ItemStack newItem = BukkitUtil.getEquipment( player, slot );
				if ( newItem != null ) {
					newItem = newItem.clone();
				}
				
				if ( newItem == null && items[ index ] == null ) {
					index++;
					continue;
				} else if ( newItem == null ^ items[ index ] == null ) {
					new PlayerUpdateItemEvent( player, items[ index ], slot ).callEvent();
				} else if ( newItem.hashCode() != items[ index ].hashCode() ) {
					ItemStack old = items[ index ];
					
					GunsmokeRepresentable oldRep = plugin.getItemManager().getRepresentable( old );
					GunsmokeRepresentable newRep = plugin.getItemManager().getRepresentable( newItem );
					
					// Don't bother calling the events if the item they're holding is the same GunsmokeRepresentable
					if ( oldRep != null && oldRep == newRep ) {
						items[ index++ ] = newItem; 
						continue;
					}
					new PlayerUpdateItemEvent( player, old, slot ).callEvent();
				}

				items[ index++ ] = newItem; 
			}
		}
	}
	
	protected void remove( HumanEntity player ) {
		setHolding( player, false );

		setProne( player, false, true );
		
		heldItems.remove( player.getUniqueId() );
		for ( EquipmentSlot slot : EquipmentSlot.values() ) {
			ItemStack item = BukkitUtil.getEquipment( player, slot );
			GunsmokeRepresentable rep = plugin.getItemManager().getRepresentable( item );
			if ( rep instanceof GunsmokeItem ) {
				GunsmokeItem gItem = ( GunsmokeItem ) rep;
				gItem.onUnequip();
			}
		}
	}
	
	public void setHolding( HumanEntity entity, boolean isHolding ) {
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
	
	public boolean isHolding( HumanEntity entity ) {
		return holdingRC.containsKey( entity.getUniqueId() );
	}
	
	public long getHoldingTime( HumanEntity player ) {
		return System.currentTimeMillis() - ( holdingRC.containsKey( player.getUniqueId() ) ? holdingRC.get( player.getUniqueId() ) : 0 );
	}

	public void rightClick( HumanEntity player, Cancellable parent ) {
		RightClickEvent event = new RightClickEvent( player );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void leftClick( HumanEntity player, Cancellable parent ) {
		LeftClickEvent event = new LeftClickEvent( player );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void rightClickEntity( HumanEntity player, Entity clicked, Cancellable parent ) {
		RightClickEntityEvent event = new RightClickEntityEvent( player, clicked );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void leftClickEntity( HumanEntity player, Entity clicked, Cancellable parent ) {
		LeftClickEvent event = new LeftClickEntityEvent( player, clicked );
		plugin.getTaskManager().callEventSync( event );
		if ( event.isCancelled() ) {
			parent.setCancelled( true );
		}
	}
	
	public void setProne( HumanEntity player, boolean prone ) {
		setProne( player, prone, false );
	}
	
	public void setProne( HumanEntity player, boolean prone, boolean force) {
		GunsmokePlayer entity = plugin.getEntityManager().getEntity( player.getUniqueId() );
		if ( !force && entity.isProne() == prone ) {
			return;
		}
		
		PlayerProneEvent event = new PlayerProneEvent( player, prone );
		plugin.getTaskManager().callEventSync( event );
		if ( prone && event.isCancelled() ) {
			return;
		}
		entity.setProne( prone );
		CrosshairMovement movement = plugin.getMovementManager().getMovement( player.getName() );
		plugin.getMovementManager().setMovement( player.getName(), null );
		Bukkit.getScheduler().runTaskLater( plugin, () -> {
			plugin.getProtocol().getHandler().set( player, prone );
			
			if ( movement != null ) {
				plugin.getMovementManager().setMovement( player.getName(), movement );
			}
		}, 2 );
		entity.update();
	}
}
