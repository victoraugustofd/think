package br.com.think.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;

public class WriteFiles
{
	private static final Logger logger = Logger.getLogger( WriteFiles.class );
	
	public static void write( String filePath, List< String > data ) throws IOException
	{
		Path path = Paths.get( filePath );
		
		logger.info( "Writing file " + filePath );
		
		try
		{
			Files.write( path, data );
			
			logger.info( "File " + filePath + " written successfully - " + data.size() + " lines." );
		}
		catch( IOException e )
		{
			logger.error( e.getMessage() );
		}
	}
}