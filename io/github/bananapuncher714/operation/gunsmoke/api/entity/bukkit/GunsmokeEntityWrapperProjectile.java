package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;

public class GunsmokeEntityWrapperProjectile extends GunsmokeEntityWrapper {
	protected Projectile entity;
	
	public GunsmokeEntityWrapperProjectile( Projectile entity ) {
		super( entity );
		this.entity = entity;
	}
	
	public Projectile getEntity() {
		return entity;
	}
	
	public void onEvent( ProjectileHitEvent event ) {
	}
}
