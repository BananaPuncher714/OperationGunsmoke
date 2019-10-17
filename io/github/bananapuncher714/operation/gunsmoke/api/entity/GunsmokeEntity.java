package io.github.bananapuncher714.operation.gunsmoke.api.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
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
	
	public boolean isValid() {
		return health > 0;
	}
	
	// This is only going to go through if the event is not cancelled
	public void damage( GunsmokeEntityDamageEvent event ) {
		setHealth( Math.max( 0, health - event.getDamage() ) );
	}
	
	public void regen( GunsmokeEntityRegenEvent event ) {
		setHealth( Math.min( maxHealth, health + event.getAmount() ) );
	}
	
	public void deseralize( ConfigurationSection section ) {
		World world = Bukkit.getWorld( section.getString( "location.world" ) );
		double x = section.getDouble( "location.x" );
		double y = section.getDouble( "location.y" );
		double z = section.getDouble( "location.z" );
		double yaw = section.getDouble( "location.yaw" ) ;
		double pitch = section.getDouble( "location.pitch" );
		
		double velX = section.getDouble( "velocity.x" );
		double velY = section.getDouble( "velocity.y" );
		double velZ = section.getDouble( "velocity.z" );
		double speed = section.getDouble( "velocity.length" );
		
		double health = section.getDouble( "health" );
		double maxHealth = section.getDouble( "max-health" );
		
		boolean invincible = section.getBoolean( "invincible" );
		
		location = new Location( world, x, y, z, ( float ) yaw, ( float ) pitch );
		velocity = new Vector( velX, velY, velZ );
		this.speed = speed;
		this.isInvincible = invincible;
		this.maxHealth = maxHealth;
		this.health = health;
	}

	public void serialize( ConfigurationSection section ) {
		section.set( "location.world", location.getWorld().getName() );
		section.set( "location.x", location.getX() );
		section.set( "location.y", location.getY() );
		section.set( "location.z", location.getZ() );
		section.set( "location.yaw", location.getYaw() );
		section.set( "location.pitch", location.getPitch() );
		
		section.set( "velocity.x", velocity.getX() );
		section.set( "velocity.y", velocity.getY() );
		section.set( "velocity.z", velocity.getZ() );
		section.set( "velocity.speed", speed );
		
		section.set( "health", health );
		section.set( "max-health", maxHealth );

		section.set( "invincible", isInvincible );
	}
}
