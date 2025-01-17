package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BukkitUtil {
	public final static Location getBlockLocation( Location loc ) {
		return new Location( loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() );
	}
	
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
	
	public final static void setEquipment( LivingEntity entity, ItemStack item, EquipmentSlot slot ) {
		switch ( slot ) {
		case CHEST:
			entity.getEquipment().setChestplate( item );
			return;
		case FEET:
			entity.getEquipment().setBoots( item );
			return;
		case HEAD:
			entity.getEquipment().setHelmet( item );
			return;
		case LEGS:
			entity.getEquipment().setLeggings( item );
			return;
		case HAND:
			entity.getEquipment().setItemInMainHand( item );
			return;
		case OFF_HAND:
			entity.getEquipment().setItemInOffHand( item );
			return;
		}
	}
	
	public final static boolean isRightClickable( Material material ) {
		switch ( material ) {
		case BOW:
		case TRIDENT:
		case SHIELD:
		case CROSSBOW:
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
		return item;
	}
	
	public static void flash( LivingEntity player ) {
		player.addPotionEffect( new PotionEffect( PotionEffectType.NIGHT_VISION, 2, 0, true, false ) );
		player.addPotionEffect( new PotionEffect( PotionEffectType.BLINDNESS, 4, 0, true, false ) );
	}
	
	public static Vector toVector( BlockFace face ) {
		return new Vector( face.getModX(), face.getModY(), face.getModZ() );
	}
	
	public static ItemStack setCustomModelData( ItemStack item, int val ) {
		return NBTEditor.set( item, val, "CustomModelData" );
	}
}
