package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.util.List;

public class GeneralUtil {
	public static < T > void reverseList( List< T > list ) {
		int size = list.size() / 2;
		int lastIndex = list.size() - 1;
		for ( int i = 0; i < size; i++ ) {
			T temp = list.get( i );
			int reverseIndex = lastIndex - i; 
			list.set( i, list.get( reverseIndex ) );
			list.set( reverseIndex, temp );
		}
	}
}
