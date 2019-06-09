package io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.projectile.GunsmokeProjectileHitEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class GunsmokeProjectile extends GunsmokeEntity {

	public GunsmokeProjectile( Location location ) {
		super( location );
	}

	@Override
	public EnumTickResult tick() {
		// First get a set of all things hit, then call events in order from closest hit to farthest hit
		Set< ProjectileTarget > hitTargets = new TreeSet< ProjectileTarget >();
		
		// First detect if this hit any entities
		List< Entity > nearbyEntities = GunsmokeUtil.getNearbyEntities( null, location, velocity );
		
		for ( Entity entity : nearbyEntities ) {
			Location intersection = VectorUtil.rayIntersect( entity, location, velocity );
			if ( intersection != null ) {
				// We know we hit something
				GunsmokeEntityWrapper wrappedEntity = new GunsmokeEntityWrapper( entity );
			}
		}
		
		// TODO Add way to hit Gunsmoke entities too...
		
		// Now start on block hit detection
		Block currentBlock = location.getBlock();
		
		Location destination = location.clone().add( velocity );
		
		
		
		return ( location.getY() > -64 ) ? EnumTickResult.CONTINUE : EnumTickResult.CANCEL;
	}

	protected void hit( GunsmokeEntity entity ) {
		
	}
	
	protected void hit( Block block, Location intersection ) {
		
	}
}
