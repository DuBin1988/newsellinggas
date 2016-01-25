package com.aote.rs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTools {
	/**
	 * 按照格式获得当前时间
	 * 
	 * @param format
	 * @return
	 */
	public static String getNow(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Calendar getDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		try {
			Date d = sdf.parse(date);
			cal.setTime(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}

	public static String getStrDate(Calendar date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String result = "";
		result = sdf.format(date.getTime());
		return result;
	}
}
