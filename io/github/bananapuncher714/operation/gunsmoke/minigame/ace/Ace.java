package io.github.bananapuncher714.operation.gunsmoke.minigame.ace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.Nameable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperLivingEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageByEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile.GunsmokeProjectileHitEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerPressRespawnButtonEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBullet;
import io.github.bananapuncher714.operation.gunsmoke.minigame.ace.classes.WeaponClassAssault;
import io.github.bananapuncher714.operation.gunsmoke.minigame.ace.classes.WeaponClassShotgun;
import io.github.bananapuncher714.operation.gunsmoke.minigame.ace.classes.WeaponClassSniper;
import io.github.bananapuncher714.operation.gunsmoke.minigame.ace.classes.WeaponClassSupport;
import io.github.bananapuncher714.operation.gunsmoke.minigame.base.Minigame;

public class Ace extends Minigame implements Listener {
	protected final AceSettings settings;
	
	protected AceCommand command;
	
	protected Map< String, WeaponClass > weaponClasses = new HashMap< String, WeaponClass >();
	protected Map< UUID, String > assignedClasses = new HashMap< UUID, String >();
	protected Map< UUID, Integer > kills = new HashMap< UUID, Integer >();
	
	protected String defClass;
	
	protected Team red;
	protected Team blue;
	
	public Ace( Gunsmoke plugin, AceSettings settings ) {
		super( plugin );
		this.settings = settings;
		
		red = scoreboard.registerNewTeam( "Red" );
		red.setCanSeeFriendlyInvisibles( true );
		red.setOption( Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM );
		red.setColor( ChatColor.RED );
		red.setAllowFriendlyFire( false );
		red.setOption( Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.NEVER );
		
		blue = scoreboard.registerNewTeam( "Blue" );
		blue.setCanSeeFriendlyInvisibles( true );
		blue.setOption( Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM );
		blue.setColor( ChatColor.BLUE );
		blue.setAllowFriendlyFire( false );
		blue.setOption( Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.NEVER );
		
		weaponClasses.put( "assault", new WeaponClassAssault() );
		weaponClasses.put( "sniper", new WeaponClassSniper() );
		weaponClasses.put( "shotgun", new WeaponClassShotgun() );
		weaponClasses.put( "support", new WeaponClassSupport() );
		defClass = "assault";
		
		command = new AceCommand( this );
	}
	
	public AceSettings getSettings() {
		return settings;
	}

	@Override
	public boolean join( GunsmokeEntity gEntity ) {
		super.join( gEntity );
		
		int redSize = red.getEntries().size();
		int blueSize = blue.getEntries().size();
		
		if ( redSize > blueSize ) {
			red.addEntry( gEntity.getUUID().toString() );
		} else {
			blue.addEntry( gEntity.getUUID().toString() );
		}
		
		if ( gEntity instanceof Nameable ) {
			Nameable nameable = ( Nameable ) gEntity;
			broadcast( ChatColor.GRAY + nameable.getName() + " has joined the game" );
		} else {
			broadcast( ChatColor.GRAY + "Someone has joined the game" );
		}
		
		spawn( gEntity );
		
		return true;
	}

	@Override
	public void leave( GunsmokeEntity gEntity ) {
		super.leave( gEntity );
		red.removeEntry( gEntity.getUUID().toString() );
		blue.removeEntry( gEntity.getUUID().toString() );
		
		removeGunsmokeProperty( gEntity );
		
		if ( gEntity instanceof Nameable ) {
			Nameable nameable = ( Nameable ) gEntity;
			broadcast( ChatColor.GRAY + nameable.getName() + " has left the game" );
		} else {
			broadcast( ChatColor.GRAY + "Someone has left the game" );
		}
		
		kills.remove( gEntity.getUUID() );
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
	
	public void onCommand( CommandSender sender, String[] args ) {
		command.onCommand( sender, args );
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
		List< PlayerSaveData > dataList;
		if ( red.hasEntry( entity.getUUID().toString() ) ) {
			dataList = settings.getRedSpawns();
		} else {
			dataList = settings.getBlueSpawns();
		}
		PlayerSaveData data = dataList.get( ThreadLocalRandom.current().nextInt() % dataList.size() );
		
		if ( entity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gPEntity = ( GunsmokeEntityWrapperPlayer ) entity;
			data.apply( gPEntity.getEntity() );
		} else {
			entity.setLocation( data.getLocation() );
		}
		
		giveGunsmokeProperty( entity );
	}
	
	protected void giveGunsmokeProperty( GunsmokeEntity gEntity ) {
		String classId = assignedClasses.get( gEntity.getUUID() );
		if ( classId == null ) {
			classId = defClass;
			assignedClasses.put( gEntity.getUUID(), classId );
		}
		WeaponClass wClass = weaponClasses.get( classId );
		
		removeGunsmokeProperty( gEntity );
		if ( gEntity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gPlayer = ( GunsmokeEntityWrapperPlayer ) gEntity;
			Player player = gPlayer.getEntity();
			
			player.getInventory().clear();
		}
		
		if ( wClass != null ) {
			wClass.equip( gEntity );
		}
	}
	
	@EventHandler
	private void onDeath( GunsmokeEntityDamageEvent event ) {
		GunsmokeEntity gEntity = event.getRepresentable();
		if ( !isParticipating( gEntity ) ) {
			return;
		}
		
		if ( gEntity.getHealth() - event.getDamage() <= 0 ) {
			// See if they deserve a killed by message or if they died mysteriously
			String name = "Someone";
			if ( gEntity instanceof Nameable ) {
				name = ( ( Nameable ) gEntity ).getName();
			}
			
			if ( event instanceof GunsmokeEntityDamageByEntityEvent ) {
				GunsmokeEntityDamageByEntityEvent damageEvent = ( GunsmokeEntityDamageByEntityEvent ) event;
				GunsmokeEntity damager = damageEvent.getDamager();
				String damagerName = "Someone";
				if ( damager instanceof Nameable ) {
					damagerName = ( ( Nameable ) damager ).getName();
				} else if ( damager instanceof ConfigBullet ) {
					GunsmokeEntity shooter = ( ( ConfigBullet ) damager ).getShooter();
					if ( shooter instanceof Nameable ) {
						damagerName = ( ( Nameable ) shooter ).getName();
					}
					damager = shooter;
				}
				
				int killAmount = kills.getOrDefault( gEntity.getUUID(), 0 );
				killAmount++;
				kills.put( gEntity.getUUID(), killAmount );
				
				if ( damager instanceof GunsmokeEntityWrapperPlayer ) {
					GunsmokeEntityWrapperPlayer playerWrapper = ( GunsmokeEntityWrapperPlayer ) damager;
					Player player = playerWrapper.getEntity();
					
					player.setLevel( killAmount );
				}
				
				broadcast( ChatColor.WHITE + name + " was killed by " + damagerName );
				
			} else {
				broadcast( ChatColor.WHITE + name + " has died mysteriously" );
			}
			
			int killAmount = kills.getOrDefault( gEntity.getUUID(), 0 );
			kills.remove( gEntity.getUUID() );
			broadcast( ChatColor.WHITE + name + " died with " + killAmount + " kills" );
			
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
				player.getInventory().clear();
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
	
	@EventHandler
	private void onProjectileHitEntityEvent( GunsmokeProjectileHitEntityEvent event ) {
		GunsmokeProjectile projectile = event.getRepresentable();
		GunsmokeEntity entity = event.getCollisionResult().getEntity();
		
		if ( !isParticipating( entity ) ) {
			return;
		}
		
		if ( projectile instanceof ConfigBullet ) {
			ConfigBullet bullet = ( ConfigBullet ) projectile;
			
			GunsmokeEntity shooter = bullet.getShooter();
			
			if ( entity != shooter && !( red.hasEntry( entity.getUUID().toString() ) ^ red.hasEntry( shooter.getUUID().toString() ) ) ) {
				event.setCancelled( true );
			}
		}
	}
	
	@EventHandler( priority = EventPriority.LOW )
	private void onEvent( BlockPlaceEvent event ) {
		if ( isParticipating( plugin.getItemManager().getEntityWrapper( event.getPlayer() ) ) ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler( priority = EventPriority.LOW )
	private void onEvent( BlockBreakEvent event ) {
		if ( isParticipating( plugin.getItemManager().getEntityWrapper( event.getPlayer() ) ) ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler( priority = EventPriority.LOW )
	private void onPlayerInteractEvent( PlayerInteractEvent event ) {
		GunsmokeEntity entity = plugin.getItemManager().getEntityWrapper( event.getPlayer() );
		if ( !isParticipating( entity ) ) {
			return;
		}
		if ( event.getHand() != EquipmentSlot.HAND ) {
			return;
		}
		if ( event.getAction() != Action.RIGHT_CLICK_BLOCK ) {
			return;
		}
		Block block = event.getClickedBlock();
		
		if ( block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN ) {
			Sign sign = ( Sign ) block.getState(); 
			String line = sign.getLine( 1 ).toLowerCase();
			
			WeaponClass wClass = weaponClasses.get( line );
			if ( wClass != null ) {
				assignedClasses.put( entity.getUUID(), line );
				removeGunsmokeProperty( entity );
				wClass.equip( entity );
			}
		}
	}
}
