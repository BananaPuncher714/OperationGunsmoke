package io.github.bananapuncher714.operation.gunsmoke.test;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class ProneListener implements Listener {
	Gunsmoke plugin;
	
	long lastSneak = 0;
	
	public ProneListener( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJump( PlayerJumpEvent event ) {
		GunsmokeEntity entity = plugin.getEntityManager().getEntity( event.getPlayer().getUniqueId() );
		if ( entity.isProne() ) {
			entity.setProne( false );
			plugin.getProtocol().getHandler().set( event.getPlayer(), false );
			entity.update();
			System.out.println( "Jump Event" );
		}
	}
	
	@EventHandler
	public void onPlayerSneak( PlayerToggleSneakEvent event ) {
		Player player = event.getPlayer();
		GunsmokeEntity entity = plugin.getEntityManager().getEntity( event.getPlayer().getUniqueId() );
		if ( event.isSneaking() ) {
			if ( player.isOnGround() && !entity.isProne() ) {
				if ( System.currentTimeMillis() - lastSneak < 500 ) {
					entity.setProne( true );
					plugin.getProtocol().getHandler().set( player, true );
					entity.update();
					System.out.println( "Sneak Event" );
				}
				lastSneak = System.currentTimeMillis();
			}
		} else {
			if ( entity.isProne() ) {
				entity.setProne( false );
				plugin.getProtocol().getHandler().set( event.getPlayer(), false );
				entity.update();
			}
		}
	}
	
	@EventHandler
	public void onEvent( EntityUpdateItemEvent event ) {
		System.out.println( "Updated item!" );
		System.out.println( event.getSlot() + ":" + event.getItem() );
	}
	
	@EventHandler
	public void onEvent( LeftClickEvent event ) {
		System.out.println( "Left clicked!" );
	}
	
	@EventHandler
	public void onEvent( RightClickEvent event ) {
		System.out.println( "Right clicked!" );
	}
	
	@EventHandler
	public void onEvent( HoldRightClickEvent event ) {
		System.out.println( "Holding right click!" );
	}
	
	@EventHandler
	public void onEvent( ReleaseRightClickEvent event ) {
		System.out.println( "Released right click!" );
	}
	
	@EventHandler
	public void onEvent( DropItemEvent event ) {
		System.out.println( "Drop item event!" );
	}
	
	@EventHandler
	public void onEvent( AdvancementOpenEvent event ) {
		event.getPlayer().closeInventory();
		System.out.println( "Advancement Done! " + event.getTab() );
	}
}
