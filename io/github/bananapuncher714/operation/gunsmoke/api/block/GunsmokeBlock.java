package io.github.bananapuncher714.operation.gunsmoke.api.block;

import org.bukkit.Location;
import org.bukkit.Material;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class GunsmokeBlock extends GunsmokeRepresentable {
	protected double health;
	protected double maxHealth;
	protected Location location;
	
	public GunsmokeBlock( Location location, double health ) {
		this.location = BukkitUtil.getBlockLocation( location );
		this.health = health;
		this.maxHealth = health;
	}
	
	public void damage( double damage, DamageType type ) {
		health = Math.max( health - damage, 0 );
	}
	
	public void destroy() {
		location.getBlock().setType( Material.AIR, false );
		GunsmokeUtil.setBlockStage( location, -1 );
	}
	
	public boolean contains( Location location ) {
		return this.location.equals( BukkitUtil.getBlockLocation( location ) );
	}

	public double getHealth() {
		return health;
	}

	public void setHealth( double health ) {
		this.health = health;
	}
	
	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth( double maxHealth ) {
		this.maxHealth = maxHealth;
	}
	
	public boolean isInvincible() {
		return maxHealth == -1;
	}
	
	public Location getLocation() {
		return location.clone();
	}

	public void updateBlockStage() {
		double percent = health / maxHealth;
		int stage = 9 - ( int ) ( percent * 10 );
		GunsmokeUtil.setBlockStage( location, stage );
	}
}