package io.github.bananapuncher714.operation.gunsmoke.minigame.ace.classes;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperLivingEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperPlayer;
import io.github.bananapuncher714.operation.gunsmoke.minigame.ace.WeaponClass;

public class WeaponClassShotgun extends WeaponClass {
	@Override
	public void equip( GunsmokeEntity gEntity ) {
		if ( gEntity instanceof GunsmokeEntityWrapperPlayer ) {
			GunsmokeEntityWrapperPlayer gPlayer = ( GunsmokeEntityWrapperPlayer ) gEntity;
			Player player = gPlayer.getEntity();
			
			player.getInventory().clear();
			
			// Give the player a gun
			addWeapon( player, "remington870" );
			addWeapon( player, "laser-pistol" );
			player.getInventory().addItem( new ItemStack( Material.COOKED_BEEF, 42 ) );
		} else if ( gEntity instanceof GunsmokeEntityWrapperLivingEntity ) {
			GunsmokeEntityWrapperLivingEntity gLEntity = ( GunsmokeEntityWrapperLivingEntity ) gEntity;
			LivingEntity entity = gLEntity.getEntity();
			
			// Give them items...
		}
	}
}
