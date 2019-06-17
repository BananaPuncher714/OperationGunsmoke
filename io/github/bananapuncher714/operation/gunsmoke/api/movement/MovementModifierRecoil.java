package io.github.bananapuncher714.operation.gunsmoke.api.movement;

import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.ModifierOperation;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.MovementModifier;
import io.github.bananapuncher714.operation.gunsmoke.api.movement.CrosshairMovement.RelativeFacing;

public class MovementModifierRecoil implements MovementModifier {
	protected double pitch;
	protected double yaw;
	protected long duration;
	protected double percentage = .7;
	
	protected double pitched = 0;
	protected double yawed = 0;
	protected double started;
	
	public MovementModifierRecoil( double maxPitch, double maxYaw, long duration ) {
		this.pitch = maxPitch;
		this.yaw = maxYaw;
		this.duration = duration;
		
		started = System.currentTimeMillis();
	}
	
	@Override
	public ModifierOperation getOperation() {
		return ModifierOperation.ADDITIVE;
	}

	@Override
	public RelativeFacing getMovement( long time ) {
		double timeLeft = time - started;
		if ( timeLeft > duration ) {
			return null;
		}
		
		double pitchAmount = percentage * ( pitch - pitched );
		double yawAmount = percentage * ( yaw - yawed );
		
		pitched += pitchAmount;
		yawed += yawAmount;
		
		return new RelativeFacing( yawAmount, pitchAmount );
	}

}
