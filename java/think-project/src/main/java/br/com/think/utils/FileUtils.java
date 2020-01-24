package br.com.think.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import br.com.think.enums.FileTypes;
import br.com.think.file.ReadFiles;
import br.com.think.file.WriteFiles;

public abstract class FileUtils
{
	private static final Logger logger = Logger.getLogger( FileUtils.class );
	
	public static boolean validateFileExtension( FileTypes fileType, String fileName )
	{
		String extension = fileName.substring( fileName.lastIndexOf( "." ) );
		
		if( null != extension && !extension.isEmpty() )
		{
			if( FileTypes.Excel.equals( fileType ) )
			{
				if( "xlsx".equalsIgnoreCase( extension ) || "xls".equalsIgnoreCase( extension ) )
					return true;
			}
			else if( FileTypes.Text.equals( fileType ) )
			{
				if( "txt".equalsIgnoreCase( extension ) || "xml".equalsIgnoreCase( extension ) )
					return true;
			}
		}
		
		return false;
	}
	
	public static void divideFile( String pathToFile, String pathToSaveFiles, String filesName, char fileSeparator, boolean isDivisionByNumberOfFiles, int quantity ) throws IOException
	{
		if ( quantity <= 0 )
			quantity = 1;
		
		Path filePath = Paths.get( pathToFile );
		
		if( null != filePath )
		{
			Path destinationPath = Paths.get( pathToSaveFiles );
			
			if( null != destinationPath )
			{
				List< String > fileLines = ReadFiles.read( pathToFile );
				int sizeOfEachList = isDivisionByNumberOfFiles ? ( fileLines.size() / quantity ) : quantity;
				
				List< List< String > > lists = ListUtils.divideList( fileLines, sizeOfEachList, isDivisionByNumberOfFiles );
				
				AtomicInteger fileNumber = new AtomicInteger( 0 );
				
				lists.stream().forEach(list -> {
					String fileName = filesName + fileSeparator + fileNumber.incrementAndGet() + ".txt";

					try {
						WriteFiles.write(destinationPath.toAbsolutePath().toString() + "\\" + fileName, list);
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				});
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
}