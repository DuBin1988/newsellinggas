package com.aote.rs.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class CalculateStairsPrice {
	
	@Autowired
	private static HibernateTemplate hibernateTemplate;
	
	private static int stairmonths;
	private static String stardate;
	private static String enddate;
	
	public static Map<String, Object> CalculateGasMoney(double GasNum, Map<String, Object> map)
	{
		String stairtype = map.get("f_stairtype") == null ? "未设" : map.get("f_stairtype").toString();
		BigDecimal sumamont = new BigDecimal(0);
		BigDecimal stair1num = new BigDecimal(0);
		BigDecimal stair1fee = new BigDecimal(0);
		BigDecimal chargenum = new BigDecimal(0);
		BigDecimal stair2num = new BigDecimal(0);
		BigDecimal stair2fee = new BigDecimal(0);
		BigDecimal stair3num = new BigDecimal(0);
		BigDecimal stair3fee = new BigDecimal(0);
		BigDecimal stair4fee = new BigDecimal(0);
		BigDecimal stair4num = new BigDecimal(0);
		BigDecimal gas = new BigDecimal(GasNum);
		BigDecimal gasprice = new BigDecimal(map.get("f_gasprice") == null ? "0" : map.get("f_gasprice").toString());
		BigDecimal stair1amount = new BigDecimal(map.get("f_stair1amount") == null ? "0" :map.get("f_stair1amount").toString());
		BigDecimal stair1price = new BigDecimal(map.get("f_stair1price") == null ? "0" : map.get("f_stair1price").toString());
		BigDecimal stair2amount = new BigDecimal(map.get("f_stair2amount") == null ? "0" : map.get("f_stair2amount").toString());
		BigDecimal stair2price = new BigDecimal(map.get("f_stair2price") == null ? "0" : map.get("f_stair2price").toString());
		BigDecimal stair3amount = new BigDecimal(map.get("f_stair3amount") == null ? "0" : map.get("f_stair3amount").toString());
		BigDecimal stair3price = new BigDecimal(map.get("f_stair3price") == null ? "0" : map.get("f_stair3price").toString());
		BigDecimal stair4price = new BigDecimal(map.get("f_stair4price") == null ? "0" : map.get("f_stair3price").toString());
		Map<String, Object> StairPriceMap = new HashMap<String, Object>();
		try {
			CountDate();
			if (!stairtype.equals("未设")) {
				final String gassql = " select isnull(sum(oughtamount),0)oughtamount from t_handplan "
						+ "where f_userid='"
						+ map.get("f_userid").toString()
						+ "' and lastinputdate>='"
						+ stardate + "' and lastinputdate<='" + enddate + "'";
				List<Map<String, Object>> gaslist = (List<Map<String, Object>>) hibernateTemplate
						.execute(new HibernateCallback() {
							public Object doInHibernate(Session session)
									throws HibernateException {
								Query q = session.createSQLQuery(gassql);
								q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
								List result = q.list();
								return result;
							}
						});
				Map<String, Object> gasmap = (Map<String, Object>) gaslist.get(0);
				// 当前购气量
				sumamont = new BigDecimal(gasmap.get("oughtamount").toString());
				// 累计购气量
				BigDecimal allamont = sumamont.add(gas);
				// 当前购气量在第一阶梯
				if (sumamont.compareTo(stair1amount) < 0) {
					if (allamont.compareTo(stair1amount) < 0) {
						stair1num = gas;
						stair1fee = gas.multiply(stair1price);
						chargenum = gas.multiply(stair1price);
					} else if (allamont.compareTo(stair1amount) >= 0
							&& allamont.compareTo(stair2amount) < 0) {
						stair1num = stair1amount.subtract(sumamont);
						stair1fee = (stair1amount.subtract(sumamont))
								.multiply(stair1price);
						stair2num = allamont.subtract(stair1amount);
						stair2fee = (allamont.subtract(stair1amount))
								.multiply(stair2price);
						chargenum = stair1fee.add(stair2fee);
					} else if (allamont.compareTo(stair2amount) >= 0
							&& allamont.compareTo(stair3amount) < 0) {
						stair1num = stair1amount.subtract(sumamont);
						stair1fee = (stair1amount.subtract(sumamont))
								.multiply(stair1price);
						stair2num = stair2amount.subtract(stair1amount);
						stair2fee = (stair2amount.subtract(stair1amount))
								.multiply(stair2price);
						stair3num = allamont.subtract(stair2amount);
						stair3fee = (allamont.subtract(stair2amount))
								.multiply(stair3price);
						chargenum = stair1fee.add(stair2fee).add(stair3fee);
					} else if (allamont.compareTo(stair3amount) >= 0) {
						stair1num = stair1amount.subtract(sumamont);
						stair1fee = (stair1amount.subtract(sumamont))
								.multiply(stair1price);
						stair2num = stair2amount.subtract(stair1amount);
						stair2fee = (stair2amount.subtract(stair1amount))
								.multiply(stair2price);
						stair3num = stair3amount.subtract(stair2amount);
						stair3fee = (stair3amount.subtract(stair2amount))
								.multiply(stair3price);
						stair4num = allamont.subtract(stair3amount);
						stair4fee = (allamont.subtract(stair3amount))
								.multiply(stair4price);
						chargenum = stair1fee.add(stair2fee).add(stair3fee)
								.add(stair4fee);
					}
					// 当前已购气量在阶梯二内
				} else if (sumamont.compareTo(stair1amount) >= 0
						&& sumamont.compareTo(stair2amount) < 0) {
					if (allamont.compareTo(stair2amount) < 0) {
						stair2num = gas;
						stair2fee = gas.multiply(stair2price);
						chargenum = stair2fee;
					} else if (allamont.compareTo(stair2amount) >= 0
							&& allamont.compareTo(stair3amount) < 0) {
						stair2num = stair2amount.subtract(sumamont);
						stair2fee = (stair2amount.subtract(sumamont))
								.multiply(stair2price);
						stair3num = allamont.subtract(stair2amount);
						stair3fee = (allamont.subtract(stair2amount))
								.multiply(stair3price);
						chargenum = stair2fee.add(stair3fee);
					} else {
						stair2num = stair2amount.subtract(sumamont);
						stair2fee = (stair2amount.subtract(sumamont))
								.multiply(stair2price);
						stair3num = stair3amount.subtract(stair2amount);
						stair3fee = (stair3amount.subtract(stair2amount))
								.multiply(stair3price);
						stair4num = allamont.subtract(stair3amount);
						stair4fee = (allamont.subtract(stair3amount))
								.multiply(stair4price);
						chargenum = stair2fee.add(stair3fee).add(stair4fee);
					}
					// 当前已购气量在阶梯三内
				} else if (sumamont.compareTo(stair2amount) >= 0
						&& sumamont.compareTo(stair3amount) < 0) {
					if (allamont.compareTo(stair3amount) < 0) {
						stair3num = gas;
						stair3fee = gas.multiply(stair3price);
						chargenum = stair3fee;
					} else {
						stair3num = stair3amount.subtract(sumamont);
						stair3fee = (stair3amount.subtract(sumamont))
								.multiply(stair3price);
						stair4num = allamont.subtract(stair3amount);
						stair4fee = (allamont.subtract(stair3amount))
								.multiply(stair4price);
						chargenum = stair3fee.add(stair4fee);
					}
					// 当前已购气量超过阶梯三
				} else if (sumamont.compareTo(stair3amount) >= 0) {
					stair4num = gas;
					stair4fee = gas.multiply(stair4price);
					chargenum = stair4fee;
				}
			} else {
				chargenum = gas.multiply(gasprice);
				stair1num = new BigDecimal(0);
				stair2num = new BigDecimal(0);
				stair3num = new BigDecimal(0);
				stair4num = new BigDecimal(0);
				stair1fee = new BigDecimal(0);
				stair2fee = new BigDecimal(0);
				stair3fee = new BigDecimal(0);
				stair4fee = new BigDecimal(0);
			}
			
			StairPriceMap = new HashMap<String, Object>();
			StairPriceMap.put("chargenum", chargenum);
			StairPriceMap.put("stair1num", stair1num);
			StairPriceMap.put("stair2num", stair2num);
			StairPriceMap.put("stair3num", stair3num);
			StairPriceMap.put("stair4num", stair4num);
			StairPriceMap.put("stair1fee", stair1fee);
			StairPriceMap.put("stair2fee", stair2fee);
			StairPriceMap.put("stair3fee", stair3fee);
			StairPriceMap.put("stair4fee", stair4fee);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		return StairPriceMap;
	}
	
	// 计算开始时间方法
	private static void CountDate() {
		// 计算当前月在哪个阶梯区间
		Calendar cal = Calendar.getInstance();
		int thismonth = cal.get(Calendar.MONTH) + 1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (stairmonths == 1) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		} else if (stairmonths == 0) {
			stardate = "";
			enddate = "";
		} else {
			/*
			 * 阶梯起始月数计算起始月 = 当前月/阶梯月数*阶梯月数+1结束月 = 当前月/阶梯月数*阶梯月数+阶梯月数注：该运算
			 * 当前月是12月时则需要剪1 上面已经算出阶梯月数为1个月时的金额一下运算阶梯月数至少为两个月 所以对算区间没有影响
			 */
			if (thismonth == 12) {
				thismonth = 11;
			}
			// 计算起始月
			int star = Math.round(thismonth / stairmonths) * stairmonths + 1;
			// 计算结束月
			int end = Math.round(thismonth / stairmonths) * stairmonths
					+ stairmonths;
			// 获得起始日期和结束日期
			cal.set(Calendar.MONTH, star - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.MONTH, end - 1);
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		}
	}
}
