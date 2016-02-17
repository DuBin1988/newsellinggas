package com.aote.rs.charge;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.helper.StringHelper;

@Path("StairPriceDetial")
@Scope("prototype")
@Component
public class StairPrice {
	static Logger log = Logger.getLogger(StairPrice.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;

	private String stairtype;
	private double gasprice;
	private int stair1amount;
	private int stair2amount;
	private int stair3amount;
	private int stair1price;
	private int stair2price;
	private int stair3price;
	private int stair4price;
	private int stairmonths;
	private int zhye;
	private String yydate;
	private String stardate;
	private String enddate;
	private int chargenum;// 交易金额
	private double OrdinaryNum; // 平价气累计购气量
	private double MaxOrdinaryNum = 0; // 本月最大平价气累计购气量限购
	private double TheMaxOrdinaryNum = 0; // 本次可购买最大平价气量
	private double f_OrdinaryNum; // 平价气累计购气量
	private int stair1amount1;//可买阶梯量
	private int stair2amount1;
	private int stair3amount1;
	
	@GET
	@Path("/num/{payfeetype}/{userid}/{pregas}/{usertype}")
	public JSONObject DealPrice(
			@PathParam("payfeetype") Integer payfeetype, // 1是发卡售气，2是卡表收费
			@PathParam("userid") String userid,
			@PathParam("pregas") int pregas,
			@PathParam("usertype") String usertype) {
		JSONObject obj = new JSONObject();
		try {
			Calendar now = Calendar.getInstance();
			Integer Current = now.get(Calendar.MONTH) + 1; // 获取当前月
			Integer Year = now.get(Calendar.YEAR); // 获取当前年

			GetStairInfo(userid); // 获取阶梯信息
			if (!"未设".equals(stairtype)) { // 有阶梯气价信息

				// 获取总购气量
				int gas = getAlreadyGas(userid);

				// 获取第一次购气年月
				Map firstDate = getFirstDate(userid);
				int year = (Integer) firstDate.get("year");
				int month = (Integer) firstDate.get("month");
				stardate = year + "-" + month;
				// 获取当前年月
				Calendar nows = Calendar.getInstance();
				int nowYear = nows.get(Calendar.YEAR);
				int nowMonth = nows.get(Calendar.MONTH) + 1;
				enddate = nowYear + "-" + nowMonth;
				// 如果是当前是12月，总月数=12-起始月份+1//
				int months = 0;//可购买的月数
				if (1 == payfeetype && gas<=0 && month != 12) {//该户第一表第一次发卡售气并且不在12月
					months = 2;
				}else if(1 == payfeetype && gas<=0 && month == 12){//第一次发卡售气为12月
					months = 1;
				}else{//售气
					if(year == nowYear){//今年已经发过卡再购气
						if(nowMonth==12)
							months = nowMonth - month + 1;
						else
							months = nowMonth - month + 2;
					}else{//去年发过卡并且购过气,可购买下一个月时的月数气量
						stardate = nowYear + "-01";//去年购过气则今年从1月开始
						if(nowMonth==12)//
							months = 12;
						else
							months = nowMonth + 1;
					}
				}
				Map<String, Object> usermap = getNowYearGas(userid,nowYear+"");//已购买的平价气量
				
				// 各阶段阶梯气量=月数*每月值
				stair1amount1 = stair1amount * months;
				stair2amount1 = (stair2amount - stair1amount) * months;
				stair3amount1 = (stair3amount - stair2amount - stair1amount) * months;

				// 调用划价函数传进去购气量，今年已购平价气量
				obj = CalculateStair(pregas, usermap); // 调用划价函数
			} else {// 没有阶梯信息直接抛出异常
				return (JSONObject) JSONObject.NULL;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		return obj;
	}

	// 按户方式获取已购气量
	private int getAlreadyGas(String f_userid) {
		final String sql = "select isnull(sum(f_pregas),0) gas from t_sellinggas where" +
		" f_payfeevalid='有效' and (f_payfeetype='发卡售气' or f_payfeetype='卡表收费' or f_payfeetype='超用收费')"
		+ " and f_userid in" 
		+ " (select u1.f_userid from t_userfiles u1 left join t_userfiles u2 on u1.f_userinfoid=u2.f_userinfoid where u2.f_userid = '"
		+ f_userid + "')";
		List<Map<String, Object>> userlist = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(sql);
						q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});
		Map<String, Object> usermap = (Map<String, Object>) userlist.get(0);
		int ret = (int)Double.parseDouble(usermap.get("gas").toString());
		return ret;
	}

	// 按户方式获取今年已购平价气量
	private Map<String, Object> getNowYearGas(String f_userid,String year) {
		final String sql = "select isnull(sum(f_stair1amount),0) f_stair1amount,isnull(sum(f_stair2amount),0) f_stair2amount from t_sellinggas where" +
		" f_payfeevalid='有效' and (f_payfeetype='发卡售气' or f_payfeetype='卡表收费' or f_payfeetype='超用收费')"
		+ " and Substring( CONVERT(varchar(12), f_deliverydate, 111 ),1,4) >= '" + year + "'" + " and f_userid in" 
		+ " (select u1.f_userid from t_userfiles u1 left join t_userfiles u2 on u1.f_userinfoid=u2.f_userinfoid where u2.f_userid = '"
		+ f_userid + "')";
		List<Map<String, Object>> userlist = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(sql);
						q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});
		Map<String, Object> usermap = (Map<String, Object>) userlist.get(0);
		return usermap;
	}

	// 按户方式获取第一次购气年月, 没有，按当前日期算，返回yyyy-mm-dd的日期格式
	private Map getFirstDate(String f_userid) {
		Map result = new HashMap();

		// 该户第一次购气时间为建档时间
		final String usersql = "select MIN(f_deliverydate) f_yydate from t_sellinggas where"
			+ " f_payfeevalid='有效' and (f_payfeetype='发卡售气' or f_payfeetype='卡表收费' or f_payfeetype='超用收费')"
				+ " and f_userid in "
				+ " (select f_userid from t_userfiles where f_userinfoid="
				+ " (select f_userinfoid from t_userfiles where f_userid='"
				+ f_userid + "'))";

		List<Map<String, Object>> userlist = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(usersql);
						q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});

		// 不存在第一次购气日期，按当前时间计算
		Map<String, Object> yydatemap = (Map<String, Object>) userlist.get(0);
		if(yydatemap.get("f_yydate")==null){
			Calendar nows = Calendar.getInstance();
			result.put("year", nows.get(Calendar.YEAR));
			result.put("month", nows.get(Calendar.MONTH) + 1);	
		}else{
			Map<String, Object> usermap = (Map<String, Object>) userlist.get(0);
			String ret = usermap.get("f_yydate").toString();
			result.put("year", Integer.parseInt(ret.substring(0, 4)));
			result.put("month", Integer.parseInt(ret.substring(5, 7)));
		}
		return result;
	}

	// 获取用户阶梯气价信息
	private void GetStairInfo(String userid) {
		try {
			// 查出该用户阶梯气价信息
			final String usersql = "select isnull(um.f_stairtype,'未设')f_stairtype, isnull(um.f_gasprice,0)f_gasprice, isnull(um.f_stair1amount,0)f_stair1amount,isnull(um.f_stair2amount,0)f_stair2amount,"
					+ "isnull(um.f_stair3amount,0)f_stair3amount,isnull(um.f_stair1price,0)*100 f_stair1price,"
					+ "isnull(um.f_stair2price,0)*100 f_stair2price,isnull(um.f_stair3price,0)*100 f_stair3price,isnull(um.f_stair4price,0)*100 f_stair4price,"
					+ "isnull(um.f_stairmonths,0)f_stairmonths,Substring( CONVERT(varchar(12), uo.f_yytdate, 111 ),1,10) f_yytdate,isnull(uo.f_zhye,0)* 100 f_zhye "
					+ " from t_userfiles um left join t_userinfo uo on um.f_userinfoid = uo.f_userid where um.f_userid='"
					+ userid + "'";
			List<Map<String, Object>> userlist = (List<Map<String, Object>>) hibernateTemplate
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							Query q = session.createSQLQuery(usersql);
							q
									.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
							List result = q.list();
							return result;
						}
					});
			if (userlist.size() != 1) {
				// 查询到多条数据，抛出异常
				throw new WebApplicationException(500);
			}
			Map<String, Object> usermap = (Map<String, Object>) userlist.get(0);
			stairtype = usermap.get("f_stairtype").toString();
			gasprice = Double.parseDouble(usermap.get("f_gasprice").toString());
			stair1amount = (int)Double.parseDouble(usermap.get("f_stair1amount")
					.toString());
			stair2amount = (int)Double.parseDouble(usermap.get("f_stair2amount")
					.toString());
			stair3amount = (int)Double.parseDouble(usermap.get("f_stair3amount")
					.toString());
			stair1price = (int)Double.parseDouble(usermap.get("f_stair1price")
					.toString());
			stair2price = (int)Double.parseDouble(usermap.get("f_stair2price")
					.toString());
			stair3price = (int)Double.parseDouble(usermap.get("f_stair3price")
					.toString());
			stair4price = (int)Double.parseDouble(usermap.get("f_stair4price")
					.toString());
			stairmonths = (int)Double.parseDouble(usermap.get("f_stairmonths")
					.toString());
			zhye = (int)Double.parseDouble(usermap.get("f_zhye").toString());

		} catch (Exception e) {
			log.debug("查询用户阶梯信息失败" + e.getMessage());
			throw new WebApplicationException(500);
		}
	}

	// 划价, pregas - 预购气量, gas - 已购气量
	private JSONObject CalculateStair(int gas, Map<String, Object> usermap) {
		double chargeamont = 0;
		int stair1num = 0;
		int stair2num = 0;
		int stair3num = 0;
		int stair4num = 0;
		int stair1fee = 0;
		int stair2fee = 0;
		int stair3fee = 0;
		int stair4fee = 0;
		try {
			//阶梯已购气量
			int stair1gas = (int)Double.parseDouble(usermap.get("f_stair1amount").toString());
			int stair2gas = (int)Double.parseDouble(usermap.get("f_stair2amount").toString());
			//买气时可买阶梯量-当年已买阶梯量
			int canBuyGas1 = stair1amount1 - stair1gas <= 0 ? 0 : stair1amount1 - stair1gas;
			int canBuyGas2 = stair2amount1 - stair2gas <= 0 ? 0 : stair2amount1 - stair2gas;
			// 第一阶梯满足
			if (canBuyGas1 >= gas) {//
				stair1num = gas;
				stair1fee = stair1num * stair1price;
				chargenum = stair1fee;
			}else if ((canBuyGas1 + canBuyGas2) >= gas) {// 第二阶梯
				stair1num = canBuyGas1;
				stair1fee = stair1num * stair1price;
				stair2num = gas - canBuyGas1;
				stair2fee = stair2num * stair2price;
				chargenum = stair1fee + stair2fee;
			} else {
				stair1num = canBuyGas1;
				stair1fee = stair1num * stair1price;
				stair2num = canBuyGas2;
				stair2fee = canBuyGas2 * stair2price;
				stair3num = gas - canBuyGas1 - canBuyGas2;
				stair3fee = stair3num * stair3price;
				chargenum = stair1fee + stair2fee + stair3fee;
			}
			Map sell = new HashMap();
			sell.put("f_stair1amount", stair1num);
			sell.put("f_stair2amount", stair2num);
			sell.put("f_stair3amount", stair3num);
			sell.put("f_stair4amount", stair4num);
			sell.put("f_stair1fee", this.toDouble(stair1fee));
			sell.put("f_stair2fee", this.toDouble(stair2fee));
			sell.put("f_stair3fee", this.toDouble(stair3fee));
			sell.put("f_stair4fee", this.toDouble(stair4fee));
			sell.put("f_stair1price", this.toDouble(stair1price));
			sell.put("f_stair2price", this.toDouble(stair2price));
			sell.put("f_stair3price", this.toDouble(stair3price));
			sell.put("f_stair4price", this.toDouble(stair4price));
			sell.put("f_allamont", stair1gas);//当年已购买气量1阶
			sell.put("f_allamont1", stair2gas);//当年已购买气量1阶
			sell.put("f_chargenum", this.toDouble(chargenum));
			sell.put("f_stardate", stardate+"   ");
			sell.put("f_enddate", enddate+"   ");
			sell.put("f_totalcost", this.toDouble(chargenum - zhye));
			sell.put("TheMaxOrdinaryNum", canBuyGas1); // 当前最大可买1阶气量
			sell.put("TheMaxOrdinaryNum1", canBuyGas2); // 当前最大可买2阶气量
			sell.put("AllGasNum1", stair1amount1); //当前最大可购买总气量1阶
			sell.put("AllGasNum2", stair2amount1); //当前最大可购买总气量2阶
//			sell.put("OrdinaryNum", OrdinaryNum); // 已用平价气
//			sell.put("MaxNum", 12 * stair1amount); // 本年度平价气总额度
			JSONObject objj = new JSONObject();
			objj = MapToJson(sell);
			return objj;
		} catch (Exception e) {
			// TODO: handle exception
			log.debug("划价失败" + e.getMessage());
			throw new WebApplicationException(500);
		}
	}

	/**
	 * 除以100.0转两位小数
	 * 
	 * @param map
	 * @return
	 */
	private double toDouble(int num) {
		return num / 100.0;
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
