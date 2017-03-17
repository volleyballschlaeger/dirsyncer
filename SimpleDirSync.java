import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDirSync extends FileSync {
	private static final Pattern MD5SUMPATTERN = Pattern.compile( "([0-9a-f]{32}) [ \\*](.+)" );
	private static final Pattern BASEURLPATTERN = Pattern.compile( "(.+://.+)/.+" );

	private final File baseDir;

	public SimpleDirSync( File bd ) {
		baseDir = bd;
	}

	public static void RecvPath( File baseDir, String relPath, String urlString, String md5 ) throws IOException {
		String s[] = relPath.split( "/", 2 );
		String name = s[0];

		if( name.length() == 0 )
			throw new IOException( "Name is empty." );
		if( s.length >= 2 ) {
			File f = new File( baseDir, name );
			System.out.println( "Creating directory " + f.getPath() + "." );
			if( !f.mkdir() )
				if( !f.isDirectory() )
					throw new IOException( "Cannot create directory " + name + "." );
			RecvPath( f, s[1], urlString, md5 );
		}
		else {
			RecvFile( baseDir, name, urlString, md5 );
		}
	}

	private static String encodepath( String relPath ) {
		String[] parts = relPath.split( "/" );
		StringBuilder result = new StringBuilder();
		try {
			for( String i : parts ) {
				result.append( '/' );
				result.append( URLEncoder.encode( i, "UTF-8" ).replace( "+", "%20" ) );
			}
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
		return result.toString();
	}

	public static abstract class Entry {
		public final String md5;
		public final String path;

		public Entry( String desc ) throws Exception {
			Matcher m = MD5SUMPATTERN.matcher( desc );
			if( m.matches() ) {
				md5 = m.group( 1 );
				path = m.group( 2 );
			}
			else
				throw new Exception( "Syntax error" );
		}

		public abstract void doit() throws IOException;

		@Override
		public String toString() {
			return md5 + "  " + path;
		}
	}

	public List<Entry> RecvEntries( String u ) throws Exception {
		Matcher m = BASEURLPATTERN.matcher( u );
		if( !m.matches() )
			throw new Exception( "Syntax error" );
		final String baseUrl = m.group( 1 );
		URL url = new URL( u );
		InputStream inputstream = null;
		BufferedReader reader = null;
		try {
			URLConnection connection = url.openConnection();
			connection.setReadTimeout( 20000 );
			connection.setConnectTimeout( 20000 );
			inputstream = connection.getInputStream();
			reader = new BufferedReader( new InputStreamReader( inputstream, "UTF-8" ) );
			List<Entry> result = new LinkedList<Entry>();
			String line;
			while( ( line = reader.readLine() ) != null )
				result.add( new Entry( line ) {
					@Override
					public void doit() throws IOException {
						RecvPath( baseDir, path, baseUrl + encodepath( path ), md5 );
					}
				} );
			return result;
		}
		finally {
			if( reader != null )
				reader.close();
			if( inputstream != null )
				inputstream.close();
		}
	}
}
