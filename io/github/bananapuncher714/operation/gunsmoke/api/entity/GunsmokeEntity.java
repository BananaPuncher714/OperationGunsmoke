package io.github.bananapuncher714.operation.gunsmoke.api.entity;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityRegenEvent;

public abstract class GunsmokeEntity extends GunsmokeRepresentable implements Tickable {
	protected Location location;
	protected Vector velocity;
	protected double speed;
	protected boolean isInvincible = false;
	protected double health;
	protected double maxHealth;
	
	public GunsmokeEntity() {
	}
	
	public GunsmokeEntity( Location location ) {
		this.location = location.clone();
	}
	
	@Override
	public EnumTickResult tick() {
		if ( health <= 0 ) {
			return EnumTickResult.CANCEL;
		}
		return EnumTickResult.CONTINUE;
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public void setLocation( Location location ) {
		this.location = location.clone();
	}
	
	public Vector getVelocity() {
		return velocity.clone().multiply( speed );
	}
	
	public void setVelocity( Vector vector ) {
		velocity = vector.clone().normalize();
		speed = vector.length();
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed( double speed ) {
		this.speed = speed;
	}

	public boolean isInvincible() {
		return isInvincible;
	}

	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth( double health ) {
		this.health = health;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth( double maxHealth ) {
		this.maxHealth = maxHealth;
	}
	
	// This is only going to go through if the event is not cancelled
	public void damage( GunsmokeEntityDamageEvent event ) {
		health = Math.max( 0, health - event.getDamage() );
	}
	
	public void regen( GunsmokeEntityRegenEvent event ) {
		this.health += Math.min( maxHealth, health + event.getAmount() );
	}
}
