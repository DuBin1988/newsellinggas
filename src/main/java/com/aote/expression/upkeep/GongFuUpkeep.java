package com.aote.expression.upkeep;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Element;

import com.aote.rs.util.DateTools;

/**
 * ����ά����
 * 
 */
public class GongFuUpkeep implements UpkeepInterface {
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public String computeUpkeep(Map map, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		try {
			// �õ���ʼ����,�͹�¯��
			String userId = (String) map.get("userid");
			String sql = "select f_guoluupkeep,substring(CONVERT(CHAR(8), f_beginfee, 112),1,6) from t_userfiles where f_userid='"
					+ userId + "'";
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			com.aote.rs.util.HibernateSQLCall sqlCall = new com.aote.rs.util.HibernateSQLCall(
					sql, 0, 10);
			List<Object> list = (List<Object>) sqlCall.doInHibernate(session);
			session.getTransaction().commit();
			session.close();
			// û������ֱ�ӷ���
			if (list.size() == 0) {
				return null;
			}
			Object[] objs = (Object[]) list.get(0);
			String str = objs[0] + "";
			String startMonth = objs[1] + "";
			// ������ǽ���ʱ��
			if (!this.notPayfeeTime()) {
				return null;
			}
			// ���ά����Ϊ��
			if (str == null || str.equals("")) {
				return null;
			}
			// ��������ѽ���
			if (payfeed(startMonth)) {
				return null;
			}
			// ���Խ��ѣ��������
			double fee = Double.parseDouble(str);
			String payfee = getPayfee(fee, startMonth, map);
			return payfee;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ����ά����
	 */
	private String getPayfee(double fee, String startMonth, Map map) {
		// ���û��startmonth,������������ڼ������
		if (startMonth == null || startMonth.equals("")) {
			return getPayfeeByInput(fee, map);
		} else {
			String endMonth = map.get("endmonth") + "";
			// �����û���startmonth����ά����
			return getPayfeeByStartMonth(fee, startMonth, endMonth);
		}
	}

	/**
	 * �����Ƿ񽻷� <!--��¯ά���ѿ�ʼ�����·�--> <goulustartmonth month="5"/>
	 */
	private boolean notPayfeeTime() {
		// Calendar now = Calendar.getInstance();
		// String sM = getSingleValue("��¯ά���ѿ�ʼ�����·�");
		// int setMonth = Integer.parseInt(sM);
		// int month = now.get(Calendar.MONTH) + 1;
		// if (month < setMonth) {
		// return false;
		// }
		return true;
	}

	/**
	 * ������������ڼ���
	 */
	private String getPayfeeByInput(double fee, Map map) {
		// ����������˿�ʼ�ͽ�������
		String inputStartMonth = (String) map.get("startmonth");
		String inputEndMonth = (String) map.get("endmonth");
		if (inputStartMonth == null || inputStartMonth.equals("")
				|| inputEndMonth == null || inputEndMonth.equals("")) {
			return null;
		} else {
			// int year = BSCalendarHelper.getDispersionOfYears(inputEndMonth,
			// inputStartMonth) + 1;
			int year = Integer.parseInt(inputEndMonth.substring(0, 4))
					- Integer.parseInt(inputStartMonth.substring(0, 4)) + 1;
			return (year * fee) + "";
		}
	}

	/**
	 * �����û���startMonth����
	 */
	private String getPayfeeByStartMonth(double fee, String startMonth,
			String endMonth) {
		// String nowDate = this.getNowMonth();
		// int year = BSCalendarHelper.getDispersionOfYears(nowDate, startMonth)
		// + 1;
		int year = Integer.parseInt(endMonth.substring(0, 4))
				- Integer.parseInt(startMonth.substring(0, 4));
		int month = Integer.parseInt(endMonth.substring(5, 7))
				- Integer.parseInt(startMonth.substring(4, 6));
		if (month == 11) {
			year = year + 1;
		}
		return (year * fee) + "";
	}

	/**
	 * ��������ѽ���
	 */
	private boolean payfeed(String startMonth) {
		if (startMonth != null
				&& (startMonth.compareTo(this.getNowMonth()) > 0)) {
			return true;
		}
		return false;
	}

	private String getNowMonth() {
		Calendar now = Calendar.getInstance();
		return DateTools.getStrDate(now, "yyyyMM");
	}

	private String getSingleValue(String name) {
		String sql = "select value from t_singlevalue where name='" + name
				+ "'";
		String result = "";
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		com.aote.rs.util.HibernateSQLCall sqlCall = new com.aote.rs.util.HibernateSQLCall(
				sql, 0, 10);
		List<Object> list = (List<Object>) sqlCall.doInHibernate(session);
		if (list.size() > 0) {
			result = list.get(0) + "";
		}
		session.getTransaction().commit();
		session.close();
		return result;
	}
}
