package io.github.bananapuncher714.operation.gunsmoke.test;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.InteractableDamage;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeItemMelee;

public class TestBroadsword extends GunsmokeItemMelee implements InteractableDamage {
	protected boolean blocking = false;
	
	public TestBroadsword() {
		super( 19 );
	}

	@Override
	public EnumEventResult onClick( LeftClickEvent event ) {
		event.setCancelled( true );
		return EnumEventResult.PROCESSED;
	}
	
	@Override
	public EnumEventResult onClick( LeftClickEntityEvent event ) {
		super.onClick( event );
		
		Entity entity = event.getHitEntity();

		boolean completed = GunsmokeUtil.damage( GunsmokeUtil.getEntity( entity ), DamageType.PHYSICAL, damage, GunsmokeUtil.getEntity( holder) );
		
		return completed ? EnumEventResult.COMPLETED : EnumEventResult.SKIPPED;
	}
	
	@Override
	public EnumEventResult onTakeDamage( GunsmokeEntityDamageEvent event ) {
		if ( blocking ) {
			event.setDamage( event.getDamage() / 3.0 );
		}
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( RightClickEvent event ) {
		blocking = true;
		return EnumEventResult.COMPLETED;
	}

	@Override
	public EnumEventResult onClick( ReleaseRightClickEvent event ) {
		blocking = false;
		return EnumEventResult.COMPLETED;
	}
	
	@Override
	public void onEquip( LivingEntity entity, GunsmokePlayer gunsmokeEntity, EquipmentSlot slot ) {
		super.onEquip( entity, gunsmokeEntity, slot );
		blocking = false;
	}
	
	@Override
	public void onUnequip() {
		blocking = false;
		super.onUnequip();
	}

	@Override
	public boolean canDualWieldWith( GunsmokeItem other ) {
		return false;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( Material.SHIELD );
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( "Broadsword" );
		item.setItemMeta( meta );
		
		item = BukkitUtil.setItemCooldown( item, 0 );
		
		return markAsGunsmokeItem( item, getUUID() );
	}
}
