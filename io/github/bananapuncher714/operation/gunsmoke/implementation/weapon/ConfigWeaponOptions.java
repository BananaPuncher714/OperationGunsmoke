package io.github.bananapuncher714.operation.gunsmoke.implementation.weapon;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.bananapuncher714.operation.gunsmoke.api.ZoomLevel;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.implementation.projectile.bullet.ConfigBulletOptions;

public class ConfigWeaponOptions {
	protected ConfigBulletOptions bullet;
	protected boolean automatic;
	protected int shootDelay;
	protected double recoilYaw;
	protected double recoilPitch;
	protected double spread;
	
	protected int shots;
	protected double bulletSpread;
	
	protected boolean useAmmo;
	protected int clipSize;
	protected int reloadDelay;
	protected int reloadAmount;
	
	protected ZoomLevel zoom;
	protected double scopeRecoilYaw;
	protected double scopeRecoilPitch;
	protected double scopeSpread;
	protected int scopeDelay;
	
	protected int switchDelay;
	
	protected String name;
	protected int model;

	public ConfigWeaponOptions( FileConfiguration config ) {
		name = config.getString( "name", "Test gun" );
		model = config.getInt( "model", 0 );
		
		bullet = GunsmokeImplementation.getInstance().getBullet( config.getString( "bullet" ) );
		automatic = config.getBoolean( "automatic" );
		shootDelay = config.getInt( "shoot-delay" );
		recoilYaw = config.getDouble( "recoil-yaw" );
		recoilPitch = config.getDouble( "recoil-pitch" );
		spread = config.getDouble( "spread" );
		
		shots = config.getInt( "shots" );
		bulletSpread = config.getDouble( "bullet-spread" );
		
		useAmmo = config.getBoolean( "use-ammo" );
		clipSize = config.getInt( "clip-size" );
		reloadDelay = config.getInt( "reload-delay" );
		reloadAmount = config.getInt( "reload-amount" );
		
		zoom = config.getBoolean( "scope" ) ? ZoomLevel.valueOf( config.getString( "zoom-level" ).toUpperCase() ) : null;
		scopeRecoilYaw = config.getDouble( "scoped-recoil-yaw" );
		scopeRecoilPitch = config.getDouble( "scoped-recoil-pitch" );
		scopeSpread = config.getDouble( "scoped-spread" );
		scopeDelay = config.getInt( "scope-delay" );
		
		switchDelay = config.getInt( "switch-delay" );
	}
	
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public int getModel() {
		return model;
	}

	public void setModel( int model ) {
		this.model = model;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic( boolean automatic ) {
		this.automatic = automatic;
	}

	public int getShootDelay() {
		return shootDelay;
	}

	public void setShootDelay( int shootDelay ) {
		this.shootDelay = shootDelay;
	}

	public double getRecoilYaw() {
		return recoilYaw;
	}

	public void setRecoilYaw( double recoilYaw ) {
		this.recoilYaw = recoilYaw;
	}

	public double getRecoilPitch() {
		return recoilPitch;
	}

	public void setRecoilPitch( double recoilPitch ) {
		this.recoilPitch = recoilPitch;
	}

	public double getSpread() {
		return spread;
	}

	public void setSpread( double spread ) {
		this.spread = spread;
	}

	public int getShots() {
		return shots;
	}

	public void setShots( int shots ) {
		this.shots = shots;
	}

	public double getBulletSpread() {
		return bulletSpread;
	}

	public void setBulletSpread( double bulletSpread ) {
		this.bulletSpread = bulletSpread;
	}

	public boolean isUseAmmo() {
		return useAmmo;
	}

	public void setUseAmmo( boolean useAmmo ) {
		this.useAmmo = useAmmo;
	}

	public int getClipSize() {
		return clipSize;
	}

	public void setClipSize( int clipSize ) {
		this.clipSize = clipSize;
	}

	public int getReloadDelay() {
		return reloadDelay;
	}

	public void setReloadDelay( int reloadDelay ) {
		this.reloadDelay = reloadDelay;
	}

	public int getReloadAmount() {
		return reloadAmount;
	}

	public void setReloadAmount( int reloadAmount ) {
		this.reloadAmount = reloadAmount;
	}

	public ZoomLevel getZoom() {
		return zoom;
	}

	public void setZoom( ZoomLevel zoom ) {
		this.zoom = zoom;
	}

	public double getScopeRecoilYaw() {
		return scopeRecoilYaw;
	}

	public void setScopeRecoilYaw( double scopeRecoilYaw ) {
		this.scopeRecoilYaw = scopeRecoilYaw;
	}

	public double getScopeRecoilPitch() {
		return scopeRecoilPitch;
	}

	public void setScopeRecoilPitch( double scopeRecoilPitch ) {
		this.scopeRecoilPitch = scopeRecoilPitch;
	}

	public double getScopeSpread() {
		return scopeSpread;
	}

	public void setScopeSpread( double scopeSpread ) {
		this.scopeSpread = scopeSpread;
	}

	public int getScopeDelay() {
		return scopeDelay;
	}

	public void setScopeDelay( int scopeDelay ) {
		this.scopeDelay = scopeDelay;
	}

	public int getSwitchDelay() {
		return switchDelay;
	}

	public void setSwitchDelay( int switchDelay ) {
		this.switchDelay = switchDelay;
	}

	public ConfigBulletOptions getBullet() {
		return bullet;
	}
}
