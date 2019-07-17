package io.github.bananapuncher714.operation.gunsmoke.tinyprotocol;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.MapMaker;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.netty.channel.Channel;

public class TinyProtocolGunsmoke extends TinyProtocol {
	protected PacketHandler handler;
	protected Gunsmoke plugin;
	
	protected Map< String, Object > playerConnections = new MapMaker().weakValues().makeMap();
	
	public TinyProtocolGunsmoke( Gunsmoke plugin, PacketHandler handler ) {
		super( plugin );
		this.plugin = plugin;
		this.handler = handler;
		
		handler.setGunsmoke( plugin );
	}
	
	public PacketHandler getHandler() {
		return handler;
	}
	
	@Override
	public void sendPacket(Player player, Object packet) {
		if ( handler.isRealPlayer( player ) ) {
			sendPacket(getChannel(player), packet);
		}
	}
	
	public void sendPacket( String name, Object packet ) {
		Channel channel = getChannel( name );
		if ( channel == null ) {
			plugin.getLogger().severe( "Channel cannot be found for player'" + name + "'" );
		} else {
			sendPacket( channel, packet );
		}
	}
	
	@Override
	public Object onPacketOutAsync( Player player, Channel channel, Object packet ) {
		return handler.onPacketInterceptOut( player, packet );
	}

	@Override
	public Object onPacketInAsync( Player player, Channel channel, Object packet ) {
		return handler.onPacketInterceptIn( player, packet );
	}
	
	public Object getPlayerConnection( String player ) {
		Object conn = playerConnections.get( player );
		if ( conn == null ) {
			if ( handler.isCurrentThreadMain() ) {
				Player playerObj = Bukkit.getPlayer( player );
				conn = getConnection.get( getPlayerHandle.invoke( playerObj ) );
				playerConnections.put( player, conn );
			} else {
				return null;
			}
		}
		return conn;
	}
}
