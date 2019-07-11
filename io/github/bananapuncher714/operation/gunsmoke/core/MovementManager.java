package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.RelativeFacing;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class MovementManager {
	private static final int DELAY = 17;
	
	private volatile boolean RUNNING = true;
	
	private Gunsmoke plugin;
	
	private Map< String, CrosshairMovement > movement = new ConcurrentHashMap< String, CrosshairMovement >();
	
	public MovementManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		Bukkit.getScheduler().runTaskAsynchronously( plugin, this::update );
	}
	
	private void update() {
		long lastUpdated = System.currentTimeMillis();
		while ( RUNNING ) {
			try {
				long delayTime = DELAY - ( System.currentTimeMillis() - lastUpdated );
				if ( delayTime > 0 ) {
					Thread.sleep( delayTime );
				}
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
			
			long time = System.currentTimeMillis();
			lastUpdated = time;
			
			for ( String player : movement.keySet() ) {
				CrosshairMovement move = movement.get( player );
				
				RelativeFacing facing = move.getMovement( time );
				
				if ( facing.pitch != 0 || facing.pitch != 0 ) {
					GunsmokeUtil.teleportRelative( player, null, facing.yaw, -facing.pitch );
				}
			}
		}
	}
	
	public void setMovement( String player, CrosshairMovement value ) {
		if ( value == null ) {
			movement.remove( player );
		} else {
			movement.put( player, value );
		}
		
	}
	
	public CrosshairMovement getMovement( String player ) {
		return movement.get( player );
	}
	
	public boolean isMoving( String player ) {
		return movement.containsKey( player );
	}
	
	protected void stop() {
		RUNNING = false;
	}
}
