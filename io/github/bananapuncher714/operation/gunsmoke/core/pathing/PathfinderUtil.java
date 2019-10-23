package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class PathfinderUtil {
	
	/**
	 * The goal of this class is to get all regions visible, as well as the visible vector to see if it's valid
	 * A vector doesn't count as visible if all we can see is one corner, maybe?
	 */
	public static Set< RegionResult > getVisibleRegionsOf( Vector start, Region startRegion ) {
		Set< RegionResult > results = new HashSet< RegionResult >();
		
		// First add our current region as a valid path
		{
			PathRegion startingPath = new PathRegion( startRegion );
			RegionResult result = new RegionResult();
			result.path = startingPath;
			results.add( result );
		}
		
		Queue< RegionResult > activeResults = new ArrayDeque< RegionResult >();
		PathRegion startPath = new PathRegion( startRegion );
		RegionResult startResult = new RegionResult();
		startResult.path = startPath;
		startResult.origin = start.clone();
		activeResults.add( startResult );
		
		while ( !activeResults.isEmpty() ) {
			RegionResult result = activeResults.poll();
			PathRegion currentPath = result.path;
			Region currentRegion = currentPath.lastRegion();
			ComparableVec currentVec = result.fov;
			
			for ( Region neighbor : currentRegion.getNeighbors().keySet() ) {
				Edge edge = currentRegion.getNeighbors().get( neighbor );
				AABB edgeBox = edge.getIntersection();
				
				// Get the 2d edge representation
				Vector boxMax = new Vector( edgeBox.maxX, edgeBox.minY, edgeBox.maxZ );
				Vector boxMin = new Vector( edgeBox.minX, edgeBox.minY, edgeBox.minZ );
				
				// Get the vectors to the box
				Vector toMax = boxMax.clone().subtract( start ).setY( 0 ).normalize();
				Vector toMin = boxMin.clone().subtract( start ).setY( 0 ).normalize();
			}
			
		}
		
		
		return results;
	}
	
	
	public static class RegionResult {
		public PathRegion path;
		public ComparableVec fov;
		public Vector origin;
	}
}
