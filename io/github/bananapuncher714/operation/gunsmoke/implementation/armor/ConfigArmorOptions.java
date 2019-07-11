package io.github.bananapuncher714.operation.gunsmoke.implementation.armor;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.EquipmentSlot;

import io.github.bananapuncher714.operation.gunsmoke.api.DamageType;

public class ConfigArmorOptions {
	protected int physicalArmor;
	protected int misakaArmor;
	protected int chemicalArmor;
	protected int explosionArmor;
	protected int fireArmor;
	protected int vanillaArmor;
	
	protected EquipmentSlot slot;
	
	protected String name;
	protected Material visibleItem;
	protected Material clientItem;
	
	public ConfigArmorOptions( FileConfiguration config ) {
		physicalArmor = config.getInt( "physical-armor" );
		misakaArmor = config.getInt( "misaka-armor" );
		chemicalArmor = config.getInt( "chemical-armor" );
		explosionArmor = config.getInt( "explosion-armor" );
		fireArmor = config.getInt( "fire-armor" );
		vanillaArmor = config.getInt( "vanilla-armor" );
		
		slot = EquipmentSlot.valueOf( config.getString( "slot" ).toUpperCase() );
		
		name = config.getString( "name" );
		visibleItem = Material.getMaterial( config.getString( "visible-item" ).toUpperCase() );
		clientItem = Material.getMaterial( config.getString( "client-item" ).toUpperCase() );
	}
	
	public int get( DamageType type ) {
		switch ( type ) {
		case CHEMICAL: return chemicalArmor;
		case EXPLOSION: return explosionArmor;
		case FIRE: return fireArmor;
		case MISAKA: return misakaArmor;
		case PHYSICAL: return physicalArmor;
		case VANILLA: return vanillaArmor;
		default: return 0;
		}
	}
	
	public void set( DamageType type, int resistance ) {
		switch ( type ) {
		case CHEMICAL: chemicalArmor = resistance;
			break;
		case EXPLOSION: explosionArmor = resistance;
			break;
		case FIRE: fireArmor = resistance;
			break;
		case MISAKA: misakaArmor = resistance;
			break;
		case PHYSICAL: physicalArmor = resistance;
			break;
		case VANILLA: vanillaArmor = resistance;
			break;
		}
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public void setSlot( EquipmentSlot slot ) {
		this.slot = slot;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Material getVisibleItem() {
		return visibleItem;
	}

	public void setVisibleItem( Material visibleItem ) {
		this.visibleItem = visibleItem;
	}

	public Material getClientItem() {
		return clientItem;
	}

	public void setClientItem( Material clientItem ) {
		this.clientItem = clientItem;
	}
}
