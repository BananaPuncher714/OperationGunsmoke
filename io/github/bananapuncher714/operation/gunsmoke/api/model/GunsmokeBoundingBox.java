package io.github.bananapuncher714.operation.gunsmoke.api.model;

import org.bukkit.util.Vector;

public abstract class GunsmokeBoundingBox {
	/**
	 * Determines whether or not this bounding box contains the given vector, in local space, with rotation of 0
	 * 
	 * @param localSpace
	 * The offset from the center
	 * @return
	 * Whether or not the point is contained
	 */
	public abstract boolean contains( Vector localSpace );
}
