package com.aote.rs.util;

import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONHelper {

	/**
	 * json����תmap
	 * @param jsonObject
	 * @return
	 */
	public static HashMap<String, Object> toHashMap(JSONObject jsonObject) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Iterator it = jsonObject.keys();
		// ����jsonObject���ݣ���ӵ�Map����
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value;
			try {
				value = jsonObject.get(key);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			result.put(key, value);
		}
		return result;
	}

}
