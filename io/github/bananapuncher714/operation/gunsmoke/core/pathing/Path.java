package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class Path {
	protected List< Location > waypoints = new ArrayList< Location >();
	protected Set< Location > checklist = new HashSet< Location >();
	protected List< Double > distanceList = new ArrayList< Double >();
	protected double distance = -1;
	
	protected Path() {}
	
	public Path( Location start ) {
		addLocation( start );
	}
	
	public void addLocation( Location location ) {
		if ( waypoints.size() > 0 ) {
			Location last = last();
			double dist;
			if ( location.getY() < last.getY() ) {
				dist = VectorUtil.distance( location.getX(), location.getZ(), last.getX(), last.getZ() );
			} else {
				dist = location.distance( last );
			}
			
			distanceList.add( dist );
		}
		waypoints.add( location );
		checklist.add( location );
		distance = -1;
	}
	
	public List< Location > getWaypoints() {
		return new ArrayList< Location >( waypoints );
	}

	public Location last() {
		return waypoints.get( waypoints.size() - 1 ).clone();
	}
	
	public Location popLast() {
		Location last = waypoints.remove( waypoints.size() - 1 );
		checklist.remove( last );
		distanceList.remove( distanceList.size() - 1 );
		distance = -1;
		return last;
	}
	
	public boolean contains( Location location ) {
		return checklist.contains( location );
	}
	
	public double getDistance() {
		if ( distance != -1 ) {
			return distance;
		}
		double sum = 0;
		for ( double val : distanceList ) {
			sum += val;
		}
		distance = sum;
		return sum;
	}
	
	public Path copyOf() {
		Path path = new Path();
		path.checklist.addAll( checklist );
		path.waypoints.addAll( waypoints );
		path.distanceList.addAll( distanceList );
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
