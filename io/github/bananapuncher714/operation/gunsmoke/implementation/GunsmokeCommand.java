package io.github.bananapuncher714.operation.gunsmoke.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Path;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.PathfinderGrid;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.RegionGenerator;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.test.PathingPanel;
import io.github.bananapuncher714.operation.gunsmoke.core.util.BukkitUtil;
import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmor;
import io.github.bananapuncher714.operation.gunsmoke.implementation.armor.ConfigArmorOptions;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigGun;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;

public class GunsmokeCommand implements CommandExecutor, TabCompleter {
	protected Location end;
	protected Path result = null;
	
	protected World world;
	protected List< AABB > boxes = null;
	
	public GunsmokeCommand() {
		Bukkit.getScheduler().runTaskTimer( GunsmokeUtil.getPlugin(), this::update, 0, 1 );
	}
	
	private void update() {
		if ( result != null ) {
			List< Location > points = result.getWaypoints();
			for ( int i = 1; i < points.size(); i++ ) {
				drawLine( points.get( i - 1 ), points.get( i ) );
			}
		}
		if ( end != null ) {
			end.getWorld().spawnParticle( Particle.FLAME, end.clone().add( .5, .5, .5 ), 0 );
		}
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			Location location = player.getLocation();
			Location blockLocation = BukkitUtil.getBlockLocation( location );
			Location shifted = location.clone().subtract( blockLocation );
			
			List< AABB > boxes = new ArrayList< AABB >();
			for ( int x = -10; x < 10; x++ ) {
				for ( int y = -10; y < 10; y++ ) {
					for ( AABB box : GunsmokeUtil.getPlugin().getProtocol().getHandler().getBoxesFor( location.clone().add( x, 0, y ) ) ) {
						boxes.add( box.shift( x - shifted.getX(), 0, y - shifted.getZ() ) );
					}
				}
			}
			AABB[] boxArr = boxes.toArray( new AABB[ boxes.size() ] );
			AABB playerBox = new AABB( player.getBoundingBox().expand( -9.999999747378752E-6D ) ).shift( -location.getX(), 0, -location.getZ() );
			PathingPanel.draw( playerBox, boxArr );
		}
		if ( boxes != null ) {
			for ( AABB box : boxes ) {
				world.spawnParticle( Particle.WATER_BUBBLE, box.maxX, box.maxY, box.maxZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.maxX, box.maxY, box.minZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.maxX, box.minY, box.minZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.minX, box.minY, box.minZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.minX, box.minY, box.maxZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.minX, box.maxY, box.maxZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.maxX, box.minY, box.maxZ, 0 );
				world.spawnParticle( Particle.WATER_BUBBLE, box.minX, box.maxY, box.minZ, 0 );
			}
		}
	}
	
	private void drawLine( Location start, Location stop ) {
		Vector to = stop.clone().subtract( start ).toVector().normalize();
		for ( int i = 0; i < 5; i++ ) {
			start.getWorld().spawnParticle( Particle.REDSTONE, start.clone().add( .5, .5, .5 ).add( to.clone().multiply( i / 5.0 ) ), 0, new DustOptions( Color.RED, 1 ) );
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
				} else if ( args[ 0 ].equalsIgnoreCase( "set" ) ) {
					end = BukkitUtil.getBlockLocation( player.getLocation() );
				} else if ( args[ 0 ].equalsIgnoreCase( "path" ) ) {
					if ( end != null ) {
						Location start = BukkitUtil.getBlockLocation( player.getLocation() );
						start.setY( end.getBlockY() );
						PathfinderGrid pathfinder = new PathfinderGrid( start, end );
						result = pathfinder.calculate( 5000 );
						if ( result != null ) {
							player.sendMessage( "Found!" );
						} else {
							player.sendMessage( "Not found!" );
						}
					} else {
						player.sendMessage( "Destination not set!" );
					}
				} else if ( args[ 0 ].equalsIgnoreCase( "scan" ) ) {
					Location location = player.getLocation();
					boxes = RegionGenerator.generateRegions( location );
					player.sendMessage( "Boxes detected: " + boxes.size() );
					world = player.getWorld();
					for ( AABB box : boxes ) {
						System.out.println( box );
					}
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
