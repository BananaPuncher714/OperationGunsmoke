package io.github.bananapuncher714.operation.gunsmoke.api.events.item;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;

public class GunsmokeItemEquipEvent extends GunsmokeItemEvent {
	private static final HandlerList handlers = new HandlerList();
	
	protected LivingEntity entity;
	protected EquipmentSlot slot;
	
	public GunsmokeItemEquipEvent( LivingEntity entity, GunsmokeItem item, EquipmentSlot slot ) {
		super( item );
		this.entity = entity;
		this.slot = slot;
	}
	
	public LivingEntity getEntity() {
		return entity;
	}
	
	public EquipmentSlot getSlot() {
		return slot;
	}

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
