package com.team.atapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;





public class DateUtil {
		final static Logger logger = Logger.getLogger(DateUtil.class);

	
		
		public static Date convertStringToDate(String  strTime, String srcFormat, String destFormat) {
			Date date= null;
		    try {
				DateFormat srcFormatter = new SimpleDateFormat(srcFormat);
				date = srcFormatter.parse(strTime);
				DateFormat destFormatter = new SimpleDateFormat(destFormat);
				System.out.println("str date in dest format : "+destFormatter.format(date));
				date = destFormatter.parse(destFormatter.format(date));
			} catch (Exception e) {
				logger.error("error in Timestamp",e);
			}
		    System.out.println("Dateeeeeeeee"+date);
		    return date;
		  }
		
		public static Date getCurrentDateTime(String format) {
			Date date= null;
		    try {
		    	Calendar calendar = Calendar.getInstance();
		    	long currentDateTime = calendar.getTimeInMillis();
				DateFormat formatter = new SimpleDateFormat(format);
				formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			//	String dateStr = formatter.format(currentDateTime);
			//	System.out.println("dateStr = "+dateStr);
				date = formatter.parse(formatter.format(currentDateTime));
			} catch (Exception e) {
				logger.error("error in  convertStringToTimestamp",e);
			}
		    return date;
		  }
		
		public static Date convertLongToDate(long longTime, String format) {
			Date date = null;
			try {
				DateFormat formatter = new SimpleDateFormat(format);
				formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(longTime);
				System.out.println(formatter.format(calendar.getTime()));
				date = formatter.parse(formatter.format(calendar.getTime()));
			} catch (Exception e) {

				logger.error("error in Timestamp", e);
			}
			return date;
		}

		
		
}