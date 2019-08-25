package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathCorner extends PathRegion {
	protected List< Corner > corners = new ArrayList< Corner >();
	protected Set< Corner > cornerChecklist = new HashSet< Corner >();
	
	protected PathCorner() {}
	
	public PathCorner( Corner corner ) {
		super( corner.getOwner() );
		add( corner );
	}
	
	public void add( Corner region ) {
		if ( !cornerChecklist.contains( region ) ) {
			corners.add( region );
			cornerChecklist.add( region );
		}
	}
	
	public boolean contains( Corner location ) {
		return cornerChecklist.contains( location );
	}
	
	public List< Corner > getCorners() {
		return corners;
	}
	
	public Corner lastCorner() {
		return corners.get( corners.size() - 1 );
	}
	
	public void add( PathCorner region ) {
		super.add( region );
		for ( Corner corner : region.getCorners() ) {
			add( corner );
		}
			
	}
	
	public PathCorner copyOf() {
		PathCorner path = new PathCorner();
		path.regionChecklist.addAll( regionChecklist );
		path.regions.addAll( regions );
		path.cornerChecklist.addAll( cornerChecklist );
		path.corners.addAll( corners );
		return path;
	}
}
