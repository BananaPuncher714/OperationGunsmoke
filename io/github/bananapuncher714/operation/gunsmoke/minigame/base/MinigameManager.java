package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.util.HashMap;
import java.util.Map;

import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class MinigameManager {
	protected Gunsmoke plugin;
	
	protected Map< String, Minigame > minigameCache = new HashMap< String, Minigame >();
	
	public MinigameManager( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	public void addMinigame( String id, Minigame minigame ) {
		Minigame old = minigameCache.put( id, minigame );
		if ( old != null ) {
			old.shutdown();
		}
	}
	
	public Minigame getGame( String id ) {
		return minigameCache.get( id );
	}
	
	public Map< String, Minigame > getMinigames() {
		return minigameCache;
	}
}
