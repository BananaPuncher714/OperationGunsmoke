package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.WorldServer;

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
	
	public static boolean getBool( Player player ) {
		World world = player.getWorld();
		WorldServer server = ( ( CraftWorld ) world ).getHandle();
		
		return server.a( ( ( CraftPlayer ) player ).getHandle().getBoundingBox().g( 0.0625D).b( 0.0D, -0.55D, 0.0D ) );
	}

	public static void setNoFly( Player player ) {
		try {
			Field c = PlayerConnection.class.getDeclaredField( "C" );
			c.setAccessible( true );
			c.set( ( ( CraftPlayer ) player ).getHandle().playerConnection, 0 );
		} catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}
}
