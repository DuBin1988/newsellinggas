package com.aote.expression.upkeep;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Element;

import com.aote.rs.util.DateTools;

/**
 * 公福维护费
 * 
 */
public class GongFuUpkeep implements UpkeepInterface {
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public String computeUpkeep(Map map, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		try {
			// 得到开始日期,和锅炉费
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
			// 没有数据直接返回
			if (list.size() == 0) {
				return null;
			}
			Object[] objs = (Object[]) list.get(0);
			String str = objs[0] + "";
			String startMonth = objs[1] + "";
			// 如果不是交费时间
			if (!this.notPayfeeTime()) {
				return null;
			}
			// 如果维护费为空
			if (str == null || str.equals("")) {
				return null;
			}
			// 如果今年已交费
			if (payfeed(startMonth)) {
				return null;
			}
			// 可以交费，计算费用
			double fee = Double.parseDouble(str);
			String payfee = getPayfee(fee, startMonth, map);
			return payfee;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 计算维护费
	 */
	private String getPayfee(double fee, String startMonth, Map map) {
		// 如果没有startmonth,根据输入的日期计算年份
		if (startMonth == null || startMonth.equals("")) {
			return getPayfeeByInput(fee, map);
		} else {
			String endMonth = map.get("endmonth") + "";
			// 根据用户的startmonth计算维护费
			return getPayfeeByStartMonth(fee, startMonth, endMonth);
		}
	}

	/**
	 * 现在是否交费 <!--锅炉维护费开始交费月分--> <goulustartmonth month="5"/>
	 */
	private boolean notPayfeeTime() {
		// Calendar now = Calendar.getInstance();
		// String sM = getSingleValue("锅炉维护费开始交费月分");
		// int setMonth = Integer.parseInt(sM);
		// int month = now.get(Calendar.MONTH) + 1;
		// if (month < setMonth) {
		// return false;
		// }
		return true;
	}

	/**
	 * 根据输入的日期计算
	 */
	private String getPayfeeByInput(double fee, Map map) {
		// 如果有输入了开始和结束日期
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
	 * 根据用户的startMonth计算
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
	 * 如果今年已交费
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
