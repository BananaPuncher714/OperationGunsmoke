package io.github.bananapuncher714.operation.gunsmoke.api.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class GunsmokeEntity {
	protected UUID uuid;
	
	protected GunsmokeEntityHand offHand;
	protected GunsmokeEntityHand mainHand;
	protected Map< EquipmentSlot, ItemStackGunsmoke > equipment;
	
	public GunsmokeEntity( UUID uuid ) {
		this.uuid = uuid;
		mainHand = new GunsmokeEntityHand();
		offHand = new GunsmokeEntityHand();
		equipment = new ConcurrentHashMap< EquipmentSlot, ItemStackGunsmoke >();
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public Map< EquipmentSlot, ItemStackGunsmoke > getEquipment() {
		return equipment;
	}
	
	public ItemStack getWearing( EquipmentSlot slot ) {
		ItemStackGunsmoke wearing = equipment.get( slot );
		if ( wearing == null ) {
			return null;
		}
		return wearing.getItem();
	}

	public GunsmokeEntityHand getOffHand() {
		return offHand;
	}
	
	public GunsmokeEntityHand getMainHand() {
		return mainHand;
	}
	
	/**
	 * Very important method!! Should call underlying NMS methods to fully
	 * update a player's visual appearance towards other players!
	 * 
	 * Essentially, these:
	 * - Armor
	 * - Both hands
	 * - Metadata
	 * - Potion effects
	 */
	public void update() {
		update( EquipmentSlot.HEAD );
		update( EquipmentSlot.CHEST );
		update( EquipmentSlot.LEGS );
		update( EquipmentSlot.FEET );
		update( EquipmentSlot.HAND );
		update( EquipmentSlot.OFF_HAND );
		
		Bukkit.getScheduler().runTask( Gunsmoke.getPlugin( Gunsmoke.class ), new Runnable() {
			@Override
			public void run() {
				update( true );
				update( false );
			}
		} );
	}
	
	public void update( EquipmentSlot slot ) {
		Entity entity = Bukkit.getEntity( uuid );
		if ( entity == null || !entity.isValid() ) {
			return;
		}
		if ( entity instanceof LivingEntity ) {
			Gunsmoke.getPlugin( Gunsmoke.class ).getProtocol().getHandler().update( ( LivingEntity ) entity, slot );
		}
	}
	
	public void update( boolean main ) {
		Entity entity = Bukkit.getEntity( uuid );
		if ( entity == null || !entity.isValid() ) {
			return;
		}
		
		if ( entity instanceof LivingEntity ) {
			Gunsmoke.getPlugin( Gunsmoke.class ).getProtocol().getHandler().update( ( LivingEntity ) entity, main );
		}
	}
}
