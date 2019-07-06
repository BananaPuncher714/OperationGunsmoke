package io.github.bananapuncher714.operation.gunsmoke.api.entity;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;

public abstract class GunsmokeEntity extends GunsmokeRepresentable implements Tickable {
	protected Location location;
	protected Vector velocity;
	protected double speed;
	protected boolean isInvincible = false;
	
	public GunsmokeEntity() {
	}
	
	public GunsmokeEntity( Location location ) {
		this.location = location.clone();
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public void setLocation( Location location ) {
		this.location = location.clone();
	}
	
	public Vector getVelocity() {
		return velocity.clone().multiply( speed );
	}
	
	public void setVelocity( Vector vector ) {
		velocity = vector.clone().normalize();
		speed = vector.length();
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed( double speed ) {
		this.speed = speed;
	}

	public boolean isInvincible() {
		return isInvincible;
	}

	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}
}
