package io.github.bananapuncher714.operation.gunsmoke.api.item;

import org.bukkit.inventory.ItemStack;

/**
 * A wrapper for a bukkit itemstack
 * 
 * @author BananaPuncher714
 */
public class ItemStackGunsmoke {
	protected ItemStack item;
	
	public ItemStackGunsmoke( ItemStack item ) {
		setItem( item );
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public void setItem( ItemStack item ) {
		this.item = item.clone();
	}
}
