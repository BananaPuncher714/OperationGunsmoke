package io.github.bananapuncher714.operation.gunsmoke.api.tracking;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VisibilityControllerDistance implements VisibilityController {
	protected double distance;
	protected double distanceSquared;
	
	public VisibilityControllerDistance( double distance ) {
		this.distance = distance;
		distanceSquared = distance * distance;
	}
	
	@Override
	public boolean isVisible( Player player, GunsmokeEntityTracker tracker ) {
		Entity entity = tracker.getEntity().getEntity();
		if ( entity.getWorld() == player.getWorld() && entity.getLocation().distanceSquared( player.getLocation() ) <= distanceSquared ) {
			return true;
		}
		
		return false;
	}
}
