package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class RegionGenerator {
	public static List< AABB > getBoxesAt( Location location ) {
		List< AABB > boxes = new ArrayList< AABB >();
		Location newLoc = location.clone();
		for ( int y = 0; y < 256; y++ ) {
			newLoc.setY( y );
			for( AABB box : GunsmokeUtil.getPlugin().getProtocol().getHandler().getBoxesFor( newLoc ) ) {
				// Shift each bounding box into the real world
				boxes.add( box.shift( newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ() ) );
			}
		}
		return boxes;
	}
	
	public static EnclosingRegion generateRegions( Location location ) {
		List< AABB > regions = new ArrayList< AABB >();
		// Given some real bounding boxes
		List< AABB > boxes = getBoxesAt( location );
		
		// So at any given time, while scanning upwards, we want to have a list of regions that can still expand or be divided
		List< AABB > potential = new ArrayList< AABB >();
		List< AABB > addNextTick = new ArrayList< AABB >();
		// Add an empty AABB box in the bottom of our location
		potential.add( new AABB( location.getBlockX() + 1, 0, location.getBlockZ() + 1, location.getBlockX(), 0, location.getBlockZ() ) );
		for ( AABB box : boxes ) {
			// Our current boxes are bound in the real world
			// We're iterating through the bottom most to the top most blocks
			for ( Iterator< AABB > iterator = potential.iterator(); iterator.hasNext(); ) {
				AABB region = iterator.next();
				// Here, we want to split each region up into smaller regions depending on the position of the current box
				// We know our region ranges from 0, 0 to 1, 1
				if ( VectorUtil.overlaps( box, region ) ) {
					// The boxes overlap somewhere!
					// We know that our regions clearly overlap, but our region isn't completely in the other one
					// Try and split the region into 4 smaller sub-regions
					// +-------+
					// |       |
					// | +----------+
					// +-|          |
					//   |          |
					//   |          |
					//   +----------+
					// As long as the minZ is less than the box's minZ then we can have a top
					if ( region.minZ < box.minZ ) {
						AABB top = new AABB( Math.min( box.maxX, region.maxX ), box.maxY, box.minZ, region.minX, region.minY, region.minZ );
						addNextTick.add( top );
					}
					// As long as the minX is less than the box's then we have a left
					if ( region.minX < box.minX ) {
						AABB left = new AABB( box.minX, box.maxY, region.maxZ, region.minX, region.minY, Math.max( region.minZ, box.minZ ) );
						addNextTick.add( left );
					}
					// Same for the other sides
					if ( region.maxX > box.maxX ) {
						AABB right = new AABB( region.maxX, box.maxY, Math.min( box.maxZ, region.maxZ ), box.maxX, region.minY, region.minZ );
						addNextTick.add( right );
					}
					if ( region.maxZ > box.maxZ ) {
						AABB bottom = new AABB( Math.max( region.maxX, box.maxX ), box.maxY, region.maxZ, Math.max( region.minX, box.minX ), region.minY, box.maxZ );
						addNextTick.add( bottom );
					}
					// We know that they overlap, so now we need to get the overlap space
					if ( box.minY > region.minY ) {
						AABB overlap = new AABB( Math.min( box.maxX, region.maxX ), box.minY, Math.min( box.maxZ, region.maxZ ), Math.max( region.minX, box.minX ), region.minY, Math.max( region.minZ, box.minZ ) );
						regions.add( overlap );
					}
					
					// Remove the current region
					iterator.remove();
				}
				// Since nothing can be on top of our current box, then we create a new one with the top
			}
			potential.add( new AABB( box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ ) );
			potential.addAll( addNextTick );
			addNextTick.clear();
		}
		for ( AABB region : potential ) {
			if ( region.minY < 255 ) {
				regions.add( new AABB( region.maxX, 255, region.maxZ, region.minX, region.minY, region.minZ ) );
			}
		}
		
		return new EnclosingRegion( boxes, regions );
	}
	
	public static List< AABB > combineRegions( List< AABB > regions ) {
		List< AABB > combined = new ArrayList< AABB >();
		Queue< AABB > uncombined = new ArrayDeque< AABB >();
		Set< AABB > toAdd = new HashSet< AABB >();
		uncombined.addAll( regions );
		
		// First combine the regions across similar x values
		while ( !uncombined.isEmpty() ) {
			// Get the first empty one or something like that...
			AABB region = uncombined.poll();
			
			boolean success = false;
			for (  Iterator< AABB > iterator = uncombined.iterator(); iterator.hasNext(); ) {
				AABB box = iterator.next();
				// If their height are the same
				if ( box.maxY == region.maxY && box.minY == region.minY ) {
					// If their Z value is the same
					if ( box.maxZ == region.maxZ && box.minZ == region.minZ ) {
						// If they're touching along the x axis
						if ( box.minX == region.maxX || box.maxX == region.minX ) {
							// Combine and add back to the list
							iterator.remove();
							success = true;

							AABB combinedRegion = new AABB( Math.max( box.maxX, region.maxX ), region.maxY, region.maxZ, Math.min( box.minX, region.minX ), region.minY, region.minZ );
							toAdd.add( combinedRegion );
							break;
						}
					}
				}
			}
			
			if ( !success ) {
				combined.add( region );
			} else {
				uncombined.addAll( toAdd );
				toAdd.clear();
			}
		}
		
		// Now we combine them across the z values
		uncombined.clear();
		uncombined.addAll( combined );
		combined.clear();
		while ( !uncombined.isEmpty() ) {
			// Get the first empty one or something like that...
			AABB region = uncombined.poll();
			
			boolean success = false;
			for ( Iterator< AABB > iterator = uncombined.iterator(); iterator.hasNext(); ) {
				AABB box = iterator.next();
				// If their height are the same
				if ( box.maxY == region.maxY && box.minY == region.minY ) {
					// If their Z value is the same
					if ( box.maxX == region.maxX && box.minX == region.minX ) {
						// If they're touching along the x axis
						if ( box.minZ == region.maxZ || box.maxZ == region.minZ ) {
							// Combine and add back to the list
							iterator.remove();
							success = true;

							AABB combinedRegion = new AABB( region.maxX, region.maxY, Math.max( box.maxZ, region.maxZ ), region.minX, region.minY, Math.min( box.minZ, region.minZ ) );
							toAdd.add( combinedRegion );
							break;
						}
					}
				}
			}
			
			if ( !success ) {
				combined.add( region );
			} else {
				uncombined.addAll( toAdd );
				toAdd.clear();
			}
		}
		
		return combined;
	}
}