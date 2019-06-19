package io.github.bananapuncher714.operation.gunsmoke.api.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BlockPopulator< T > {
	List< T > valid = new ArrayList< T >();
	Set< T > checked = new HashSet< T >();
	Queue< T > checking = new ArrayDeque< T >();
	
	Collector< T > collector;
	Validator< T > validator;
	
	public BlockPopulator( Collector< T > collector, Validator< T > validator ) {
		this.collector = collector;
		this.validator = validator;
	}
	
	public Collection< T > gather( T object ) {
		checking.add( object );
		while ( !checking.isEmpty() ) {
			T first = checking.poll();
			if ( checked.contains( first ) ) {
				continue;
			}
			checked.add( first );
			if ( validator.isValid( first ) ) {
				valid.add( first );
				collector.collect( first, checking );
			}
		}
		
		List< T > set = new ArrayList< T >( valid );
		valid.clear();
		checked.clear();
		checking.clear();
		return set;
	}
	
	interface Collector< T > {
		void collect( T object, Collection< T > collection );
	}
	
	interface Validator< T > {
		boolean isValid( T object );
	}
}
