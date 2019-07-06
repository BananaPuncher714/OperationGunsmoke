package io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.implementation.world.ConfigExplosion;

public class ConfigBulletOptions {
	protected double power;
	protected double gravity;
	protected double range;
	protected double speed;
	protected int maxLife;
	protected int hitEntityReduction;
	protected boolean piercing;
	protected boolean damageEntity;
	
	protected double blockHitReduction;
	protected Set< Material > damageableBlocks = new HashSet< Material >();
	protected Set< Material > ignoreBlocks = new HashSet< Material >();
	
	protected int fireTicks;
	protected Set< PotionEffect > effects = new HashSet< PotionEffect >();
	
	protected ConfigExplosion explosion;
	
	protected boolean bounce;
	protected double bounceReduction;
	protected Set< Material > bounceableBlocks = new HashSet< Material >();
	
	public ConfigBulletOptions( FileConfiguration config ) {
		power = config.getDouble( "power", 7 );
		gravity = config.getDouble( "gravity", .01 );
		speed = config.getDouble( "speed", 3 );
		range = config.getDouble( "range", 100 );
		maxLife = config.getInt( "max-life", 3000 );
		hitEntityReduction = config.getInt( "hit-entity-reduction", 3 );
		piercing = config.getBoolean( "piercing" );
		damageEntity = config.getBoolean( "damage-entity", true );
		
		blockHitReduction = config.getDouble( "block-hit-reduction" );
		for ( String str : config.getStringList( "damageable-blocks" ) ) {
			Material mat = Material.getMaterial( str.toUpperCase() );
			if ( mat == null ) {
				System.out.println( str + " is not a valid material! Please check your bullets folder!" );
			} else {
				damageableBlocks.add( mat );
			}
		}
		
		for ( String str : config.getStringList( "ignore-blocks" ) ) {
			Material mat = Material.getMaterial( str.toUpperCase() );
			if ( mat == null ) {
				System.out.println( str + " is not a valid material! Please check your bullets folder!" );
			} else {
				ignoreBlocks.add( mat );
			}
		}
		if ( config.getBoolean( "ignore-grass-blocks", true ) ) {
			for ( Material material : Material.values() ) {
				if ( material.isBlock() && !material.isSolid() ) {
					ignoreBlocks.add( material );
				}
			}
		}
		
		fireTicks = config.getInt( "fire", 0 );
		for ( String effect : config.getStringList( "effects" ) ) {
			String[] effParts = effect.split( " " );
			PotionEffectType type = PotionEffectType.getByName( effParts[ 0 ].toUpperCase() );
			int amp = Integer.valueOf( effParts[ 1 ] );
			int duration = Integer.valueOf( effParts[ 2 ] );
			
			effects.add( new PotionEffect( type, duration, amp, true, false, false ) );
		}
		
		bounce = config.getBoolean( "bounce", false );
		bounceReduction = config.getDouble( "bounce-reduction", .85 );
		for ( String str : config.getStringList( "bounceable-blocks" ) ) {
			Material mat = Material.getMaterial( str.toUpperCase() );
			if ( mat == null ) {
				System.out.println( str + " is not a valid material! Please check your bullets folder!" );
			} else {
				bounceableBlocks.add( mat );
			}
		}
		
		if ( config.getBoolean( "explode" ) ) {
			this.explosion = GunsmokeImplementation.getInstance().getExplosion( config.getString( "explosion", "" ) );
		}
	}
	
	public double getPower() {
		return power;
	}
	
	public void setPower( double power ) {
		this.power = power;
	}
	
	public double getGravity() {
		return gravity;
	}
	
	public void setGravity( double gravity ) {
		this.gravity = gravity;
	}
	
	public double getRange() {
		return range;
	}
	
	public void setRange( double range ) {
		this.range = range;
	}
	
	public double getSpeed() {
		return speed;
	}

	public void setSpeed( double speed ) {
		this.speed = speed;
	}

	public int getMaxLife() {
		return maxLife;
	}
	
	public void setMaxLife( int maxLife ) {
		this.maxLife = maxLife;
	}
	
	public int getEntityHitReduction() {
		return hitEntityReduction;
	}
	
	public void setHitEntityReduction( int hitEntityReduction ) {
		this.hitEntityReduction = hitEntityReduction;
	}
	
	public boolean isPiercing() {
		return piercing;
	}

	public void setPiercing( boolean piercing ) {
		this.piercing = piercing;
	}
	
	public boolean isDamageEntity() {
		return damageEntity;
	}

	public void setDamageEntity( boolean damageEntity ) {
		this.damageEntity = damageEntity;
	}
	
	public double getBlockHitReduction() {
		return blockHitReduction;
	}

	public void setBlockHitReduction( double blockHitReduction ) {
		this.blockHitReduction = blockHitReduction;
	}

	public int getFireTicks() {
		return fireTicks;
	}
	
	public void setFireTicks( int fireTicks ) {
		this.fireTicks = fireTicks;
	}
	
	public Set< PotionEffect > getEffects() {
		return effects;
	}
	
	public ConfigExplosion getExplosion() {
		return explosion;
	}
	
	public void setExplosion( ConfigExplosion explosion ) {
		this.explosion = explosion;
	}
	
	public boolean isBounce() {
		return bounce;
	}
	
	public void setBounce( boolean bounce ) {
		this.bounce = bounce;
	}
	
	public double getBounceReduction() {
		return bounceReduction;
	}
	
	public void setBounceReduction( double bounceReduction ) {
		this.bounceReduction = bounceReduction;
	}
	
	public Set< Material > getDamageableBlocks() {
		return damageableBlocks;
	}
	
	public Set< Material > getIgnoreBlocks() {
		return ignoreBlocks;
	}
	
	public Set< Material > getBounceableBlocks() {
		return bounceableBlocks;
	}
}
