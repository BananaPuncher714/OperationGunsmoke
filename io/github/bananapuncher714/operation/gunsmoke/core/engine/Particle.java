package io.github.bananapuncher714.operation.gunsmoke.core.engine;

import org.bukkit.util.Vector;

public class Particle {
	protected Vector position;
	protected Vector velocity;
	
	public Particle( Vector position ) {
		setPosition( position );
		setVelocity( new Vector( 0, 0, 0 ) );
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition( Vector position ) {
		this.position = position.clone();
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity( Vector velocity ) {
		this.velocity = velocity;
	}
}
