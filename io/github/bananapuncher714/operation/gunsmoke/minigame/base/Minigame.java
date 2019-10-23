package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A minimal minigame concept class
 * 
 * Essentially, a minigame is an isolated instance, like a bungee server but not bungee.
 * They can be in any location or world, and the participants can be anyone.
 */
public abstract class Minigame implements Tickable {
	protected final Gunsmoke plugin;
	protected File baseDir;
	
	protected Set< UUID > participants = new HashSet< UUID >();
	protected Set< UUID > entities = new HashSet< UUID >();
	
	protected Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	
	public Minigame( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	protected final void finalStart( File baseDir ) {
		this.baseDir = baseDir;
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
		participants.add( entity.getUUID() );
		return true;
	}
	
	public void leave( GunsmokeEntity entity ) {
		if ( entity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gEntity = ( GunsmokeEntityWrapperPlayer ) entity;
			gEntity.getEntity().setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );
		}
		participants.remove( entity.getUUID() );
	}
	
	public void broadcast( String message ) {
		for ( UUID uuid : participants ) {
			GunsmokeRepresentable representable = plugin.getItemManager().get( uuid );
			if ( representable instanceof GunsmokeEntity ) {
				GunsmokeEntity entity = ( GunsmokeEntity ) representable;
				
				entity.sendMessage( message );
			}
		}
	}
	
	public void broadcast( BaseComponent component ) {
		for ( UUID uuid : participants ) {
			GunsmokeRepresentable representable = plugin.getItemManager().get( uuid );
			if ( representable instanceof GunsmokeEntity ) {
				GunsmokeEntity entity = ( GunsmokeEntity ) representable;
				
				entity.sendMessage( component );
			}
		}
	}
	
	public abstract void start();
	public abstract void stop();
	
	public void onCommand( CommandSender sender, String[] args ) {
	}
}
