package io.github.bananapuncher714.operation.gunsmoke.minigame.ace;

import java.util.ArrayList;
import java.util.List;

import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;

public class AceSettings {
	protected List< PlayerSaveData > redSpawns;
	protected List< PlayerSaveData > blueSpawns;
	
	public AceSettings() {
		redSpawns = new ArrayList< PlayerSaveData >();
		blueSpawns = new ArrayList< PlayerSaveData >();
	}

	public List< PlayerSaveData > getRedSpawns() {
		return redSpawns;
	}

	public List< PlayerSaveData > getBlueSpawns() {
		return blueSpawns;
	}
	
	public void addRedSpawn( PlayerSaveData redSpawn ) {
		redSpawns.add( redSpawn );
	}
	
	public void addBlueSpawn( PlayerSaveData blueSpawn ) {
		blueSpawns.add( blueSpawn );
	}
}
