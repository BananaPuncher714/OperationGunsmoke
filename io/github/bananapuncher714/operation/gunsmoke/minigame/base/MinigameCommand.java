package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GeneralUtil;

public class MinigameCommand implements CommandExecutor {
	protected MinigameManager manager;
	
	protected MinigameCommand( MinigameManager manager ) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		// So I don't feel like implementing a fully functional and expandable command system here
		// Instead I'm going to throw something together since this is a project I just want done
		try {
			if ( args.length == 0 ) {
				sender.sendMessage( ChatColor.RED + "Arguments required! Usage: /minigame <join|leave|list> [...]" );
			} else if ( args.length > 0 ) {
				String option = args[ 0 ];
				args = GeneralUtil.pop( args );
				if ( option.equalsIgnoreCase( "create" ) ) {
					create( sender, args );
				} else if ( option.equalsIgnoreCase( "delete" ) ) {
					delete( sender, args );
				} else if ( option.equalsIgnoreCase( "list" ) ) {
					list( sender, args );
				} else if ( option.equalsIgnoreCase( "join" ) ) {
					join( sender, args );
				} else if ( option.equalsIgnoreCase( "leave" ) ) {
					leave( sender, args );
				} else {
					sender.sendMessage( ChatColor.RED + "Incorrect Arguments!" );
				}
			}
		} catch ( IllegalArgumentException exception ) {
			sender.sendMessage( exception.getMessage() );
		}
		return false;
	}
	
	private void create( CommandSender sender, String[] args ) {
		Validate.isTrue( sender.hasPermission( "gunsmoke.minigame.command.create" ), ChatColor.RED + "You do not have permission to run this command!" );
		Validate.isTrue( args.length > 1, ChatColor.RED + "Incorrect number of arguments! /minigame create <id> <type> [...]" );
		String id = args[ 0 ];
		String type = args[ 1 ];
		
		MinigameFactory factory = manager.getFactory( type );
		Validate.isTrue( factory != null, ChatColor.RED + "The current type(" + type + ") of minigame does not exist!" );
		
		args = GeneralUtil.pop( GeneralUtil.pop( args ) );
		
		Minigame game = factory.get( sender, args );
		
		manager.addMinigame( id, game );
		sender.sendMessage( ChatColor.GREEN + "Minigame(" + id + ") created!" );
	}
	
	private void delete( CommandSender sender, String[] args ) {
		Validate.isTrue( sender.hasPermission( "gunsmoke.minigame.command.create" ), ChatColor.RED + "You do not have permission to run this command!" );
		Validate.isTrue( args.length == 1, ChatColor.RED + "Must provide a minigame id! /minigame delete <id>" );
		String id = args[ 0 ];
		
		Minigame game = manager.removeMinigame( id );
		
		Validate.isTrue( game != null, ChatColor.RED + "That minigame does not exist!" );
		
		sender.sendMessage( ChatColor.GREEN + "Minigame(" + id + ") deleted!" );
	}
	
	private void list( CommandSender sender, String[] args ) {
		Validate.isTrue( sender.hasPermission( "gunsmoke.minigame.command.create" ), ChatColor.RED + "You do not have permission to run this command!" );
		if ( manager.getMinigames().isEmpty() ) {
			sender.sendMessage( ChatColor.BLUE + "No minigames exist!" );
		} else {
			sender.sendMessage( ChatColor.BLUE + "Minigames:" );
			for ( String id : manager.getMinigames().keySet() ) {
				sender.sendMessage( "- " + ChatColor.WHITE + id );
			}
		}
	}
	
	private void join( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, ChatColor.RED + "You must be a player to run this command!" );
		Player player = ( Player ) sender;
		GunsmokeEntity entity = manager.getPlugin().getItemManager().getEntityWrapper( player );
		
		Validate.isTrue( args.length == 1, ChatColor.RED + "You must provide an id!" );
		Validate.isTrue( manager.getGame( args[ 0 ] ) != null, ChatColor.RED + "That is not a valid game!" );
		Validate.isTrue( manager.participating( entity ) == null, ChatColor.RED + "You are already in a game!" );
		
		manager.join( args[ 0 ], entity );
		player.sendMessage( "You have joined the game" );
	}
	
	private void leave( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, ChatColor.RED + "You must be a player to run this command!" );
		Player player = ( Player ) sender;
		GunsmokeEntity entity = manager.getPlugin().getItemManager().getEntityWrapper( player );
		Validate.isTrue( manager.participating( entity ) != null, ChatColor.RED + "You must be in a game to leave!" );
		manager.leave( entity );
		player.sendMessage( "You have left your game" );
	}
}
