package br.com.think.utils;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author victoraugustofd
 *
 */

public abstract class DateUtils {

    private static final String YEAR_RANGE   = "yY";
    private static final String MONTH_RANGE  = "M";
    private static final String WEEK_RANGE   = "wW";
    private static final String DAY_RANGE    = "dD";
    private static final String HOUR_RANGE   = "hH";
    private static final String MINUTE_RANGE = "m";
    private static final String SECOND_RANGE = "sS";
    
    private static final String BEGIN_PATTERN = "(\\s*";
    private static final String END_PATTERN   = "{1}\\s*)";
    
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("\\d+\\s*");
    private static final Pattern YEAR_PATTERN    = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + YEAR_RANGE   + "])" + END_PATTERN);
    private static final Pattern MONTH_PATTERN   = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + MONTH_RANGE  + "])" + END_PATTERN);
    private static final Pattern WEEK_PATTERN    = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + WEEK_RANGE   + "])" + END_PATTERN);
    private static final Pattern DAY_PATTERN     = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + DAY_RANGE    + "])" + END_PATTERN);
    private static final Pattern HOUR_PATTERN    = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + HOUR_RANGE   + "])" + END_PATTERN);
    private static final Pattern MINUTE_PATTERN  = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + MINUTE_RANGE + "])" + END_PATTERN);
    private static final Pattern SECOND_PATTERN  = Pattern.compile(BEGIN_PATTERN + NUMERIC_PATTERN + "([" + SECOND_RANGE + "])" + END_PATTERN);
    
    private static final Pattern LOOK_FORWARD_ASSERT = Pattern.compile("(?=.*[" +
                                                                        YEAR_RANGE   +
                                                                        MONTH_RANGE  +
                                                                        WEEK_RANGE   +
                                                                        DAY_RANGE    +
                                                                        HOUR_RANGE   +
                                                                        MINUTE_RANGE +
                                                                        SECOND_RANGE +
                                                                       "])");
    
    private static final Pattern PATTERN = Pattern.compile("(" +
                                                            LOOK_FORWARD_ASSERT  + 
                                                            YEAR_PATTERN   + "?" +
                                                            MONTH_PATTERN  + "?" +
                                                            WEEK_PATTERN   + "?" +
                                                            DAY_PATTERN    + "?" +
                                                            HOUR_PATTERN   + "?" +
                                                            MINUTE_PATTERN + "?" +
                                                            SECOND_PATTERN + "?" +
                                                           ")");

    public static LocalDateTime calculatePastDate(String parameter) {
        return calculateDate(parameter, Boolean.TRUE);
    }
    
    public static LocalDateTime calculateFutureDate(String parameter) {
        return calculateDate(parameter, Boolean.FALSE);
    }
    
    private static LocalDateTime calculateDate(String parameter, Boolean isPast) {

        if (validateParameter(parameter)) {

            Character signal = defineSignal(isPast);
            
            Integer years   = setSignal(signal, retrieveYears(parameter));
            Integer months  = setSignal(signal, retrieveMonths(parameter));
            Integer weeks   = setSignal(signal, retrieveWeeks(parameter));
            Integer days    = setSignal(signal, retrieveDays(parameter));
            Integer hours   = setSignal(signal, retrieveHours(parameter));
            Integer minutes = setSignal(signal, retrieveMinutes(parameter));
            Integer seconds = setSignal(signal, retrieveSeconds(parameter));
            
            return LocalDateTime.now()
                                .plusYears(years)
                                .plusMonths(months)
                                .plusWeeks(weeks)
                                .plusDays(days)
                                .plusHours(hours)
                                .plusMinutes(minutes)
                                .plusSeconds(seconds);
        } else {
            // TODO
        }

        return null;
    }

    private static Boolean validateParameter(String parameter) {
        return null != parameter && PATTERN.matcher(parameter).matches();
    }

    private static Integer retrieveYears(String parameter) {
        return retrieveTime(parameter, YEAR_PATTERN);
    }

    private static Integer retrieveMonths(String parameter) {
        return retrieveTime(parameter, MONTH_PATTERN);
    }

    private static Integer retrieveWeeks(String parameter) {
        return retrieveTime(parameter, WEEK_PATTERN);
    }

    private static Integer retrieveDays(String parameter) {
        return retrieveTime(parameter, DAY_PATTERN);
    }

    private static Integer retrieveHours(String parameter) {
        return retrieveTime(parameter, HOUR_PATTERN);
    }

    private static Integer retrieveMinutes(String parameter) {
        return retrieveTime(parameter, MINUTE_PATTERN);
    }

    private static Integer retrieveSeconds(String parameter) {
        return retrieveTime(parameter, SECOND_PATTERN);
    }

    private static Integer retrieveTime(String parameter, Pattern pattern) {

        Matcher matcher = pattern.matcher(parameter);

        if (matcher.find()) {

            String time = matcher.group();

            if (null != time) {
                return Integer.parseInt(time.replaceAll("[^\\d]", ""));
            }
        }

        return 0;
    }
    
    private static Character defineSignal(Boolean isPast) {
        return isPast ? '-' : '+';
    }

    private static Integer setSignal(Character signal, Integer time) {
        return Integer.parseInt(signal + "" + time);
    }
}
