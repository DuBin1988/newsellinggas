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
 * �������ɽ����
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
	 * ��ѯ����δ���Ѽ�¼��ͬʱ�������ɽ�
	 * @param path
	 *  ��ѯsql
	 * @return
	 */
	public JSONArray invoke(String hql) {
		JSONArray array = new JSONArray();
		JSONArray result = new JSONArray();
		try {
			// ��ѯ����δ���Ѽ�¼
			List<Object> list = this.hibernateTemplate.find(hql);
			for (Object obj : list) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = MapToJson(map);
				// ɾ�������ĵ�����Ϣ
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
	 * ����ÿ�����ɽ�
	 * 
	 * @param array
	 *            ������Ϣ�б�
	 * @return
	 * @throws Exception
	 */
	public JSONArray due(JSONArray array) throws Exception {
		Calendar cal = Calendar.getInstance();
		JSONArray result = new JSONArray();
		double zhye = -1;
		// ѭ������ÿ�������¼�����ɽ�
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (zhye == -1) {
				// ����˻����
				zhye = getBalance(obj.getString("f_userid"));
			}
			obj.put("f_zhye", zhye);
			// ��������,��ǰʱ��
			cal.setTime(new Date());
			cal(obj, cal.getTime());
			// �������
			double oughtfee = obj.getDouble("oughtfee");
			// ���������������ʣ�µ��˺����
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
	 * �������ɽ�
	 * 
	 * @param obj
	 *            ���ڼ������ɽ�ĳ������
	 * @param deliverydate
	 *            ��������
	 * @return ���ɽ���ϸ
	 */
	private void cal(JSONObject obj, Date deliverydate) throws Exception {
		JSONArray result = new JSONArray();
		Calendar cal = Calendar.getInstance();
		// ���ɽ�ϵ��
		BigDecimal znj = getZNJ(obj);//-------------------------------0.005
		// ���ɽ�;
		BigDecimal znjmoney = new BigDecimal(0);
		// ���ѽ�ֹ����
		// cal.setTimeInMillis(obj.getLong("f_endjfdate"));
		Date expire_date = getExpireDate(obj);
		// �����������С�ڽ�ֹ����,���ɽ�Ϊ0
		if (before(deliverydate, expire_date)) {
			// �������ɽ�
			znjmoney = new BigDecimal(0);
			// ����ƽ���������
			calavg(obj);
		} else {
			// ����Ԥ��������ƽ���������
			calfor(obj);
			// Ԥ������
			int yjday = obj.getInt("f_enoughtime");
			// ����ʼ����
			cal.setTimeInMillis(obj.getLong("scinputdate"));
			// ����ʼ���ڼ����Ѿ�Ԥ��������
			cal.add(Calendar.DAY_OF_YEAR, yjday);
			// ���ɽ�����=��������-Ԥ������������
			int znjday = getDay(cal.getTime(), deliverydate);
			obj.put("f_zhinajintianshu", znjday);
			// ��ƽ���������
			BigDecimal averageamount = new BigDecimal(obj
					.getDouble("f_averageamount"));
			// ����Ҫ�������ɽ������=��������-��������
			Calendar lastinputdate = Calendar.getInstance();
			lastinputdate.setTimeInMillis(obj.getLong("lastinputdate"));
			int no = getDay(lastinputdate.getTime(), deliverydate);
			// ÿһ������ɽ�
			for (int i = znjday; i > no; i--) {
				JSONObject j = new JSONObject();
				// ÿ�����ɽ�
				BigDecimal day_znj = averageamount.multiply(znj).multiply(
						new BigDecimal(i));
				// �����ɽ�
				znjmoney = znjmoney.add(day_znj);
				// ���ɽ���ϸ��
				j.put("EntityType", "t_overdetail");
				// �û����
				j.put("f_userid", obj.getString("f_userid"));
				// ����id
				j.put("f_handid", obj.getInt("id"));
				// ÿ������ɽ�
				j.put("f_dayznj", day_znj.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue());
				// ����
				j.put("f_date", sdf.format(cal.getTime()));
				// ����
				j.put("f_day", i);
				// ���㹫ʽ
				j.put("f_gongshi", averageamount.setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue()
						+ " * " + znj.doubleValue() + " * " + i);
				cal.add(Calendar.DAY_OF_YEAR, 1);
				result.put(j);
			}
			// ���ɽ���ϸ�б�
			obj.put("zhinajinlist", result);
		}
		obj.put("f_zhinajin", znjmoney.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * ���㽻�ѽ�ֹ����
	 * 
	 * @param obj
	 */
	private Date getExpireDate(JSONObject obj) throws Exception {
		Calendar cal = Calendar.getInstance();
		// ��������
		cal.setTimeInMillis(obj.getLong("f_handdate"));
		// �������ڵ����µ�
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.DATE, -1);
		obj.put("f_endjfdate", cal.getTimeInMillis());
		return cal.getTime();
	}

	/**
	 * ����ƽ��������,����������
	 * 
	 * @param obj
	 * @throws Exception
	 */
	private void calavg(JSONObject obj) throws Exception {
		Calendar cal = Calendar.getInstance();
		// ����ʼ����
		cal.setTimeInMillis(obj.getLong("scinputdate"));
		Date begindate = cal.getTime();
		// ��������
		cal.setTimeInMillis(obj.getLong("lastinputdate"));
		Date enddate = cal.getTime();
		// ������������
		int cycday = getDay(begindate, enddate);
		obj.put("f_cycle", cycday);
		// �����������
		BigDecimal oughtfee = new BigDecimal(obj.getDouble("oughtfee"));
		// ÿ���������
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
	 * ����Ԥ��������ƽ������������Ϣ
	 * 
	 * @param obj
	 * @throws Exception
	 */
	private void calfor(JSONObject obj) throws Exception {
		double day = 0;
		Calendar cal = Calendar.getInstance();
		// ����û����
		double zhye = obj.getDouble("f_zhye");//-----------------------------------0
		// ����ʼ����
		cal.setTimeInMillis(obj.getLong("scinputdate"));//---------------------2017-1-18
		Date begindate = cal.getTime();
		// ��������
		cal.setTimeInMillis(obj.getLong("lastinputdate"));//--------------------2017-2-24
		Date enddate = cal.getTime();
		// ������������
		int cycday = getDay(begindate, enddate);//---------------------------37��
		obj.put("f_cycle", cycday);
		// �����������
		BigDecimal oughtfee = new BigDecimal(obj.getDouble("oughtfee"));//----------461.9
		// ÿ���������
		BigDecimal avg = oughtfee.divide(new BigDecimal(cycday), 2,
				BigDecimal.ROUND_HALF_UP);//-----------------------------------12.48
		obj.put("f_averageamount", avg.doubleValue());
		if (zhye > 0) {
			if (avg.doubleValue()==0) {
				day = 0;
			}
			else {
			// Ԥ������
			day = zhye / avg.doubleValue();
			// ��������0������1
			if (zhye / avg.doubleValue() > 0) {
				day++;
			}
			}
		}
		obj.put("f_enoughtime", day);
	}

	/**
	 * ����hql�������ݿ���һ������
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
	 * ����û��˻����
	 * 
	 * @param f_userid
	 *            �û����
	 * @return �˻����
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
	 * ������ɽ�ϵ��,�������ã������÷ֱ�ӵ�ֵ�л�����ɽ����
	 * 
	 * @return ���ɽ����
	 */
	private BigDecimal getZNJ(JSONObject obj) throws Exception {
		// �������ã������÷ֱ�ӵ�ֵ�л�����ɽ����
		JSONObject json = getOneObj("from t_singlevalue where name='"
				+ obj.getString("f_usertype") + "���ɽ����'");
		return new BigDecimal(json.getDouble("value"));
	}

	/**
	 * ������������������
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
		// ����������ȣ�����True
		if (sdf.format(d1).equals(sdf.format(d2))) {
			return true;
		}
		return d1.after(d2);
	}

	private boolean before(Date d1, Date d2) throws Exception {
		// ����������ȣ�����True
		if (sdf.format(d1).equals(sdf.format(d2))) {
			return true;
		}
		return d1.before(d2);
	}

	// �ѵ���mapת����JSON����
	private JSONObject MapToJson(Map<String, Object> map) {
		JSONObject json = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				String key = entry.getKey();
				Object value = entry.getValue();
				// ��ֵת����JSON�Ŀն���
				if (value == null) {
					value = JSONObject.NULL;
				} else if (value instanceof PersistentSet) {
					PersistentSet set = (PersistentSet) value;
					value = ToJson(set);
				}
				// �����$type$����ʾʵ�����ͣ�ת����EntityType
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

	// �Ѽ���ת����Json����
	private Object ToJson(PersistentSet set) {
		// û���صļ��ϵ�����
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
