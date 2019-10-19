package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;

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
		}
	}
	
	public void leave( GunsmokeEntity entity ) {
		String prev = participants.remove( entity.getUUID() );
		if ( prev != null ) {
			getGame( prev ).leave( entity );
			Player player = ( ( GunsmokeEntityWrapperPlayer ) entity ).getEntity();
			PlayerSaveData data = saveData.get( player.getUniqueId() );
			data.apply( player );
		}
	}
	
	protected void quit( Player player ) {
		leave( plugin.getItemManager().getEntityWrapper( player ) );
	}
}
