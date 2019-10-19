package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.BooleanResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.tracking.GunsmokeEntityTracker;
import io.github.bananapuncher714.operation.gunsmoke.api.tracking.VisibilityController;

public class MinigameVisibilityController implements VisibilityController {
	protected MinigameManager manager;
	
	public MinigameVisibilityController( MinigameManager manager ) {
		this.manager = manager;
	}
	
	@Override
	public BooleanResult isVisible( Player player, GunsmokeEntityTracker tracker ) {
		GunsmokeEntityWrapper gEntity = tracker.getEntity();
		
		Minigame game = manager.belongsTo( gEntity );
		Minigame playerGame = manager.participating( manager.getPlugin().getItemManager().getEntityWrapper( player ) );
		
		return game != playerGame ? BooleanResult.FALSE : BooleanResult.UNSET;
	}

}
