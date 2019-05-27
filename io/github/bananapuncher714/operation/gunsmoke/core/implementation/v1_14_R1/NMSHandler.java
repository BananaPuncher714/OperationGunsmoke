package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntityHand;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayInArmAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockPlace;
import net.minecraft.server.v1_14_R1.PacketPlayInTeleportAccept;
import net.minecraft.server.v1_14_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_14_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_14_R1.SoundEffect;
import net.minecraft.server.v1_14_R1.DataWatcher.Item;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition.EnumPlayerTeleportFlags;

public class NMSHandler implements PacketHandler {
	private final static int HAND_STATE_INDEX = 7;
	
	private static Field ENTITYMETADATA_ITEMLIST;
	private static Field ENTITYMETADATA_ID;

	private static Field ENTITYEQUIPMENT_ID;
	private static Field ENTITYEQUIPMENT_SLOT;
	private static Field ENTITYEQUIPMENT_ITEMSTACK;

	private static Field BLOCKCHANGE_POSITION;

	private static Field MAPCHUNK_X;
	private static Field MAPCHUNK_Z;

	private static Field SPAWNENTITY_TYPE;

	private static Field PLAYERABILITIES_FOV;

	static {
		try {
			ENTITYMETADATA_ITEMLIST = PacketPlayOutEntityMetadata.class.getDeclaredField( "b" );
			ENTITYMETADATA_ITEMLIST.setAccessible( true );

			ENTITYMETADATA_ID = PacketPlayOutEntityMetadata.class.getDeclaredField( "a" );
			ENTITYMETADATA_ID.setAccessible( true );


			ENTITYEQUIPMENT_ID = PacketPlayOutEntityEquipment.class.getDeclaredField( "a" );
			ENTITYEQUIPMENT_ID.setAccessible( true );

			ENTITYEQUIPMENT_SLOT = PacketPlayOutEntityEquipment.class.getDeclaredField( "b" );
			ENTITYEQUIPMENT_SLOT.setAccessible( true );

			ENTITYEQUIPMENT_ITEMSTACK = PacketPlayOutEntityEquipment.class.getDeclaredField( "c" );
			ENTITYEQUIPMENT_ITEMSTACK.setAccessible( true );

			BLOCKCHANGE_POSITION = PacketPlayOutBlockChange.class.getDeclaredField( "a" );
			BLOCKCHANGE_POSITION.setAccessible( true );

			MAPCHUNK_X = PacketPlayOutMapChunk.class.getDeclaredField( "a" );
			MAPCHUNK_X.setAccessible( true );

			MAPCHUNK_Z = PacketPlayOutMapChunk.class.getDeclaredField( "b" );
			MAPCHUNK_Z.setAccessible( true );

			SPAWNENTITY_TYPE = PacketPlayOutSpawnEntity.class.getDeclaredField( "k" );
			SPAWNENTITY_TYPE.setAccessible( true );

			PLAYERABILITIES_FOV = PacketPlayOutAbilities.class.getDeclaredField( "f" );
			PLAYERABILITIES_FOV.setAccessible( true );
		} catch ( NoSuchFieldException | SecurityException e ) {
			e.printStackTrace();
		}
	}
	
	private Gunsmoke plugin;
	
	public void setGunsmoke( Gunsmoke plugin ) {
		this.plugin = plugin;
	}

	private Set< Integer > equipmentPackets = new HashSet< Integer >();

	/**
	 * Intercept outgoing packets; Edit them if they are the EntityMetadata packet or the EntityEquipment packet
	 */
	@Override
	public boolean onPacketInterceptOut( Player reciever, Object packet ) {
		if ( packet instanceof PacketPlayOutEntityMetadata ) {
			handleMetadataPacket( reciever, ( PacketPlayOutEntityMetadata ) packet );
		} else if ( packet instanceof PacketPlayOutEntityEquipment ) {
			handleEntityEquipmentPacket( reciever, ( PacketPlayOutEntityEquipment ) packet );
		}
		return true;
	}

	/**
	 * Catch all incoming packets; The TeleportAccept packet for relative teleportations, and the BlockPlace and BlockDig for holding down right click
	 */
	@Override
	public boolean onPacketInterceptIn( Player reciever, Object packet ) {
		return true;
	}

	/**
	 * Edit the entity metadata packet so that the player's arms are in the right "state"
	 */
	private void handleMetadataPacket( Player player, PacketPlayOutEntityMetadata packet ) {
		List< Item< ? > > items;
		int id;
		try {
			items = ( List< Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			System.out.println( items );
			if ( items == null ) {
				return;
			}
			id = ( Integer ) ENTITYMETADATA_ID.get( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			return;
		}
		
		if ( ( ( CraftEntity ) player ).getEntityId() == id ) {
			System.out.println( 5 );
			return;
		}

		World world = player.getWorld();
		org.bukkit.entity.Entity entity = NMSUtils.getEntityFromId( world, id );
		
		if ( !( entity instanceof LivingEntity ) ) {
			System.out.println( 6 );
			return;
		}
		System.out.println( 2 );
		
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		
		byte handStateMask = -1;
		int pos = 0;
		for ( int index = 0; index < items.size(); index++ ) {
			Item< ? > item = items.get( index );
			if ( item.a().a() == HAND_STATE_INDEX ) {
				handStateMask = ( Byte ) item.b();
				pos = index;
				break;
			}
		}
		System.out.println( 1 );
		if ( handStateMask != -1 ) {
			byte bitmask = 0b000;
			if ( ( handStateMask & 0b010 ) == 0 ) {
				// This is optional; allows the player to use bows naturally
				if ( gEntity.getMainHand().getItem() == null ) {
					return;
				}
				if ( gEntity.getMainHand().getState() == State.DEFAULT ) {
					bitmask = 0b000;
				} else {
					bitmask = 0b001;
				}
			} else {
				// This is optional; allows the player to use bows naturally
				if ( gEntity.getOffHand().getItem() == null ) {
					return;
				}
				if ( gEntity.getOffHand().getState() == State.DEFAULT ) {
					bitmask = 0b010;
				} else {
					bitmask = 0b011;
				}
			}
			System.out.println( bitmask );
			items.remove( pos );
			items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), bitmask ) );
		}
	}

	/**
	 * Edit entity equipment including hand to show exactly what needs to be "displayed"
	 */
	private void handleEntityEquipmentPacket( Player player, PacketPlayOutEntityEquipment packet ) {
		int id;
		EnumItemSlot slot;
		// TODO move this somewhere nicer
		try {
			id = ( Integer ) ENTITYEQUIPMENT_ID.get( packet );
			slot = ( EnumItemSlot ) ENTITYEQUIPMENT_SLOT.get( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			return;
		}

		World world = player.getWorld();
		org.bukkit.entity.Entity entity = NMSUtils.getEntityFromId( world, id );
		
		if ( player == entity || !( entity instanceof LivingEntity ) ) {
			return;
		}
		
		EquipmentSlot equipment = NMSUtils.getEquipmentSlot( slot );
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		
		// Now either disguise or something
		// Temporary
		org.bukkit.inventory.ItemStack item;
		if ( equipment == EquipmentSlot.HAND ) {
			GunsmokeEntityHand hand = gEntity.getMainHand();
			item = hand.getHolding();
		} else if ( equipment == EquipmentSlot.OFF_HAND ) {
			GunsmokeEntityHand hand = gEntity.getOffHand();
			item = hand.getHolding();
		} else {
			item = gEntity.getWearing( equipment );
		}
		
		if ( item == null ) {
			return;
		}
		
		try {
			ENTITYEQUIPMENT_ITEMSTACK.set( packet, CraftItemStack.asNMSCopy( item ) );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the hand state according to the Gunsmoke Entity
	 */
	public void update( LivingEntity entity, boolean main ) {
		int id = ( ( CraftEntity ) entity ).getEntityId();
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		if ( ( gEntity.getMainHand().getItem() == null && main ) || ( gEntity.getOffHand().getItem() == null && !main ) ) {
			return;
		}
		
		DataWatcher watcher = ( ( CraftEntity ) entity ).getHandle().getDataWatcher();
		byte value = watcher.get( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ) );
		watcher.set( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), ( byte ) ( ( value & 0b001 ) | ( main ? 0b001 : 0b010 ) ) );
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata( id, watcher, false );
		
		broadcastPacket( entity, packet );
	}
	
	/**
	 * Update the player's equipment slot
	 */
	public void update( LivingEntity entity, EquipmentSlot slot ) {
		EnumItemSlot NMSSlot = NMSUtils.getEnumItemSlot( slot );
		int id = ( ( CraftEntity ) entity ).getEntityId();
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		org.bukkit.inventory.ItemStack item = gEntity.getWearing( slot );
		
		if ( slot == EquipmentSlot.HAND ) {
			item = gEntity.getMainHand().getHolding();
		} else if ( slot == EquipmentSlot.OFF_HAND ) {
			item = gEntity.getOffHand().getHolding();
		}
		
		if ( item != null ) {
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment( id, NMSSlot, CraftItemStack.asNMSCopy( item ) );
			
			broadcastPacket( entity, packet );
		}
	}

//	@Override
//	public void sendMessage( Player player, String message, Display display ) {
//		PacketPlayOutChat packet = new PacketPlayOutChat( new ChatComponentText( message ), ChatMessageType.a( display.location ) );
//		( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
//
//	}
//
//	/**
//	 * Teleport a player relative to their current location; Negative pitch indicates a movement upwards.
//	 */
//	@Override
//	public void teleportRelative( String player, Vector location, double yaw, double pitch ) {
//		Set< EnumPlayerTeleportFlags > set = new HashSet< EnumPlayerTeleportFlags >();
//		for ( EnumPlayerTeleportFlags flag : EnumPlayerTeleportFlags.values() ) {
//			set.add( flag );
//		}
//		int id = 42;
//		if ( location == null ) {
//			location = new Vector( 0, 0, 0 );
//		}
//		PacketPlayOutPosition packet = new PacketPlayOutPosition( location.getX(), location.getY(), location.getZ(), ( float ) yaw, ( float ) pitch, set, id );
//		OrdnanceUtil.sendPacket( player, packet );
//	}
//
//	@Override
//	public void playParticle( Player player, boolean everyoneElse, Particle particle, boolean farView, Location location, float dx, float dy, float dz, float speed, int count, int... params ) {
//		EnumParticle eParticle = FailSafe.getEnum( EnumParticle.class, particle.name().toUpperCase() );
//		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles( eParticle, farView, ( float ) location.getX(), ( float ) location.getY(), ( float ) location.getZ(), dx, dy, dz, speed, count, params );
//		if ( player == null || everyoneElse ) {
//			for ( Player rel : location.getWorld().getPlayers() ) {
//				if ( rel != player || !everyoneElse ) {
//					( ( CraftPlayer ) rel ).getHandle().playerConnection.sendPacket( packet );
//				}
//			}
//		} else {
//			( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
//		}
//	}
//
//	@Override
//	public void playBlockCrack( Location location, int level ) {
//		PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation( location.hashCode(), new BlockPosition( location.getBlockX(), location.getBlockY(), location.getBlockZ() ), level );
//		for ( Player player : location.getWorld().getPlayers() ) {
//			( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
//		}
//	}
//
//	@Override
//	public void setFOV( Player player, float value ) {
//		EntityPlayer entityPlayer = ( ( CraftPlayer ) player ).getHandle();
//
//		PacketPlayOutAbilities packet = new PacketPlayOutAbilities( entityPlayer.abilities );
//
//		packet.b( value );
//
//		entityPlayer.playerConnection.sendPacket( packet );
//	}
	
	private void sendPacket( Player entity, Packet< ? > packet ) {
		( ( CraftPlayer ) entity ).getHandle().playerConnection.sendPacket( packet );
	}
	
	private void broadcastPacket( org.bukkit.entity.Entity origin, Packet< ? > packet ) {
		World world = origin.getWorld();
		for ( Player player : world.getPlayers() ) {
			if ( origin != player ) {
				sendPacket( player, packet );
			}
		}
	}
}