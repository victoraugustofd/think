package br.com.think.to;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectTo extends DatabaseTo
{
	private boolean			isString;
	private boolean			isQuerySeparated;
	private List< String >	dataList;
	private String			fieldToFind;
	private List< String >	queries;

	public SelectTo( Connection connection, Class transferObject, boolean usePlaceHolder, boolean isString, boolean isQuerySeparated, List< String > dataList, String... queries )
	{
		super( connection, transferObject, usePlaceHolder );
		setString( isString );
		setQuerySeparated( isQuerySeparated );
		setDataList( dataList );
		setQueries( queries );
	}
	
	public SelectTo( Connection connection, Class transferObject, boolean usePlaceHolder, boolean isString, boolean isQuerySeparated, String fieldToFind, List< String > dataList, String... queries )
	{
		super( connection, transferObject, usePlaceHolder );
		setString( isString );
		setQuerySeparated( isQuerySeparated );
		setDataList( dataList );
		setFieldToFind( fieldToFind );
		setQueries( queries );
	}
	
	public boolean isString()
	{
		return isString;
	}
	
	public boolean isQuerySeparated()
	{
		return isQuerySeparated;
	}
	
	public String getFieldToFind()
	{
		return fieldToFind;
	}
	
	public List< String > getDataList()
	{
		return dataList;
	}
	
	public List< String > getQueries()
	{
		return queries;
	}
	
	public void setString( boolean isString )
	{
		this.isString = isString;
	}
	
	public void setQuerySeparated( boolean isQuerySeparated )
	{
		this.isQuerySeparated = isQuerySeparated;
	}
	
	public void setDataList( List< String > dataList )
	{
		if( dataList != null )
			this.dataList = new ArrayList< String >( dataList );
	}

	public void setFieldToFind( String fieldToFind )
	{
		this.fieldToFind = fieldToFind;
	}

	public void setQueries( String[] queries )
	{
		if( queries.length > 0 )
			this.queries = Arrays.asList( queries );
	}
	
	public boolean isValid()
	{
		boolean isValid = false;
		
		isValid = ( getConnection()	!= null ) &&
				  ( getTo() != null ) &&
				  ( ( isQuerySeparated() && getQueries().size() > 1 ) || !isQuerySeparated() ) &&
				  ( ( getDataList() != null && !getDataList().isEmpty() ) || getDataList() == null );
		
		return isValid;
	}
}