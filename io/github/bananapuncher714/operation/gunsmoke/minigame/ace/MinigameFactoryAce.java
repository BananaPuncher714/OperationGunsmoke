package io.github.bananapuncher714.operation.gunsmoke.minigame.ace;

import java.io.File;
import java.io.FileNotFoundException;

import org.bukkit.command.CommandSender;

import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.GunsmokeFileManager;
import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;
import io.github.bananapuncher714.operation.gunsmoke.minigame.base.Minigame;
import io.github.bananapuncher714.operation.gunsmoke.minigame.base.MinigameFactory;

public class MinigameFactoryAce implements MinigameFactory {
	protected Gunsmoke plugin;
	
	public MinigameFactoryAce( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	@Override
	public Minigame get( CommandSender sender, String[] args ) {
		return new Ace( plugin, new AceSettings() );
	}
	
	@Override
	public Minigame get( File baseDir ) {
		AceSettings settings = new AceSettings();;
		
		File redFolder = new File( baseDir + "/" + "teams" + "/" + "red" + "/" + "spawns" );
		if ( redFolder.exists() ) {
			for ( File file : redFolder.listFiles() ) {
				settings.addRedSpawn( GunsmokeFileManager.load( file ) );
			}
		}
		
		File blueFolder = new File( baseDir + "/" + "teams" + "/" + "blue" + "/" + "spawns" );
		if ( blueFolder.exists() ) {
			for ( File file : blueFolder.listFiles() ) {
				settings.addBlueSpawn( GunsmokeFileManager.load( file ) );
			}
		}
		
		return new Ace( plugin, settings );
	}
	
	@Override
	public boolean save( File baseDir, Minigame game ) {
		if ( game instanceof Ace ) {
			Ace ace = ( Ace ) game;
			AceSettings settings = ace.getSettings();
			
			File redFolder = new File( baseDir + "/" + "teams" + "/" + "red" + "/" + "spawns" );
			redFolder.mkdirs();
			
			for ( PlayerSaveData data : settings.getRedSpawns() ) {
				File saveFolder = new File( redFolder + "/" + data.hashCode() );
				GunsmokeFileManager.save( data, saveFolder );
			}
				
			File blueFolder = new File( baseDir + "/" + "teams" + "/" + "blue" + "/" + "spawns" );
			blueFolder.mkdirs();
			
			for ( PlayerSaveData data : settings.getBlueSpawns() ) {
				File saveFolder = new File( blueFolder + "/" + data.hashCode() );
				GunsmokeFileManager.save( data, saveFolder );
			}
			
			return true;
		} else {
			return false;
		}
	}
}