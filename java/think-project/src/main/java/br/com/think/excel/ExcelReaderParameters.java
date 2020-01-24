package br.com.think.excel;

import java.util.List;

public class ExcelReaderParameters extends ExcelParameters
{
	private Integer	sheetToRead;
	private Integer	firstRowToRead;
	private Integer	lastRowToRead;
	private Integer	firstCellToRead;
	private Integer	lastCellToRead;
	
	public ExcelReaderParameters( String excelFileAbsolutePath, Class cls, List< String > fields, Integer sheetToRead, Integer firstRowToRead,
			Integer lastRowToRead, Integer firstCellToRead, Integer lastCellToRead )
	{
		super( excelFileAbsolutePath, cls, fields );
		setSheetToRead( sheetToRead );
		setFirstRowToRead( firstRowToRead );
		setLastRowToRead( lastRowToRead );
		setFirstCellToRead( firstCellToRead );
		setLastCellToRead( lastCellToRead );
	}
	
	public Integer getSheetToRead()
	{
		return sheetToRead;
	}
	
	public Integer getFirstRowToRead()
	{
		return firstRowToRead;
	}
	
	public Integer getLastRowToRead()
	{
		return lastRowToRead;
	}
	
	public Integer getFirstCellToRead()
	{
		return firstCellToRead;
	}
	
	public Integer getLastCellToRead()
	{
		return lastCellToRead;
	}
	
	public void setSheetToRead( Integer sheetToRead )
	{
		this.sheetToRead = sheetToRead;
	}
	
	public void setFirstRowToRead( Integer firstRowToRead )
	{
		this.firstRowToRead = firstRowToRead;
	}
	
	public void setLastRowToRead( Integer lastRowToRead )
	{
		this.lastRowToRead = lastRowToRead;
	}
	
	public void setFirstCellToRead( Integer firstCellToRead )
	{
		this.firstCellToRead = firstCellToRead;
	}
	
	public void setLastCellToRead( Integer lastCellToRead )
	{
		this.lastCellToRead = lastCellToRead;
	}
	
	public boolean isValid()
	{
		return super.isValid() 		   &&
			   sheetToRead 	   != null &&
			   firstRowToRead  != null &&
//			   lastRowToRead   != null &&
			   firstCellToRead != null &&
			   lastCellToRead  != null;
	}
}