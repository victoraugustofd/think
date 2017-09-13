package br.com.think.utils;

import br.com.think.to.DatabaseTo;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GenericDaoUtils
{
	private final static Logger logger = Logger.getLogger( GenericDaoUtils.class );

	public final static String SELECT = "SELECT";
	public final static String FROM = "FROM";
	public final static String DUAL = "DUAL";
	public final static String FROM_DUAL = FROM + " " + DUAL;
	public final static String COUNT = SELECT + " COUNT(*) AS TOTAL FROM (";
	public final static String INSERT = "INSERT";
	public final static String ALL = "ALL";
	public final static String INSERT_ALL = INSERT + " " + ALL;
	public final static String INTO = "INTO";
	public final static String INSERT_INTO = INSERT + " " + INTO;
	public final static String VALUES = "VALUES";
	public final static String SELECT_FROM_DUAL = "SELECT * FROM DUAL";
	public final static String UNION = "UNION";
	public final static String UNION_ALL = UNION + " " + ALL;
	public final static String UPDATE = "UPDATE";
	public final static String SET = "SET";
	public final static String WHERE = "WHERE";
	public final static String IN = "IN (";
	public final static String SYSDATE = "SYSDATE";
	public final static String NOW = "NOW()";
	public final static String DELETE = "DELETE";

	public static String putLeftParenthesis( String query )
	{
		StringUtils.stripEnd( query, " " );
		String aux;

		if( query.endsWith( "(" ))
			aux = query;
		else
			aux = query + "(";
		
		return aux;
	}
	
	public static String putPlaceHoldersOnQuery( int quantity )
	{
		String aux;

		aux = StringUtils.repeat( "?", ",", quantity );
		
		return aux;
	}
	
	public static String putInfoOnQuery( List< String > dataList, boolean isString )
	{
		StringBuilder query = new StringBuilder();
		List< String > dataListAux = new ArrayList<>( dataList );
		Object[] objectData;
		
		if( isString )
		{
			String aux;
			int count = 0;

			for ( String data : dataListAux )
			{
				aux = "'" + data + "'";

				dataListAux.set( count, aux );

				count++;
			}
		}

		objectData = dataListAux.toArray();
		String auxString = Arrays.toString( objectData );

		auxString = auxString.substring( 1, auxString.length() - 1 );

		query.append( auxString );

		dataListAux.clear();

		return String.valueOf( query );
	}
	
	public static String putInfoFromListOnQuery( Object[] dataList )
	{
		StringBuilder query = new StringBuilder();
		String aux;
		Object[] objectData = new Object[ dataList.length ];
		
		for ( int i = 0; i < dataList.length; i++ )
		{
			if( dataList[ i ] instanceof String )
			{
				aux = "'" + String.valueOf( dataList[ i ] ) + "'";
				objectData[ i ] = aux;
			}
			else if( dataList[ i ] instanceof Integer )
				objectData[ i ] = ( int ) dataList[ i ];
			else if( dataList[ i ] instanceof Double )
				objectData[ i ] = ( double ) dataList[ i ];
			else if( dataList[ i ] instanceof Long )
				objectData[ i ] = ( long ) dataList[ i ];
			else if( dataList[ i ] instanceof Timestamp )
			{
				aux = "TO_DATE('" + dataList[ i ];
				aux = aux.substring( 0, aux.length() - 2 );
				aux += "', 'YYYY-MM-DD HH24:MI:SS')";
				objectData[ i ] = aux;
			}
			else
				objectData[ i ] = dataList[ i ];
		}

		aux = Arrays.toString( objectData );

		aux = aux.substring( 1, aux.length() - 1 );

		query.append( aux );

		return String.valueOf( query );
	}
	
	public static < T > String putInfoFromListOnQueryUsingReflection( String databaseName, String maskForDate, List< String > fields, T transferObject )
	{
		String result = "";
		String methodReturn;
		String dateFunction;
		String mask = maskForDate;
		StringBuilder getterName = new StringBuilder();
		StringBuilder valueAux = new StringBuilder();
		Class< ? > cls;
		Class< ? > returnType;
		Map< String, Method > getters;
		Method method;
		List< String > fieldsData = new ArrayList<>();

		if( transferObject != null )
		{
			try
			{
				if( databaseName.equalsIgnoreCase( "MySQL" ) )
					dateFunction = "STR_TO_DATE";
				else
					dateFunction = "TO_DATE";

				if( mask == null || mask.isEmpty() )
					mask = "YYYY-MM-DD HH24:MI:SS";

				cls = transferObject.getClass();
				getters = ReflectionUtils.getGetterMethods( cls );
				
				for ( String field : fields )
				{
					getterName.append( field.toLowerCase() );
					getterName.insert( 0, "get" );
					
					method = getters.get( String.valueOf( getterName ) );
					returnType = method.getReturnType();
					
					methodReturn = String.valueOf( method.invoke( transferObject ) );
					valueAux.append( methodReturn );
					
					if( !String.valueOf( valueAux ).toLowerCase().equals( "null" ) && methodReturn != null )
					{
						if( returnType.equals( String.class ) )
						{
							valueAux.insert( 0, "'" );
							valueAux.append( "'" );
						}
						else if( returnType.equals( Timestamp.class ) || returnType.equals( Date.class ) || returnType.equals( java.util.Date.class ) )
						{
							if( String.valueOf( valueAux ).contains( "." ) )
							{
								String dateAux = valueAux.substring( 0, valueAux.length() - 2 );
								
								valueAux.delete( 0, valueAux.length() );
								
								valueAux.append( dateAux );
							}
							
							valueAux.insert( 0, dateFunction + "('" );
							valueAux.append( "', '" );
							valueAux.append( mask );
							valueAux.append( "')" );
						}
					}
					
					fieldsData.add( String.valueOf( valueAux ) );
					
					valueAux.setLength( 0 );
					getterName.setLength( 0 );
				}
				
				result = String.join( ",", fieldsData );
			}
			catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static String getQueryEndRank( byte rank )
	{
		StringBuilder query = new StringBuilder();
		
		query.append( ") A " );
		query.append( "WHERE A.RANK = " );
		query.append( rank );

		return String.valueOf( query );
	}

	/**
	 * To use this method...
	 */
	public static < T > void prepareStatement( PreparedStatement preparedStatement, Integer continueParameterFrom, Integer numberOfIterations, Integer offset, List< String > fields, List< T > dataList )
	{
		int preparedStatementParameter = 1;

		if( continueParameterFrom != null && continueParameterFrom != 0 )
			preparedStatementParameter = continueParameterFrom;

		Map< String, Method > getters = null;

		try
		{
			if ( fields != null && !fields.isEmpty() )
			{
				getters = ReflectionUtils.getGetterMethods( dataList.get( 0 ).getClass() );
				getters.keySet().forEach( String::toLowerCase );
			}

			for ( T object : dataList )
			{
				if ( fields != null && !fields.isEmpty() )
				{
					for ( String field : fields )
					{
						setPreparedStatementAttribute( preparedStatement, preparedStatementParameter++, getters.get( "get" + field ).invoke( object ) );
					}
				}
				else
				{
					if ( numberOfIterations == null || numberOfIterations <= 0 )
						numberOfIterations = 1;

					for ( int i = 0; i < numberOfIterations; i++ )
						setPreparedStatementAttribute( preparedStatement, preparedStatementParameter + ( i * offset ),
								object );

					preparedStatementParameter++;
				}
			}
		}
		catch ( IllegalAccessException | InvocationTargetException e )
		{
			logger.error( e.getMessage() );
		}
	}

	public static < T > void setPreparedStatementAttribute( PreparedStatement preparedStatement, int preparedStatementParameter, T object )
	{
		Type type = object.getClass();

		try
		{
			if ( type.equals( Boolean.TYPE ) || type.equals( Boolean.class ) )
			{
				preparedStatement.setBoolean( preparedStatementParameter, ( Boolean ) object );
			}
			else if ( ( type.equals( Byte.TYPE ) || type.equals( Byte.class ) ) )
			{
				preparedStatement.setByte( preparedStatementParameter, ( Byte ) object );
			}
			else if ( type.equals( Short.TYPE ) || type.equals( Short.class ) )
			{
				preparedStatement.setShort( preparedStatementParameter, ( Short ) object );
			}
			else if ( type.equals( Integer.TYPE ) || type.equals( Integer.class ) )
			{
				preparedStatement.setInt( preparedStatementParameter, ( Integer ) object );
			}
			else if ( type.equals( Long.TYPE ) || type.equals( Long.class ) )
			{
				preparedStatement.setLong( preparedStatementParameter, ( Long ) object );
			}
			else if ( type.equals( Double.TYPE ) || type.equals( Double.class ) )
			{
				preparedStatement.setDouble( preparedStatementParameter, ( Double ) object );
			}
			else if ( type.equals( String.class ) || ( type.equals( Character.TYPE ) || type.equals( Character.class ) ) )
			{
				preparedStatement.setString( preparedStatementParameter, String.valueOf( object ) );
			}
			else if ( type.equals( Timestamp.class ) )
			{
				preparedStatement.setTimestamp( preparedStatementParameter, ( Timestamp ) object );
			}
			else if ( type.equals( Date.class ) )
			{
				preparedStatement.setDate( preparedStatementParameter, ( Date ) object );
			}
			else if ( type.equals( java.util.Date.class ) )
			{
				preparedStatement.setDate( preparedStatementParameter, new Date( ( ( java.util.Date ) object ).getTime() ) );
			}
			else if ( type.equals( Blob.class ) )
			{
				/*Blob blob = dbTO.getConnection().createBlob();
				blob.setBytes( 1, Serializer.serialize( object ) );*/

				preparedStatement.setBlob( preparedStatementParameter, ( Blob ) object );
			}
			else if ( type.equals( Clob.class ) )
			{
				/*Clob clob = dbTO.getConnection().createClob();
				clob.setString( 1, String.valueOf( object ) );*/

				preparedStatement.setClob( preparedStatementParameter, ( Clob ) object );
			}
			else
			{
				preparedStatement.setObject( preparedStatementParameter, object );
			}
		}
		catch ( SQLException e )
		{
			logger.error( e.getMessage() );
		}
	}

	public static boolean validateIndex( Connection connection, String owner, String tableName, String indexName )
	{
		boolean isIndexValid = false;
		ResultSet resultSet;

		try
		{
			DatabaseMetaData dbmd = connection.getMetaData();
			resultSet = dbmd.getPrimaryKeys( null, owner, tableName );

			while( resultSet.next() && !isIndexValid )
			{
				if( resultSet.getString( "COLUMN_NAME" ).equalsIgnoreCase( indexName ) )
					isIndexValid = true;
			}

			if( !isIndexValid )
			{
				resultSet = dbmd.getIndexInfo( null, owner, tableName, true, false );

				while( resultSet.next() && !isIndexValid )
				{
					if( resultSet.getString( "COLUMN_NAME" ).equalsIgnoreCase( indexName ) )
						isIndexValid = true;
				}
			}
		}
		catch ( SQLException e )
		{
			logger.error( e.getMessage() );
		}

		return isIndexValid;
	}

	@SuppressWarnings( "unchecked" )
	public static < T > Multimap< String, T > getMultiMap( String databaseName, DatabaseTo dbTO, Map< String, Method > getters, List< String > fields, String fieldToFind, List< T > dataList )
	{
		Multimap< String, T > multiMapData = ArrayListMultimap.create();
		String key;
		StringBuffer getterName = new StringBuffer();
		T value;

		try
		{
			for ( T object : dataList )
			{
				key = GenericDaoUtils
						.putInfoFromListOnQueryUsingReflection( databaseName, dbTO.getMaskForDate(), fields, object );
				key = key.replace( " ", "" );

				getterName.append( fieldToFind.toLowerCase() );
				getterName.insert( 0, "get" );

				value = ( T ) getters.get( String.valueOf( getterName ) ).invoke( object );

				multiMapData.put( key, value );

				getterName.setLength( 0 );
			}
		}
		catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
		{
			logger.error( e.getMessage() );
		}

		return sortedByAscendingFrequencyUsingReflection( multiMapData );
	}

	/*
	 * Code extracted from http://stackoverflow.com/questions/7881629/sort-guava-multimap-by-number-of-values
	 */
	private static < T > Multimap< String, T > sortedByAscendingFrequencyUsingReflection( Multimap< String, T > multimap )
	{
		return ImmutableMultimap.< String, T >builder()
				.orderKeysBy( ascendingCountOrdering( multimap.keys() ) )
				.putAll( multimap )
				.build();
	}

	private static Ordering< String > ascendingCountOrdering( final Multiset< String > multiset )
	{
		return new Ordering< String >()
		{
			@Override
			public int compare( String left, String right )
			{
				return Ints.compare( multiset.count( right ), multiset.count( left ) );
			}
		};
	}

	public static < T > void fillTransferObject( T transferObject, ResultSet resultSet )
	{
		Map< String, Method > getters = ReflectionUtils.getGetterMethods( transferObject.getClass() );
		Map< String, Method > setters = ReflectionUtils.getSetterMethods( transferObject.getClass() );

		String columnName;
		String setterName;

		StringBuilder getterName = new StringBuilder();

		Class< ? > returnType;

		ResultSetMetaData rsmd;

		try
		{
			rsmd = resultSet.getMetaData();

			for ( int i = 0; i < rsmd.getColumnCount(); i++ )
			{
				columnName = rsmd.getColumnName( i + 1 ).toLowerCase();

				getterName.append( columnName );
				getterName.insert( 0, "get" );

				setterName = getterName.toString().replace( "get", "set" );

				returnType = getters.get( getterName.toString() ).getReturnType();

				if ( returnType.equals( Boolean.TYPE ) || returnType.equals( Boolean.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getBoolean( i + 1 ) );
				}
				else if ( ( returnType.equals( Byte.TYPE ) || returnType.equals( Byte.class ) ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getByte( i + 1 ) );
				}
				else if ( returnType.equals( Short.TYPE ) || returnType.equals( Short.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getShort( i + 1 ) );
				}
				else if ( returnType.equals( Integer.TYPE ) || returnType.equals( Integer.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getInt( i + 1 ) );
				}
				else if ( returnType.equals( Long.TYPE ) || returnType.equals( Long.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getLong( i + 1 ) );
				}
				else if ( returnType.equals( Double.TYPE ) || returnType.equals( Double.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getDouble( i + 1 ) );
				}
				else if ( returnType.equals( String.class ) || ( returnType.equals( Character.TYPE ) || returnType
						.equals( Character.class ) ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getString( i + 1 ) );
				}
				else if ( returnType.equals( Timestamp.class ) || returnType.equals( Date.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getTimestamp( i + 1 ) );
				}
				else if ( returnType.equals( Blob.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getBlob( i + 1 ) );
				}
				else if ( returnType.equals( Clob.class ) )
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getClob( i + 1 ) );
				}
				else
				{
					setters.get( setterName ).invoke( transferObject, resultSet.getObject( i + 1 ) );
				}

				getterName.setLength( 0 );
			}
		} catch ( SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
		{
			logger.error( e.getMessage() );
		}
	}

	public static List< String > validateQueries( List< String > queries )
	{
		List< String > validatedQueriesList = queries
				.subList( 0, queries.size() - 1 )
				.stream()
				.map( GenericDaoUtils::putLeftParenthesis )
				.collect( Collectors.toList() );

		validatedQueriesList.add( queries.get( queries.size() - 1 ) );

		return validatedQueriesList;
	}
}