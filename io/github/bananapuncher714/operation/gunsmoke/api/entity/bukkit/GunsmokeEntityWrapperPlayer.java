package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class GunsmokeEntityWrapperPlayer extends GunsmokeEntityWrapperLivingEntity {
	protected Player entity;
	
	public GunsmokeEntityWrapperPlayer( Player entity) {
		super( entity );
		this.entity = entity;
	}

	@Override
	public Player getEntity() {
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
