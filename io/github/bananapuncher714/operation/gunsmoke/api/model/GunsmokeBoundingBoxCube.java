package io.github.bananapuncher714.operation.gunsmoke.api.model;

import org.bukkit.util.Vector;

public class GunsmokeBoundingBoxCube extends GunsmokeBoundingBox {
	protected Vector upper;
	protected Vector lower;
	
	public GunsmokeBoundingBoxCube( Vector upper, Vector lower ) {
		this.upper = upper.clone();
		this.lower = lower.clone();
	}
	
	@Override
	public boolean contains( Vector localSpace ) {
		// TODO are the comparison operators right?!
		return localSpace.getX() < upper.getX() &&
				localSpace.getY() < upper.getY() &&
				localSpace.getZ() < upper.getZ() &&
				localSpace.getX() > lower.getX() &&
				localSpace.getY() > lower.getY() &&
				localSpace.getZ() > lower.getZ();
	}

	public Vector getUpper() {
		return upper.clone();
	}

	public void setUpper( Vector upper ) {
		this.upper = upper.clone();
	}

	public Vector getLower() {
		return lower.clone();
	}

	public void setLower( Vector lower ) {
		this.lower = lower.clone();
	}
}
