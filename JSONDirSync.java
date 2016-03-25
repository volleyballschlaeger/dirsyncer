import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONDirSync extends FileSync {
	public static boolean RecvDirContent( File dir, JSONArray entries ) throws JSONException {
		if( !dir.isDirectory() ) {
			System.out.println( dir + " is not a directory." );
			return false;
		}
		boolean complete = true;
		for( int i = 0; i < entries.length(); i++ ) {
			JSONObject entry = entries.getJSONObject( i );
			String name = entry.getString( "name" );
			String type = entry.getString( "type" );
			if( name.indexOf( File.separatorChar ) >= 0 || name.equals( ".." ) || name.equals( "." ) ) {
				System.out.println( name + " invalid name" );
				break;
			}
			if( type.equals( "file" ) ) {
				String md5 = entry.optString( "md5", null );
				String url = entry.getString( "url" );
				try {
					RecvFile( dir, name, url, md5 );
				} catch( IOException e ) {
					complete = false;
					System.out.println( new File( dir, name ) + " failed: " + e );
				}
			}
			else if( type.equals( "dir" ) ) {
				JSONArray newentries = entry.getJSONArray( "entries" );
				File newdir = new File( dir, name );
				System.out.println( "mkdir " + newdir );
				newdir.mkdir();
				complete &= RecvDirContent( newdir, newentries );
			}
			else
				System.out.println( type + " invalid type" );
		}
		return complete;
	}
}
