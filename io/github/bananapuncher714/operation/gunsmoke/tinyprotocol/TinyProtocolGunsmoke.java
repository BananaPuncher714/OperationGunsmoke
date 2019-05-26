package io.github.bananapuncher714.operation.gunsmoke.tinyprotocol;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.netty.channel.Channel;

public class TinyProtocolGunsmoke extends TinyProtocol {
	protected PacketHandler handler;
	
	public TinyProtocolGunsmoke( Plugin plugin, PacketHandler handler ) {
		super( plugin );
		this.handler = handler;
	}
	
	@Override
	public Object onPacketOutAsync( Player player, Channel channel, Object packet ) {
		return handler.onPacketInterceptOut( player, packet ) ? packet : null;
	}

	@Override
	public Object onPacketInAsync( Player player, Channel channel, Object packet ) {
		return handler.onPacketInterceptIn( player, packet ) ? packet : null;
	}
}
