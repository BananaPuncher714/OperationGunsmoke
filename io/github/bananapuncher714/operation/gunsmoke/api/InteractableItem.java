package io.github.bananapuncher714.operation.gunsmoke.api;

import io.github.bananapuncher714.operation.gunsmoke.api.player.events.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.events.RightClickEvent;

public interface InteractableItem {
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
