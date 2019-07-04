package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.DamageRecord;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageByEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class EntityManager {
	protected static final Map< DamageCause, Integer > DEFAULT_INVINCIBILITY;
	
	protected Gunsmoke plugin;
	protected Map< UUID, GunsmokePlayer > entities;
	protected Map< UUID, DamageRecord > records = new HashMap< UUID, DamageRecord >();
	
	static {
		DEFAULT_INVINCIBILITY = new HashMap< DamageCause, Integer >();
		for ( DamageCause cause : DamageCause.values() ) {
			DEFAULT_INVINCIBILITY.put( cause, 10 );
		}
		
		DEFAULT_INVINCIBILITY.put( DamageCause.ENTITY_EXPLOSION, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.BLOCK_EXPLOSION, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.LIGHTNING, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.FALL, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.DROWNING, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.WITHER, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.VOID, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.THORNS, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.FALLING_BLOCK, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.HOT_FLOOR, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.FIRE, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.POISON, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.PROJECTILE, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.ENTITY_ATTACK, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.ENTITY_SWEEP_ATTACK, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.SUFFOCATION, 5 );
		DEFAULT_INVINCIBILITY.put( DamageCause.CUSTOM, 0 );
		DEFAULT_INVINCIBILITY.put( DamageCause.CONTACT, 5 );
	}
	
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
	
	public DamageRecord getDamageRecord( UUID uuid ) {
		DamageRecord record = records.get( uuid );
		if ( record == null ) {
			record = new DamageRecord();
			records.put( uuid, record );
		}
		return record;
	}
	
	public boolean damage( GunsmokeEntity entity, double damage, DamageType type, DamageCause cause ) {
		// These 3 should get handled with damage by entity
		if ( entity.isInvincible() ) {
			return false;
		}
		if ( cause == DamageCause.PROJECTILE || cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_SWEEP_ATTACK ) {
			return false;
		}
		if ( !getDamageRecord( entity.getUUID() ).setTicksRemainingFor( cause, damage, DEFAULT_INVINCIBILITY.get( cause ) ) ) {
			return false;
		}
		
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
			return true;
		}
		return false;
	}
	
	public boolean damage( GunsmokeEntity entity, double damage, DamageType type, GunsmokeEntity damager ) {
		if ( entity.isInvincible() ) {
			return false;
		}
		if ( !getDamageRecord( entity.getUUID() ).setTicksRemainingFor( damager.getUUID(), damage, DEFAULT_INVINCIBILITY.get( DamageCause.ENTITY_ATTACK ) ) ) {
			return false;
		}
		
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
			if ( damager instanceof GunsmokeEntityWrapper ) {
				GunsmokeEntityWrapper damagerWrapper = ( GunsmokeEntityWrapper ) entity;
				if ( damagerWrapper.getEntity() instanceof Arrow ) {
					damagerWrapper.getEntity().remove();
				}
			}
			return true;
		}
		return false;
	}
}
