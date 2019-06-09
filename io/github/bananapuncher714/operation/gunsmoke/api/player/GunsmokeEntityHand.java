package io.github.bananapuncher714.operation.gunsmoke.api.player;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState;
import io.github.bananapuncher714.operation.gunsmoke.api.display.ItemStackMultiState.State;

public class GunsmokeEntityHand {
	protected State state = State.DEFAULT;
	protected ItemStackMultiState item;
	
	public State getState() {
		return state;
	}
	
	public ItemStackMultiState getItem() {
		return item;
	}
	
	public void setState( State state ) {
		this.state = state;
	}
	
	public void setItem( ItemStackMultiState item ) {
		this.item = item;
		state = State.DEFAULT;
	}
	
	public ItemStack getHolding() {
		if ( item == null ) {
			return null;
		}
		return item.getItem( state );
	}
}
