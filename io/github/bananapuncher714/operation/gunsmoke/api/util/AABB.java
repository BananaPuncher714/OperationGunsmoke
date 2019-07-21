package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.util.BoundingBox;

public class AABB {
	public final double maxX;
	public final double maxY;
	public final double maxZ;
	public final double minX;
	public final double minY;
	public final double minZ;
	public final double oriX;
	public final double oriY;
	public final double oriZ;
	public final double radX;
	public final double radY;
	public final double radZ;
	public final double lenX;
	public final double lenY;
	public final double lenZ;
	
	public AABB( double maxX, double maxY, double maxZ, double minX, double minY, double minZ ) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.oriX = ( maxX + minX ) * .5;
		this.oriY = ( maxY + minY ) * .5;
		this.oriZ = ( maxZ + minZ ) * .5;
		this.radX = oriX - minX;
		this.radY = oriY - minY;
		this.radZ = oriZ - minZ;
		this.lenX = radX * 2;
		this.lenY = radY * 2;
		this.lenZ = radZ * 2;
	}
	
	public AABB( BoundingBox box ) {
		this( box.getMaxX(), box.getMaxY(), box.getMaxZ(), box.getMinX(), box.getMinY(), box.getMinZ() );
	}

	public AABB shift( double x, double y, double z ) {
		return new AABB( maxX + x, maxY + y, maxZ + z, minX + x, minY + y, minZ + z );
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(maxX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		AABB other = (AABB) obj;
		if (Double.doubleToLongBits(maxX) != Double.doubleToLongBits(other.maxX))
			return false;
		if (Double.doubleToLongBits(maxY) != Double.doubleToLongBits(other.maxY))
			return false;
		if (Double.doubleToLongBits(maxZ) != Double.doubleToLongBits(other.maxZ))
			return false;
		if (Double.doubleToLongBits(minX) != Double.doubleToLongBits(other.minX))
			return false;
		if (Double.doubleToLongBits(minY) != Double.doubleToLongBits(other.minY))
			return false;
		if (Double.doubleToLongBits(minZ) != Double.doubleToLongBits(other.minZ))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AABB{[" + minX + "," + minY + "," + minZ + "]->[" + maxX + "," + maxY + "," + maxZ + "]}";
	}
}
