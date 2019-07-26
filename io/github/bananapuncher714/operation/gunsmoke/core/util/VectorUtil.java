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
import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
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
		
		// TODO Maybe create a special method that takes delay in account?
		Location entLoc = GunsmokeUtil.getPlugin().getEntityTracker().getLocationOf( entity.getUniqueId() );
		if ( entLoc != null ) {
			Vector shift = entLoc.clone().subtract( entity.getLocation() ).toVector();
			box.shift( shift );
		}
		
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
	
	public static boolean intersects( Vector offset1, AABB box, Vector offset2, AABB... boxes ) {
		double originX = offset2.getX() - offset1.getX();
		double originY = offset2.getY() - offset1.getY();
		double originZ = offset2.getZ() - offset1.getZ();

		double oriX = box.oriX - originX;
		double oriY = box.oriY - originY;
		double oriZ = box.oriZ - originZ;
		
		for ( AABB otherBox : boxes ) {
			if ( Math.abs( oriY - otherBox.oriY ) < box.radY + otherBox.radY ) {
				if ( Math.abs( oriX - otherBox.oriX ) < box.radX + otherBox.radX ) {
					if ( Math.abs( oriZ - otherBox.oriZ ) < box.radZ + otherBox.radZ ) {
						return true;
					}					
				}
			}
		}
		return false;
	}
	
	public static boolean intersects( AABB box, AABB otherBox ) {
		if ( Math.abs( box.oriY - otherBox.oriY ) < box.radY + otherBox.radY ) {
			if ( Math.abs( box.oriX - otherBox.oriX ) < box.radX + otherBox.radX ) {
				if ( Math.abs( box.oriZ - otherBox.oriZ ) < box.radZ + otherBox.radZ ) {
					return true;
				}					
			}
		}
		return false;
	}
	
	public static boolean overlaps( AABB box, AABB other ) {
		if ( Math.abs( box.oriX - other.oriX ) < box.radX + other.radX ) {
			if ( Math.abs( box.oriZ - other.oriZ ) < box.radZ + other.radZ ) {
				return true;
			}					
		}
		return false;
	}
	
	public static boolean touching( AABB box, AABB other ) {
		// Check how much each axis overlaps each other
		// They must all be non-negative and only 1 can be zero
		double yOverlap = box.radY + other.radY - Math.abs( box.oriY - other.oriY );
		if ( yOverlap < 0 ) {
			return false;
		}
		double xOverlap = box.radX + other.radX - Math.abs( box.oriX - other.oriX );
		if ( xOverlap < 0 ) {
			return false;
		}
		double zOverlap = box.radZ + other.radZ - Math.abs( box.oriZ - other.oriZ );
		if ( zOverlap < 0 ) {
			return false;
		}
		return ( yOverlap == 0 ^ xOverlap == 0 ^ zOverlap == 0 ) && ( yOverlap + xOverlap + zOverlap > 0 );
	}
	
	public static boolean contains( AABB box, Vector vector ) {
		if ( Math.abs( box.oriY - vector.getY() ) <= box.radY ) {
			if ( Math.abs( box.oriX - vector.getX() ) <= box.radX ) {
				if ( Math.abs( box.oriZ - vector.getZ() ) <= box.radZ ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Vector closestPoint( AABB box, Vector point ) {
		double x = Math.max( box.minX, Math.min( box.maxX, point.getX() ) );
		double y = Math.max( box.minY, Math.min( box.maxY, point.getY() ) );
		double z = Math.max( box.minZ, Math.min( box.maxZ, point.getZ() ) );
		return new Vector( x, y, z );
	}
	
	public static double swept( AABB box, Vector movement, AABB check ) {
		double distEntX;
		double distEntY;
		double distEntZ;
		double distExtX;
		double distExtY;
		double distExtZ;

		if ( movement.getX() > 0 ) {
			distEntX = check.minX - box.maxX;
			distExtX = check.maxX - box.minX;
		} else {
			distEntX = check.maxX - box.minX;
			distExtX = check.minX - box.maxX;
		}

		if ( movement.getY() > 0 ) {
			distEntY = check.minY - box.maxY;
			distExtY = check.maxY - box.minY;
		} else {
			distEntY = check.maxY - box.minY;
			distExtY = check.minY - box.maxY;
		}

		if ( movement.getZ() > 0 ) {
			distEntZ = check.minZ - box.maxZ;
			distExtZ = check.maxZ - box.minZ;
		} else {
			distEntZ = check.maxZ - box.minZ;
			distExtZ = check.minZ - box.maxZ;
		}

		double entryX;
		double entryY;
		double entryZ;
		double exitX;
		double exitY;
		double exitZ;

		if ( movement.getX() == 0 ) {
			entryX = Double.NEGATIVE_INFINITY;
			exitX = Double.POSITIVE_INFINITY;
		} else {
			entryX = distEntX / movement.getX();
			exitX = distExtX / movement.getX();
		}

		if ( movement.getY() == 0 ) {
			entryY = Double.NEGATIVE_INFINITY;
			exitY = Double.POSITIVE_INFINITY;
		} else {
			entryY = distEntY / movement.getY();
			exitY = distExtY / movement.getY();
		}

		if ( movement.getZ() == 0 ) {
			entryZ = Double.NEGATIVE_INFINITY;
			exitZ = Double.POSITIVE_INFINITY;
		} else {
			entryZ = distEntZ / movement.getZ();
			exitZ = distExtZ / movement.getZ();
		}

		double entryTime = Math.max( entryX, Math.max( entryY, entryZ ) );
		double exitTime = Math.min( exitX, Math.min( exitY, exitZ ) );

		if ( entryTime >= exitTime || ( entryX < 0 && entryY < 0 && entryZ < 0 ) || entryX >= 1 || entryY >= 1 || entryZ >= 1 ) {
			return 1;
		} else {
			return 0;
		}
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
			
			Location entityLocation = GunsmokeUtil.getPlugin().getEntityTracker().getLocationOf( entity.getUUID() );
			
			if ( entityLocation == null ) {
				return false;
			}
			
			double entityTop = entityLocation.getY() + wrapper.getEntity().getHeight();
			
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
	
	public static Vector calculateVector( Vector planeLoc, Vector plane, Vector origin, Vector direction ) {
		if ( plane.dot( direction ) == 0 ) {
			return null;
		}
		double distance = ( plane.dot( planeLoc ) - plane.dot( origin ) ) / plane.dot( direction );
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
