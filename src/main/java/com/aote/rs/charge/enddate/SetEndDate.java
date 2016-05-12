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
	public Calendar enddate(String userid, HibernateTemplate hibernateTemplate,
			Map<String, Object> loginuser) throws Exception {
		// TODO Auto-generated method stub
		// ��÷ֹ�˾
		String f_filiale = loginuser.get("f_fengongsi").toString();
		// ��ѯ���ɽ��������ñ��Ƿ��и÷ֹ�˾������
		String hql = "from t_zhinajindate where f_filiale='" + f_filiale + "'";
		List<Object> list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("û���ҵ��ֹ�˾����" + f_filiale + "�����ɽ�����������Ϣ��");
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
			throw new ResultException("���ɽ���������û���ҵ�����Ϊ��" + type + "�Ĵ������");
		}
		return cal;
	}

}
