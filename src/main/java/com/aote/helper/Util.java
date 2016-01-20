package com.aote.helper;

import sun.misc.BASE64Encoder;

public class Util {
	public static String encode(String error) {
		try {
			return (new BASE64Encoder()).encodeBuffer(error.getBytes("UTF-8"));
		} catch (Exception e) {
			return "";
		}
	}
}
