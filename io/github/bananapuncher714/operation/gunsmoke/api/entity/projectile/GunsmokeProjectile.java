package io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult.CollisionType;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public abstract class GunsmokeProjectile extends GunsmokeEntity {
	private Set< UUID > hitEntities;
	private Set< Location > hitBlocks;
	
	public GunsmokeProjectile( Location location ) {
		super( location );
		
		hitEntities = new HashSet< UUID >();
		hitBlocks = new HashSet< Location >();
	}

	@Override
	public EnumTickResult tick() {
		if ( speed > 0 ) {
			// First get a set of all things hit, then call events in order from closest hit to farthest hit
			Set< ProjectileTarget > hitTargets = new TreeSet< ProjectileTarget >();
			
			// Detect if this hit any entities
			// TODO Add way to hit Gunsmoke entities too...
			List< Entity > nearbyEntities = GunsmokeUtil.getNearbyEntities( null, location, velocity );
			for ( Entity entity : nearbyEntities ) {
				if ( getHitEntities().contains( entity.getUniqueId() ) ) {
					continue;
				}
				CollisionResult intersection = VectorUtil.rayIntersect( entity, location, velocity );
				if ( intersection != null ) {
					// We know we hit something
					GunsmokeEntityWrapper wrappedEntity = new GunsmokeEntityWrapper( entity );
					
					hitTargets.add( new ProjectileTargetEntity( this, intersection.copyOf(), wrappedEntity ) );
					getHitEntities().add( entity.getUniqueId() );
				}
			}
			Set< Location > tickHitBlocks = new HashSet< Location >();
			
			// Now start on block hit detection
			// Get the point where our projectile should be after this tick
			Location destination = location.clone().add( getVelocity() );
			// Get the distance squared because getting the root is a scam
			double distance = location.distanceSquared( destination );
	
			// Get the first iteration done
			CollisionResultBlock hitBlock = GunsmokeUtil.rayTrace( location, velocity );
			Location hitLoc = hitBlock.getLocation();
			// PINEAPPLE GHOST FUWA FUWA
			// While we still haven't reached the full destination
			while ( distance >= location.distanceSquared( hitLoc ) ) {
				Block block = hitBlock.getBlock();
				
				if ( !tickHitBlocks.contains( block.getLocation() ) ) {
					if ( hitBlock.getCollisionType() == CollisionType.BLOCK ) {
						// TODO Add proper collision type list
						if ( block.getType() != Material.AIR ) {
							// TODO Add GunsmokeStructure detection
							Vector dirVec = BukkitUtil.toVector( hitBlock.getDirection() );
							hitBlock.getLocation().add( dirVec.multiply( .01 ) );
							ProjectileTarget target = new ProjectileTargetBlock( this, hitBlock );
							hitTargets.add( target );
							getHitBlocks().add( block.getLocation() );
							tickHitBlocks.add( block.getLocation() );
						}
					}
				}
				
				hitBlock = GunsmokeUtil.rayTrace( hitLoc, velocity );
				hitLoc = hitBlock.getLocation();
			}
			
			// Now that we have our targets we can start calling our other methods in order
			// Event calling should be handled by the implementation
			for ( ProjectileTarget target : hitTargets ) {
				hit( target );
			}
			location.add( getVelocity() );
		}
		// Erase this projectile from existence if it falls beyond the void
		return ( location.getY() > -64 ) ? EnumTickResult.CONTINUE : EnumTickResult.CANCEL;
	}
	
	protected Set< UUID > getHitEntities() {
		return hitEntities;
	}
	
	protected Set< Location > getHitBlocks() {
		return hitBlocks;
	}

	abstract public void hit( ProjectileTarget target );
}
