package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.inventory.EquipmentSlot;

import net.minecraft.server.v1_14_R1.EnumItemSlot;

public class NMSUtils {
	protected final static org.bukkit.entity.Entity getEntityFromId( World world, int id ) {
		return ( ( CraftWorld ) world ).getHandle().getEntity( id ).getBukkitEntity();
	}
	
	protected final static EquipmentSlot getEquipmentSlot( EnumItemSlot slot ) {
		switch ( slot ) {
		case CHEST:
			return EquipmentSlot.CHEST;
		case FEET:
			return EquipmentSlot.FEET;
		case HEAD:
			return EquipmentSlot.HEAD;
		case LEGS:
			return EquipmentSlot.LEGS;
		case MAINHAND:
			return EquipmentSlot.HAND;
		case OFFHAND:
			return EquipmentSlot.OFF_HAND;
		default:
			return null;
		}
	}
	
	protected final static EnumItemSlot getEnumItemSlot( EquipmentSlot slot ) {
		switch ( slot ) {
		case CHEST:
			return EnumItemSlot.CHEST;
		case FEET:
			return EnumItemSlot.FEET;
		case HEAD:
			return EnumItemSlot.HEAD;
		case LEGS:
			return EnumItemSlot.LEGS;
		case HAND:
			return EnumItemSlot.MAINHAND;
		case OFF_HAND:
			return EnumItemSlot.OFFHAND;
		default:
			return null;
		}
	}
}
