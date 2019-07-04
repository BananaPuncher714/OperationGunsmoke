package io.github.bananapuncher714.operation.gunsmoke.ngui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.operation.gunsmoke.ngui.inventory.BananaHolder;

/**
 * Primitive yet effective inventory system involving NBT tags and inventory holders
 * 
 * @author BananaPuncher714
 */
public class NGui {
	public static void init( JavaPlugin plugin ) {
		Bukkit.getPluginManager().registerEvents( new ClickListener(), plugin );
	}
	
	public static void disable() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			Inventory inventory = player.getOpenInventory().getTopInventory();
			if ( inventory != null && inventory.getHolder() instanceof BananaHolder ) {
				player.closeInventory();
			}
		}
	}
}
