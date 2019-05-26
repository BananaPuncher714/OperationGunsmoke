package io.github.bananapuncher714.operation.gunsmoke.api.item;

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
	
	public ItemStack getItem( State state ) {
		return getItemGunsmoke( state ).getItem();
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
		case DEFAULT: standard = item; return;
		case SHIELD: shield = item; return;
		case CROSSBOW: crossbow = item; return;
		case BOW: bow = item; return;
		case TRIDENT: trident = item; return;
		default: standard = item; return;
		}
	}
	
	public static enum State {
		DEFAULT, SHIELD, CROSSBOW, BOW, TRIDENT;
	}
}
