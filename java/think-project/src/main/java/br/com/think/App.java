package br.com.think;

import java.io.IOException;

import org.apache.log4j.Logger;

import br.com.think.file.ReadFiles;

public class App
{
	private static final Logger logger = Logger.getLogger( App.class );
	
	public static void main( String[] args )
	{
		logger.info( "Begin" );
		
		try
		{
			ReadFiles.divideFile( "C:\\Users\\Victor\\Desktop\\Extração_DDD_11.txt", "C:\\Users\\Victor\\Desktop", "Teste", false, 100_000 );
		}
		catch ( IOException e )
		{
			logger.error( e.getMessage() );
		}
		
		logger.info( "Begin" );
	}
}