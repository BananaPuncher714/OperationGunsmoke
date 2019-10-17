package io.github.bananapuncher714.operation.gunsmoke.api.tracking;

import org.bukkit.entity.Player;

public interface VisibilityController {
	boolean isVisible( Player player, GunsmokeEntityTracker tracker );
}
