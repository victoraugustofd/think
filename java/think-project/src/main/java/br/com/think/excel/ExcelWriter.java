package br.com.think.excel;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;

import br.com.think.utils.ExcelUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelWriter
{
	private final static Logger logger = Logger.getLogger( ExcelWriter.class );

	public static void writeSheet( ExcelWriterParameters headerParameters, ExcelWriterParameters dataParameters,
	        String sheetName, List< String > headerFields, Object[][] data )
	{
		SXSSFWorkbook workbook = null;
		XSSFDataFormat dataFormat;
		XSSFCellStyle cellStyle;
		XSSFFont font;
		SXSSFSheet sheet = null;
		
		int rowCount = 0;
		int columnCount = 0;
		int firstColumn = 0;
		int lastColumn = 0;
		
		int sheetCount = 1;
		
		List< Object[][] > listData = divideArray( data, ExcelUtils.MAX_ROW - 2 );
		
		int numberOfSheets = listData.size();
		
		if ( dataParameters != null && dataParameters.isValid() )
		{
			try
			{
				workbook = ExcelUtils.getWorkbookToWrite( headerParameters.getExcelFileAbsolutePath() );
				
				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Begin process" );
				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Number of Sheets: " + numberOfSheets );
				
				for ( int i = 0; i < numberOfSheets; i++ )
				{
					logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Processing sheet " + sheetCount );
					
					sheet = workbook.createSheet( sheetName != null ? sheetName + "_" + sheetCount++ : "Sheet" );
					
					if ( headerParameters != null && headerParameters.isValid() )
					{
						if ( headerFields != null && !headerFields.isEmpty() )
						{
							logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Writing header" );
							
							SXSSFRow row = sheet.createRow( ++rowCount );
							dataFormat = ( XSSFDataFormat ) workbook.createDataFormat();
							font = ( XSSFFont ) workbook.createFont();
							
							cellStyle = ( XSSFCellStyle ) workbook.createCellStyle();
							cellStyle.setAlignment( HorizontalAlignment.CENTER );
							cellStyle.setVerticalAlignment( VerticalAlignment.CENTER );
							
							cellStyle.setFillForegroundColor( new XSSFColor( headerParameters.getBackgroundColor() ) );
							cellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
							
							font.setFontName( headerParameters.getFont().getFontName() );
							font.setFontHeightInPoints( ( short ) headerParameters.getFont().getSize() );
							font.setColor( new XSSFColor( headerParameters.getFontColor() ) );
							
							cellStyle.setFont( font );
							
							if ( headerParameters.isBordered() )
							{
								cellStyle.setBorderTop( BorderStyle.THIN );
								cellStyle.setBorderRight( BorderStyle.THIN );
								cellStyle.setBorderBottom( BorderStyle.THIN );
								cellStyle.setBorderLeft( BorderStyle.THIN );
							}
							
							for ( String fieldName : headerFields )
							{
								SXSSFCell cell = row.createCell( ++columnCount );
								
								ExcelUtils.setCellValue( cellStyle, dataFormat, cell, fieldName );
							}
						}
					}
					
					cellStyle = ( XSSFCellStyle ) workbook.createCellStyle();
					dataFormat = ( XSSFDataFormat ) workbook.createDataFormat();
					font = ( XSSFFont ) workbook.createFont();
					
					cellStyle.setFillForegroundColor( new XSSFColor( dataParameters.getBackgroundColor() ) );
					cellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
					
					cellStyle.setVerticalAlignment( VerticalAlignment.CENTER );
					
					font.setFontName( dataParameters.getFont().getFontName() );
					font.setFontHeightInPoints( ( short ) dataParameters.getFont().getSize() );
					font.setColor( new XSSFColor( dataParameters.getFontColor() ) );
					
					cellStyle.setFont( font );
					
					if ( dataParameters.isBordered() )
					{
						cellStyle.setBorderTop( BorderStyle.THIN );
						cellStyle.setBorderRight( BorderStyle.THIN );
						cellStyle.setBorderBottom( BorderStyle.THIN );
						cellStyle.setBorderLeft( BorderStyle.THIN );
					}
					
					for ( Object[] rowData : listData.get( i ) )
					{
						SXSSFRow row = sheet.createRow( ++rowCount );
						
						columnCount = 0;
						
						for ( Object cellData : rowData )
						{
							SXSSFCell cell = row.createCell( ++columnCount );
							
							ExcelUtils.setCellValue( cellStyle, dataFormat, cell, cellData );
						}
						
						if ( rowCount % 1000 == 0 )
							logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT
							        + "- Row(s) written so far: " + rowCount );
						
						firstColumn = row.getFirstCellNum();
						lastColumn = row.getLastCellNum() - 1;
					}

					logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Row(s) written so far: "
					        + rowCount );
					
					sheet.trackAllColumnsForAutoSizing();
					
					sheet.setAutoFilter( new CellRangeAddress( sheet.getFirstRowNum(), sheet.getLastRowNum(),
					        firstColumn, lastColumn ) );
					
					for ( int j = 1; j <= headerFields.size(); j++ )
					{
						sheet.autoSizeColumn( j );
					}
					
					sheet.setColumnWidth( 0, 575 );
					
					sheet.createFreezePane( 2, 2 );
					
					rowCount = 0;
					columnCount = 0;
				}
				
				FileOutputStream outputStream = new FileOutputStream( headerParameters.getExcelFileAbsolutePath() );

				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Begin writing on file" );
				
				workbook.write( outputStream );

				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Finish process" );
				
				workbook.close();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		else
			System.out.println( "Par�metros inv�lidos para gerar o arquivo Excel!" );
	}
	
	public static void writeOneSheetOrMore( ExcelWriterParameters headerParameters,
	        ExcelWriterParameters dataParameters, List< String > sheetsName, List< List< String > > headersFields,
	        Object[][]... data )
	{
		SXSSFWorkbook workbook = null;
		XSSFDataFormat dataFormat;
		XSSFCellStyle cellStyle;
		XSSFFont font;
		SXSSFSheet sheet = null;
		
		int rowCount = 0;
		int columnCount = 0;
		int firstColumn = 0;
		int lastColumn = 0;
		
		int sheetCount = 1;
		
		int originalColumnWidth;
		
		int numberOfSheets = data.length;
		
		if ( dataParameters != null && dataParameters.isValid() )
		{
			try
			{
				workbook = ExcelUtils.getWorkbookToWrite( headerParameters.getExcelFileAbsolutePath() );

				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Begin process" );
				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Number of Sheets: " + numberOfSheets );
				
				for ( int i = 0; i < numberOfSheets; i++ )
				{
					logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Processing sheet " + sheetCount++ );
					
					sheet = workbook.createSheet( sheetsName.get( i ) );
					
					if ( headerParameters != null && headerParameters.isValid() )
					{
						if ( headersFields.get( i ) != null && !headersFields.get( i ).isEmpty() )
						{
							logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Writing header" );
							
							SXSSFRow row = sheet.createRow( ++rowCount );
							dataFormat = ( XSSFDataFormat ) workbook.createDataFormat();
							font = ( XSSFFont ) workbook.createFont();
							
							cellStyle = ( XSSFCellStyle ) workbook.createCellStyle();
							cellStyle.setAlignment( HorizontalAlignment.CENTER );
							cellStyle.setVerticalAlignment( VerticalAlignment.CENTER );
							
							cellStyle.setFillForegroundColor( new XSSFColor( headerParameters.getBackgroundColor() ) );
							cellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
							
							font.setFontName( headerParameters.getFont().getFontName() );
							font.setFontHeightInPoints( ( short ) headerParameters.getFont().getSize() );
							font.setColor( new XSSFColor( headerParameters.getFontColor() ) );
							
							cellStyle.setFont( font );
							
							if ( headerParameters.isBordered() )
							{
								cellStyle.setBorderTop( BorderStyle.THIN );
								cellStyle.setBorderRight( BorderStyle.THIN );
								cellStyle.setBorderBottom( BorderStyle.THIN );
								cellStyle.setBorderLeft( BorderStyle.THIN );
							}
							
							for ( String fieldName : headersFields.get( i ) )
							{
								SXSSFCell cell = row.createCell( ++columnCount );
								
								ExcelUtils.setCellValue( cellStyle, dataFormat, cell, fieldName );
							}
						}
					}
					
					cellStyle = ( XSSFCellStyle ) workbook.createCellStyle();
					dataFormat = ( XSSFDataFormat ) workbook.createDataFormat();
					font = ( XSSFFont ) workbook.createFont();
					
					cellStyle.setFillForegroundColor( new XSSFColor( dataParameters.getBackgroundColor() ) );
					cellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
					
					cellStyle.setVerticalAlignment( VerticalAlignment.CENTER );
					
					font.setFontName( dataParameters.getFont().getFontName() );
					font.setFontHeightInPoints( ( short ) dataParameters.getFont().getSize() );
					font.setColor( new XSSFColor( dataParameters.getFontColor() ) );
					
					cellStyle.setFont( font );
					
					if ( dataParameters.isBordered() )
					{
						cellStyle.setBorderTop( BorderStyle.THIN );
						cellStyle.setBorderRight( BorderStyle.THIN );
						cellStyle.setBorderBottom( BorderStyle.THIN );
						cellStyle.setBorderLeft( BorderStyle.THIN );
					}
					
					for ( Object[] rowData : data[ i ] )
					{
						SXSSFRow row = sheet.createRow( ++rowCount );
						
						columnCount = 0;
						
						for ( Object cellData : rowData )
						{
							SXSSFCell cell = row.createCell( ++columnCount );
							
							ExcelUtils.setCellValue( cellStyle, dataFormat, cell, cellData );
						}
						
						if ( rowCount % 1000 == 0 )
							logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT
							        + "- Row(s) written so far: " + rowCount );
						
						firstColumn = row.getFirstCellNum();
						lastColumn = row.getLastCellNum() - 1;
					}
					
					sheet.trackAllColumnsForAutoSizing();
					
					sheet.setAutoFilter( new CellRangeAddress( sheet.getFirstRowNum(), sheet.getLastRowNum(),
					        firstColumn, lastColumn ) );
					
					for ( int j = 1; j <= headersFields.get( i ).size(); j++ )
					{
						originalColumnWidth = sheet.getColumnWidth( j );
						
						sheet.autoSizeColumn( j );
						
						// Reset to original width if resized width is smaller
						// than default/original
						if ( originalColumnWidth > sheet.getColumnWidth( j ) )
							sheet.setColumnWidth( j, originalColumnWidth );
					}
					
					sheet.setColumnWidth( 0, 575 );
					
					sheet.createFreezePane( 2, 2 );
					
					sheet.setDisplayGridlines( false );

					logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Row(s) written so far: "
					        + rowCount );
					
					rowCount = 0;
					columnCount = 0;
				}
				
				FileOutputStream outputStream = new FileOutputStream( headerParameters.getExcelFileAbsolutePath() );

				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Begin writing on file" );
				
				workbook.write( outputStream );

				logger.info( ExcelUtils.MESSAGE_WRITER_SYSOUT + "- Finish process" );
				
				workbook.close();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		else
			System.out.println( "Par�metros inv�lidos para gerar o arquivo Excel!" );
	}
	
	private static List< Object[][] > divideArray( Object[][] data, int chunksize )
	{
		List< Object[][] > result = new ArrayList< Object[][] >();
		
		int start = 0;
		int length = data.length;
		
		while ( start < length )
		{
			int end = Math.min( data.length, start + chunksize );
			
			result.add( Arrays.copyOfRange( data, start, end ) );
			
			/*
			 * result.add( ( Object[][] ) Arrays.stream( data ) .skip( start )
			 * .limit( end ) .map( array -> Arrays .stream( array ) .toArray() )
			 * .toArray() );
			 */
			
			start += chunksize;
		}
		
		return result;
	}
}