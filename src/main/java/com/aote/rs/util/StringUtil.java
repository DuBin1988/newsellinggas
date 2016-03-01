package com.aote.rs.util;

import java.util.Random;

public class StringUtil {

	/**
	 * �ж��ַ����Ƿ񲻹����ȣ��������Ҳ�ƴ��str
	 * 
	 * @param a
	 *            ԭʼ�ַ���
	 * @param len
	 *            Ŀ�곤��
	 * @param str
	 *            ƴ���ַ���
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
	 * ���ݳ��ȣ�����������������
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
