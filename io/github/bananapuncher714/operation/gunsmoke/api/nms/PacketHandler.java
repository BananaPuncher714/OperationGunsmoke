package io.github.bananapuncher714.operation.gunsmoke.api.nms;

import org.bukkit.Location;
import org.bukkit.Particle;
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
	boolean onPacketInterceptOut( Player player, Object packet );
	boolean onPacketInterceptIn( Player player, Object packet );
	
	void update( LivingEntity entity, boolean main );
	void update( LivingEntity entity, EquipmentSlot slot );
	
	
//	void update( LivingEntity entity );
//	void updateBow( LivingEntity entity );
//	void updateBow( LivingEntity entity, Player viewer );
//	void sendMessage( Player player, String message, Display display );
//	void teleportRelative( String player, Vector location, double yaw, double pitch );
//	void playParticle( Player player, boolean everyoneElse, Particle particle, boolean farView, Location location, float dx, float dy, float dz, float speed, int count, int... params );
//	void playBlockCrack( Location location, int level );
//	void setFOV( Player player, float value );
}
