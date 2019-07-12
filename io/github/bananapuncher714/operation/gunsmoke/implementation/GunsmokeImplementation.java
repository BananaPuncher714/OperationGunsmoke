package io.github.bananapuncher714.operation.gunsmoke.implementation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.core.util.FileUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmorOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBulletOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.world.ConfigExplosion;

public class GunsmokeImplementation {
	private static GunsmokeImplementation INSTANCE;
	
	private static File BULLET_FOLDER;
	private static File EXPLOSION_FOLDER;
	private static File WEAPON_FOLDER;
	private static File ARMOR_FOLDER;
	
	protected Gunsmoke plugin;
	
	protected Map< String, ConfigBulletOptions > bullets = new HashMap< String, ConfigBulletOptions >();
	protected Map< String, ConfigExplosion > explosions = new HashMap< String, ConfigExplosion >();
	protected Map< String, ConfigWeaponOptions > guns = new HashMap< String, ConfigWeaponOptions >();
	protected Map< String, ConfigArmorOptions > armor = new HashMap< String, ConfigArmorOptions >();
	
	public GunsmokeImplementation( Gunsmoke plugin ) {
		INSTANCE = this;
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents( new EventListener(), plugin );
		
		BULLET_FOLDER = new File( plugin.getDataFolder() + "/" + "bullets/" );
		EXPLOSION_FOLDER = new File( plugin.getDataFolder() + "/" + "explosions/" );
		WEAPON_FOLDER = new File( plugin.getDataFolder() + "/" + "weapons/" );
		ARMOR_FOLDER = new File( plugin.getDataFolder() + "/" + "armor/" );
		
		GunsmokeCommand command = new GunsmokeCommand();
		plugin.getCommand( "gunsmoke" ).setExecutor( command );
		plugin.getCommand( "gunsmoke" ).setTabCompleter( command );
		
		init();
	}
	
	private void init() {
		FileUtil.saveToFile( plugin.getResource( "data/bullets/example_bullet.yml" ), new File( BULLET_FOLDER + "/" + "example_bullet.yml" ), false );
		FileUtil.saveToFile( plugin.getResource( "data/explosions/example_explosion.yml" ), new File( EXPLOSION_FOLDER + "/" + "example_explosion.yml" ), false );
		FileUtil.saveToFile( plugin.getResource( "data/guns/example_gun.yml" ), new File( WEAPON_FOLDER + "/" + "example_gun.yml" ), false );
		FileUtil.saveToFile( plugin.getResource( "data/armor/example_armor.yml" ), new File( ARMOR_FOLDER + "/" + "example_armor.yml" ), false );
		
		loadExplosions();
		loadBullets();
		loadGuns();
		loadArmor();
	}
	
	private void loadExplosions() {
		for ( File file : EXPLOSION_FOLDER.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			String id = file.getName().replaceAll( "\\.yml$", "" );
			ConfigExplosion explosion = new ConfigExplosion( config );
			
			System.out.println( "Loaded explosion " + id );
			explosions.put( id, explosion );
		}
	}
	
	private void loadBullets() {
		for ( File file : BULLET_FOLDER.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			String id = file.getName().replaceAll( "\\.yml$", "" );
			ConfigBulletOptions options = new ConfigBulletOptions( config );
			
			System.out.println( "Loaded bullet " + id );
			bullets.put( id, options );
		}
	}
	
	private void loadGuns() {
		for ( File file : WEAPON_FOLDER.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			String id = file.getName().replaceAll( "\\.yml$", "" );
			ConfigWeaponOptions options = new ConfigWeaponOptions( config );
			
			System.out.println( "Loaded gun " + id );
			guns.put( id, options );
		}
	}
	
	private void loadArmor() {
		for ( File file : ARMOR_FOLDER.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			String id = file.getName().replaceAll( "\\.yml$", "" );
			ConfigArmorOptions options = new ConfigArmorOptions( config );
			
			System.out.println( "Loaded armor " + id );
			armor.put( id, options );
		}
	}
	
	public ConfigBulletOptions getBullet( String id ) {
		return bullets.get( id );
	}
	
	public ConfigExplosion getExplosion( String id ) {
		return explosions.get( id );
	}
	
	public ConfigWeaponOptions getGun( String id ) {
		return guns.get( id );
	}
	
	public ConfigArmorOptions getArmor( String id ) {
		return armor.get( id );
	}
	
	public static GunsmokeImplementation getInstance() {
		return INSTANCE;
	}
}
