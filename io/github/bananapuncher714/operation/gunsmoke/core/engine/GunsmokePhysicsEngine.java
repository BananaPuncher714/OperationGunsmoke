package io.github.bananapuncher714.operation.gunsmoke.core.engine;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.util.Vector;

public class GunsmokePhysicsEngine {
	public final static double GRAVITY = -9.81;
	
	protected Set< Particle > particles = new HashSet< Particle >();
	protected long tick = 0;
	
	
	
	public static void main( String[] args ) {
		GunsmokePhysicsEngine engine = new GunsmokePhysicsEngine();
		
		engine.addParticle( new Particle( new Vector( 0, 255, 0 ) ) );
		
		boolean step = false;
		
		while( true ) {
			try {
				// Tick
				if ( step ) {
					System.in.read( new byte[ 25565 ] );
				} else {
					Thread.sleep( 10 );
				}
			} catch ( IOException | InterruptedException e ) {
				e.printStackTrace();
			}
			
			engine.tick();
		}
	}
	
	public GunsmokePhysicsEngine() {
		
	}
	
	public void addParticle( Particle particle ) {
		this.particles.add( particle );
	}
	
	private void tick() {
		System.out.println( "TICK START " + tick );
		
		System.out.println();
		
		System.out.println( "Ticking particles" );
		System.out.println( "  Updating forces" );
		for ( Particle particle : particles ) {
			System.out.println( "    Updating " + particle );
			Vector particleVel = particle.getVelocity();
			System.out.println( "    Applying gravity..." );
			Vector newVel = particleVel.clone().add( new Vector( 0, GRAVITY, 0 ) );
			System.out.println( "    Applying air resistance..." );
			newVel.multiply( .97 );
			System.out.println( "      Velocity " + particleVel + "->" + newVel );
			particle.setVelocity( newVel );
		}
		System.out.println( "  Updating positions" );
		for ( Particle particle : particles ) {
			System.out.println( "    Updating " + particle );
			Vector pos = particle.getPosition();
			Vector newPos = pos.clone().add( particle.getVelocity() );
			System.out.println( "      Position " + pos + "->" + newPos );
			particle.setPosition( newPos );
		}
		
		System.out.println( "TICK END " + tick++ );
	}
}
