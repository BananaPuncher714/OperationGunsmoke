package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;

/**
 * A minimal minigame concept class
 * 
 * Essentially, a minigame is an isolated instance, like a bungee server but not bungee.
 * They can be in any location or world, and the participants can be anyone.
 */
public abstract class Minigame implements Tickable {
	protected Set< GunsmokeEntity > participants = new HashSet< GunsmokeEntity >();
	protected Set< GunsmokeEntity > entities = new HashSet< GunsmokeEntity >();
	
	protected Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	
	protected final void finalStart() {
		start();
	}
	
	protected final void finalStop() {
		stop();
	}
	
	@Override
	public EnumTickResult tick() {
		return EnumTickResult.CONTINUE;
	}
	
	public boolean isParticipating( GunsmokeEntity entity ) {
		return participants.contains( entity );
	}
	
	public boolean isRegistered( GunsmokeEntity entity ) {
		return entities.contains( entity );
	}
	public abstract boolean join( GunsmokeEntity entity );
	public abstract void leave( GunsmokeEntity entity );
	public abstract void start();
	public abstract void stop();
}
