package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperLivingEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultEntity;

/**
 * Simple vector math
 * 
 * @author BananaPuncher714
 */
public final class VectorUtil {
	private VectorUtil() {
	}
	
	// This checks to see if a ray intersects with an entity
	public static CollisionResultEntity rayIntersect( Entity entity, Location origin, Vector ray ) {
		if ( origin.getWorld() != entity.getWorld() ) {
			return null;
		}
		Location location = entity.getLocation();
		
		ray = ray.clone().normalize();
		
		BoundingBox box = entity.getBoundingBox();
		
		Location lower = new Location( location.getWorld(), box.getMinX(), box.getMinY(), box.getMinZ() );
		Location upper = new Location( location.getWorld(), box.getMaxX(), box.getMaxY(), box.getMaxZ() );

		Location entry = null;
		Location exit = null;
		BlockFace entryFace = null;
		BlockFace exitFace = null;
		
		if ( lower.getZ() - origin.getZ() > 0 ^ ray.getZ() < 0 ) {
			Location zLow = calculateVector( lower, new Vector( 0, 0, 1 ), origin, ray );
			if ( zLow != null && zLow.getX() >= lower.getX() && zLow.getX() <= upper.getX() && zLow.getY() >= lower.getY() && zLow.getY() <= upper.getY() ) {
				// Since this is the first one, no point in seeing if entry is null
				entry = zLow;
				entryFace = BlockFace.EAST;
			}
		}
		
		if ( upper.getZ() - origin.getZ() > 0 ^ ray.getZ() < 0 ) {
			Location zHigh = calculateVector( upper, new Vector( 0, 0, 1 ), origin, ray );
			if ( zHigh != null && zHigh.getX() >= lower.getX() && zHigh.getX() <= upper.getX() && zHigh.getY() >= lower.getY() && zHigh.getY() <= upper.getY() ) {
				if ( entry == null || origin.distanceSquared( zHigh ) < origin.distanceSquared( entry ) ) {
					exit = entry;
					exitFace = entryFace;
					entry = zHigh;
					entryFace = BlockFace.WEST;
				} else {
					exit = zHigh;
					exitFace = BlockFace.WEST;
				}
			}
		}
		
		if ( lower.getX() - origin.getX() > 0 ^ ray.getX() < 0 ) {
			Location xLow = calculateVector( lower, new Vector( 1, 0, 0 ), origin, ray );
			if ( xLow != null && xLow.getZ() >= lower.getZ() && xLow.getZ() <= upper.getZ() && xLow.getY() >= lower.getY() && xLow.getY() <= upper.getY() ) {
				if ( entry == null || origin.distanceSquared( xLow ) < origin.distanceSquared( entry ) ) {
					exit = entry;
					exitFace = entryFace;
					entry = xLow;
					entryFace = BlockFace.SOUTH;
				} else {
					exit = xLow;
					exitFace = BlockFace.SOUTH;
				}
			}
		}

		if ( upper.getX() - origin.getX() > 0 ^ ray.getX() < 0 ) {
			Location xHigh = calculateVector( upper, new Vector( 1, 0, 0 ), origin, ray );
			if ( xHigh != null && xHigh.getZ() >= lower.getZ() && xHigh.getZ() <= upper.getZ() && xHigh.getY() >= lower.getY() && xHigh.getY() <= upper.getY() ) {
				if ( entry == null || origin.distanceSquared( xHigh ) < origin.distanceSquared( entry ) ) {
					exit = entry;
					exitFace = entryFace;
					entry = xHigh;
					entryFace = BlockFace.NORTH;
				} else {
					exit = xHigh;
					exitFace = BlockFace.NORTH;
				}
			}
		}

		if ( lower.getY() - origin.getY() > 0 ^ ray.getY() < 0 ) {
			Location yLow = calculateVector( lower, new Vector( 0, 1, 0 ), origin, ray );
			if ( yLow != null && yLow.getX() >= lower.getX() && yLow.getX() <= upper.getX() && yLow.getZ() >= lower.getZ() && yLow.getZ() <= upper.getZ() ) {
				if ( entry == null || origin.distanceSquared( yLow ) < origin.distanceSquared( entry ) ) {
					exit = entry;
					exitFace = entryFace;
					entry = yLow;
					entryFace = BlockFace.DOWN;
				} else {
					exit = yLow;
					exitFace = BlockFace.DOWN;
				}
			}
		}

		if ( upper.getY() - origin.getY() > 0 ^ ray.getY() < 0 ) {
			Location yHigh = calculateVector( upper, new Vector( 0, 1, 0 ), origin, ray );
			if ( yHigh != null && yHigh.getX() >= lower.getX() && yHigh.getX() <= upper.getX() && yHigh.getZ() >= lower.getZ() && yHigh.getZ() <= upper.getZ() ) {
				if ( entry == null || origin.distanceSquared( yHigh ) < origin.distanceSquared( entry ) ) {
					exit = entry;
					exitFace = entryFace;
					entry = yHigh;
					entryFace = BlockFace.UP;
				} else {
					exit = yHigh;
					exitFace = BlockFace.UP;
				}
			}
		}

		if ( entry != null ) {
			return new CollisionResultEntity( GunsmokeUtil.getEntity( entity ), entry, entryFace, exit, exitFace );
		}
		return null;
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
	
	public static boolean fastCanSeeTwo( Location start, Location end, double scale ) {
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
	
	public static boolean isHeadshot( CollisionResultEntity intersection ) {
		GunsmokeEntity entity = intersection.getEntity();
		if ( entity instanceof GunsmokeEntityWrapperLivingEntity ) {
			GunsmokeEntityWrapperLivingEntity wrapper = ( GunsmokeEntityWrapperLivingEntity ) entity;
			
			double entityTop = wrapper.getEntity().getLocation().getY() + wrapper.getEntity().getHeight();
			
			double headLevel = wrapper.getEntity().getHeight() - wrapper.getEntity().getEyeHeight();
			
			entityTop -= headLevel * 2;

			if ( intersection.getLocation().getY() >= entityTop ) {
				return true;
			}
			if ( intersection.getExit() != null && intersection.getExit().getY() >= entityTop ) {
				return true;
			}
		}
		return false;
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
