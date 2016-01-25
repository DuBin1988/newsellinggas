package com.aote.rs.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.ResultTransformer;
import org.springframework.orm.hibernate3.HibernateCallback;

public class HibernateSQLCall implements HibernateCallback {
	String sql;
	int page;
	int rows;
	// 查询结果转换器，可以转换成Map等。
	public ResultTransformer transformer = null;

	public HibernateSQLCall(String sql, int page, int rows) {
		this.sql = sql;
		this.page = page;
		this.rows = rows;
	}

	public Object doInHibernate(Session session) {
		Query q = session.createSQLQuery(sql);
		// 有转换器，设置转换器
		if (transformer != null) {
			q.setResultTransformer(transformer);
		}
		List result = q.setFirstResult(page * rows).setMaxResults(rows).list();
		return result;
	}

	// 保存已经转换过的对象
	private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

	// 把单个map转换成JSON对象
	public Object MapToJson(Map<String, Object> map) {
		// 转换过，返回空对象
		if (contains(map))
			return JSONObject.NULL;
		transed.add(map);
		JSONObject json = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				String key = entry.getKey();
				Object value = entry.getValue();
				// 空值转换成JSON的空对象
				if (value == null) {
					value = JSONObject.NULL;
				} else if (value instanceof HashMap) {
					value = MapToJson((Map<String, Object>) value);
				} else if (value instanceof PersistentSet) {
					PersistentSet set = (PersistentSet) value;
					value = ToJson(set);
				}
				// 如果是$type$，表示实体类型，转换成EntityType
				if (key.equals("$type$")) {
					json.put("EntityType", value);
				} else if (value instanceof Date) {
					Date d1 = (Date) value;
					Calendar c = Calendar.getInstance();
					long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
					json.put(key, time);
				} else if (value instanceof MapProxy) {
					// MapProxy没有加载，不管
				} else {
					json.put(key, value);
				}
			} catch (JSONException e) {
				throw new WebApplicationException(400);
			}
		}
		return json;
	}

	// 把集合转换成Json数组
	public Object ToJson(PersistentSet set) {
		// 没加载的集合当做空
		if (!set.wasInitialized()) {
			return JSONObject.NULL;
		}
		JSONArray array = new JSONArray();
		for (Object obj : set) {
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = (JSONObject) MapToJson(map);
			array.put(json);
		}
		return array;
	}

	// 判断已经转换过的内容里是否包含给定对象
	public boolean contains(Map<String, Object> obj) {
		for (Map<String, Object> map : this.transed) {
			if (obj == map) {
				return true;
			}
		}
		return false;
	}
}
