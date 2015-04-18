import java.io.File;
import java.io.IOException;

public class SimplePathSync extends FileSync {
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
}
