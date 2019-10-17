package io.github.bananapuncher714.operation.gunsmoke.api.tracking;

import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;

public interface GunsmokeEntityTracker {
	GunsmokeEntityWrapper getEntity();
	int getTrackingDistance();
	int getChunkRange();
	boolean isDeltaTracking();
	Set< Player > getPlayers();
	
	void track( Player player );
	void trackPlayers( List< Player > players );
	void update( Player player );
	void update();
	void untrack( Player player );
	
	void setVisibilityController( VisibilityController controller );
	VisibilityController getVisiblityController();
}
