package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import org.bukkit.util.Vector;

// Made for 2d calculations only!!!
// Min and max don't really have any meaning apart from being 2 bounds
public class ComparableVec {
	protected Vector min;
	protected Vector max;
	protected Vector mid;
	private double dot;
	
	public ComparableVec( Vector min, Vector max ) {
		this.min = min.clone().setY( 0 );
		this.max = max.clone().setY( 0 );
		recalculate();
	}
	
	// Returns 0 if within min and max, -1 if less than min, and 1 if greater than max
	public int getTowards( Vector vec ) {
		double midDot = mid.dot( vec );
		if ( midDot >= dot ) {
			return 0;
		}
		// Watch out for -0.5366845635742735, 0.0, 0.8437829574132742
		if ( min.equals( vec ) || max.equals( vec ) ) {
			return 0;
		}
		double minDot = min.dot( vec );
		double maxDot = max.dot( vec );
		return minDot > maxDot ? -1 : 1;
	}
	
	private void recalculate() {
		mid = min.clone().midpoint( max ).normalize();
		dot = mid.dot( min );
	}
	
	public void setMin( Vector min ) {
		this.min = min;
		recalculate();
	}
	
	public void setMax( Vector max ) {
		this.max = max;
		recalculate();
	}
	
	public Vector getMin() {
		return min.clone();
	}
	
	public Vector getMax() {
		return max.clone();
	}
	
	public Vector getMid() {
		return mid.clone();
	}
	
	public double dot() {
		if ( min.equals( max ) ) {
			return 1;
		}
		return min.dot( max );
	}
	
	public static Vector max( Vector vec1, Vector vec2 ) {
		return vec1.getX() + vec1.getZ() > vec2.getX() + vec2.getZ() ? vec1 : vec2; 
	}
}
