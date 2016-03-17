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
 * �߳���������
 * 
 * @author Administrator
 *
 */
public class SynchronizedTools {

	// �����̻߳�ñ��
	public synchronized static JSONObject getSerialNumber(
			SessionFactory sessionFactory, String query, String attrname)
			throws ResultException {
		JSONObject result = new JSONObject();
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10));
		if (list.size() != 1) {
			// ��ѯ���������ݣ��ܳ��쳣
			throw new ResultException("��ѯ���������ݻ���δ��ѯ�����ݣ�" + query);
		}
		// �ѵ���mapת����JSON����
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		long attrVal = Long.parseLong(map.get(attrname).toString());
		map.put(attrname, attrVal + 1 + "");
		sessionFactory.getCurrentSession().update(map);
		return result;
	}

	// �����̻߳�ñ��
	public synchronized static JSONObject getSerialNumber(
			HibernateTemplate hibernateTemplate, String query, String attrname)
			throws ResultException {
		JSONObject result = new JSONObject();
		List list = hibernateTemplate.find(query);
		if (list.size() != 1) {
			// ��ѯ���������ݣ��ܳ��쳣
			throw new ResultException("��ѯ���������ݻ���δ��ѯ�����ݣ�" + query);
		}
		// �ѵ���mapת����JSON����
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
