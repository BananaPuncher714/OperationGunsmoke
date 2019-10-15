package io.github.bananapuncher714.operation.gunsmoke.api.file;

import java.io.File;
import java.util.UUID;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;

public class GunsmokeCacheDisk implements GunsmokeCache {
	protected final File root;
	
	public GunsmokeCacheDisk( File file ) {
		if ( file.exists() && !file.isDirectory() ) {
			throw new IllegalArgumentException( "File provided must be a directory!" );
		} else {
			file.mkdirs();
		}
		root = file;
	}

	@Override
	public boolean contains( UUID uuid ) {
		return false;
	}
	
	@Override
	public GunsmokeRepresentable load( UUID uuid ) {
		return null;
	}

	@Override
	public void save( GunsmokeRepresentable representable ) {
	}
	
	@Override
	public void remove( UUID uuid ) {
	}
}
