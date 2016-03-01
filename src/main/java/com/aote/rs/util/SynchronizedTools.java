package com.aote.rs.util;

import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * 线程锁定处理
 * 
 * @author Administrator
 *
 */
public class SynchronizedTools {

	// 锁定线程获得编号
	public synchronized static JSONObject getSerialNumber(Session session,
			SessionFactory sessionFactory, String query, String attrname) {
		JSONObject result = new JSONObject();
		List list =null;
		if(null!=session){
			list = executeFind(session,new HibernateCall(query, 0, 10));
		}else{
			executeFind(sessionFactory.getCurrentSession(),new HibernateCall(query, 0, 10));
		}
		if (list.size() != 1) {
			// 查询到多条数据，跑出异常
			throw new WebApplicationException(500);
		}
		// 把单个map转换成JSON对象
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);		
		if(null!=session){
			try {
				bulkUpdate(session, "update t_singlevalue set value=value+1 where name ='" + result.getString("name") + "'");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
				long attrVal = Long.parseLong(map.get(attrname).toString());
				map.put(attrname, attrVal + 1 + "");
				sessionFactory.getCurrentSession().update(map);
		}
		return result;
	}

	//执行查找
	private static List executeFind(Session session, HibernateCall hibernateCall) {
		return (List) hibernateCall.doInHibernate(session);
	}
	
	/**
	 * 返回updateCount
	 * 
	 * @param session
	 * @param sql
	 * @return
	 */
	private static int bulkUpdate(Session session, String sql) {
		Query queryObject = session.createQuery(sql);
		return new Integer(queryObject.executeUpdate()).intValue();
	}
}

