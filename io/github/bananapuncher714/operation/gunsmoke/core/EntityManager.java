package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.LivingEntity;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;

public class EntityManager {
	Map< UUID, GunsmokePlayer > entities;
	
	public EntityManager() {
		entities = new ConcurrentHashMap< UUID, GunsmokePlayer >();
	}
	
	public GunsmokePlayer getEntity( UUID uuid ) {
		// TODO do something about properly filling out this information
		GunsmokePlayer entity = entities.get( uuid );
		if ( entity == null ) {
			entity = new GunsmokePlayer( uuid );
			entities.put( uuid, entity );
		}
		
		return entity;
	}
	
	public void damage( LivingEntity entity, double damage, DamageType type ) {
		
	}
}
