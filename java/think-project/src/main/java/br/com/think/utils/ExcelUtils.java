package br.com.think.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.think.enums.FileTypes;

public abstract class ExcelUtils
{
	private static final Logger logger = Logger.getLogger( ExcelUtils.class );
	
	public static final int WIDTH_ARROW_BUTTON = 1300;
	public static final int MAX_ROW = 1048576;
	public static final String MESSAGE_READER_SYSOUT = "- [ EXCEL READER ] ";
	public static final String MESSAGE_WRITER_SYSOUT = "- [ EXCEL WRITER ] ";
	public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
	public static final int UNIT_OFFSET_LENGTH = 7;
	public static final int[] UNIT_OFFSET_MAP = new int[] { 0, 36, 73, 109, 146, 182, 219 };
	
	public static Workbook getWorkbookToRead( String excelFileAbsolutePath )
	{
		Workbook workbook = null;
		File file;
		InputStream inputStream;
		
		try
		{
			if( FileUtils.validateFileExtension( FileTypes.Excel, excelFileAbsolutePath ) )
			{
				if ( excelFileAbsolutePath.endsWith( "xlsx" ) )
				{
					file = new File( excelFileAbsolutePath );
					
					if( !file.exists() )
						file.createNewFile();
					
					workbook = new XSSFWorkbook( file );
				}
				else if ( excelFileAbsolutePath.endsWith( "xls" ) )
				{
					file = new File( excelFileAbsolutePath );
					inputStream = new FileInputStream( file );
					
					workbook = new HSSFWorkbook( inputStream );
				}
				else
				{
					throw new IllegalArgumentException( "O arquivo de entrada não é um arquivo Excel!" );
				}
			}
			else
				logger.error( "Invalid file!" );
		}
		catch ( IOException | InvalidFormatException e )
		{
			logger.error( e.getMessage() );
		}
		
		return workbook;
	}
	
	public static SXSSFWorkbook getWorkbookToWrite( String excelFileAbsolutePath )
	{
		SXSSFWorkbook workbook = null;
		
		if ( excelFileAbsolutePath.endsWith( "xlsx" ) )
		{
			workbook = new SXSSFWorkbook( 500 );
		}
		else
		{
			throw new IllegalArgumentException( "O arquivo de entrada n�o � um arquivo Excel!" );
		}
		
		return workbook;
	}
	
	public static int validateRowOrCellNumber( int rowOrCellNumber )
	{
		int rowOrCellValid;
		
		if( rowOrCellNumber < 0 )
			rowOrCellValid = 0;
		else
			rowOrCellValid = rowOrCellNumber;
		
		return rowOrCellValid;
	}

	public static < T > T getCellFormulaValue( Workbook workbook, Cell cell )
	{
		T cellValueReturn = null;

		if( cell.getCellTypeEnum() == CellType.FORMULA )
		{
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			CellValue cellValue = evaluator.evaluate( cell );

			switch( cellValue.getCellTypeEnum() )
			{
				case BOOLEAN:
				{
					cellValueReturn = ( T ) new Boolean( cellValue.getBooleanValue() );
					break;
				}

				case NUMERIC:
				{
					cellValueReturn = ( T ) new Double( cellValue.getNumberValue() );
					break;
				}

				case STRING:
				{
					cellValueReturn = ( T ) cellValue.getStringValue();
					break;
				}

				case BLANK:
				{
					cellValueReturn = ( T ) "-";
					break;
				}

				case ERROR:
				{
					cellValueReturn = ( T ) new Byte( cellValue.getErrorValue() );
					break;
				}
				default:
				{
					cellValueReturn = ( T ) "-";
					break;
				}
			}

			return cellValueReturn;
		}

		return getCellValue( cell );
	}

	public static < T > T getCellValue( Cell cell )
	{
		T cellValue = null;

		switch ( cell.getCellTypeEnum() )
		{
			case STRING:
			{
				cellValue = ( T ) cell.getStringCellValue();
				break;
			}
			case BOOLEAN:
			{
				cellValue = ( T ) new Boolean( cell.getBooleanCellValue() );
				break;
			}
			case NUMERIC:
			{
				cellValue = ( T ) new Double( cell.getNumericCellValue() );
				break;
			}
			case BLANK:
			{
				cellValue = ( T ) "-";
				break;
			}
			default:
			{
				cellValue = ( T ) "-";
				break;
			}
		}
		
		return cellValue;
		
		/*
		 * When poi 4.0 release, use code below
		 *
		 *switch ( cell.getCellType() )
		{
			case CellType.STRING:
				return cell.getStringCellValue();
			
			case CellType.BOOLEAN:
				return cell.getBooleanCellValue();
			
			case CellType.NUMERIC:
				return cell.getNumericCellValue();
			
			case CellType.BLANK:
				return "-";
		}*/
	}

	public static < T > T getCellValue( Cell cell, Type numericType )
	{
		T cellValue = null;

		switch ( cell.getCellTypeEnum() )
		{
			case STRING:
			{
				cellValue = ( T ) cell.getStringCellValue();
				break;
			}
			case BOOLEAN:
			{
				cellValue = ( T ) new Boolean( cell.getBooleanCellValue() );
				break;
			}
			case NUMERIC:
			{
				cellValue = ( T ) new Double( cell.getNumericCellValue() );

				if( numericType.equals( Long.TYPE ) || numericType.equals( Long.class ) )
					cellValue = ( T ) new Long( ( ( Double ) cellValue ).longValue() );
				else if( numericType.equals( Integer.class ) )
					cellValue = ( T ) new Integer( String.valueOf( cellValue ) );

				break;
			}
			case BLANK:
			{
				cellValue = ( T ) "-";
				break;
			}
			default:
			{
				cellValue = ( T ) "-";
				break;
			}
		}

		return cellValue;

		/*
		 * When poi 4.0 release, use code below
		 *
		 *switch ( cell.getCellType() )
		{
			case CellType.STRING:
				return cell.getStringCellValue();

			case CellType.BOOLEAN:
				return cell.getBooleanCellValue();

			case CellType.NUMERIC:
				return cell.getNumericCellValue();

			case CellType.BLANK:
				return "-";
		}*/
	}

	public static void setCellValue( XSSFCellStyle cellStyle, XSSFDataFormat dataFormat, SXSSFCell cell, Object cellData )
	{
		if( cellData == null )
			cell.setCellValue( "" );
		else
		{
			if ( cellData instanceof String )
			{
				cell.setCellType( CellType.STRING );
				cell.setCellValue( ( String ) cellData );
			}
			else if ( cellData instanceof Byte || cellData instanceof Short || cellData instanceof Integer || cellData instanceof Long )
			{
				cell.setCellType( CellType.NUMERIC );
				
				short numberFormat = dataFormat.getFormat( "0" );
				
				cellStyle.setDataFormat( numberFormat );
				
				if( cellData instanceof Integer )
					cell.setCellValue( ( Integer ) cellData );
				else if( cellData instanceof Long )
					cell.setCellValue( ( Long ) cellData );
				else
					cell.setCellValue( String.valueOf( cellData ) );
			}
			else if ( cellData instanceof Double )
			{
				cell.setCellType( CellType.NUMERIC );
				
				short doubleFormat = dataFormat.getFormat( "0.00" );
				
				cellStyle.setDataFormat( doubleFormat );
				
				cell.setCellValue( ( Double ) cellData );
			}
			else if ( cellData instanceof Timestamp || cellData instanceof Date )
			{
				cell.setCellType( CellType.STRING );
				
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSSS" );
				
				cell.setCellValue( ( String ) sdf.format( cellData ) );
			}
		}
		
		cell.setCellStyle( cellStyle );
	}
	
	/**
	 * pixel units to excel width units(units of 1/256th of a character width)
	 * 
	 * @param pxs
	 * @return
	 */
	public static short pixel2WidthUnits( int pxs )
	{
		short widthUnits = ( short ) ( EXCEL_COLUMN_WIDTH_FACTOR * ( pxs / UNIT_OFFSET_LENGTH ) );
		
		widthUnits += UNIT_OFFSET_MAP[ ( pxs % UNIT_OFFSET_LENGTH ) ];
		
		return widthUnits;
	}
	
	/**
	 * excel width units(units of 1/256th of a character width) to pixel units
	 * 
	 * @param widthUnits
	 * @return
	 */
	public static int widthUnits2Pixel( short widthUnits )
	{
		int pixels = ( widthUnits / EXCEL_COLUMN_WIDTH_FACTOR ) * UNIT_OFFSET_LENGTH;
		
		int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
		pixels += Math
		        .round( ( float ) offsetWidthUnits / ( ( float ) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH ) );
		
		return pixels;
	}
}