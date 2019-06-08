package io.github.bananapuncher714.operation.gunsmoke.api.item;

import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;

public abstract class GunsmokeItemInteractable extends GunsmokeItem {
	public EnumInteractResult onClick( AdvancementOpenEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( DropItemEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( PlayerSwapHandItemsEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( LeftClickEntityEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( LeftClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( RightClickEntityEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( RightClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( HoldRightClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	public EnumInteractResult onClick( ReleaseRightClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
}
