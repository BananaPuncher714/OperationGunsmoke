package io.github.bananapuncher714.operation.gunsmoke.api.file;

import org.bukkit.configuration.ConfigurationSection;

public interface GunsmokeObjectDeserializer< T > {
	T deseralize( ConfigurationSection section );
}
