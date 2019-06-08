package io.github.bananapuncher714.operation.gunsmoke.test;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.EnumInteractResult;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class TestGunsmokeItemInteractable extends GunsmokeItemInteractable {
	Gunsmoke plugin;
	
	public TestGunsmokeItemInteractable( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	@Override
	public EnumInteractResult onClick( AdvancementOpenEvent event ) {
		event.getPlayer().closeInventory();
		event.getPlayer().setFireTicks( 20 );
		return EnumInteractResult.COMPLETED;
	}

	@Override
	public EnumInteractResult onClick( DropItemEvent event ) {
		event.getPlayer().getLocation().getBlock().setType( Material.FIRE );
		event.setCancelled( true );
		return EnumInteractResult.COMPLETED;
	}

	@Override
	public EnumInteractResult onClick( PlayerSwapHandItemsEvent event ) {
		event.getPlayer().launchProjectile( Fireball.class );
		return EnumInteractResult.COMPLETED;
	}

	@Override
	public EnumInteractResult onClick( LeftClickEntityEvent event ) {
		event.setCancelled( true );
		if ( event.getHitEntity() instanceof LivingEntity ) {
			LivingEntity lEntity = ( LivingEntity ) event.getHitEntity();
			plugin.getProtocol().getHandler().hurt( lEntity );
			lEntity.setHealth( lEntity.getHealth() - 1 );
		}
		return EnumInteractResult.COMPLETED;
	}

	@Override
	public EnumInteractResult onClick( LeftClickEvent event ) {
		event.getPlayer().sendMessage( "Boop" );
		event.setCancelled( true );
		return EnumInteractResult.COMPLETED;
	}

	@Override
	public EnumInteractResult onClick( HoldRightClickEvent event ) {
		event.getPlayer().launchProjectile( Arrow.class );
		return EnumInteractResult.COMPLETED;
	}

	@Override
	public void onEquip( LivingEntity entity, GunsmokeEntity gunsmokeEntity, EquipmentSlot slot ) {
		super.onEquip( entity, gunsmokeEntity, slot );
	}
	
	@Override
	public void onUnequip() {
		super.onUnequip();
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( Material.SHIELD );
		
		item = BukkitUtil.setItemCooldown( item, 5000 );
		
		return markAsGunsmokeItem( item, getUUID() );
	}

}
