package io.github.bananapuncher714.operation.gunsmoke.core.pathing.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import io.github.bananapuncher714.operation.gunsmoke.api.util.AABB;
import io.github.bananapuncher714.operation.gunsmoke.core.pathing.Region;

public class RegionLoader {
	public static void toFile( Region region, File baseDir ) {
		try {
			File save = new File( baseDir + "/" + region.hashCode() );
			if ( save.exists() ) {
				save.delete();
			}
			save.createNewFile();
			BufferedWriter writer = new BufferedWriter( new FileWriter( save ) );
			writer.write( region.getRegion().maxX + "\n" );
			writer.write( region.getRegion().maxY + "\n" );
			writer.write( region.getRegion().maxZ + "\n" );
			writer.write( region.getRegion().minX + "\n" );
			writer.write( region.getRegion().minY + "\n" );
			writer.write( region.getRegion().minZ + "" );
			writer.close();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	public static AABB load( File file ) {
		try {
			List< String > contents = Files.readAllLines( file.toPath() );
			if ( contents.size() != 6 ) {
				System.out.println( "Improper!" );
				return null;
			}
			double[] arr = new double[ contents.size() ];
			for ( int i = 0; i < contents.size(); i++ ) {
				arr[ i ] = Double.valueOf( contents.get( i ) );
			}
			AABB box = new AABB( arr[ 0 ], arr[ 1 ], arr[ 2 ], arr[ 3 ], arr[ 4 ], arr[ 5 ] );
			return box;
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
}
