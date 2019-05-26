package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;

public class EntityManager {
	Map< UUID, GunsmokeEntity > entities;
	
	public EntityManager() {
		entities = new ConcurrentHashMap< UUID, GunsmokeEntity >();
	}
	
	public GunsmokeEntity getEntity( UUID uuid ) {
		// TODO do something about properly filling out this information
		GunsmokeEntity entity = entities.get( uuid );
		if ( entity == null ) {
			entity = new GunsmokeEntity( uuid );
			entities.put( uuid, entity );
		}
		
		return entity;
	}
}
