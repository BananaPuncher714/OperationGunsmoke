package io.github.bananapuncher714.operation.gunsmoke.implementation;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;

public abstract class GunsmokeItemMelee extends GunsmokeItemInteractable {
	protected double damage = 0;
	
	public GunsmokeItemMelee( double damage ) {
		this.damage = damage;
	}
	
	public double getDamage() {
		return damage;
	}
	
	@Override
	public EnumEventResult onClick( LeftClickEntityEvent event ) {
		event.setCancelled( true );
		return super.onClick( event );
	}

}
