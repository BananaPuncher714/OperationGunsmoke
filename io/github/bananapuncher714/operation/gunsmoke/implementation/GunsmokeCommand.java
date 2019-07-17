package io.github.bananapuncher714.operation.gunsmoke.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.NPCAction;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmor;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmorOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigGun;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;

public class GunsmokeCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			
			if ( args.length == 1 ) {
				if ( args[ 0 ].equalsIgnoreCase( "sneak" ) ) {
					
					for ( GunsmokeNPC npc : GunsmokeUtil.getPlugin().getNPCManager().getNPCs() ) {
						npc.interact( NPCAction.START_SNEAKING );
						npc.interact( NPCAction.STOP_SNEAKING );
						npc.interact( NPCAction.START_SNEAKING );
					}
				}
			} else if ( args.length == 2 ) {
				if ( args[ 0 ].equalsIgnoreCase( "get" ) ) {
					String id = args[ 1 ];
					
					GunsmokeItem item = null;
					
					ConfigWeaponOptions options = GunsmokeImplementation.getInstance().getGun( id );
					if ( options != null ) {
						item = new ConfigGun( options );
					} else {
						ConfigArmorOptions armor = GunsmokeImplementation.getInstance().getArmor( id );
						if ( armor != null ) {
							item = new ConfigArmor( armor );
						}
					}
					
					if ( item != null ) {
						GunsmokeUtil.getPlugin().getItemManager().register( item );
						player.getInventory().addItem( item.getItem() );
						player.sendMessage( ChatColor.GREEN + "Gave you a " + id + "!" );
					} else {
						player.sendMessage( ChatColor.RED + id + " is not a valid item!" );
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public List< String > onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
		List< String > completions = new ArrayList< String >();
		if ( !( sender instanceof Player ) ) {
			return completions;
		}
		List< String > suggestions = new ArrayList< String >();
		if ( args.length == 1 ) {
			suggestions.add( "get" );
		} else if ( args.length == 2 ) {
			if ( args[ 0 ].equalsIgnoreCase( "get" ) ) {
				suggestions.addAll( GunsmokeImplementation.getInstance().guns.keySet() );
				suggestions.addAll( GunsmokeImplementation.getInstance().armor.keySet() );
			}
		}
		StringUtil.copyPartialMatches( args[ args.length - 1 ], suggestions, completions);
		Collections.sort( completions );
		return completions;
	}
}
