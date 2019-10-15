package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

/**
 * A minimal minigame concept class
 * 
 * Essentially, a minigame is an isolated instance, like a bungee server but not bungee.
 * They can be in any location or world, and the participants can be anyone.
 */
public abstract class Minigame {
	public abstract boolean canParticipate( GunsmokeEntity entity );
	
	public abstract boolean join( GunsmokeEntity entity );
	public abstract boolean leave( GunsmokeEntity entity );
	
	public abstract void shutdown();
}
