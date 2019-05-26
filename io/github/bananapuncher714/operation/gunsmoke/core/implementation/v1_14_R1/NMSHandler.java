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

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.EnumParticle;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.MinecraftKey;
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
import network.aeternum.ordnance.api.events.EntityUpdateItemEvent;
import network.aeternum.ordnance.api.nms.PacketHandler.Display;
import network.aeternum.ordnance.api.objects.OrdnancePlayer;
import network.aeternum.ordnance.api.weapons.models.BowRepresentable;
import network.aeternum.ordnance.core.Ordnance;
import network.aeternum.ordnance.core.PlayerTracker;
import network.aeternum.ordnance.core.WeaponManager;
import network.aeternum.ordnance.core.ZoomManager;
import network.aeternum.ordnance.core.threading.SyncBukkit;
import network.aeternum.ordnance.core.util.FailSafe;
import network.aeternum.ordnance.core.util.ItemData;
import network.aeternum.ordnance.core.util.OrdnanceUtil;
import network.aeternum.ordnance.weapons.Weapon;

public class NMSHandler implements PacketHandler {
	private static Field ENTITYMETADATA_ITEMLIST;
	private static Field ENTITYMETADATA_ID;

	private static Field ENTITYEQUIPMENT_ID;
	private static Field ENTITYEQUIPMENT_SLOT;
	private static Field ENTITYEQUIPMENT_ITEMSTACK;

	private static Field BLOCKCHANGE_POSITION;

	private static Field SOUND_EFFECT;
	private static Field SOUND_EFFECT_KEY;

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

			SOUND_EFFECT = PacketPlayOutNamedSoundEffect.class.getDeclaredField( "a" );
			SOUND_EFFECT.setAccessible( true );
			SOUND_EFFECT_KEY = SoundEffect.class.getDeclaredField( "b" );
			SOUND_EFFECT_KEY.setAccessible( true );

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

	private Set< Integer > equipmentPackets = new HashSet< Integer >();

	/**
	 * Intercept outgoing packets; Edit them if they are the EntityMetadata packet or the EntityEquipment packet
	 */
	@Override
	public boolean onPacketInterceptOut( Player reciever, Object packet ) {
		if ( packet instanceof PacketPlayOutEntityMetadata ) {
			handleMetadataPacket( reciever, ( PacketPlayOutEntityMetadata ) packet );
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
	 * Edit the entity metadata packet so that the player is "drawing" a bow if their drawstate is true;
	 * Does not send any additional packets
	 */
	@SuppressWarnings("unchecked")
	private void handleMetadataPacket( Player player, PacketPlayOutEntityMetadata packet ) {
		List< Item< ? > > items;
		int id;
		try {
			items = ( List< Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			if ( items == null ) {
				return;
			}
			id = ( Integer ) ENTITYMETADATA_ID.get( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			return;
		}

		World world = player.getWorld();
		Entity entity = ( ( CraftWorld ) world ).getHandle().getEntity( id );
		if ( entity == null ) {
			return;
		}
		CraftEntity craftEntity = entity.getBukkitEntity();
		if ( !( craftEntity instanceof LivingEntity ) ) {
			return;
		}
		LivingEntity livingEntity = ( LivingEntity ) craftEntity;
		if ( player == livingEntity ) {
			return;
		}
		byte val = -1;
		int pos = 0;
		for ( int index = 0; index < items.size(); index++ ) {
			Item< ? > item = items.get( index );
			if ( item.a().a() == 6 ) {
				val = ( Byte ) item.b();
				pos = index;
				break;
			}
		}
		if ( val == -1 ) {
			return;
		}

//		OrdnancePlayer drawer = OrdnancePlayer.getPlayer( livingEntity );
//		if ( !drawer.isDrawing( val > 1 ) ) {
//			return;
//		}
//		if ( val > -1 ) {
//			items.remove( pos );
//			items.add( new Item< Byte >( new DataWatcherObject<>( 6, DataWatcherRegistry.a ), ( byte ) ( 1 | val ) ) );
//		}
	}

	/**
	 * Edit the entity equipment so that the item being held is a bow and is suitable for other players to see the "draw" animation
	 * Sends an EntityMetadata packet 1 tick after to update the players; Must be made more efficient.
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

		if ( slot != EnumItemSlot.MAINHAND && slot != EnumItemSlot.OFFHAND ) {
			return;
		}
		World world = player.getWorld();
		Entity entity = ( ( CraftWorld ) world ).getHandle().getEntity( id );
		if ( entity == null ) {
			return;
		}
		CraftEntity craftEntity = entity.getBukkitEntity();
		if ( !( craftEntity instanceof LivingEntity ) ) {
			return;
		}
		LivingEntity livingEntity = ( LivingEntity ) craftEntity;
		if ( player == livingEntity ) {
			return;
		}

		EquipmentSlot equipment;
		if ( slot == EnumItemSlot.MAINHAND ) {
			equipment = EquipmentSlot.HAND;
		} else {
			equipment = EquipmentSlot.OFF_HAND;
		}

		OrdnancePlayer drawer = OrdnancePlayer.getPlayer( livingEntity );

		if ( !equipmentPackets.contains( packet.hashCode() ) ) {
			CountDownLatch latch = new CountDownLatch( 1 );
			Bukkit.getScheduler().scheduleSyncDelayedTask( Ordnance.getInstance(), new Runnable() {
				@Override
				public void run() {
					EntityUpdateItemEvent event = new EntityUpdateItemEvent( livingEntity, drawer, equipment );
					Bukkit.getPluginManager().callEvent( event );
					latch.countDown();
				}
			} );
			try {
				latch.await();
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}
		equipmentPackets.add( packet.hashCode() );

		if ( drawer.getWeapon( slot == EnumItemSlot.OFFHAND ) == null ) {
			return;
		}

		try {
			Weapon weapon = drawer.getWeapon( slot == EnumItemSlot.OFFHAND );
			if ( weapon instanceof BowRepresentable ) {
				BowRepresentable bowWeapon = ( BowRepresentable ) weapon;
				ENTITYEQUIPMENT_ITEMSTACK.set( packet, CraftItemStack.asNMSCopy( new org.bukkit.inventory.ItemStack( bowWeapon.requireBow() ? Material.BOW : Material.SHIELD, 1, bowWeapon.getBowModel() ) ) );
			}
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask( Ordnance.getInstance(), new Runnable() {
			@Override
			public void run() {
				updateBow( livingEntity, player );
			}
		}, 1 );
	}
//	
//	/**
//	 * Update an entity's item to reflect the item it is holding; May get edited in the {{@link #handleEntityEquipmentPacket(Player, PacketPlayOutEntityEquipment)} method
//	 */
//	@Override
//	public void update( LivingEntity entity ) {
//		EntityLiving entityLiving = ( ( CraftLivingEntity ) entity ).getHandle();
//
//		for ( int i = 0; i < 2; i++ ) {
//			EnumItemSlot slot = i == 0 ? EnumItemSlot.MAINHAND : EnumItemSlot.OFFHAND;
//			ItemStack item = entityLiving.getEquipment( slot );
//			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment( entity.getEntityId(), slot, item );
//
//			for ( Player player : Bukkit.getOnlinePlayers() ) {
//				if ( player == entity ) {
//					continue;
//				}
//				( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
//			}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void updateBow( LivingEntity entity, Player player ) {
//		if ( entity == player ) {
//			return;
//		}
//		EntityLiving livingEntity = ( ( CraftLivingEntity ) entity ).getHandle();
//		OrdnancePlayer drawer = OrdnancePlayer.getPlayer( entity );
//		for ( int i = 0; i < 2; i++ ) {
//			if ( drawer.getWeapon( i == 1 ) == null ) {
//				continue;
//			}
//			PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata( livingEntity.getId(), livingEntity.getDataWatcher(), false );
//			List< Item< ? > > items;
//			try {
//				items = ( List< Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( metadata );
//				if ( items == null ) {
//					items = new ArrayList< Item< ? > >();
//					ENTITYMETADATA_ITEMLIST.set( metadata, items );
//				}
//			} catch ( IllegalArgumentException | IllegalAccessException e ) {
//				e.printStackTrace();
//				return;
//			}
//
//			byte val = ( byte ) ( 1 + i * 2 );
//			if ( !drawer.isDrawing( i == 1 ) ) {
//				val = ( byte ) ( val - 1 );
//			}
//			items.add( new Item< Byte >( new DataWatcherObject<>( 6, DataWatcherRegistry.a ), val )  );
//			( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( metadata );
//		}
//	}
//
//	/**
//	 * Update the entity's bow draw animation for everyone on the server. Does not get processed additionally.
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public void updateBow( LivingEntity entity ) {
//		EntityLiving livingEntity = ( ( CraftLivingEntity ) entity ).getHandle();
//		OrdnancePlayer drawer = OrdnancePlayer.getPlayer( entity );
//		for ( int i = 0; i < 2; i++ ) {
//			if ( drawer.getWeapon( i == 1 ) == null ) {
//				continue;
//			}
//			PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata( livingEntity.getId(), livingEntity.getDataWatcher(), false );
//			List< Item< ? > > items;
//			try {
//				items = ( List< Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( metadata );
//				if ( items == null ) {
//					items = new ArrayList< Item< ? > >();
//					ENTITYMETADATA_ITEMLIST.set( metadata, items );
//				}
//			} catch ( IllegalArgumentException | IllegalAccessException e ) {
//				e.printStackTrace();
//				return;
//			}
//
//			byte val = ( byte ) ( 1 + i * 2 );
//			if ( !drawer.isDrawing( i == 1 ) ) {
//				val = ( byte ) ( val - 1 );
//			}
//			items.add( new Item< Byte >( new DataWatcherObject<>( 6, DataWatcherRegistry.a ), val )  );
//			for ( Player player : Bukkit.getOnlinePlayers() ) {
//				if ( player != entity ) {
//					( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( metadata );
//				}
//			}
//		}
//	}
//
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
}