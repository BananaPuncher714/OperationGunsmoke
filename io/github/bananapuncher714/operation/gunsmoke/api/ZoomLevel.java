package io.github.bananapuncher714.operation.gunsmoke.api;

/**
 * Thought it might be nice to sort out the different FOVs you can get with walk speed and slowness
 * 
 * @author BananaPuncher714
 */
public enum ZoomLevel {
	_1( 0.001f, 0 ),
	_2( 0.001f, 1 ),
	_3( 0.001f, 2 ),
	_4( 0.001f, 3 ),
	_5( 0.001f, 4 ),
	_6( 0.001f, 5 ),
	_7( 0.001f, 6 ),
	_8( -0.5f, 5 ),
	_9( -0.5f, 6 ),
	_10( -0.2f, 7 ),
	_11( -0.1f, 8 ),
	_12( -0.1f, 9 ),
	_13( -0.05f, 10 ),
	_14( -0.015f, 11 );
	
	private final float speed;
	private final int amp;
	
	private ZoomLevel( float walkSpeed, int slowAmp ) {
		speed = walkSpeed;
		amp = slowAmp;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public int getSlowAmp() {
		return amp;
	}
}
