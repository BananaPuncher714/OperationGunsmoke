package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosion;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosionResult;
import io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1.NMSUtils;
import io.github.bananapuncher714.operation.gunsmoke.core.listeners.PlayerListener;
import io.github.bananapuncher714.operation.gunsmoke.core.util.NBTEditor;
import io.github.bananapuncher714.operation.gunsmoke.core.util.ReflectionUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.EventListener;
import io.github.bananapuncher714.operation.gunsmoke.ngui.NGui;
import io.github.bananapuncher714.operation.gunsmoke.test.ProneListener;
import io.github.bananapuncher714.operation.gunsmoke.tinyprotocol.TinyProtocolGunsmoke;

public class Gunsmoke extends JavaPlugin {
	protected TinyProtocolGunsmoke protocol;
	protected ItemManager itemManager;
	protected EntityManager entityManager;
	protected BlockManager blockManager;
	protected PlayerManager playerManager;
	protected TaskManager taskManager;
	protected MovementManager movementManager;
	protected ZoomManager zoomManager;
	
	@Override
	public void onEnable() {
		PacketHandler handler = ReflectionUtil.getNewPacketHandlerInstance();
		protocol = new TinyProtocolGunsmoke( this, handler );
		
		itemManager = new ItemManager( this );
		entityManager = new EntityManager( this );
		blockManager = new BlockManager( this );
		playerManager = new PlayerManager( this );
		taskManager = new TaskManager( this );
		movementManager = new MovementManager( this );
		zoomManager = new ZoomManager( this );
		
		NGui.init( this );
		
		Bukkit.getScheduler().runTaskTimer( this, this::run, 0, 1 );
		Bukkit.getPluginManager().registerEvents( new PlayerListener( this ), this );
		
		
		Bukkit.getPluginManager().registerEvents( new ProneListener( this ), this );
		Bukkit.getPluginManager().registerEvents( new EventListener(), this );
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			protocol.getPlayerConnection( player.getName() );
		}
	}
	
	@Override
	public void onDisable() {
		movementManager.stop();
		NGui.disable();
	}
	
	/**
	 * Temporary
	 */
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			
			player.sendMessage( "Finding..." );
			CollisionResultBlock location = protocol.getHandler().rayTrace( player.getEyeLocation(), player.getLocation().getDirection(), 100 );

			player.sendMessage( "Type: " + location.getBlock().getType() );
			player.sendMessage( "Collision: " + location.getCollisionType() );
			player.sendMessage( "Direction: " + location.getDirection() );
			
			GunsmokeExplosion explosion = new GunsmokeExplosion( null, location.getLocation(), 8, 10 );
			long time = System.currentTimeMillis();
			GunsmokeExplosionResult result = explosion.explode();
			Map< Location, Double > power = result.getBlockDamage();
			Map< Entity, Double > entityDamage = result.getEntityDamage();
			
			player.sendMessage( "Took " + ( System.currentTimeMillis() - time ) + "ms" );
			player.sendMessage( "Detected " + power.size() + " blocks in blast radius!" );
			player.sendMessage( "Detected " + entityDamage.size() + " entities in blast radius!" );
			int a = 0;
			for ( Location explodeLoc : power.keySet() ) {
				if ( explodeLoc.getBlock().getType() != Material.AIR ) {
					a++;
					int stage = ( int ) Math.min( 9, power.get( explodeLoc ) );
					if ( power.get( explodeLoc ) != 0 ) {
						protocol.getHandler().damageBlock( explodeLoc, stage );
					}
				}
			}
			player.sendMessage( a + " blocks were damaged!" );
			for ( Entity entity : entityDamage.keySet() ) {
				player.sendMessage( entity.getName() + " took " + result.getEntityDamageFor( entity ) + " power" );
			}
			
			Block targetBlock = player.getTargetBlock( ( Set< Material > ) null, 100 );
			if ( targetBlock.getType() == Material.STRUCTURE_BLOCK ) {
				System.out.println( "MODIFIED" );
				NBTEditor.set( targetBlock, 130, "posY" );
			}
			
//			VectorUtil.fastCanSeeTwo( player.getEyeLocation(), location );
			
			location.getLocation().getWorld().spawnParticle( Particle.VILLAGER_HAPPY, location.getLocation(), 0 );
		}
		
		return false;
	}
	
	/**
	 * Temporary too
	 */
	private void run() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			GunsmokePlayer entity = entityManager.getEntity( player.getUniqueId() );
//			entity.update();

			protocol.getHandler().setTint( player, 1 - player.getHealth() / player.getHealthScale() );
			
			NMSUtils.setNoFly( player );
//			player.setRemainingAir( 285 );
//			protocol.getHandler().setAir( player, 285 );
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
}
