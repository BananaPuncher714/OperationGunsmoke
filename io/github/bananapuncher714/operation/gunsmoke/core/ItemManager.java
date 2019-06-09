package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.EnumInteractResult;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.item.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class ItemManager implements Listener {
	private Gunsmoke plugin;
	private Map< UUID, GunsmokeRepresentable > items = new ConcurrentHashMap< UUID, GunsmokeRepresentable >();
	
	public ItemManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
		Bukkit.getPluginManager().registerEvents( this, plugin );
	}
	
	private void update() {
		for ( Iterator< Entry< UUID, GunsmokeRepresentable > > iterator = items.entrySet().iterator(); iterator.hasNext(); ) {
			Entry< UUID, GunsmokeRepresentable > entry = iterator.next();
			GunsmokeRepresentable item = entry.getValue();
			if ( item instanceof Tickable ) {
				Tickable tickableItem = ( Tickable ) item;
				if ( tickableItem.tick() == EnumTickResult.CANCEL ) {
					iterator.remove();
				}
			}
		}
	}
	
	public void register( GunsmokeRepresentable item ) {
		items.put( item.getUUID(), item );
	}
	
	public GunsmokeRepresentable get( UUID uuid ) {
		return items.get( uuid );
	}
	
	public void remove( UUID uuid ) {
		GunsmokeRepresentable item = items.remove( uuid );
		if ( item != null ) {
			item.remove();
		}
	}
	
	public GunsmokeRepresentable getRepresentable( LivingEntity entity, EquipmentSlot slot ) {
		ItemStack item = BukkitUtil.getEquipment( entity, slot );
		return getRepresentable( item );
	}
	
	public GunsmokeRepresentable getRepresentable( ItemStack item ) {
		UUID id = GunsmokeItem.getUUID( item );
		if ( id == null ) {
			return null;
		}
		
		return get( id );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( EntityUpdateItemEvent event ) {
		ItemStack item = event.getItem();
		GunsmokeRepresentable representable = getRepresentable( item );
		if ( representable instanceof GunsmokeItem ) {
			GunsmokeItem gItem = ( GunsmokeItem ) representable;
			
			if ( gItem.isEquipped() ) {
				gItem.onUnequip();
			}
		}
		
		ItemStack newItem = BukkitUtil.getEquipment( event.getEntity(), event.getSlot() );
		GunsmokeRepresentable newRepresentable = getRepresentable( newItem );
		if ( newRepresentable instanceof GunsmokeItem ) {
			GunsmokeItem gItem = ( GunsmokeItem ) newRepresentable;
			
			if ( gItem.isEquipped() ) {
				gItem.onUnequip();
			}
			
			GunsmokeEntity entity = plugin.getEntityManager().getEntity( event.getEntity().getUniqueId() );
			gItem.onEquip( event.getEntity(), entity, event.getSlot() );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( AdvancementOpenEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( DropItemEvent event ) {
		Player player = event.getPlayer();
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			event.setCancelled( true );
			
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				mainInteractable.onClick( event );
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( PlayerSwapHandItemsEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
		
		if ( result == EnumInteractResult.COMPLETED ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( LeftClickEntityEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( LeftClickEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( RightClickEntityEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( RightClickEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( HoldRightClickEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( ReleaseRightClickEvent event ) {
		Player player = event.getPlayer();
		EnumInteractResult result = EnumInteractResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumInteractResult.SKIPPED || result == EnumInteractResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
	}
	
	/*
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent() {
		
	}
	*/
}
