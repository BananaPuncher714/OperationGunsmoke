package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.bananapuncher714.operation.gunsmoke.core.util.SessionUtil;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumCreatureType;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.IRegistry;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PlayerConnection;

public class NMSUtils {
	
	protected final static CraftEntity getEntityFromId( World world, int id ) {
		net.minecraft.server.v1_14_R1.World nmsWorld = ( ( CraftWorld ) world ).getHandle();
		net.minecraft.server.v1_14_R1.Entity nmsEntity = nmsWorld.getEntity( id );
		return nmsEntity == null ? null : nmsEntity.getBukkitEntity();
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
	
	protected static void register( String id, EntityTypes< ? > parent, EntityTypes.a< ? > type ) {
		// TODO Do some safe checking to see if id already exists?
		IRegistry.ENTITY_TYPE.a( IRegistry.ENTITY_TYPE.a( parent ), new MinecraftKey( id ), type.a( id ) );
	}
	
	protected static EntityTypes.a< ? extends Entity > create( EntityTypes.b< ? extends Entity > constructor, EnumCreatureType type, double width, double height ) {
		EntityTypes.a< ? extends Entity > a = EntityTypes.a.a( constructor, type ).b().a( ( float ) width, ( float ) height );
		return a;
	}
	
	protected static GameProfile convert( GameProfile profile, String name ) {
		String[] properties = SessionUtil.getTextureFrom( name, false );
		if ( properties != null ) {
			profile.getProperties().put( "textures", new Property( "textures", properties[ 0 ], properties[ 1 ] ) );
		}
		return profile;
	}
	
	public static void setNoFly( Player player ) {
		// TODO Remove some time
		try {
			Field c = PlayerConnection.class.getDeclaredField( "C" );
			c.setAccessible( true );
			c.set( ( ( CraftPlayer ) player ).getHandle().playerConnection, 0 );
		} catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}
}
