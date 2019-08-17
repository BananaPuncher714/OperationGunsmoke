package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.node.ComparableVec;

public class RegionPanel extends PaintPanel implements MouseListener {
	public static RegionPanel INSTANCE;
	private static boolean paused = false;
	
	public static void draw( Vector start, PathRegion region, ComparableVec vec, Edge edge ) {
		if ( INSTANCE == null ) {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					new RegionPanel();
				}
			} );
		} else {
			INSTANCE.set( start, region, vec, edge );
		}
	}
	
	private JFrame frame;
	
	private final int windowWidth = 800;
	private final int windowHeight = 800;
	
	private Vector start;
	private PathRegion region;
	private ComparableVec vec;
	private Edge edge;
	private Map< Region, Integer > regions = new ConcurrentHashMap< Region, Integer >();
	private Set< AABB > corners = new HashSet< AABB >();
	
	protected RegionPanel() {
		INSTANCE = this;
		
		frame = new JFrame( "2D Pathfinder" );

		setCenterX( windowWidth / 2 );
		setCenterY( windowHeight / 2 );
		setScale( 32 );

		frame.add( this );

		frame.addMouseListener( this );
		
		frame.setSize( windowWidth, windowHeight );
		frame.setVisible( true );
		frame.setResizable( true );
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		if ( start == null ) {
			return;
		}
		
		g.setColor( Color.BLACK );
		for ( Region r : regions.keySet() ) {
			draw( r.getRegion() );
		}
		
		g.setColor( Color.BLUE );
		for ( int i = 0; i < region.getRegions().size(); i++ ) {
			Region r = region.getRegions().get( i );
			draw( r.getRegion() );
			drawString( i + "", start.getX() - r.getRegion().maxX, start.getZ() - r.getRegion().maxZ );
			regions.put( r, 1 );
		}

		if ( edge != null ) {
			g.setColor( Color.CYAN );
			draw( edge.getIntersection() );
		}
		
		g.setColor( Color.RED );
		for ( AABB corner : corners ) {
			draw( corner );
		}
		
		g.setColor( Color.GREEN );
		Vector max = vec.getMax().multiply( -10 );
		Vector min = vec.getMin().multiply( -10 );
		drawLine( 0, 0, max.getX(), max.getZ() );
		drawLine( 0, 0, min.getX(), min.getZ() );
		
	}
	
	private void draw( AABB box ) {
		this.drawLine( start.getX() - box.minX, start.getZ() - box.minZ, start.getX() - box.maxX, start.getZ() - box.minZ );
		this.drawLine( start.getX() - box.maxX, start.getZ() - box.minZ, start.getX() - box.maxX, start.getZ() - box.maxZ );
		this.drawLine( start.getX() - box.maxX, start.getZ() - box.maxZ, start.getX() - box.minX, start.getZ() - box.maxZ );
		this.drawLine( start.getX() - box.minX, start.getZ() - box.maxZ, start.getX() - box.minX, start.getZ() - box.minZ );
	}
	
	private void set( Vector start, PathRegion region, ComparableVec vec, Edge edge ) {
		this.start = start;
		this.region = region;
		this.vec = vec;
		this.edge = edge;
		repaint();
	}
	
	public static void addCorner( AABB corner ) {
		INSTANCE.corners.add( corner );
		INSTANCE.repaint();
	}
	
	public static void pause() {
		paused = true;
		while ( paused ) {
			try {
				Thread.sleep( 100 );
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseClicked( MouseEvent arg0 ) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed( MouseEvent arg0 ) {
		paused = false;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
