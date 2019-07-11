package io.github.bananapuncher714.operation.gunsmoke.implementation.armor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeItemEquippable;

public class ConfigArmor extends GunsmokeItemEquippable {
	protected ItemStackGunsmoke display;
	protected ConfigArmorOptions options;
	
	public ConfigArmor( ConfigArmorOptions options ) {
		display = new ItemStackGunsmoke( new ItemStack( options.getVisibleItem() ) );
		
		this.options = options;
	}

	public int getArmor( DamageType type ) {
		return options.get( type );
	}
	
	@Override
	public EnumEventResult onTakeDamage( GunsmokeEntityDamageEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	@Override
	public EnumEventResult onClick( RightClickEvent event ) {
		event.setCancelled( true );
		
		ItemStack item = BukkitUtil.getEquipment( holder, options.getSlot() );
		
		if ( item == null ) {
			BukkitUtil.setEquipment( holder, null, slot);
			BukkitUtil.setEquipment( holder, getItem(), options.getSlot() );
		}
		
		return EnumEventResult.COMPLETED;
	}

	@Override
	public void onEquip( LivingEntity entity, GunsmokePlayer gunsmokeEntity, EquipmentSlot slot ) {
		super.onEquip( entity, gunsmokeEntity, slot );
		gunsmokeEntity.setWearing( slot, display );
		gunsmokeEntity.update( slot );
	}
	
	@Override
	public void onUnequip() {
		gunsmokeHolder.setWearing( slot, null );
		super.onUnequip();
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( options.getClientItem() );
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( options.getName() );
		item.setItemMeta( meta );
		
		return GunsmokeItem.markAsGunsmokeItem( item, getUUID() );
	}
}
