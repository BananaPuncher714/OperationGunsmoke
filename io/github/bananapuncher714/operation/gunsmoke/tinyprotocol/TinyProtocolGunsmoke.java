package io.github.bananapuncher714.operation.gunsmoke.tinyprotocol;

import java.lang.reflect.Field;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.core.Gunsmoke;
import io.netty.channel.Channel;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition;

public class TinyProtocolGunsmoke extends TinyProtocol {
	protected PacketHandler handler;
	protected Gunsmoke plugin;
	
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
	public Object onPacketOutAsync( Player player, Channel channel, Object packet ) {
		return handler.onPacketInterceptOut( player, packet );
	}

	@Override
	public Object onPacketInAsync( Player player, Channel channel, Object packet ) {
		return handler.onPacketInterceptIn( player, packet );
	}
}
