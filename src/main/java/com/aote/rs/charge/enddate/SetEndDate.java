package com.aote.rs.charge.enddate;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.exception.ResultException;

/**
 * �������ɽ��������ù��ܣ���ø÷ֹ�˾�����ɽ�����
 * 
 * @author Administrator
 * 
 */
public class SetEndDate implements IEndDate {

	@Override
	public Calendar enddate(String userinfoid, HibernateTemplate hibernateTemplate,
			Map<String, Object> loginuser) throws Exception {
		// TODO Auto-generated method stub
		// ��÷ֹ�˾
		String f_filiale = loginuser.get("f_fengongsi").toString();
		// ����û�����
		String hql = "from t_userinfo where f_filiale='" + f_filiale
				+ "' and f_userid='" + userinfoid + "'";
		List<Object> list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("û���ҵ�����ţ�" + userinfoid + "����Ϣ��");
		}
		Map<String, Object> hu = (Map<String, Object>) list.get(0);
		String usertype = hu.get("f_usertype") + "";
		if (usertype == "" || usertype.equals("")) {
			throw new ResultException("û���ҵ�����ţ�" + userinfoid
					+ "���û����� (f_usertype)��Ϣ��");
		}
		// ��ѯ���ɽ��������ñ��Ƿ��и÷ֹ�˾������
		hql = "from t_zhinajindate where f_usertype='" + usertype
				+ "' and f_filiale='" + f_filiale + "'";
		list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("û���ҵ��ֹ�˾����" + f_filiale + ",�û�����: "
					+ usertype + "�����ɽ�������Ϣ��");
		}
		Calendar cal = Calendar.getInstance();
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		String type = map.get("f_type").toString();
		if (type.equals("�����ƺ�")) {
			String day = map.get("f_day").toString();
			cal.add(Calendar.DATE, Integer.parseInt(day));
		} else if (type.equals("�����ƺ�")) {
			String month = map.get("f_month").toString();
			String day = map.get("f_monthday").toString();
			cal.add(Calendar.MONTH, Integer.parseInt(month));
			cal.set(Calendar.DATE, Integer.parseInt(day));
		} else {
			throw new ResultException("���ɽ�����û���ҵ�����Ϊ��" + type + "�Ĵ������");
		}
		return cal;
	}

}
