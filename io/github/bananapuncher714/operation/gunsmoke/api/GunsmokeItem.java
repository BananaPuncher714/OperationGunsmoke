package io.github.bananapuncher714.operation.gunsmoke.api;

import org.bukkit.inventory.ItemStack;

public abstract class GunsmokeItem extends GunsmokeRepresentable {
	private final static Object[] CUSTOM = { "io", "github", "bananapuncher714", "operation", "gunsmoke", "item", "custom" };
	
	public abstract ItemStack getItem();
	
	protected static ItemStack markAsGunsmokeItem( ItemStack item ) {
		return item;
	}
}
