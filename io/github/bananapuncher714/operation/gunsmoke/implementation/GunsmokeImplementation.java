package io.github.bananapuncher714.operation.gunsmoke.implementation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.FileUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBulletOptions;

public class GunsmokeImplementation {
	private static GunsmokeImplementation INSTANCE;
	
	private static File BULLET_FOLDER;
	private static File EXPLOSION_FOLDER;
	private static File WEAPON_FOLDER;
	
	protected Gunsmoke plugin;
	
	protected Map< String, ConfigBulletOptions > bullets = new HashMap< String, ConfigBulletOptions >();
	protected Map< String, ConfigExplosion > explosions = new HashMap< String, ConfigExplosion >();
	
	public GunsmokeImplementation( Gunsmoke plugin ) {
		INSTANCE = this;
		this.plugin = plugin;
		
		BULLET_FOLDER = new File( plugin.getDataFolder() + "/" + "bullets/" );
		EXPLOSION_FOLDER = new File( plugin.getDataFolder() + "/" + "explosions/" );
		WEAPON_FOLDER = new File( plugin.getDataFolder() + "/" + "weapons/" );
		
		init();
	}
	
	private void init() {
		FileUtil.saveToFile( plugin.getResource( "data/bullets/" ), BULLET_FOLDER, false );
		FileUtil.saveToFile( plugin.getResource( "data/explosions/" ), EXPLOSION_FOLDER, false );
		FileUtil.saveToFile( plugin.getResource( "data/guns/" ), WEAPON_FOLDER, false );
		
		loadExplosions();
	}
	
	private void loadExplosions() {
		for ( File file : EXPLOSION_FOLDER.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			String id = file.getName().replaceAll( "\\.yml$", "" );
			ConfigExplosion explosion = new ConfigExplosion( config );
			
			explosions.put( id, explosion );
		}
	}
	
	public static GunsmokeImplementation getInstance() {
		return INSTANCE;
	}
}
