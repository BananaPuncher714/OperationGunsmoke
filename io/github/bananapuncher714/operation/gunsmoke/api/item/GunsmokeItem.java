package io.github.bananapuncher714.operation.gunsmoke.api.item;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.operation.gunsmoke.api.GunsmokeRepresentable;
import io.github.bananapuncher714.operation.gunsmoke.api.events.player.PlayerProneEvent;
import io.github.bananapuncher714.operation.gunsmoke.api.player.GunsmokePlayer;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.NBTEditor;

public abstract class GunsmokeItem extends GunsmokeRepresentable {
	private final static Object[] CUSTOM = { "io", "github", "bananapuncher714", "operation", "gunsmoke", "item", "id" };
	
	protected LivingEntity holder;
	protected GunsmokePlayer gunsmokeHolder;
	protected EquipmentSlot slot;
	protected boolean isEquipped = false;
	
	public void onEquip( LivingEntity entity, GunsmokePlayer gunsmokeEntity, EquipmentSlot slot ) {
		this.holder = entity;
		this.gunsmokeHolder = gunsmokeEntity;
		this.slot = slot;
		isEquipped = true;
	}
	
	public void onUnequip() {
		holder = null;
		gunsmokeHolder = null;
		slot = null;
		isEquipped = false;
	}
	
	public EquipmentSlot getEquippedSlot() {
		return slot;
	}
	
	public LivingEntity getHolder() {
		return holder;
	}
	
	public boolean isEquipped() {
		return isEquipped;
	}
	
	public void dualWield( GunsmokeItem other ) {
	}
	
	public boolean canDualWieldWith( GunsmokeItem other ) {
		return true;
	}
	
	@Override
	public void remove() {
		if ( isEquipped ) {
			onUnequip();
		}
	}
	
	public void updateItem() {
		if ( holder != null ) {
			BukkitUtil.setEquipment( holder, getItem(), slot );
			GunsmokePlayer player = GunsmokeUtil.getPlugin().getEntityManager().getEntity( holder.getUniqueId() );
			player.updateHands();
		}
	}
	
	public abstract ItemStack getItem();
	
	public void onPlayerProneEvent( PlayerProneEvent event ) {
		Bukkit.getScheduler().runTaskLater( GunsmokeUtil.getPlugin(), this::updateItem, 1 );
	}
	
	protected static ItemStack markAsGunsmokeItem( ItemStack item, UUID uuid ) {
		return NBTEditor.set( item, uuid.toString(), CUSTOM );
	}
	
	public static UUID getUUID( ItemStack item ) {
		String value = NBTEditor.getString( item, CUSTOM );
		if ( value != null ) {
			return UUID.fromString( value );
		}
		return null;
	}
}
