package io.github.bananapuncher714.operation.gunsmoke.api.file;

import org.bukkit.configuration.ConfigurationSection;

public interface GunsmokeObjectSerializer< T > {
	void serialize( ConfigurationSection section, T object );
}
