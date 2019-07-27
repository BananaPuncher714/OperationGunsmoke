package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1.NMSUtils;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.ReflectionUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.ngui.NGui;
import io.github.bananapuncher714.operation.gunsmoke.tinyprotocol.TinyProtocolGunsmoke;

public class Gunsmoke extends JavaPlugin {
	protected TinyProtocolGunsmoke protocol;
	protected ItemManager itemManager;
	protected EntityManager entityManager;
	protected EntityTracker entityTracker;
	protected BlockManager blockManager;
	protected PlayerManager playerManager;
	protected TaskManager taskManager;
	protected MovementManager movementManager;
	protected ZoomManager zoomManager;
	protected NPCManager npcManager;
	
	@Override
	public void onEnable() {
		PacketHandler handler = ReflectionUtil.getNewPacketHandlerInstance();
		protocol = new TinyProtocolGunsmoke( this, handler );
		
		itemManager = new ItemManager( this );
		entityManager = new EntityManager( this );
		entityTracker = new EntityTracker( this );
		blockManager = new BlockManager( this );
		playerManager = new PlayerManager( this );
		taskManager = new TaskManager( this );
		movementManager = new MovementManager( this );
		zoomManager = new ZoomManager( this );
		npcManager = new NPCManager( this );
		
		NGui.init( this );
		
		Bukkit.getScheduler().runTaskTimer( this, this::run, 0, 1 );
		Bukkit.getPluginManager().registerEvents( new PlayerListener( this ), this );
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			protocol.getPlayerConnection( player.getName() );
		}
		
		new GunsmokeImplementation( this );
	}
	
	@Override
	public void onDisable() {
		movementManager.stop();
		NGui.disable();
		
		npcManager.disable();
	}
	
	/**
	 * Temporary too
	 */
	private void run() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
//			GunsmokePlayer entity = entityManager.getEntity( player.getUniqueId() );
//			entity.update();

			protocol.getHandler().setTint( player, 1 - player.getHealth() / player.getHealthScale() );
			
			NMSUtils.setNoFly( player );
			
//			protocol.getHandler().display( player );
			
			AABB[] boxes = protocol.getHandler().getBoxesFor( player.getLocation() );
			if ( VectorUtil.intersects( new Vector( 0, 0, 0 ), new AABB( player.getBoundingBox().expand(-9.999999747378752E-6D) ), BukkitUtil.getBlockLocation( player.getLocation() ).toVector(), boxes ) ) {
				player.sendMessage( "IN BLOCK" );
			}
			
			for ( Entity entity : player.getWorld().getNearbyEntities( player.getLocation(), 20, 20, 20 ) ) {
				Location location = entityTracker.getLocationOf( entity.getUniqueId(), 6 );
				if ( location != null ) {
					entity.getWorld().spawnParticle( Particle.WATER_BUBBLE, location.clone().add( 0, entity.getHeight() + .5, 0 ), 0 );
				}
			}
			
			for ( GunsmokeNPC npc : GunsmokeUtil.getPlugin().getNPCManager().getNPCs() ) {
				if ( npc.getPlayer() == player ) {
					continue;
				}
				npc.look( player.getEyeLocation() );
			}
			
//			player.setRemainingAir( 285 );
//	 		protocol.getHandler().setAir( player, 285 );
//			entity.update();
		}
	}
	
	public TinyProtocolGunsmoke getProtocol() {
		return protocol;
	}
	
	public ItemManager getItemManager() {
		return itemManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public EntityTracker getEntityTracker() {
		return entityTracker;
	}
	
	public BlockManager getBlockManager() {
		return blockManager;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}
	
	public MovementManager getMovementManager() {
		return movementManager;
	}
	
	public ZoomManager getZoomManager() {
		return zoomManager;
	}
	
	public NPCManager getNPCManager() {
		return npcManager;
	}
}
