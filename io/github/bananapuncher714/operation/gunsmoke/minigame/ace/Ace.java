package io.github.bananapuncher714.operation.gunsmoke.minigame.ace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperLivingEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerPressRespawnButtonEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigGun;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;
import io.github.bananapuncher714.operation.gunsmoke.minigame.base.Minigame;

public class Ace extends Minigame implements Listener {
	protected final Gunsmoke plugin;
	protected final AceSettings settings;
	protected Team red;
	protected Team blue;
	
	public Ace( Gunsmoke plugin, AceSettings settings ) {
		this.plugin = plugin;
		this.settings = settings;
		
		red = scoreboard.registerNewTeam( "Red" );
		red.setCanSeeFriendlyInvisibles( true );
		red.setOption( Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM );
		red.setColor( ChatColor.RED );
		
		blue = scoreboard.registerNewTeam( "Blue" );
		blue.setCanSeeFriendlyInvisibles( true );
		blue.setOption( Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM );
		blue.setColor( ChatColor.BLUE );
	}
	
	public AceSettings getSettings() {
		return settings;
	}

	@Override
	public boolean join( GunsmokeEntity gEntity ) {
		super.join( gEntity );
		red.addEntry( gEntity.getUUID().toString() );
		
		spawn( gEntity );
		
		return true;
	}

	@Override
	public void leave( GunsmokeEntity gEntity ) {
		super.leave( gEntity );
		red.removeEntry( gEntity.getUUID().toString() );
		
		removeGunsmokeProperty( gEntity );
	}
	
	@Override
	public EnumTickResult tick() {
		return EnumTickResult.CONTINUE;
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents( this, plugin );
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll( this );
	}
	
	protected void removeGunsmokeProperty( GunsmokeEntity gEntity ) {
		if ( gEntity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gPlayer = ( GunsmokeEntityWrapperPlayer ) gEntity;
			Player player = gPlayer.getEntity();
			
			// Remove all gunsmoke related items from their inventory and destroy them
			ItemStack[] items = player.getInventory().getContents();
			for ( int i = 0; i < items.length; i++ ) {
				GunsmokeRepresentable item = plugin.getItemManager().getRepresentable( items[ i ] );
				if ( item != null ) {
					plugin.getItemManager().remove( item.getUUID() );
					items[ i ] = null;
					System.out.println( "detected " + item );
				}
			}
			player.getInventory().setContents( items );
		}
		
		if ( gEntity instanceof GunsmokeEntityWrapperLivingEntity ) {
			GunsmokeEntityWrapperLivingEntity gLEntity = ( GunsmokeEntityWrapperLivingEntity ) gEntity;
			LivingEntity entity = gLEntity.getEntity();
			
			// Remove their armor and hand items
			for ( EquipmentSlot slot : EquipmentSlot.values() ) {
				ItemStack itemstack = BukkitUtil.getEquipment( entity, slot );
				GunsmokeRepresentable item = plugin.getItemManager().getRepresentable( itemstack );
				if ( item != null ) {
					plugin.getItemManager().remove( item.getUUID() );
					BukkitUtil.setEquipment( entity, null, slot );
				}
			}
		}
	}
	
	protected void spawn( GunsmokeEntity entity ) {
		if ( entity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gPEntity = ( GunsmokeEntityWrapperPlayer ) entity;
			settings.getRedSpawn().apply( gPEntity.getEntity() );
		} else {
			entity.setLocation( settings.getRedSpawn().getLocation() );
		}
		
		giveGunsmokeProperty( entity );
	}
	
	protected void giveGunsmokeProperty( GunsmokeEntity gEntity ) {
		if ( gEntity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gPlayer = ( GunsmokeEntityWrapperPlayer ) gEntity;
			Player player = gPlayer.getEntity();
			
			// Give the player a gun
			ConfigWeaponOptions options = GunsmokeImplementation.getInstance().getGun( "remington870" );
			ConfigGun gun = new ConfigGun( options );
			
			plugin.getItemManager().register( gun );
			player.getInventory().addItem( gun.getItem() );
		} else if ( gEntity instanceof GunsmokeEntityWrapperLivingEntity ) {
			GunsmokeEntityWrapperLivingEntity gLEntity = ( GunsmokeEntityWrapperLivingEntity ) gEntity;
			LivingEntity entity = gLEntity.getEntity();
			
			// Give them items...
		}
	}
	
	@EventHandler
	private void onDeath( GunsmokeEntityDamageEvent event ) {
		GunsmokeEntity gEntity = event.getRepresentable();
		if ( !isParticipating( gEntity ) ) {
			return;
		}

		if ( gEntity.getHealth() - event.getDamage() <= 0 ) {
			// We don't want the player really dying, so we're going to cancel their death and "kill" them ourselves
			
			// There are no real custom entities that can act as players, so for now (20191018) we're going to see if they're a player.
			
			removeGunsmokeProperty( gEntity );
			
			if ( gEntity instanceof GunsmokeEntityWrapperPlayer ) {
				// Watch out for those fake players...
				// I don't have full control over them so I can't let them respawn. It would be too dangerous.
				GunsmokeEntityWrapperPlayer gPlayer = ( GunsmokeEntityWrapperPlayer ) gEntity;
				Player player = gPlayer.getEntity();
				
				if ( plugin.getProtocol().getHandler().isRealPlayer( player ) ) {
					// If they're real, then we want to "kill" them
					
					gPlayer.setHealth( 0 );
					gPlayer.setHealth( gPlayer.getMaxHealth() );
					
					player.setGameMode( GameMode.SPECTATOR );
					
					event.setCancelled( true );
				} else {
					// Deal with the fake NPCs here
				}
			} else if ( gEntity instanceof GunsmokeEntityWrapper ) {
				// Can't save an entity that can die only once
				leave( gEntity );
			}
		}
	}
	
	@EventHandler
	private void onRespawnButton( PlayerPressRespawnButtonEvent event ) {
		if ( !isParticipating( plugin.getItemManager().getEntityWrapper( event.getEntity() ) ) ) {
			return;
		}
		
		spawn( plugin.getItemManager().getEntityWrapper( event.getEntity() ) );
	}
	
	@EventHandler
	private void onDropItemEvent( PlayerDropItemEvent event ) {
		if ( !isParticipating( plugin.getItemManager().getEntityWrapper( event.getPlayer() ) ) ) {
			return;
		}
		
		event.setCancelled( true );
	}
}
