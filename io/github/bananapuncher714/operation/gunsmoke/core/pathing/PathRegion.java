package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathRegion implements Comparable< PathRegion > {
	protected List< Region > regions = new ArrayList< Region >();
	protected Set< Region > regionChecklist = new HashSet< Region >();
	protected double weight;
	
	protected PathRegion() {}
	
	public PathRegion( Region start ) {
		add( start );
	}
	
	public void add( Region region ) {
		regions.add( region );
		regionChecklist.add( region );
	}
	
	public boolean contains( Region location ) {
		return regionChecklist.contains( location );
	}
	
	public List< Region > getRegions() {
		return regions;
	}
	
	public Region lastRegion() {
		return regions.get( regions.size() - 1 );
	}
	public PathRegion copyOf() {
		PathRegion path = new PathRegion();
		regionChecklist.addAll( path.regionChecklist );
		regions.forEach( path.regions::add );
		return path;
	}

	@Override
	public int compareTo( PathRegion o ) {
		if ( weight < o.weight ) {
			return -1;
		} else if ( weight > o.weight ) {
			return 1;
		}
		return 0;
	}
}
