package io.github.bananapuncher714.operation.gunsmoke.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.NBTCompound;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class GunsmokeFileManager {
	protected Gunsmoke plugin;
	protected File baseDir;
	
	public GunsmokeFileManager( Gunsmoke plugin, File baseDir ) {
		this.plugin = plugin;
		this.baseDir = baseDir;
		
		baseDir.mkdirs();
	}
	
	public File getBaseFile() {
		return baseDir;
	}
	
	public static PlayerSaveData load( File baseDir ) {
		Validate.isTrue( baseDir.exists(), "Base directory does not exist!" );
		Validate.isTrue( baseDir.isDirectory(), "Player save data directory must be a directory!" );
		
		File file = new File( baseDir + "/" + "data.yml" );
		FileConfiguration yamlData = YamlConfiguration.loadConfiguration( file );
		
		File compoundFile = new File( baseDir + "/" + "data.nbt" );
	
		
		NBTCompound compound;
		try {
			compound = GunsmokeUtil.getPlugin().getProtocol().getHandler().loadNBTCompound( new FileInputStream( compoundFile ) );
		} catch ( IOException e ) {
			e.printStackTrace();
			return null;
		}
		
		int slot = yamlData.getInt( "slot" );
		GameMode mode = GameMode.valueOf( yamlData.getString( "gamemode" ) );
		String world = yamlData.getString( "location.world" );
		double x = yamlData.getDouble( "location.x" );
		double y = yamlData.getDouble( "location.y" );
		double z = yamlData.getDouble( "location.z" );
		double yaw = yamlData.getDouble( "location.yaw" );
		double pitch = yamlData.getDouble( "location.pitch" );
		Location location = new Location( Bukkit.getWorld( world ), x, y, z, ( float ) yaw, ( float ) pitch );

		return new PlayerSaveData( compound, mode, location, slot );
	}
	
	public static void save( PlayerSaveData data, File baseDir ) {
		baseDir.mkdirs();
		File file = new File( baseDir + "/" + "data.yml" );
		File compoundFile = new File( baseDir + "/" + "data.nbt" );
		try {
			file.createNewFile();
			compoundFile.createNewFile();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		FileConfiguration yamlData = YamlConfiguration.loadConfiguration( file );
		
		yamlData.set( "slot", data.getSlot() );
		yamlData.set( "gamemode", data.getGamemode().name() );
		Location location = data.getLocation();
		yamlData.set( "location.world", location.getWorld().getName() );
		yamlData.set( "location.x", location.getX() );
		yamlData.set( "location.y", location.getY() );
		yamlData.set( "location.z", location.getZ() );
		yamlData.set( "location.yaw", location.getYaw() );
		yamlData.set( "location.pitch", location.getPitch() );
		
		try {
			GunsmokeUtil.getPlugin().getProtocol().getHandler().saveNBTCompound( data.getCompound(), new FileOutputStream( compoundFile ) );
			yamlData.save( file );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
