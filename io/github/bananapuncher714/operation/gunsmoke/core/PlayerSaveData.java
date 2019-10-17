package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.NBTCompound;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class PlayerSaveData {
	protected NBTCompound tags;
	protected GameMode gamemode;
	protected Location location;
	
	public PlayerSaveData( Player player ) {
		tags = GunsmokeUtil.getPlugin().getProtocol().getHandler().getPlayerCompound( player );
		gamemode = player.getGameMode();
		location = player.getLocation();
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public void apply( Player player ) {
		applyAt( player, location );
	}
	
	public void applyAt( Player player, Location location ) {
		// Teleport the player to the proper world before setting their whatever location, as well as their gamemode
		player.teleport( this.location );
		GunsmokeUtil.getPlugin().getProtocol().getHandler().setPlayerCompound( player, tags );
		player.teleport( location );
		player.setGameMode( GameMode.SPECTATOR );
		player.setGameMode( gamemode );
		
		GunsmokeUtil.getPlugin().getItemManager().getEntityWrapper( player ).setHealth( player.getHealth() );
	}
}