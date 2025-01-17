package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.awt.Graphics;

import javax.swing.JPanel;

public abstract class PaintPanel extends JPanel {
	protected int centerX = 400;
	protected int centerY = 400;
	protected Graphics g;
	
	protected int scale = 1;
	
	public void drawPoint( double x, double y ) {
		g.fillRect( centerX + ( int ) ( x * scale ), centerY + ( int ) ( y * scale ), 1, 1 );
	}
	
	public void drawLine( double x1, double y1, double x2, double y2 ) {
		double s = Math.max( Math.abs( x1 - x2 ), Math.abs( y1 - y2 ) ) * scale;
		for ( double i = 0; i <= s; i++ ) {
			drawPoint( x2 + ( x1 - x2 ) * ( i / s ), y2 + ( y1 - y2 ) * ( i / s ) );
		}
	}
	
	public void drawString( String string, double x, double y ) {
		int ix = ( int ) ( x * scale ) + centerX;
		int iy = ( int ) ( y * scale ) + centerY + 10;
		g.drawString( string, ix, iy );
	}
	
	public void drawLineThin( double x1, double y1, double x2, double y2 ) {
		g.drawLine( ( int ) ( scale * x1 ) + centerX, ( int ) ( y1 * scale ) + centerY, ( int ) ( x2 * scale ) + centerX, ( int ) ( y2 * scale ) + centerY );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		this.g = g;
	}
	
	public int getCenterX() {
		return centerX;
	}

	public void setCenterX( int centerX ) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY( int centerY ) {
		this.centerY = centerY;
	}

	public int getScale() {
		return scale;
	}

	public void setScale( int scale ) {
		this.scale = scale;
	}
}
