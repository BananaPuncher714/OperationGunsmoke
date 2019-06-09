package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class GunsmokeUtil {
	private static Gunsmoke GUNSMOKE_INSTANCE;
	
	public static void flash( LivingEntity player ) {
		player.addPotionEffect( new PotionEffect( PotionEffectType.NIGHT_VISION, 2, 0, true, false ) );
		player.addPotionEffect( new PotionEffect( PotionEffectType.BLINDNESS, 4, 0, true, false ) );
	}
	
	public static List< Entity > getNearbyEntities( Entity entity, Location location, Vector vector ) {
		return plugin().getProtocol().getHandler().getNearbyEntities( entity, location, vector );
	}
	
	public static void teleportRelative( Player player, Vector vector, double yaw, double pitch ) {
		plugin().getProtocol().getHandler().teleportRelative( player.getName(), vector, yaw, pitch );
	}
	
	private static Gunsmoke plugin() {
		if ( GUNSMOKE_INSTANCE == null ) {
			GUNSMOKE_INSTANCE = Gunsmoke.getPlugin( Gunsmoke.class );
		}
		return GUNSMOKE_INSTANCE;
	}
	
	public static void callEventSync( Event event ) {
		plugin().getTaskManager().callEventSync( event );
	}
	
	public static Location rayTrace( Location start, Vector ray ) {
		return plugin().getProtocol().getHandler().rayTrace( start, ray );
	}
	
	public static Location rayTrace( Location start, Vector ray, double dist ) {
		return plugin().getProtocol().getHandler().rayTrace( start, ray, dist );
	}
}
