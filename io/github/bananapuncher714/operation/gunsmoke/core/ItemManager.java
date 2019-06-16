package io.github.bananapuncher714.operation.gunsmoke.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.EntityUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;

public class ItemManager implements Listener {
	private Gunsmoke plugin;
	private Map< UUID, GunsmokeRepresentable > items = new ConcurrentHashMap< UUID, GunsmokeRepresentable >();
	
	public ItemManager( Gunsmoke plugin ) {
		this.plugin = plugin;
		
		// Tick timer for Tickables
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
		
		// This is to capture events to pass them onto whatever things are registered
		Bukkit.getPluginManager().registerEvents( this, plugin );
	}
	
	private void update() {
		for ( Iterator< Entry< UUID, GunsmokeRepresentable > > iterator = items.entrySet().iterator(); iterator.hasNext(); ) {
			Entry< UUID, GunsmokeRepresentable > entry = iterator.next();
			GunsmokeRepresentable item = entry.getValue();
			if ( item instanceof Tickable ) {
				Tickable tickableItem = ( Tickable ) item;
				if ( tickableItem.tick() == EnumTickResult.CANCEL ) {
					item.remove();
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
	
	public GunsmokeRepresentable getRepresentable( Entity entity ) {
		UUID uuid = entity.getUniqueId();
		return get( uuid );
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
			
			GunsmokePlayer entity = plugin.getEntityManager().getEntity( event.getEntity().getUniqueId() );
			gItem.onEquip( event.getEntity(), entity, event.getSlot() );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( AdvancementOpenEvent event ) {
		Player player = event.getPlayer();
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			if ( offRepresentable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable offInteractable = ( GunsmokeItemInteractable ) offRepresentable;

				if ( offInteractable.isEquipped() ) {
					result = offInteractable.onClick( event );
				}
			}
		}
		
		if ( result == EnumEventResult.COMPLETED ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( LeftClickEntityEvent event ) {
		Player player = event.getPlayer();
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
		if ( mainRepresentable instanceof GunsmokeItemInteractable ) {
			GunsmokeItemInteractable mainInteractable = ( GunsmokeItemInteractable ) mainRepresentable;

			if ( mainInteractable.isEquipped() ) {
				result = mainInteractable.onClick( event );
			}
		}
		
		if ( result == EnumEventResult.SKIPPED || result == EnumEventResult.PROCESSED ) {
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
	private void onEvent( EntityDamageEvent event ) {
		GunsmokeRepresentable gEntity = getRepresentable( event.getEntity() );
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		if ( gEntity instanceof GunsmokeEntityWrapper ) {
			GunsmokeEntityWrapper entity = ( GunsmokeEntityWrapper ) gEntity;
			
			result = entity.onEvent( event );
		}
		
		if ( result == EnumEventResult.SKIPPED && event instanceof EntityDamageByEntityEvent ) {
			EntityDamageByEntityEvent damageEvent = ( EntityDamageByEntityEvent ) event;
			GunsmokeRepresentable damager = getRepresentable( damageEvent.getDamager() );
			
			if ( damager instanceof GunsmokeEntityWrapper ) {
				GunsmokeEntityWrapper damagerWrapper = ( GunsmokeEntityWrapper ) damager;
				
				result = damagerWrapper.onEvent( event );
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( ProjectileHitEvent event ) {
		GunsmokeRepresentable entity = getRepresentable( event.getEntity() );
		
		if ( entity instanceof GunsmokeEntityWrapperProjectile ) {
			GunsmokeEntityWrapperProjectile projectile = ( GunsmokeEntityWrapperProjectile ) entity;
			
			projectile.onEvent( event );
		}
	}
	

	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( ProjectileLaunchEvent event ) {
		Projectile projectile = event.getEntity();
		ProjectileSource source = projectile.getShooter();
		if ( source instanceof LivingEntity ) {
			LivingEntity shooter = ( LivingEntity ) source;
			GunsmokeRepresentable main = getRepresentable( shooter, EquipmentSlot.HAND );
			if ( main != null ) {
				event.setCancelled( true );
				return;
			}
			
			GunsmokeRepresentable off = getRepresentable( shooter, EquipmentSlot.OFF_HAND );
			if ( off != null ) {
				event.setCancelled( true );
				return;
			}
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( EntityDamageByEntityEvent event ) {
		Entity damager = event.getDamager();
		if ( damager instanceof LivingEntity ) {
			LivingEntity entity = ( LivingEntity ) damager;
			GunsmokeRepresentable representable = getRepresentable( entity, EquipmentSlot.HAND );
			if ( representable != null ) {
				event.setCancelled( true );
			}
		}
	}
	
//	@EventHandler( priority = EventPriority.HIGHEST )
//	private void onEvent() {
//		
//	}
//	
//	@EventHandler( priority = EventPriority.HIGHEST )
//	private void onEvent() {
//		
//	}
	
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
	*/
}
