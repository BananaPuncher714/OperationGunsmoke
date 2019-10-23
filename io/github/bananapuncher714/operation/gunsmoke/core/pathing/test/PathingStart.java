package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.ComparableVec;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Corner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathCorner;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathfinderDev;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionMap;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.NodeGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.PathEnd;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class PathingStart {
	private final static File BASE = new File( System.getProperty( "user.dir" ) );
	
	public static void main( String[] args ) {
		File regions = new File( BASE + "/" + "regions" );
		RegionMap map = new RegionMap();
		for ( File file : regions.listFiles() ) {
			map.getRegionFor( RegionLoader.load( file ) );
		}
		for ( Region region : map.getRegions() ) {
			AABB box = region.getRegion();
			for ( Region otherRegion : map.getRegions() ) {
				AABB other = otherRegion.getRegion(); 
				if ( other == box ) {
					continue;
				}
				if ( VectorUtil.touching( box, other ) ) {
					region.addNeighbor( otherRegion );
				}
			}
		}
		RegionGenerator.generateCorners( map );
		System.out.println( "Done loading!" );
		System.out.println( "Loaded " + map.getRegions().size() + " regions" );
		
		Vector playerPos = new Vector( -291.484, 61, -36.7 );
		Region playerRegion = map.getRegion( playerPos );
		Vector finish = new Vector( -294.5179450720437,56.375,-48.581931510232025 );
		Region finishRegion = map.getRegion( finish );
		
		Vector self = new Vector( -0.5366845635742735,0.0,0.8437829574132742 );
		System.out.println( self.dot( self ) );
		
		RegionPanel.draw( playerPos, new PathRegion( map.getRegion( playerPos ) ), new ComparableVec( new Vector( 1, 0, 0 ), new Vector( 1, 0, 0 ) ), null );
		RegionPanel.pause();
		
		PathfinderDev pathfinder = new PathfinderDev( map, playerPos, finish );
//		PathRegion path = pathfinder.calculate( 10000 );
		for ( PathCorner region : PathfinderDev.getPossibleCornerPaths( map, playerPos, new Corner( playerRegion, playerPos ), finish, new Corner( finishRegion, finish ) ) ) {
			RegionPanel.draw( playerPos, region, null, null );
			System.out.println( region.lastCorner().getVector() );
			RegionPanel.pause();
			
//			RegionPanel.addPath( region );
//			RegionPanel.addCorner( region.lastCorner().min() );
		}
		RegionPanel.draw( playerPos, new PathRegion( map.getRegion( playerPos ) ), new ComparableVec( new Vector( 1, 0, 0 ), new Vector( 1, 0, 0 ) ), null );

//		System.out.println( path.getRegions().size() );
//		System.out.println( path.getPath().getWaypoints().size() );
//		RegionPanel.draw( playerPos, path, null, null );
//		RegionPanel.pause();
//		Path updated = PathfinderDev.optimize( map, playerPos, playerRegion, finish, finishRegion, path );
//		path.setOptimized( updated, 0 );
//		RegionPanel.draw( playerPos, path, null, null );
	}
	
	public static Set< PathEnd > getVisibleCornersFor( Region region, Vector start ) {
		Set< PathEnd > visible = new HashSet< PathEnd >();
		
		if ( region == null  ) {
			return visible;
		}

		Queue< PathRegion > paths = new ArrayDeque< PathRegion >();
		Map< PathRegion, ComparableVec > vecMap = new HashMap< PathRegion, ComparableVec >();
		
		// This map is in case if you wanted to loop back around
		Map< PathRegion, Vector > starts = new HashMap< PathRegion, Vector >();
		
		// Store the last edges intersected
		Map< PathRegion, Set< Edge > > lastEdges = new HashMap< PathRegion, Set< Edge > >();
		
		PathRegion startPath = new PathRegion( region );
		paths.add( startPath );
		starts.put( startPath, start.clone().setY( 0 ) );
		
		for ( Corner corner : region.getCorners() ) {
			visible.add( new PathEnd( startPath, corner ) );
		}

		int iterations = 0;
		while ( !paths.isEmpty() ) {
			iterations++;
			PathRegion path = paths.poll();
			Region latest = path.lastRegion();
			ComparableVec originalVec = vecMap.remove( path );
			Vector startVector = starts.remove( path );
			Set< Edge > lastEdge = lastEdges.remove( path );
			
			for ( Region neighbor : latest.getNeighbors().keySet() ) {
				if ( path.contains( neighbor ) ) {
					continue;
				}
				// Check if we can reach this region
				if ( neighbor.getRegion().minY - latest.getRegion().minY > 1.25 ) {
					continue;
				}
				Edge edge = latest.getNeighbors().get( neighbor );
				AABB box = edge.getIntersection();
				// Check if we can physically fit in the region
				if ( box.lenY < .6 ) {
					continue;
				}
				
				Vector origin = startVector.clone();
				
				Vector toMax = new Vector( box.maxX, 0, box.maxZ ).subtract( origin ).setY( 0 ).normalize();
				Vector toMin = new Vector( box.minX, 0, box.minZ ).subtract( origin ).setY( 0 ).normalize();
				
				// Check if this region is visible
				ComparableVec cv = originalVec == null ? null : new ComparableVec( originalVec.getMin(), originalVec.getMax() );
				ComparableVec newVec = new ComparableVec( toMin, toMax );
				int val = 0;
				Set< Edge > edgeSet = new HashSet< Edge >();
				if ( cv != null ) {
					if ( lastEdge != null ) {
						boolean reversed = false;
						for ( Edge prevEdge : lastEdge ) {
							AABB prevBox = prevEdge.getIntersection();
							AABB edgeIntersection = new AABB( Math.min( box.maxX, prevBox.maxX ),
									0,
									Math.min( box.maxZ, prevBox.maxZ ),
									Math.max( box.minX, prevBox.minX ),
									0,
									Math.max( box.minZ, prevBox.minZ ) );
							if ( ( edgeIntersection.lenX > 0 ^ edgeIntersection.lenZ > 0 ) && ( edgeIntersection.lenX >= 0 && edgeIntersection.lenZ >= 0 ) ) {
								Vector reverseNormal = edge.getNormal().clone().add( edge.getNormal() ).subtract( new Vector( 1, 1, 1 ) ).multiply( -1 );
								double xDist = ( box.oriX - origin.getX() ) * edge.getNormal().getX() * 2;
								double zDist = ( box.oriZ - origin.getZ() ) * edge.getNormal().getZ() * 2;
								origin.add( new Vector( xDist, 0, zDist ) );
								
								toMax = new Vector( box.maxX, 0, box.maxZ ).subtract( origin ).setY( 0 ).normalize();
								toMin = new Vector( box.minX, 0, box.minZ ).subtract( origin ).setY( 0 ).normalize();
								
								newVec = new ComparableVec( toMin, toMax );
								cv = new ComparableVec( cv.getMin().multiply( reverseNormal ), cv.getMax().multiply( reverseNormal ) );
								reversed = true;
								break;
							}
						}
						if ( !reversed ) {
							edgeSet.addAll( lastEdge );
						}
					}
					
					int maxRange = cv.getTowards( toMax );
					int minRange = cv.getTowards( toMin );

					int cvMaxRange = newVec.getTowards( cv.getMax() );
					int cvMinRange = newVec.getTowards( cv.getMin() );

					if ( ( maxRange != 0 && maxRange == minRange ) || ( cvMaxRange != 0 && cvMaxRange == cvMinRange ) ) {
						Vector corner = new Vector( box.minX, 0, box.minZ );
						if ( cv.getMid().dot( toMax ) > cv.getMid().dot( toMin ) ) {
							corner = new Vector( box.maxX, 0, box.maxZ );
						}
						RegionPanel.addCorner( corner );
						continue;
					}
					
					// First, see if both of the values are within the range
					if ( maxRange == 0 && minRange == 0 ) {
						val = 1;
					} else if ( cvMaxRange == 0 && cvMinRange == 0 ) {
						val = 2;
						newVec = new ComparableVec( cv.getMin(), cv.getMax() );
					} else {
						if ( maxRange == 0 && cvMaxRange == 0 ) {
							val = 3;
							newVec = new ComparableVec( toMax, cv.getMax() );
						} else if ( maxRange == 0 && cvMinRange == 0 ) {
							val = 4;
							newVec = new ComparableVec( toMax, cv.getMin() );
						} else if ( minRange == 0 && cvMaxRange == 0 ) {
							val = 5;
							newVec = new ComparableVec( toMin, cv.getMax() );
						} else {
							val = 6;
							// -0.5366845635742735,0.0,0.8437829574132742 is not a nice vector
							newVec = new ComparableVec( toMin, cv.getMin() );
						}
					}
				}
				edgeSet.add( edge );
				
				// At this point we can be certain we can see the region
				PathRegion newPath = path.copyOf();
				newPath.add( neighbor );
				paths.add( newPath );
				vecMap.put( newPath, newVec );
				starts.put( newPath, origin );
				lastEdges.put( newPath, edgeSet );
				
				RegionPanel.draw( origin, newPath, newVec, edge );
				if ( NodeGenerator.intersects( edge, origin, newVec.getMax() ) == null || NodeGenerator.intersects( edge, origin, newVec.getMin() ) == null ) {
					System.out.println( "ERROR " + val );
				}
				
				try {
					Thread.sleep( 2 );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
//				RegionPanel.pause();

				for ( Corner corner : neighbor.getCorners() ) {
					Vector toCorner = corner.getVector().subtract( origin ).setY( 0 ).normalize();
					if ( newVec.getTowards( toCorner ) == 0  ) {
						visible.add( new PathEnd( newPath, corner ) );
					}
				}
			}
		}
		
		System.out.println( "Iterations: " + iterations );
		
		return visible;
	}
}
