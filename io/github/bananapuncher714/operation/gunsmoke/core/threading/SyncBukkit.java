package io.github.bananapuncher714.operation.gunsmoke.core.threading;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SyncBukkit {
	private static Map< UUID, ItemStack[] > hands = new HashMap< UUID, ItemStack[] >();

	static {
		Bukkit.getScheduler().scheduleSyncRepeatingTask( null, SyncBukkit::update, 0, 1 );
	}

	public static void init() {
	}
	
	private static void update() {
		synchronized ( hands ) {
			for ( Player player : Bukkit.getOnlinePlayers() ) {
				ItemStack[] holding;
				if ( hands.containsKey( player.getUniqueId() ) ) {
					holding = hands.get( player.getUniqueId() );
				} else {
					holding = new ItemStack[ 2 ];
					hands.put( player.getUniqueId(), holding );
				}

				holding[ 0 ] = player.getInventory().getItemInMainHand();
				holding[ 1 ] = player.getInventory().getItemInOffHand();
			}
		}
	}

	public static ItemStack getItem( Player player, boolean offhand ) {
		synchronized ( hands ) {
			if ( hands.containsKey( player.getUniqueId() ) ) {
				return hands.get( player.getUniqueId() )[ offhand ? 1 : 0 ];
			} else {
				return null;
			}
		}
	}
}
