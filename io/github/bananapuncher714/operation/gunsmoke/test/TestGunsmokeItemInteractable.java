package io.github.bananapuncher714.operation.gunsmoke.test;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class TestGunsmokeItemInteractable extends GunsmokeItemInteractable {
	Gunsmoke plugin;
	
	ItemStackMultiState display;
	
	public TestGunsmokeItemInteractable( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		display = new ItemStackMultiState( new ItemStackGunsmoke( new ItemStack( Material.TRIDENT ) ) );
	}
	
	@Override
	public EnumEventResult onClick( AdvancementOpenEvent event ) {
		event.getPlayer().closeInventory();
		event.getPlayer().setFireTicks( 20 );
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( DropItemEvent event ) {
		event.getPlayer().getLocation().getBlock().setType( Material.FIRE );
		event.setCancelled( true );
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( PlayerSwapHandItemsEvent event ) {
		event.getPlayer().launchProjectile( Fireball.class );
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( LeftClickEntityEvent event ) {
		event.setCancelled( true );
		if ( event.getHitEntity() instanceof LivingEntity ) {
			LivingEntity lEntity = ( LivingEntity ) event.getHitEntity();
			plugin.getProtocol().getHandler().hurt( lEntity );
			lEntity.setHealth( lEntity.getHealth() - 1 );
		}
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( LeftClickEvent event ) {
		TestGunsmokeProjectile projectile = new TestGunsmokeProjectile( event.getPlayer(), event.getPlayer().getEyeLocation(), 100 );
		projectile.setVelocity( event.getPlayer().getLocation().getDirection().multiply( 5 ) );

		plugin.getItemManager().register( projectile );
		
		event.setCancelled( true );
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( HoldRightClickEvent event ) {
		event.getPlayer().launchProjectile( Arrow.class );
		
		GunsmokeUtil.flash( event.getPlayer() );
		
		double finYaw = ThreadLocalRandom.current().nextDouble() * 1 - ( 1 * .5 );
		double pitch = -2;
		
		plugin.getProtocol().getHandler().teleportRelative( event.getPlayer().getName(), null, finYaw, pitch );
		
		return EnumEventResult.COMPLETED;
	}

	@Override
	public void onEquip( LivingEntity entity, GunsmokePlayer gunsmokeEntity, EquipmentSlot slot ) {
		super.onEquip( entity, gunsmokeEntity, slot );

		entity.sendMessage( "Equipped in slot " + slot );
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeEntity.getMainHand() : gunsmokeEntity.getOffHand() ).setItem( display );
		gunsmokeEntity.setHandState( State.SHIELD, slot == EquipmentSlot.HAND );
	}
	
	@Override
	public void onUnequip() {
		holder.sendMessage( "Unequipped" );
		( ( slot == EquipmentSlot.HAND ) ? gunsmokeHolder.getMainHand() : gunsmokeHolder.getOffHand() ).setItem( null );
		gunsmokeHolder.setHandState( State.DEFAULT, slot == EquipmentSlot.HAND );
		
		super.onUnequip();
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( Material.SHIELD );
		
		item = BukkitUtil.setItemCooldown( item, 5000 );
		
		return markAsGunsmokeItem( item, getUUID() );
	}

}
