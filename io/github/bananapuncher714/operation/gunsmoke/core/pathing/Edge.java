package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class Edge {
	Region r1;
	Region r2;
	AABB intersection;
	Vector normal;
	
	Set< Vector > points = new HashSet< Vector >();
	
	public Edge( Region region1, Region region2 ) {
		this.r1 = region1;
		this.r2 = region2;
		calculatePoints();
	}
	
	protected void calculatePoints() {
		AABB region1 = r1.getRegion();
		AABB region2 = r2.getRegion();
		points.clear();
		if ( VectorUtil.touching( region1, region2 ) ) {
			intersection = new AABB( Math.min( region1.maxX, region2.maxX ),
					Math.min( region1.maxY, region2.maxY ),
					Math.min( region1.maxZ, region2.maxZ ),
					Math.max( region1.minX, region2.minX ),
					Math.max( region1.minY, region2.minY ),
					Math.max( region1.minZ, region2.minZ ) );
			
			points.add( new Vector( intersection.maxX, intersection.maxY, intersection.maxZ ) );
			points.add( new Vector( intersection.maxX, intersection.maxY, intersection.minZ ) );
			points.add( new Vector( intersection.maxX, intersection.minY, intersection.maxZ ) );
			points.add( new Vector( intersection.maxX, intersection.minY, intersection.minZ ) );
			points.add( new Vector( intersection.minX, intersection.maxY, intersection.maxZ ) );
			points.add( new Vector( intersection.minX, intersection.maxY, intersection.minZ ) );
			points.add( new Vector( intersection.minX, intersection.minY, intersection.maxZ ) );
			points.add( new Vector( intersection.minX, intersection.minY, intersection.minZ ) );
			
			normal = new Vector( intersection.maxX == intersection.minX ? 1 : 0, intersection.maxY == intersection.minY ? 1 : 0, intersection.maxZ == intersection.minZ ? 1 : 0 );
		}
	}
	
	public Vector getClosestPoint( Vector origin, Vector ray ) {
		Vector point = VectorUtil.calculateVector( new Vector( intersection.maxX, intersection.maxY, intersection.maxZ ), normal, origin, ray );
		if ( point != null ) {
			return VectorUtil.closestPoint( intersection, point );
		}
		return VectorUtil.closestPoint( intersection, origin );
	}
	
	public boolean intersects( Vector origin, Vector ray ) {
		Vector point = VectorUtil.calculateVector( new Vector( intersection.maxX, intersection.maxY, intersection.maxZ ), normal, origin, ray );
		if ( point != null ) {
			return Math.abs( point.getX() - intersection.oriX ) <= intersection.radX &&
					Math.abs( point.getY() - intersection.oriY ) <= intersection.radY &&
					Math.abs( point.getZ() - intersection.oriZ ) <= intersection.radZ;
		}
		return false;
	}
	
	public AABB getIntersection() {
		return intersection;
	}
	
	public Set< Vector > getPoints() {
		return points;
	}
}
