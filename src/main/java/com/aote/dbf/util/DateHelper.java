package com.aote.dbf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.apache.bcel.generic.NEW;

public class DateHelper {

	public static String getdatetime() {
		Date now = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String f_tbdate=fmt.format(now);
		return fmt.format(now);
	}
	
	public static String longtoDate(Long longdate) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(longdate);
		return fmt.format(date);
	}
	
	public static String longto_Date(Long longdate) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(longdate);
		return fmt.format(date);
	}
	
	public static String longto_time(Long longdate) {
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(longdate);
		return fmt.format(date);
	}

	public static String getdateymd() {
		Date now = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		// String f_tbdate=fmt.format(now);
		return fmt.format(now);
	}

	public static String getdateym() {
		Date now = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
		// String f_tbdate=fmt.format(now);
		return fmt.format(now);
	}
	
	public static String getdateymd(String datestr) {
		String rest="1970-01-01";
		try {
			rest=datestr.substring(0, 4)+"-"+datestr.substring(4,6)+"-"+datestr.substring(6, 8);			
		} catch (Exception e) {
			rest="1970-01-01";
		}
		return rest;
	}
	
	public static long getDateTimeLong() {
			Date now = new Date();
			//SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String f_tbdate=fmt.format(now);
			return now.getTime();
	}
	
	public static long getDateTimeLong(String date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt = null;
		try {
			dt = fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt.getTime();
	}
		
		
	public static void main(String[] args) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateHelper.getdatetime();
		System.out.println(DateHelper.getDateTimeLong());
		long oo=1458086400000L;
		System.out.println(fmt.format(new Date(oo)));
		oo=7715617L;
		System.out.println(fmt.format(new Date(oo)));
		System.out.println(fmt.format(new Date(1458795600000L)));
		
		//"f_datetime":1458086400000,"f_datetimesub":1458086400000, "f_adjustDate":1458783206304,"f_adjustDatetime":1458795600000
		
	}
	
	

}
