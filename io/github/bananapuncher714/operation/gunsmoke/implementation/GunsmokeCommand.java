package io.github.bananapuncher714.operation.gunsmoke.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.GunsmokeNPC;
import io.github.bananapuncher714.operation.gunsmoke.api.entity.npc.NPCAction;
import io.github.bananapuncher714.operation.gunsmoke.api.item.GunsmokeItem;
import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.ElevationLayer;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.EnclosingRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Path;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Pathfinder;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathfinderElevation;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathfinderElevationFast;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathfinderRegion;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionMap;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.test.PathingPanel;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.MDChat;
import io.github.bananapuncher714.operation.gunsmoke.core.util.VectorUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmor;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmorOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigGun;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;
import net.md_5.bungee.api.ChatMessageType;

public class GunsmokeCommand implements CommandExecutor, TabCompleter {
	protected Location end;
	protected Path result = null;
	protected PathRegion regionResult = null;
	
	protected World world;
	protected List< AABB > spaces = null;
	protected List< AABB > solids = null;
	
	protected RegionMap regionMap;
	
	public GunsmokeCommand() {
		Bukkit.getScheduler().runTaskTimer( GunsmokeUtil.getPlugin(), this::update, 0, 5 );
	}
	
	private void update() {
//		if ( regionMap != null && end != null ) {
//			for ( Player player : Bukkit.getOnlinePlayers() ) {
//				Pathfinder pathfinder = new PathfinderRegion( regionMap, player.getLocation(), end );
//				result = pathfinder.calculate( 200 );
//			}
//		}
		
		if ( result != null ) {
			List< Location > points = result.getWaypoints();
			for ( int i = 1; i < points.size(); i++ ) {
				drawLine( points.get( i - 1 ), points.get( i ) );
			}
			for ( Location loc : points ) {
				loc.getWorld().spawnParticle( Particle.FLAME, loc, 0 );
			}
		}
		if ( regionMap != null ) {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			Location location = player.getLocation();
			Region region = regionMap.getRegion( location );
			if ( region != null ) {
				for ( AABB box : region.getCorners() ) {
					Location one = new Location( location.getWorld(), box.maxX, box.maxY, box.maxZ );
					Location two = new Location( location.getWorld(), box.minX, box.minY, box.minZ );
					
					two.getWorld().spawnParticle( Particle.FLAME, two.clone(), 0 );
				}
				player.spigot().sendMessage( ChatMessageType.ACTION_BAR, MDChat.getMessageFromString( "Solids " + region.getSolids().size() + " | Corners " + region.getCorners().size() , true ) );
			} else {
				player.spigot().sendMessage( ChatMessageType.ACTION_BAR, MDChat.getMessageFromString( "Not in region", true ) );
			}
		}
		}
		if ( end != null ) {
			end.getWorld().spawnParticle( Particle.FLAME, end.clone(), 0 );
		}
	}
	
	private void drawLine( Location start, Location stop ) {
		Vector to = stop.clone().subtract( start ).toVector();
		for ( int i = 0; i < 10; i++ ) {
			start.getWorld().spawnParticle( Particle.REDSTONE, start.clone().add( to.clone().multiply( i / 10.0 ) ), 0, new DustOptions( Color.RED, 1 ) );
//			start.getWorld().spawnParticle( Particle.WATER_BUBBLE, start.clone().add( to.clone().multiply( i / 10.0 ) ), 0 );
		}
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			
			if ( args.length == 1 ) {
				if ( args[ 0 ].equalsIgnoreCase( "sneak" ) ) {
					
					for ( GunsmokeNPC npc : GunsmokeUtil.getPlugin().getNPCManager().getNPCs() ) {
						npc.interact( NPCAction.START_SNEAKING );
						npc.interact( NPCAction.STOP_SNEAKING );
						npc.interact( NPCAction.START_SNEAKING );
					}
				} else if ( args[ 0 ].equalsIgnoreCase( "gen" ) ) {
					AABB[] lines = new AABB[ 20 ];
					Random rand = ThreadLocalRandom.current();
					for ( int i = 0; i < lines.length; i++ ) {
						double min = rand.nextInt() % 7;
						double max = Math.abs( rand.nextInt() % 10 ) + min - 4;
						lines[ i ] = new AABB( i - lines.length + 10, 0, max, i - lines.length + 10, 0, min );
					}
					PathingPanel.draw( new AABB( -11, 0, 0, -11, 0, 0 ), lines );
				} else if ( args[ 0 ].equalsIgnoreCase( "set" ) ) {
					end = player.getLocation();
				} else if ( args[ 0 ].equalsIgnoreCase( "path" ) ) {
					if ( end != null ) {
						if ( regionMap != null ) {
							Location start = player.getLocation();
							Pathfinder pathfinder = new PathfinderRegion( regionMap, start, end );
							Bukkit.getScheduler().runTaskAsynchronously( GunsmokeUtil.getPlugin(), () -> {
								PathRegion path = pathfinder.calculate( 5000 );
								Bukkit.getScheduler().runTask( GunsmokeUtil.getPlugin(), () -> {
									regionResult = path;
									result = path.getPath();
									if ( result != null ) {
										player.sendMessage( "Number of points: " + result.getWaypoints().size() );
										for ( Location location : result.getWaypoints() ) {
											System.out.println( location );
										}
									}
								} );
							} );
							player.sendMessage( "Calculating..." );
						} else {
							player.sendMessage( "Region map is null!" );
						}
					} else {
						player.sendMessage( "Destination not set!" );
					}
				} else if ( args[ 0 ].equalsIgnoreCase( "layerpath" ) ) {
					if ( end != null ) {
						if ( regionMap != null ) {
							Location start = player.getLocation();
							Pathfinder pathfinder = new PathfinderRegion( regionMap, start, end );
							Bukkit.getScheduler().runTaskAsynchronously( GunsmokeUtil.getPlugin(), () -> {
								PathRegion path = pathfinder.calculate( 5000 );
								Bukkit.getScheduler().runTask( GunsmokeUtil.getPlugin(), () -> {
									regionResult = path;
									result = path.getPath();
									if ( result != null ) {
										player.sendMessage( "Number of points: " + result.getWaypoints().size() );
										for ( Location location : result.getWaypoints() ) {
											System.out.println( location );
										}
									}
								} );
							} );
							player.sendMessage( "Calculating..." );
						} else {
							player.sendMessage( "Region map is null!" );
						}
					} else {
						player.sendMessage( "Destination not set!" );
					}
				} else if ( args[ 0 ].equalsIgnoreCase( "corners" ) ) {
					if ( regionMap != null ) {
						Location location = player.getLocation();
						Region region = regionMap.getRegion( location );
						if ( region != null ) {
							RegionGenerator.getCornersFor( region );
						}
					}
				} else if ( args[ 0 ].equalsIgnoreCase( "scan" ) ) {
					Location location = player.getLocation();
					world = player.getWorld();
					spaces = new ArrayList< AABB >();
					solids = new ArrayList< AABB >();
					for ( int x = -16; x < 16; x++ ) {
						for ( int z = -16; z < 16; z++ ) {
							EnclosingRegion region = RegionGenerator.generateRegions( location.clone().add( x, 0, z ) );
							spaces.addAll( region.getSpace() );
							solids.addAll( region.getSolid() );
						}
					}
					System.out.println( "Combining spaces... " );
					spaces = RegionGenerator.combineRegions( spaces );
					solids = RegionGenerator.combineRegions( solids );
					player.sendMessage( "Boxes detected: " + spaces.size() );
					
					AABB[] boxArr = new AABB[ spaces.size() ];
					for ( int i = 0; i < spaces.size(); i++  ) {
						boxArr[ i ] = spaces.get( i ).shift( -location.getX(), 0, -location.getZ() );
					}
					AABB realPlayerBox = new AABB( player.getBoundingBox() );
					AABB playerBox = new AABB( player.getBoundingBox().expand( -9.999999747378752E-6D ) ).shift( -location.getX(), 0, -location.getZ() );
//					PathingPanel.draw( playerBox, boxArr );

					// Now that we have our boxes, let's create regions linking each other
					System.out.println( "Getting neighbors..." ); 
					regionMap = new RegionMap();
					for ( AABB box : spaces ) {
						Region region = regionMap.getRegionFor( box );
						for ( AABB other : spaces ) {
							if ( other == box ) {
								continue;
							}
							if ( VectorUtil.touching( box, other ) ) {
								region.addNeighbor( regionMap.getRegionFor( other ) );
							}
						}
						
						for ( AABB solid : solids ) {
							if ( VectorUtil.touching( box, solid ) ) {
								region.getSolids().add( solid );
							}
						}
					}
					RegionGenerator.elevate( regionMap );
					RegionGenerator.generateCorners( regionMap );
//					RegionGenerator.trimCorners( regionMap );
					System.out.println( "Layers detected: " + regionMap.getLayers().size() );
					System.out.println( "Generating spaces..." );
					// Now that we have our spaces, we want to get the corners or something
					// There are eight possible corners that we need to generate
					// So for each neighbor, get the AABB box of intersection
					// Then, generate 8 points off of that
					Set< AABB > corners = new HashSet< AABB >();
					double pw = player.getWidth();
					double ph = player.getHeight();
					
					Set< Vector > points = new HashSet< Vector >();
					for ( Region region : regionMap.getRegions() ) {
						AABB regionBox = region.getRegion();
						for ( Region neighbor : region.getNeighbors().keySet() ) {
							AABB neighborBox = neighbor.getRegion();
							AABB i = new AABB( Math.min( regionBox.maxX, neighborBox.maxX ),
									Math.min( regionBox.maxY, neighborBox.maxY ),
									Math.min( regionBox.maxZ, neighborBox.maxZ ),
									Math.max( regionBox.minX, neighborBox.minX ),
									Math.max( regionBox.minY, neighborBox.minY ),
									Math.max( regionBox.minZ, neighborBox.minZ ) );
							
							// Given the border, get the 8 points and add them to the set of corners to generate
							points.add( new Vector( i.maxX, i.maxY, i.maxZ ) );
							points.add( new Vector( i.maxX, i.maxY, i.minZ ) );
							points.add( new Vector( i.maxX, i.minY, i.maxZ ) );
							points.add( new Vector( i.maxX, i.minY, i.minZ ) );
							points.add( new Vector( i.minX, i.maxY, i.maxZ ) );
							points.add( new Vector( i.minX, i.maxY, i.minZ ) );
							points.add( new Vector( i.minX, i.minY, i.maxZ ) );
							points.add( new Vector( i.minX, i.minY, i.minZ ) );
						}
					}
					for ( Vector i : points ) {
						corners.add( new AABB( i.getX() + pw, i.getY() + ph, i.getZ() + pw, i.getX(), i.getY(), i.getZ() ) );
						corners.add( new AABB( i.getX() + pw, i.getY() + ph, i.getZ(), i.getX(), i.getY(), i.getZ() - pw ) );
						corners.add( new AABB( i.getX() + pw, i.getY(), i.getZ() + pw, i.getX(), i.getY() - ph, i.getZ() ) );
						corners.add( new AABB( i.getX() + pw, i.getY(), i.getZ(), i.getX(), i.getY() - ph, i.getZ() - pw ) );
						corners.add( new AABB( i.getX(), i.getY() + ph, i.getZ() + pw, i.getX() - pw, i.getY(), i.getZ() ) );
						corners.add( new AABB( i.getX(), i.getY() + ph, i.getZ(), i.getX() - pw, i.getY(), i.getZ() - pw ) );
						corners.add( new AABB( i.getX(), i.getY(), i.getZ() + pw, i.getX() - pw, i.getY() - ph, i.getZ() ) );
						corners.add( new AABB( i.getX(), i.getY(), i.getZ(), i.getX() - pw, i.getY() - ph, i.getZ() - pw ) );
					}
					
					System.out.println( "Trimming colliding nodes..." );
					for ( Iterator< AABB > iterator = corners.iterator(); iterator.hasNext(); ) {
						AABB corner = iterator.next();
						for ( AABB solid : solids ) {
							if ( VectorUtil.intersects( corner, solid ) ) {
								iterator.remove();
								break;
							}
						}
					}
					Collection< AABB > shiftedCorners = new ArrayList< AABB >();
					corners.forEach( ( corner ) -> { shiftedCorners.add( corner.shift( -location.getX(), 0, -location.getZ()  ) ); } );
					System.out.println( "Corners found: " + corners.size() );
//					PathingPanel.INSTANCE.setCorners( shiftedCorners );
					
					// So right now our corners contains each valid position our player can move
					// But before that...
					// We have the RegionMap, which contains every region registered along with their edges and neighbors
					
				}
			} else if ( args.length == 2 ) {
				if ( args[ 0 ].equalsIgnoreCase( "get" ) ) {
					String id = args[ 1 ];
					
					GunsmokeItem item = null;
					
					ConfigWeaponOptions options = GunsmokeImplementation.getInstance().getGun( id );
					if ( options != null ) {
						item = new ConfigGun( options );
					} else {
						ConfigArmorOptions armor = GunsmokeImplementation.getInstance().getArmor( id );
						if ( armor != null ) {
							item = new ConfigArmor( armor );
						}
					}
					
					if ( item != null ) {
						GunsmokeUtil.getPlugin().getItemManager().register( item );
						player.getInventory().addItem( item.getItem() );
						player.sendMessage( ChatColor.GREEN + "Gave you a " + id + "!" );
					} else {
						player.sendMessage( ChatColor.RED + id + " is not a valid item!" );
					}
				} else if ( args[ 0 ].equalsIgnoreCase( "chat" ) ) {
					player.spigot().sendMessage( MDChat.getMessageFromString( args[ 1 ].replace( '&', '\u00A7' ), true ) );
				}
			}
		}
		return false;
	}
	
	@Override
	public List< String > onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
		List< String > completions = new ArrayList< String >();
		if ( !( sender instanceof Player ) ) {
			return completions;
		}
		List< String > suggestions = new ArrayList< String >();
		if ( args.length == 1 ) {
			suggestions.add( "get" );
		} else if ( args.length == 2 ) {
			if ( args[ 0 ].equalsIgnoreCase( "get" ) ) {
				suggestions.addAll( GunsmokeImplementation.getInstance().guns.keySet() );
				suggestions.addAll( GunsmokeImplementation.getInstance().armor.keySet() );
			}
		}
		StringUtil.copyPartialMatches( args[ args.length - 1 ], suggestions, completions);
		Collections.sort( completions );
		return completions;
	}
}
