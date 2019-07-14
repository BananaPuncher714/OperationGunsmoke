package io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult.CollisionType;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public abstract class GunsmokeProjectile extends GunsmokeEntity {
	private Set< UUID > hitEntities;
	private Set< UUID > tickHitEntities;
	private Set< Location > hitBlocks;
	private Set< Location > tickHitBlocks;
	
	public GunsmokeProjectile( Location location ) {
		super( location );
		
		hitEntities = new HashSet< UUID >();
		hitBlocks = new HashSet< Location >();
		tickHitEntities = new HashSet< UUID >();
		tickHitBlocks = new HashSet< Location >();
	}

	@Override
	public EnumTickResult tick() {
		if ( getSpeed() > 0 ) {
			double speedSquared = getSpeed() * getSpeed();
			
			// First get a set of all things hit, then call events in order from closest hit to farthest hit
			Set< ProjectileTarget > hitTargets = new TreeSet< ProjectileTarget >();
			
			// Detect if this hit any entities
			// TODO Add way to hit Gunsmoke entities too...
			
			Collection< Entity > nearbyEntities = location.getWorld().getNearbyEntities( location, getSpeed(), getSpeed(), getSpeed() );//GunsmokeUtil.getNearbyEntities( null, location, getVelocity() );
			
			for ( Entity entity : nearbyEntities ) {
				if ( tickHitEntities.contains( entity.getUniqueId() ) ) {
					continue;
				}
				Location originalPos = GunsmokeUtil.getPlugin().getEntityTracker().getLocationOf( entity.getUniqueId() );
				if ( originalPos == null ) {
					continue;
				}
				
				CollisionResultEntity intersection = VectorUtil.rayIntersect( entity, location, velocity );
				if ( intersection != null ) {
					if ( intersection.getLocation().distanceSquared( location ) > speedSquared ) {
						continue;
					}
					Vector toEntity = intersection.getLocation().clone().subtract( location ).toVector();
					if ( velocity.dot( toEntity ) <= 0 ) {
						continue;
					}
					
					// We know we hit something
					CollisionResultEntity copy = intersection.copyOf();
					
					hitTargets.add( new ProjectileTargetEntity( this, copy ) );
					getHitEntities().add( entity.getUniqueId() );
					tickHitEntities.add( entity.getUniqueId() );
				}
			}
			// Now start on block hit detection
			// Get the point where our projectile should be after this tick
			Location destination = location.clone().add( getVelocity() );
			// Get the distance squared because getting the root is a scam
			double distance = location.distanceSquared( destination );

			List< CollisionResultBlock > collisions = GunsmokeUtil.rayTrace( location, getVelocity() );
			// For each collision...
			for ( CollisionResultBlock collision : collisions ) {
				Block block = collision.getBlock();
				Location hitLoc = collision.getLocation();
				if ( distance < location.distanceSquared( hitLoc ) ) {
					break;
				}
				
				if ( !tickHitBlocks.contains( block.getLocation() ) ) {
					if ( collision.getCollisionType() == CollisionType.BLOCK ) {
						// TODO Add proper collision type list
						if ( block.getType() != Material.AIR ) {
							// TODO Add GunsmokeStructure detection
							Vector dirVec = BukkitUtil.toVector( collision.getDirection() );
							collision.getLocation().add( dirVec.multiply( .01 ) );
							collision.getLocation().getWorld().spawnParticle( Particle.DRIP_LAVA, collision.getLocation(), 0 );
							ProjectileTarget target = new ProjectileTargetBlock( this, collision );
							hitTargets.add( target );
							getHitBlocks().add( block.getLocation() );
							tickHitBlocks.add( block.getLocation() );
						}
					}
				}
			}
			
			// Now that we have our targets we can start calling our other methods in order
			// Event calling should be handled by the implementation
			Location newLoc = location.clone().add( getVelocity() );
			for ( ProjectileTarget target : hitTargets ) {
				if ( hit( target ) == EnumTickResult.CANCEL ) {
					newLoc = target.getIntersection().getLocation();
					break;
				}
			}
			
			location = newLoc;
		}
		// Erase this projectile from existence if it falls beyond the void
		return ( location.getY() > -64 ) ? EnumTickResult.CONTINUE : EnumTickResult.CANCEL;
	}
	
	protected Set< UUID > getHitEntities() {
		return hitEntities;
	}

	protected Set< UUID > getTickHitEntities() {
		return tickHitEntities;
	}
	
	protected Set< Location > getHitBlocks() {
		return hitBlocks;
	}
	
	protected Set< Location > getTickHitBlocks() {
		return tickHitBlocks;
	}

	abstract public EnumTickResult hit( ProjectileTarget target );
}
