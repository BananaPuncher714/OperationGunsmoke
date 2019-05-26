package io.github.bananapuncher714.operation.gunsmoke.api.item;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an item that can be gotten
 * 
 * @author BananaPuncher714
 */
public class ItemStackGunsmokeAnimated extends ItemStackGunsmoke {
	protected ItemStackGunsmoke[] frames;
	
	public ItemStackGunsmokeAnimated( ItemStackGunsmoke[] frames ) {
		super( frames[ 0 ].getItem() );
		this.frames = frames;
	}
	
	@Override
	public ItemStack getItem() {
		return getItem( 0 );
	}
	
	public ItemStack getItem( int frame ) {
		return frames[ frame ].getItem();
	}

	public ItemStack getItem( double percent ) {
		return getItem( percent * frames.length );
	}
	
	public int getLength() {
		return frames.length;
	}
}
