package io.github.bananapuncher714.operation.gunsmoke.minigame.ace;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigGun;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;

public abstract class WeaponClass {
	public abstract void equip( GunsmokeEntity entity );
	
	public void addWeapon( Player player, String weaponType ) {
		ConfigWeaponOptions options = GunsmokeImplementation.getInstance().getGun( weaponType );
		ConfigGun gun = new ConfigGun( options );
		
		GunsmokeUtil.getPlugin().getItemManager().register( gun );
		player.getInventory().addItem( gun.getItem() );
	}
}
