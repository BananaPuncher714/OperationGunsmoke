package io.github.bananapuncher714.operation.gunsmoke.api.movement;

import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.ModifierOperation;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.MovementModifier;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.RelativeFacing;

public class MovementModifierRecoil implements MovementModifier {
	protected double pitch;
	protected double yaw;
	protected double percentage = .7;
	
	protected double pitched = 0;
	protected double yawed = 0;
	protected double started;
	
	public MovementModifierRecoil( double maxPitch, double maxYaw ) {
		this.pitch = maxPitch;
		this.yaw = maxYaw;
		
		started = System.currentTimeMillis();
	}
	
	@Override
	public ModifierOperation getOperation() {
		return ModifierOperation.ADDITIVE;
	}

	@Override
	public RelativeFacing getMovement( long time ) {
		double pitchAmount = percentage * ( pitch - pitched );
		double yawAmount = percentage * ( yaw - yawed );
		
		pitched += pitchAmount;
		yawed += yawAmount;
		
		if ( Math.abs( pitchAmount ) < .05 ) {
			return null;
		}
		
		return new RelativeFacing( yawAmount, pitchAmount );
	}

}
