package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.entity.LivingEntity;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityRegenEvent;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class GunsmokeEntityWrapperLivingEntity extends GunsmokeEntityWrapper {
	protected LivingEntity entity;
	
	public GunsmokeEntityWrapperLivingEntity( LivingEntity entity ) {
		super( entity );
		this.entity = entity;
		this.health = entity.getHealth();
		this.maxHealth = entity.getMaxHealth();
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}
	
	@Override
	public boolean isInvincible() {
		return super.isInvincible() || entity.isInvulnerable();
	}
	
	@Override
	public void damage( GunsmokeEntityDamageEvent event ) {
		super.damage( event );
		
		entity.setHealth( Math.ceil( health ) );
		GunsmokeUtil.playHurtAnimationFor( entity );
	}
	
	@Override
	public void regen( GunsmokeEntityRegenEvent event ) {
		super.regen( event );
		entity.setHealth( health );
	}
}
