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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumEventResult;
import io.github.bananapuncher714.operation.gunsmoke.api.EnumTickResult;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeEntityWrapperFactory;
import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.InteractableDamage;
import io.github.bananapuncher714.operation.gunsmoke.api.Tickable;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.GunsmokeEntity;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapper;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.bukkit.GunsmokeEntityWrapperProjectile;
import io.github.bananapuncher714.operation.gunsmoke.api.events.entity.GunsmokeEntityDamageEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.AdvancementOpenEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.DropItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.HoldRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.LeftClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerUpdateItemEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.ReleaseRightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEntityEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.RightClickEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItemInteractable;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

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
	
	public GunsmokeEntityWrapper getEntityWrapper( Entity entity ) {
		UUID uuid = entity.getUniqueId();
		if ( items.containsKey( uuid ) ) {
			GunsmokeRepresentable representable = items.get( uuid );
			if ( representable instanceof GunsmokeEntityWrapper ) {
				return ( GunsmokeEntityWrapper ) representable;
			}
		}
		return GunsmokeEntityWrapperFactory.wrap( entity );
	}
	
	public GunsmokeRepresentable getRepresentable( LivingEntity entity, EquipmentSlot slot ) {
		ItemStack item = BukkitUtil.getEquipment( entity, slot );
		return getRepresentable( item );
	}
	
	public GunsmokeRepresentable getRepresentable( ItemStack item ) {
		if ( item == null ) {
			return null;
		}
		UUID id = GunsmokeItem.getUUID( item );
		if ( id == null ) {
			return null;
		}
		
		return get( id );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( PlayerUpdateItemEvent event ) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		ItemStack newItem = BukkitUtil.getEquipment( player, event.getSlot() );
		GunsmokeRepresentable representable = getRepresentable( item );
		if ( representable instanceof GunsmokeItem ) {
			GunsmokeItem gItem = ( GunsmokeItem ) representable;
			
			if ( gItem.isEquipped() ) {
				gItem.onUnequip();
				BukkitUtil.setEquipment( player, newItem, event.getSlot() );
			}
		}
		
		GunsmokeRepresentable newRepresentable = getRepresentable( newItem );
		GunsmokeRepresentable otherRepresentable = getRepresentable( player, event.getSlot() == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND );
		if ( newRepresentable instanceof GunsmokeItem ) {
			GunsmokeItem gItem = ( GunsmokeItem ) newRepresentable;
			
			if ( gItem.isEquipped() ) {
				gItem.onUnequip();
			}
			
			GunsmokePlayer entity = plugin.getEntityManager().getEntity( player.getUniqueId() );
			
			// Make sure the slot is a hand slpt, since this event supports armor slots too
			if ( event.getSlot() == EquipmentSlot.HAND || event.getSlot() == EquipmentSlot.OFF_HAND ) {
				// Check to ensure no illegal dual wielding occurs
				if ( otherRepresentable instanceof GunsmokeItem ) {
					GunsmokeItem otherGItem = ( GunsmokeItem ) otherRepresentable;
					if ( !gItem.canDualWieldWith( otherGItem ) || !otherGItem.canDualWieldWith( gItem ) ) {
						for ( ItemStack extraItem : player.getInventory().addItem( newItem ).values() ) {
							player.getWorld().dropItem( player.getLocation(), extraItem );
						}
						
						if ( event.getSlot() == EquipmentSlot.HAND ) {
							player.getEquipment().setItemInMainHand( null );
						} else {
							player.getEquipment().setItemInOffHand( null );
						}
						
						return;
					}
				}
			}
			
			gItem.onEquip( player, entity, event.getSlot() );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( AdvancementOpenEvent event ) {
		Player player = event.getPlayer();
		EnumEventResult result = EnumEventResult.SKIPPED;
		
		for ( EquipmentSlot slot : GunsmokeUtil.getEquipmentSlotOrdering() ) {
			GunsmokeRepresentable representable = getRepresentable( player, slot );
			
			if ( representable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable interactable = ( GunsmokeItemInteractable ) representable;
				
				if ( interactable.isEquipped() ) {
					result = interactable.onClick( event );
				}
			}
			if ( result == EnumEventResult.COMPLETED || result == EnumEventResult.STOPPED ) {
				break;
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
		
		for ( EquipmentSlot slot : GunsmokeUtil.getEquipmentSlotOrdering() ) {
			GunsmokeRepresentable representable = getRepresentable( player, slot );
			
			if ( representable instanceof GunsmokeItemInteractable ) {
				GunsmokeItemInteractable interactable = ( GunsmokeItemInteractable ) representable;
				
				if ( interactable.isEquipped() ) {
					result = interactable.onClick( event );
				}
			}
			if ( result == EnumEventResult.COMPLETED || result == EnumEventResult.STOPPED ) {
				break;
			}
		}
		
		if ( result == EnumEventResult.COMPLETED ) {
			event.setCancelled( true );
		} else {
			GunsmokeRepresentable mainRepresentable = getRepresentable( player, EquipmentSlot.HAND );
			GunsmokeRepresentable offRepresentable = getRepresentable( player, EquipmentSlot.OFF_HAND );
			// We need to do some fast checking to see if they can be dual wielded, or else bad things will happen
			if ( mainRepresentable instanceof GunsmokeItem && offRepresentable instanceof GunsmokeItem ) {
				GunsmokeItem main = ( GunsmokeItem ) mainRepresentable;
				GunsmokeItem off = ( GunsmokeItem ) offRepresentable;
				if ( !main.canDualWieldWith( off ) || !off.canDualWieldWith( main )  ) {
					// This shouldn't actually ever get called, since it would mean
					// they'd have to be together in the first place,
					// which, should be caught as soon as one of them got equipped
					event.setCancelled( true );
				}
			}
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
	
	
	@EventHandler( priority = EventPriority.HIGH )
	private void onEvent( EntityDamageEvent event ) {
		plugin.getEntityManager().damage( getEntityWrapper( event.getEntity() ), event.getDamage(), DamageType.VANILLA, event.getCause() );
		event.setCancelled( true );
	}
	

	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( EntityDamageByEntityEvent event ) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		event.setCancelled( true );
		if ( damager instanceof LivingEntity ) {
			LivingEntity entity = ( LivingEntity ) damager;
			GunsmokeRepresentable representable = getRepresentable( entity, EquipmentSlot.HAND );
			if ( representable != null ) {
				return;
			}
		}
			
		plugin.getEntityManager().damage( getEntityWrapper( damaged ), event.getDamage(), DamageType.VANILLA, getEntityWrapper( damager ) );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( ProjectileHitEvent event ) {
		GunsmokeRepresentable entity = getEntityWrapper( event.getEntity() );
		
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
	private void onEvent( GunsmokeEntityDamageEvent event ) {
		GunsmokeEntity damagee = event.getRepresentable();
		if ( !( damagee instanceof GunsmokeEntityWrapper ) ) {
			return;
		}
		Entity entity = ( ( GunsmokeEntityWrapper ) damagee ).getEntity();
		if ( entity instanceof LivingEntity ) {
			LivingEntity lEntity = ( LivingEntity ) entity;
			EnumEventResult result = EnumEventResult.SKIPPED;

			for ( EquipmentSlot slot : GunsmokeUtil.getEquipmentSlotOrdering() ) {
				GunsmokeRepresentable representable = getRepresentable( lEntity, slot );

				if ( representable instanceof GunsmokeItemInteractable ) {
					GunsmokeItemInteractable interactable = ( GunsmokeItemInteractable ) representable;

					if ( interactable.isEquipped() ) {
						if ( interactable instanceof InteractableDamage ) {
							InteractableDamage damageInteractable = ( InteractableDamage ) interactable;
							result = damageInteractable.onTakeDamage( event );
						}
					}
				}
				if ( result == EnumEventResult.COMPLETED || result == EnumEventResult.STOPPED ) {
					break;
				}
			}
		}
	}
	
//	@EventHandler( priority = EventPriority.HIGHEST )
//	private void onEvent( GunsmokeEntityDamageByEntityEvent event ) {
//		GunsmokeEntity damagee = event.getDamager();
//		if ( !( damagee instanceof GunsmokeEntityWrapper ) ) {
//			return;
//		}
//		Entity entity = ( ( GunsmokeEntityWrapper ) damagee ).getEntity();
//		if ( entity instanceof LivingEntity ) {
//			LivingEntity lEntity = ( LivingEntity ) entity;
//			EnumEventResult result = EnumEventResult.SKIPPED;
//
//			for ( EquipmentSlot slot : GunsmokeUtil.getEquipmentSlotOrdering() ) {
//				GunsmokeRepresentable representable = getRepresentable( lEntity, slot );
//
//				if ( representable instanceof GunsmokeItemInteractable ) {
//					GunsmokeItemInteractable interactable = ( GunsmokeItemInteractable ) representable;
//
//					if ( interactable.isEquipped() ) {
//						if ( interactable instanceof InteractableDamage ) {
//							InteractableDamage damageInteractable = ( InteractableDamage ) interactable;
//							result = damageInteractable.onEvent( event );
//						}
//					}
//				}
//				if ( result == EnumEventResult.COMPLETED || result == EnumEventResult.STOPPED ) {
//					break;
//				}
//			}
//		}
//	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( BlockBreakEvent event ) {
		plugin.getBlockManager().destroy( event.getBlock().getLocation() );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( BlockPlaceEvent event ) {
		plugin.getBlockManager().unregisterBlock( event.getBlock().getLocation() );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( BlockPhysicsEvent event ) {
		plugin.getBlockManager().unregisterBlock( event.getBlock().getLocation() );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onEvent( BlockFromToEvent event ) {
		plugin.getBlockManager().unregisterBlock( event.getBlock().getLocation() );
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
	*/
}
