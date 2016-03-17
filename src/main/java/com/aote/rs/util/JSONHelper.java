package com.aote.rs.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.ListType;
import org.hibernate.type.LongType;
import org.hibernate.type.SetType;
import org.hibernate.type.TimeType;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.expression.ExpressionGenerator;

public class JSONHelper {
	static Logger log = Logger.getLogger(JSONHelper.class);

	
	/**
	 * json对象转map
	 * @param jsonObject
	 * @return
	 */
	public static HashMap<String, Object> toHashMap(JSONObject jsonObject) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Iterator it = jsonObject.keys();
		// 遍历jsonObject数据，添加到Map对象
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
	
	/**
	 * json对象转map
	 * 
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	public static HashMap<String, Object> toHashMap(JSONObject object,
			HibernateTemplate hibernateTemplate, String entityName)
			throws JSONException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		ClassMetadata classData = hibernateTemplate.getSessionFactory()
				.getClassMetadata(entityName);
		// 遍历jsonObject数据，添加到Map对象
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = object.get(key);
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (Exception e) {

			}

			if (object.isNull(key)) {
				// 空的id号不往属性里放，以便按新增处理
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray
					&& propType instanceof SetType) {
				// Json数组转换成一对多关系的Set
				Set<Map<String, Object>> set = saveSet(hibernateTemplate,
						(JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONArray
					&& propType instanceof ListType) {
				// Json数组转换成一对多关系的Set
				List<Map<String, Object>> set = saveList(hibernateTemplate,
						(JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = (String) obj.get("EntityType");
				Map<String, Object> set = saveWithoutExp(hibernateTemplate,
						type, (JSONObject) value);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				long l = 0;
				if (value instanceof Double) {
					l = ((Double) value).longValue();
				} else if (value instanceof Long) {
					l = ((Long) value).longValue();
				} else if (value instanceof Integer) {
					l = ((Integer) value).intValue();
				}
				Date d = new Date(l);
				map.put(key, d);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// int直接转换成double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof Integer && propType instanceof LongType) {
				Long v = Long.valueOf(value.toString());
				map.put(key, v.longValue());
			} else {
				// 有需要后台计算的表达式，后台计算后，把结果返回
				if (value instanceof String
						&& value.toString().indexOf("#") != -1) {
					// 调用表达式运算
					try {
						value = ExpressionGenerator.getExpressionValue(value
								.toString());
					} catch (Exception e) {
						log.debug(value + "表达式处理发生异常,使用self值");
					}
				}
				map.put(key, value);
			}
		}
		return map;
	}

	// 保存JSONArray里的对象，并转换为Set
	private static Set<Map<String, Object>> saveSet(
			HibernateTemplate hibernateTemplate, JSONArray array)
			throws JSONException {
		Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = saveWithoutExp(hibernateTemplate, type,
					obj);
			set.add(map);
		}
		return set;
	}

	// 保存JSONArray里的对象，并转换为Set
	private static List<Map<String, Object>> saveList(
			HibernateTemplate hibernateTemplate, JSONArray array)
			throws JSONException {
		List<Map<String, Object>> set = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = saveWithoutExp(hibernateTemplate, type,
					obj);
			set.add(map);
		}
		return set;
	}

	// 保存JsonObject，并转换为Map，保存时，不做后台表达式运算，用于一对多关系中的子的保存
	private static Map<String, Object> saveWithoutExp(
			HibernateTemplate hibernateTemplate, String entityName,
			JSONObject object) throws JSONException {
		// 根据实体名字去除配置属性信息
		ClassMetadata classData = hibernateTemplate.getSessionFactory()
				.getClassMetadata(entityName);
		// 把json对象转换成map
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (Exception e) {

			}

			Object value = object.get(key);
			if (object.isNull(key)) {
				// 空的id号不往属性里放，以便按新增处理
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray) {
				// Json数组转换成一对多关系的Set
				Set<Map<String, Object>> set = saveSet(hibernateTemplate,
						(JSONArray) value);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				long l = 0;
				if (value instanceof Double) {
					l = ((Double) value).longValue();
				} else if (value instanceof Long) {
					l = ((Long) value).longValue();
				}
				Date d = new Date(l);
				map.put(key, d);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// int直接转换成double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = (String) obj.get("EntityType");
				Map<String, Object> set = saveWithoutExp(hibernateTemplate,
						type, (JSONObject) value);
				map.put(key, set);
			} else {
				map.put(key, value);
			}
		}
		hibernateTemplate.saveOrUpdate(entityName, map);
		return map;
	}

}
