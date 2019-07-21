package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;

public class PathingPanel extends PaintPanel implements WindowListener {
	public static PathingPanel INSTANCE;
	
	public static void draw( AABB player, AABB... boxes ) {
		if ( INSTANCE == null ) {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					new PathingPanel( player, boxes );
				}
			} );
		} else {
			INSTANCE.set( player, boxes );
		}
	}
	
	private JFrame frame;
	
	private final int windowWidth = 800;
	private final int windowHeight = 800;
	
	private AABB player;
	private AABB[] boxes;
	
	protected PathingPanel( AABB player, AABB... boxes ) {
		INSTANCE = this;
		
		System.out.println( "Box length: " + boxes.length );
		
		this.player = player;
		this.boxes = boxes;

		frame = new JFrame( "2D Pathfinder" );

		setCenterX( windowWidth / 2 );
		setCenterY( windowHeight / 2 );
		setScale( 32 );

		frame.add( this );

		frame.setSize( windowWidth, windowHeight );
		frame.setVisible( true );
		frame.setResizable( true );
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );

		g.setColor( Color.BLUE );
		draw( player );

		boolean calculateCorners = true;
		boolean calculateRegions = true;

		if ( calculateCorners ) {
			Set< AABB > corners = new HashSet< AABB >();
			for ( AABB box : boxes ) {
				// Do some tests
				AABB nw = new AABB( player.lenX + box.maxX,
						0,
						player.lenZ + box.maxZ,
						box.maxX,
						0,
						box.maxZ );
				corners.add( nw );

				AABB ne = new AABB( player.lenX + box.maxX,
						0,
						box.minZ,
						box.maxX,
						0,
						box.minZ - player.lenZ );
				corners.add( ne );

				AABB sw = new AABB( box.minX,
						0,
						player.lenZ + box.maxZ,
						box.minX - player.lenX,
						0,
						box.maxZ );
				corners.add( sw );

				AABB se = new AABB( box.minX,
						0,
						box.minZ,
						box.minX - player.lenX,
						0,
						box.minZ - player.lenZ );
				corners.add( se );

				AABB n1 = new AABB( box.maxX,
						0,
						box.minZ,
						box.maxX - player.lenX,
						0,
						box.minZ - player.lenZ );
				corners.add( n1 );

				AABB n2 = new AABB( box.minX,
						0,
						box.maxZ,
						box.minX - player.lenX,
						0,
						box.maxZ - player.lenZ );
				corners.add( n2 );

				AABB e1 = new AABB( box.minX + player.lenX,
						0,
						box.minZ,
						box.minX,
						0,
						box.minZ - player.lenZ );
				corners.add( e1 );

				AABB e2 = new AABB( box.minX,
						0,
						box.minZ + player.lenZ,
						box.minX - player.lenX,
						0,
						box.minZ );
				corners.add( e2 );

				AABB w1 = new AABB( box.maxX,
						0,
						box.maxZ + player.lenZ,
						box.maxX - player.lenX,
						0,
						box.maxZ );
				corners.add( w1 );

				AABB w2 = new AABB( box.maxX + player.lenX,
						0,
						box.maxZ,
						box.maxX,
						0,
						box.maxZ - player.lenZ );
				corners.add( w2 );

				AABB s1 = new AABB( box.maxX + player.lenX,
						0,
						box.minZ + player.lenZ,
						box.maxX,
						0,
						box.minZ );
				corners.add( s1 );

				AABB s2 = new AABB( box.minX + player.lenX,
						0,
						box.maxZ + player.lenZ,
						box.minX,
						0,
						box.maxZ );
				corners.add( s2 );
			}

			if ( calculateRegions ) {
				// TODO do something
			}
			
			for ( AABB corner : corners ) {
				if ( intersects2d( corner, boxes ) ) {
					continue;
				}
				Vector toBox = new Vector( corner.oriX - player.oriX, corner.oriY - player.oriY, corner.oriZ - player.oriZ );
				boolean canSee = true;
				for ( AABB box : boxes ) {
					double dist = swept2d( player, toBox, box );
					if ( dist != 1 ) {
						canSee = false;
						break;
					}
				}
				if ( canSee ) {
					g.setColor( Color.GREEN );
					draw( corner );
				}
			}
		}
		
		if ( calculateRegions ) {
			
		}
		
		g.setColor( Color.BLACK );
		for ( AABB box : boxes ) {
			draw( box );
		}
	}
	
	private void draw( AABB box ) {
		this.drawLine( box.minX, box.minZ, box.maxX, box.minZ );
		this.drawLine( box.maxX, box.minZ, box.maxX, box.maxZ );
		this.drawLine( box.maxX, box.maxZ, box.minX, box.maxZ );
		this.drawLine( box.minX, box.maxZ, box.minX, box.minZ );
	}
	
	private double swept2d( AABB box, Vector movement, AABB check ) {
		double distEntX;
		double distEntY;
		double distExtX;
		double distExtY;
		
		if ( movement.getX() > 0 ) {
			distEntX = check.minX - box.maxX;
			distExtX = check.maxX - box.minX;
		} else {
			distEntX = check.maxX - box.minX;
			distExtX = check.minX - box.maxX;
		}
		
		if ( movement.getZ() > 0 ) {
			distEntY = check.minZ - box.maxZ;
			distExtY = check.maxZ - box.minZ;
		} else {
			distEntY = check.maxZ - box.minZ;
			distExtY = check.minZ - box.maxZ;
		}
		
		double entryX;
		double entryY;
		double exitX;
		double exitY;
		
		if ( movement.getX() == 0 ) {
			entryX = Double.NEGATIVE_INFINITY;
			exitX = Double.POSITIVE_INFINITY;
		} else {
			entryX = distEntX / movement.getX();
			exitX = distExtX / movement.getX();
		}
		
		if ( movement.getZ() == 0 ) {
			entryY = Double.NEGATIVE_INFINITY;
			exitY = Double.POSITIVE_INFINITY;
		} else {
			entryY = distEntY / movement.getZ();
			exitY = distExtY / movement.getZ();
		}
		
		double entryTime = Math.max( entryX, entryY );
		double exitTime = Math.min( exitX, exitY );
		
		 if ( entryTime >= exitTime || ( entryX < 0 && entryY < 0 ) || entryX >= 1 || entryY >= 1 ) {
			 return 1;
		 } else {
			 return 0;
		 }
	}
	
	private boolean intersects2d( AABB box, AABB[] other ) {
		for ( AABB otherBox : boxes ) {
			if ( Math.abs( box.oriX - otherBox.oriX ) < box.radX + otherBox.radX ) {
				if ( Math.abs( box.oriZ - otherBox.oriZ ) < box.radZ + otherBox.radZ ) {
					return true;
				}					
			}
		}
		return false;
	}
	
	public void set( AABB player, AABB... boxes ) {
		this.player = player;
		this.boxes = boxes;
		repaint();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		INSTANCE = null;
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		INSTANCE = null;
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}
}
