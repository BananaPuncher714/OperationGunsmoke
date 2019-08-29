package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;

public class GunsmokeEntityWrapperProjectile extends GunsmokeEntityWrapper {
	protected Projectile entity;
	
	public GunsmokeEntityWrapperProjectile( Projectile entity ) {
		super( entity );
		this.entity = entity;
	}
	
	@Override
	public Projectile getEntity() {
		return entity;
	}
	
	public void onEvent( ProjectileHitEvent event ) {
	}
	
	@Override
	public boolean isInvincible() {
		return true;
	}
	
	@Override
	public void damage( GunsmokeEntityDamageEvent event ) {
		super.damage( event );
		entity.remove();
	}
}
