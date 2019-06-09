package io.github.bananapuncher714.operation.gunsmoke.api.item;

import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;

public abstract class GunsmokeItemInteractable extends GunsmokeItem {
	public EnumEventResult onClick( AdvancementOpenEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( DropItemEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( PlayerSwapHandItemsEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( LeftClickEntityEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( LeftClickEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( RightClickEntityEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( RightClickEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( HoldRightClickEvent event ) {
		return EnumEventResult.SKIPPED;
	}
	
	public EnumEventResult onClick( ReleaseRightClickEvent event ) {
		return EnumEventResult.SKIPPED;
	}
}
