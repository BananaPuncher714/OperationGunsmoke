package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.tracking.GunsmokeEntityTracker;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class MinigameManager {
	protected Gunsmoke plugin;
	protected File baseDir;
	protected MinigameListener listener;
	
	protected Map< String, Minigame > minigameCache = new HashMap< String, Minigame >();
	protected Map< String, MinigameFactory > factories = new HashMap< String, MinigameFactory >();
	
	protected Map< UUID, String > participants = new HashMap< UUID, String >();
	protected Map< UUID, PlayerSaveData > saveData = new HashMap< UUID, PlayerSaveData >();
	
	protected MinigameCommand minigameCommand;
	protected ArenaCommand arenaCommand;
	
	protected MinigameLoader loader;
	
	public MinigameManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		baseDir = new File( plugin.getFileManager().getBaseFile() + "/minigame/" );
		
		listener = new MinigameListener( this );
		Bukkit.getPluginManager().registerEvents( listener, plugin );
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
		
		for ( World world : Bukkit.getWorlds() ) {
			for ( Entity entity : world.getEntities() ) {
				loadTracker( plugin.getItemManager().getEntityWrapper( entity ) );
			}
		}
		
		// Register the commands for /minigame and /arena
		minigameCommand = new MinigameCommand( this );
		arenaCommand = new ArenaCommand( this );
		
		plugin.getCommand( "minigame" ).setExecutor( minigameCommand );
		plugin.getCommand( "arena" ).setExecutor( arenaCommand );
		
		loader = new MinigameLoader( this, new File( baseDir + "/" + "saves" ) );
	}
	
	public void enableManager() {
		loader.loadMinigames();
	}
	
	public void disableManager() {
		// Make all players force quit
		for ( UUID uuid : participants.keySet() ) {
			GunsmokeRepresentable representable = plugin.getItemManager().get( uuid );
			if ( representable instanceof GunsmokeEntity ) {
				leave( ( GunsmokeEntity ) representable );
			}
		}
		
		// Stop any currently active minigames
		for ( Minigame minigame : minigameCache.values() ) {
			minigame.finalStop();
		}
		
		// Save them all
		loader.saveMinigames();
	}
	
	private void update() {
		for ( Minigame minigame : minigameCache.values() ) {
			// TODO make the minigame tick return value mean something
			minigame.tick();
		}
	}
	
	public void registerMinigameFactory( String id, MinigameFactory factory ) {
		System.out.println( "Got factory for " + id );
		factories.put( id, factory );
	}
	
	public MinigameFactory getFactory( String id ) {
		return factories.get( id );
	}
	
	protected Map< String, MinigameFactory > getFactories() {
		return factories;
	}
	
	public void addMinigame( String id, Minigame minigame ) {
		Minigame old = minigameCache.put( id, minigame );
		if ( old != null ) {
			old.stop();
		}
		minigameCache.put( id, minigame );
		minigame.finalStart( new File( baseDir + "/" + id ) );
	}
	
	public Minigame removeMinigame( String minigame ) {
		Minigame game = minigameCache.remove( minigame );
		if ( game != null ) {
			game.finalStop();
			loader.delete( minigame );
		}
		return game;
	}
	
	public void start( String minigame ) {
		Minigame game = minigameCache.get( minigame );
		if ( game != null ) {
			game.finalStart( new File( baseDir + "/" + minigame ) );
		}
	}
	
	
	public void stop( String minigame ) {
		Minigame game = minigameCache.get( minigame );
		if ( game != null ) {
			game.finalStop();
		}
	}
	
	public Minigame getGame( String id ) {
		return minigameCache.get( id );
	}
	
	public Map< String, Minigame > getMinigames() {
		return minigameCache;
	}
	
	public Minigame participating( GunsmokeEntity entity ) {
		return getGame( participants.get( entity.getUUID() ) );
	}
	
	public Minigame belongsTo( GunsmokeEntity entity ) {
		for ( Minigame game : minigameCache.values() ) {
			if ( game.isParticipating( entity ) || game.isRegistered( entity ) ) {
				return game;
			}
		}
		return null;
	}
	
	public void join( String minigame, GunsmokeEntity entity ) {
		leave( entity );
		Minigame game = getGame( minigame );
		if ( game != null ) {
			if ( entity instanceof GunsmokeEntityWrapperPlayer ) {
				Player player = ( ( GunsmokeEntityWrapperPlayer ) entity ).getEntity();
				PlayerSaveData data = new PlayerSaveData( player );
				saveData.put( player.getUniqueId(), data );
			}
			
			game.join( entity );
			participants.put( entity.getUUID(), minigame );
		}
	}
	
	public void leave( GunsmokeEntity entity ) {
		String prev = participants.remove( entity.getUUID() );
		if ( prev != null ) {
			getGame( prev ).leave( entity );
			Player player = ( ( GunsmokeEntityWrapperPlayer ) entity ).getEntity();
			PlayerSaveData data = saveData.remove( player.getUniqueId() );
			if ( data != null ) {
				data.apply( player );
			}
		}
	}
	
	protected void quit( Player player ) {
		leave( plugin.getItemManager().getEntityWrapper( player ) );
	}
	
	protected void loadTracker( GunsmokeEntity gEntity ) {
		if ( gEntity instanceof GunsmokeEntityWrapper ) {
			Entity entity = ( ( GunsmokeEntityWrapper ) gEntity ).getEntity();
			
			GunsmokeEntityTracker tracker = GunsmokeUtil.getPlugin().getProtocol().getHandler().getEntityTrackerFor( entity );
			
			tracker.setVisibilityController( new MinigameVisibilityController( this ) );
		}
	}
	
	public Gunsmoke getPlugin() {
		return plugin;
	}
}
