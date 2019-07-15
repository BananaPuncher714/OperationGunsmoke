package io.github.bananapuncher714.operation.gunsmoke.core.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Thanks to Remceau for these super useful utilities
 */
public class SessionUtil {
	private static JsonParser PARSER = new JsonParser();
	// Cache the skins, don't want to get ratelimited by Mojang
	private static Map< String, String[] > SKINS = new HashMap< String, String[] >();

	public static String[] getTextureFrom( String name, boolean force ) {
		if ( !force && SKINS.containsKey( name ) ) {
			return SKINS.get( name );
		} else {
			String[] values = getTextureFrom( name );
			SKINS.put( name, values );
			return values;
		}
	}

	private static String[] getTextureFrom( String playerName ) {
		try {
			URL url_0 = new URL( "https://api.mojang.com/users/profiles/minecraft/" + playerName );
			InputStreamReader reader_0 = new InputStreamReader( url_0.openStream() );
			String uuid = PARSER.parse( reader_0 ).getAsJsonObject().get( "id" ).getAsString();

			URL url_1 = new URL( "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false" );
			InputStreamReader reader_1 = new InputStreamReader( url_1.openStream() );
			JsonObject textureProperty = PARSER.parse( reader_1 ).getAsJsonObject().get( "properties" ).getAsJsonArray().get( 0 ).getAsJsonObject();
			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();

			return new String[] { texture, signature };
		} catch ( IOException e ) {
			System.err.println("Could not get skin data from session servers!");
			e.printStackTrace();
			return null;
		}
	}
	
	protected static Map< String, String[] > getSkins() {
		return SKINS;
	}
}
