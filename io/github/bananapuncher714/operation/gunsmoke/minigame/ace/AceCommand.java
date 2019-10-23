package io.github.bananapuncher714.operation.gunsmoke.minigame.ace;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.core.PlayerSaveData;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GeneralUtil;

public class AceCommand {
	protected Ace ace;
	
	protected AceCommand( Ace ace ) {
		this.ace = ace;
	}
	
	protected void onCommand( CommandSender sender, String[] args ) {
		if ( args.length == 0 ) {
			sender.sendMessage( ChatColor.RED + "Arguments required!" );
		} else if ( args.length > 0 ) {
			String option = args[ 0 ];
			args = GeneralUtil.pop( args );
			if ( option.equalsIgnoreCase( "addredspawn" ) ) {
				addRedSpawn( sender, args );
			} else if ( option.equalsIgnoreCase( "addbluespawn" ) ) {
				addBlueSpawn( sender, args );
			} else {
				sender.sendMessage( ChatColor.RED + "Incorrect Arguments!" );
			}
		}
	}

	private void addRedSpawn( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, ChatColor.RED + "You must be a player to run this command!" );
		Player player = ( Player ) sender;
		
		ace.getSettings().addRedSpawn( new PlayerSaveData( player ) );
		
		player.sendMessage( "Added red spawn" );
	}
	
	private void addBlueSpawn( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, ChatColor.RED + "You must be a player to run this command!" );
		Player player = ( Player ) sender;
		
		ace.getSettings().addBlueSpawn( new PlayerSaveData( player ) );
		
		player.sendMessage( "Added blue spawn" );
	}
}