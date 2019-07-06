package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult.CollisionType;

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
	public static CollisionResult rayIntersect( Entity entity, Location origin, Vector ray ) {
		if ( origin.getWorld() != entity.getWorld() ) {
			return null;
		}
		Location location = entity.getLocation();
		
		ray = ray.clone().normalize();
		
		BoundingBox box = entity.getBoundingBox();
		
		Location lower = new Location( location.getWorld(), box.getMinX(), box.getMinY(), box.getMinZ() );
		Location upper = new Location( location.getWorld(), box.getMaxX(), box.getMaxY(), box.getMaxZ() );

		// TODO Add a way to determine the entry and exit point?
		// TODO This is somewhat important
		// TODO so I thought I'd add
		// TODO a few more here to remind myself
		
		CollisionResult collision = null;
		
		if ( lower.getZ() - origin.getZ() > 0 ^ ray.getZ() < 0 ) {
			Location zLow = calculateVector( lower, new Vector( 0, 0, 1 ), origin, ray );
			if ( zLow != null && zLow.getX() >= lower.getX() && zLow.getX() <= upper.getX() && zLow.getY() >= lower.getY() && zLow.getY() <= upper.getY() ) {
				CollisionResult hitPoint = new CollisionResult( zLow, BlockFace.EAST, CollisionType.ENTITY );
				if ( collision == null || origin.distanceSquared( hitPoint.getLocation() ) < origin.distanceSquared( collision.getLocation() ) ) {
					collision = hitPoint;
				}
			}
		}
		
		if ( upper.getZ() - origin.getZ() > 0 ^ ray.getZ() < 0 ) {
			Location zHigh = calculateVector( upper, new Vector( 0, 0, 1 ), origin, ray );
			if ( zHigh != null && zHigh.getX() >= lower.getX() && zHigh.getX() <= upper.getX() && zHigh.getY() >= lower.getY() && zHigh.getY() <= upper.getY() ) {
				CollisionResult hitPoint = new CollisionResult( zHigh, BlockFace.WEST, CollisionType.ENTITY );
				if ( collision == null || origin.distanceSquared( hitPoint.getLocation() ) < origin.distanceSquared( collision.getLocation() ) ) {
					collision = hitPoint;
				}
			}
		}
		
		if ( lower.getX() - origin.getX() > 0 ^ ray.getX() < 0 ) {
			Location xLow = calculateVector( lower, new Vector( 1, 0, 0 ), origin, ray );
			if ( xLow != null && xLow.getZ() >= lower.getZ() && xLow.getZ() <= upper.getZ() && xLow.getY() >= lower.getY() && xLow.getY() <= upper.getY() ) {
				CollisionResult hitPoint = new CollisionResult( xLow, BlockFace.SOUTH, CollisionType.ENTITY );
				if ( collision == null || origin.distanceSquared( hitPoint.getLocation() ) < origin.distanceSquared( collision.getLocation() ) ) {
					collision = hitPoint;
				}
			}
		}

		if ( upper.getX() - origin.getX() > 0 ^ ray.getX() < 0 ) {
			Location xHigh = calculateVector( upper, new Vector( 1, 0, 0 ), origin, ray );
			if ( xHigh != null && xHigh.getZ() >= lower.getZ() && xHigh.getZ() <= upper.getZ() && xHigh.getY() >= lower.getY() && xHigh.getY() <= upper.getY() ) {
				CollisionResult hitPoint = new CollisionResult( xHigh, BlockFace.NORTH, CollisionType.ENTITY );
				if ( collision == null || origin.distanceSquared( hitPoint.getLocation() ) < origin.distanceSquared( collision.getLocation() ) ) {
					collision = hitPoint;
				}
			}
		}

		if ( lower.getY() - origin.getY() > 0 ^ ray.getY() < 0 ) {
			Location yLow = calculateVector( lower, new Vector( 0, 1, 0 ), origin, ray );
			if ( yLow != null && yLow.getX() >= lower.getX() && yLow.getX() <= upper.getX() && yLow.getZ() >= lower.getZ() && yLow.getZ() <= upper.getZ() ) {
				CollisionResult hitPoint = new CollisionResult( yLow, BlockFace.DOWN, CollisionType.ENTITY );
				if ( collision == null || origin.distanceSquared( hitPoint.getLocation() ) < origin.distanceSquared( collision.getLocation() ) ) {
					collision = hitPoint;
				}
			}
		}

		if ( upper.getY() - origin.getY() > 0 ^ ray.getY() < 0 ) {
			Location yHigh = calculateVector( upper, new Vector( 0, 1, 0 ), origin, ray );
			if ( yHigh != null && yHigh.getX() >= lower.getX() && yHigh.getX() <= upper.getX() && yHigh.getZ() >= lower.getZ() && yHigh.getZ() <= upper.getZ() ) {
				CollisionResult hitPoint = new CollisionResult( yHigh, BlockFace.UP, CollisionType.ENTITY );
				if ( collision == null || origin.distanceSquared( hitPoint.getLocation() ) < origin.distanceSquared( collision.getLocation() ) ) {
					collision = hitPoint;
				}
			}
		}
		return collision;
	}
	
	public static boolean fastCanSee( Location start, Location end ) {
			// Get the coords
			int px1 = start.getBlockX();
			int py1 = start.getBlockY();
			int pz1 = start.getBlockZ();
			
			int px2 = end.getBlockX();
			int py2 = end.getBlockY();
			int pz2 = end.getBlockZ();
			
			// Get the width and height difference
			int dx = Math.abs( px1 - px2 );
		    int dy = Math.abs( py1 - py2 );
		    int dz = Math.abs( pz1 - pz2 );
		    
		    int x = px1;
		    int y = py1;
		    int z = pz1;
		    int n = 1 + dx + dy + dz;
		    int x_inc = (px2 > px1) ? 1 : -1;
		    int y_inc = (py2 > py1) ? 1 : -1;
		    int z_inc = (pz2 > pz1) ? 1 : -1;
		    int errorxy = dx - dy;
		    int errorxz = dx - dz;
		    int errorzy = dz - dy;
		    dx *= 2;
		    dy *= 2;
		    dz *= 2;

		    for (; n > 0; --n) {
		    	Location newLocation = new Location( start.getWorld(), x, y, z );
		        if ( newLocation.getBlock().getType() != Material.AIR ) {
		        	return false;
		        }
		        newLocation.getBlock().setType( Material.GLASS );

		        if ( errorxy > 0 && errorxz > 0 ) {
		        	x += x_inc;
		        	errorxy -= dy;
		        	errorxz -= dz;
		        } else if ( errorxz <= 0 && errorzy > 0 ) {
		        	z += z_inc;
		        	errorxz += dx;
		        	errorzy -= dy;
		        } else {
		        	y += y_inc;
		        	errorxy += dx;
		        	errorzy += dz;
		        }
		        
		    }
		    return true;
	}
	
	public static boolean fastCanSeeTwo( Location start, Location end ) {
		double scale = 3;
		
		// Get the coords
		double px1 = start.getX();
		double py1 = start.getY();
		double pz1 = start.getZ();
		
		double px2 = end.getX();
		double py2 = end.getY();
		double pz2 = end.getZ();
		
		// Get the width and height difference
		double dx = Math.abs( px1 - px2 );
		double dy = Math.abs( py1 - py2 );
		double dz = Math.abs( pz1 - pz2 );
	    
		double x = px1;
		double y = py1;
		double z = pz1;
		double n = ( dx + dy + dz ) * scale;
		double x_inc = ((px2 > px1) ? 1 : -1 ) / scale;
		double y_inc = ((py2 > py1) ? 1 : -1 ) / scale;
		double z_inc = ((pz2 > pz1) ? 1 : -1 ) / scale;
		double errorxy = dx - dy;
		double errorxz = dx - dz;
		double errorzy = dz - dy;
	    dx *= 2;
	    dy *= 2;
	    dz *= 2;

	    for (; n > 0; --n) {
	    	Location newLocation = new Location( start.getWorld(), x, y, z );
	        if ( newLocation.getBlock().getType() != Material.AIR && newLocation.getBlock().getType() != Material.GLASS ) {
	        	return false;
	        }
	        newLocation.getBlock().setType( Material.GLASS );

	        if ( errorxy > 0 && errorxz > 0 ) {
	        	x += x_inc;
	        	errorxy -= dy;
	        	errorxz -= dz;
	        } else if ( errorxz <= 0 && errorzy > 0 ) {
	        	z += z_inc;
	        	errorxz += dx;
	        	errorzy -= dy;
	        } else {
	        	y += y_inc;
	        	errorxy += dx;
	        	errorzy += dz;
	        }
	        
	    }
	    return true;
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
	 * Round a location's values rounded
	 * 
	 * @param location
	 * @return
	 */
	public static Location round( Location location, double val ) {
		location.setX( ( int ) ( location.getX() * val ) / val );
		location.setY( ( int ) ( location.getY() * val ) / val );
		location.setZ( ( int ) ( location.getZ() * val ) / val );
		return location;
	}
	
	public static Location round( Location location ) {
		location.setX( location.getBlockX() );
		location.setY( location.getBlockY() );
		location.setZ( location.getBlockZ() );
		return location;
		
	}
}
