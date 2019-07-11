package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.List;
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
import org.bukkit.util.BlockIterator;

import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeStructure;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosion;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosionResult;
import io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1.NMSUtils;
import io.github.bananapuncher714.operation.gunsmoke.core.listeners.PlayerListener;
import io.github.bananapuncher714.operation.gunsmoke.core.util.NBTEditor;
import io.github.bananapuncher714.operation.gunsmoke.core.util.ReflectionUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.EventListener;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
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
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			protocol.getPlayerConnection( player.getName() );
		}
		
		new GunsmokeImplementation( this );
	}
	
	@Override
	public void onDisable() {
		movementManager.stop();
		NGui.disable();
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
