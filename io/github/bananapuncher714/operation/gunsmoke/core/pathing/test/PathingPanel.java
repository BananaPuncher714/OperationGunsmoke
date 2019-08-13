package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Edge;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Path;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;

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
	
	private Collection< AABB > corners = new ArrayList< AABB >();
	
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

		boolean calculateCorners = false;
		boolean calculateRegions = true;

		g.setColor( Color.BLACK );
		for ( AABB box : boxes ) {
			draw( box );
		}
		
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
			g.setColor( Color.GREEN );
			List< Vector > points = getOriginalSolution( false );
			double sum1 = 0;
			List< Vector > reverse = getOriginalSolution( true );
			double sum2 = 0;
			
			g.setColor( Color.GREEN );
			for ( int idx = 1; idx < points.size(); idx++ ) {
				Vector prev = points.get( idx - 1 );
				Vector curr = points.get( idx );
				sum1 += prev.distance( curr );
				this.drawLine( prev.getX(), prev.getZ(), curr.getX(), curr.getZ() );
			}

			g.setColor( Color.BLUE );
			for ( int idx = 1; idx < reverse.size(); idx++ ) {
				Vector prev = reverse.get( idx - 1 );
				Vector curr = reverse.get( idx );
				sum2 += prev.distance( curr );
				this.drawLine( prev.getX(), prev.getZ() - .1, curr.getX(), curr.getZ() - .1 );
			}
			
			System.out.println( sum1 );
			System.out.println( sum2 );
		}
		
		g.setColor( Color.GREEN );
		for ( AABB corner : corners ) {
			draw( corner );
		}
		
		g.setColor( Color.BLACK );
		for ( AABB box : boxes ) {
			draw( box );
		}
	}
	
	private List< Vector > getOriginalSolution( boolean reverse ) {
		Vector lastSolid = new Vector( player.maxX, player.maxY, player.maxZ );
		Vector end = new Vector( player.maxX + 21, player.maxY, player.maxZ );
		List< Edge > edges = new ArrayList< Edge >();
		for ( AABB box : boxes ) {
			edges.add( new Edge( box ) );
		}
		
		// Reverse the inputs?
		if ( reverse ) {
			Vector temp = lastSolid;
			lastSolid = end;
			end = temp;
			for ( int i = 0; i < edges.size() / 2; i++ ) {
				int reverseIndex = edges.size() - ( i + 1 );
				Edge tempEdge = edges.get( i );
				edges.set( i, edges.get( reverseIndex ) );
				edges.set( reverseIndex, tempEdge );
			}
		}
		
		Vector lastClosest = end.clone();
		Vector solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
		
		List< Vector > points = new ArrayList< Vector >();
		points.add( lastSolid );
		
		// First check if we can directly go from start to stop
		boolean direct = true;
		for ( Edge edge : edges ) {
			if ( !edge.intersects( lastSolid, solidToLastClosest ) ) {
				direct = false;
				break;
			}
		}
		
		int closestEdge = -1;
		while ( !direct ) {
			lastClosest = end;
			for ( int i = edges.size() - 1; i > closestEdge; i-- ) {
				// First construct a vector from the last solid to the last closest
				solidToLastClosest = lastClosest.clone().subtract( lastSolid ).normalize();
				Edge edge = edges.get( i );
				// Get the edge and the closest point
				Vector closest = edge.getClosestPoint( lastSolid, solidToLastClosest );
				// Construct a new one that goes directly to the solid
				Vector solidToClosest = closest.clone().subtract( lastSolid ).normalize();
				boolean valid = true;
				for ( int j = i - 1; j > closestEdge; j-- ) {
					Edge nextEdge = edges.get( j );
					if ( !nextEdge.intersects( lastSolid, solidToClosest ) ) {
						valid = false;
						break;
					}
				}
				if ( valid ) {
					closestEdge = i;
					lastSolid = closest;
					points.add( closest );
					if ( i == edges.size() - 1 ) {
						direct = true;
					}
					break;
				}
				lastClosest = closest;
			}
		}
		points.add( end );
		return points;
	}
	
	private List< Vector > getNewSolution() {
		Vector lastSolid = new Vector( player.maxX, player.maxY, player.maxZ );
		List< Vector > points = new ArrayList< Vector >();
		points.add( lastSolid );
		List< Edge > edges = new ArrayList< Edge >();
		for ( AABB box : boxes ) {
			edges.add( new Edge( box ) );
		}
		
		Edge lastEdge = null;
		int lastEdgeIndex = 0;
		Vector lastClosest = lastSolid;
		// The variables that carry over are our last edge, last solid, and last closest
		for ( int i = 0; i < edges.size(); i++ ) {
			// First get the edge we need to go over
			Edge edge = edges.get( i );
			// Next, get the closest point on that edge to our last solid position
			Vector closest = VectorUtil.closestPoint( edge.getIntersection(), lastClosest );
			// Then create a vector from our position to that edge
			Vector lastSolidToClosest = closest.clone().subtract( lastSolid );

			// Can our last solid position see the edge?
			// We start at lastEdgeIndex, which holds the first edge between the solid position and the current edge
			boolean directSight = true;
			for ( int j = lastEdgeIndex; j < i; j++ ) {
				Edge checkEdge = edges.get( j );
				if ( !checkEdge.intersects( lastSolid, lastSolidToClosest ) ) {
					directSight = false;
					break;
				}
			}
			// If we have a direct line of sight, then we can set the latest point and edge to the one we can see
			if ( directSight ) {
				lastClosest = closest;
				lastEdge = edge;
				continue;
			}
			
			if ( lastEdge != null ) {
				// Get the closest point between the last edge and our last solid
				Vector guaranteedPoint = lastEdge.getClosestPoint( lastSolid, lastSolidToClosest );
				// What if the guaranteed point is different than our last closest point?
				points.add( guaranteedPoint );
				lastSolid = guaranteedPoint;
			}
			lastClosest = closest;
			lastEdge = edge;
			lastEdgeIndex = i;
		}
		return points;
	}
	
	private void draw( AABB box ) {
		this.drawLine( box.minX, box.minZ, box.maxX, box.minZ );
		this.drawLine( box.maxX, box.minZ, box.maxX, box.maxZ );
		this.drawLine( box.maxX, box.maxZ, box.minX, box.maxZ );
		this.drawLine( box.minX, box.maxZ, box.minX, box.minZ );
	}
	
	private double swept3d( AABB box, Vector movement, AABB check ) {
		double distEntX;
		double distEntY;
		double distEntZ;
		double distExtX;
		double distExtY;
		double distExtZ;
		
		if ( movement.getX() > 0 ) {
			distEntX = check.minX - box.maxX;
			distExtX = check.maxX - box.minX;
		} else {
			distEntX = check.maxX - box.minX;
			distExtX = check.minX - box.maxX;
		}
		
		if ( movement.getY() > 0 ) {
			distEntY = check.minY - box.maxY;
			distExtY = check.maxY - box.minY;
		} else {
			distEntY = check.maxY - box.minY;
			distExtY = check.minY - box.maxY;
		}
		
		if ( movement.getZ() > 0 ) {
			distEntZ = check.minZ - box.maxZ;
			distExtZ = check.maxZ - box.minZ;
		} else {
			distEntZ = check.maxZ - box.minZ;
			distExtZ = check.minZ - box.maxZ;
		}
		
		double entryX;
		double entryY;
		double entryZ;
		double exitX;
		double exitY;
		double exitZ;
		
		if ( movement.getX() == 0 ) {
			entryX = Double.NEGATIVE_INFINITY;
			exitX = Double.POSITIVE_INFINITY;
		} else {
			entryX = distEntX / movement.getX();
			exitX = distExtX / movement.getX();
		}
		
		if ( movement.getY() == 0 ) {
			entryY = Double.NEGATIVE_INFINITY;
			exitY = Double.POSITIVE_INFINITY;
		} else {
			entryY = distEntY / movement.getY();
			exitY = distExtY / movement.getY();
		}
		
		if ( movement.getZ() == 0 ) {
			entryZ = Double.NEGATIVE_INFINITY;
			exitZ = Double.POSITIVE_INFINITY;
		} else {
			entryZ = distEntZ / movement.getZ();
			exitZ = distExtZ / movement.getZ();
		}
		
		double entryTime = Math.max( entryX, Math.max( entryY, entryZ ) );
		double exitTime = Math.min( exitX, Math.min( exitY, exitZ ) );
		
		 if ( entryTime >= exitTime || ( entryX < 0 && entryY < 0 && entryZ < 0 ) || entryX >= 1 || entryY >= 1 || entryZ >= 1 ) {
			 return 1;
		 } else {
			 return 0;
		 }
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
	
	public void setCorners( Collection< AABB > corners ) {
		this.corners = corners;
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
