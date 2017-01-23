package com.aote.rs.weixin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;


public class WxSign {
	private static String characterEncoding = "UTF-8";

	@SuppressWarnings("rawtypes")
	public static String createSign(SortedMap<Object, Object> parameters,
			String key) {
		StringBuffer sb = new StringBuffer();
		Set es = parameters.entrySet();
		// ���в��봫�εĲ�������accsii��������
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + key);
		String sign = MD5.MD5Encode(sb.toString(), characterEncoding)
				.toUpperCase();
		return sign;
	}

	public static String getNonceStr() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String OrderID = format.format(date);
		Random Serial = new Random();
		OrderID = OrderID + Serial.nextInt();
		OrderID = OrderID.substring(0,15);
		return OrderID;
			}

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

}
