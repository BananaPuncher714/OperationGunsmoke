package io.github.bananapuncher714.operation.gunsmoke.api.item;

import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;

public interface InteractableItem {
	default EnumInteractResult onClick( AdvancementOpenEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	default EnumInteractResult onClick( DropItemEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	default EnumInteractResult onClick( LeftClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	default EnumInteractResult onClick( RightClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	default EnumInteractResult onClick( HoldRightClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
	
	default EnumInteractResult onClick( ReleaseRightClickEvent event ) {
		return EnumInteractResult.SKIPPED;
	}
}
