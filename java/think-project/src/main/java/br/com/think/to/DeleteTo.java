package br.com.think.to;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DeleteTo< T > extends DatabaseTo
{
	private boolean isString;
	private String fieldToFind;
	private List< T > dataList;
	
	public DeleteTo( Connection connection, String owner, String tableName, Class transferObject, boolean usePlaceHolder,
			boolean isString, String fieldToFind, List< T > dataList )
	{
		super( connection, owner, tableName, null, transferObject, usePlaceHolder );
		setString( isString );
		setFieldToFind( fieldToFind );
		setDataList( dataList );
	}
	
	public boolean isString()
	{
		return isString;
	}
	
	public String getFieldToFind()
	{
		return fieldToFind;
	}

	public List< T > getDataList()
	{
		return dataList;
	}
	
	public void setString( boolean isString )
	{
		this.isString = isString;
	}
	
	public void setFieldToFind( String fieldToFind )
	{
		this.fieldToFind = fieldToFind;
	}

	public void setDataList( List< T > dataList )
	{
		this.dataList = new ArrayList< T >( dataList );
	}
	
	public boolean isValid()
	{
		boolean isValid = false;
		
		isValid = super.isValid() &&
				  ( getFieldToFind() != null && !getFieldToFind().isEmpty() ) &&
				  ( getDataList() 	 != null && !getDataList().isEmpty() );
		
		return isValid;
	}
}