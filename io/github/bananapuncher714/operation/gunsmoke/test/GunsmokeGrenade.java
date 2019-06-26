package io.github.bananapuncher714.operation.gunsmoke.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosion;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosionResult;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class GunsmokeGrenade extends GunsmokeProjectile {
	protected double reduction = .85;
	protected boolean hit = false;
	protected CollisionResult last = null;
	protected int bounces = 0;
	protected Vector gravity = new Vector( 0, -.05, 0 );
	protected Item item;
	
	public GunsmokeGrenade( Location location ) {
		super( location );
		
		item = location.getWorld().dropItem( location, new ItemStack( Material.SNOWBALL ) );
		item.setInvulnerable( true );
		item.setPickupDelay( 32767 );
	}
	
	@Override
	public EnumTickResult tick() {
		hit = false;
		last = null;
		Location previous = location.clone();
		super.tick();
		Vector velocity = getVelocity();
		velocity.setY( Math.max( -2, velocity.getY() - .05 ) );
		if ( last != null ) {
			location = last.getLocation();
			if ( bounces-- <= 0 ) {
				// Explode
				GunsmokeExplosion explosion = new GunsmokeExplosion( this, location, 8, 10 );
				GunsmokeExplosionResult result = explosion.explode();
				
				for ( Location location : result.getBlockDamage().keySet() ) {
					double health = GunsmokeUtil.getBlockHealth( location );
					health -= result.getBlockDamageAt( location );
					GunsmokeUtil.setBlockHealth( location, health );
				}
				
				return EnumTickResult.CANCEL;
			}
			location = last.getLocation();
			Vector direction = last.getDirection();
			
			direction.multiply( direction ).multiply( -reduction );
			
			if ( direction.getX() != 0 ) {
				velocity.setX( velocity.getX() * direction.getX() );
			}
			if ( direction.getY() != 0 ) {
				velocity.setY( velocity.getY() * direction.getY() );
			}
			if ( direction.getZ() != 0 ) {
				velocity.setZ( velocity.getZ() * direction.getZ() );
			}
		}
		setVelocity( velocity );
		
		Vector line = previous.subtract( location ).toVector();
		for ( int i = 0; i < 3; i++ ) {
			previous.getWorld().spawnParticle( Particle.FIREWORKS_SPARK, location.clone().add( line.clone().multiply( i / 11.0 ) ), 0 );
		}
		
		item.teleport( location );
		item.setVelocity( velocity );
		
		return EnumTickResult.CONTINUE;
	}
	
	@Override
	public void remove() {
		item.remove();
	}

	@Override
	public void hit( ProjectileTarget target ) {
		if ( !hit && target instanceof ProjectileTargetBlock ) {
			hit = true;
			last = target.getIntersection();
		}
	}

}
