package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeEntityTracker;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityTrackerEntry;
import net.minecraft.server.v1_14_R1.PlayerChunkMap;
import net.minecraft.server.v1_14_R1.PlayerChunkMap.EntityTracker;

public class CustomEntityTracker extends EntityTracker implements GunsmokeEntityTracker {
	private static Field TRACKING_ENTRY;
	
	static {
		try {
			TRACKING_ENTRY = EntityTracker.class.getDeclaredField( "trackerEntry" );
			TRACKING_ENTRY.setAccessible( true );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	private Entity entity;
	private int chunkRange;
	private int trackingRange;
	private boolean deltaTracking;
	private EntityTrackerEntry trackerEntry;
	
	public CustomEntityTracker( PlayerChunkMap playerChunkMap, Entity entity, int chunkRange, int trackingRange, boolean deltaTracking ) {
		playerChunkMap.super( entity, chunkRange, trackingRange, deltaTracking );
		this.entity = entity;
		this.chunkRange = chunkRange;
		this.trackingRange = trackingRange;
		this.deltaTracking = deltaTracking;

		try {
			trackerEntry = ( EntityTrackerEntry ) TRACKING_ENTRY.get( this );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public GunsmokeEntity getEntity() {
		return GunsmokeUtil.getEntity( entity.getBukkitEntity() );
	}

	@Override
	public int getTrackingDistance() {
		return trackingRange;
	}
	
	@Override
	public int getChunkRange() {
		return chunkRange;
	}
	
	@Override
	public boolean isDeltaTracking() {
		return deltaTracking;
	}

	@Override
	public Set< Player > getPlayers() {
		Set< Player > players = new HashSet< Player >();
		for ( EntityPlayer player : trackedPlayers ) {
			players.add( player.getBukkitEntity() );
		}
		return players;
	}

	@Override
	public void track( Player player ) {
		update( player );
	}

	@Override
	public void trackPlayers( List< Player > players ) {
		for ( Player player : players ) {
			track( player );
		}
	}
	
	@Override
	public void updatePlayer( EntityPlayer player ) {
		// NMS method
		
		// trackerEntry.a is remove
		// trackerEntry.b is add
		// Don't forget to remove the entity's id from the player's remove queue if the entity is visible
		
		Player bukkitPlayer = player.getBukkitEntity();
		org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
		
		if ( bukkitEntity != bukkitPlayer && bukkitPlayer.getWorld() == bukkitEntity.getWorld() ) {
			if ( bukkitPlayer.getLocation().distanceSquared( bukkitEntity.getLocation() ) <= 100 ) {
				player.removeQueue.remove( Integer.valueOf( entity.getId() ) );
				if ( trackedPlayers.add( player ) ) {
					trackerEntry.b( player );
				}
			} else {
				if ( this.trackedPlayers.remove( player ) ) {
					trackerEntry.a( player );
				}
			}
		} else {
			if ( this.trackedPlayers.remove( player ) ) {
				trackerEntry.a( player );
			}
		}
	}
	
	@Override
	public void update( Player player ) {
		updatePlayer( ( ( CraftPlayer ) player ).getHandle() );
	}

	@Override
	public void update() {
		for ( EntityPlayer player : trackedPlayers ) {
			updatePlayer( player );
		}
	}

	@Override
	public void untrack( Player player ) {
		clear( ( ( CraftPlayer ) player ).getHandle() );
	}

}
