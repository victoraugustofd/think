package br.com.think.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time
{
	public static Calendar calendar = Calendar.getInstance();
	
	public final static NumberFormat dayFormat 	 = new DecimalFormat( "00" );
	public final static NumberFormat monthFormat = new DecimalFormat( "00" );
	public final static NumberFormat yearFormat  = new DecimalFormat( "0000" );
	
	public enum CompareDates
	{
		DATA_MENOR( -1 ),
		DATA_IGUAL( 0 ),
		DATA_MAIOR( 1 );
		
		private int value;
		
		CompareDates( int value )
		{
			setValue( value );
		}
		
		public int getValue()
		{
			return value;
		}
		
		public void setValue( int value )
		{
			this.value = value;
		}
	}
	
	public static String getTime()
	{
		String time;
		
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSSS" );
		Date now = new Date();
		
		time = "[ " + sdf.format( now ) + " ] ";
		
		return time;
	}
	
	public static int getLastDayOfMonth( int month, int year )
	{
		int lastDayOfMonth;
		Calendar calendar = Calendar.getInstance();
		
		calendar.set( year, month - 1, 1 );
		
		lastDayOfMonth = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
		
		return lastDayOfMonth;
	}
	
	public static String getRandomTimeBetweenTwoDates()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		
		long randomDateCalculated;
		long beginTime = Timestamp.valueOf( "2016-08-25 23:30:00" ).getTime();
		long endTime = Timestamp.valueOf( "2016-08-26 03:00:00" ).getTime();
		long diff = endTime - beginTime + 1;
		
		randomDateCalculated = beginTime + ( long ) ( Math.random() * diff );
		
		if( randomDateCalculated < beginTime )
		{
			randomDateCalculated = beginTime;
		}
		else if( randomDateCalculated > endTime )
		{
			randomDateCalculated = endTime;
		}
		
		Date randomDate = new Date( randomDateCalculated );
		
		return dateFormat.format( randomDate );
	}
}
