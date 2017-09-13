package br.com.think.excel;

import br.com.think.utils.ExcelUtils;
import br.com.think.utils.ReflectionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ExcelReader
{
	private final static Logger logger = Logger.getLogger( ExcelReader.class );

	public static Object[][] _readSheet( ExcelReaderParameters parameters )
	{
		Object[][] sheetData = null;
		
		Workbook workbook;
		Sheet sheetToRead;
		
		Integer firstRowToRead;
		Integer lastRowToRead;
		Integer firstCellToRead;
		Integer lastCellToRead;
		
		if( parameters.isValid() )
		{
			workbook = ExcelUtils.getWorkbookToRead( parameters.getExcelFileAbsolutePath() );
			
			// Caso seja enviado um n�mero negativo para o n�mero da aba (sheet),
			// pegamos o valor absoluto (Math.abs) 
			sheetToRead = workbook.getSheetAt( Math.abs( parameters.getSheetToRead() ) );
			
			// firstRowToRead � par�metro opcional; se vier nulo, a primeira linha a ser lida ser�
			// a primeira com dados da aba (sheet) a ser lida.
			if( parameters.getFirstRowToRead() == null )
				firstRowToRead = sheetToRead.getFirstRowNum();
			else
				firstRowToRead = parameters.getFirstRowToRead();
			
			// lastRowToRead � par�metro opcional; se vier nulo, a �ltima linha a ser lida ser�
			// a �ltima com dados da aba (sheet) a ser lida.
			if ( parameters.getLastRowToRead() == null )
				lastRowToRead = sheetToRead.getLastRowNum();
			else
				lastRowToRead = parameters.getLastRowToRead();
			
			if ( parameters.getFirstCellToRead() == null )
				firstCellToRead = 0;
			else
				firstCellToRead = parameters.getFirstCellToRead();
			
			if ( parameters.getLastCellToRead() == null )
				lastCellToRead = 0;
			else
				lastCellToRead = parameters.getLastCellToRead();
			
			if( lastRowToRead < firstRowToRead )
			{
				Integer aux = new Integer( lastRowToRead );
				
				lastRowToRead = new Integer( firstRowToRead );
				firstRowToRead = new Integer( aux );
			}
			
			if( lastCellToRead < firstCellToRead )
			{
				Integer aux = new Integer( lastCellToRead );
				
				lastCellToRead = new Integer( firstCellToRead );
				firstCellToRead = new Integer( aux );
			}
			
			int rowsToRead = ExcelUtils.validateRowOrCellNumber( lastRowToRead ) - ExcelUtils.validateRowOrCellNumber( firstRowToRead ) + 1;
			int cellsToRead = ExcelUtils.validateRowOrCellNumber( lastCellToRead ) - ExcelUtils.validateRowOrCellNumber( firstCellToRead ) + 1;
			
			sheetData = new Object[ rowsToRead ][ cellsToRead ];
			
			for ( int i = firstRowToRead; i <= lastRowToRead; i++ )
			{
				Row nextRow = sheetToRead.getRow( i );
				
				if ( nextRow != null )
				{
					for ( int j = firstCellToRead; j <= lastCellToRead; j++ )
					{
						Cell cell = nextRow.getCell( j );
						
						if ( cell != null )
						{
							if( cell.getCellType() == Cell.CELL_TYPE_FORMULA )
							{
								sheetData[ i - firstRowToRead ][ j - firstCellToRead ] = ExcelUtils.getCellFormulaValue( workbook, cell );
							}
							else
							{
								sheetData[ i - firstRowToRead ][ j - firstCellToRead ] = ExcelUtils.getCellValue( cell );
							}
						}
					}
				}
			}
		}
		else
			System.out.println( "Par�metros inv�lidos para ler o arquivo Excel!" );
		
		return sheetData;
	}

	public static < T > List< T > readSheet( ExcelReaderParameters parameters )
	{
		List< T > listReturn = null;
		List< String > fields;

		Workbook workbook;
		Sheet sheetToRead;

		Class cls = parameters.getCls();
		Object object;
		T cellValue;

		Map< String, Method > setters = ReflectionUtils.getSetterMethods( cls );
		Map< String, Method > getters = ReflectionUtils.getGetterMethods( cls );

		Integer firstRowToRead;
		Integer lastRowToRead;
		Integer firstCellToRead;
		Integer lastCellToRead;

		int fieldNumber;

		if( parameters.isValid() )
		{
			listReturn = new ArrayList<>();
			fields = parameters.getFields();

			workbook = ExcelUtils.getWorkbookToRead( parameters.getExcelFileAbsolutePath() );

			sheetToRead = workbook.getSheetAt( Math.abs( parameters.getSheetToRead() ) );

			if( parameters.getFirstRowToRead() == null )
				firstRowToRead = sheetToRead.getFirstRowNum();
			else
				firstRowToRead = parameters.getFirstRowToRead();

			if ( parameters.getLastRowToRead() == null )
				lastRowToRead = sheetToRead.getLastRowNum();
			else
				lastRowToRead = parameters.getLastRowToRead();

			if ( parameters.getFirstCellToRead() == null )
				firstCellToRead = 0;
			else
				firstCellToRead = parameters.getFirstCellToRead();

			if ( parameters.getLastCellToRead() == null )
				lastCellToRead = 0;
			else
				lastCellToRead = parameters.getLastCellToRead();

			// if the last row to read is greater than the first, swap their values
			if( lastRowToRead < firstRowToRead )
			{
				firstRowToRead = firstRowToRead ^ lastRowToRead;
				lastRowToRead  = lastRowToRead ^ firstRowToRead;
				firstRowToRead = firstRowToRead ^ lastRowToRead;
			}

			// if the last cell to read is greater than the first, swap their values
			if( lastCellToRead < firstCellToRead )
			{
				firstCellToRead = firstCellToRead ^ lastCellToRead;
				lastCellToRead  = lastCellToRead ^ firstCellToRead;
				firstCellToRead = firstCellToRead ^ lastCellToRead;
			}

			for ( int i = firstRowToRead; i <= lastRowToRead; i++ )
			{
				Row row = sheetToRead.getRow( i );

				if ( row != null )
				{
					try
					{
						object = cls.newInstance();
						fieldNumber = 0;

						for ( int j = firstCellToRead; j <= lastCellToRead; j++ )
						{
							Cell cell = row.getCell( j );

							if ( cell != null )
							{
								if ( cell.getCellType() == Cell.CELL_TYPE_FORMULA )
								{
									cellValue = ExcelUtils.getCellFormulaValue( workbook, cell );
								}
								else
								{
									cellValue = ExcelUtils.getCellValue( cell, getters.get( "get" + fields.get( fieldNumber ) ).getReturnType() );
								}

								setters.get( "set" + fields.get( fieldNumber++ ) ).invoke( object, cellValue );
							}
						}

						listReturn.add( ( T ) object );
					}
					catch ( InstantiationException | IllegalAccessException | InvocationTargetException e )
					{
						logger.error( e.getMessage() );
					}
				}
			}
		}
		else
			logger.error( "Invalid parameters to read this sheet" );

		return listReturn;
	}
}