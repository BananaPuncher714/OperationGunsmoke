package io.github.bananapuncher714.operation.gunsmoke.api.events.world;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosion;

public abstract class GunsmokeExplosionEvent extends Event implements Cancellable {
	protected GunsmokeExplosion explosion;
	
	public GunsmokeExplosionEvent( GunsmokeExplosion explosion ) {
		this.explosion = explosion;
	}
	
	public GunsmokeExplosion getExplosion() {
		return explosion;
	}
}
