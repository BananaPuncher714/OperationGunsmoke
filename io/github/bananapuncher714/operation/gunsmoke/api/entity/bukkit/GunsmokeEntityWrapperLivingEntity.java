package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import org.bukkit.entity.LivingEntity;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
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
	public double getHealth() {
		return health;
	}

	@Override
	public void setHealth( double health ) {
		super.setHealth( health );
		double clientHp = ( health * entity.getMaxHealth() ) / maxHealth;
		if ( clientHp == 0 && health != 0 ) {
			clientHp = 1;
		}
		entity.setHealth( clientHp );
	}

	
	@Override
	public void damage( GunsmokeEntityDamageEvent event ) {
		super.damage( event );
		
		GunsmokeUtil.playHurtAnimationFor( entity );
	}
}
