package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.operation.gunsmoke.core.util.FileUtil;

public class MinigameLoader {
	protected MinigameManager manager;
	protected File baseDir;
	
	public MinigameLoader( MinigameManager manager, File baseDir ) {
		this.manager = manager;
		this.baseDir = baseDir;
		baseDir.mkdirs();
	}
	
	protected void delete( String id ) {
		File file = new File( baseDir + "/" + id );
		FileUtil.recursiveDelete( file );
	}
	
	protected void loadMinigames() {
		for ( File file : baseDir.listFiles() ) {
			File dataFile = new File( file + "/" + "minigame_data.yml" );
			if ( !dataFile.exists() ) {
				System.out.println( "Missing data file! Cannot load " + file );
				continue;
			}
			
			FileConfiguration config = YamlConfiguration.loadConfiguration( dataFile );
			String data = config.getString( "id" );
			
			MinigameFactory factory = manager.getFactory( data );
			if ( factory == null ) {
				System.out.println( "Cannot load minigame with type " + data );
				continue;
			}
			
			Minigame game = factory.get( file );
			manager.addMinigame( file.getName(), game );
			System.out.println( "Loaded minigame " + file.getName() + " of type " + data );
		}
	}
	
	protected void saveMinigames() {
		for ( Entry< String, Minigame > entry : manager.getMinigames().entrySet() ) {
			saveMinigame( entry.getKey(), entry.getValue() );
		}
	}
	
	protected void saveMinigame( String id, Minigame game ) {
		File minigameDir = new File( baseDir + "/" + id );
		FileUtil.recursiveDelete( minigameDir );
		minigameDir.mkdirs();
		
		String minigameType = null;
		
		for ( Entry< String, MinigameFactory > entry : manager.getFactories().entrySet() ) {
			MinigameFactory factory = entry.getValue();
			if ( factory.save( minigameDir, game ) ) {
				minigameType = entry.getKey();
				break;
			}
		}
		
		if ( minigameType == null ) {
			throw new IllegalArgumentException( "The given factory for " + id + " does not exist! Cannot save!" );
		}
		
		File dataFile = new File( minigameDir + "/" + "minigame_data.yml" );
		
		if ( !dataFile.exists() ) {
			try {
				dataFile.createNewFile();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration( dataFile );
		config.set( "id", minigameType );
		
		try {
			config.save( dataFile );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		System.out.println( "Saved minigame " + id + " of type " + minigameType );
	}
}
