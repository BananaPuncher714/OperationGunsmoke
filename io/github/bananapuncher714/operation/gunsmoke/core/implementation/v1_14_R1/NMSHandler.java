package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayerHand;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import net.minecraft.server.v1_14_R1.AttributeInstance;
import net.minecraft.server.v1_14_R1.AxisAlignedBB;
import net.minecraft.server.v1_14_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcher.Item;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityPose;
import net.minecraft.server.v1_14_R1.EntitySize;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.LightEngine;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.MovingObjectPositionBlock;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayInAdvancements;
import net.minecraft.server.v1_14_R1.PacketPlayInAdvancements.Status;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockPlace;
import net.minecraft.server.v1_14_R1.PacketPlayInFlying;
import net.minecraft.server.v1_14_R1.PacketPlayInTeleportAccept;
import net.minecraft.server.v1_14_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_14_R1.PacketPlayOutLightUpdate;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition.EnumPlayerTeleportFlags;
import net.minecraft.server.v1_14_R1.PacketPlayOutUpdateAttributes;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.RayTrace;
import net.minecraft.server.v1_14_R1.Vec3D;

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
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}

	private Gunsmoke plugin;
	
	public void setGunsmoke( Gunsmoke plugin ) {
		this.plugin = plugin;
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
			return handleFlyingPacket( reciever, (PacketPlayInFlying ) packet );
		} else if ( packet instanceof PacketPlayInBlockPlace ) {
			return handleBlockPlacePacket( reciever, ( PacketPlayInBlockPlace ) packet );
		} else if ( packet instanceof PacketPlayInBlockDig ) {
			return handleBlockDigPacket( reciever, ( PacketPlayInBlockDig ) packet );
		} else if ( packet instanceof PacketPlayInAdvancements ) {
			return handleAdvancementPacket( reciever, ( PacketPlayInAdvancements ) packet );
		} else if ( packet instanceof PacketPlayInTeleportAccept ) {
			return handleTeleportAcceptPacket( reciever, ( PacketPlayInTeleportAccept ) packet );
		}
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
				EntityUpdateItemEvent event = new EntityUpdateItemEvent( player, nmsItem == null ? null : CraftItemStack.asBukkitCopy( nmsItem ), NMSUtils.getEquipmentSlot( slot ) );
				
				plugin.getTaskManager().callEventSync( event );
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
		org.bukkit.inventory.ItemStack item;
		if ( equipment == EquipmentSlot.HAND ) {
			GunsmokePlayerHand hand = gEntity.getMainHand();
			item = hand.getHolding();
		} else if ( equipment == EquipmentSlot.OFF_HAND ) {
			GunsmokePlayerHand hand = gEntity.getOffHand();
			item = hand.getHolding();
		} else {
			item = gEntity.getWearing( equipment );
		}

		if ( item == null ) {
			return packet;
		}

		try {
			ENTITYEQUIPMENT_ITEMSTACK.set(packet, CraftItemStack.asNMSCopy(item));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return packet;
	}

	private boolean handleAbilitiesPacket( Player player, PacketPlayOutAbilities packet ) {
		// TODO
		try {
			 float fov = ( float ) PLAYERABILITIES_FOV.getFloat( packet );
			 if ( plugin.getZoomManager().getZoomLevel( player ) == null ) {
				 packet.b( .1f );
			 } else {
				 packet.b( fov );
			 }
			 return true;
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Packet< ? > handleFlyingPacket(Player player, PacketPlayInFlying packet) {
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
		org.bukkit.inventory.ItemStack item = gEntity.getWearing( slot );

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
	public void set( Player player, boolean down ) {
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
	public Location rayTrace( Location location, Vector vector, double distance ) {
		return rayTrace( location, vector.clone().normalize().multiply( distance ) );
	}
	
	@Override
	public Location rayTrace( Location location, Vector vector ) {
		net.minecraft.server.v1_14_R1.World world = ( ( CraftWorld ) location.getWorld() ).getHandle();
		
		Location dest = location.clone().add( vector );
		
		Vec3D start = new Vec3D( location.getX(), location.getY(), location.getZ() );
		Vec3D end = new Vec3D( dest.getX(), dest.getY(), dest.getZ() );
		
		// Not sure what the difference between COLLIDER and OUTLINE is. Perhaps a intersection detection method?
		RayTrace trace = new RayTrace( start, end, RayTrace.BlockCollisionOption.OUTLINE, RayTrace.FluidCollisionOption.NONE, null );
		
		MovingObjectPositionBlock result = world.rayTrace( trace );

		Vec3D fin = result.getPos();
		
		Location interception = new Location( location.getWorld(), fin.getX(), fin.getY(), fin.getZ() );
		
		return interception;
	}
	
	@Override
	public List< org.bukkit.entity.Entity > getNearbyEntities( org.bukkit.entity.Entity entity, Location location, Vector vector ) {
		net.minecraft.server.v1_14_R1.World world = ( ( CraftWorld ) location.getWorld() ).getHandle();
	
		AxisAlignedBB axis = new AxisAlignedBB( location.getX(), location.getY(), location.getZ(),
				location.getX() + vector.getX(), location.getY() + vector.getY(), location.getZ() + vector.getZ() );
		
		Entity nmsEntity = entity == null ? null : ( ( CraftEntity ) entity ).getHandle();
		
		List< org.bukkit.entity.Entity > nearby = new ArrayList< org.bukkit.entity.Entity >();
		for ( Entity nearbyEntity : world.getEntities( nmsEntity, axis ) ) {
			nearby.add( nearbyEntity.getBukkitEntity() );
		}
		return nearby;
	}
	
	// @Override
	// public void sendMessage( Player player, String message, Display display ) {
	// PacketPlayOutChat packet = new PacketPlayOutChat( new ChatComponentText(
	// message ), ChatMessageType.a( display.location ) );
	// ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
	//
	// }
	//
	// /**
	// * Teleport a player relative to their current location; Negative pitch
	// indicates a movement upwards.
	// */
	// @Override
	// public void teleportRelative( String player, Vector location, double yaw,
	// double pitch ) {
	// Set< EnumPlayerTeleportFlags > set = new HashSet< EnumPlayerTeleportFlags
	// >();
	// for ( EnumPlayerTeleportFlags flag : EnumPlayerTeleportFlags.values() ) {
	// set.add( flag );
	// }
	// int id = 42;
	// if ( location == null ) {
	// location = new Vector( 0, 0, 0 );
	// }
	// PacketPlayOutPosition packet = new PacketPlayOutPosition( location.getX(),
	// location.getY(), location.getZ(), ( float ) yaw, ( float ) pitch, set, id );
	// OrdnanceUtil.sendPacket( player, packet );
	// }
	//
	// @Override
	// public void playParticle( Player player, boolean everyoneElse, Particle
	// particle, boolean farView, Location location, float dx, float dy, float dz,
	// float speed, int count, int... params ) {
	// EnumParticle eParticle = FailSafe.getEnum( EnumParticle.class,
	// particle.name().toUpperCase() );
	// PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
	// eParticle, farView, ( float ) location.getX(), ( float ) location.getY(), (
	// float ) location.getZ(), dx, dy, dz, speed, count, params );
	// if ( player == null || everyoneElse ) {
	// for ( Player rel : location.getWorld().getPlayers() ) {
	// if ( rel != player || !everyoneElse ) {
	// ( ( CraftPlayer ) rel ).getHandle().playerConnection.sendPacket( packet );
	// }
	// }
	// } else {
	// ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
	// }
	// }
	//
	// @Override
	// public void playBlockCrack( Location location, int level ) {
	// PacketPlayOutBlockBreakAnimation packet = new
	// PacketPlayOutBlockBreakAnimation( location.hashCode(), new BlockPosition(
	// location.getBlockX(), location.getBlockY(), location.getBlockZ() ), level );
	// for ( Player player : location.getWorld().getPlayers() ) {
	// ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
	// }
	// }
	//
	// @Override
	// public void setFOV( Player player, float value ) {
	// EntityPlayer entityPlayer = ( ( CraftPlayer ) player ).getHandle();
	//
	// PacketPlayOutAbilities packet = new PacketPlayOutAbilities(
	// entityPlayer.abilities );
	//
	// packet.b( value );
	//
	// entityPlayer.playerConnection.sendPacket( packet );
	// }

	private void broadcastPacket(org.bukkit.entity.Entity origin, Packet<?> packet, boolean updateSelf) {
		World world = origin.getWorld();
		// TODO Get an entity tracker entry sometime or something similar
		for ( Player player : world.getPlayers() ) {
			if ( updateSelf || origin != player ) {
				plugin.getProtocol().sendPacket( player, packet );
			}
		}
	}
}