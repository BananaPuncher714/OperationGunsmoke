package io.github.bananapuncher714.operation.gunsmoke.api.block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class GunsmokeStructure extends GunsmokeBlock implements Tickable {
	protected Map< Location, GunsmokeBlock > locations = new HashMap< Location, GunsmokeBlock >();
	
	public GunsmokeStructure( Location location, double health ) {
		super( location, health );
		locations.put( getLocation(), new GunsmokeBlock( location, health ) );
	}
	
	@Override
	public EnumTickResult tick() {
		for ( GunsmokeBlock block : locations.values() ) {
			if ( block instanceof Tickable ) {
				( ( Tickable ) block ).tick();
			}
		}
		return EnumTickResult.CONTINUE;
	}
	
	@Override
	public void damage( double damage, DamageType type ) {
		for ( GunsmokeBlock block : locations.values() ) {
			block.damage( damage, type );
		}
		super.damage( damage, type );
	}

	@Override
	public void destroy() {
		for ( GunsmokeBlock block : locations.values() ) {
			block.destroy();
		}
	}

	@Override
	public boolean contains( Location location ) {
		for ( GunsmokeBlock block : locations.values() ) {
			if ( block.contains( location ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setHealth( double health ) {
		for ( GunsmokeBlock block : locations.values() ) {
			block.setHealth( health);
		}
		super.setHealth( health );
	}

	@Override
	public void setMaxHealth( double maxHealth ) {
		for ( GunsmokeBlock block : locations.values() ) {
			block.setMaxHealth( maxHealth );
		}
		super.setMaxHealth( maxHealth );
	}

	@Override
	public void updateBlockStage() {
		for ( GunsmokeBlock block : locations.values() ) {
			block.updateBlockStage();
		}
	}
	
	public void addBlock( GunsmokeBlock block ) {
		locations.put( BukkitUtil.getBlockLocation( block.getLocation() ), block );
	}

	public void removeBlock( Location location ) {
		locations.remove( BukkitUtil.getBlockLocation( location ) );
	}
	
	public Collection< GunsmokeBlock > getLocations() {
		return locations.values();
	}
}
