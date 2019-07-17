package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;

public class NPCManager {
	protected Gunsmoke plugin;
	
	protected Map< UUID, GunsmokeNPC > npcs = new HashMap< UUID, GunsmokeNPC >();
	
	public NPCManager( Gunsmoke plugin ) {
		this.plugin = plugin;
	}
	
	public GunsmokeNPC getNPC( UUID uuid ) {
		return npcs.get( uuid );
	}
	
	public void register( GunsmokeNPC npc ) {
		npcs.put( npc.getPlayer().getUniqueId(), npc );
	}
	
	public void remove( UUID uuid ) {
		GunsmokeNPC npc = npcs.remove( uuid );
		if ( npc != null ) {
			npc.remove();
		}
	}
	
	public Collection< GunsmokeNPC > getNPCs() {
		return npcs.values();
	}
	
	protected void disable() {
		for ( GunsmokeNPC npc : npcs.values() ) {
			npc.remove();
		}
		npcs.clear();
	}
}
