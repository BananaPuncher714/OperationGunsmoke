package io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeEntityWrapperFactory;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperLivingEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosion;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosionResult;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.world.ConfigExplosion;

public class ConfigBullet extends GunsmokeProjectile {
	protected GunsmokeEntity shooter;
	protected double power;
	protected long created;
	protected double distanceTravelled;
	
	protected CollisionResultBlock collision = null;
	
	protected ConfigBulletOptions options;
	
	public ConfigBullet( GunsmokeEntity shooter, Location location, ConfigBulletOptions options ) {
		super( location );
		this.shooter = shooter;
		
		created = System.currentTimeMillis();
		
		this.options = options;
		
		power = options.getPower();
		
		setVelocity( location.getDirection().multiply( options.getSpeed() ) );
	}

	@Override
	public EnumTickResult tick() {
		// Save last hit block for ricochet
		collision = null;
		Location previous = location.clone();
		if ( super.tick() == EnumTickResult.CANCEL ) {
			return EnumTickResult.CANCEL;
		}
		Vector velocity = getVelocity();
		velocity.setY( Math.max( -2, velocity.getY() - options.getGravity() ) );
		
		if ( collision != null ) {
			Vector direction = BukkitUtil.toVector( collision.getDirection() );
			
			direction.multiply( direction ).multiply( - options.getBounceReduction() );
			
			if ( direction.getX() != 0 ) {
				velocity.setX( velocity.getX() * direction.getX() );
			}
			if ( direction.getY() != 0 ) {
				velocity.setY( velocity.getY() * direction.getY() );
			}
			if ( direction.getZ() != 0 ) {
				velocity.setZ( velocity.getZ() * direction.getZ() );
			}
			getTickHitBlocks().clear();
		}
		
		distanceTravelled += location.distance( previous );
		
		setVelocity( velocity );
		
		// Bullet trail
		Vector line = previous.subtract( location ).toVector();
		for ( int i = 0; i < 10; i++ ) {
			location.getWorld().spawnParticle( Particle.DRIP_WATER, location.clone().add( line.clone().multiply( i / 10.0 ) ), 0 );
		}
		
		return ( distanceTravelled > options.getRange() || System.currentTimeMillis() - created > options.getMaxLife() || power <= 0 ) ? EnumTickResult.CANCEL : EnumTickResult.CONTINUE;
	}
	
	@Override
	public EnumTickResult hit( ProjectileTarget target ) {
		if ( target instanceof ProjectileTargetBlock ) {
			ProjectileTargetBlock blockTarget = ( ProjectileTargetBlock ) target;
			Block mcBlock = blockTarget.getIntersection().getBlock();
			
			// If bounce...
			if ( options.isBounce() ) {
				if ( options.getBounceableBlocks().contains( mcBlock.getType() ) ) {
					collision = blockTarget.getIntersection();
					return EnumTickResult.CANCEL;
				}
			}
			
			if ( options.getIgnoreBlocks().contains( mcBlock.getType() ) ) {
				return EnumTickResult.CONTINUE;
			}
			
			// Explode if applicable
			ConfigExplosion configExplosion = options.getExplosion();
			if ( configExplosion != null ) {
				
				GunsmokeExplosion explosion = configExplosion.getExplosion( this, target.getIntersection().getLocation() );
				
				GunsmokeExplosionResult result = explosion.explode();
				
				for ( Location location : result.getBlockDamage().keySet() ) {
					if ( location.getBlock().getType() != Material.AIR ) {
						GunsmokeUtil.damageBlockAt( location, result.getBlockDamage().get( location ), this, DamageType.EXPLOSION );
					}
				}
				
				for ( Entity entity : result.getEntityDamage().keySet() ) {
					GunsmokeUtil.damage( GunsmokeEntityWrapperFactory.wrap( entity ), DamageType.EXPLOSION, result.getEntityDamage().get( entity ), this );
				}
				
				power = 0;
				return EnumTickResult.CANCEL;
			}
			
			// Stop the bullet if it hits an undamageable block
			if ( !options.getDamageableBlocks().contains( mcBlock.getType() ) ) {
				power = 0;
				return EnumTickResult.CANCEL;
			}
			
			// Now get the block and damage or whatever
			GunsmokeBlock block = GunsmokeUtil.getBlockAt( blockTarget.getIntersection().getBlock().getLocation() );
			if ( block.isInvincible() ) {
				power = 0;
				return EnumTickResult.CANCEL;
			}
			
			double health = block.getHealth();
			GunsmokeUtil.damageBlockAt( block.getLocation(), power, this, DamageType.PHYSICAL );
			power -= health * options.getBlockHitReduction();
		} else if ( target instanceof ProjectileTargetEntity ) {
			ProjectileTargetEntity entTarget = ( ProjectileTargetEntity ) target;
			
			if ( !options.isDamageEntity() ) {
				return EnumTickResult.CONTINUE;
			}
			
			GunsmokeEntity entity = entTarget.getHitEntity();
			if ( !entity.isInvincible() && !entity.getUUID().equals( shooter.getUUID() ) ) {
				double distance = distanceTravelled + location.distance( target.getIntersection().getLocation() );
				double damage = getDamage( distance ) * power;
				if ( damage > 0 ) {
					GunsmokeUtil.damage( entity, DamageType.PHYSICAL, power, this );
					if ( shooter instanceof GunsmokeEntityWrapperPlayer ) {
						Player player = ( ( GunsmokeEntityWrapperPlayer ) shooter ).getEntity();
						player.playSound( player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 15, 1 );
					}
				}
				if ( entity instanceof GunsmokeEntityWrapperLivingEntity ) {
					GunsmokeEntityWrapperLivingEntity wrapper = ( GunsmokeEntityWrapperLivingEntity ) entity;
					if ( options.getFireTicks() > 0 ) {
						wrapper.getEntity().setFireTicks( options.getFireTicks() );
					}
					for ( PotionEffect effect : options.getEffects() ) {
						wrapper.getEntity().addPotionEffect( effect );
					}
				}
				power -= options.getEntityHitReduction();
				if ( !options.isPiercing() ) {
					power = 0;
					return EnumTickResult.CANCEL;
				}
			}
		}
		return power <= 0 ? EnumTickResult.CANCEL : EnumTickResult.CONTINUE;
	}
	
	protected double getDamage( double distance ) {
		if ( distance > options.getRange() ) {
			return 0;
		}
		double halfRange = options.getRange() / 2;
		if ( distance > halfRange ) {
			return 1 - ( ( distance - halfRange ) / halfRange );
		}
		return 1;
	}
}
