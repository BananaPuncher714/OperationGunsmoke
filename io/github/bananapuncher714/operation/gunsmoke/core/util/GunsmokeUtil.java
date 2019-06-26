package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResult;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class GunsmokeUtil {
	private static Gunsmoke GUNSMOKE_INSTANCE;
	
	private static final EquipmentSlot[] EQUIPMENT_SLOT_IMPORTANCE;
	
	static {
		EQUIPMENT_SLOT_IMPORTANCE = new EquipmentSlot[ EquipmentSlot.values().length ];
		EQUIPMENT_SLOT_IMPORTANCE[ 0 ] = EquipmentSlot.HAND;
		EQUIPMENT_SLOT_IMPORTANCE[ 1 ] = EquipmentSlot.OFF_HAND;
		EQUIPMENT_SLOT_IMPORTANCE[ 2 ] = EquipmentSlot.HEAD;
		EQUIPMENT_SLOT_IMPORTANCE[ 3 ] = EquipmentSlot.CHEST;
		EQUIPMENT_SLOT_IMPORTANCE[ 4 ] = EquipmentSlot.LEGS;
		EQUIPMENT_SLOT_IMPORTANCE[ 5 ] = EquipmentSlot.FEET;
	}
	
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
	
	public static void teleportRelative( String player, Vector vector, double yaw, double pitch ) {
		plugin().getProtocol().getHandler().teleportRelative( player, vector, yaw, pitch );
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
	
	public static CollisionResult rayTrace( Location start, Vector ray ) {
		return plugin().getProtocol().getHandler().rayTrace( start, ray );
	}
	
	public static CollisionResult rayTrace( Location start, Vector ray, double dist ) {
		return plugin().getProtocol().getHandler().rayTrace( start, ray, dist );
	}
	
	public static int getCurrentTick() {
		return plugin().getProtocol().getHandler().getServerTick();
	}
	
	public static void playHurtAnimationFor( LivingEntity entity ) {
		plugin().getProtocol().getHandler().playHurtAnimationFor( entity );
	}
	
	public static boolean damage( GunsmokeEntity entity, DamageType type, double damage, DamageCause cause ) {
		return plugin().getEntityManager().damage( entity, damage, type, cause );
	}
	
	public static boolean damage( GunsmokeEntity entity, DamageType type, double damage, GunsmokeEntity damager ) {
		return plugin().getEntityManager().damage( entity, damage, type, damager );
	}
	
	public static double getBlockHealth( Location location ) {
		return plugin().getBlockManager().getHealthAt( location );
	}
	
	public static void setBlockHealth( Location location, double health ) {
		plugin().getBlockManager().setHealthAt( location, health );
	}
	
	public static GunsmokeEntityWrapper getEntity( Entity entity ) {
		return plugin().getItemManager().getEntityWrapper( entity );
	}
	
	public static void log( String message, Level level ) {
		plugin().getLogger().log( level, message );
	}
	
	public static EquipmentSlot[] getEquipmentSlotOrdering() {
		return EQUIPMENT_SLOT_IMPORTANCE;
	}
}
