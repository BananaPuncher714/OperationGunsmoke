package io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.Nameable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

public class GunsmokeEntityWrapper extends GunsmokeEntity implements Nameable {
	protected Entity entity;
	
	public GunsmokeEntityWrapper( Entity entity ) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	@Override
	public Location getLocation() {
		return entity.getLocation();
	}
	
	@Override
	public void setLocation( Location location ) {
		entity.teleport( location );
	}
	
	@Override
	public Vector getVelocity() {
		return entity.getVelocity();
	}
	
	@Override
	public void setVelocity( Vector vector ) {
		entity.setVelocity( vector );
	}

	@Override
	public UUID getUUID() {
		return entity.getUniqueId();
	}

	public void despawn() {
	}
	
	public void unload() {
	}
	
	@Override
	public void remove() {
	}

	@Override
	public EnumTickResult tick() {

		return super.tick();
	}
	
	@Override
	public boolean isValid() {
		return entity.isValid();
	}
	
	public EnumEventResult onEvent( EntityDamageEvent event ) {
		return EnumEventResult.SKIPPED;
	}

	@Override
	public String getName() {
		return entity.getCustomName() == null ? entity.getName() : entity.getCustomName();
	}

	@Override
	public void setName( String name ) {
		entity.setCustomName( name );
	}
}
