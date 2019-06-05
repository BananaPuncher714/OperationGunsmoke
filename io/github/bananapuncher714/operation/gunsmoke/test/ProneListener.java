package io.github.bananapuncher714.operation.gunsmoke.test;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PlayerJumpEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;

public class ProneListener implements Listener {
	Gunsmoke plugin;
	
	long lastSneak = 0;
	
	public ProneListener( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJump( PlayerJumpEvent event ) {
		GunsmokeEntity entity = plugin.getEntityManager().getEntity( event.getPlayer().getUniqueId() );
		if ( entity.isProne() ) {
			entity.setProne( false );
			plugin.getProtocol().getHandler().set( event.getPlayer(), false );
			entity.update();
			System.out.println( "Jump Event" );
		}
	}
	
	@EventHandler
	public void onPlayerSneak( PlayerToggleSneakEvent event ) {
		Player player = event.getPlayer();
		GunsmokeEntity entity = plugin.getEntityManager().getEntity( event.getPlayer().getUniqueId() );
		if ( event.isSneaking() ) {
			if ( player.isOnGround() && !entity.isProne() ) {
				if ( System.currentTimeMillis() - lastSneak < 500 ) {
					entity.setProne( true );
					plugin.getProtocol().getHandler().set( player, true );
					entity.update();
					System.out.println( "Sneak Event" );
				}
				lastSneak = System.currentTimeMillis();
			}
		} else {
			if ( entity.isProne() ) {
				entity.setProne( false );
				plugin.getProtocol().getHandler().set( event.getPlayer(), false );
				entity.update();
			}
		}
	}
}
