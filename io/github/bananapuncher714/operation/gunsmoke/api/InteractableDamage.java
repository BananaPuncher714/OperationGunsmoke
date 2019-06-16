package io.github.bananapuncher714.operation.gunsmoke.api;

import org.bukkit.event.entity.EntityDamageEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;

public interface InteractableDamage {
	void onEvent( GunsmokeEntityDamageEvent event );
	void onEvent( EntityDamageEvent event );
}
