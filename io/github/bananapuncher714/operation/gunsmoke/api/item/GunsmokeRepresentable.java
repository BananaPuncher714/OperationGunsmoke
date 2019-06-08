package io.github.bananapuncher714.operation.gunsmoke.api.item;

import java.util.UUID;

public abstract class GunsmokeRepresentable {
	protected final UUID uuid;
	
	public GunsmokeRepresentable() {
		uuid = UUID.randomUUID();
	}
	
	public UUID getUUID() {
		return uuid;
	}
}
