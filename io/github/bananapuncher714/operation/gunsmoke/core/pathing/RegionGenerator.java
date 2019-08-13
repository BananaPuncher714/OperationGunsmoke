package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.util.Vector;

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
		
		while ( !uncombined.isEmpty() ) {
			// Get the first empty one or something like that...
			AABB region = uncombined.poll();

			boolean success = false;
			for (  Iterator< AABB > iterator = uncombined.iterator(); iterator.hasNext(); ) {
				AABB box = iterator.next();
				// If their height are the same
				if ( box.maxX == region.maxX && box.minX == region.minX ) {
					// If their Z value is the same
					if ( box.maxZ == region.maxZ && box.minZ == region.minZ ) {
						// If they're touching along the x axis
						if ( box.minY == region.maxY || box.maxY == region.minY ) {
							// Combine and add back to the list
							iterator.remove();
							success = true;

							AABB combinedRegion = new AABB( region.maxX, Math.max( region.maxY, box.maxY ), region.maxZ, region.minX, Math.min( region.minY, box.minY ), region.minZ );
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
		uncombined.clear();
		uncombined.addAll( combined );
		combined.clear();
		
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
	
	public static void elevate( RegionMap map ) {
		// First sort regions into their corresponding y values
		Map< Double, List< Region > > layers = new TreeMap< Double, List< Region > >();
		for ( Region region : map.getRegions() ) {
			double y = region.getRegion().minY;
			List< Region > layerSet = layers.get( y );
			if ( layerSet == null ) {
				layerSet = new ArrayList< Region >();
				layers.put( y, layerSet );
			}
			layerSet.add( region );
		}
		
		// Now we can sort them or something
		for ( List< Region > regionList : layers.values() ) {
			while ( !regionList.isEmpty() ) {
				Region pop = regionList.remove( regionList.size() - 1 );
				ElevationLayer newLayer = new ElevationLayer();
				newLayer.add( pop );
				List< Region > neighbors = new ArrayList< Region >( pop.getNeighbors().keySet() );
				while ( !neighbors.isEmpty() ) {
					Region neighbor = neighbors.remove( neighbors.size() - 1 );
					if ( neighbor.getRegion().minY != pop.getRegion().minY ) {
						continue;
					}
					if ( regionList.contains( neighbor ) ) {
						newLayer.add( neighbor );
						regionList.remove( neighbor );
						neighbors.addAll( neighbor.getNeighbors().keySet() );
					}
				}
				map.getLayers().add( newLayer );
			}
		}
		
		// Now we have our layers and need to construct the layer connections
		for ( ElevationLayer eLayer : map.getLayers() ) {
			for ( Region region : eLayer.getRegions() ) {
				for ( Region neighbor : region.getNeighbors().keySet() ) {
					if ( eLayer.contains( neighbor ) ) {
						continue;
					}
					
					ElevationLayer neighborLayer = map.getLayer( neighbor );
					ElevationEdge edge = new ElevationEdge( region, eLayer, neighbor, neighborLayer );
					
					eLayer.getEdgesFor( region ).add( edge );
				}
			}
		}
	}
	
	public static void generateCorners( RegionMap map ) {
		// So essentially, construct a set of AABB vertical lines indicating the corners of each neighbor
		// Only worry about the ones which are at least .6 tall and less than or equal to 1.25 tall
		// Then, check to see if any of them overlap with more than .6 distance
		// If so, it means there's not really a corner there and remove
		for ( Region region : map.getRegions() ) {
			AABB regionBox = region.getRegion();
			List< AABB > corners = new ArrayList< AABB >();
			Set< AABB > remove = new HashSet< AABB >();
			
			for ( Region neighbor : region.getNeighbors().keySet() ) {
				Edge edge = region.getNeighbors().get( neighbor );
				AABB box = edge.getIntersection();
				
				// Test for the edge's heights
				if ( box.minY - regionBox.minY > 1.25 || box.lenY < .6 || regionBox.minY - neighbor.getRegion().minY > 1.25 ) {
					continue;
				}
				
				AABB max = new AABB( box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ );
				AABB min = new AABB( box.minX, box.maxY, box.minZ, box.minX, box.minY, box.minZ );
				
				corners.add( min );
				corners.add( max );
			}
			for ( int i = 0; i < corners.size(); i++ ) {
				AABB one = corners.get( i );
				for ( int j = i + 1; j < corners.size(); j++ ) {
					AABB two = corners.get( j );
					if ( one.minX == two.minX && one.minZ == two.minZ ) {
						if ( Math.abs( one.oriY - two.oriY ) <= one.radY + two.radY + .6 ) {
							remove.add( one );
							remove.add( two );
						}
					}
				}
			}
			Set< AABB > cornerSet = new HashSet< AABB >( corners );
			cornerSet.removeAll( remove );
			for ( AABB corner : cornerSet ) {
				region.getCorners().add( new Corner( region, corner ) );
			}
			// The corners provided by this solution cover all the necessary corners, and a little more
		}
	}
	
	public static Set< Region > getVisibleRegionsFor( RegionMap map, Location location ) {
		Set< Region > visible = new HashSet< Region >();
		
		Region region = map.getRegion( location );
		if ( region == null  ) {
			return visible;
		}
		
		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		while ( !paths.isEmpty() ) {
			PathRegion path = paths.poll();
			Region latest = path.lastRegion();

			visible.add( latest );
			for ( Region neighbor : latest.getNeighbors().keySet() ) {
				if ( visible.contains( latest ) ) {
					continue;
				}
				if ( neighbor.getRegion().minY - latest.getRegion().minY > 1.25 ) {
					continue;
				}
				Edge edge = latest.getNeighbors().get( neighbor );
				AABB box = edge.getIntersection();
				if ( box.lenY < .6 ) {
					continue;
				}
				
				AABB max = new AABB( box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ );
				AABB min = new AABB( box.minX, box.maxY, box.minZ, box.minX, box.minY, box.minZ );
				
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				paths.add( newPath );
			}
		}
		
		return visible;
	}
	
	public static Set< Corner > getVisibleCornersFor( RegionMap map, Location location ) {
		Set< Corner > visible = new HashSet< Corner >();
		
		Region region = map.getRegion( location );
		if ( region == null  ) {
			return visible;
		}

		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		Set< Region > passed = new HashSet< Region >();
		passed.add( region );
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		visible.addAll( region.getCorners() );
		while ( !paths.isEmpty() ) {
			PathRegion path = paths.poll();
			Region latest = path.lastRegion();

			passed.add( latest );
			for ( Region neighbor : latest.getNeighbors().keySet() ) {
				if ( passed.contains( latest ) ) {
					continue;
				}
				for ( Corner corner : neighbor.getCorners() ) {
					
				}
				
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
			}
		}
		
		return visible;
	}
	
	public static void getCornersFor( Region region ) {
		AABB regionBox = region.getRegion();
		List< AABB > corners = new ArrayList< AABB >();
		Set< AABB > remove = new HashSet< AABB >();
		
		for ( Edge edge : region.getNeighbors().values() ) {
			AABB box = edge.getIntersection();
			
			// Test for the edge's heights
			if ( box.minY - regionBox.minY > 1.25 || box.lenY < .6 ) {
				continue;
			}
			
			AABB max = new AABB( box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ );
			AABB min = new AABB( box.minX, box.maxY, box.minZ, box.minX, box.minY, box.minZ );
			
			corners.add( min );
			corners.add( max );
		}
		System.out.println( "Amount detected " + corners.size() );
		for ( int i = 0; i < corners.size(); i++ ) {
			AABB one = corners.get( i );
			System.out.println( one );
			for ( int j = i + 1; j < corners.size(); j++ ) {
				AABB two = corners.get( j );
				if ( one.minX == two.minX && one.minZ == two.minZ ) {
					System.out.println( "Same coord as " + two );
					if ( Math.abs( one.oriY - two.oriY ) <= one.radY + two.radY + .6 ) {
						System.out.println( "INTERSECTED" );
						remove.add( one );
						remove.add( two );
					}
				}
			}
		}
		System.out.println( "Remove " + remove.size() );
		Set< AABB > cornerSet = new HashSet< AABB >( corners );
		cornerSet.removeAll( remove );
		System.out.println( "New size " + cornerSet.size() );
	}
	
	public static Path optimize( Location start, Location end, PathRegion path ) {
		// Convert the start to a vector
		Vector lastSolid = start.toVector();
		// Convert the end to a vector
		Vector lastClosest = end.toVector();
		// Create a vector from the start to the end and normalize it
		Vector solidToLastClosest = lastClosest.clone().subtract( lastSolid );
		
		// Create our path
		Path optimized = new Path( start );
		
		// Take our list of regions and create a list of edges from them
		List< Region > regions = path.getRegions();
		List< Edge > edges = new ArrayList< Edge >();
		for ( int i = 0; i < regions.size() - 1; i++ ) {
			Region currentRegion = regions.get( i );
			Region nextRegion = regions.get( i + 1 );
			
			Edge edge = currentRegion.getNeighbors().get( nextRegion );
			if ( edge != null ) {
				edges.add( edge );
			} else {
				System.out.println( "An edge doesn't exist between 2 neighbors!" );
			}
		}
		
		// First check if we can directly go from start to stop
		boolean direct = true;
		for ( Edge edge : edges ) {
			if ( !edge.intersects( lastSolid, solidToLastClosest ) ) {
				direct = false;
				break;
			}
		}
		
		// Now we set our closest edge to -1
		int closestEdge = -1;
		while ( !direct ) {
			// Re-set our last closest point
			lastClosest = end.toVector();
			// For each edge
			for ( int i = edges.size() - 1; i > closestEdge; i-- ) {
				Edge edge = edges.get( i );
				
				// First construct a vector from the last point to the last closest
				solidToLastClosest = lastClosest.clone().subtract( lastSolid );

				// Get the edge and the closest point from the last point
				Vector closest = edge.getClosestPoint( lastSolid, solidToLastClosest );

				// Construct a new one that goes directly to the solid
				Vector solidToClosest = closest.clone().subtract( lastSolid ).normalize();
				
				boolean valid = true;
				for ( int j = i - 1; j > closestEdge; j-- ) {
					Edge nextEdge = edges.get( j );
					if ( !nextEdge.intersects( lastSolid, solidToClosest ) ) {
						valid = false;
						break;
					}
				}
				if ( valid ) {
					closestEdge = i;
					lastSolid = closest;
					// Right here, we need to change lastSolid to reflect the proper values that haven't changed
					optimized.addLocation( new Location( start.getWorld(), closest.getX(), closest.getY(), closest.getZ() ) );
					if ( i == edges.size() - 1 ) {
						direct = true;
					}
					break;
				}
				lastClosest = closest;
			}
		}
		optimized.addLocation( end );
		return optimized;
		
		// So the way this optimization works is as follows:
		// Construct a vector from the last solid point to the end point
		// The last solid point is a point where it's guaranteed to be the fastest point
		// It should change each time a new point is found
		// With the vector, loop through each edge in reverse order
		// For each edge
		// Get the point closest to the vector
		// Construct a new vector from the last solid point to the new point
		// Loop through each edge in reverse from the point to the last solid point
		// If no edges intersect, then it is a successful point
		// Add this to the list of points on the path, and mark this as the last solid point
		// Otherwise, if an intersection is detected, then skip this
		// Repeat the for loop until there is a path from the start to the stop
		// The path is now optimized
	}
}