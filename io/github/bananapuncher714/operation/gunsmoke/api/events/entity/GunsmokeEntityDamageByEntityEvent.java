package io.github.bananapuncher714.operation.gunsmoke.api.events.entity;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

public class GunsmokeEntityDamageByEntityEvent extends GunsmokeEntityDamageEvent {
	protected GunsmokeEntity damager;
	
	public GunsmokeEntityDamageByEntityEvent( GunsmokeEntity entity, DamageType type, double amount, GunsmokeEntity damager ) {
		super( entity, type, amount, DamageCause.ENTITY_ATTACK );
		this.damager = damager;
	}
	
	public GunsmokeEntity getDamager() {
		return damager;
	}
}
