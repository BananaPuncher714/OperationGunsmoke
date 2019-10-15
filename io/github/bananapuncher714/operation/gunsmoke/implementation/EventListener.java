package io.github.bananapuncher714.operation.gunsmoke.implementation;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperLivingEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockBreakEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.block.GunsmokeBlockCreateEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmor;
import io.github.bananapuncher714.operation.gunsmoke.implementation.block.RegeneratingGunsmokeBlock;

public class EventListener implements Listener {
	@EventHandler
	private void onEvent( GunsmokeBlockCreateEvent event ) {
		GunsmokeBlock block = event.getRepresentable();
		RegeneratingGunsmokeBlock regenBlock = new RegeneratingGunsmokeBlock( block.getLocation(), block.getHealth() );
		event.setBlock( regenBlock );
	}
	
	@EventHandler
	private void onEvent( GunsmokeBlockBreakEvent event ) {
		if ( event.getRepresentable() instanceof RegeneratingGunsmokeBlock ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler
	private void onEvent( GunsmokeEntityDamageEvent event ) {
		// TODO Get a gunsmoke bullet event
		GunsmokeEntity entity = event.getRepresentable();
		if ( entity instanceof GunsmokeEntityWrapperLivingEntity ) {
			GunsmokeEntityWrapperLivingEntity wrapper = ( GunsmokeEntityWrapperLivingEntity ) entity;
			LivingEntity lEntity = wrapper.getEntity();
			
			int armor = getArmor( lEntity, EquipmentSlot.HEAD, event.getType() );
			armor += getArmor( lEntity, EquipmentSlot.CHEST, event.getType() );
			armor += getArmor( lEntity, EquipmentSlot.LEGS, event.getType() );
			armor += getArmor( lEntity, EquipmentSlot.FEET, event.getType() );
			
			double damageMultiplier = 100.0 / ( 100 + armor );
			
			event.setDamage( event.getDamage() * damageMultiplier );
		}
	}
	
	private int getArmor( LivingEntity entity, EquipmentSlot slot, DamageType type ) {
		ItemStack item = BukkitUtil.getEquipment( entity, slot );
		GunsmokeRepresentable representable = GunsmokeUtil.getPlugin().getItemManager().getRepresentable( item );
		if ( representable instanceof ConfigArmor ) {
			return ( ( ConfigArmor ) representable ).getArmor( type );
		}
		return 0;
	}
}
