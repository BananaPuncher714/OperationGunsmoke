package io.github.bananapuncher714.operation.gunsmoke.implementation.world;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.world.GunsmokeExplosion;

public class ConfigExplosion {
	protected double power;
	protected double range;
	protected Set< Material > invincible = new HashSet< Material >();
	
	public ConfigExplosion( FileConfiguration config ) {
		power = config.getDouble( "power", 10 );
		range = config.getDouble( "range", 8 );
		for ( String str : config.getStringList( "unbreakable-blocks" ) ) {
			Material mat = Material.getMaterial( str.toUpperCase() );
			if ( mat == null ) {
				System.out.println( str + " is not a valid material! Please check your explosions folder!" );
			} else {
				invincible.add( mat );
			}
		}
	}
	
	public ConfigExplosion( double power, double range ) {
		this.power = power;
		this.range = range;
	}

	public double getPower() {
		return power;
	}

	public void setPower( double power ) {
		this.power = power;
	}

	public double getRange() {
		return range;
	}

	public void setRange( double range ) {
		this.range = range;
	}

	public Set< Material > getInvincible() {
		return invincible;
	}

	public void setInvincible( Set< Material > invincible ) {
		this.invincible = invincible;
	}
	
	public GunsmokeExplosion getExplosion( GunsmokeRepresentable exploder, Location center ) {
		return new GunsmokeExplosion( exploder, center, power, range ) {
			@Override
			public double getBlastReductionFor( Location location ) {
				if ( invincible.contains( location.getBlock().getType() ) ) {
					return -1;
				}
				return super.getBlastReductionFor( location );
			}
		};
	}
}
