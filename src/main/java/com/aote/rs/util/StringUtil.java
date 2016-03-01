package com.aote.rs.util;

import java.util.Random;

public class StringUtil {

	/**
	 * 判断字符串是否不够长度，不够的右侧拼接str
	 * 
	 * @param a
	 *            原始字符串
	 * @param len
	 *            目标长度
	 * @param str
	 *            拼接字符串
	 * @return
	 */
	public static String joint(String a, int len, String str) {
		String b = "";
		if (a.length() < len) {
			for (int i = 0; i < len - a.length(); i++) {
				b += str;
			}
		}
		return a+b;
	}

	public static String jointleft(String a, int len, String str) {
		String b = "";
		if (a.length() < len) {
			for (int i = 0; i < len - a.length(); i++) {
				b += str;
			}
		}
		a = b + a;
		return a;
	}

	/**
	 * 根据长度，获得随机产生的数字
	 * 
	 * @param len
	 * @return
	 */
	public static String grandom(int len) {
		String result = "";
		int a[] = new int[len];
		for (int i = 0; i < a.length; i++) {
			a[i] = (int) (10 * (Math.random()));
			result += a[i];
		}
		return result;
	}
}
