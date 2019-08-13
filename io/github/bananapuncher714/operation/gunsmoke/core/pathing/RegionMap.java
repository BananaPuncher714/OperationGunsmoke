package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class RegionMap {
	Map< AABB, Region > regions = new HashMap< AABB, Region >();
	Set< ElevationLayer > layers = new HashSet< ElevationLayer >();
	Set< Corner > corners = new HashSet< Corner >();
	
	public Region getRegionFor( AABB box ) {
		Region region = regions.get( box );
		if ( region == null  ) {
			region = new Region( box );
			regions.put( box, region );
		}
		return region;
	}
	
	public Region getRegion( Location location ) {
		for ( Region region : regions.values() ) {
			if ( VectorUtil.contains( region.getRegion(), location.toVector() ) ) {
				return region;
			}
		}
		return null;
	}
	
	public ElevationLayer getLayer( Region region ) {
		for ( ElevationLayer layer : layers ) {
			if ( layer.contains( region ) ) {
				return layer;
			}
		}
		return null;
	}
	
	public Collection< Region > getRegions() {
		return regions.values();
	}
	
	public Set< ElevationLayer > getLayers() {
		return layers;
	}
	
	public Set< Corner > getCorners() {
		return corners;
	}
}
