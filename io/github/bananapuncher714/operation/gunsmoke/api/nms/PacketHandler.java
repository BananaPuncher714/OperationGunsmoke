package io.github.bananapuncher714.operation.gunsmoke.api.nms;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public interface PacketHandler {
	enum Display {
		CHAT( ( byte ) 0 ), SYSTEM( ( byte ) 1 ), ACTIONBAR( ( byte ) 2 );
		public final byte location;
		private Display( byte loc ) {
			location = loc;
		}
	}
	void setGunsmoke( Gunsmoke plugin );
	Object onPacketInterceptOut( Player player, Object packet );
	Object onPacketInterceptIn( Player player, Object packet );
	
	void update( LivingEntity entity, boolean main );
	void update( LivingEntity entity, boolean main, boolean updateSelf );
	void update( LivingEntity entity, EquipmentSlot slot );
	
	void teleportRelative( String player, Vector vector, double yaw, double pitch );
	
	void set( Player player, boolean down );
	void setAir( Player player, int ticks );
	
	boolean isCurrentThreadMain();
	
	void darkness( Player player );
	
	void hurt( LivingEntity entity );
	
	int getServerTick();
	
	Location rayTrace( Location start, Vector ray );
	Location rayTrace( Location start, Vector ray, double dist );
	
	List< Entity > getNearbyEntities( Entity entity, Location location, Vector vector );
	
//	void update( LivingEntity entity );
//	void updateBow( LivingEntity entity );
//	void updateBow( LivingEntity entity, Player viewer );
//	void sendMessage( Player player, String message, Display display );
//	void teleportRelative( String player, Vector location, double yaw, double pitch );
//	void playParticle( Player player, boolean everyoneElse, Particle particle, boolean farView, Location location, float dx, float dy, float dz, float speed, int count, int... params );
//	void playBlockCrack( Location location, int level );
//	void setFOV( Player player, float value );
}
