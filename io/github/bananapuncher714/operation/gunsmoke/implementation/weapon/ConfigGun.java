package io.github.bananapuncher714.operation.gunsmoke.implementation.weapon;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.MovementModifier;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.MovementModifierRecoil;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.MDChat;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBullet;
import net.md_5.bungee.api.ChatMessageType;

public class ConfigGun extends GunsmokeItemInteractable implements Tickable {
	protected ItemStackMultiState display;
	
	protected long lastSwitched;
	protected long lastShot;
	protected long lastReloaded;
	protected long lastScoped;
	
	protected boolean isReloading = false;
	protected boolean isSwitching = false;
	protected boolean isScoping = false;
	
	protected boolean isScoped = false;
	
	protected double shots = 0;
	
	protected int bullets = 0;
	
	protected ConfigWeaponOptions options;
	
	public ConfigGun( ConfigWeaponOptions options ) {
		this.options = options;
		bullets = options.getClipSize();
		
		ItemStack item = new ItemStack( Material.BOW );
		item = BukkitUtil.setCustomModelData( item, options.getModel() );
		
		display = new ItemStackMultiState( new ItemStackGunsmoke( item ) );
	}
	
	public int getBulletsRemaining() {
		return bullets;
	}
	
	public void shoot() {
		long time = System.currentTimeMillis();
		// Do last shot check
		long timeSinceLastShot = time - lastShot;
		if ( timeSinceLastShot < options.getShootDelay() ) {
			return;
		}
		// Do is reloading check
		if ( isReloading || isSwitching ) {
			return;
		}
		
		if ( options.isUseAmmo() && bullets <= 0 ) {
			return;
		}
		
		Vector facing = holder.getLocation().getDirection();
		if ( isScoped ) {
			if ( timeSinceLastShot > options.getScopeSpreadRecover() ) {
				shots = 0;
			}
			double degrees = Math.sqrt( shots );
			degrees *= options.getScopeSpreadEnd() - options.getScopeSpreadStart();
			degrees += options.getScopeSpreadStart();
			
			facing = VectorUtil.randomizeSpread( facing, degrees, degrees );
			shots = Math.min( 1, shots + 1 / ( double ) options.getScopeSpreadShots() );
		} else {
			if ( timeSinceLastShot > options.getSpreadRecover() ) {
				shots = 0;
			}
			double degrees = Math.sqrt( shots );
			degrees *= options.getSpreadEnd() - options.getSpreadStart();
			degrees += options.getSpreadStart();
			
			facing = VectorUtil.randomizeSpread( facing, degrees, degrees );
			shots = Math.min( 1, shots + 1 / ( double ) options.getSpreadShots() );
		}
		for ( int i = 0; i < options.getShots(); i++ ) {
			GunsmokeProjectile projectile = new ConfigBullet( GunsmokeUtil.getEntity( holder ), holder.getEyeLocation(), options.getBullet() );
			GunsmokeUtil.getPlugin().getItemManager().register( projectile );
			
			double speed = projectile.getVelocity().length();
			
			Vector newVec = VectorUtil.randomizeSpread( facing, options.getBulletSpread(), options.getBulletSpread() );
			newVec.normalize().multiply( speed );
			
			projectile.setVelocity( newVec );
		}
		
		GunsmokeUtil.flash( holder );
		
		lastShot = time;
		bullets--;
		
		double yaw = options.getRecoilYaw();
		double pitch = options.getRecoilPitch();
		if ( isScoped ) {
			yaw = options.getScopeRecoilYaw();
			pitch = options.getScopeRecoilPitch();
		}
		yaw = ( ThreadLocalRandom.current().nextDouble() * yaw ) - ( yaw * .5 );
		
		MovementModifier modifier = new MovementModifierRecoil( pitch, yaw );
		CrosshairMovement movement = GunsmokeUtil.getPlugin().getMovementManager().getMovement( holder.getName() );
		if ( movement != null ) {
			movement.addMovementModifier( modifier );
		}
		
		// TODO temporary hotfix
		Location shotLoc = holder.getEyeLocation();
		if ( ChatColor.stripColor( options.getName() ).equalsIgnoreCase( "remington 870" ) ) {
			shotLoc.getWorld().playSound( shotLoc, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1 );
			shotLoc.getWorld().playSound( shotLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 2 );
			shotLoc.getWorld().playSound( shotLoc, Sound.ENTITY_GHAST_SHOOT, 1, 1 );
		} else {
			shotLoc.getWorld().playSound( shotLoc, Sound.ENTITY_IRON_GOLEM_ATTACK, 1, 2 );
			shotLoc.getWorld().playSound( shotLoc, Sound.ENTITY_SKELETON_HURT, 1, 2 );
			shotLoc.getWorld().playSound( shotLoc, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 2 );
		}
	}
	
	public void unscope() {
		if ( isScoped || isScoping ) {
			GunsmokeUtil.getPlugin().getZoomManager().removeZoom( holder );
			isScoped = false;
			isScoping = false;
			updateItem();
		}
		lastScoped = System.currentTimeMillis();
	}
	
	public void scope() {
		if ( !isReloading ) {
			isScoping = true;
			lastScoped = System.currentTimeMillis();
			updateItem();
		}
	}
	
	public void reload() {
		if ( !isSwitching && !isReloading && bullets < options.getClipSize() ) {
			unscope();
			
			isReloading = true;
			lastReloaded = System.currentTimeMillis();
		}
	}
	
	@Override
	public EnumTickResult tick() {
		long time = System.currentTimeMillis();
		if ( isSwitching && time - lastSwitched >= options.getSwitchDelay() ) {
			isSwitching = false;
			updateItem();
		}
		
		if ( isReloading && time - lastReloaded >= options.getReloadDelay() ) {
			isReloading = false;
			bullets = Math.min( options.getClipSize(), bullets + options.getReloadAmount() );
		}
		
		if ( isScoping && time - lastScoped >= options.getScopeDelay() ) {
			isScoping = false;
			isScoped = true;
			GunsmokeUtil.getPlugin().getZoomManager().setZoom( holder, options.getZoom(), options.getScopeSpeed() );
		}
		
		if ( holder instanceof Player ) {
			Player player = ( Player ) holder;
			String message = "Ready";
			if ( isSwitching ) {
				message = "Switching " + ( options.getSwitchDelay() - ( time - lastSwitched ) );
			} else if ( isReloading ) {
				message = "Reloading " + ( options.getReloadDelay() - ( time - lastReloaded ) );
			} else if ( isScoping ) {
				message = "Scoping " + ( options.getScopeDelay() - ( time - lastScoped ) );
			} else if ( time - lastShot < options.getShootDelay() ) {
				message = "Can shoot in " + ( options.getShootDelay() - ( time - lastShot ) );
			}
			message += " | Ammo: " + bullets + "/" + options.getClipSize();
			
			player.spigot().sendMessage( ChatMessageType.ACTION_BAR, MDChat.getMessageFromString( ChatColor.AQUA + ChatColor.BOLD.toString() + message ) );
		}
		
		return EnumTickResult.CONTINUE;
	}
	
	@Override
	public EnumEventResult onClick( AdvancementOpenEvent event ) {
		event.getEntity().closeInventory();
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( DropItemEvent event ) {
		reload();
		
		event.setCancelled( true );
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( PlayerSwapHandItemsEvent event ) {
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( LeftClickEntityEvent event ) {
		event.setCancelled( true );
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( LeftClickEvent event ) {
		event.setCancelled( true );
		if ( !isReloading && !isSwitching ) {
			if ( options.getZoom() != null ) {
				if ( isScoped || isScoping ) {
					unscope();
				} else {
					scope();
				}
			}
		}
		
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( RightClickEvent event ) {
		if ( !options.isAutomatic() ) {
			shoot();
		}
		
		return EnumEventResult.COMPLETED;
	}
	
	@Override
	public EnumEventResult onClick( HoldRightClickEvent event ) {
		if ( options.isAutomatic() ) {
			shoot();
		}
		
		return EnumEventResult.COMPLETED;
	}
	
	@Override
	public void onEquip( LivingEntity entity, GunsmokePlayer gunsmokeEntity, EquipmentSlot slot ) {
		super.onEquip( entity, gunsmokeEntity, slot );
		
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeEntity.getMainHand() : gunsmokeEntity.getOffHand() ).setItem( display );
		gunsmokeEntity.setHandState( State.BOW, slot == EquipmentSlot.HAND );
		
		GunsmokeUtil.getPlugin().getMovementManager().setMovement( holder.getName(), new CrosshairMovement() );

		lastSwitched = System.currentTimeMillis();
		isSwitching = true;
		
		shots = 0;
		
		updateItem();
	}
	
	@Override
	public void onUnequip() {
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeHolder.getMainHand() : gunsmokeHolder.getOffHand() ).setItem( null );
		gunsmokeHolder.setHandState( State.DEFAULT, slot == EquipmentSlot.HAND );
		
		GunsmokeUtil.getPlugin().getMovementManager().setMovement( holder.getName(), null );
		
		isReloading = false;
		isSwitching = false;
		
		shots = 0;
		
		unscope();
		
		super.onUnequip();
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( Material.SHIELD );
		
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable( true );
		meta.setDisplayName( options.getName() );
		item.setItemMeta( meta );
		
		int model = ( isScoped || isScoping ) ? options.getModel() + 1 : options.getModel();
		if ( gunsmokeHolder != null && gunsmokeHolder.isProne() ) {
			model += 2;
		}
		
		item = BukkitUtil.setCustomModelData( item, model );
		
		int cooldown = ( isSwitching ? options.getSwitchDelay() : ( isScoping ? options.getScopeDelay() : 0 ) );
		
		item = BukkitUtil.setItemCooldown( item, cooldown );
		
		return markAsGunsmokeItem( item, getUUID() );
	}
}
