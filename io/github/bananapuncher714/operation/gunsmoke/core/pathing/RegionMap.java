package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

public class RegionMap {
	Map< AABB, Region > regions = new HashMap< AABB, Region >();
	
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
	
	public Collection< Region > getRegions() {
		return regions.values();
	}
}
