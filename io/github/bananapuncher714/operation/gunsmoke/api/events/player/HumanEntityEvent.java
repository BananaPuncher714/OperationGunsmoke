package io.github.bananapuncher714.operation.gunsmoke.api.events.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityEvent;

public abstract class HumanEntityEvent extends EntityEvent {
	protected HumanEntity entity;
	
	public HumanEntityEvent( HumanEntity entity ) {
		super( entity );
		this.entity = entity;
	}

	@Override
	public HumanEntity getEntity() {
		return entity;
	}
}
