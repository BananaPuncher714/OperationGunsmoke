package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.List;
import java.util.UUID;
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
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.block.GunsmokeBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
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
		return getPlugin().getProtocol().getHandler().getNearbyEntities( entity, location, vector );
	}
	
	public static void teleportRelative( Player player, Vector vector, double yaw, double pitch ) {
		getPlugin().getProtocol().getHandler().teleportRelative( player.getName(), vector, yaw, pitch );
	}
	
	public static void teleportRelative( String player, Vector vector, double yaw, double pitch ) {
		getPlugin().getProtocol().getHandler().teleportRelative( player, vector, yaw, pitch );
	}
	
	public static Gunsmoke getPlugin() {
		if ( GUNSMOKE_INSTANCE == null ) {
			GUNSMOKE_INSTANCE = Gunsmoke.getPlugin( Gunsmoke.class );
		}
		return GUNSMOKE_INSTANCE;
	}
	
	public static void callEventSync( Event event ) {
		getPlugin().getTaskManager().callEventSync( event );
	}
	
	public static List< CollisionResultBlock > rayTrace( Location start, Vector ray ) {
		return getPlugin().getProtocol().getHandler().rayTrace( start, ray );
	}
	
	public static List< CollisionResultBlock > rayTrace( Location start, Vector ray, double dist ) {
		return getPlugin().getProtocol().getHandler().rayTrace( start, ray, dist );
	}
	
	public static int getCurrentTick() {
		return getPlugin().getProtocol().getHandler().getServerTick();
	}
	
	public static void playHurtAnimationFor( LivingEntity entity ) {
		getPlugin().getProtocol().getHandler().playHurtAnimationFor( entity );
	}
	
	public static boolean damage( GunsmokeEntity entity, DamageType type, double damage, DamageCause cause ) {
		return getPlugin().getEntityManager().damage( entity, damage, type, cause );
	}
	
	public static boolean damage( GunsmokeEntity entity, DamageType type, double damage, GunsmokeEntity damager ) {
		return getPlugin().getEntityManager().damage( entity, damage, type, damager );
	}
	
	public static void setBlockStage( Location location, int stage ) {
		getPlugin().getProtocol().getHandler().damageBlock( location, stage );
	}
	
	public static GunsmokeBlock getBlockAt( Location location ) {
		return getPlugin().getBlockManager().getBlockOrCreate( location );
	}
	
	public static void damageBlockAt( Location location, double damage, GunsmokeRepresentable damager, DamageType type ) {
		getPlugin().getBlockManager().damage( location, damage, damager, type );
	}
	
	public static GunsmokeEntityWrapper getEntity( Entity entity ) {
		return getPlugin().getItemManager().getEntityWrapper( entity );
	}
	
	public void register( GunsmokeRepresentable gunsmokeRepresentable ) {
		getPlugin().getItemManager().register( gunsmokeRepresentable );
	}
	
	public void unregisterGunsmokeRepresentable( UUID uuid ) {
		getPlugin().getItemManager().remove( uuid );
	}
	
	public static void log( String message, Level level ) {
		getPlugin().getLogger().log( level, message );
	}
	
	public static EquipmentSlot[] getEquipmentSlotOrdering() {
		return EQUIPMENT_SLOT_IMPORTANCE;
	}
}
