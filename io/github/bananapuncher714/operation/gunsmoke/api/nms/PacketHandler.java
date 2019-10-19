package io.github.bananapuncher714.operation.gunsmoke.api.nms;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;
import io.github.bananapuncher714.operation.gunsmoke.api.tracking.GunsmokeEntityTracker;
import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.api.util.CollisionResultBlock;
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
	
	void tick();
	
	void update( LivingEntity entity, boolean main );
	void update( LivingEntity entity, boolean main, boolean updateSelf );
	void update( LivingEntity entity, EquipmentSlot slot );
	
	void teleportRelative( String player, Vector vector, double yaw, double pitch );
	
	void set( HumanEntity player, boolean down );
	void setAir( Player player, int ticks );
	void setTint( Player player, double tint );
	void playHurtAnimationFor( LivingEntity entity );
	
	boolean isCurrentThreadMain();
	
	void damageBlock( Location location, int stage );
	
	// TODO remove
	void darkness( Player player );
	
	int getServerTick();
	
	List< CollisionResultBlock > rayTrace( Location start, Vector ray );
	List< CollisionResultBlock > rayTrace( Location start, Vector ray, double dist );
	
	List< Entity > getNearbyEntities( Entity entity, Location location, Vector vector );
	
	boolean isRealPlayer( Player player );
	GunsmokeNPC getNPC( Player player );
	
	void display( Player player );
	AABB[] getBoxesFor( Location location );
	
	GunsmokeEntityTracker getEntityTrackerFor( Entity entity );
	
	NBTCompound getPlayerCompound( Player player );
	void setPlayerCompound( Player player, NBTCompound compound );
	
//	void update( LivingEntity entity );
//	void updateBow( LivingEntity entity );
//	void updateBow( LivingEntity entity, Player viewer );
//	void sendMessage( Player player, String message, Display display );
//	void teleportRelative( String player, Vector location, double yaw, double pitch );
//	void playParticle( Player player, boolean everyoneElse, Particle particle, boolean farView, Location location, float dx, float dy, float dz, float speed, int count, int... params );
//	void playBlockCrack( Location location, int level );
//	void setFOV( Player player, float value );
}
