package br.com.think.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import br.com.think.utils.ListUtils;

public class ReadFiles
{
	private static final Logger logger = Logger.getLogger( ReadFiles.class );
	
	public static List< String > read( String pathToFile ) throws IOException
	{
		List< String > fileData = null;
		Path path = Paths.get( pathToFile );
		
		logger.info( "Reading file " + pathToFile );
		
		if( null != path )
		{
			try
			{
				fileData = Files.lines( path ).collect( Collectors.toList() );
				
				logger.info( "File " + pathToFile + " read successfully - " + fileData.size() + " lines." );
			}
			catch ( IOException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
		}
		else
		{
			logger.error( "File doesn't exist!" );
		}
		
		return fileData;
	}
}