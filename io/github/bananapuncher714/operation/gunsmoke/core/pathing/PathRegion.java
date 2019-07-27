package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathRegion implements Comparable< PathRegion > {
	protected List< Region > regions = new ArrayList< Region >();
	protected Set< Region > regionChecklist = new HashSet< Region >();
	protected Path optimized;
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
	
	public void setOptimized( Path path ) {
		optimized = path;
		weight = path.getDistance();
	}
	
	public Path getPath() {
		return optimized;
	}
	
	public List< Region > getRegions() {
		return regions;
	}
	
	public Region lastRegion() {
		return regions.get( regions.size() - 1 );
	}
	
	public PathRegion copyOf() {
		PathRegion path = new PathRegion();
		path.regionChecklist.addAll( regionChecklist );
		regions.forEach( path.regions::add );
		return path;
	}
	
	public PathRegion reverse() {
		PathRegion path = new PathRegion();
		path.regionChecklist.addAll( regionChecklist );
		for ( int i = regions.size() - 1; i >= 0; i-- ) {
			path.regions.add( regions.get( i ) );
		}
		return path;
	}
	
	@Override
	public int compareTo( PathRegion o ) {
		if ( weight < o.weight ) {
			return -1;
		} else if ( weight > o.weight ) {
			return 1;
		}
		return this == o ? 0 : 1;
	}
}
