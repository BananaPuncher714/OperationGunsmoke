package io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTarget;

public class GunsmokeBullet extends GunsmokeProjectile {
	protected double power;
	
	public GunsmokeBullet( Location location ) {
		super( location );
		power = 0;
	}

	@Override
	public void hit( ProjectileTarget target ) {
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
}
