package io.github.bananapuncher714.operation.gunsmoke.api;

import org.bukkit.event.entity.EntityDamageEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;

public interface InteractableDamage {
	EnumEventResult onEvent( GunsmokeEntityDamageEvent event );
	EnumEventResult onEvent( EntityDamageEvent event );
}
