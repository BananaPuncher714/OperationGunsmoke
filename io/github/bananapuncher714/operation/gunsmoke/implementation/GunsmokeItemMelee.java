package io.github.bananapuncher714.operation.gunsmoke.implementation;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;

public class GunsmokeItemMelee extends GunsmokeItemInteractable {
	
	
	@Override
	public EnumEventResult onClick( LeftClickEntityEvent event ) {
		return super.onClick( event );
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

}
