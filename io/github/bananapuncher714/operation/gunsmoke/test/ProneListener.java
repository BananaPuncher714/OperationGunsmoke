package io.github.bananapuncher714.operation.gunsmoke.test;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class ProneListener implements Listener {
	Gunsmoke plugin;
	GunsmokeItem item;
	
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
	public void onEvent( LeftClickEntityEvent event ) {
		System.out.println( "Left clicked entity!" );
	}
	
	@EventHandler
	public void onEvent( RightClickEntityEvent event ) {
		System.out.println( "Right clicked entity!" );
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
		if ( event.getPlayer().getEquipment().getItemInMainHand().getType() == Material.GOLDEN_CARROT ) {
			event.setCancelled( true );
			
			item = new TestGunsmokeItemInteractable( plugin );
			
			plugin.getItemManager().register( item );
			
			event.getPlayer().getEquipment().setItemInMainHand( item.getItem() );
		}
		System.out.println( "Drop item event!" );
	}
	
	@EventHandler
	public void onEvent( AdvancementOpenEvent event ) {
		System.out.println( "Advancement Done! " + event.getTab() );
	}
	
	@EventHandler
	public void onEvent( EntityDamageEvent event ) {
		Entity entity = event.getEntity();
		if ( entity instanceof LivingEntity ) {
			LivingEntity livingEntity = ( LivingEntity ) entity;
			livingEntity.setNoDamageTicks( 0 );
			livingEntity.setMaximumNoDamageTicks( Integer.MIN_VALUE );
		}
	}
}
