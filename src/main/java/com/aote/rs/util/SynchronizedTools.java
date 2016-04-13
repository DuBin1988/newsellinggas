package com.aote.rs.util;

import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.exception.ResultException;

/**
 * 线程锁定处理
 * 
 * @author Administrator
 *
 */
public class SynchronizedTools {

	// 锁定线程获得编号
	public synchronized static JSONObject getSerialNumber(
			SessionFactory sessionFactory, String query, String attrname)
			throws ResultException {
		JSONObject result = new JSONObject();
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10));
		if (list.size() != 1) {
			// 查询到多条数据，跑出异常
			throw new ResultException("查询到多条数据或者未查询到数据：" + query);
		}
		// 把单个map转换成JSON对象
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		long attrVal = Long.parseLong(map.get(attrname).toString());
		map.put(attrname, attrVal + 1 + "");
		sessionFactory.getCurrentSession().update(map);
		return result;
	}

	// 锁定线程获得编号
	public synchronized static JSONObject getSerialNumber(
			HibernateTemplate hibernateTemplate, String query, String attrname)
			throws ResultException {
		JSONObject result = new JSONObject();
		List list = hibernateTemplate.find(query);
		if (list.size() != 1) {
			// 查询到多条数据，跑出异常
			throw new ResultException("查询到多条数据或者未查询到数据：" + query);
		}
		// 把单个map转换成JSON对象
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		long attrVal = Long.parseLong(map.get(attrname).toString());
		map.put(attrname, attrVal + 1 + "");
		hibernateTemplate.update(map);
		return result;
	}

	private static List executeFind(Session session, HibernateCall hibernateCall) {
		return (List) hibernateCall.doInHibernate(session);
	}
}
