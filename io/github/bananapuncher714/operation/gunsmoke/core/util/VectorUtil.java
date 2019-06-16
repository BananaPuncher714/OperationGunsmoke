package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

/**
 * Simple vector math
 * 
 * @author BananaPuncher714
 */
public final class VectorUtil {
	private VectorUtil() {
	}
	
	// This can be made so much faster since I know where the origin is
	// This checks to see if a ray intersects with an entity
	public static Location rayIntersect( Entity entity, Location origin, Vector ray ) {
		if ( origin.getWorld() != entity.getWorld() ) {
			return null;
		}
		Location location = entity.getLocation();
		
		ray = ray.clone().normalize();
		
		double rad = entity.getWidth() / 2.0;
		
		Location lower = location.clone().subtract( rad, 0, rad );
		Location upper = location.clone().add( rad, entity.getHeight(), rad );

		// TODO Add a way to determine the entry and exit point?
		// TODO This is somewhat important
		// TODO so I thought I'd add
		// TODO a few more here to remind myself
		
		if ( lower.getZ() - origin.getZ() > 0 ^ ray.getZ() < 0 ) {
			Location zLow = calculateVector( lower, new Vector( 0, 0, 1 ), origin, ray );
			if ( zLow != null && zLow.getX() >= lower.getX() && zLow.getX() <= upper.getX() && zLow.getY() >= lower.getY() && zLow.getY() <= upper.getY() ) {
				return zLow;
			}
		}
		
		if ( upper.getZ() - origin.getZ() > 0 ^ ray.getZ() < 0 ) {
			Location zHigh = calculateVector( upper, new Vector( 0, 0, 1 ), origin, ray );
			if ( zHigh != null && zHigh.getX() >= lower.getX() && zHigh.getX() <= upper.getX() && zHigh.getY() >= lower.getY() && zHigh.getY() <= upper.getY() ) {
				return zHigh;
			}
		}

		if ( lower.getX() - origin.getX() > 0 ^ ray.getX() < 0 ) {
			Location xLow = calculateVector( lower, new Vector( 1, 0, 0 ), origin, ray );
			if ( xLow != null && xLow.getZ() >= lower.getZ() && xLow.getZ() <= upper.getZ() && xLow.getY() >= lower.getY() && xLow.getY() <= upper.getY() ) {
				return xLow;
			}
		}

		if ( upper.getX() - origin.getX() > 0 ^ ray.getX() < 0 ) {
			Location xHigh = calculateVector( upper, new Vector( 1, 0, 0 ), origin, ray );
			if ( xHigh != null && xHigh.getZ() >= lower.getZ() && xHigh.getZ() <= upper.getZ() && xHigh.getY() >= lower.getY() && xHigh.getY() <= upper.getY() ) {
				return xHigh;
			}
		}

		if ( lower.getY() - origin.getY() > 0 ^ ray.getY() < 0 ) {
			Location yLow = calculateVector( lower, new Vector( 0, 1, 0 ), origin, ray );
			if ( yLow != null && yLow.getX() >= lower.getX() && yLow.getX() <= upper.getX() && yLow.getZ() >= lower.getZ() && yLow.getZ() <= upper.getZ() ) {
				return yLow;
			}
		}

		if ( upper.getY() - origin.getY() > 0 ^ ray.getY() < 0 ) {
			Location yHigh = calculateVector( upper, new Vector( 0, 1, 0 ), origin, ray );
			if ( yHigh != null && yHigh.getX() >= lower.getX() && yHigh.getX() <= upper.getX() && yHigh.getZ() >= lower.getZ() && yHigh.getZ() <= upper.getZ() ) {
				return yHigh;
			}
		}
		return null;
	}
	
	public static Vector randomizeSpread( Vector vec, double deg ) {
		// No Y difference for you, only good for single shots
		double length = vec.length();
		vec.normalize();
		double x = vec.getX();
		double y = vec.getY();
		double z = vec.getZ();
		
		double flatLength = Math.sqrt( x * x + z * z );
		double ratio = y / flatLength;
		
		double degree = FastMath.atan2( ( float ) x, ( float ) z ) * FastMath.DEG;
		degree = degree + ( ThreadLocalRandom.current().nextDouble() * ( 2 * deg ) ) - deg;
		double ydeg = FastMath.atan2( ( float ) flatLength, ( float ) vec.getY() ) * FastMath.DEG + ( ThreadLocalRandom.current().nextDouble() * deg );
		double rad = ( degree * Math.PI ) / 180.0;
		double yrad = ( ydeg * Math.PI ) / 180.0;
		
		vec.setZ( flatLength * Math.cos( rad ) );
		vec.setX( flatLength * Math.sin( rad ) );
		vec.setY( ratio * Math.sin( yrad ) );
		vec.normalize().multiply( length );
		return vec;
	}
	
	public static Vector randomizeSpread( Vector vec, double yaw, double pitch ) {
		Random rand = ThreadLocalRandom.current();
		double length = vec.length();
		
		Location location = new Location( null, 0, 0, 0 );
		location.setDirection( vec );
		// Equal random distribution
//		double yawDiff = yaw - ( rand.nextDouble() * 2 * yaw );
//		double pitchDiff = pitch - ( rand.nextDouble() * 2 * pitch );
		
		// Normal distribution
		double yawDiff = yaw * rand.nextGaussian() / 3.0;
		double pitchDiff = pitch * rand.nextGaussian() / 3.0;
		
		location.setYaw( ( float ) ( location.getYaw() + yawDiff ) );
		location.setPitch( ( float ) ( location.getPitch() + pitchDiff ) );
		
		return location.getDirection().multiply( length );
	}
	
	public static boolean isHeadshot( LivingEntity entity, Location intersection ) {
		double upper = entity.getHeight() + entity.getLocation().getY();
		double lower = upper - ( entity.getHeight() - entity.getEyeHeight() ) * 2;
		
		return intersection.getY() >= lower;
	}
	
	/**
	 * Calculate the exact location at which a vector intersects a given plane
	 * 
	 * @param planeLoc
	 * A point on the plane
	 * @param plane
	 * A vector normal to the plane
	 * @param origin
	 * The origin of the ray
	 * @param direction
	 * A normal direction of the ray
	 * @return
	 * null if it never intersects
	 */
	public static Location calculateVector( Location planeLoc, Vector plane, Location origin, Vector direction ) {
		if ( plane.dot( direction ) == 0 ) {
			return null;
		}
		
		double distance = ( plane.dot( planeLoc.toVector() ) - plane.dot( origin.toVector() ) ) / plane.dot( direction );
		return origin.clone().add( direction.multiply( distance ) );
	}
	
	/**
	 * Round a location's values rounded to the sixteenth
	 * 
	 * @param location
	 * @return
	 */
	public static Location sixteenth( Location location ) {
		location.setX( location.getX() * 128 / 128 );
		location.setY( location.getY() * 128 / 128 );
		location.setZ( location.getZ() * 128 / 128 );
		return location;
	}
}
