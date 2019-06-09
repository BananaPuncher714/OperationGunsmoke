package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1.NMSUtils;
import io.github.bananapuncher714.operation.gunsmoke.core.listeners.PlayerListener;
import io.github.bananapuncher714.operation.gunsmoke.core.util.ReflectionUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;
import io.github.bananapuncher714.operation.gunsmoke.test.ProneListener;
import io.github.bananapuncher714.operation.gunsmoke.tinyprotocol.TinyProtocolGunsmoke;

public class Gunsmoke extends JavaPlugin {
	protected TinyProtocolGunsmoke protocol;
	protected ItemManager itemManager;
	protected EntityManager entityManager;
	protected PlayerManager playerManager;
	protected TaskManager taskManager;
	
	@Override
	public void onEnable() {
		PacketHandler handler = ReflectionUtil.getNewPacketHandlerInstance();
		protocol = new TinyProtocolGunsmoke( this, handler );
		
		itemManager = new ItemManager( this );
		entityManager = new EntityManager();
		playerManager = new PlayerManager( this );
		taskManager = new TaskManager( this );
		
		Bukkit.getScheduler().runTaskTimer( this, this::run, 0, 1 );
		Bukkit.getPluginManager().registerEvents( new ProneListener( this ), this );
		Bukkit.getPluginManager().registerEvents( new PlayerListener( this ), this );
	}
	
	/**
	 * Temporary
	 */
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			
			player.sendMessage( "Finding..." );
			for ( Entity nearEntity : player.getWorld().getEntities() ) {
				if ( nearEntity != player ) {
					Location hit = VectorUtil.rayIntersect( nearEntity, player.getEyeLocation(), player.getLocation().getDirection() );
					if ( hit != null ) {
						player.sendMessage( "Found: " + nearEntity.getName() + " at " + nearEntity.getLocation() );
						protocol.getHandler().hurt( player );
					}
				}
			}
			
			Location location = protocol.getHandler().rayTrace( player.getEyeLocation(), player.getLocation().getDirection(), 100 );
			location.getWorld().spawnParticle( Particle.VILLAGER_HAPPY, location, 0 );
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
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}
}
