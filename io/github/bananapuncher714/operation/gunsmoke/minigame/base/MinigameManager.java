package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.tracking.GunsmokeEntityTracker;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class MinigameManager {
	protected Gunsmoke plugin;
	protected MinigameListener listener;
	
	protected Map< String, Minigame > minigameCache = new HashMap< String, Minigame >();
	
	protected Map< UUID, String > participants = new HashMap< UUID, String >();
	protected Map< UUID, PlayerSaveData > saveData = new HashMap< UUID, PlayerSaveData >();
	
	public MinigameManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		listener = new MinigameListener( this );
		Bukkit.getPluginManager().registerEvents( listener, plugin );
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
		
		for ( World world : Bukkit.getWorlds() ) {
			for ( Entity entity : world.getEntities() ) {
				loadTracker( plugin.getItemManager().getEntityWrapper( entity ) );
			}
		}
	}
	
	private void update() {
		for ( Minigame minigame : minigameCache.values() ) {
			// TODO make the minigame tick return value mean something
			minigame.tick();
		}
	}
	
	public void addMinigame( String id, Minigame minigame ) {
		Minigame old = minigameCache.put( id, minigame );
		if ( old != null ) {
			old.stop();
		}
		minigameCache.put( id, minigame );
		minigame.finalStart();
	}
	
	public Minigame removeMinigame( String minigame ) {
		Minigame game = minigameCache.remove( minigame );
		if ( game != null ) {
			game.finalStop();
		}
		return game;
	}
	
	public void start( String minigame ) {
		Minigame game = minigameCache.get( minigame );
		if ( game != null ) {
			game.finalStart();
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
