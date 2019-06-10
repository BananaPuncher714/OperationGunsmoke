package io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile.GunsmokeProjectileHitBlockEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile.GunsmokeProjectileHitEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public abstract class GunsmokeProjectile extends GunsmokeEntity {

	public GunsmokeProjectile( Location location ) {
		super( location );
	}

	@Override
	public EnumTickResult tick() {
		// First get a set of all things hit, then call events in order from closest hit to farthest hit
		Set< ProjectileTarget > hitTargets = new TreeSet< ProjectileTarget >();
		
		// Detect if this hit any entities
		// TODO Add way to hit Gunsmoke entities too...
		List< Entity > nearbyEntities = GunsmokeUtil.getNearbyEntities( null, location, velocity );
		
		for ( Entity entity : nearbyEntities ) {
			Location intersection = VectorUtil.rayIntersect( entity, location, velocity );
			if ( intersection != null ) {
				// We know we hit something
				GunsmokeEntityWrapper wrappedEntity = new GunsmokeEntityWrapper( entity );
				
				hitTargets.add( new ProjectileTargetEntity( this, intersection, wrappedEntity ) );
			}
		}
		
		// Now start on block hit detection
		// Get the point where our projectile should be after this tick
		Location destination = location.clone().add( velocity );
		// Get the distance squared because getting the root is a scam
		double distance = location.distanceSquared( destination );

		// Don't want to detect the same block twice
		Set< Block > hitBlocks = new HashSet< Block >();
		// Don't hit the current block we're in
		hitBlocks.add( location.getBlock() );
		// Get the first iteration done
		Location hitBlock = GunsmokeUtil.rayTrace( location, velocity );
		// While we still haven't reached the full destination
		while ( distance >= location.distanceSquared( hitBlock ) ) {
			Block block = hitBlock.getBlock();
			
			// TODO Add GunsmokeStructure detection
			if ( block.getType() != Material.AIR ) {
				if ( !hitBlocks.contains( block ) ) {
					hitTargets.add( new ProjectileTargetBlock( this, hitBlock, block ) );
					hitBlocks.add( block );
				}
			}
			
			hitBlock = GunsmokeUtil.rayTrace( hitBlock, velocity );
		}
		
		// Now that we have our targets we can start calling the events in order
		for ( ProjectileTarget target : hitTargets ) {
			// TODO Add a more friendly way to include other sorts of ProjectileTargets
			if ( target instanceof ProjectileTargetBlock ) {
				ProjectileTargetBlock blockTarget = ( ProjectileTargetBlock ) target;
				GunsmokeProjectileHitBlockEvent hitEvent = new GunsmokeProjectileHitBlockEvent( this, blockTarget.getHitBlock(), blockTarget.getIntersection() );

				GunsmokeUtil.callEventSync( hitEvent );
				
				if ( !hitEvent.isCancelled() ) {
					hit( blockTarget );
				}
			} else if ( target instanceof ProjectileTargetEntity ) {
				ProjectileTargetEntity entityTarget = ( ProjectileTargetEntity ) target;
				GunsmokeProjectileHitEntityEvent hitEvent = new GunsmokeProjectileHitEntityEvent( this, entityTarget.getHitEntity(), entityTarget.getIntersection() );

				GunsmokeUtil.callEventSync( hitEvent );
				
				if ( !hitEvent.isCancelled() ) {
					hit( entityTarget );
				}
			}
		}
		
		location.add( velocity );
		
		// Erase this projectile from existence if it falls beyond the void
		return ( location.getY() > -64 ) ? EnumTickResult.CONTINUE : EnumTickResult.CANCEL;
	}

	abstract public void hit( ProjectileTargetEntity target );
	abstract public void hit( ProjectileTargetBlock target );
}
