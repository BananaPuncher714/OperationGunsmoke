package io.github.bananapuncher714.operation.gunsmoke.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackGunsmoke;
import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackGunsmokeRandom;
import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackMultiState;
import io.github.bananapuncher714.operation.gunsmoke.api.item.ItemStackMultiState.State;
import io.github.bananapuncher714.operation.gunsmoke.api.nms.PacketHandler;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.ReflectionUtils;
import io.github.bananapuncher714.operation.gunsmoke.tinyprotocol.TinyProtocolGunsmoke;

public class Gunsmoke extends JavaPlugin {
	protected TinyProtocolGunsmoke protocol;
	protected EntityManager entityManager;
	
	@Override
	public void onEnable() {
		PacketHandler handler = ReflectionUtils.getNewPacketHandlerInstance();
		protocol = new TinyProtocolGunsmoke( this, handler );
		
		entityManager = new EntityManager();
		
		Bukkit.getScheduler().runTaskTimer( this, this::run, 0, 1 );
	}
	
	/**
	 * Temporary
	 */
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			
			ItemStackGunsmoke standard = new ItemStackGunsmoke( new ItemStack( Material.TRIDENT ) );
			ItemStackGunsmoke bow = new ItemStackGunsmoke( new ItemStack( Material.BOW ) );
			
			ItemStackMultiState compound = new ItemStackMultiState( standard );
			compound.setItem( State.BOW, bow );

			GunsmokeEntity entity = entityManager.getEntity( player.getUniqueId() );
			entity.getMainHand().setItem( compound );
			entity.getMainHand().setState( entity.getMainHand().getState() == State.DEFAULT ? State.BOW : State.DEFAULT );
			
			ItemStackGunsmokeRandom randomItem = new ItemStackGunsmokeRandom( new ItemStackGunsmoke[] { new ItemStackGunsmoke( new ItemStack( Material.CHAINMAIL_CHESTPLATE ) ), new ItemStackGunsmoke( new ItemStack( Material.DIAMOND_CHESTPLATE ) ) } );
			entity.getEquipment().put( EquipmentSlot.CHEST, randomItem );
			
			entity.update();
		}
		
		return false;
	}
	
	/**
	 * Temporary too
	 */
	private void run() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			GunsmokeEntity entity = entityManager.getEntity( player.getUniqueId() );
//			entity.update();
		}
	}
	
	public TinyProtocolGunsmoke getProtocol() {
		return protocol;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
