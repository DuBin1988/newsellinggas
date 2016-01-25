package com.aote.expression.upkeep;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.aote.rs.util.DateTools;

/**
 * 居民维护费
 * 
 */
public class InhabitantUpkeep implements UpkeepInterface {
	private SessionFactory sessionFactory;

	public String computeUpkeep(Map map, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		final int year = 12;
		// 得到开始日期和结束日期
		String startMonth = (String) map.get("startmonth");
		String endMonth = (String) map.get("endmonth");
		Calendar s = DateTools.getDate(startMonth, "yyyy-MM-dd");
		Calendar e = DateTools.getDate(endMonth, "yyyy-MM-dd");
		int dY = e.get(Calendar.YEAR) - s.get(Calendar.YEAR);
		int dM = e.get(Calendar.MONTH) - s.get(Calendar.MONTH)+1;
		dM = dM + dY * year;
		// 如果是一年
		if (dM == 12) {
			return getSingleValue("居民年维护费");
		}
		// 否则 ,用相差月数 * 单价
		else if (dM < 12) {
			String upKeep = (String) getSingleValue("居民月维护费");
			double dUppKeep = Double.parseDouble(upKeep);
			return (dUppKeep * dM) + "";
		} else {
			// 维护费大于一年,按年维护费 + 剩余月份 * 维护费单价
			double years = Math.abs(dM / year);
			double months = dM - (years * year);
			String yearUpKeep = getSingleValue("居民年维护费");
			double yearsUpKeep = Double.parseDouble(yearUpKeep);
			String monthUpKeep = getSingleValue("居民月维护费");
			double monthsUpKeep = Double.parseDouble(monthUpKeep);
			return (yearsUpKeep * years + months * monthsUpKeep) + "";
		}
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
