import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
			else if( type.equals( "mount" ) ) {
				String url = entry.getString( "url" );
				File newdir = new File( dir, name );
				System.out.println( "mkdir " + newdir );
				newdir.mkdir();
				complete &= RecvDirContent( newdir, url );
			}
			else
				System.out.println( type + " invalid type" );
		}
		return complete;
	}

	public static String RecvString( String urlString ) throws IOException {
		URL url = new URL( urlString );
		InputStream inputstream = null;
		BufferedReader reader = null;
		try {
			URLConnection connection = url.openConnection();
			connection.setReadTimeout( 20000 );
			connection.setConnectTimeout( 20000 );
			inputstream = connection.getInputStream();
			reader = new BufferedReader( new InputStreamReader( inputstream, "UTF-8" ) );
			StringBuilder sb = new StringBuilder();
			String line;
			while( ( line = reader.readLine() ) != null ) {
				sb.append( line );
				sb.append( '\n' );
			}
			return sb.toString();
		}
		finally {
			if( reader != null )
				reader.close();
			if( inputstream != null )
				inputstream.close();
		}
	}

	public static boolean RecvDirContent( File dir, String urlString ) {
		try {
			JSONArray newentries = new JSONArray( RecvString( urlString ) );
			return RecvDirContent( dir, newentries );
		} catch( Exception e ) {
			System.out.println( dir + " failed: " + e );
			return false;
		}
	}
}
