package com.aote.rs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.collection.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * 计算滞纳金服务
 * @author Administrator
 * 
 */
@Component
public class OverdueService {
	static Logger log = Logger.getLogger(OverdueService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 查询抄表未交费记录，同时计算滞纳金
	 * @param path
	 *  查询sql
	 * @return
	 */
	public JSONArray invoke(String hql) {
		JSONArray array = new JSONArray();
		JSONArray result = new JSONArray();
		try {
			// 查询抄表未交费记录
			List<Object> list = this.hibernateTemplate.find(hql);
			for (Object obj : list) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = MapToJson(map);
				// 删除关联的档案信息
				json.remove("users");
				array.put(json);
			}
			result = due(array);
			log.debug(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 计算每条滞纳金
	 * 
	 * @param array
	 *            抄表信息列表
	 * @return
	 * @throws Exception
	 */
	public JSONArray due(JSONArray array) throws Exception {
		Calendar cal = Calendar.getInstance();
		JSONArray result = new JSONArray();
		double zhye = -1;
		// 循环计算每条抄表记录的滞纳金
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (zhye == -1) {
				// 获得账户余额
				zhye = getBalance(obj.getString("f_userid"));
			}
			obj.put("f_zhye", zhye);
			// 交费日期,当前时间
			cal.setTime(new Date());
			cal(obj, cal.getTime());
			// 用气金额
			double oughtfee = obj.getDouble("oughtfee");
			// 计算完后，重新设置剩下的账号余额
			if (oughtfee > zhye) {
				zhye = 0;
			} else {
				zhye = zhye - oughtfee;
			}
			result.put(obj);
		}
		return result;
	}

	/**
	 * 计算滞纳金
	 * 
	 * @param obj
	 *            用于计算滞纳金的抄表对象
	 * @param deliverydate
	 *            交费日期
	 * @return 滞纳金明细
	 */
	private void cal(JSONObject obj, Date deliverydate) throws Exception {
		JSONArray result = new JSONArray();
		Calendar cal = Calendar.getInstance();
		// 滞纳金系数
		BigDecimal znj = getZNJ(obj);//-------------------------------0.005
		// 滞纳金;
		BigDecimal znjmoney = new BigDecimal(0);
		// 交费截止日期
		// cal.setTimeInMillis(obj.getLong("f_endjfdate"));
		Date expire_date = getExpireDate(obj);
		// 如果交费日期小于截止日期,滞纳金为0
		if (before(deliverydate, expire_date)) {
			// 设置滞纳金
			znjmoney = new BigDecimal(0);
			// 计算平均用气金额
			calavg(obj);
		} else {
			// 计算预交天数，平均用气金额
			calfor(obj);
			// 预交天数
			int yjday = obj.getInt("f_enoughtime");
			// 抄表开始日期
			cal.setTimeInMillis(obj.getLong("scinputdate"));
			// 抄表开始日期加上已经预交的天数
			cal.add(Calendar.DAY_OF_YEAR, yjday);
			// 滞纳金天数=交费日期-预交天数后日期
			int znjday = getDay(cal.getTime(), deliverydate);
			obj.put("f_zhinajintianshu", znjday);
			// 日平均用气金额
			BigDecimal averageamount = new BigDecimal(obj
					.getDouble("f_averageamount"));
			// 不需要计算滞纳金的天数=交费日期-抄表日期
			Calendar lastinputdate = Calendar.getInstance();
			lastinputdate.setTimeInMillis(obj.getLong("lastinputdate"));
			int no = getDay(lastinputdate.getTime(), deliverydate);
			// 每一天的滞纳金
			for (int i = znjday; i > no; i--) {
				JSONObject j = new JSONObject();
				// 每天滞纳金
				BigDecimal day_znj = averageamount.multiply(znj).multiply(
						new BigDecimal(i));
				// 总滞纳金
				znjmoney = znjmoney.add(day_znj);
				// 滞纳金明细表
				j.put("EntityType", "t_overdetail");
				// 用户编号
				j.put("f_userid", obj.getString("f_userid"));
				// 抄表id
				j.put("f_handid", obj.getInt("id"));
				// 每天的滞纳金
				j.put("f_dayznj", day_znj.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue());
				// 日期
				j.put("f_date", sdf.format(cal.getTime()));
				// 天数
				j.put("f_day", i);
				// 计算公式
				j.put("f_gongshi", averageamount.setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue()
						+ " * " + znj.doubleValue() + " * " + i);
				cal.add(Calendar.DAY_OF_YEAR, 1);
				result.put(j);
			}
			// 滞纳金明细列表
			obj.put("zhinajinlist", result);
		}
		obj.put("f_zhinajin", znjmoney.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * 计算交费截止日期
	 * 
	 * @param obj
	 */
	private Date getExpireDate(JSONObject obj) throws Exception {
		Calendar cal = Calendar.getInstance();
		// 抄表日期
		cal.setTimeInMillis(obj.getLong("f_handdate"));
		// 抄表日期当月月底
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.DATE, -1);
		obj.put("f_endjfdate", cal.getTimeInMillis());
		return cal.getTime();
	}

	/**
	 * 计算平均用气量,抄表间隔天数
	 * 
	 * @param obj
	 * @throws Exception
	 */
	private void calavg(JSONObject obj) throws Exception {
		Calendar cal = Calendar.getInstance();
		// 抄表开始日期
		cal.setTimeInMillis(obj.getLong("scinputdate"));
		Date begindate = cal.getTime();
		// 抄表日期
		cal.setTimeInMillis(obj.getLong("lastinputdate"));
		Date enddate = cal.getTime();
		// 抄表周期天数
		int cycday = getDay(begindate, enddate);
		obj.put("f_cycle", cycday);
		// 抄表用气金额
		BigDecimal oughtfee = new BigDecimal(obj.getDouble("oughtfee"));
		// 每天用气金额
		if (cycday == 0) {
			BigDecimal avg = new BigDecimal(0.00);
		}
		else {
		BigDecimal avg = oughtfee.divide(new BigDecimal(cycday), 2,
				BigDecimal.ROUND_HALF_UP);
		obj.put("f_averageamount", avg.doubleValue());
		}
	}

	/**
	 * 计算预交天数，平均用气金额等信息
	 * 
	 * @param obj
	 * @throws Exception
	 */
	private void calfor(JSONObject obj) throws Exception {
		double day = 0;
		Calendar cal = Calendar.getInstance();
		// 获得用户余额
		double zhye = obj.getDouble("f_zhye");//-----------------------------------0
		// 抄表开始日期
		cal.setTimeInMillis(obj.getLong("scinputdate"));//---------------------2017-1-18
		Date begindate = cal.getTime();
		// 抄表日期
		cal.setTimeInMillis(obj.getLong("lastinputdate"));//--------------------2017-2-24
		Date enddate = cal.getTime();
		// 抄表周期天数
		int cycday = getDay(begindate, enddate);//---------------------------37天
		obj.put("f_cycle", cycday);
		// 抄表用气金额
		BigDecimal oughtfee = new BigDecimal(obj.getDouble("oughtfee"));//----------461.9
		// 每天用气金额
		BigDecimal avg = oughtfee.divide(new BigDecimal(cycday), 2,
				BigDecimal.ROUND_HALF_UP);//-----------------------------------12.48
		obj.put("f_averageamount", avg.doubleValue());
		if (zhye > 0) {
			if (avg.doubleValue()==0) {
				day = 0;
			}
			else {
			// 预交天数
			day = zhye / avg.doubleValue();
			// 余数大于0天数加1
			if (zhye / avg.doubleValue() > 0) {
				day++;
			}
			}
		}
		obj.put("f_enoughtime", day);
	}

	/**
	 * 根据hql，从数据库获得一个对象
	 * 
	 * @param hql
	 * @return
	 */
	private JSONObject getOneObj(String hql) {
		JSONObject json = new JSONObject();
		List<Object> list = this.hibernateTemplate.find(hql);
		if (list.size() == 1) {
			Map<String, Object> map = (Map<String, Object>) list.get(0);
			json = MapToJson(map);
		}
		return json;
	}

	/**
	 * 获得用户账户余额
	 * 
	 * @param f_userid
	 *            用户编号
	 * @return 账户余额
	 */

	private double getBalance(String f_userid) throws Exception {
		BigDecimal result = new BigDecimal(0);
		String hql = "from t_userfiles where f_userid='" + f_userid + "'";
		List<Object> list = this.hibernateTemplate.find(hql);
		if (list.size() == 1) {
			Map<String, Object> map = (Map<String, Object>) list.get(0);
			JSONObject json = MapToJson(map);
			result = new BigDecimal(json.getDouble("f_zhye"));
		}
		return result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 获得滞纳金系数,根据民用，非民用分别从单值中获得滞纳金比率
	 * 
	 * @return 滞纳金比率
	 */
	private BigDecimal getZNJ(JSONObject obj) throws Exception {
		// 根据民用，非民用分别从单值中获得滞纳金比率
		JSONObject json = getOneObj("from t_singlevalue where name='"
				+ obj.getString("f_usertype") + "滞纳金比率'");
		return new BigDecimal(json.getDouble("value"));
	}

	/**
	 * 获得两个日期相差天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private int getDay(Date startDate, Date endDate) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(startDate);
		long time1 = aCalendar.getTimeInMillis();
		aCalendar.setTime(endDate);
		long time2 = aCalendar.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(between_days + "");
	}

	private boolean after(Date d1, Date d2) throws Exception {
		// 两个日期相等，返回True
		if (sdf.format(d1).equals(sdf.format(d2))) {
			return true;
		}
		return d1.after(d2);
	}

	private boolean before(Date d1, Date d2) throws Exception {
		// 两个日期相等，返回True
		if (sdf.format(d1).equals(sdf.format(d2))) {
			return true;
		}
		return d1.before(d2);
	}

	// 把单个map转换成JSON对象
	private JSONObject MapToJson(Map<String, Object> map) {
		JSONObject json = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				String key = entry.getKey();
				Object value = entry.getValue();
				// 空值转换成JSON的空对象
				if (value == null) {
					value = JSONObject.NULL;
				} else if (value instanceof PersistentSet) {
					PersistentSet set = (PersistentSet) value;
					value = ToJson(set);
				}
				// 如果是$type$，表示实体类型，转换成EntityType
				if (key.equals("$type$")) {
					json.put("EntityType", value);
				} else if (value instanceof Date) {
					Date d1 = (Date) value;
					Calendar c = Calendar.getInstance();
					long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
					json.put(key, time);
				} else if (value instanceof HashMap) {
					JSONObject json1 = MapToJson((Map<String, Object>) value);
					json.put(key, json1);
				} else {
					json.put(key, value);
				}
			} catch (JSONException e) {
				throw new WebApplicationException(400);
			}
		}
		return json;
	}

	// 把集合转换成Json数组
	private Object ToJson(PersistentSet set) {
		// 没加载的集合当做空
		if (!set.wasInitialized()) {
			return JSONObject.NULL;
		}
		JSONArray array = new JSONArray();
		for (Object obj : set) {
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = MapToJson(map);
			array.put(json);
		}
		return array;
	}
}
