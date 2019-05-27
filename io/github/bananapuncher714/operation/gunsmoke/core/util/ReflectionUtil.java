package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;

public class ReflectionUtil {
	public static final String VERSION;
	
	static {
		VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	public static PacketHandler getNewPacketHandlerInstance() {
		try {
			Class< ? > clazz = Class.forName( "io.github.bananapuncher714.operation.gunsmoke.core.implementation." + VERSION + ".NMSHandler" );
			return ( PacketHandler ) clazz.newInstance();
		} catch ( ClassNotFoundException | InstantiationException | IllegalAccessException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void set( Object object, String name, Object value ) {
		try {
			Field field = object.getClass().getField( name );
			field.setAccessible( true );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
}
