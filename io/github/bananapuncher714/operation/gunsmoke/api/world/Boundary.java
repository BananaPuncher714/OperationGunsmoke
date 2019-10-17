package io.github.bananapuncher714.operation.gunsmoke.api.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class Boundary {
	Map< World, Set< AABB > > bounds = new HashMap< World, Set< AABB > >();
	
	public Boundary() {
	}
	
	public Map< World, Set< AABB > > getBounds() {
		return bounds;
	}
	
	public Set< AABB > getBoundsFor( World world ) {
		return bounds.get( world );
	}
}
