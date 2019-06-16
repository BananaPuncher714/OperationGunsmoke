package io.github.bananapuncher714.operation.gunsmoke.api.movement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;

public class CrosshairMovement {
	protected Set< MovementModifier > modifiers = Collections.synchronizedSet( new HashSet< MovementModifier >() );
	
	public RelativeFacing getMovement( long time ) {
		double finYaw = 0;
		double finPitch = 0;
		for ( Iterator< MovementModifier > iterator = modifiers.iterator(); iterator.hasNext(); ) {
			MovementModifier modifier = iterator.next();
			ModifierOperation operation = modifier.getOperation();
			
			RelativeFacing relative = modifier.getMovement( time );
			if ( relative == null ) {
				iterator.remove();
				continue;
			}
			
			if ( operation == ModifierOperation.ADDITIVE ) {
				finYaw += relative.yaw;
				finPitch += relative.pitch;
			} else if ( operation == ModifierOperation.MULTIPLICATIVE ) {
				finYaw *= relative.yaw;
				finPitch *= relative.pitch;
			} else {
				GunsmokeUtil.log( "Invalid modifier operation detected! Outdated perhaps?", Level.SEVERE );
			}
		}
		
		return new RelativeFacing( finYaw, finPitch );
	}
	
	public void addMovementModifier( MovementModifier modifier ) {
		modifiers.add( modifier );
	}

	public enum ModifierOperation {
		ADDITIVE, MULTIPLICATIVE;
	}
	
	public interface MovementModifier {
		ModifierOperation getOperation();
		RelativeFacing getMovement( long time );
	}
	
	public static class RelativeFacing {
		public final double yaw;
		public final double pitch;
		
		public RelativeFacing( double yaw, double pitch ) {
			this.yaw = yaw;
			this.pitch = pitch;
		}
		
		public boolean isEmpty() {
			return yaw == 0 && pitch == 0;
		}
	}
}
