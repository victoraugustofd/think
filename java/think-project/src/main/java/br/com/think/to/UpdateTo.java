package br.com.think.to;

import br.com.think.utils.GenericDaoUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UpdateTo< T > extends DatabaseTo
{
	private boolean isString;
	private boolean isWithDateUpdate;
	private List< String > dateFields;
	private String dateValue;
	private String fieldToFind;
	private List< T > dataList;
	
	public UpdateTo( Connection connection, String owner, String table, List< String > fields, Class transferObject, boolean usePlaceHolder,
			boolean isString, boolean isWithDateUpdate, List< String > dateFields, String dateValue, String fieldToFind, List< T > dataList )
	{
		super( connection, owner, table, fields, transferObject, usePlaceHolder );
		setString( isString );
		setWithDateUpdate( isWithDateUpdate );
		setDateFields( dateFields );
		setDateValue( dateValue );
		setFieldToFind( fieldToFind );
		setDataList( dataList );
	}
	
	public boolean isString()
	{
		return isString;
	}
	
	public boolean isWithDateUpdate()
	{
		return isWithDateUpdate;
	}
	
	public List< String > getDateFields()
	{
		return dateFields;
	}
	
	public String getDateValue()
	{
		return dateValue;
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
	
	public void setWithDateUpdate( boolean isWithDateUpdate )
	{
		this.isWithDateUpdate = isWithDateUpdate;
	}
	
	public void setDateFields( List< String > dateFields )
	{
		this.dateFields = new ArrayList< String >( dateFields );
	}
	
	public void setDateValue( String dateValue )
	{
		StringBuffer aux = new StringBuffer();
		
		if( dateValue != null )
		{
			aux.append( dateValue );
			
			if( !dateValue.equalsIgnoreCase( GenericDaoUtils.SYSDATE ) &&
				!dateValue.equalsIgnoreCase( GenericDaoUtils.NOW ) )
			{
				if( !dateValue.startsWith( "'" ) )
					aux.insert( 0, "'" );
				if( !dateValue.endsWith( "'" ) )
					aux.append( "'" );
			}
		}
		
		this.dateValue = String.valueOf( aux );
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
				  ( getFields() != null && !getFields().isEmpty() ) &&
				  ( ( isWithDateUpdate() &&
					( ( getDateFields() != null && !getDateFields().isEmpty() ) ) ) ) &&
				  ( getFieldToFind() != null && !getFieldToFind().isEmpty() ) &&
				  ( getDataList() 	 != null && !getDataList().isEmpty() );
		
		return isValid;
	}
}