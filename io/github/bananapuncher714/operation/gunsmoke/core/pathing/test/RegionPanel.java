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
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.ComparableVec;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Path;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;

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
	private Map< Vector, Integer > corners = new ConcurrentHashMap< Vector, Integer >();
	private Set< PathRegion > paths = new HashSet< PathRegion >();
	
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
		
		for ( Vector corner : corners.keySet() ) {
			g.setColor( Color.DARK_GRAY );
			drawPoint( start.getX() - corner.getX(), start.getZ() - corner.getZ() );
			drawString( "*", start.getX() - corner.getX(), start.getZ() - corner.getZ() );
		}
		
		if ( vec != null ) {
			g.setColor( Color.GREEN );
			Vector max = vec.getMax().multiply( -10 );
			Vector min = vec.getMin().multiply( -10 );
			drawLine( 0, 0, max.getX(), max.getZ() );
			drawLine( 0, 0, min.getX(), min.getZ() );
		}
		
		Path path = region.getPath();
		if ( path != null ) { 
			g.setColor( Color.GREEN );
			for ( int i = 0; i < path.getWaypoints().size() - 1; i++ ) {
				Vector _1 = path.getWaypoints().get( i );
				Vector _2 = path.getWaypoints().get( i + 1 );
				drawString( i + "", start.getX() - _1.getX(), start.getZ() - _1.getZ() );
				drawLine( start.getX() - _1.getX(), start.getZ() - _1.getZ(), start.getX() - _2.getX(), start.getZ() - _2.getZ() );
			}
		}

		for ( PathRegion p : paths ) {
			if ( p.getPath() != null ) {
				g.setColor( Color.MAGENTA );
				for ( int i = 0; i < p.getPath().getWaypoints().size() - 1; i++ ) {
					Vector _1 = p.getPath().getWaypoints().get( i );
					Vector _2 = p.getPath().getWaypoints().get( i + 1 );
					drawLine( start.getX() - _1.getX(), start.getZ() - _1.getZ(), start.getX() - _2.getX(), start.getZ() - _2.getZ() );
				}
			}
		}
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
	
	public static void dump() {
		for ( int i : INSTANCE.corners.values() ) {
			System.out.println( i );
		}
	}
	
	public static void addCorner( Vector corner ) {
		INSTANCE.corners.put( corner, INSTANCE.corners.getOrDefault( corner, 0 ) + 1 );
		INSTANCE.repaint();
	}
	
	public static void addPath( PathRegion path ) {
		INSTANCE.paths.add( path );
		INSTANCE.repaint();
	}
	
	public static void clearPaths() {
		INSTANCE.paths.clear();
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
