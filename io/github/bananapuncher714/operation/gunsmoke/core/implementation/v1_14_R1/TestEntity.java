package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.NPCAction;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayInEntityAction;
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_14_R1.PlayerInteractManager;
import net.minecraft.server.v1_14_R1.World;
import net.minecraft.server.v1_14_R1.WorldServer;

public class TestEntity extends EntityPlayer implements GunsmokeNPC {
	protected TestEntity( World world, GameProfile profile ) {
		super( MinecraftServer.getServer(), ( WorldServer ) world, profile, new PlayerInteractManager( ( WorldServer ) world ) );
		
		new DummyPlayerConnection( MinecraftServer.getServer(), this );
		MinecraftServer.getServer().getPlayerList().a( playerConnection.networkManager, this );
		
		GunsmokeUtil.getPlugin().getNPCManager().register( this );
	}
	
	protected TestEntity( EntityTypes< ? extends EntityHuman > entitytypes, World world, String name ) {
		super( MinecraftServer.getServer(), ( WorldServer ) world, NMSUtils.convert( new GameProfile( UUID.randomUUID(), name ), name ), new PlayerInteractManager( ( WorldServer ) world ) );
		org.bukkit.World bWorld = this.getBukkitEntity().getWorld();
		
		// Maybe register this class to be tracked with the PlayerChunkMap?
		
		new DummyPlayerConnection( MinecraftServer.getServer(), this );
		MinecraftServer.getServer().getPlayerList().a( playerConnection.networkManager, this );
		
//		PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo( EnumPlayerInfoAction.ADD_PLAYER, this );
		PacketPlayOutNamedEntitySpawn spawnPlayer = new PacketPlayOutNamedEntitySpawn( this );
//		PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo( EnumPlayerInfoAction.REMOVE_PLAYER, this );
//		
//		NMSHandler.broadcastPacket( bWorld, addPlayer );
		// TODO spawn the player in properly somehow?
//		NMSHandler.broadcastPacket( bWorld, spawnPlayer );
//		Bukkit.getScheduler().scheduleSyncDelayedTask( GunsmokeUtil.getPlugin(), new Runnable() {
//			@Override
//			public void run() {
//				NMSHandler.broadcastPacket( bWorld, removePlayer );
//			}
//		}, 5 );
		
		GunsmokeUtil.getPlugin().getNPCManager().register( this );
	}
	
	protected void onPacket( Packet< ? > packet ) {
	}
	
	@Override
	public void tick() {
		super.tick();
		super.playerTick();
	}
	
	@Override
	public void interact( NPCAction action ) {
		if ( action == NPCAction.START_SNEAKING ) {
			playerConnection.a( new PacketPlayInEntityAction() {
				 public EnumPlayerAction c() {
					 return EnumPlayerAction.START_SNEAKING;
				 }
			} );
		} else if ( action == NPCAction.STOP_SNEAKING ) {
			playerConnection.a( new PacketPlayInEntityAction() {
				 public EnumPlayerAction c() {
					 return EnumPlayerAction.STOP_SNEAKING;
				 }
			} );
		} else if ( action == NPCAction.START_SPRINTING ) {
			playerConnection.a( new PacketPlayInEntityAction() {
				 public EnumPlayerAction c() {
					 return EnumPlayerAction.START_SPRINTING;
				 }
			} );
		} else if ( action == NPCAction.STOP_SPRINTING ) {
			playerConnection.a( new PacketPlayInEntityAction() {
				 public EnumPlayerAction c() {
					 return EnumPlayerAction.STOP_SPRINTING;
				 }
			} );
		} else if ( action == NPCAction.START_FALL_FLYING ) {
			playerConnection.a( new PacketPlayInEntityAction() {
				 public EnumPlayerAction c() {
					 return EnumPlayerAction.START_FALL_FLYING;
				 }
			} );
		}
	}
	
	@Override
	public void moveTo( Location location ) {
		double x = locX;
		double y = locY;
		double z = locZ;
		
		double xDiff = location.getX() - locX;
		double yDiff = location.getY() - locY;
		double zDiff = location.getZ() - locZ;
		
		boolean validPos = false;
		for ( int i = 0; i < 6; i++ ) {
			this.setLocation( location.getX(), location.getY() + i * ( 1 / 8.0 ), location.getZ(), yaw, pitch );
			if ( this.getWorldServer().getCubes( this, getBoundingBox().shrink( 9.999999747378752E-6D ) ) ) {
				validPos = true;
				break;
			}
		}
		if ( !validPos ) {
			this.setLocation( x, y, z, yaw, pitch );
			return;
		}
		
//		move( EnumMoveType.PLAYER, new Vec3D( xDiff, yDiff, zDiff ) );

		PlayerMoveEvent event = new PlayerMoveEvent( getBukkitEntity(), new Location( getBukkitEntity().getWorld(), x, y, z, yaw, pitch ), location.clone() );
		Bukkit.getPluginManager().callEvent( event );
		
		if ( event.isCancelled() ) {
			playerConnection.teleport( event.getFrom() );
			return;
		}
		if ( !event.getTo().equals( location ) ) {
			getBukkitEntity().teleport( event.getTo(), TeleportCause.PLUGIN );
			return;
		}
		
		checkMovement( xDiff, yDiff, zDiff );
		getWorldServer().getChunkProvider().movePlayer( this );
		a( locY - y, onGround );
	}
	
	@Override
	public void jump() {
		super.jump();
	}
	
	@Override
	public void look( float yaw, float pitch ) {
		this.setYawPitch( yaw, pitch );
	}
	
	@Override
	public void look( Location location ) {
		Vector vector = location.clone().subtract( getBukkitEntity().getEyeLocation() ).toVector();
		location = location.clone();
		location.setDirection( vector );
		
		this.setYawPitch( location.getYaw(), location.getPitch() );
	}
	
	public void spawnFor( Player player ) {
		EntityPlayer entityPlayer = ( ( CraftPlayer ) player ).getHandle();
		if ( entityPlayer instanceof TestEntity ) {
			return;
		} else {
			PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo( EnumPlayerInfoAction.ADD_PLAYER, this );
			PacketPlayOutNamedEntitySpawn spawnPlayer = new PacketPlayOutNamedEntitySpawn( this );
			PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo( EnumPlayerInfoAction.REMOVE_PLAYER, this );
			
			entityPlayer.playerConnection.sendPacket( addPlayer );
			entityPlayer.playerConnection.sendPacket( spawnPlayer );
			entityPlayer.playerConnection.sendPacket( removePlayer );
		}
	}
	
	@Override
	public void remove() {
		MinecraftServer.getServer().getPlayerList().disconnect( this );
	}
	
	@Override
	public Player getPlayer() {
		return getBukkitEntity();
	}
}
