package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumCreatureType;
import net.minecraft.server.v1_14_R1.IRegistry;
import net.minecraft.server.v1_14_R1.MinecraftKey;

/**
 * NMS Tools meant for registering custom entities
 * 
 * @author BananaPuncher714
 */
public class EntityRegistry {
	/*
	 * So firstly, what is required is a unique id and an entity type
	 * 
	 * 
	 * 
	 * 
	 */
	public static void register( String id, EntityTypes< ? > parent, EntityTypes.a< ? > type ) {
		// TODO Do some safe checking to see if id already exists?
		IRegistry.ENTITY_TYPE.a( IRegistry.ENTITY_TYPE.a( parent ), new MinecraftKey( id ), type.a( id ) );
	}
	
	public static EntityTypes.a< ? extends Entity > create( EntityTypes.b< ? extends Entity > constructor, EnumCreatureType type, double width, double height ) {
		EntityTypes.a< ? extends Entity > a = EntityTypes.a.a( constructor, type ).b().a( ( float ) width, ( float ) height );
		return a;
	}
}
