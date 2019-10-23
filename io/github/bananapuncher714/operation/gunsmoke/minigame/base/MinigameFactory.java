package io.github.bananapuncher714.operation.gunsmoke.minigame.base;

import java.io.File;

import org.bukkit.command.CommandSender;

public interface MinigameFactory {
	Minigame get( CommandSender sender, String[] args );
	Minigame get( File baseDir );
	boolean save( File baseDir, Minigame game );
}
