package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import org.bukkit.util.Vector;

public class Corner {
	Region owner;
	Vector vector;
	
	public Corner( Region owner, Vector point ) {
		this.owner = owner;
		this.vector = point.clone();
	}
	
	public Vector getVector() {
		return vector.clone();
	}
	
	public Region getOwner() {
		return owner;
	}
	
	public boolean intersects( Corner corner ) {
		double x = vector.getX();
		double y = vector.getY();
		double z = vector.getZ();
		double height = owner.region.lenY * .5;
		y += height;
		double cx = corner.vector.getX();
		double cy = corner.vector.getY();
		double cz = corner.vector.getZ();
		double cHeight = corner.owner.region.lenY * .5;
		cy += cHeight;
		return cx == x && cz == z && ( cHeight + height ) - Math.abs( y - cy ) >= .6;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vector == null) ? 0 : vector.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Corner other = (Corner) obj;
		if (vector == null) {
			if (other.vector != null)
				return false;
		} else if (!vector.equals(other.vector))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}
}
