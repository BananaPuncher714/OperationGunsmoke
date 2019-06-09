package io.github.bananapuncher714.operation.gunsmoke.api.events.entity;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.events.GunsmokeEvent;

public abstract class GunsmokeEntityEvent extends GunsmokeEvent {
	protected GunsmokeEntity entity;
	
	public GunsmokeEntityEvent( GunsmokeEntity entity ) {
		super( entity );
		this.entity = entity;
	}

	@Override
	public GunsmokeEntity getRepresentable() {
		return entity;
	}
}