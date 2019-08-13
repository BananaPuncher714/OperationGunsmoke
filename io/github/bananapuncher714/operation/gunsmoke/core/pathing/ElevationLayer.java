package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ElevationLayer {
	double level;
	Set< Region > regions = new HashSet< Region >();
	Map< Region, Set< ElevationEdge > > edges = new HashMap< Region, Set< ElevationEdge > >();
	
	public void add( Region region ) {
		regions.add( region );
		edges.putIfAbsent( region, new HashSet< ElevationEdge >() );
	}
	
	public double getLevel() {
		return level;
	}
	
	public boolean contains( Region region ) {
		return regions.contains( region );
	}
	
	public Set< ElevationEdge > getEdgesFor( Region region ) {
		return edges.get( region );
	}

	public Set< ElevationEdge > getEdges() {
		Set< ElevationEdge > edgeSet = new HashSet< ElevationEdge >();
		for ( Set< ElevationEdge > set : edges.values() ) {
			edgeSet.addAll( set );
		}
		return edgeSet;
	}
	
	public Set< Region > getRegions() {
		return regions;
	}
}
