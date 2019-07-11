package io.github.bananapuncher714.operation.gunsmoke.api;

import org.bukkit.event.entity.EntityRegainHealthEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;

public interface InteractableDamage {
	EnumEventResult onTakeDamage( GunsmokeEntityDamageEvent event );
	default EnumEventResult onEvent( EntityRegainHealthEvent event ) {
		return EnumEventResult.SKIPPED;
	}
}
