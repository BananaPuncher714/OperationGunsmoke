package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

public class Path {
	protected List< Location > waypoints = new ArrayList< Location >();
	protected Set< Location > checklist = new HashSet< Location >();
	
	protected Path() {}
	
	public Path( Location start ) {
		addLocation( start );
	}
	
	public void addLocation( Location location ) {
		waypoints.add( location );
		checklist.add( location );
	}
	
	public List< Location > getWaypoints() {
		return new ArrayList< Location >( waypoints );
	}

	public Location last() {
		return waypoints.get( waypoints.size() - 1 ).clone();
	}
	
	public boolean contains( Location location ) {
		return checklist.contains( location );
	}
	
	public int getDistance() {
		return waypoints.size();
	}
	
	public Path copyOf() {
		Path path = new Path();
		waypoints.forEach( path::addLocation );
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((waypoints == null) ? 0 : waypoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (waypoints == null) {
			if (other.waypoints != null)
				return false;
		} else if (!waypoints.equals(other.waypoints))
			return false;
		return true;
	}
}
