package br.com.think;

import java.io.IOException;

import org.apache.log4j.Logger;

import br.com.think.utils.FileUtils;

public class App
{
	private static final Logger logger = Logger.getLogger( App.class );
	
	public static void main( String[] args )
	{
		logger.info( "Begin" );
		
		try
		{
			FileUtils.divideFile( "C:\\Users\\Victor\\Desktop\\Extração_DDD_11.txt", "C:\\Users\\Victor\\Desktop\\Teste", "Teste", '-', true, 10 );
		}
		catch ( IOException e )
		{
			logger.error( e.getMessage() );
		}
	}
}