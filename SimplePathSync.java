import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SimplePathSync extends FileSync {
	public static interface Callback
	{
		public void fileFound( File f, String relPath ) throws IOException;
	}

	private final File basedir;

	public SimplePathSync( File bd )
	{
		basedir = bd;
	}

	public static void RecvPath( File baseDir, String relPath, String urlString, String md5 ) throws IOException
	{
		String s[] = relPath.split( "/", 2 );
		String name = s[0];

		if( name.length() == 0 )
		{
			if( s.length >= 2 )
				RecvPath( baseDir, s[1], urlString, md5 );
			return;
		}
		if( name.indexOf( '.' ) != -1 )
		{
			RecvFile( baseDir, name, urlString, md5 );
			return;
		}
		File f = new File( baseDir, name );
		System.out.println( "Creating directory " + f.getPath() + "." );
		if( !f.mkdir() )
			if( !f.isDirectory() )
				throw new IOException( "Cannot create directory " + name + "." );
		if( s.length < 2 )
			return;
		RecvPath( f, s[1], urlString, md5 );
	}

	public void RecvPath( String desc ) throws Exception
	{
		String[] fields = desc.split( ";", 4 );
		RecvPath( basedir, fields[0], fields[1], fields[2] );
	}

	public static void WalkPath( File baseDir, String relPath, Callback callback ) throws IOException
	{
		String prefix = relPath == null ? "" : relPath + "/";
		File files[] = baseDir.listFiles();
		if( files == null )
			return;
		for( File f : files )
		{
			String name = f.getName();
			if( f.isDirectory() )
			{
				if( name.indexOf( '.' ) == -1 )
					WalkPath( f, prefix + name, callback );
			}
			else
			{
				if( name.indexOf( '.' ) != -1 )
					callback.fileFound( f, prefix + name );
			}
		}
	}

	private static String encodepath( String relPath )
	{
		String[] parts = relPath.split( "/" );
		String res = "";
		try {
			for( String i : parts )
				res += "/" + URLEncoder.encode( i, "UTF-8" );
		} catch( UnsupportedEncodingException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res.replace( "+", "%20" );
	}

	public List<String> ListPaths( final String baseUrl, final boolean urlEncode ) throws IOException
	{
		final List<String> res = new ArrayList<String>();
		WalkPath( basedir, null, new Callback() {
			@Override
			public void fileFound( File f, String relPath ) throws IOException {
				if( relPath.indexOf( ';' ) == -1 )
				{
					String url = urlEncode ? baseUrl + encodepath( relPath ) : baseUrl + "/" + relPath;
					res.add( relPath + ";" + url + ";" + md5sum( f ) + ";" );
				}
			}
		});
		return res;
	}
}
