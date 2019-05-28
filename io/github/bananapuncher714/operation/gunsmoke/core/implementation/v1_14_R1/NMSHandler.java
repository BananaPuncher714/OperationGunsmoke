package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntityHand;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import net.minecraft.server.v1_14_R1.DataWatcherSerializer;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityPose;
import net.minecraft.server.v1_14_R1.EntitySize;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayInArmAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockPlace;
import net.minecraft.server.v1_14_R1.PacketPlayInFlying;
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

	private static Map< EntityPose, EntitySize > sizes = new HashMap< EntityPose, EntitySize >();
	
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

			SPAWNENTITY_TYPE = PacketPlayOutSpawnEntity.class.getDeclaredField("k");
			SPAWNENTITY_TYPE.setAccessible(true);

			PLAYERABILITIES_FOV = PacketPlayOutAbilities.class.getDeclaredField("f");
			PLAYERABILITIES_FOV.setAccessible(true);
			
			Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
			
			Field universal = EntityHuman.class.getDeclaredField( "b" );
			universal.setAccessible( true );
			universal.getModifiers();
			
            modifiersField.setInt( universal, universal.getModifiers() & ~Modifier.FINAL );
			
            Map< EntityPose, EntitySize > poses = ( Map< EntityPose, EntitySize > ) universal.get( null );
            
            for ( EntityPose pose : poses.keySet() ) {
            	sizes.put( pose, poses.get( pose ) );
            }
            
            universal.set( null, sizes );
            
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private Gunsmoke plugin;

	public void setGunsmoke(Gunsmoke plugin) {
		this.plugin = plugin;
	}

	private Set<Integer> equipmentPackets = new HashSet<Integer>();

	/**
	 * Intercept outgoing packets; Edit them if they are the EntityMetadata packet
	 * or the EntityEquipment packet
	 */
	@Override
	public Object onPacketInterceptOut(Player reciever, Object packet) {
		if (packet instanceof PacketPlayOutEntityMetadata) {
			return handleMetadataPacket(reciever, (PacketPlayOutEntityMetadata) packet);
		} else if (packet instanceof PacketPlayOutEntityEquipment) {
			return handleEntityEquipmentPacket(reciever, (PacketPlayOutEntityEquipment) packet);
		}
		return packet;
	}

	/**
	 * Catch all incoming packets; The TeleportAccept packet for relative
	 * teleportations, and the BlockPlace and BlockDig for holding down right click
	 */
	@Override
	public Object onPacketInterceptIn(Player reciever, Object packet) {
		if (packet instanceof PacketPlayInFlying) {
			return handleFlyingPacket(reciever, (PacketPlayInFlying) packet);
		}
		return packet;
	}

	/**
	 * Edit the entity metadata packet so that the player's arms are in the right
	 * "state"
	 */
	private Packet handleMetadataPacket(Player player, PacketPlayOutEntityMetadata packet) {
		List< Item< ? > > items;
		int id;
		try {
			items = ( List<Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			if ( items == null ) {
				return packet;
			}
			id = ( Integer ) ENTITYMETADATA_ID.get( packet );
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return packet;
		}

		World world = player.getWorld();
		org.bukkit.entity.Entity entity = NMSUtils.getEntityFromId( world, id );

		if ( !( entity instanceof LivingEntity ) ) {
			return packet;
		}

		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		
		if ( entity == player ) {
			return handleInternalMetadataPacket( player, items, gEntity ) ? packet : null;
		} else {
			return handleExternalMetadataPacket( player, ( LivingEntity ) entity, items, gEntity ) ? packet : null;
		}
	}
	
	private boolean handleInternalMetadataPacket( Player reciever, List< Item< ? > > items, GunsmokeEntity entity ) {
		if ( entity.isProne() ) {
			for ( int index = 0; index < items.size(); index++ ) {
				if ( items.get( index ).a().a() == 6 ) {
					items.remove( index );
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
		if ( handStateMask > -1 && handStateMask % 2 == 0 ) {
			byte bitmask = ( byte ) ( ( handStateMask & 0b011 ) | ( entity.isProne() ? 0b100 : 0b000 ) );
			items.remove( pos );
			items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), bitmask ) );

		}
		return items.size() > 0;
	}
	
	private boolean handleExternalMetadataPacket( Player reciever, LivingEntity livingEntity, List< Item< ? > > items, GunsmokeEntity entity ) {
		// First confirm the player's hand
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
			boolean main = true;
			if ( ( handStateMask & 0b010 ) == 0 ) {
				if ( entity.getMainHand().getState() == State.DEFAULT ) {
					bitmask = 0b000;
				} else {
					bitmask = 0b001;
				}
			} else {
				main = false;
				if ( entity.getOffHand().getState() == State.DEFAULT ) {
					bitmask = 0b010;
				} else {
					bitmask = 0b011;
				}
			}
			if ( GunsmokeUtil.canUpdate( entity, main ) ) {
				bitmask = ( byte ) ( handStateMask & 0b011 );
			}
			items.remove( pos );
			items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), bitmask ) );
		}
		
		// We want to remove index 6 if the living entity is prone, aka cancel sneak/swim/sprint
		// Then, we want to add number 6
		if ( entity.isProne() ) {
			for ( int index = 0; index < items.size(); index++ ) {
				if ( items.get( index ).a().a() == 6 ) {
					items.remove( index );
					break;
				}
			}
			items.add( new Item< EntityPose >( new DataWatcherObject< EntityPose >( 6, DataWatcherRegistry.s ), EntityPose.SWIMMING ) );
		}
		
		return items.size() > 0;
	}

	/**
	 * Edit entity equipment including hand to show exactly what needs to be
	 * "displayed"
	 */
	private Packet handleEntityEquipmentPacket(Player player, PacketPlayOutEntityEquipment packet) {
		int id;
		EnumItemSlot slot;
		// TODO move this somewhere nicer
		try {
			id = (Integer) ENTITYEQUIPMENT_ID.get(packet);
			slot = (EnumItemSlot) ENTITYEQUIPMENT_SLOT.get(packet);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return packet;
		}

		if (((CraftEntity) player).getEntityId() == id) {
			return packet;
		}

		World world = player.getWorld();
		org.bukkit.entity.Entity entity = NMSUtils.getEntityFromId(world, id);

		if (!(entity instanceof LivingEntity)) {
			return packet;
		}

		EquipmentSlot equipment = NMSUtils.getEquipmentSlot(slot);
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity(entity.getUniqueId());

		// Now either disguise or something
		// Temporary
		org.bukkit.inventory.ItemStack item;
		if (equipment == EquipmentSlot.HAND) {
			GunsmokeEntityHand hand = gEntity.getMainHand();
			item = hand.getHolding();
		} else if (equipment == EquipmentSlot.OFF_HAND) {
			GunsmokeEntityHand hand = gEntity.getOffHand();
			item = hand.getHolding();
		} else {
			item = gEntity.getWearing(equipment);
		}

		if (item == null) {
			return packet;
		}

		try {
			ENTITYEQUIPMENT_ITEMSTACK.set(packet, CraftItemStack.asNMSCopy(item));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return packet;
	}

	private Packet handleFlyingPacket(Player player, PacketPlayInFlying packet) {
		if (player.isOnGround() && !packet.b()
				&& packet.b(player.getLocation().getY()) - player.getLocation().getY() > 0) {
			PlayerJumpEvent event = new PlayerJumpEvent(player);
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(event);
				}
			});
		}
		return packet;
	}

	public void update(LivingEntity entity, boolean main) {
		update(entity, main, false);
	}

	/**
	 * Update the hand state according to the Gunsmoke Entity
	 */
	public void update( LivingEntity entity, boolean main, boolean updateSelf ) {
		int id = ( ( CraftEntity ) entity ).getEntityId();
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity( entity.getUniqueId() );
		// When updating, there are several things to take into account
		if ( !GunsmokeUtil.canUpdate( gEntity, main ) ) {
			return;
		}
		DataWatcher watcher = ( ( CraftEntity ) entity ).getHandle().getDataWatcher();
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata( id, watcher, false );

		List< Item< ? > > items = null;
		try {
			items = ( List<Item< ? > > ) ENTITYMETADATA_ITEMLIST.get( packet );
			if ( items == null ) {
				items = new ArrayList< Item< ? > >();
				ENTITYMETADATA_ITEMLIST.set( packet, items );
			}
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		items.clear();
		items.add( new Item< Byte >( new DataWatcherObject< Byte >( HAND_STATE_INDEX, DataWatcherRegistry.a ), ( byte ) ( main ? 0b000 : 0b010 ) ) );
		
		broadcastPacket( entity, packet, updateSelf );
	}

	/**
	 * Update the player's equipment slot
	 */
	public void update(LivingEntity entity, EquipmentSlot slot) {
		EnumItemSlot NMSSlot = NMSUtils.getEnumItemSlot(slot);
		int id = ((CraftEntity) entity).getEntityId();
		GunsmokeEntity gEntity = plugin.getEntityManager().getEntity(entity.getUniqueId());
		org.bukkit.inventory.ItemStack item = gEntity.getWearing(slot);

		if (slot == EquipmentSlot.HAND) {
			item = gEntity.getMainHand().getHolding();
		} else if (slot == EquipmentSlot.OFF_HAND) {
			item = gEntity.getOffHand().getHolding();
		}

		if (item != null) {
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(id, NMSSlot,CraftItemStack.asNMSCopy(item));

			broadcastPacket(entity, packet, false);
		}
	}

	public void set( Player player, boolean down ) {
		try {
			
			Field size = Entity.class.getDeclaredField( "size" );
			size.setAccessible( true );

			EntitySize original = sizes.get( EntityPose.SNEAKING );
			sizes.put( EntityPose.SNEAKING, EntitySize.b( 0.6f, 0.6f ) );

			( ( CraftEntity ) player ).getHandle().updateSize();

			sizes.put( EntityPose.SNEAKING, original );
		} catch (Exception exception) {
			exception.printStackTrace();
		}
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
		for (Player player : world.getPlayers()) {
			if (updateSelf || origin != player) {
				plugin.getProtocol().sendPacket(player, packet);
			}
		}
	}
}