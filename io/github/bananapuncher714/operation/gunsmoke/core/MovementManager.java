package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.Bukkit;

public class MovementManager {
	private static final int DELAY = 17;
	
	private volatile boolean RUNNING = true;
	
	private Gunsmoke plugin;
	
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
			
			// Start of actual things
			// TODO figure out a logical way to organize recoil, particularly if the player does the pulling down themselves
			// Don't want to mess up the player's sense of direction after all...
			// Perhaps only activate the recoil recovery sometimes?
			// Or make it optional?
			
		}
	}
	
	protected void stop() {
		RUNNING = false;
	}
}
