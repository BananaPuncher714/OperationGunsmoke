package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageByEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class EntityManager {
	protected Gunsmoke plugin;
	protected Map< UUID, GunsmokePlayer > entities;
	
	public EntityManager( Gunsmoke plugin ) {
		this.plugin = plugin;
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
	
	public void damage( GunsmokeEntity entity, double damage, DamageType type, DamageCause cause ) {
		GunsmokeEntityDamageEvent event = new GunsmokeEntityDamageEvent( entity, type, damage, cause );
		
		plugin.getTaskManager().callEventSync( event );
		
		if ( !event.isCancelled() ) {
			if ( entity instanceof GunsmokeEntityWrapper ) {
				GunsmokeEntityWrapper wrapper = ( GunsmokeEntityWrapper ) entity;
				if ( wrapper.getEntity() instanceof LivingEntity ) {
					LivingEntity lEntity = ( LivingEntity ) wrapper.getEntity();
					lEntity.setHealth( Math.max( 0, lEntity.getHealth() - event.getDamage() ) );
					GunsmokeUtil.playHurtAnimationFor( lEntity );
				}
			}
		}
	}
	
	public void damage( GunsmokeEntity entity, double damage, DamageType type, GunsmokeEntity damager ) {
		GunsmokeEntityDamageByEntityEvent event = new GunsmokeEntityDamageByEntityEvent( entity, type, damage, damager );
		
		plugin.getTaskManager().callEventSync( event );
		
		if ( !event.isCancelled() ) {
			if ( entity instanceof GunsmokeEntityWrapper ) {
				GunsmokeEntityWrapper wrapper = ( GunsmokeEntityWrapper ) entity;
				if ( wrapper.getEntity() instanceof LivingEntity ) {
					LivingEntity lEntity = ( LivingEntity ) wrapper.getEntity();
					lEntity.setHealth( Math.max( 0, lEntity.getHealth() - event.getDamage() ) );
					GunsmokeUtil.playHurtAnimationFor( lEntity );
				}
			}
		}
	}
}
