package com.aote.expression.upkeep;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.aote.rs.util.DateTools;

/**
 * 商业维护费
 * 
 */
public class BussinesUpkeep implements UpkeepInterface {
	private SessionFactory sessionFactory;

	public String computeUpkeep(Map map, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		String metertype = (String) map.get("metertype");
		if (metertype != null && metertype.equals("卡表")) {
			return getCardMeterValue(map);
		}
		return this.getMachineMeterValue(map);
	}

	/**
	 * 机表计算维护费
	 */
	private String getMachineMeterValue(Map map) {
		// 获得用气量
		String svalue = (String) map.get("oughtamount");
		double value = Double.parseDouble(svalue);
		String result = this.getRepairFee((int) value) + "";
		return result;
	}

	/**
	 * 卡表计算维护费
	 */
	private String getCardMeterValue(Map map) {
		try {
			// 如果开户日期不足3个月，维护费为0
			String date = getOpenDate(map);
			Calendar now = Calendar.getInstance();
			Calendar cal = DateTools.getDate(date, "yyyy-MM-dd");
			int ymonth = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
			month = month + ymonth * 12;
			if (month < 2) {
				return "0";
			}
			// 否则,得到最后购气日期,计算,如果无购气记录,
			String lastpayfeedate = (String) map.get("f_finabuygasdate");
			if (lastpayfeedate == null || lastpayfeedate.equals("")) {
				month = month - 1;
			} else {
				Calendar cal2 = DateTools.getDate(lastpayfeedate, "yyyy-MM-dd");
				// 卡表商业用户，截止日期为上个月
				now.add(Calendar.MONTH, -1);
				month = now.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);

			}
			// 维护费=未交费月份*单价+最后一个月交费气量查找出来的费用
			String price = "20";
			int result = 0;
			for (int i = 1; i <= month + 1; i++) {
				now = Calendar.getInstance();
				// 查找每个月的气量,如果该月有气量,维护费根据气量计算,如果没有 为price
				now.add(Calendar.MONTH, -i);
				now.set(Calendar.DAY_OF_MONTH,1);
				String startDate = DateTools.getStrDate(now, "yyyy-MM-dd");
				now.set(Calendar.DAY_OF_MONTH, now
						.getActualMaximum(Calendar.DAY_OF_MONTH));
				String endDate = DateTools.getStrDate(now, "yyyy-MM-dd");
				double gas = getGasSumOneMonth(map, startDate, endDate);
				if (gas > 0) {
					result += this.getRepairFee(gas);
				} else {
					result += Integer.parseInt(price);
				}
			}

			return result + "";
		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 得到该用户的开户日期
	 */
	private String getOpenDate(Map map) {
		try {
			String result = "";
			String userId = (String) map.get("userid");
			String sql = "select f_dateofopening,f_beginfee from t_userfiles where f_userid='"
					+ userId + "'";
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			com.aote.rs.util.HibernateSQLCall sqlCall = new com.aote.rs.util.HibernateSQLCall(
					sql, 0, 10);
			List<Object> list = (List<Object>) sqlCall.doInHibernate(session);
			if (list.size() > 0) {
				Object[] o = (Object[]) list.get(0);
				result = o[0] + "";
				map.put("f_beginfee", o[1] + "");
			}
			session.getTransaction().commit();
			session.close();
			return result;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 得到某个月气量总和
	 */
	private double getGasSumOneMonth(Map map, String startDate, String endDate) {
		double result = 0;
		String userid = (String) map.get("userid");
		String sql = "select isnull(sum(f_pregas),0) from t_sellinggas where f_userid='"
				+ userid
				+ "' and f_deliverydate >='"
				+ startDate
				+ " 00:00:00' and f_deliverydate<='" + endDate + " 23:59:59'";
		try {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			com.aote.rs.util.HibernateSQLCall sqlCall = new com.aote.rs.util.HibernateSQLCall(
					sql, 0, 10);
			List<Object> list = (List<Object>) sqlCall.doInHibernate(session);
			if (list.size() > 0) {
				result = Double.parseDouble(list.get(0) + "");
			}
			session.getTransaction().commit();
			session.close();
			return result;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获得商业维护费配置
	 * 
	 * @return
	 */
	private List<Map> getshangye() {
		List<Map> list = new ArrayList<Map>();
		for (int i = 1; i <= 4; i++) {
			Map map = new HashMap();
			if (i == 1) {
				map.put("min", 1);
				map.put("max", 499);
				map.put("value", 20);
				list.add(map);
			} else if (i == 2) {
				map.put("min", 500);
				map.put("max", 999);
				map.put("value", 40);
				list.add(map);
			} else if (i == 3) {
				map.put("min", 1000);
				map.put("max", 1999);
				map.put("value", 60);
				list.add(map);
			} else if (i == 4) {
				map.put("min", 2000);
				map.put("value", 100);
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 根据售气量得到维护费 <!-- 商业用户维护费计算参数 --> <shangye price="20"> <segment min="1" *
	 * max="499" value="20" /> <segment min="500" max="999" value="40" />
	 * <segment min="1000" max="1999" value="60" /> <segment min="2000" *
	 * value="100" /> </shangye>
	 */
	private double getRepairFee(double gas) {
		List<Map> list = getshangye();
		// 对于每一个计算段
		for (int i = 0; i < list.size(); i++) {
			Map seg = list.get(i);
			String min = seg.get("min") + "";
			int imin = Integer.parseInt(min);
			// 如果没有max,给最大值
			String max = seg.get("max") + "";
			int imax = Integer.MAX_VALUE;
			try {
				imax = Integer.parseInt(max);
			} catch (Exception e) {
			}
			// 如果气量在计算段范围内,返回其值
			if (gas >= imin && gas <= imax) {
				String value = seg.get("value") + "";
				return Integer.parseInt(value);
			}
		}
		return 0;
	}
}
