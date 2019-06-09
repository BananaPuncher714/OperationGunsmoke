package io.github.bananapuncher714.operation.gunsmoke.api.entity;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;

public abstract class GunsmokeEntity extends GunsmokeRepresentable implements Tickable {
	protected Location location;
	protected Vector velocity;

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
		return velocity.clone();
	}
	
	public void setVelocity( Vector vector ) {
		velocity = vector.clone();
	}
}
