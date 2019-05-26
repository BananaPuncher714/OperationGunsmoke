package io.github.bananapuncher714.operation.gunsmoke.api.player;

import java.util.UUID;

public class GunsmokeEntity {
	UUID uuid;
	
	protected GunsmokeEntityHand leftHand;
	protected GunsmokeEntityHand rightHand;
	
	public GunsmokeEntityHand getLeftHand() {
		return leftHand;
	}
	
	public GunsmokeEntityHand getRightHand() {
		return rightHand;
	}
}
