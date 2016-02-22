package com.aote.rs.util;

import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * �߳���������
 * 
 * @author Administrator
 *
 */
public class SynchronizedTools {

	// �����̻߳�ñ��
	public synchronized static JSONObject getSerialNumber(
			SessionFactory sessionFactory, String query, String attrname) {
		JSONObject result = new JSONObject();
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10));
		if (list.size() != 1) {
			// ��ѯ���������ݣ��ܳ��쳣
			throw new WebApplicationException(500);
		}
		// �ѵ���mapת����JSON����
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		long attrVal = Long.parseLong(map.get(attrname).toString());
		map.put(attrname, attrVal + 1 + "");
		sessionFactory.getCurrentSession().update(map);
		return result;
	}

	private static List executeFind(Session session, HibernateCall hibernateCall) {
		return (List) hibernateCall.doInHibernate(session);
	}
}
