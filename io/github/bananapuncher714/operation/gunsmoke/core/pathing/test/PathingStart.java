package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class PathingStart {

	public static void main( String[] args ) {
		AABB[] lines = new AABB[ 20 ];
		Random rand = ThreadLocalRandom.current();
		for ( int i = 0; i < lines.length; i++ ) {
			double min = rand.nextDouble() * 7 - 4;
			double max = Math.abs( rand.nextDouble() * 7 ) + min + 1;
			System.out.println( min + " : " + max );
			lines[ i ] = new AABB( i - lines.length + 10, 0, max, i - lines.length + 10, 0, min );
		}
		PathingPanel.draw( new AABB( -11, 0, 0, -11, 0, 0 ), lines );
	}

}
