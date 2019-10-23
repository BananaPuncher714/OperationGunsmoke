package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.core.util.GeneralUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class Path {
	protected List< Vector > waypoints = new ArrayList< Vector >();
	protected Set< Vector > checklist = new HashSet< Vector >();
	protected List< Double > distanceList = new ArrayList< Double >();
	protected double distance = -1;
	
	protected Path() {}
	
	public Path( Vector start ) {
		addLocation( start );
	}
	
	public void addLocation( Vector location ) {
		if ( checklist.contains( location ) ) {
			return;
		}
		if ( waypoints.size() > 0 ) {
			Vector last = last();
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
	
	public void add( Path path ) {
		for ( Vector waypoint : path.waypoints ) {
			addLocation( waypoint );
		}
	}
	
	public List< Vector > getWaypoints() {
		return new ArrayList< Vector >( waypoints );
	}

	public Vector last() {
		return waypoints.get( waypoints.size() - 1 ).clone();
	}
	
	public Vector popLast() {
		Vector last = waypoints.remove( waypoints.size() - 1 );
		checklist.remove( last );
		distanceList.remove( distanceList.size() - 1 );
		distance = -1;
		return last;
	}
	
	public boolean contains( Vector location ) {
		return checklist.contains( location );
	}
	
	public double getDistance() {
		double sum = 0;
		for ( int i = 0; i < waypoints.size() - 2; i++ ) {
			Vector prev = waypoints.get( i );
			Vector next = waypoints.get( i + 1 );
			
			if ( next.getY() < prev.getY() ) {
				sum += VectorUtil.distance( prev.getX(), prev.getZ(), next.getX(), next.getZ() );
			} else {
				sum += prev.distance( next );
			}
		}
		
		return sum;
	}
	
	public double getDistanceOld() {
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
	
	public void reverseSelf() {
		GeneralUtil.reverseList( waypoints );
		GeneralUtil.reverseList( distanceList );
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
