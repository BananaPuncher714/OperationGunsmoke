package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.World;

public class TestEntity extends EntityHuman {
	protected TestEntity( EntityTypes< ? extends EntityHuman > entitytypes, World world ) {
		super( world, new GameProfile( UUID.randomUUID(), world.worldData.getName() ) );
		
	}

	@Override
	public boolean isCreative() {
		return false;
	}

	@Override
	public boolean isSpectator() {
		return false;
	}
}
