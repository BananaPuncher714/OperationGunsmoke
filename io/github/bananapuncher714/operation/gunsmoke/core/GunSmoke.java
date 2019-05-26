package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.core.util.ReflectionUtils;
import io.github.bananapuncher714.operation.gunsmoke.tinyprotocol.TinyProtocolGunsmoke;

public class GunSmoke extends JavaPlugin {
	protected TinyProtocolGunsmoke protocol;
	
	@Override
	public void onEnable() {
		PacketHandler handler = ReflectionUtils.getNewPacketHandlerInstance();
		protocol = new TinyProtocolGunsmoke( this, handler );
	}
}
