package io.github.bananapuncher714.operation.gunsmoke.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.projectile.GunsmokeProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetBlock;
import io.github.bananapuncher714.operation.gunsmoke.api.util.ProjectileTargetEntity;

public class TestGunsmokeProjectile extends GunsmokeProjectile {
	double life;
	Entity shooter;
	Item item;
	
	public TestGunsmokeProjectile( Entity shooter, Location location, double life ) {
		super( location );
		this.life = life;
		this.shooter = shooter;
		
		item = location.getWorld().dropItem( location, new ItemStack( Material.SNOWBALL ) );
		item.setInvulnerable( true );
		item.setPickupDelay( 32767 );
	}

	@Override
	public EnumTickResult tick() {
		life--;
		
		for ( int i = 0; i < 10; i++ ) {
			location.getWorld().spawnParticle( Particle.FLAME, location.clone().add( velocity.clone().multiply( i / 11.0 ) ), 0 );
		}
		
		item.teleport( location );
		item.setVelocity( velocity );
		
		if ( super.tick() == EnumTickResult.CANCEL ) {
			return EnumTickResult.CANCEL;
		} else {
			return life <= 0 ? EnumTickResult.CANCEL : EnumTickResult.CONTINUE;
		}
	}
	
	@Override
	public void remove() {
		item.remove();
	}
	
	@Override
	public void hit( ProjectileTargetEntity target ) {
		GunsmokeEntity hitEntity = target.getHitEntity();
		if ( hitEntity instanceof GunsmokeEntityWrapper ) {
			Entity bukkitEntity = ( ( GunsmokeEntityWrapper ) hitEntity ).getEntity();
			
			if ( shooter == bukkitEntity ) {
				return;
			}
			
			System.out.println( "Hit entity " + bukkitEntity.getType() );
			
			if ( bukkitEntity instanceof Creeper ) {
				life = 0;
			} else {
				life -= 10;
			}
		}
	}

	@Override
	public void hit( ProjectileTargetBlock target ) {
		Material hitType = target.getHitBlock().getType();
		if ( hitType == Material.WATER || hitType == Material.LAVA ) {
			return;
		}
		System.out.println( "Hit block " + target.getHitBlock().getType() );
		if ( target.getHitBlock().getType() != Material.GLASS ) {
			life -= 30;
		}
	}
}