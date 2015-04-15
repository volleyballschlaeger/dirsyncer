import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileSync {
	public static String BytesToHex( byte[] in )
	{
		StringBuilder sb = new StringBuilder();
		for( byte b : in )
			sb.append( String.format( "%02x", b ) );
		return sb.toString();
	}

	public static String md5sum( File file ) throws IOException
	{
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "MD5" );
		} catch( NoSuchAlgorithmException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( md == null )
			return null;

		InputStream inputstream = null;
		try {
			inputstream = new FileInputStream( file );
			byte[] buffer = new byte[4096];
			int bytesread = 0;
			while( bytesread >= 0 )
			{
				bytesread = inputstream.read( buffer, 0, 4096 );
				if( bytesread > 0 )
					md.update( buffer, 0, bytesread );
			}
		}
		finally {
			if( inputstream != null )
				inputstream.close();
		}
		return BytesToHex( md.digest() );
	}

	public static void Stream2StreamCopy( OutputStream outputstream, InputStream inputstream ) throws IOException
	{
		byte[] buffer = new byte[4096];
		int bytesread = 0;
		try {
			while( bytesread >= 0 )
			{
				bytesread = inputstream.read( buffer, 0, 4096 );
				if( bytesread > 0 )
					outputstream.write( buffer, 0, bytesread );
			}
		}
		finally {
			inputstream.close();
		}
	}

	public static void RecvFile( File dir, String filename, String urlString, String md5 ) throws IOException
	{
		URL url = new URL( urlString );
		File file = new File( dir, filename );

		if( file.exists() )
		{
			if( md5 != null )
				if( md5.equals( md5sum( file ) ) )
				{
					System.out.println( file.getAbsolutePath() + " is up to date." );
					return;
				}
		}

		System.out.println( "Downloading " + urlString + " ..." );
		File tmp = File.createTempFile( "tmp", "", dir );
		InputStream inputstream = null;
		OutputStream outputstream = null;

		try {
			URLConnection connection = url.openConnection();
			connection.setReadTimeout( 20000 );
			connection.setConnectTimeout( 20000 );
			inputstream = connection.getInputStream();
			outputstream = new FileOutputStream( tmp );
			Stream2StreamCopy( outputstream, inputstream );
			outputstream.close();

			if( md5 != null )
				if( !md5.equals( md5sum( tmp ) ) )
					throw new IOException( "Download is corrupt." );
			if( !tmp.renameTo( file ) )
				throw new IOException( "Cannot rename file." );
			System.out.println( "... success!" );
		}
		finally {
			if( inputstream != null )
				inputstream.close();
			if( outputstream != null )
				outputstream.close();
			tmp.delete();
		}
	}
}
