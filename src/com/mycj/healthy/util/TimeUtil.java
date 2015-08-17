package com.mycj.healthy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
	 
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
 
    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }
 
    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }
    
 	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static boolean isSameDay(String date1, Date date2) {
    	String dateStr2 = sdf.format(date2);
    	return date1.equals(dateStr2);
    }
    
    public static boolean isSameMonth(Date date1, Date date2) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(date1);
    	Calendar c2 = Calendar.getInstance();
    	c2.setTime(date2);
    	return c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH)&&c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR);
    	
    }
    
    public static String dateToString(Date date){
    	return sdf.format(date);
    }
    
    public static String dateToHourStr(long date){
    	Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(date);
     	SimpleDateFormat s = new SimpleDateFormat("mm:ss");
    	return s.format(c.getTime());
    }
    public static Date stringToDate(String date){
    	try {
			return sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    public static Date stringToDate(String date,String sdf){
    	try {
        	SimpleDateFormat sdf1 = new SimpleDateFormat(sdf);
    		return sdf1.parse(date);
    	} catch (ParseException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public static String getString(int date){
    	if (date<10) {
			return 0+"date";
		}else{
			return String.valueOf(date);
		}
    }
    
    public static int getDayOfMonth(Date date){
//   获取一个月的天
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);;
    	return c.get(Calendar.DAY_OF_MONTH);
    }
    public static int getDayOfMonth(String date){
//   获取一个月的天
    	return getDayOfMonth(stringToDate(date));
    }
    
    //当前的前DIFF天
    public static Date getDateOfDiffDay(Date date,int diff){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);;
    	c.add(Calendar.DATE, diff);
    	return  c.getTime();
    }
    //当前的前DIFF月
    public static Date getDateOfDiffMonth(Date date,int diff){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);;
    	c.add(Calendar.MONTH, diff);
    	return  c.getTime();
    }
    
    public static String dateToString(Date date,String sdf){
    	SimpleDateFormat sdf1 = new SimpleDateFormat(sdf);
    	return sdf1.format(date);	
    }
    
    
    
}
