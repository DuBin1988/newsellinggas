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
	private int chargenum;// ���׽��
	private double OrdinaryNum; // ƽ�����ۼƹ�����
	private double MaxOrdinaryNum = 0; // �������ƽ�����ۼƹ������޹�
	private double TheMaxOrdinaryNum = 0; // ���οɹ������ƽ������
	private double f_OrdinaryNum; // ƽ�����ۼƹ�����
	private int stair1amount1;//���������
	private int stair2amount1;
	private int stair3amount1;
	
	@GET
	@Path("/num/{payfeetype}/{userid}/{pregas}/{usertype}")
	public JSONObject DealPrice(
			@PathParam("payfeetype") Integer payfeetype, // 1�Ƿ���������2�ǿ����շ�
			@PathParam("userid") String userid,
			@PathParam("pregas") int pregas,
			@PathParam("usertype") String usertype) {
		JSONObject obj = new JSONObject();
		try {
			Calendar now = Calendar.getInstance();
			Integer Current = now.get(Calendar.MONTH) + 1; // ��ȡ��ǰ��
			Integer Year = now.get(Calendar.YEAR); // ��ȡ��ǰ��

			GetStairInfo(userid); // ��ȡ������Ϣ
			if (!"δ��".equals(stairtype)) { // �н���������Ϣ

				// ��ȡ�ܹ�����
				int gas = getAlreadyGas(userid);

				// ��ȡ��һ�ι�������
				Map firstDate = getFirstDate(userid);
				int year = (Integer) firstDate.get("year");
				int month = (Integer) firstDate.get("month");
				stardate = year + "-" + month;
				// ��ȡ��ǰ����
				Calendar nows = Calendar.getInstance();
				int nowYear = nows.get(Calendar.YEAR);
				int nowMonth = nows.get(Calendar.MONTH) + 1;
				enddate = nowYear + "-" + nowMonth;
				// ����ǵ�ǰ��12�£�������=12-��ʼ�·�+1//
				int months = 0;//�ɹ��������
				if (1 == payfeetype && gas<=0 && month != 12) {//�û���һ���һ�η����������Ҳ���12��
					months = 2;
				}else if(1 == payfeetype && gas<=0 && month == 12){//��һ�η�������Ϊ12��
					months = 1;
				}else{//����
					if(year == nowYear){//�����Ѿ��������ٹ���
						if(nowMonth==12)
							months = nowMonth - month + 1;
						else
							months = nowMonth - month + 2;
					}else{//ȥ�귢�������ҹ�����,�ɹ�����һ����ʱ����������
						stardate = nowYear + "-01";//ȥ�깺����������1�¿�ʼ
						if(nowMonth==12)//
							months = 12;
						else
							months = nowMonth + 1;
					}
				}
				Map<String, Object> usermap = getNowYearGas(userid,nowYear+"");//�ѹ����ƽ������
				
				// ���׶ν�������=����*ÿ��ֵ
				stair1amount1 = stair1amount * months;
				stair2amount1 = (stair2amount - stair1amount) * months;
				stair3amount1 = (stair3amount - stair2amount - stair1amount) * months;

				// ���û��ۺ�������ȥ�������������ѹ�ƽ������
				obj = CalculateStair(pregas, usermap); // ���û��ۺ���
			} else {// û�н�����Ϣֱ���׳��쳣
				return (JSONObject) JSONObject.NULL;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		return obj;
	}

	// ������ʽ��ȡ�ѹ�����
	private int getAlreadyGas(String f_userid) {
		final String sql = "select isnull(sum(f_pregas),0) gas from t_sellinggas where" +
		" f_payfeevalid='��Ч' and (f_payfeetype='��������' or f_payfeetype='�����շ�' or f_payfeetype='�����շ�')"
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

	// ������ʽ��ȡ�����ѹ�ƽ������
	private Map<String, Object> getNowYearGas(String f_userid,String year) {
		final String sql = "select isnull(sum(f_stair1amount),0) f_stair1amount,isnull(sum(f_stair2amount),0) f_stair2amount from t_sellinggas where" +
		" f_payfeevalid='��Ч' and (f_payfeetype='��������' or f_payfeetype='�����շ�' or f_payfeetype='�����շ�')"
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

	// ������ʽ��ȡ��һ�ι�������, û�У�����ǰ�����㣬����yyyy-mm-dd�����ڸ�ʽ
	private Map getFirstDate(String f_userid) {
		Map result = new HashMap();

		// �û���һ�ι���ʱ��Ϊ����ʱ��
		final String usersql = "select MIN(f_deliverydate) f_yydate from t_sellinggas where"
			+ " f_payfeevalid='��Ч' and (f_payfeetype='��������' or f_payfeetype='�����շ�' or f_payfeetype='�����շ�')"
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

		// �����ڵ�һ�ι������ڣ�����ǰʱ�����
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

	// ��ȡ�û�����������Ϣ
	private void GetStairInfo(String userid) {
		try {
			// ������û�����������Ϣ
			final String usersql = "select isnull(um.f_stairtype,'δ��')f_stairtype, isnull(um.f_gasprice,0)f_gasprice, isnull(um.f_stair1amount,0)f_stair1amount,isnull(um.f_stair2amount,0)f_stair2amount,"
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
				// ��ѯ���������ݣ��׳��쳣
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
			log.debug("��ѯ�û�������Ϣʧ��" + e.getMessage());
			throw new WebApplicationException(500);
		}
	}

	// ����, pregas - Ԥ������, gas - �ѹ�����
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
			//�����ѹ�����
			int stair1gas = (int)Double.parseDouble(usermap.get("f_stair1amount").toString());
			int stair2gas = (int)Double.parseDouble(usermap.get("f_stair2amount").toString());
			//����ʱ���������-�������������
			int canBuyGas1 = stair1amount1 - stair1gas <= 0 ? 0 : stair1amount1 - stair1gas;
			int canBuyGas2 = stair2amount1 - stair2gas <= 0 ? 0 : stair2amount1 - stair2gas;
			// ��һ��������
			if (canBuyGas1 >= gas) {//
				stair1num = gas;
				stair1fee = stair1num * stair1price;
				chargenum = stair1fee;
			}else if ((canBuyGas1 + canBuyGas2) >= gas) {// �ڶ�����
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
			sell.put("f_allamont", stair1gas);//�����ѹ�������1��
			sell.put("f_allamont1", stair2gas);//�����ѹ�������1��
			sell.put("f_chargenum", this.toDouble(chargenum));
			sell.put("f_stardate", stardate+"   ");
			sell.put("f_enddate", enddate+"   ");
			sell.put("f_totalcost", this.toDouble(chargenum - zhye));
			sell.put("TheMaxOrdinaryNum", canBuyGas1); // ��ǰ������1������
			sell.put("TheMaxOrdinaryNum1", canBuyGas2); // ��ǰ������2������
			sell.put("AllGasNum1", stair1amount1); //��ǰ���ɹ���������1��
			sell.put("AllGasNum2", stair2amount1); //��ǰ���ɹ���������2��
//			sell.put("OrdinaryNum", OrdinaryNum); // ����ƽ����
//			sell.put("MaxNum", 12 * stair1amount); // �����ƽ�����ܶ��
			JSONObject objj = new JSONObject();
			objj = MapToJson(sell);
			return objj;
		} catch (Exception e) {
			// TODO: handle exception
			log.debug("����ʧ��" + e.getMessage());
			throw new WebApplicationException(500);
		}
	}

	/**
	 * ����100.0ת��λС��
	 * 
	 * @param map
	 * @return
	 */
	private double toDouble(int num) {
		return num / 100.0;
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
