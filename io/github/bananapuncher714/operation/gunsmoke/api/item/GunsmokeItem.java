package io.github.bananapuncher714.operation.gunsmoke.api.item;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.core.util.NBTEditor;

public abstract class GunsmokeItem extends GunsmokeRepresentable {
	private final static Object[] CUSTOM = { "io", "github", "bananapuncher714", "operation", "gunsmoke", "item", "custom" };
	
	protected LivingEntity holder;
	protected EquipmentSlot slot;
	protected boolean isEquipped = false;
	
	public void onEquip( LivingEntity entity, EquipmentSlot slot ) {
		this.holder = entity;
		this.slot = slot;
		isEquipped = true;
	}
	
	public void onUnequip() {
		holder = null;
		slot = null;
		isEquipped = false;
	}
	
	public EquipmentSlot getEquippedSlot() {
		return slot;
	}
	
	public LivingEntity getHolder() {
		return holder;
	}
	
	public boolean isEquipped() {
		return isEquipped;
	}
	
	public abstract ItemStack getItem();
	
	protected static ItemStack markAsGunsmokeItem( ItemStack item ) {
		return NBTEditor.set( item, ( byte ) 1, CUSTOM );
	}
}
