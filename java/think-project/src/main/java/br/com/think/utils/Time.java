package br.com.think.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
    public static String getRandomTimeBetweenTwoDates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long randomDateCalculated;
        long beginTime = Timestamp.valueOf("2016-08-25 23:30:00").getTime();
        long endTime = Timestamp.valueOf("2016-08-26 03:00:00").getTime();
        long diff = endTime - beginTime + 1;

        randomDateCalculated = beginTime + (long) (Math.random() * diff);

        if (randomDateCalculated < beginTime) {
            randomDateCalculated = beginTime;
        } else if (randomDateCalculated > endTime) {
            randomDateCalculated = endTime;
        }

        Date randomDate = new Date(randomDateCalculated);

        return dateFormat.format(randomDate);
    }
    
	public static String getTime() {
		return "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(new Date()) + " ] ";
	}
}
