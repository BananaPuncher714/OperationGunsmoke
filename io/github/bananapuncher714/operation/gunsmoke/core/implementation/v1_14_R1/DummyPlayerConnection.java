package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import net.minecraft.server.v1_14_R1.EnumProtocolDirection;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PlayerConnection;

public class DummyPlayerConnection extends PlayerConnection {
	public DummyPlayerConnection( MinecraftServer minecraftserver, TestEntity entityplayer ) {
		super( minecraftserver, new DummyNetworkManager( EnumProtocolDirection.CLIENTBOUND, entityplayer ), entityplayer );
	}
	
	@Override
	public void sendPacket( Packet< ? > packet ) {
	}
}
