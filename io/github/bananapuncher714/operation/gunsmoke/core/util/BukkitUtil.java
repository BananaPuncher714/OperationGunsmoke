package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitUtil {
	public final static ItemStack getEquipment( LivingEntity entity, EquipmentSlot slot ) {
		switch ( slot ) {
		case CHEST:
			return entity.getEquipment().getChestplate();
		case FEET:
			return entity.getEquipment().getBoots();
		case HEAD:
			return entity.getEquipment().getHelmet();
		case LEGS:
			return entity.getEquipment().getLeggings();
		case HAND:
			return entity.getEquipment().getItemInMainHand();
		case OFF_HAND:
			return entity.getEquipment().getItemInOffHand();
		default:
			return null;
		}
	}
	
	public final static boolean isRightClickable( Material material ) {
		switch ( material ) {
		case BOW:
		case TRIDENT:
		case SHIELD:
			return true;
		default:
			return false;
		}
	}
	
	public final static ItemStack setItemCooldown( ItemStack item, double ms ) {
		double attackSpeed = ( 1 / ( ms / 1000.0 ) ) - 4;
		ItemMeta meta = item.getItemMeta();
		meta.addAttributeModifier( Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier( UUID.randomUUID(), "Cooldown", attackSpeed, Operation.ADD_NUMBER, EquipmentSlot.HAND ) );
		item.setItemMeta( meta );
//		item = NBTEditor.set( item, "generic.attackSpeed", "AttributeModifiers", null, "AttributeName" );
//		item = NBTEditor.set( item, "Blah", "AttributeModifiers", 0, "Name" );
//		item = NBTEditor.set( item, attackSpeed, "AttributeModifiers", 0, "Amount" );
//		item = NBTEditor.set( item, 0, "AttributeModifiers", 0, "Operation" );
//		item = NBTEditor.set( item, "mainhand", "AttributeModifiers", 0, "Slot" );
//		item = NBTEditor.set( item, 1l, "AttributeModifiers", 0, "UUIDMost" );
//		item = NBTEditor.set( item, 1l, "AttributeModifiers", 0, "UUIDLeast" );
		return item;
	}
}
