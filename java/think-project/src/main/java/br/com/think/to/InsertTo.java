package br.com.think.to;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InsertTo< T > extends DatabaseTo
{
	private List< T > dataList;

	public InsertTo( Connection connection, String owner, String tableName, List< String > fields, Class transferObject, boolean usePlaceHolder,
			List< T > dataList )
	{
		super( connection, owner, tableName, fields, transferObject, usePlaceHolder );
		setDataList( dataList );
	}

	public List< T > getDataList()
	{
		return dataList;
	}

	public void setDataList( List< T > dataList )
	{
		if ( dataList != null )
			this.dataList = new ArrayList<>( dataList );
	}

	public boolean isValid()
	{
		boolean isValid = false;

		isValid = super.isValid() &&
				  ( getFields()	  != null && !getFields().isEmpty() ) &&
				  ( getDataList() != null && !getDataList().isEmpty() );

		return isValid;
	}
}