package br.com.think.dao.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import br.com.think.dao.IGenericDao;
import br.com.think.file.WriteFiles;
import br.com.think.to.DeleteTo;
import br.com.think.to.InsertTo;
import br.com.think.to.SelectTo;
import br.com.think.to.UpdateTo;
import br.com.think.utils.GenericDaoUtils;
import br.com.think.utils.ReflectionUtils;

public abstract class GenericDao implements IGenericDao
{
	protected Connection        connection;
	protected PreparedStatement pstmt;
	protected ResultSet         rs;
	protected StringBuffer      query;

	private final static Logger logger = Logger.getLogger( GenericDao.class );
	
	@Override
	public < T > List< T > selectAll( SelectTo selectTO, String message )
	{
		return selectAll( selectTO, message, null );
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	public < T > List< T > selectAll( SelectTo selectTO, String message, String filePath )
	{
		List< T > dataReturn = null;
		List< String > dataList;
		List< String > queries;
		Class< ? > cls;
		Object obj;
		
		boolean isQuerySeparated = selectTO.isQuerySeparated();
		boolean usePlaceHolder = selectTO.getUsePlaceHolder();
		boolean continueSelect = true;
		
		int listSize;
		int numberOfIterations;
		int numberOfItems;
		int divisionFactor;
		int begin;
		int end;

		long registerCount;
		long found;
		
		if ( selectTO.isValid() )
		{
			try
			{
				logger.info( message + " - Begin extraction" );
				
				dataReturn = new ArrayList<>();
				dataList = selectTO.getDataList();
				
				pstmt = null;
				rs = null;
				query = new StringBuffer();
				
				cls = selectTO.getTo();
				
				queries = GenericDaoUtils.validateQueries( selectTO.getQueries() );

				isQuerySeparated = selectTO.isQuerySeparated();
				usePlaceHolder = selectTO.getUsePlaceHolder();
				continueSelect = true;
				
				listSize = dataList.size();
				divisionFactor = 1;
				begin = 0;

				registerCount = 0;
				found = 0;

				if( usePlaceHolder )
					divisionFactor = queries.size();

				numberOfIterations = listSize / ( 1000 / divisionFactor );

				for ( int i = 0; i <= numberOfIterations && continueSelect; i++ )
				{
					if ( i == numberOfIterations )
						numberOfItems = listSize - ( numberOfIterations * ( 1000 / divisionFactor ) );
					else
						numberOfItems = 1000 / divisionFactor;

					end = begin + numberOfItems;
					registerCount += numberOfItems;

					for ( int j = 0; ( !isQuerySeparated && j < queries.size() ) ||
									 (  isQuerySeparated && j < queries.size() - 1 ); j++ )
					{
						query.append( queries.get( j ) );

						if( usePlaceHolder )
							query.append( GenericDaoUtils.putPlaceHoldersOnQuery( numberOfItems ) );
						else
							query.append( GenericDaoUtils.putInfoOnQuery( dataList.subList( begin, end ), selectTO.isString() ) );

						query.append( ")" );
					}

					if( isQuerySeparated )
						query.append( queries.get( queries.size() - 1 ) ); 

					pstmt = selectTO.getConnection().prepareStatement( String.valueOf( query ) );

					if( usePlaceHolder )
					{
						GenericDaoUtils
								.prepareStatement( pstmt, null, queries.size(), numberOfItems, null, dataList.subList( begin, end ) );
					}

					rs = pstmt.executeQuery();

					while ( rs.next() )
					{
						obj = cls.newInstance();

						GenericDaoUtils.fillTransferObject( obj, rs );

						dataReturn.add( ( T ) obj );
					}
					
					if( null != filePath )
					{
						WriteFiles.write(filePath, dataReturn.stream().map(Object::toString).collect(Collectors.toList()), true);
						
						found += dataReturn.size();
						dataReturn.clear();
					}
					else
					{
						found = dataReturn.size();
					}
					
					closePreparedStatement();
					closeResultSet();

					begin = end;

					query.setLength( 0 );

					logger.info( message + " - Searched " + registerCount + " of "
					        + listSize + " - Found so far: " + found );

					if ( listSize - registerCount == 0 )
						continueSelect = false;
				}

				logger.info( message + " - Finish extraction" );
			}
			catch ( SQLException | InstantiationException | IllegalAccessException | IOException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
			finally
			{
				closeResources();
			}
		}
		else
			logger.error( message + " - SelectTO doesn't have enough info to execute the process" );
		
		return dataReturn;
	}

	@Override
	public < T > boolean insertAll( InsertTo< T > insertTO, String message )
	{
		boolean success = false;
		
		if ( insertTO.isValid() )
		{
			logger.info( message + " - Begin insert" );
			
			pstmt = null;
			query = new StringBuffer();

			StringBuilder queryAux = new StringBuilder();

			String owner = insertTO.getOwner();
			String tableName = insertTO.getTable();
			List< String > fields = insertTO.getFields();
			List< T > dataList = insertTO.getDataList();

			String databaseName;
			
			boolean continueInsert = true;
			boolean usePlaceHolder = insertTO.getUsePlaceHolder();
			
			int numberOfInserts = 1000;
			
			int listSize = dataList.size();
			int numberOfIterations;
			int numberOfItems;
			int divisionFactor = 1;
			int begin = 0;
			int end;
			
			long registerCount = 0;

			if( usePlaceHolder )
				divisionFactor = fields.size();

			numberOfIterations = listSize / ( numberOfInserts / divisionFactor );
			
			try
			{
				databaseName = insertTO.getConnection().getMetaData().getDatabaseProductName();

				query.append( GenericDaoUtils.INSERT_INTO + " " );

				if ( owner != null && !owner.isEmpty() )
				{
					query.append( owner );
					query.append( "." );
				}

				query.append( tableName );
				query.append( "(" );

				query.append( String.join( ",", fields ) );
				query.append( ")" );
				
				for ( int i = 0; i <= numberOfIterations && continueInsert; i++ )
				{
					if ( i == numberOfIterations )
						numberOfItems = listSize - ( numberOfIterations * ( numberOfInserts / divisionFactor ) );
					else
						numberOfItems = numberOfInserts / divisionFactor;
					
					end = begin + numberOfItems;
					registerCount += numberOfItems;
					
					queryAux.append( query );

					if( usePlaceHolder )
					{
						queryAux.append( StringUtils.repeat( " " + GenericDaoUtils.SELECT + " " + GenericDaoUtils.putPlaceHoldersOnQuery( fields.size() ),
															 " " + GenericDaoUtils.FROM_DUAL + " " + GenericDaoUtils.UNION_ALL,
															numberOfItems ) );
					}
					else
					{
						for ( int j = begin; j < end; j++ )
						{
							queryAux.append( " " + GenericDaoUtils.SELECT + " " );
							queryAux.append( GenericDaoUtils
									.putInfoFromListOnQueryUsingReflection( databaseName, insertTO.getMaskForDate(),
											fields, dataList.get( j ) ) );
							queryAux.append( " " + GenericDaoUtils.FROM_DUAL );
							queryAux.append( " " + GenericDaoUtils.UNION_ALL );
						}
					}

					queryAux.delete( queryAux.length() - GenericDaoUtils.UNION_ALL.length(), queryAux.length() );
					
					pstmt = insertTO.getConnection().prepareStatement( String.valueOf( queryAux ) );

					if( usePlaceHolder )
					{
						GenericDaoUtils
								.prepareStatement( pstmt, null, null, null, fields, dataList.subList( begin, end ) );
					}

					pstmt.executeUpdate();
					
					closePreparedStatement();
					
					begin = end;
					
					queryAux.setLength( 0 );
					
					logger.info( message + " - Inserted so far: " + registerCount + " of "
					        + listSize );
					
					if ( listSize - registerCount == 0 )
						continueInsert = false;
				}

				success = true;
				logger.info( message + " - Finish insert" );
			}
			catch ( SQLException e )
			{
				logger.error( e.getMessage() );
                throw new RuntimeException(e.getMessage());
			}
			finally
			{
				closeResources();
			}
		}
		else
			logger.error( message + " - InsertTO doesn't have enough info to execute the process" );
		
		return success;
	}
	
	public < T > boolean insertAllWithSelect( InsertTo< T > insertTO, SelectTo selectTO, String message )
	{
		boolean success = false;
		
		if ( insertTO.isValid() )
		{
			if( selectTO.isValid() )
			{
				try
				{
					logger.info( message + " - Begin insert" );
					
					List< T > dataList = null;
					List< String > dataListString = null;
					
					Class< ? > cls;
					
					Map< String, Method > getters;
					
					String getterFieldToFindName;
					Method getterFieldToFind = null;
					
					if( selectTO.getDataList() != null )
					{
						dataListString = new ArrayList<>( selectTO.getDataList() );
					}
					else
					{
						dataList = new ArrayList<>( insertTO.getDataList() );
						
						cls = selectTO.getTo();
						
						getters = ReflectionUtils.getGetterMethods( cls );
						
						getterFieldToFindName = "get" + selectTO.getFieldToFind().toLowerCase();
						getterFieldToFind = getters.get( getterFieldToFindName );
					}
					
					boolean continueProcessing = true;
					
					int listSize;
					
					if( dataList != null )
						listSize = dataList.size();
					else
						listSize = dataListString.size();
					
					int numberOfItemsToSearch = 1000;
					int numberOfIterations = listSize / numberOfItemsToSearch;
					int numberOfParts = listSize % numberOfItemsToSearch == 0 ? numberOfIterations : numberOfIterations + 1;
					int numberOfItems;
					int begin = 0;
					int end;
					int registerCount = 0;
					
					for ( int i = 0; i <= numberOfIterations && continueProcessing; i++ )
					{
						if ( i == numberOfIterations )
							numberOfItems = listSize - ( numberOfIterations * numberOfItemsToSearch );
						else
							numberOfItems = numberOfItemsToSearch;
						
						end = begin + numberOfItems;
						registerCount += numberOfItems;
						
						List< String > dividedDataList;
						
						if( dataList != null )
						{
							final Method method = getterFieldToFind;
							
							dividedDataList = dataList.subList( begin, end )
												.stream()
													.map( object ->{
																		try
																		{
																			return ( object != null
																					? String.valueOf( method.invoke( object ) )
																					: null );
																		}
																		catch ( IllegalAccessException 	 |
																				IllegalArgumentException |
																				InvocationTargetException e )
																		{
																			logger.error( e.getMessage() );
																			return null;
																		}
																	} )
														.collect( Collectors.toList() );
							
							// Code below is for Java 7 or less
							/*List< String > dividedDataList = new ArrayList< String >();
							
							for ( T object : dataList.subList( begin, end ) )
							{
								dividedDataList.add( object != null ? String.valueOf( getterFieldToFind.invoke( object ) ) : null );
							}*/
						}
						else
						{
							dividedDataList = dataListString.subList( begin, end );
						}
						
						logger.info( message + " - Processing part " + ( i + 1 ) + " of "
						        + numberOfParts );
						
						selectTO.setDataList( dividedDataList );
						
						List< T > dataToInsert = selectAll( selectTO, message );
						
						dividedDataList.clear();
						
						insertTO.setDataList( dataToInsert );
						insertAll( insertTO, message );
						
						logger.info( message + " - Finish part " + ( i + 1 ) + " of " + numberOfParts );
						
						selectTO.getDataList().clear();
						insertTO.getDataList().clear();
						
						begin = end;
						
						if ( listSize - registerCount == 0 )
							continueProcessing = false;
					}
					
					success = true;
					
					logger.info( message + " - Finish Insert with Select" );
				}
				catch ( IllegalArgumentException e )
				{
					logger.error( e.getMessage() );
					throw new RuntimeException(e.getMessage());
				}
				finally
				{
					closeResources();
				}
			}
			else
				logger.error( message + " - SelectTO doesn't have enough info to execute the process" );
		}
		else
			logger.error( message + " - InsertTO doesn't have enough info to execute the process" );
		
		return success;
	}

	@Override
	public < T > boolean updateAll( UpdateTo< T > updateTO, String message )
	{
		boolean success = false;
		boolean continueUpdate;
		boolean usePlaceHolder;
		
		StringBuilder queryAux;
		
		String query;
		String owner;
		String tableName;
		String fieldToFind;
		String aux;
		String databaseName;
		String[] fieldsValues;
		
		Class< ? > cls;
		
		Map< String, Method > getters;
		Multimap< String, T > multiMapData;
		
		Set< String > keys;
		
		List< String > fields;
		List< String > dateFields;
		List< String > listSet;
		List< String > values;
		List< T > objectValues;
		
		int indexGroup;
		int listSize;
		int numberOfIterations;
		int numberOfItems;
		int divisionFactor;
		int begin;
		int end;
		int preparedStatementParameter;
		
		long registerCount;
		long totalRegisterCount;
		
		if( updateTO.isValid() )
		{
			totalRegisterCount = 0;
			
			try
			{
				if( GenericDaoUtils.validateIndex( updateTO.getConnection(), updateTO.getOwner(), updateTO.getTable(), updateTO.getFieldToFind() ) )
				{
					owner = updateTO.getOwner();
					tableName = updateTO.getTable();

					fields = updateTO.getFields();
					dateFields = updateTO.getDateFields();
					listSet = new ArrayList<>();
					fieldToFind = updateTO.getFieldToFind();

					databaseName = updateTO.getConnection().getMetaData().getDatabaseProductName();

					cls = updateTO.getTo();

					getters = ReflectionUtils.getGetterMethods( cls );

					usePlaceHolder = updateTO.getUsePlaceHolder();
					divisionFactor = fields.size() + ( dateFields != null && !dateFields.isEmpty() ? dateFields.size() : 0 );

					multiMapData = GenericDaoUtils
							.getMultiMap( databaseName, updateTO, getters, fields, fieldToFind, updateTO.getDataList() );

					if ( multiMapData != null && !multiMapData.isEmpty() )
					{
						logger.info( message + " - Begin update" );

						pstmt = null;
						queryAux = new StringBuilder();

						keys = multiMapData.keySet();

						indexGroup = 0;

						updateTO.getDataList().clear();

						logger.info( message + " - The following update has " + keys.size() + " group(s) of update:" );

						for ( String key : keys )
						{
							fieldsValues = key.split( "," );

							aux = message + " - Group " + ++indexGroup + ": " + multiMapData.get( key ).size() + " element(s) - ";

							for ( int i = 0; i < fields.size(); i++ )
								aux += fields.get( i ) + ": " + fieldsValues[ i ] + ( i != fields.size() - 1 ?
										" / " :
										"" );

							logger.info( aux );
						}

						indexGroup = 0;

						for ( String key : keys )
						{
							logger.info( message + " - Processing group " + ++indexGroup );

							fieldsValues = key.split( "," );

							objectValues = ( List< T > ) multiMapData.get( key );

							values = objectValues.stream().map( object -> ( object != null ? object.toString() : null ) )
									.collect( Collectors.toList() );

							continueUpdate = true;

							listSize = values.size();
							numberOfIterations = listSize / ( 1000 - divisionFactor );
							begin = 0;

							registerCount = 0;

							query = GenericDaoUtils.UPDATE + " ";

							if ( owner != null && !owner.isEmpty() )
								query += owner + ".";

							query += tableName + " ";
							query += GenericDaoUtils.SET;

							for ( int i = 0; i < fields.size(); i++ )
								listSet.add( " " + fields.get( i ) + " = " + ( usePlaceHolder ? " ? " : fieldsValues[ i ].trim() ) );

							listSet.clear();

							query += String.join( ",", listSet );

							if ( updateTO.isWithDateUpdate() )
							{
								query += ", ";

								for ( String dateField : dateFields )
									listSet.add( dateField + " = " +
											( usePlaceHolder ?
													" ? " :
													( updateTO.getDateValue() != null && !updateTO.getDateValue().isEmpty() ?
															updateTO.getDateValue() :
															"null" ) ) );

								query += String.join( ",", listSet );
							}

							listSet.clear();

							query += " " + GenericDaoUtils.WHERE + " " + updateTO.getFieldToFind() + " " + GenericDaoUtils.IN;

							for ( int i = 0; i <= numberOfIterations && continueUpdate; i++ )
							{
								if ( i == numberOfIterations )
									numberOfItems = listSize - ( numberOfIterations * ( 1000 - divisionFactor ) );
								else
									numberOfItems = 1000 - divisionFactor;

								end = begin + numberOfItems;
								registerCount += numberOfItems;

								queryAux.append( GenericDaoUtils.putLeftParenthesis( query ) );

								if( usePlaceHolder )
									queryAux.append( GenericDaoUtils.putPlaceHoldersOnQuery( numberOfItems ) );
								else
									queryAux.append( GenericDaoUtils.putInfoOnQuery( values.subList( begin, end ), updateTO.isString() ) );

								queryAux.append( ")" );

								pstmt = updateTO.getConnection().prepareStatement( String.valueOf( queryAux ) );

								if( usePlaceHolder )
								{
									preparedStatementParameter = 1;

									for( String field : fields )
									{
										T object = ( ( List< T > ) multiMapData.get( key ) ).get( 0 );

										GenericDaoUtils.setPreparedStatementAttribute( pstmt, preparedStatementParameter++, getters.get( "get" + field ).invoke( object ) );
									}

									for( int j = 0; j < dateFields.size(); j++ )
									{
										GenericDaoUtils.setPreparedStatementAttribute( pstmt, preparedStatementParameter++, updateTO.getDateValue() != null && !updateTO.getDateValue().isEmpty() ? updateTO.getDateValue() : null );
									}

									GenericDaoUtils.prepareStatement( pstmt, divisionFactor + 1, null, null, null, values.subList( begin, end ) );
								}

								pstmt.executeUpdate();

								closePreparedStatement();

								begin = end;

								queryAux.setLength( 0 );

								logger.info( message + " - Group " + indexGroup + " - Updated so far: " + registerCount
										+ " of " + listSize );

								if ( listSize - registerCount == 0 )
									continueUpdate = false;
							}

							totalRegisterCount = totalRegisterCount + registerCount;
						}
					}

					success = true;
					logger.info( message + " - Finish update ( " + totalRegisterCount + " register(s) updated )" );
				}
				else
				{
					logger.error( message + " - It is not recommended to use this method to update data without an index" );
				}
			}
			catch( IllegalAccessException | InvocationTargetException | IllegalArgumentException | SQLException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
			finally
			{
				closeResources();
			}
		}
		else
			logger.error( message + " - UpdateTO doesn't have enough info to execute the process" );
		
		return success;
	}
	
	@Override
	public < T > boolean deleteAll( DeleteTo< T > deleteTO, String message )
	{
		boolean success = false;
		boolean continueDelete;
		boolean usePlaceHolder;
		
		StringBuilder queryAux;
		
		String query;
		String owner;
		String tableName;
		String fieldToFind;
		String getterName;
		
		Class< ? > cls;
		
		Map< String, Method > getters;
		
		List< String > values;
		List< T > list;
		
		int listSize;
		int numberOfIterations;
		int numberOfItems;
		int begin = 0;
		int end;
		
		long registerCount;
		long totalRegisterCount;
		
		if( deleteTO.isValid() )
		{
			totalRegisterCount = 0;
			
			try
			{
				owner = deleteTO.getOwner();
				tableName = deleteTO.getTable();
				
				fieldToFind = deleteTO.getFieldToFind();

				if( GenericDaoUtils.validateIndex( deleteTO.getConnection(), owner, tableName, fieldToFind ) )
				{
					usePlaceHolder = deleteTO.getUsePlaceHolder();

					list = deleteTO.getDataList();

					cls = deleteTO.getTo();

					getters = ReflectionUtils.getGetterMethods( cls );
					getterName = "get" + fieldToFind.toLowerCase();

					values = list.stream().map( object ->
					{
						try
						{
							return String.valueOf( getters.get( getterName ).invoke( object ) );
						}
						catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
						{
							logger.error( e.getMessage() );
							throw new RuntimeException(e.getMessage());
						}
					} ).collect( Collectors.toList() );

					logger.info( message + " - Begin delete" );

					pstmt = null;
					queryAux = new StringBuilder();

					continueDelete = true;

					listSize = values.size();
					numberOfIterations = listSize / 1000;

					registerCount = 0;

					query = GenericDaoUtils.DELETE + " " + GenericDaoUtils.FROM + " ";

					if ( owner != null && !owner.isEmpty() )
						query += owner + ".";

					query += tableName + " ";

					query += GenericDaoUtils.WHERE + " " + fieldToFind + " " + GenericDaoUtils.IN;

					for ( int i = 0; i <= numberOfIterations && continueDelete; i++ )
					{
						if ( i == numberOfIterations )
							numberOfItems = listSize - ( numberOfIterations * 1000 );
						else
							numberOfItems = 1000;

						end = begin + numberOfItems;
						registerCount += numberOfItems;

						queryAux.append( GenericDaoUtils.putLeftParenthesis( query ) );

						if( usePlaceHolder )
							queryAux.append( GenericDaoUtils.putPlaceHoldersOnQuery( numberOfItems ) );
						else
							queryAux.append( GenericDaoUtils.putInfoOnQuery( values.subList( begin, end ), deleteTO.isString() ) );

						queryAux.append( ")" );

						pstmt = deleteTO.getConnection().prepareStatement( String.valueOf( queryAux ) );

						if( usePlaceHolder )
							GenericDaoUtils.prepareStatement( pstmt, null, null, null, null, values.subList( begin, end ) );

						pstmt.executeUpdate();

						closePreparedStatement();

						begin = end;

						queryAux.setLength( 0 );

						logger.info( message + " - Deleted so far: " + registerCount + " of " + listSize );

						if ( listSize - registerCount == 0 )
							continueDelete = false;
					}

					totalRegisterCount += registerCount;
				}
				else
					logger.error( message + " - It is not recommended to use this method to delete data without an index" );

				closeResources();

				success = true;
				logger.info( message + " - Finish delete ( " + totalRegisterCount + " register(s) deleted )" );
			}
			catch( IllegalArgumentException | SQLException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
			finally
			{
				closeResources();
			}
		}
		else
			logger.error( message + " - DeleteTO doesn't have enough info to execute the process" );

		return success;
	}
	
	@Override
	public void openConnection()
	{
		// TODO
	}
	
	@Override
	public void closeResources()
	{
		closeConnection();
		closePreparedStatement();
		closeResultSet();
	}

	@Override
	public void closeConnection()
	{
		if( connection != null )
		{
			try
			{
				connection.close();
			}
			catch ( SQLException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	@Override
	public void closePreparedStatement()
	{
		if ( pstmt != null )
		{
			try
			{
				pstmt.close();
			}
			catch ( SQLException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	@Override
	public void closeResultSet()
	{
		if ( rs != null )
		{
			try
			{
				rs.close();
			}
			catch ( SQLException e )
			{
				logger.error( e.getMessage() );
				throw new RuntimeException(e.getMessage());
			}
		}
	}
}