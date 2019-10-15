package io.github.bananapuncher714.operation.gunsmoke.api.world;

import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

public interface GunsmokeEntityTracker {
	GunsmokeEntity getEntity();
	int getTrackingDistance();
	int getChunkRange();
	boolean isDeltaTracking();
	Set< Player > getPlayers();
	
	void track( Player player );
	void trackPlayers( List< Player > players );
	void update( Player player );
	void update();
	void untrack( Player player );
}
