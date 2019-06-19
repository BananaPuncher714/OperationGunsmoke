package io.github.bananapuncher714.operation.gunsmoke.api.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class GunsmokeExplosion {
	protected final static Vector[] DIRECTIONS;
	protected final static double SCALE;
	
	private final static double SCALE_INVERSE;
	
	static {
		SCALE = 2;
		SCALE_INVERSE = 1 / SCALE;
		DIRECTIONS = new Vector[ 6 ];
		DIRECTIONS[ 0 ] = new Vector( 1 / SCALE, 0, 0 );
		DIRECTIONS[ 1 ] = new Vector( 0, 1 / SCALE, 0 );
		DIRECTIONS[ 2 ] = new Vector( 0, 0, 1 / SCALE );
		DIRECTIONS[ 3 ] = new Vector( -1 / SCALE, 0, 0 );
		DIRECTIONS[ 4 ] = new Vector( 0, -1 / SCALE, 0 );
		DIRECTIONS[ 5 ] = new Vector( 0, 0, -1 / SCALE );
	}
	
	protected Location center;
	protected double maxDistance;
	protected double maxDistanceSquared;
	
	protected double power;
	protected double amp;
	protected double frequency;
	
	protected Map< Location, Double > damage = new HashMap< Location, Double >();
	protected Map< Location, Double > reduction = new HashMap< Location, Double >();

	// For speed
	protected Set< Location > tempLeads = new HashSet< Location >();
	
	public GunsmokeExplosion( Location center, double maxDistance, double power ) {
		this.center = center.clone();
		this.maxDistance = maxDistance;
		this.maxDistanceSquared = maxDistance * maxDistance;
		this.power = power;

		amp = Math.sqrt( power ) / power;
		frequency = amp * maxDistance;
		amp = 1 / ( amp * amp );
	}
	
	public Map< Location, Double > explode() {
		double blastReduction = getBlastReductionFor( center );
		double finPower = power - blastReduction;
		if ( blastReduction == -1 || finPower <= 0 ) {
			// Clearly the explosion spawned inside an invincible block
			// Or it isn't strong enough
			damage.put( center, 0.0 );
		} else {	
			damage.put( center, finPower );
			Set< Location > leads = new HashSet< Location >();
			leads.add( center );
			while ( explode( leads ) > 0 );
		}
		
		double cubed = SCALE * SCALE * SCALE;
		Map< Location, Double > locations = new HashMap< Location, Double >();
		for ( Location location : damage.keySet() ) {
			double power = damage.get( location ) / cubed;

			Location block = new Location( location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ() );

			if ( locations.containsKey( block ) ) {
				locations.put( block, power + locations.get( block ) );
			} else {
				locations.put( block, power );
			}
		}
		
		return locations;
	}
	
	protected int explode( Set< Location > leads ) {
		tempLeads.clear();
		
		for ( Location location : leads ) {
			for ( Vector vec : DIRECTIONS ) {
				// Not sure how slow cloning is
				Location newLoc = location.clone().add( vec );
				
				if ( damage.containsKey( newLoc ) ) {
					continue;
				}
				
				if ( center.distanceSquared( newLoc ) > maxDistanceSquared ) {
					continue;
				}

				double blastReduction = getDamageReduction( newLoc, center );
				
				if ( blastReduction == -1 ) {
					damage.put( newLoc, 0.0 );
				} else {
					// The distance calculation may be a large source of lag
					double power = getPowerAt( center.distance( newLoc ) ) - blastReduction;
					damage.put( newLoc, Math.max( 0, power ) );
					if ( power > 0 ) {
						tempLeads.add( newLoc );
					}
				}
			}
		}
		
		leads.clear();
		leads.addAll( tempLeads );
		
		return tempLeads.size();
	}
	
	public double getPowerAt( double distance ) {
		double height = distance / frequency;
		return amp - ( height * height );
	}
	
	public double getBlastReductionFor( Location location ) {
		switch ( location.getBlock().getType() ) {
		case BEDROCK:
		case OBSIDIAN: return -1;
		case STONE: return 5;
		case DIRT:
		case GRASS: return 2;
		default: return 0;
		}
	}
	
	protected double getDamageReduction( Location start, Location end ) {
		// Get the coords
		double px1 = start.getX();
		double py1 = start.getY();
		double pz1 = start.getZ();
		
		double px2 = end.getX();
		double py2 = end.getY();
		double pz2 = end.getZ();
		
		// Get the width and height difference
		double dx = Math.abs( px1 - px2 );
		double dy = Math.abs( py1 - py2 );
		double dz = Math.abs( pz1 - pz2 );
	    
		double x = px1;
		double y = py1;
		double z = pz1;
		double n = 1 + ( dx + dy + dz ) * SCALE;
		double x_inc = (px2 > px1) ? SCALE_INVERSE : -SCALE_INVERSE;
		double y_inc = (py2 > py1) ? SCALE_INVERSE : -SCALE_INVERSE;
		double z_inc = (pz2 > pz1) ? SCALE_INVERSE : -SCALE_INVERSE;
		double errorxy = dx - dy;
		double errorxz = dx - dz;
		double errorzy = dz - dy;
	    dx *= 2;
	    dy *= 2;
	    dz *= 2;

	    double totalResis = 0;
	    for (; n > 0; --n) {
	    	Location newLocation = new Location( start.getWorld(), NumberConversions.floor( x ), NumberConversions.floor( y ), NumberConversions.floor( z ) );
	    	double resistance = reduction.getOrDefault( newLocation, -2.0 );
	    	if ( resistance == -2 ) {
	    		resistance = getBlastReductionFor( newLocation );
	    		reduction.put( newLocation, resistance );
	    	}
	    	if ( resistance == -1 ) {
	    		return -1;
	    	}
	    	totalResis += resistance;
	    	
	    	if ( errorxy > 0 && errorxz > 0 ) {
	        	x += x_inc;
	        	errorxy -= dy;
	        	errorxz -= dz;
	        } else if ( errorxz <= 0 && errorzy > 0 ) {
	        	z += z_inc;
	        	errorxz += dx;
	        	errorzy -= dy;
	        } else {
	        	y += y_inc;
	        	errorxy += dx;
	        	errorzy += dz;
	        }
	    }
	    return totalResis;
	}
}
