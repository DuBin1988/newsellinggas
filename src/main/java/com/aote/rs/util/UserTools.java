package com.aote.rs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.exception.ResultException;

/**
 * ��¼�û�������
 * 
 * @author Administrator
 *
 */
public class UserTools {

	/**
	 * ����id��õ�¼�û������һ����֯��Ϣ
	 * 
	 * @param loginuserid
	 * @param sessionFactory
	 * @return
	 * @throws ResultException
	 */
	public static Map<String, Object> getUser(String loginuserid,
			HibernateTemplate hibernateTemplate) throws ResultException {
		Map<String, Object> user = new HashMap<String, Object>();
		String hql = "from t_user where id=" + loginuserid;
		List list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("û���ҵ�idΪ" + loginuserid + "�Ĳ���Ա��Ϣ");
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		user.putAll(map);
		// �����֯��Ϣ
		String orgstr = getOrgStr(loginuserid, hibernateTemplate);
		user.put("orgpathstr", orgstr);
		return user;
	}

	/**
	 * ����id�����֯��Ϣ
	 * 
	 * @param loginuserid
	 * @param sessionFactory
	 * @return
	 * @throws ResultException
	 */
	private static String getOrgStr(String loginuserid,
			HibernateTemplate hibernateTemplate) throws ResultException {
		String result = "";
		String hql = "from t_user where id=" + loginuserid;
		List list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("û���ҵ�idΪ" + loginuserid + "�Ĳ���Ա��Ϣ");
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		String PARENTID = (String) map.get("parentid");
		for (;;) {
			hql = "from t_organization where id=" + PARENTID;
			list = hibernateTemplate.find(hql);
			if (list.size() == 0)
				break;
			map = (Map<String, Object>) list.get(0);
			String name = (String) map.get("name");
			PARENTID = (String) map.get("parentid");
			result = name + "." + result;
		}
		if (result != "") {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
