package io.github.bananapuncher714.operation.gunsmoke.api.player.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

// Not sure why this doesn't extend PlayerEvent, but just in case if I find some use for it in the future
public class EntityUpdateItemEvent extends EntityEvent {
	private static final HandlerList handlers = new HandlerList();
	protected LivingEntity entity;
	protected EquipmentSlot slot;
	protected ItemStack itemSnapshot;
	
	public EntityUpdateItemEvent( LivingEntity updater, ItemStack item, EquipmentSlot slot ) {
		super( updater );
		entity = updater;
		this.slot = slot;
		itemSnapshot = item.clone();
	}
	
	@Override
	public LivingEntity getEntity() {
		return entity;
	}
	
	public EquipmentSlot getSlot() {
		return slot;
	}
	
	public ItemStack getItem() {
		return itemSnapshot;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
