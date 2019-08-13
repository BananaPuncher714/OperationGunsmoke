package io.github.bananapuncher714.operation.gunsmoke.core.pathing;

public class ElevationEdge {
	Region from;
	Region to;
	ElevationLayer fromLayer;
	ElevationLayer toLayer;
	
	public ElevationEdge( Region from, ElevationLayer fromLayer, Region to, ElevationLayer toLayer ) {
		this.from = from;
		this.to = to;
		this.fromLayer = fromLayer;
		this.toLayer = toLayer;
	}
	
	public Region getFrom() {
		return from;
	}

	public Region getTo() {
		return to;
	}

	public ElevationLayer getFromLayer() {
		return fromLayer;
	}

	public ElevationLayer getToLayer() {
		return toLayer;
	}

	public boolean traversable( double jumpHeight ) {
		return to.getRegion().minY - from.getRegion().minY <= jumpHeight;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		ElevationEdge other = ( ElevationEdge ) obj;
		return ( from == other.from && to == other.to ) || ( from == other.to && to == other.from );
	}
}
