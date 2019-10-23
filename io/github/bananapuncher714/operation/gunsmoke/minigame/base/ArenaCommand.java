package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.bananapuncher714.operation.gunsmoke.core.util.GeneralUtil;

public class ArenaCommand implements CommandExecutor {
	protected MinigameManager manager;
	
	protected ArenaCommand( MinigameManager manager ) {
		this.manager = manager;
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( !sender.hasPermission( "gunsmoke.minigame.command.arena" ) ) {
			sender.sendMessage( ChatColor.RED + "You do not have permission to run this command!" );
			return false;
		}
		if ( args.length > 0 ) {
			String minigameName = args[ 0 ];
			
			Minigame minigame = manager.getGame( minigameName );
			
			if ( minigame != null ) {
				args = GeneralUtil.pop( args );
				minigame.onCommand( sender, args );
			} else {
				sender.sendMessage( "Arena does not exist!" );
			}
		} else {
			sender.sendMessage( "Arena specification required" );
		}
		
		return false;
	}
}
