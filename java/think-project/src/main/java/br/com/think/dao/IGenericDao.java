package br.com.think.dao;

import br.com.think.to.SelectTo;
import br.com.think.to.InsertTo;
import br.com.think.to.UpdateTo;
import br.com.think.to.DeleteTo;

import java.util.List;

public interface IGenericDao
{
	< T > List< T > selectAll( SelectTo selectTO, String message );
	< T > List< T > selectAll( SelectTo selectTO, String message, String filePath );
	< T > boolean insertAll( InsertTo< T > insertTO, String message );
	< T > boolean updateAll( UpdateTo< T > updateTO, String message );
	< T > boolean deleteAll( DeleteTo< T > deleteTO, String message );
	< T > boolean insertAllWithSelect( InsertTo< T > insertTO, SelectTo selectTO, String message );

	void openConnection();
	void closeResources();
	void closeConnection();
	void closePreparedStatement();
	void closeResultSet();
}