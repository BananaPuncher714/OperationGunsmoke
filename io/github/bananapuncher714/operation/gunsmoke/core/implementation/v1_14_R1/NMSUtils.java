package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

import net.minecraft.server.v1_14_R1.Entity;

public class NMSUtils {
	protected final static org.bukkit.entity.Entity getEntityFromId( World world, int id ) {
		Entity entity = ( ( CraftWorld ) world ).getHandle().getEntity( id );
		return entity.getBukkitEntity();
	}
}
