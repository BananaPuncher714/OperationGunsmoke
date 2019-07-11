package io.github.bananapuncher714.operation.gunsmoke.implementation.weapon;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.ZoomLevel;
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
	
	protected int bullets = 0;
	
	protected ConfigWeaponOptions options;
	
	public ConfigGun( ConfigWeaponOptions options ) {
		this.options = options;
		bullets = options.getClipSize();
		display = new ItemStackMultiState( new ItemStackGunsmoke( new ItemStack( Material.BOW ) ) );
	}
	
	protected void shoot() {
		long time = System.currentTimeMillis();
		// Do last shot check
		if ( time - lastShot < options.getShootDelay() ) {
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
			facing = VectorUtil.randomizeSpread( facing, options.getScopeSpread(), options.getScopeSpread() );
		} else {
			facing = VectorUtil.randomizeSpread( facing, options.getSpread(), options.getSpread() );
		}
		for ( int i = 0; i < options.getShots(); i++ ) {
			GunsmokeProjectile projectile = new ConfigBullet( GunsmokeUtil.getEntity( holder ), holder.getEyeLocation(), options.getBullet() );
			GunsmokeUtil.getPlugin().getItemManager().register( projectile );
			
			double speed = projectile.getVelocity().length();
			
			Vector newVec = VectorUtil.randomizeSpread( facing, options.getBulletSpread(), options.getBulletSpread() );
			newVec.normalize().multiply( speed );
			
			projectile.setVelocity( newVec );
		}
		
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
	}
	
	protected void unscope() {
		isScoping = false;
		if ( isScoped ) {
			GunsmokeUtil.getPlugin().getZoomManager().removeZoom( holder );
			isScoped = false;
			updateItem();
		}
		lastScoped = System.currentTimeMillis();
	}
	
	protected void scope() {
		if ( !isReloading ) {
			isScoping = true;
			lastScoped = System.currentTimeMillis();
			GunsmokeUtil.getPlugin().getZoomManager().setZoom( holder, options.getZoom() );
			updateItem();
		}
	}
	
	@Override
	public EnumTickResult tick() {
		long time = System.currentTimeMillis();
		if ( isSwitching && time - lastSwitched >= options.getSwitchDelay() ) {
			isSwitching = false;
		}
		
		if ( isReloading && time - lastReloaded >= options.getReloadDelay() ) {
			isReloading = false;
			bullets = Math.min( options.getClipSize(), bullets + options.getReloadAmount() );
		}
		
		if ( isScoping && time - lastScoped >= options.getScopeDelay() ) {
			isScoping = false;
			isScoped = true;
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
			
			player.spigot().sendMessage( ChatMessageType.ACTION_BAR, MDChat.getMessageFromString( ChatColor.BLUE + message ) );
		}
		
		return EnumTickResult.CONTINUE;
	}
	
	@Override
	public EnumEventResult onClick( AdvancementOpenEvent event ) {
		event.getPlayer().closeInventory();
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( DropItemEvent event ) {
		if ( !isSwitching && !isReloading && bullets < options.getClipSize() ) {
			unscope();
			
			isReloading = true;
			lastReloaded = System.currentTimeMillis();
		}
		
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
				if ( isScoped ) {
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
		
		updateItem();
	}
	
	@Override
	public void onUnequip() {
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeHolder.getMainHand() : gunsmokeHolder.getOffHand() ).setItem( null );
		gunsmokeHolder.setHandState( State.DEFAULT, slot == EquipmentSlot.HAND );
		
		GunsmokeUtil.getPlugin().getMovementManager().setMovement( holder.getName(), null );
		
		isReloading = false;
		isSwitching = false;
		
		GunsmokeUtil.getPlugin().getZoomManager().removeZoom( holder );
		isScoped = false;
		isScoping = false;
		
		super.onUnequip();
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( Material.SHIELD );
		
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable( true );
		meta.setDisplayName( options.getName() );
		
		( ( Damageable ) meta ).setDamage( ( isScoped || isScoping ) ? 1 : 0 );
		
		item.setItemMeta( meta );
		
		int cooldown = ( isSwitching ? options.getSwitchDelay() : ( isScoping ? options.getScopeDelay() : 0 ) );
		
		item = BukkitUtil.setItemCooldown( item, cooldown );
		
		return markAsGunsmokeItem( item, getUUID() );
	}
}
