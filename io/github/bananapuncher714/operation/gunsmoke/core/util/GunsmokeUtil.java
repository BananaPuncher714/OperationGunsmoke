package io.github.bananapuncher714.operation.gunsmoke.core.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;

public class GunsmokeUtil {
	public final static boolean canUpdate( GunsmokeEntity entity, boolean main ) {
		ItemStack mainHand = entity.getMainHand().getHolding();
		if ( main ) {
			return true;
		}
		
		ItemStack offHand = entity.getOffHand().getHolding();
		if ( offHand == null ) {
			return false;
		}
		if ( mainHand == null ) {
			return entity.getOffHand().getState() != State.DEFAULT;
		}
		
		// Now we can test for specific cases regarding crossbows, shields, tridents, and bows
		Material mainType = mainHand.getType();
		Material offType = offHand.getType();
		
		if ( mainType == Material.BOW ) {
			return false;
		} else if ( mainType == Material.CROSSBOW ) {
			return false;
		} else if ( mainType == Material.TRIDENT ) {
			return false;
		} else if ( mainType == Material.SHIELD ) {
			return false;
		} else {
			return false;
		}
	}
}
