package io.github.bananapuncher714.operation.gunsmoke.api.movement;

public class CrosshairMovementStandard extends CrosshairMovement {
	protected double amp = 2;
	protected double duration = 3000;
	
	public CrosshairMovementStandard( double amp, double duration ) {
		this.amp = amp;
		this.duration = duration;
	}
	
	@Override
	public RelativeFacing getMovement( long time ) {
		RelativeFacing facing = super.getMovement( time );
		
		if ( amp > 0 ) {
			double percentage = time % duration / duration;
			
			double radians = Math.toRadians( percentage * 360 );
			double yawMovement = Math.cos( radians ) * amp;
			double pitchMovement = Math.cos( radians * 2 ) * amp;
			
			return new RelativeFacing( facing.yaw + yawMovement, facing.pitch + pitchMovement );
		}
		
		return facing;
	}
}
