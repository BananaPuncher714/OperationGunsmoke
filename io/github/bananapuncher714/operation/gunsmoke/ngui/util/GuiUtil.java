package io.github.bananapuncher714.operation.gunsmoke.ngui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class GuiUtil {

	/**
	 * Convert a slot to Cartesian coordinates, with the origin being the top left and the highest values the bottom right
	 * 
	 * @param slot
	 * @param width
	 * @return
	 */
	public static int[] slotToCoord( int slot, int width ) {
		int x = slot % width;
		int y = ( slot - x ) / width;
		return new int[] { x, y };
	}
	
	/**
	 * Convert Cartesian coordinates into a slot number, inverse of {@link #slotToCoord( int, int ) slotToCoord} method 
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static int coordToSlot( int x, int y, int width, int height ) {
		if ( x > width || x < 0 || y > height || y < 0 ) return -1;
		return y * width + x;
	}
	
	/**
	 * Combine like ItemStacks into a new list; Excludes null items
	 * 
	 * @param items
	 * @return
	 */
	public static List< ItemStack > combine( List< ItemStack > items ) {
		List< ItemStack > sorted = new ArrayList< ItemStack >();
		for ( ItemStack item : items ) {
			if ( item == null ) {
				continue;
			}
			boolean putInPlace = false;
			for ( ItemStack sitem : sorted ) {
				if ( item.isSimilar( sitem ) ) {
					if ( item.getAmount() + sitem.getAmount() < sitem.getMaxStackSize() ) {
						sitem.setAmount( sitem.getAmount() + item.getAmount() );
						putInPlace = true;
					} else {
						item.setAmount( item.getAmount() - ( sitem.getMaxStackSize() - sitem.getAmount() ) );
						sitem.setAmount( sitem.getMaxStackSize() );
					}
					break;
				}
			}
			if ( !putInPlace ) {
				sorted.add( item );
			}
		}
		return sorted;
	}
	
	/**
	 * Given a smaller rectangle in a larger rectangle, get the slot number of the smaller rectangle in relation to the larger rectangle
	 * 
	 * @param clickedSlot
	 * Does not check whether the slot falls inside the smaller rectangle.
	 * @param outerWidth
	 * @param topLeftInnerSlot
	 * @param innerWidth
	 * @return
	 */
	public static int getInnerSlot( int clickedSlot, int outerWidth, int topLeftInnerSlot, int innerWidth ) {
		int normalizedSlot = clickedSlot - topLeftInnerSlot;
		int differenceInWidth = outerWidth - innerWidth;
		
		return ( int ) ( normalizedSlot - Math.floor( normalizedSlot / outerWidth ) * differenceInWidth );
	}
	
	/**
	 * Remove as many of item from a given list of items; Will reduce the amount of item as much as possible
	 * 
	 * @param items
	 * The list to remove items from
	 * @param toRemove
	 * The item that has to be removed
	 * @return
	 * The remaining item; null if all was removed
	 */
	public static ItemStack subtract( List< ItemStack > items, ItemStack toRemove ) {
		int amount = toRemove.getAmount();
		for ( Iterator< ItemStack > iterator = items.iterator(); iterator.hasNext(); ) {
			ItemStack item = iterator.next();
			if ( item != null ) {
				if ( toRemove.isSimilar( item ) ) {
					int itemAmount = item.getAmount();
					if ( itemAmount > amount ) {
						item.setAmount( itemAmount - amount );
						toRemove = null;
						break;
					} else if ( itemAmount == amount ) {
						iterator.remove();
						toRemove = null;
						break;
					} else if ( itemAmount < amount ) {
						iterator.remove();
						amount -= itemAmount;
					}
				}
			}
		}
		List< ItemStack > sorted = combine( items );
		items.clear();
		items.addAll( sorted );
		if ( toRemove != null ) {
			ItemStack remaining = toRemove.clone();
			remaining.setAmount( amount );
			return remaining;
		}
		return null;
	}
}
