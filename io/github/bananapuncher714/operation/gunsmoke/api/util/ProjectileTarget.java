package io.github.bananapuncher714.operation.gunsmoke.api.util;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;

public abstract class ProjectileTarget implements Comparable< ProjectileTarget > {
	protected CollisionResult intersection;
	protected double distance = -1;
	protected double distanceSquared;
	
	public ProjectileTarget( GunsmokeProjectile projectile, CollisionResult intersection ) {
		distanceSquared = projectile.getLocation().distanceSquared( intersection.getLocation() );
		this.intersection = intersection;
	}
	
	public CollisionResult getIntersection() {
		return intersection;
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
