package io.github.bananapuncher714.operation.gunsmoke.ngui.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface Clickable {
	/**
	 * Any inventory click anywhere
	 * 
	 * @param event
	 * @return
	 * Whether or not to stop all other clickables
	 */
	public abstract boolean onClick( InventoryClickEvent event );
}
