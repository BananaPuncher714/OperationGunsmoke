package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.v1_14_R1.EnumProtocolDirection;
import net.minecraft.server.v1_14_R1.NetworkManager;
import net.minecraft.server.v1_14_R1.Packet;

public class DummyNetworkManager extends NetworkManager {
	protected TestEntity entity;
	
	public DummyNetworkManager( EnumProtocolDirection enumprotocoldirection, TestEntity player ) {
		super( enumprotocoldirection );
		this.entity = player;
	}

	@Override
	public boolean isConnected() {
		// For spiritual purposes we'll pretend that this network manager is connected
		return true;
	}
	
	@Override
	public void sendPacket( Packet< ? > packet, GenericFutureListener< ? extends Future< ? super Void > > genericfuturelistener ) {
		entity.onPacket( packet );
	}
}
