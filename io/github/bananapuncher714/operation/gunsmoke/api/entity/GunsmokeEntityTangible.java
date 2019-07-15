package io.github.bananapuncher714.operation.gunsmoke.api.entity;

import io.github.bananapuncher714.operation.gunsmoke.api.model.GunsmokeBoundingBox;

public abstract class GunsmokeEntityTangible extends GunsmokeEntity {
	protected GunsmokeBoundingBox box;
	
	public GunsmokeBoundingBox getBoundingBox() {
		return box;
	}
	
}
