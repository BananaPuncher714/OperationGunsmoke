package io.github.bananapuncher714.operation.gunsmoke.api.file;

import java.util.UUID;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;

/**
 * An interface built to load and save GunsmokeRepresentables in the future
 * Can be file based, can also store the objects directly in ram or something like that.
 * This will allow me to expand the different storage solutions for saving GunsmokeRepresentables since
 * loading from files is slow and I don't want to be doing that forever.
 */
public interface GunsmokeCache {
	boolean contains( UUID uuid );
	GunsmokeRepresentable load( UUID uuid );
	void save( GunsmokeRepresentable representable );
	void remove( UUID uuid );
}
