package io.github.bananapuncher714.operation.gunsmoke.api.tracking;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.BooleanResult;

public interface VisibilityController {
	BooleanResult isVisible( Player player, GunsmokeEntityTracker tracker );
}
