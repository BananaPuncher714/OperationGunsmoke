package io.github.bananapuncher714.operation.gunsmoke.api.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class GunsmokePlayer {
	protected UUID uuid;
	
	protected GunsmokePlayerHand offHand;
	protected GunsmokePlayerHand mainHand;
	protected Map< EquipmentSlot, ItemStackGunsmoke > equipment;
	protected boolean isProne = false;
	protected boolean isRightClicking = false;
	
	public GunsmokePlayer( UUID uuid ) {
		this.uuid = uuid;
		mainHand = new GunsmokePlayerHand();
		offHand = new GunsmokePlayerHand();
		equipment = new ConcurrentHashMap< EquipmentSlot, ItemStackGunsmoke >();
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public Map< EquipmentSlot, ItemStackGunsmoke > getEquipment() {
		return equipment;
	}
	
	public boolean isProne() {
		return isProne;
	}

	public void setProne( boolean isProne ) {
		this.isProne = isProne;
	}
	
	public boolean isRightClicking() {
		return isRightClicking;
	}

	public void setRightClicking( boolean isRightClicking ) {
		this.isRightClicking = isRightClicking;
	}

	public void setWearing( EquipmentSlot slot, ItemStackGunsmoke item ) {
		if ( item != null ) {
			equipment.put( slot, item );
		} else {
			equipment.remove( slot );
		}
	}
	
	public ItemStackGunsmoke getWearing( EquipmentSlot slot ) {
		return equipment.get( slot );
	}

	public GunsmokePlayerHand getOffHand() {
		return offHand;
	}
	
	public GunsmokePlayerHand getMainHand() {
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

		updateHands();
	}
	
	protected void updateHands() {
		update( EquipmentSlot.HAND );
		update( EquipmentSlot.OFF_HAND );
		
		Bukkit.getScheduler().runTaskLater( Gunsmoke.getPlugin( Gunsmoke.class ), () -> {
				update( true );
				update( false );
		}, 2 );
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
			Gunsmoke.getPlugin( Gunsmoke.class ).getProtocol().getHandler().update( ( LivingEntity ) entity, main, true );
		}
	}
	
	public void setHandState( State state, boolean isMain ) {
		GunsmokePlayerHand hand = isMain ? mainHand : offHand;
		hand.setState( state );
		updateHands();
	}
	
	public void swingArm( boolean main ) {
		
	}
}
