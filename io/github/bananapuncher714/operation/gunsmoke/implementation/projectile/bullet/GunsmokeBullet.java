package io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet;

import org.bukkit.Location;
import org.bukkit.Particle;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class GunsmokeBullet extends GunsmokeProjectile {
	protected GunsmokeEntity shooter;
	protected double power;
	protected long created;
	
	public GunsmokeBullet( GunsmokeEntity shooter, Location location ) {
		super( location );
		this.shooter = shooter;
		created = System.currentTimeMillis();
		power = 0;
	}
	
	@Override
	public EnumTickResult tick() {
		for ( int i = 0; i < 10; i++ ) {
			location.getWorld().spawnParticle( Particle.WATER_BUBBLE, location.clone().add( velocity.clone().multiply( i / 11.0 ) ), 0 );
		}
		
		if ( super.tick() == EnumTickResult.CANCEL ) {
			return EnumTickResult.CANCEL;
		}
		return ( System.currentTimeMillis() - created > 5000 || power <= 0 ) ? EnumTickResult.CANCEL : EnumTickResult.CONTINUE;
	}

	@Override
	public EnumTickResult hit( ProjectileTarget target ) {
		if ( target instanceof ProjectileTargetBlock ) {
			ProjectileTargetBlock blockTarget = ( ProjectileTargetBlock ) target;
			
			GunsmokeBlock block = GunsmokeUtil.getBlockAt( blockTarget.getIntersection().getBlock().getLocation() );
			if ( block.isInvincible() ) {
				power = 0;
				return EnumTickResult.CANCEL;
			}
			
			double health = block.getHealth();
			GunsmokeUtil.damageBlockAt( block.getLocation(), power, this, DamageType.PHYSICAL );
			power -= health;
		} else if ( target instanceof ProjectileTargetEntity ) {
			ProjectileTargetEntity entTarget = ( ProjectileTargetEntity ) target;
			
			GunsmokeEntity entity = entTarget.getHitEntity();
			if ( !entity.isInvincible() && !entity.getUUID().equals( shooter.getUUID() ) ) {
				GunsmokeUtil.damage( entity, DamageType.PHYSICAL, power, this );
				power--;
			}
		}
		return power <= 0 ? EnumTickResult.CANCEL : EnumTickResult.CONTINUE;
	}

	public double getPower() {
		return power;
	}

	public void setPower( double power ) {
		this.power = power;
	}
}
