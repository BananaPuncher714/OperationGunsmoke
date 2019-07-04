package io.github.bananapuncher714.operation.gunsmoke.api.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.events.world.GunsmokeExplosionPrepareEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class GunsmokeExplosion extends GunsmokeRepresentable {
	protected final static Vector[] DIRECTIONS;
	protected final static double SCALE;
	
	protected final static double SCALE_INVERSE;
	protected final static double SCALE_INVERSE_HALF;
	protected final static double SCALE_SQUARED;
	protected final static double SCALE_CUBED;
	
	static {
		// Scale determines how fine the raytracing will be
		// Gets exponentially more expensive the larger SCALE is
		SCALE = 2;
		SCALE_INVERSE = 1 / SCALE;
		SCALE_INVERSE_HALF = SCALE_INVERSE * .5;
		SCALE_SQUARED = SCALE * SCALE;
		SCALE_CUBED = SCALE_SQUARED * SCALE;
		DIRECTIONS = new Vector[ 6 ];
		DIRECTIONS[ 0 ] = new Vector( SCALE_INVERSE, 0, 0 );
		DIRECTIONS[ 1 ] = new Vector( 0, SCALE_INVERSE, 0 );
		DIRECTIONS[ 2 ] = new Vector( 0, 0, SCALE_INVERSE );
		DIRECTIONS[ 3 ] = new Vector( -SCALE_INVERSE, 0, 0 );
		DIRECTIONS[ 4 ] = new Vector( 0, -SCALE_INVERSE, 0 );
		DIRECTIONS[ 5 ] = new Vector( 0, 0, -SCALE_INVERSE );
	}
	
	protected GunsmokeRepresentable exploder;
	
	protected Location center;
	protected Vector offset;
	protected double maxDistance;
	protected double maxDistanceSquared;
	
	protected double power;
	protected double amp;
	protected double frequency;
	
	private boolean exploded = false;
	
	protected Map< Location, Double > damage = new HashMap< Location, Double >();
	protected Map< Location, Double > reduction = new HashMap< Location, Double >();

	// For speed
	protected Set< Location > tempLeads = new HashSet< Location >();
	
	public GunsmokeExplosion( GunsmokeRepresentable exploder, Location center, double maxDistance, double power ) {
		this.exploder = exploder;
		this.center = center.clone();
		this.center.setYaw( 0 );
		this.center.setPitch( 0 );
		this.maxDistance = maxDistance;
		this.maxDistanceSquared = maxDistance * maxDistance;

		setPower( power );
	}
	
	public GunsmokeExplosionResult explode() {
		GunsmokeExplosionPrepareEvent event = new GunsmokeExplosionPrepareEvent( this );
		GunsmokeUtil.callEventSync( event );
		if ( event.isCancelled() ) {
			return null;
		}
		Location roundedCenter = VectorUtil.round( center.clone(), SCALE ).add( -SCALE_INVERSE_HALF, SCALE_INVERSE_HALF, SCALE_INVERSE_HALF );
		double blastReduction = getBlastReductionFor( roundedCenter );
		
		// Debug particles
		roundedCenter.getWorld().spawnParticle( Particle.DRIP_WATER, roundedCenter, 0 );
		
		if ( blastReduction == -1 ) {
		} else {
			blastReduction /= SCALE_CUBED;
			double finPower = power - blastReduction;
			if ( finPower > 0 ) {
				damage.put( roundedCenter, finPower / SCALE_SQUARED );
				Set< Location > leads = new HashSet< Location >();
				leads.add( roundedCenter.clone() );
				while ( explode( leads ) > 0 );
			}
		}
		
		exploded = true;
		
		return new GunsmokeExplosionResult( this );
	}
	
	protected int explode( Set< Location > leads ) {
		tempLeads.clear();
		
		for ( Location location : leads ) {
			for ( Vector vec : DIRECTIONS ) {
				Location newLoc = location.clone().add( vec );
				
				if ( damage.containsKey( newLoc ) ) {
					continue;
				}
				
				if ( center.distanceSquared( newLoc ) > maxDistanceSquared ) {
					continue;
				}

				double blastReduction = getDamageReduction( newLoc, center );
				// Ok, right here is the blast reduction or whatever it is
				
				if ( blastReduction == -1 ) {
					damage.put( newLoc, -1.0 );
				} else {
					blastReduction /= SCALE_CUBED;
					double rawPower = getPowerAt( center.distance( newLoc ) ) / SCALE_SQUARED;
					double power = rawPower - blastReduction;
					damage.put( newLoc, rawPower );
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
	
	public void setMaxDistance( double distance ) {
		this.maxDistance = distance;
		this.maxDistanceSquared = distance * distance;
	}
	
	public void setPower( double power ) {
		this.power = power;
		amp = Math.sqrt( power ) / power;
		frequency = 1 / ( amp * maxDistance );
		amp = 1 / ( amp * amp );
	}
	
	public double getPowerAt( double distance ) {
		double height = distance * frequency;
		return amp - ( height * height );
	}
	
	public boolean hasExploded() {
		return exploded;
	}
	
	protected double getBlastReductionFor( Location location ) {
		return GunsmokeUtil.getBlockAt( location ).getHealth();
	}
	
	public GunsmokeRepresentable getExploder() {
		return exploder;
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
		double n = ( dx + dy + dz ) * SCALE;
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
	
	protected double getDamageAt( Location location ) {
		Location rounded = VectorUtil.round( location.clone(), SCALE ).add( -SCALE_INVERSE_HALF, SCALE_INVERSE_HALF, SCALE_INVERSE_HALF );
		
		return damage.getOrDefault( rounded, 0.0 );
	}
	
	public interface BlockDamageModifier {
		public int getModifiedBlockDamage( Location location, int damage );
	}
}
