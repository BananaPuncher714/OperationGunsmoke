package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

public class GunsmokeEntityWrapperHumanEntity extends GunsmokeEntityWrapperLivingEntity {
	protected HumanEntity entity;
	
	public GunsmokeEntityWrapperHumanEntity( HumanEntity entity) {
		super( entity );
		this.entity = entity;
	}

	@Override
	public HumanEntity getEntity() {
		return entity;
	}
	
	@Override
	public void remove() {
	}
	
	public boolean isTracking( Entity entity ) {
		return false;
	}
	
	public void setTracking( Entity entity, boolean track ) {
		
	}
	
	@Override
	public boolean isInvincible() {
		return super.isInvincible() || entity.getGameMode() == GameMode.CREATIVE || entity.getGameMode() == GameMode.SPECTATOR;
	}
}
