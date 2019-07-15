package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.SessionUtil;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.PlayerInteractManager;
import net.minecraft.server.v1_14_R1.WorldServer;

public class EntityInterceptor {
	private static Field SPAWNENTITYLIVING_ID;
	private static Field NAMEDENTITYSPAWN_ID;
	private static Field ENTITYDESTROY_IDS;
	
	static {
		try {
			SPAWNENTITYLIVING_ID = PacketPlayOutSpawnEntityLiving.class.getDeclaredField( "a" );
			SPAWNENTITYLIVING_ID.setAccessible( true );

			NAMEDENTITYSPAWN_ID = PacketPlayOutNamedEntitySpawn.class.getDeclaredField( "a" );
			NAMEDENTITYSPAWN_ID.setAccessible( true );
			
			ENTITYDESTROY_IDS = PacketPlayOutEntityDestroy.class.getDeclaredField( "a" );
			ENTITYDESTROY_IDS.setAccessible( true );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	private Map< Integer, EntityPlayer > idPlayers = new HashMap< Integer, EntityPlayer >();
	
	protected Packet< ? > onCapturePacket( Player player, PacketPlayOutSpawnEntityLiving packet ) {
		int id = getIdOf( packet );
		CraftEntity entity = NMSUtils.getEntityFromId( player.getWorld(), id );
		
		if ( entity != null && ( entity.getHandle() instanceof TestEntity ) ) {
			System.out.println( "Done!" );
			EntityPlayer entPlayer ;
			if ( idPlayers.containsKey( id ) ) {
				entPlayer = idPlayers.get( id );
			} else {
				entPlayer = constructPlayerFor( player.getWorld(), entity, player.getName() );
			}
		
			PlayerConnection connection = ( ( CraftPlayer ) player ).getHandle().playerConnection;
			connection.sendPacket( new PacketPlayOutPlayerInfo( EnumPlayerInfoAction.ADD_PLAYER, entPlayer ) );
			PacketPlayOutNamedEntitySpawn spawnPlayer = new PacketPlayOutNamedEntitySpawn( entPlayer );
			setIdOf( spawnPlayer, id );
			connection.sendPacket( spawnPlayer );
			Bukkit.getScheduler().scheduleSyncDelayedTask( GunsmokeUtil.getPlugin(), new Runnable() {
				@Override
				public void run() {
					connection.sendPacket( new PacketPlayOutPlayerInfo( EnumPlayerInfoAction.REMOVE_PLAYER, entPlayer ) );
				}
			}, 5 );
			return spawnPlayer;
		}
		return packet;
	}
	
	protected Packet< ? > onCapturePacket( Player player, PacketPlayOutEntityDestroy packet ) {
		int[] ids = getIdsOf( packet );
		for ( int id : ids ) {
			idPlayers.remove( id );
		}
		return packet;
	}
	
	private static int getIdOf( PacketPlayOutSpawnEntityLiving packet ) {
		try {
			return SPAWNENTITYLIVING_ID.getInt( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private static void setIdOf( PacketPlayOutNamedEntitySpawn packet, int id ) {
		try {
			NAMEDENTITYSPAWN_ID.set( packet, id );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}
	
	private static int[] getIdsOf( PacketPlayOutEntityDestroy packet ) {
		try {
			return ( int[] ) ENTITYDESTROY_IDS.get( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		return new int[] {};
	}
	
	private static EntityPlayer constructPlayerFor( org.bukkit.World world, org.bukkit.entity.Entity realEntity, String name ) {
		MinecraftServer nmsServer = ( ( CraftServer ) Bukkit.getServer() ).getServer();
		WorldServer theWorld = ( ( CraftWorld ) world ).getHandle();

		GameProfile profile = new GameProfile( UUID.randomUUID(), name );
		String[] properties = SessionUtil.getTextureFrom( name, false );
		if ( properties != null ) {
			profile.getProperties().put( "textures", new Property( "textures", properties[ 0 ], properties[ 1 ] ) );
		}

		EntityPlayer entity = new EntityPlayer( nmsServer, theWorld, profile, new PlayerInteractManager( theWorld ) );

		Location location = realEntity.getLocation();
		entity.setLocation( location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch() );

		return entity;
	}
}
