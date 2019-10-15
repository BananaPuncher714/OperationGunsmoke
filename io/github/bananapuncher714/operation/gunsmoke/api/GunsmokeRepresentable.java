package io.github.bananapuncher714.operation.gunsmoke.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class GunsmokeRepresentable implements ConfigurationSerializable {
	protected final UUID uuid;
	
	protected GunsmokeRepresentable( Map< String, Object > map ) {
		uuid = UUID.fromString( ( String ) map.get( "uuid" ) );
	}
	
	public GunsmokeRepresentable() {
		uuid = UUID.randomUUID();
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public void remove() {
	}
	
	@Override
	public Map< String, Object > serialize() {
		Map< String, Object > objects = new HashMap< String, Object >();
		objects.put( "uuid", uuid.toString() );
		return objects;
	}
}
