package br.com.think.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

public class ReadFiles
{
	private static final Logger logger = Logger.getLogger( ReadFiles.class );
	
	public static List< String > read( String pathToFile ) throws IOException
	{
		List< String > fileData = null;
		Path path = Paths.get( pathToFile );
		
		logger.info( "Reading file " + pathToFile );
		
		if( validatePath( path ) )
		{
			try
			{
				fileData = Files.lines( path ).collect( Collectors.toList() );
				
				logger.info( "File " + pathToFile + " read successfully - " + fileData.size() + " lines." );
			}
			catch ( IOException e )
			{
				logger.error( e.getMessage() );
			}
		}
		else
		{
			logger.error( "File doesn't exist!" );
		}
		
		return fileData;
	}
	
	public static List< List< String > > divideList( List< String > list, int sizeOfEachList )
	{
		if ( sizeOfEachList <= 0 )
			sizeOfEachList = 1;
		
		return Lists.partition( list, sizeOfEachList );
	}
	
	public static void divideFile( String pathToFile, String pathToSaveFiles, String filesName, boolean isDivisionByNumberOfFiles, int quantity ) throws IOException
	{
		if ( quantity <= 0 )
			quantity = 1;
		
		Path filePath = Paths.get( pathToFile );
		
		if( validatePath( filePath ) )
		{
			Path destinationPath = Paths.get( pathToSaveFiles );
			
			if( validatePath( destinationPath ) )
			{
				List< String > fileLines = read( pathToFile );
				int sizeOfEachList = isDivisionByNumberOfFiles ? ( fileLines.size() / quantity ) : quantity;
				
				List< List< String > > lists = divideList( fileLines, sizeOfEachList );
				
				AtomicInteger fileNumber = new AtomicInteger( 0 );
				
				lists
				.stream()
				.forEach( list ->
						{
							String fileName = filesName + "_" + fileNumber.incrementAndGet() + ".txt";
							
							try
							{
								WriteFiles.write( destinationPath.toAbsolutePath().toString() + "\\" + fileName, list );
							}
							catch ( IOException e )
							{
								logger.error( e.getMessage() );
							}
						} );
			}
			else
			{
				logger.error( "Destination path is not valid!" );
			}
		}
		else
		{
			logger.error( "File doesn't exist!" );
		}
	}
	
	public static boolean validatePath( Path path )
	{
		return null != path;
	}
}