package io.github.bananapuncher714.operation.gunsmoke.implementation.weapon;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
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
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBullet;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBulletOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.GunsmokeBullet;

public class ConfigGun extends GunsmokeItemInteractable {
	protected ItemStackMultiState display;
	protected boolean zoomed = false;
	
	public ConfigGun() {
		display = new ItemStackMultiState( new ItemStackGunsmoke( new ItemStack( Material.BOW ) ) );
	}
	
	@Override
	public EnumEventResult onClick( AdvancementOpenEvent event ) {
		event.getPlayer().closeInventory();
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( DropItemEvent event ) {
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
		if ( zoomed ) {
			GunsmokeUtil.getPlugin().getZoomManager().setZoom( holder, ZoomLevel._11 );
		} else {
			GunsmokeUtil.getPlugin().getZoomManager().removeZoom( holder );
		}
		zoomed = !zoomed;
		
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( RightClickEvent event ) {
		ConfigBulletOptions options = GunsmokeImplementation.getInstance().getBullet( "example_bullet" );
		GunsmokeProjectile projectile = new ConfigBullet( GunsmokeUtil.getEntity( holder ), holder.getEyeLocation(), options);
		GunsmokeUtil.getPlugin().getItemManager().register( projectile );
		
		GunsmokeUtil.flash( event.getPlayer() );
		
		double finYaw = ThreadLocalRandom.current().nextDouble() * 1 - ( 1 * .5 );
		double pitch = -5;
		
		MovementModifier modifier = new MovementModifierRecoil( pitch, finYaw );
		
		CrosshairMovement movement = GunsmokeUtil.getPlugin().getMovementManager().getMovement( holder.getName() );
		if ( movement != null ) {
			movement.addMovementModifier( modifier );
		}
		
		return EnumEventResult.COMPLETED;
	}
	
	@Override
	public void onEquip( LivingEntity entity, GunsmokePlayer gunsmokeEntity, EquipmentSlot slot ) {
		super.onEquip( entity, gunsmokeEntity, slot );
		
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeEntity.getMainHand() : gunsmokeEntity.getOffHand() ).setItem( display );
		gunsmokeEntity.setHandState( State.BOW, slot == EquipmentSlot.HAND );
	}
	
	@Override
	public void onUnequip() {
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeHolder.getMainHand() : gunsmokeHolder.getOffHand() ).setItem( null );
		gunsmokeHolder.setHandState( State.DEFAULT, slot == EquipmentSlot.HAND );
		
		GunsmokeUtil.getPlugin().getMovementManager().setMovement( holder.getName(), null );
		
		super.onUnequip();
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( Material.SHIELD );
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( "Config gun" );
		item.setItemMeta( meta );
		
		item = BukkitUtil.setItemCooldown( item, 5000 );
		
		return markAsGunsmokeItem( item, getUUID() );
	}
}
