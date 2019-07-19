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
	
	@Override
	public String toString() {
		return "AABB{[" + minX + "," + minY + "," + minZ + "]->[" + maxX + "," + maxY + "," + maxZ + "]}";
	}
}
