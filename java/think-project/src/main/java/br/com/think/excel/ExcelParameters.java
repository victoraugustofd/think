package br.com.think.excel;

import java.util.List;

public abstract class ExcelParameters
{
	protected String         excelFileAbsolutePath;
	protected Class          cls;
	protected List< String > fields;
	
	protected ExcelParameters( String excelFileAbsolutePath, Class cls, List< String > fields )
	{
		setExcelFileAbsolutePath( excelFileAbsolutePath );
		setCls( cls );
		setFields( fields );
	}
	
	public String getExcelFileAbsolutePath()
	{
		return excelFileAbsolutePath;
	}
	
	public void setExcelFileAbsolutePath( String excelFileAbsolutePath )
	{
		this.excelFileAbsolutePath = excelFileAbsolutePath;
	}

	public Class getCls()
	{
		return cls;
	}

	public void setCls( Class cls )
	{
		this.cls = cls;
	}

	public List<String> getFields()
	{
		return fields;
	}

	public void setFields( List<String> fields )
	{
		this.fields = fields;
	}

	protected boolean isValid()
	{
		return excelFileAbsolutePath != null && !excelFileAbsolutePath.isEmpty();
	}
}