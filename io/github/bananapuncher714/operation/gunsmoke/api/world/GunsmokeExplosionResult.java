package io.github.bananapuncher714.operation.gunsmoke.api.world;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class GunsmokeExplosionResult {
	protected GunsmokeExplosion explosion;
	protected Map< Location, Double > blockDamage;
	protected Map< Entity, Double > entityDamage; 
	
	protected GunsmokeExplosionResult( GunsmokeExplosion explosion ) {
		this.explosion = explosion;
		blockDamage = new HashMap< Location, Double >();
		entityDamage = new HashMap< Entity, Double >();
		
		// Calculate the block damage
		for ( Location location : explosion.damage.keySet() ) {
			double power = explosion.damage.get( location );// / cubed;

			if ( power == -1 ) {
				continue;
			}
			
			Location block = new Location( location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ() );

			blockDamage.put( block, blockDamage.getOrDefault( block, 0.0 ) + power );
		}
		
		for ( Entity entity : explosion.center.getWorld().getNearbyEntities( explosion.center, explosion.maxDistance, explosion.maxDistance, explosion.maxDistance ) ) {
			double width = entity.getWidth();
			double height = entity.getHeight();
			Location loc = entity.getLocation();
			loc.setYaw( 0 );
			loc.setPitch( 0 );
			loc.subtract( width / 2.0, 0, width / 2.0 );
			
			double damage = 0;
			for ( double x = loc.getX(); x < loc.getX() + width; x += GunsmokeExplosion.SCALE_INVERSE ) {
				for ( double z = loc.getZ(); z < loc.getZ() + width; z += GunsmokeExplosion.SCALE_INVERSE ) {
					for ( double y = loc.getY(); y < loc.getY() + height; y += GunsmokeExplosion.SCALE_INVERSE ) {
						damage += explosion.getDamageAt( new Location( entity.getWorld(), x, y, z ) );
					}
				}
			}
			entityDamage.put( entity, damage / GunsmokeExplosion.SCALE );
		}
	}
	
	public Map< Location, Double > getBlockDamage() {
		return blockDamage;
	}
	
	public Map< Entity, Double > getEntityDamage() {
		return entityDamage;
	}
	
	public double getBlockDamageAt( Location location ) {
		return blockDamage.getOrDefault( location, -1.0 );
	}

	public double getEntityDamageFor( Entity entity ) {
		return entityDamage.getOrDefault( entity, -1.0 );
	}
	
	public Map< Location, Double > getPreciseBlockDamage() {
		return explosion.damage;
	}
	
	public GunsmokeExplosion getExplosion() {
		return explosion;
	}
}
