package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.BooleanResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
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
		
		if ( game != playerGame ) {
			return BooleanResult.FALSE;
		}
		
		if ( gEntity instanceof GunsmokeEntityWrapperPlayer ) {
			Player trackerPlayer = ( ( GunsmokeEntityWrapperPlayer ) gEntity ).getEntity();
			Location location = player.getLocation();
			Location trackerLoc = trackerPlayer.getLocation();
			
			if ( location.getWorld() != trackerLoc.getWorld() ) {
				return BooleanResult.UNSET;
			}
			return location.distanceSquared( trackerLoc ) < 250 * 250 ? BooleanResult.TRUE : BooleanResult.FALSE;
		} else {
			return BooleanResult.UNSET;
		}
	}

}
