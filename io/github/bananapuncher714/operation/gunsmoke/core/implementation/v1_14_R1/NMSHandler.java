package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;

import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerPressRespawnButtonEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.NBTCompound;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayerHand;
import io.github.bananapuncher714.operation.gunsmoke.api.tracking.GunsmokeEntityTracker;
import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import net.minecraft.server.v1_14_R1.AttributeInstance;
import net.minecraft.server.v1_14_R1.AxisAlignedBB;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcher.Item;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityPose;
import net.minecraft.server.v1_14_R1.EntitySize;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EntityZombie;
import net.minecraft.server.v1_14_R1.EnumCreatureType;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.Fluid;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.LightEngine;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.MovingObjectPositionBlock;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayInAdvancements;
import net.minecraft.server.v1_14_R1.PacketPlayInAdvancements.Status;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockPlace;
import net.minecraft.server.v1_14_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_14_R1.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_14_R1.PacketPlayInFlying;
import net.minecraft.server.v1_14_R1.PacketPlayInTeleportAccept;
import net.minecraft.server.v1_14_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_14_R1.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_14_R1.PacketPlayOutLightUpdate;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition.EnumPlayerTeleportFlags;
import net.minecraft.server.v1_14_R1.PacketPlayOutUpdateAttributes;
import net.minecraft.server.v1_14_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_14_R1.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_14_R1.PlayerChunkMap.EntityTracker;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.RayTrace;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.VoxelShape;
import net.minecraft.server.v1_14_R1.WorldServer;

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

	private static Field PLAYERABILITIES_FOV;

	private static Field TELEPORT_AWAIT;
	
	private static Field[] WORLDBORDERPACKET_FIELDS;
	
	private static Field ENTITYTRACKER_ENTITY;
	
	private static Method VOXEL_SHAPE_CONTAINS;
	
	private static Map< EntityPose, EntitySize > sizes = new HashMap< EntityPose, EntitySize >();

	private static Set< EnumPlayerTeleportFlags > TELEPORT_FLAGS;
	
	static {
		try {
			ENTITYMETADATA_ITEMLIST = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
			ENTITYMETADATA_ITEMLIST.setAccessible(true);

			ENTITYMETADATA_ID = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
			ENTITYMETADATA_ID.setAccessible(true);

			ENTITYEQUIPMENT_ID = PacketPlayOutEntityEquipment.class.getDeclaredField("a");
			ENTITYEQUIPMENT_ID.setAccessible(true);

			ENTITYEQUIPMENT_SLOT = PacketPlayOutEntityEquipment.class.getDeclaredField("b");
			ENTITYEQUIPMENT_SLOT.setAccessible(true);

			ENTITYEQUIPMENT_ITEMSTACK = PacketPlayOutEntityEquipment.class.getDeclaredField("c");
			ENTITYEQUIPMENT_ITEMSTACK.setAccessible(true);

			BLOCKCHANGE_POSITION = PacketPlayOutBlockChange.class.getDeclaredField("a");
			BLOCKCHANGE_POSITION.setAccessible(true);

			MAPCHUNK_X = PacketPlayOutMapChunk.class.getDeclaredField("a");
			MAPCHUNK_X.setAccessible(true);

			MAPCHUNK_Z = PacketPlayOutMapChunk.class.getDeclaredField("b");
			MAPCHUNK_Z.setAccessible(true);

			PLAYERABILITIES_FOV = PacketPlayOutAbilities.class.getDeclaredField("f");
			PLAYERABILITIES_FOV.setAccessible(true);
			
			TELEPORT_AWAIT = PlayerConnection.class.getDeclaredField( "teleportAwait" );
			TELEPORT_AWAIT.setAccessible( true );
			
			VOXEL_SHAPE_CONTAINS = VoxelShape.class.getDeclaredMethod( "b", double.class, double.class, double.class );
			VOXEL_SHAPE_CONTAINS.setAccessible( true );
			
			ENTITYTRACKER_ENTITY = EntityTracker.class.getDeclaredField( "tracker" );
			ENTITYTRACKER_ENTITY.setAccessible( true );
			
			WORLDBORDERPACKET_FIELDS = new Field[ 9 ];
			WORLDBORDERPACKET_FIELDS[ 0 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "a" );
			WORLDBORDERPACKET_FIELDS[ 1 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "b" );
			WORLDBORDERPACKET_FIELDS[ 2 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "c" );
			WORLDBORDERPACKET_FIELDS[ 3 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "d" );
			WORLDBORDERPACKET_FIELDS[ 4 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "e" );
			WORLDBORDERPACKET_FIELDS[ 5 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "f" );
			WORLDBORDERPACKET_FIELDS[ 6 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "g" );
			WORLDBORDERPACKET_FIELDS[ 7 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "h" );
			WORLDBORDERPACKET_FIELDS[ 8 ] = PacketPlayOutWorldBorder.class.getDeclaredField( "i" );
			for ( Field field : WORLDBORDERPACKET_FIELDS ) {
				field.setAccessible( true );
			}
			
			Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
			
			Field universal = EntityHuman.class.getDeclaredField( "b" );
			universal.setAccessible( true );
			universal.getModifiers();
			
            modifiersField.setInt( universal, universal.getModifiers() & ~Modifier.FINAL );
			
            Map< EntityPose, EntitySize > poses = ( Map< EntityPose, EntitySize > ) universal.get( null );
            
            if ( !( poses instanceof HashMap ) ) {
	            for ( EntityPose pose : poses.keySet() ) {
	            	sizes.put( pose, poses.get( pose ) );
	            }
	            
	            universal.set( null, sizes );
            } else {
            	sizes = poses;
            }
            
            TELEPORT_FLAGS = Collections.unmodifiableSet( EnumSet.allOf( EnumPlayerTeleportFlags.class ) );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException e ) {
			e.printStackTrace();
		}
	}
	
	private Gunsmoke plugin;
	private NMSTracker entityTracker;
	
	public NMSHandler() {
		NMSUtils.register( "gunbot", EntityTypes.ZOMBIE, NMSUtils.create( new EntityTypes.b< EntityZombie >() {
			@Override
			public GunBot create( EntityTypes< EntityZombie > arg0, net.minecraft.server.v1_14_R1.World arg1 ) {
				return new GunBot( arg0, arg1 );
			}
		}, EnumCreatureType.MONSTER, .6, 1.8 ) );
		
		NMSUtils.register( "test", EntityTypes.PLAYER, NMSUtils.create( new EntityTypes.b< EntityPlayer >() {
			@Override
			public EntityPlayer create( EntityTypes< EntityPlayer > arg0, net.minecraft.server.v1_14_R1.World arg1 ) {
				return new TestEntity( arg0, arg1, "scr" );
			}
		}, EnumCreatureType.MISC, .6, 1.8 ) );
		
		entityTracker = new NMSTracker();
	}
	
	public void setGunsmoke( Gunsmoke plugin ) {
		this.plugin = plugin;
	}

	public void tick() {
		entityTracker.tick();
	}
	
	/**
	 * Intercept outgoing packets; Edit them if they are the EntityMetadata packet
	 * or the EntityEquipment packet
	 */
	@Override
	public Object onPacketInterceptOut( Player reciever, Object packet ) {
		if ( packet instanceof PacketPlayOutEntityMetadata ) {
			return handleMetadataPacket( reciever, ( PacketPlayOutEntityMetadata ) packet );
		} else if ( packet instanceof PacketPlayOutEntityEquipment ) {
			return handleEntityEquipmentPacket( reciever, ( PacketPlayOutEntityEquipment ) packet );
		} else if ( packet instanceof PacketPlayOutAbilities ) {
			handleAbilitiesPacket( reciever, ( PacketPlayOutAbilities ) packet );
		}
		return packet;
	}

	/**
	 * Catch all incoming packets; The TeleportAccept packet for relative
	 * teleportations, and the BlockPlace and BlockDig for holding down right click
	 */
	@Override
	public Object onPacketInterceptIn( Player reciever, Object packet ) {
		if ( packet instanceof PacketPlayInFlying ) {
			return handleFlyingPacket( reciever, ( PacketPlayInFlying ) packet );
		} else if ( packet instanceof PacketPlayInBlockPlace ) {
			return handleBlockPlacePacket( reciever, ( PacketPlayInBlockPlace ) packet );
		} else if ( packet instanceof PacketPlayInBlockDig ) {
			return handleBlockDigPacket( reciever, ( PacketPlayInBlockDig ) packet );
		} else if ( packet instanceof PacketPlayInAdvancements ) {
			return handleAdvancementPacket( reciever, ( PacketPlayInAdvancements ) packet );
		} else if ( packet instanceof PacketPlayInTeleportAccept ) {
			return handleTeleportAcceptPacket( reciever, ( PacketPlayInTeleportAccept ) packet );
		} else if ( packet instanceof PacketPlayInClientCommand ) {
			return handleRespawnRequest( reciever, ( PacketPlayInClientCommand ) packet );
		}
		return packet;
	}
	
	private Packet< ? > handleRespawnRequest( Player player, PacketPlayInClientCommand packet ) {
		new PlayerPressRespawnButtonEvent( player ).callEvent();
		return packet;
	}
	
	private Packet< ? > handleTeleportAcceptPacket( Player player, PacketPlayInTeleportAccept packet ) {
		return packet.b() == 0 ? null : packet;
	}
	
	private Packet< ? > handleAdvancementPacket( Player player, PacketPlayInAdvancements packet ) {
		if ( packet.c() == Status.OPENED_TAB ) {
			plugin.getTaskManager().callEventSync( new AdvancementOpenEvent( player, packet.d().getKey() ) );
		}
		return packet;
	}

	private Packet< ? > handleBlockPlacePacket( Player player, PacketPlayInBlockPlace packet ) {
		if ( BukkitUtil.isRightClickable( player.getEquipment().getItemInMainHand().getType() ) ) {
			plugin.getPlayerManager().setHolding( player, true );
		}
		return packet;
	}
	
	private Packet< ? > handleBlockDigPacket( Player player, PacketPlayInBlockDig packet ) {
		if ( packet.d() == EnumPlayerDigType.RELEASE_USE_ITEM ) {
			plugin.getPlayerManager().setHolding( player, false );
		} else if ( packet.d() == EnumPlayerDigType.DROP_ITEM ) {
			DropItemEvent event = new DropItemEvent( player );
			
			CountDownLatch latch = new CountDownLatch( 1 );
			Bukkit.getScheduler().scheduleSyncDelayedTask( plugin, () -> {
				Bukkit.getPluginManager().callEvent( event );
				latch.countDown();
			} );
			
			try {
				latch.await();
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
			
			if ( event.isCancelled() ) {
				player.getEquipment().setItemInMainHand( player.getEquipment().getItemInMainHand() );
				return null;
			}
		}
		return packet;
	}
	
	/**
	 * Edit the entity metadata packet so that the player's arms are in the right
	 * "state"
	 */
	private Packet< ? > handleMetadataPacket(Player player, PacketPlayOutEntityMetadata packet) {
		List< Item< ? > > items;
		int id;
		try {
			List< Item< ? > > itemList = ( List<Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			if ( itemList == null ) {
				return packet;
			}
			items = new ArrayList< Item< ? > >( itemList );
			
			id = ( Integer ) ENTITYMETADATA_ID.get( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			return packet;
		}

		World world = player.getWorld();
		org.bukkit.entity.Entity entity = NMSUtils.getEntityFromId( world, id );

		if ( !( entity instanceof LivingEntity ) ) {
			return packet;
		}

		GunsmokePlayer gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );

		Packet< ? > result;
		if ( entity == player ) {
			result = handleInternalMetadataPacket( player, items, gEntity ) ? packet : null;
		} else {
			result = handleExternalMetadataPacket( player, ( LivingEntity ) entity, items, gEntity ) ? packet : null;
		}
		try {
			ENTITYMETADATA_ITEMLIST.set( packet, items );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private boolean handleInternalMetadataPacket( Player reciever, List< Item< ? > > items, GunsmokePlayer entity ) {
		if ( entity.isProne() ) {
			for ( int index = 0; index < items.size(); index++ ) {
				if ( items.get( index ).a().a() == 6 ) {
					items.remove( index );
					// Re-set the player's hitbox in case if they come out from under a block
					if ( entity.isProne() ) {
						set( reciever, entity.isProne() );
					}
					break;
				}
			}
		}
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
		if ( handStateMask > -1 ) {
			byte bitmask = ( byte ) ( ( handStateMask & 0b011 ) | ( entity.isProne() ? 0b100 : 0b000 ) );
			items.remove( pos );
			items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), bitmask ) );
		}
		return items.size() > 0;
	}
	
	private boolean handleExternalMetadataPacket( Player reciever, LivingEntity livingEntity, List< Item< ? > > items, GunsmokePlayer entity ) {
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
		if ( handStateMask != -1 ) {
			byte bitmask = 0b000;
			
			// So, do not send 10 or 00 unless both are DEFAULT state
			if ( entity.getMainHand().getState() == State.DEFAULT && entity.getOffHand().getState() == State.DEFAULT ) {
				bitmask = 0b000;
			} else if ( entity.getMainHand().getState() != State.DEFAULT ) {
				bitmask = 0b001;
			} else {
				bitmask = 0b011;
			}
			
			if ( entity.getMainHand().getHolding() == null && entity.getOffHand().getHolding() == null ) {
				// Resort to default if the hand is not holding anything
				bitmask = ( byte ) ( handStateMask & 0b011 );
			}
			
			items.remove( pos );
			items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), bitmask ) );
		}
		
		// We want to remove index 6 if the living entity is prone, aka cancel sneak/swim/sprint
		// Then, we want to add number 6
		for ( int index = 0; index < items.size(); index++ ) {
			if ( items.get( index ).a().a() == 6 ) {
				items.remove( index );
				break;
			}
		}
		if ( entity.isProne() ) {
			items.add( new Item< EntityPose >( new DataWatcherObject< EntityPose >( 6, DataWatcherRegistry.s ), EntityPose.SWIMMING ) );
		} else {
			items.add( new Item< EntityPose >( new DataWatcherObject< EntityPose >( 6, DataWatcherRegistry.s ), ( ( CraftLivingEntity ) livingEntity ).getHandle().getDataWatcher().get( new DataWatcherObject< EntityPose >( 6, DataWatcherRegistry.s ) ) ) );
		}
		
		return items.size() > 0;
	}

	/**
	 * Edit entity equipment including hand to show exactly what needs to be, as well as calling an update event when changing items to display
	 * "displayed"
	 */
	private Packet< ? > handleEntityEquipmentPacket( Player player, PacketPlayOutEntityEquipment packet ) {
		int id;
		EnumItemSlot slot;
		ItemStack nmsItem;
		// TODO move this somewhere nicer
		try {
			id = ( Integer ) ENTITYEQUIPMENT_ID.get( packet );
			slot = ( EnumItemSlot ) ENTITYEQUIPMENT_SLOT.get( packet );
			nmsItem = ( ItemStack ) ENTITYEQUIPMENT_ITEMSTACK.get( packet );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			return packet;
		}

		if ( ( ( CraftEntity ) player ).getEntityId() == id ) {
			// We know that the player has experienced a change item in hand packet
			// Oh wait, this isnt ever called
			if ( slot == EnumItemSlot.MAINHAND || slot == EnumItemSlot.OFFHAND ) {
				PlayerUpdateItemEvent event = new PlayerUpdateItemEvent( player, nmsItem == null ? null : CraftItemStack.asBukkitCopy( nmsItem ), NMSUtils.getEquipmentSlot( slot ) );
				event.callEvent();
			}
			
			return packet;
		}

		World world = player.getWorld();
		org.bukkit.entity.Entity entity = NMSUtils.getEntityFromId(world, id);

		if (!(entity instanceof LivingEntity)) {
			return packet;
		}

		EquipmentSlot equipment = NMSUtils.getEquipmentSlot( slot );
		GunsmokePlayer gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );

		// Now either disguise or something
		// Temporary
		org.bukkit.inventory.ItemStack item = null;
		if ( equipment == EquipmentSlot.HAND ) {
			GunsmokePlayerHand hand = gEntity.getMainHand();
			item = hand.getHolding();
		} else if ( equipment == EquipmentSlot.OFF_HAND ) {
			GunsmokePlayerHand hand = gEntity.getOffHand();
			item = hand.getHolding();
		} else {
			ItemStackGunsmoke gItem = gEntity.getWearing( equipment );
			if ( gItem != null ) {
				item = gItem.getItem();
			}
		}

		if ( item == null ) {
			return packet;
		}

		try {
			ENTITYEQUIPMENT_ITEMSTACK.set( packet, CraftItemStack.asNMSCopy( item ) );
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return packet;
	}

	private void handleAbilitiesPacket( Player player, PacketPlayOutAbilities packet ) {
		// TODO
		try {
			 float fov = ( float ) PLAYERABILITIES_FOV.getFloat( packet );
			 if ( plugin.getZoomManager().getZoomLevel( player ) == null ) {
				 packet.b( .1f );
			 } else {
				 packet.b( fov );
			 }
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}
	
	private Packet< ? > handleFlyingPacket( Player player, PacketPlayInFlying packet ) {
		if ( player.isOnGround() && !packet.b() && packet.b( player.getLocation().getY() ) - player.getLocation().getY() > 0 ) {
			PlayerJumpEvent event = new PlayerJumpEvent( player );
			plugin.getTaskManager().callEventSync( event );
		}
		return packet;
	}
	
	
	// Debug method
	public void darkness( Player player ) {
		LightEngine engine = ( ( CraftWorld ) player.getWorld() ).getHandle().getChunkProvider().getLightEngine();
		int x = player.getLocation().getBlockX() >> 4;
		int z = player.getLocation().getBlockZ() >> 4;
		ChunkCoordIntPair coords = new ChunkCoordIntPair( x, z );
		
		PacketPlayOutLightUpdate packet = new PacketPlayOutLightUpdate( coords, engine );
		
		// 1x18
		int val = 0b111111111111111111;
		
		try {
			Field[] fields = new Field[ 8 ];
			fields[ 0 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "a" );
			fields[ 1 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "b" );
			fields[ 2 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "c" );
			fields[ 3 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "d" );
			fields[ 4 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "e" );
			fields[ 5 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "f" );
			fields[ 6 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "g" );
			fields[ 7 ] = PacketPlayOutLightUpdate.class.getDeclaredField( "h" );
			
			for ( Field field : fields ) {
				field.setAccessible( true );
			}
			
			List< byte[] > skyLevels = new ArrayList< byte[] >();
			List< byte[] > groundLevels = new ArrayList< byte[] >();
			for ( int i = 0; i < 18; i++ ) {
				byte[] skyNibble = new byte[ 2048 ];
				Arrays.fill( skyNibble, ( byte ) 0 );
				skyLevels.add( skyNibble );
				
				byte[] groundNibble = new byte[ 2048 ];
				Arrays.fill( groundNibble, ( byte ) 0 );
				groundLevels.add( groundNibble );
			}
			
			fields[ 2 ].set( packet, val );
//			fields[ 3 ].set( packet, val );
			fields[ 4 ].set( packet, 0 );
//			fields[ 5 ].set( packet, 0 );
			fields[ 6 ].set( packet, skyLevels );
//			fields[ 7 ].set( packet, groundLevels );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		
		plugin.getProtocol().sendPacket( player, packet );
	}
	
	@Override
	public void playHurtAnimationFor( LivingEntity entity ) {
		if ( entity instanceof Player ) {
			List< AttributeInstance > attributes = new ArrayList< AttributeInstance >();
			attributes.add( ( ( CraftLivingEntity ) entity ).getHandle().getAttributeInstance( GenericAttributes.MAX_HEALTH ) );
			PacketPlayOutUpdateAttributes attributePacket = new PacketPlayOutUpdateAttributes( entity.getEntityId(), attributes );

			plugin.getProtocol().sendPacket( ( Player ) entity , attributePacket );
		}
		
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus( ( ( CraftEntity ) entity ).getHandle(), ( byte ) 2 );
		broadcastPacket( entity, packet, true );
	}

	@Override
	public void update( LivingEntity entity, boolean main ) {
		update( entity, main, false );
	}

	/**
	 * Update the hand state according to the Gunsmoke Entity
	 */
	public void update( LivingEntity entity, boolean main, boolean updateSelf ) {
		int id = entity.getEntityId();
		DataWatcher watcher = ( ( CraftEntity ) entity ).getHandle().getDataWatcher();
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata( id, watcher, false );

		List< Item< ? > > items = null;
		try {
			items = ( List< Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			if ( items == null ) {
				items = new ArrayList< Item< ? > >();
				ENTITYMETADATA_ITEMLIST.set( packet, items );
			}
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		byte value = watcher.get( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ) );
		
		value = ( byte ) ( ( main ? 0b000 : 0b010 ) | ( value & 0b001 ) );
		
		items.clear();
		items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), value ) );
		
		broadcastPacket( entity, packet, updateSelf );
	}

	/**
	 * Update the player's equipment slot
	 */
	public void update( LivingEntity entity, EquipmentSlot slot ) {
		EnumItemSlot NMSSlot = NMSUtils.getEnumItemSlot( slot );
		int id = ( ( CraftEntity ) entity ).getEntityId();
		GunsmokePlayer gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		ItemStackGunsmoke gItem = gEntity.getWearing( slot );
		org.bukkit.inventory.ItemStack item = gItem == null ? null : gItem.getItem();

		if ( slot == EquipmentSlot.HAND ) {
			item = gEntity.getMainHand().getHolding();
		} else if ( slot == EquipmentSlot.OFF_HAND ) {
			item = gEntity.getOffHand().getHolding();
		}

		if ( item != null ) {
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment( id, NMSSlot,CraftItemStack.asNMSCopy( item ) );

			broadcastPacket( entity, packet, false );
		}
	}

	@Override
	public void teleportRelative( String player, Vector vector, double yaw, double pitch ) {
		vector = vector == null ? new Vector( 0, 0, 0 ) : vector;
		try {
			Object connObj = plugin.getProtocol().getPlayerConnection( player );
			if ( connObj != null ) {
				PlayerConnection connection = ( PlayerConnection ) connObj;
				PacketPlayOutPosition packet = new PacketPlayOutPosition( vector.getX(), vector.getY(), vector.getZ(), ( float ) yaw, ( float ) pitch, TELEPORT_FLAGS, TELEPORT_AWAIT.getInt( connection ) );
				plugin.getProtocol().sendPacket( player, packet );
			} else {
				System.out.println( "NULL" );
			}
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void set( HumanEntity player, boolean down ) {
		try {
			Field size = Entity.class.getDeclaredField( "size" );
			size.setAccessible( true );
			Field height = Entity.class.getDeclaredField( "headHeight" );
			height.setAccessible( true );
			
			EntityPose pose = ( ( CraftEntity ) player ).getHandle().getPose();
			
			EntitySize original = sizes.get( pose );
			if ( down ) {
				sizes.put( pose, EntitySize.b( 0.6f, 0.6f ) );
			}

			( ( CraftEntity ) player ).getHandle().updateSize();

			EntitySize playerSize = ( EntitySize ) size.get( ( ( CraftEntity ) player ).getHandle() );
			height.set( ( ( CraftEntity ) player ).getHandle(), playerSize.height * 0.85f );
			
			sizes.put( pose, original );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void setAir( Player player, int air ) {
		int id = player.getEntityId();
		DataWatcher watcher = ( ( CraftEntity ) player ).getHandle().getDataWatcher();
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata( id, watcher, false );

		List< Item< ? > > items = null;
		try {
			items = ( List< Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			if ( items == null ) {
				items = new ArrayList< Item< ? > >();
				ENTITYMETADATA_ITEMLIST.set( packet, items );
			}
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		items.clear();
		items.add( new Item< Integer >( new DataWatcherObject< Integer >( 1, DataWatcherRegistry.b ), air ) );
		
		plugin.getProtocol().sendPacket( player, packet );
	}
	
	@Override
	public boolean isCurrentThreadMain() {
		return Thread.currentThread() == MinecraftServer.getServer().serverThread;
	}
	
	@Override
	public int getServerTick() {
		return MinecraftServer.currentTick;
	}
	
	@Override
	public void damageBlock( Location location, int stage ) {
		location.setYaw( 0 );
		location.setPitch( 0 );
		location = BukkitUtil.getBlockLocation( location );
		PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation( location.hashCode(), new BlockPosition( location.getBlockX(), location.getBlockY(), location.getBlockZ() ), stage );
		
		broadcastPacket( location.getWorld(), packet );
	}

	@Override
	public List< CollisionResultBlock > rayTrace( Location start, Vector vector, double distance ) {
		return rayTrace( start, vector.clone().normalize().multiply( distance ) );
	}
	
	@Override
	public List< CollisionResultBlock > rayTrace( Location start, Vector vector ) {
		net.minecraft.server.v1_14_R1.World world = ( ( CraftWorld ) start.getWorld() ).getHandle();
		List< CollisionResultBlock > collisions = new ArrayList< CollisionResultBlock >();
		for ( MovingObjectPositionBlock result : rayTrace( start, vector, new BiFunction< RayTrace, BlockPosition, MovingObjectPositionBlock >() {
			@Override
			public MovingObjectPositionBlock apply( RayTrace ray, BlockPosition blockPosition ) {
				IBlockData hitBlock = world.getType( blockPosition );
	            Fluid hitFluid = world.getFluid( blockPosition);
	            Vec3D getStart = ray.b();
	            Vec3D getEnd = ray.a();
	            VoxelShape blockShape = ray.a( hitBlock, world, blockPosition );
	            MovingObjectPositionBlock blockResult = world.rayTrace( getStart, getEnd, blockPosition, blockShape, hitBlock );
	            VoxelShape fluidShape = ray.a( hitFluid, world, blockPosition );
	            MovingObjectPositionBlock fluidResult = fluidShape.rayTrace( getStart, getEnd, blockPosition);
	            // Get whichever one is shorter and return
	            double blockDistance = blockResult == null ? Double.MAX_VALUE : ray.b().distanceSquared( blockResult.getPos() );
	            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : ray.b().distanceSquared( fluidResult.getPos() );
	            return blockDistance <= fluidDistance ? blockResult : fluidResult;
			}
		} ) ) {
			collisions.add( getResultFrom( world, result ) );
		}
		return collisions;
	}
	
	private List< MovingObjectPositionBlock > rayTrace( Location origin, Vector vector, BiFunction< RayTrace, BlockPosition, MovingObjectPositionBlock > biFunction ) {
		Location dest = origin.clone().add( vector );
		Vec3D start = new Vec3D( origin.getX(), origin.getY(), origin.getZ() );
		Vec3D end = new Vec3D( dest.getX(), dest.getY(), dest.getZ() );
		RayTrace trace = new RayTrace( start, end, RayTrace.BlockCollisionOption.OUTLINE, RayTrace.FluidCollisionOption.NONE, null );
		
		BlockIterator iterator = new BlockIterator( origin.getWorld(), origin.toVector(), vector, 0, ( int ) ( 1 + vector.length() ) );

	    List< MovingObjectPositionBlock > results = new ArrayList< MovingObjectPositionBlock >();
	    
	    while ( iterator.hasNext() ) {
	    	Block block = iterator.next();
	    	Location bLoc = block.getLocation();
	    	MovingObjectPositionBlock result = biFunction.apply( trace, new BlockPosition( bLoc.getBlockX(), bLoc.getBlockY(), bLoc.getBlockZ() ) );
	    	if ( result != null ) {
	    		results.add( result );
	    	}
	    }
	    
	    return results;
	}
	
	@Override
	public GunsmokeEntityTracker getEntityTrackerFor( org.bukkit.entity.Entity bukkitEntity ) {
		return entityTracker.getEntityTrackerFor( bukkitEntity );
	}
	
	@Override
	public List< org.bukkit.entity.Entity > getNearbyEntities( org.bukkit.entity.Entity entity, Location location, Vector vector ) {
		net.minecraft.server.v1_14_R1.World world = ( ( CraftWorld ) location.getWorld() ).getHandle();
	
		AxisAlignedBB axis = new AxisAlignedBB( location.getX(), location.getY(), location.getZ(),
				location.getX() + vector.getX() * 2, location.getY() + vector.getY() * 2, location.getZ() + vector.getZ() * 2 );
		
		Entity nmsEntity = entity == null ? null : ( ( CraftEntity ) entity ).getHandle();
		
		List< org.bukkit.entity.Entity > nearby = new ArrayList< org.bukkit.entity.Entity >();
		for ( Entity nearbyEntity : world.getEntities( nmsEntity, axis ) ) {
			nearby.add( nearbyEntity.getBukkitEntity() );
		}
		return nearby;
	}
	
	@Override
	public void setTint( Player player, double percent ) {
		int lower = 30_000_000;
		int higher = 600_000_000;
		percent = percent * percent * percent;
		int diff = ( int ) ( ( higher - lower ) * percent );
		
		net.minecraft.server.v1_14_R1.World world = ( ( CraftWorld ) player.getWorld() ).getHandle();
		
		try {
			PacketPlayOutWorldBorder centerPacket = new PacketPlayOutWorldBorder( world.getWorldBorder(), EnumWorldBorderAction.SET_CENTER );
			WORLDBORDERPACKET_FIELDS[ 2 ].set( centerPacket, player.getLocation().getX() );
			WORLDBORDERPACKET_FIELDS[ 3 ].set( centerPacket, player.getLocation().getY() );
			WORLDBORDERPACKET_FIELDS[ 5 ].set( centerPacket, 60_000_000.0 );
			WORLDBORDERPACKET_FIELDS[ 7 ].set( centerPacket, 0 );
			PacketPlayOutWorldBorder warningPacket = new PacketPlayOutWorldBorder( world.getWorldBorder(), EnumWorldBorderAction.SET_WARNING_BLOCKS );
			WORLDBORDERPACKET_FIELDS[ 8 ].set( warningPacket, diff + lower );
			WORLDBORDERPACKET_FIELDS[ 5 ].set( centerPacket, 60_000_000.0 );
			WORLDBORDERPACKET_FIELDS[ 7 ].set( centerPacket, 0 );
			
			plugin.getProtocol().sendPacket( player, centerPacket );
			plugin.getProtocol().sendPacket( player, warningPacket );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isRealPlayer( Player player ) {
		return ( ( CraftPlayer ) player ).getHandle().getClass().equals( EntityPlayer.class );
	}
	
	@Override
	public GunsmokeNPC getNPC( Player player ) {
		EntityPlayer ep = ( ( CraftPlayer ) player ).getHandle();
		if ( ep instanceof GunsmokeNPC ) {
			return ( GunsmokeNPC ) ep;
		}
		return null;
	}
	
	@Override
	public GunsmokeNPC spawnNPC( World bukkitWorld, String name, String skin ) {
		WorldServer world = ( ( CraftWorld ) bukkitWorld ).getHandle();
		
		GameProfile profile = NMSUtils.convert( new GameProfile( UUID.randomUUID(), name ), skin );
		
		TestEntity npc = new TestEntity( world, profile );
		
		world.addEntity( npc );
		
		return npc;
	}
	
	@Override
	public void display( Player player ) {
		List< CollisionResultBlock > collisions = rayTrace( player.getEyeLocation(), player.getLocation().getDirection(), 20 );
		if ( collisions.isEmpty() ) {
			return;
		}
		Block block = collisions.get( 0 ).getBlock();
		WorldServer world = ( ( CraftWorld ) player.getWorld() ).getHandle();
		Location location = block.getLocation();
		BlockPosition blockPos = new BlockPosition( location.getBlockX(), location.getBlockY(), location.getBlockZ() );
		IBlockData hitBlock = world.getType( blockPos );
		VoxelShape blockShape = hitBlock.getCollisionShape( world, blockPos );
		
		for ( AxisAlignedBB box : blockShape.d() ) {
			player.getWorld().spawnParticle( Particle.FLAME, box.maxX + location.getBlockX(), box.maxY + location.getBlockY(), box.maxZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.minX + location.getBlockX(), box.maxY + location.getBlockY(), box.maxZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.maxX + location.getBlockX(), box.minY + location.getBlockY(), box.maxZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.minX + location.getBlockX(), box.minY + location.getBlockY(), box.maxZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.maxX + location.getBlockX(), box.maxY + location.getBlockY(), box.minZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.minX + location.getBlockX(), box.maxY + location.getBlockY(), box.minZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.maxX + location.getBlockX(), box.minY + location.getBlockY(), box.minZ + location.getBlockZ(), 0 );
			player.getWorld().spawnParticle( Particle.FLAME, box.minX + location.getBlockX(), box.minY + location.getBlockY(), box.minZ + location.getBlockZ(), 0 );
		}
	}
	
	@Override
	public AABB[] getBoxesFor( Location location ) {
		WorldServer world = ( ( CraftWorld ) location.getWorld() ).getHandle();
		BlockPosition blockPos = new BlockPosition( location.getBlockX(), location.getBlockY(), location.getBlockZ() );
		IBlockData hitBlock = world.getType( blockPos );
		VoxelShape blockShape = hitBlock.getCollisionShape( world, blockPos );
		List< AxisAlignedBB > boxes = blockShape.d();
		AABB[] boundingBoxes = new AABB[ boxes.size() ];
		for ( int i = 0; i < boxes.size(); i++ ) {
			AxisAlignedBB box = boxes.get( i );
			boundingBoxes[ i ] = new AABB( box.maxX, box.maxY, box.maxZ, box.minX, box.minY, box.minZ );
		}
		return boundingBoxes;
	}
	
	@Override
	public NBTCompound getPlayerCompound( Player player ) {
		EntityPlayer human = ( ( CraftPlayer ) player ).getHandle();
		return new NBTCompoundNMS( human.save( new NBTTagCompound() ) );
	}
	
	@Override
	public void setPlayerCompound( Player player, NBTCompound compound ) {
		EntityPlayer human = ( ( CraftPlayer ) player ).getHandle();
		if ( compound instanceof NBTCompoundNMS ) {
			human.f( ( ( NBTCompoundNMS ) compound ).compound );
		} else {
			throw new IllegalArgumentException( compound + " is not of the right NMS version!" );
		}
		
	}
	
	// @Override
	// public void sendMessage( Player player, String message, Display display ) {
	// PacketPlayOutChat packet = new PacketPlayOutChat( new ChatComponentText(
	// message ), ChatMessageType.a( display.location ) );
	// ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
	//
	// }

	private CollisionResultBlock getResultFrom( net.minecraft.server.v1_14_R1.World world, MovingObjectPositionBlock position ) {
		Block block = CraftBlock.at( world, position.getBlockPosition() );
		BlockFace face = CraftBlock.notchToBlockFace( position.getDirection() );
		Vec3D fin = position.getPos();
		Location interception = new Location( world.getWorld(), fin.getX(), fin.getY(), fin.getZ() );
		return new CollisionResultBlock( interception, face, block );
	}
	
	protected void broadcastPacket( World world, Packet< ? > packet ) {
		for ( Player player : world.getPlayers() ) {
			GunsmokeUtil.getPlugin().getProtocol().sendPacket( player, packet );
		}
	}
	
	protected void broadcastPacket( org.bukkit.entity.Entity origin, Packet< ? > packet, boolean updateSelf ) {
		CustomEntityTracker tracker = entityTracker.getEntityTrackerFor( origin );
		if ( updateSelf ) {
			tracker.broadcastIncludingSelf( packet );
		} else {
			tracker.broadcast( packet );
		}
	}
}