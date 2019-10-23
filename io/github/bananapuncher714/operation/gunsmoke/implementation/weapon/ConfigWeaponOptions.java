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
	protected double spreadStart;
	protected double spreadEnd;
	protected int spreadShots;
	protected int spreadRecover;
	
	protected int shots;
	protected double bulletSpread;
	
	protected boolean useAmmo;
	protected int clipSize;
	protected int reloadDelay;
	protected int reloadAmount;
	
	protected ZoomLevel zoom;
	protected double scopeRecoilYaw;
	protected double scopeRecoilPitch;
	protected double scopeSpreadStart;
	protected double scopeSpreadEnd;
	protected double scopeSpreadShots;
	protected double scopeSpreadRecover;
	protected int scopeDelay;
	protected double scopeSpeed;
	
	protected int switchDelay;
	
	protected String name;
	protected int model;

	public ConfigWeaponOptions( FileConfiguration config ) {
		name = config.getString( "name", "Test gun" ).replace( '&', '\u00A7' );
		model = config.getInt( "model", 0 );
		
		bullet = GunsmokeImplementation.getInstance().getBullet( config.getString( "bullet" ) );
		automatic = config.getBoolean( "automatic" );
		shootDelay = config.getInt( "shoot-delay" );
		recoilYaw = config.getDouble( "recoil-yaw" );
		recoilPitch = config.getDouble( "recoil-pitch" );
		spreadStart = config.getDouble( "spread-start" );
		spreadEnd = config.getDouble( "spread-end" );
		spreadShots = config.getInt( "spread-shots" );
		spreadRecover = config.getInt( "spread-recover-time" );
		
		shots = config.getInt( "shots" );
		bulletSpread = config.getDouble( "bullet-spread" );
		
		useAmmo = config.getBoolean( "use-ammo" );
		clipSize = config.getInt( "clip-size" );
		reloadDelay = config.getInt( "reload-delay" );
		reloadAmount = config.getInt( "reload-amount" );
		
		zoom = config.getBoolean( "scope" ) ? ZoomLevel.valueOf( config.getString( "zoom-level" ).toUpperCase() ) : null;
		scopeRecoilYaw = config.getDouble( "scoped-recoil-yaw" );
		scopeRecoilPitch = config.getDouble( "scoped-recoil-pitch" );
		scopeSpreadStart = config.getDouble( "scoped-spread-start" );
		scopeSpreadEnd = config.getDouble( "scoped-spread-end" );
		scopeSpreadShots = config.getInt( "scoped-spread-shots" );
		scopeSpreadRecover = config.getInt( "scoped-spread-recover-time" );
		scopeDelay = config.getInt( "scope-delay" );
		scopeSpeed = config.getDouble( "scope-speed", 1 );
		
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

	public double getSpreadStart() {
		return spreadStart;
	}

	public void setSpreadStart( double spreadStart ) {
		this.spreadStart = spreadStart;
	}

	public double getSpreadEnd() {
		return spreadEnd;
	}

	public void setSpreadEnd( double spreadEnd ) {
		this.spreadEnd = spreadEnd;
	}

	public int getSpreadShots() {
		return spreadShots;
	}

	public void setSpreadShots( int spreadTime ) {
		this.spreadShots = spreadTime;
	}

	public int getSpreadRecover() {
		return spreadRecover;
	}

	public void setSpreadRecover( int spreadRecover ) {
		this.spreadRecover = spreadRecover;
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

	public double getScopeSpreadStart() {
		return scopeSpreadStart;
	}

	public void setScopeSpreadStart( double scopeSpreadStart ) {
		this.scopeSpreadStart = scopeSpreadStart;
	}

	public double getScopeSpreadEnd() {
		return scopeSpreadEnd;
	}

	public void setScopeSpreadEnd( double scopeSpreadEnd ) {
		this.scopeSpreadEnd = scopeSpreadEnd;
	}

	public double getScopeSpreadShots() {
		return scopeSpreadShots;
	}

	public void setScopeSpreadShots( double scopeSpreadTime ) {
		this.scopeSpreadShots = scopeSpreadTime;
	}

	public double getScopeSpreadRecover() {
		return scopeSpreadRecover;
	}

	public void setScopeSpreadRecover( double scopeSpreadRecover ) {
		this.scopeSpreadRecover = scopeSpreadRecover;
	}

	public int getScopeDelay() {
		return scopeDelay;
	}

	public void setScopeDelay( int scopeDelay ) {
		this.scopeDelay = scopeDelay;
	}
	
	public double getScopeSpeed() {
		return scopeSpeed;
	}

	public void setScopeSpeed( double scopeSpeed ) {
		this.scopeSpeed = scopeSpeed;
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
