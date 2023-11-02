package it.izs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class DateFormatUtils {
	
	public static final String sqlDatePattern = "yyyy-MM-dd";
	public static final String sqlDatePatternWithHours = "yyyy-MM-dd HH:mm:ss";
	public static final String sqlDatePatternWithHoursOracle = "yyyy-MM-dd HH24:MI:SS";
	public static final String italianDatePattern = "dd/MM/yyyy";
	public static final String italianDatePatternWithHours = "dd/MM/yyyy HH:mm:ss";
	public static final String italianDatePatternWithHoursOracle = "dd/MM/yyyy HH24:MI:SS";
		
	public static boolean isValidDateFormat(String format, String value) {
		value = value.replace("\'", "");
	    LocalDateTime ldt = null;
	    DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, Locale.ITALIAN);

	    try {
	        ldt = LocalDateTime.parse(value, fomatter);
	        String result = ldt.format(fomatter);
	        return result.equals(value);
	    } catch (DateTimeParseException e) {
	        try {
	            LocalDate ld = LocalDate.parse(value, fomatter);
	            String result = ld.format(fomatter);
	            return result.equals(value);
	        } catch (DateTimeParseException exp) {
	            try {
	                LocalTime lt = LocalTime.parse(value, fomatter);
	                String result = lt.format(fomatter);
	                return result.equals(value);
	            } catch (DateTimeParseException e2) {
	                // Debugging purposes
	                //e2.printStackTrace();
	            }
	        }
	    }

	    return false;
	}
	
	
	public static String validateDateFormat(String value) {
	    SimpleDateFormat formatter = new SimpleDateFormat(sqlDatePattern);
	    //To make strict date format validation
	    formatter.setLenient(false);
	    Date parsedDate = null;
	    try {
	        parsedDate = formatter.parse(value);
	    } catch (ParseException e) {
	        //Handle exception
	    }
	    return formatter.format(parsedDate);
	}
	 
	 
	 public static String sqlFormatDate(String value) {
		 value = value.replace("\'", "");
		 if(isValidDateFormat(DateFormatUtils.sqlDatePattern, value)) {
			 return "to_date(\'" + value + "\', \'" + sqlDatePattern + "\')";
		 } else if(isValidDateFormat(DateFormatUtils.sqlDatePattern, value)) {			 
			 return "to_date(\'" + value + "\', \'" + sqlDatePattern + "\')";
		 } else if(isValidDateFormat(DateFormatUtils.sqlDatePatternWithHours, value)) {			 
			 return "to_date(\'" + value + "\', \'" + sqlDatePatternWithHoursOracle + "\')";
		 } else if(isValidDateFormat(DateFormatUtils.italianDatePatternWithHours, value)) {			 
			 return "to_date(\'" + value + "\', \'" + italianDatePatternWithHoursOracle + "\')";
		 }
		 
		 return "";
	 }
	 
}
