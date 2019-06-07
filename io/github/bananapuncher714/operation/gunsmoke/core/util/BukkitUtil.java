package io.github.bananapuncher714.operation.gunsmoke.core.util;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
}
