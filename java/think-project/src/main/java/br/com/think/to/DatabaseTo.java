package br.com.think.to;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTo
{
	protected Connection		connection;
	protected String			owner;
	protected String			table;
	protected List< String >	fields;
	protected Class< ? >		to;
	protected String			maskForDate;
	protected boolean			usePlaceHolder;
	
	protected DatabaseTo( Connection connection, Class< ? > to, boolean usePlaceHolder )
	{
		setConnection( connection );
		setTo( to );
		setUsePlaceHolder( usePlaceHolder );
	}
	
	protected DatabaseTo( Connection connection, String owner, String table, List< String > fields,
	        Class< ? > transferObject, boolean usePlaceHolder )
	{
		setConnection( connection );
		setOwner( owner );
		setTable( table );
		setFields( fields );
		setTo( transferObject );
		setUsePlaceHolder( usePlaceHolder );
	}
	
	public Connection getConnection()
	{
		return connection;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public String getTable()
	{
		return table;
	}
	
	public List< String > getFields()
	{
		return fields;
	}
	
	public Class< ? > getTo()
	{
		return to;
	}
	
	public void setConnection( Connection connection )
	{
		this.connection = connection;
	}
	
	public void setOwner( String owner )
	{
		this.owner = owner;
	}
	
	public void setTable( String table )
	{
		this.table = table;
	}
	
	public void setFields( List< String > fields )
	{
		this.fields = new ArrayList< String >( fields );
	}
	
	public void setTo( Class< ? > transferObject )
	{
		this.to = transferObject;
	}
	
	public String getMaskForDate()
	{
		return maskForDate;
	}
	
	public void setMaskForDate( String maskForDate )
	{
		this.maskForDate = maskForDate;
	}
	
	public boolean getUsePlaceHolder()
	{
		return usePlaceHolder;
	}
	
	public void setUsePlaceHolder( boolean usePlaceHolder )
	{
		this.usePlaceHolder = usePlaceHolder;
	}
	
	protected boolean isValid()
	{
		return connection != null && to != null && ( table != null && !table.isEmpty() );
	}
}