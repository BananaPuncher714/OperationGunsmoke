package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public abstract class ProjectileTarget implements Comparable< ProjectileTarget > {
	protected CollisionResult intersection;
	protected Location projectileOrigin;
	protected double distance = -1;
	protected double distanceSquared;
	
	public ProjectileTarget( GunsmokeProjectile projectile, CollisionResult intersection ) {
		projectileOrigin = projectile.getLocation().clone();
		distanceSquared = projectileOrigin.distanceSquared( intersection.getLocation() );
		this.intersection = intersection;
	}
	
	public CollisionResult getIntersection() {
		return intersection;
	}
	
	// TODO Determine if this is needed
	protected void updateDistance() {
		distance = -1;
		distanceSquared = projectileOrigin.distanceSquared( intersection.getLocation() );
	}
	
	public double getDistance() {
		if ( distance == -1 ) {
			distance = Math.sqrt( distanceSquared );
		}
		return distance;
	}
	
	public double getDistanceSquared() {
		return distanceSquared;
	}

	@Override
	public int compareTo( ProjectileTarget other ) {
		if ( other.getDistanceSquared() > distanceSquared ) {
			return -1;
		} else if ( other.getDistanceSquared() < distanceSquared ) {
			return 1;
		} else {
			int hash = hashCode();
			int otherHash = other.hashCode();
			if ( hash == otherHash ) {
				return 0;
			} else {
				return other.hashCode() > hashCode() ? -1 : 1;
			}
		}
	}
}
