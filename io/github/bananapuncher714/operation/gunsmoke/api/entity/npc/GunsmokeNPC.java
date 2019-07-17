package io.github.bananapuncher714.operation.gunsmoke.api.entity.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface GunsmokeNPC {
	void interact( NPCAction action );
	void moveTo( Location location );
	void jump();
	void look( float yaw, float pitch );
	void look( Location location );
	
	void remove();
	Player getPlayer();
}
