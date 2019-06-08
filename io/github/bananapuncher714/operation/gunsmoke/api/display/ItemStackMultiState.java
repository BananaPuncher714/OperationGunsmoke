package io.github.bananapuncher714.operation.gunsmoke.api.display;

import org.bukkit.inventory.ItemStack;

/**
 * A class representing an itemstack that can be portrayed in multiple positions by the player
 * 
 * @author BananaPuncher714
 */
public class ItemStackMultiState {
	protected ItemStackGunsmoke standard;
	protected ItemStackGunsmoke shield;
	protected ItemStackGunsmoke crossbow;
	protected ItemStackGunsmoke bow;
	protected ItemStackGunsmoke trident;
	
	public ItemStackMultiState( ItemStackGunsmoke standard ) {
		this.standard = standard;
	}
	
	public ItemStack getItem( State state ) {
		ItemStackGunsmoke customItem = getItemGunsmoke( state );
		if ( customItem == null ) {
			return standard.getItem();
		}
		ItemStack item = getItemGunsmoke( state ).getItem();
		return item == null ? standard.getItem() : item;
	}
	
	public ItemStackGunsmoke getItemGunsmoke( State state ) {
		switch ( state ) {
		case DEFAULT: return standard;
		case SHIELD: return shield;
		case CROSSBOW: return crossbow;
		case BOW: return bow;
		case TRIDENT: return trident;
		default: return standard;
		}
	}
	
	public void setItem( State state, ItemStackGunsmoke item ) {
		switch ( state ) {
		case DEFAULT: standard = item; break;
		case SHIELD: shield = item; break;
		case CROSSBOW: crossbow = item; break;
		case BOW: bow = item; break;
		case TRIDENT: trident = item; break;
		default: standard = item; break;
		}
	}
	
	public static enum State {
		DEFAULT, SHIELD, CROSSBOW, BOW, TRIDENT, CUSTOM;
	}
}
