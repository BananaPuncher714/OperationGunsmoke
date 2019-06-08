package io.github.bananapuncher714.operation.gunsmoke.core.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;

import io.github.bananapuncher714.operation.gunsmoke.api.events.player.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class PlayerListener implements Listener {
	private Gunsmoke plugin;
	
	public PlayerListener( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	private void onPlayerDeathEvent( PlayerDeathEvent event ) {
		plugin.getPlayerManager().setHolding( event.getEntity(), false );
	}
	
	@EventHandler
	private void onPlayerTeleportEvent( PlayerTeleportEvent event ) {
		plugin.getPlayerManager().setHolding( event.getPlayer(), false );
	}
	
	@EventHandler
	private void onPlayerOpenInventoryEvent( InventoryOpenEvent event ) {
		plugin.getPlayerManager().setHolding( ( Player ) event.getPlayer(), false );
	}
	
	@EventHandler
	private void onPlayerSwitchItem( EntityUpdateItemEvent event ) {
		if ( event.getEntity() instanceof Player ) {
			plugin.getPlayerManager().setHolding( ( Player ) event.getEntity(), false );
		}
	}
	
	@EventHandler
	private void onPlayerQuitEvent( PlayerQuitEvent event ) {
		plugin.getPlayerManager().setHolding( ( Player ) event.getPlayer(), false );
		// TODO add weapon unequip here
	}
	
	// This is because bukkit has an abyssmal player left click detection system for adventure mode people
	@EventHandler( ignoreCancelled = false )
	private void onPlayerInteractEvent( PlayerAnimationEvent event ) {
		if ( event.getPlayer().getGameMode() == GameMode.ADVENTURE ) {
			plugin.getPlayerManager().leftClick( event.getPlayer() );
		}
	}
	
	@EventHandler
	private void onPlayerInteractEvent( PlayerInteractEvent event ) {
		if ( event.getHand() != EquipmentSlot.HAND ) {
			return;
		}
		// This gets called twice if a player breaks a block without anything behind it
		// Does not happen if the BlockBreakEvent is cancelled accordingly
		
		if ( event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK ) {
			plugin.getPlayerManager().leftClick( event.getPlayer() );
		}
	}
}
