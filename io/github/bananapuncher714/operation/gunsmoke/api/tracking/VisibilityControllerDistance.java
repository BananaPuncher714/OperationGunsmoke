package io.github.bananapuncher714.operation.gunsmoke.api.tracking;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.BooleanResult;

public class VisibilityControllerDistance implements VisibilityController {
	protected double distance;
	protected double distanceSquared;
	
	public VisibilityControllerDistance( double distance ) {
		this.distance = distance;
		distanceSquared = distance * distance;
	}
	
	@Override
	public BooleanResult isVisible( Player player, GunsmokeEntityTracker tracker ) {
		Entity entity = tracker.getEntity().getEntity();
		if ( entity.getWorld() == player.getWorld() && entity.getLocation().distanceSquared( player.getLocation() ) <= distanceSquared ) {
			return BooleanResult.TRUE;
		}
		
		return BooleanResult.FALSE;
	}
}
