package io.github.bananapuncher714.operation.gunsmoke.api.model;

import org.bukkit.util.Vector;

public abstract class GunsmokeModel {
	protected double yaw;
	protected double pitch;
	
	/**
	 * Whether or not this model contains the given point
	 * 
	 * @param localSpace
	 * @return
	 */
	public abstract boolean contains( Vector localSpace );

	public double getYaw() {
		return yaw;
	}

	public void setYaw( double yaw ) {
		this.yaw = yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch( double pitch ) {
		this.pitch = pitch;
	}
}
