package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;

/**
 * A minimal minigame concept class
 * 
 * Essentially, a minigame is an isolated instance, like a bungee server but not bungee.
 * They can be in any location or world, and the participants can be anyone.
 */
public abstract class Minigame implements Tickable {
	protected Set< UUID > participants = new HashSet< UUID >();
	protected Set< UUID > entities = new HashSet< UUID >();
	
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
		return participants.contains( entity.getUUID() );
	}
	
	public boolean isRegistered( GunsmokeEntity entity ) {
		return entities.contains( entity.getUUID() );
	}
	
	public boolean join( GunsmokeEntity entity ) {
		if ( entity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gEntity = ( GunsmokeEntityWrapperPlayer ) entity;
			gEntity.getEntity().setScoreboard( scoreboard );
		}
		return true;
	}
	
	public void leave( GunsmokeEntity entity ) {
		if ( entity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gEntity = ( GunsmokeEntityWrapperPlayer ) entity;
			gEntity.getEntity().setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );
		}
	}
	public abstract void start();
	public abstract void stop();
}
