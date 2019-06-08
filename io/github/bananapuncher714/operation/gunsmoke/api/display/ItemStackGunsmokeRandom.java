package io.github.bananapuncher714.operation.gunsmoke.api.display;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.inventory.ItemStack;

public class ItemStackGunsmokeRandom extends ItemStackGunsmoke {
	protected ItemStackGunsmoke[] items;
	
	public ItemStackGunsmokeRandom( ItemStackGunsmoke[] items ) {
		super( items[ 0 ].getItem() );
		this.items = items;
	}
	
	@Override
	public ItemStack getItem() {
		return items[ ThreadLocalRandom.current().nextInt( items.length ) ].getItem();
	}
}
