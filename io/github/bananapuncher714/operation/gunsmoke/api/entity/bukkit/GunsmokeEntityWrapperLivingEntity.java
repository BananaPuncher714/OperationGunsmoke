package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.entity.LivingEntity;

public class GunsmokeEntityWrapperLivingEntity extends GunsmokeEntityWrapper {
	protected LivingEntity entity;
	
	public GunsmokeEntityWrapperLivingEntity( LivingEntity entity ) {
		super( entity );
		this.entity = entity;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}
	
	@Override
	public boolean isInvincible() {
		return super.isInvincible() || entity.isInvulnerable();
	}
}
