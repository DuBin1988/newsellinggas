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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aote.helper.StringHelper;
import com.aote.listener.ContextListener;
import com.aote.rs.charge.countdate.ICountDate;

@Path("charge")
@Scope("prototype")
@Component
/**
 * �����շ�ҵ�������������
 */
public class CardCharge {

	static Logger log = Logger.getLogger(CardCharge.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	private String stairtype;
	private double gasprice;
	private double stair1amount;
	private double stair2amount;
	private double stair3amount;
	private double stair1price;
	private double stair2price;
	private double stair3price;
	private double stair4price;
	private int stairmonths;
	private double zhye;

	private String stardate;
	private String enddate;

	private double sumamont;

	@GET
	@Path("/num/{userid}/{pregas}")
	public JSONObject pregas(@PathParam("userid") String userid,
			@PathParam("pregas") double pregas) {
		JSONObject obj = new JSONObject();
		try {
			double chargenum = 0;
			double stair1num = 0;
			double stair2num = 0;
			double stair3num = 0;
			// һ����ʣ��ɹ�
			double stair1surplus = 0;
			// ������ʣ��ɹ�
			double stair2surplus = 0;
			// ������ʣ��ɹ�
			double stair3surplus = 0;
			double stair4num = 0;
			double stair1fee = 0;
			double stair2fee = 0;
			double stair3fee = 0;
			double stair4fee = 0;
			SearchStair(userid);
			// ������ý������۵��û�����
			if (!stairtype.equals("δ��")) {
				CountAmount(userid);
				// �ۼƹ�����
				double allamont = sumamont + pregas;
				// ��ǰ�������ڵ�һ����
				if (sumamont < stair1amount) {
					if (allamont < stair1amount) {
						stair1surplus = stair1amount - allamont;
						stair2surplus = stair2amount-stair1amount;
						stair3surplus = stair3amount-stair2amount;
						stair1num = pregas;
						stair1fee = pregas * stair1price;
						chargenum = pregas * stair1price;
					} else if (allamont >= stair1amount
							&& allamont < stair2amount) {
						stair2surplus = stair2amount - allamont;
						stair3surplus = stair3amount-stair2amount;
						stair1num = stair1amount - sumamont;
						stair1fee = (stair1amount - sumamont) * stair1price;
						stair2num = allamont - stair1amount;
						stair2fee = (allamont - stair1amount) * stair2price;
						chargenum = stair1fee + stair2fee;
					} else if (allamont >= stair2amount
							&& allamont < stair3amount) {
						stair3surplus = stair3amount - allamont;
						stair1num = stair1amount - sumamont;
						stair1fee = (stair1amount - sumamont) * stair1price;
						stair2num = stair2amount - stair1amount;
						stair2fee = (stair2amount - stair1amount) * stair2price;
						stair3num = allamont - stair2amount;
						stair3fee = (allamont - stair2amount) * stair3price;
						chargenum = stair1fee + stair2fee + stair3fee;
					} else if (allamont >= stair3amount) {
						stair1num = stair1amount - sumamont;
						stair1fee = (stair1amount - sumamont) * stair1price;
						stair2num = stair2amount - stair1amount;
						stair2fee = (stair2amount - stair1amount) * stair2price;
						stair3num = stair3amount - stair2amount;
						stair3fee = (stair3amount - stair2amount) * stair3price;
						stair4num = allamont - stair3amount;
						stair4fee = (allamont - stair3amount) * stair4price;
						chargenum = stair1fee + stair2fee + stair3fee
								+ stair4fee;
					}
					// ��ǰ�ѹ������ڽ��ݶ���
				} else if (sumamont >= stair1amount && sumamont < stair2amount) {
					if (allamont < stair2amount) {
						stair2surplus = stair2amount - allamont;
						stair3surplus = stair3amount-stair2amount;
						stair2num = pregas;
						stair2fee = pregas * stair2price;
						chargenum = stair2fee;
					} else if (allamont >= stair2amount
							&& allamont < stair3amount) {
						stair3surplus = stair3amount - allamont;
						stair2num = stair2amount - sumamont;
						stair2fee = (stair2amount - sumamont) * stair2price;
						stair3num = allamont - stair2amount;
						stair3fee = (allamont - stair2amount) * stair3price;
						chargenum = stair2fee + stair3fee;
					} else {
						stair2num = stair2amount - sumamont;
						stair3surplus = stair3amount-stair2amount;
						stair2fee = (stair2amount - sumamont) * stair2price;
						stair3num = stair3amount - stair2amount;
						stair3fee = (stair3amount - stair2amount) * stair3price;
						stair4num = allamont - stair3amount;
						stair4fee = (allamont - stair3amount) * stair4price;
						chargenum = stair2fee + stair3fee + stair4fee;
					}
					// ��ǰ�ѹ������ڽ�������
				} else if (sumamont >= stair2amount && sumamont < stair3amount) {
					if (allamont < stair3amount) {
						stair3surplus = stair3amount - allamont;
						stair3num = pregas;
						stair3fee = pregas * stair3price;
						chargenum = stair3fee;
					} else {
						stair3num = stair3amount - sumamont;
						stair3fee = (stair3amount - sumamont) * stair3price;
						stair4num = allamont - stair3amount;
						stair4fee = (allamont - stair3amount) * stair4price;
						chargenum = stair3fee + stair4fee;
					}
					// ��ǰ�ѹ���������������
				} else if (sumamont >= stair3amount) {
					stair4num = pregas;
					stair4fee = pregas * stair4price;
					chargenum = stair4fee;
				}

				// ���û�δ���ý�������
			} else {
				chargenum = pregas * gasprice;
			}
			Map sell = new HashMap();
			sell.put("f_stair1amount", stair1num);
			sell.put("f_stair2amount", stair2num);
			sell.put("f_stair3amount", stair3num);
			sell.put("f_stair4amount", stair4num);
			sell.put("f_stair1surplus", stair1surplus);
			sell.put("f_stair2surplus", stair2surplus);
			sell.put("f_stair3surplus", stair3surplus);
			sell.put("f_stair1fee", stair1fee);
			sell.put("f_stair2fee", stair2fee);
			sell.put("f_stair3fee", stair3fee);
			sell.put("f_stair4fee", stair4fee);
			sell.put("f_stair1price", stair1price);
			sell.put("f_stair2price", stair2price);
			sell.put("f_stair3price", stair3price);
			sell.put("f_stair4price", stair4price);
			sell.put("f_allamont", sumamont);
			sell.put("f_chargenum", chargenum);
			sell.put("f_stardate", stardate);
			sell.put("f_enddate", enddate);
			sell.put("f_totalcost", chargenum - zhye);
			obj = MapToJson(sell);
			return obj;
		} catch (Exception e) {
			log.debug("�շ�ʧ��" + e.getMessage());
			throw new WebApplicationException(500);
		}

	}

	// ������
	@GET
	@Path("/fee/{userid}/{prefee}")
	public JSONObject prefee(@PathParam("userid") String userid,
			@PathParam("prefee") double prefee) {
		JSONObject obj = new JSONObject();
		try {
			double chargeamont = 0;
			double stair1num = 0;
			double stair2num = 0;
			double stair3num = 0;
			double stair4num = 0;
			double stair1fee = 0;
			double stair2fee = 0;
			double stair3fee = 0;
			double stair4fee = 0;

			SearchStair(userid);
			prefee += zhye;
			// ������ý������۵��û�����
			if (!stairtype.equals("δ��")) {
				// ��ѯ�����ܹ�����
				CountAmount(userid);
				// ��ǰ�������ڵ�һ����
				if (sumamont < stair1amount) {
					// �׶�һʣ�������Ľ����ڱ��ι������ ֱ�Ӱ�����һ�ļ۸��������
					if ((stair1amount - sumamont) * stair1price > prefee) {
						stair1num = prefee / stair1price;
						stair1fee = prefee;
						chargeamont = stair1num;
						// ��ǰ�����������Ӧ��������������һ
					} else {
						// �ȼ�����׶�һ�������ͽ��
						stair1num = stair1amount - sumamont;
						stair1fee = stair1num * stair1price;
						// ��ǰ��������Ӧ������δ�������ݶ�ʱ ��������ͽ��
						if ((prefee - stair1fee) / stair2price < stair2amount
								- stair1amount) {
							stair2num = (prefee - stair1fee) / stair2price;
							stair2fee = prefee - stair1fee;
							chargeamont = stair1num + stair2num;
							// ��ǰ��������Ӧ�������������ݶ�
						} else {
							// ������ݶ��������ͽ��
							if ((prefee - stair2fee) / stair2price < stair3amount
									- stair2amount) {
								stair2num = stair2amount - stair1amount;
								stair2fee = stair2num * stair2price;
								stair3num = (prefee - stair2fee - stair1fee)
										/ stair3price;
								stair3fee = prefee - stair2fee - stair1fee;
								chargeamont = stair1num + stair2num + stair3num;
							} else {
								stair2num = stair2amount - stair1amount;
								stair2fee = stair2num * stair2price;
								stair3num = stair3amount - stair2amount;
								stair3fee = stair3num * stair3price;
								stair4num = (prefee - stair3fee - stair2fee - stair1fee)
										/ stair4price;
								stair4fee = prefee - stair3fee - stair2fee
										- stair1fee;
								chargeamont = stair1num + stair2num + stair3num
										+ stair4num;
							}
						}
					}
					// ��ǰ�ѹ������ڽ��ݶ���
				} else if (sumamont >= stair1amount && sumamont < stair2amount) {
					// �׶ζ�ʣ�������Ľ����ڱ��ι������ ֱ�Ӱ����ݶ��ļ۸��������
					if ((stair2amount - sumamont) * stair2price > prefee) {
						stair2num = prefee / stair2price;
						stair2fee = prefee;
						chargeamont = stair2num;
						// ��ǰ�����������Ӧ�������������ݶ�
					} else {
						// �ȼ�����׶ζ��������ͽ��
						stair2num = stair2amount - sumamont;
						stair2fee = stair2num * stair2price;
						// ��ǰ��������Ӧ������δ����������ʱ ��������ͽ��
						if ((prefee - stair2fee) / stair3price < stair3amount
								- stair2amount) {
							stair3num = (prefee - stair2fee) / stair3price;
							stair3fee = prefee - stair2fee;
							chargeamont = stair2num + stair3num;
						} else {
							stair3num = stair3amount - stair2amount;
							stair3fee = stair3num * stair3price;
							stair4num = (prefee - stair3fee - stair2fee)
									/ stair4price;
							stair4fee = prefee - stair3fee - stair2fee;
							chargeamont = stair2num + stair3num + stair4num;
						}
					}
					// ��ǰ�ѹ������ڽ�������
				} else if (sumamont >= stair2amount && sumamont < stair3amount) {
					// �׶���ʣ�������Ľ����ڱ��ι������ ֱ�Ӱ��׶����ļ۸��������
					if ((stair3amount - sumamont) * stair3price > prefee) {
						stair3num = prefee / stair3price;
						stair3fee = prefee;
						chargeamont = stair3num;
						// ��ǰ�����������Ӧ����������������
					} else {
						// �ȼ�����׶����������ͽ��
						stair3num = stair3amount - sumamont;
						stair3fee = stair3num * stair3price;
						stair4num = (prefee - stair3fee) / stair4price;
						stair4fee = prefee - stair3fee;
						chargeamont = stair3num + stair4num;

					}
					// ��ǰ�ѹ���������������
				} else if (sumamont >= stair3amount) {
					stair4num = prefee / stair4price;
					stair4fee = prefee;
					chargeamont = stair4num;
				}

				// ���û�δ���ý�������
			} else {
				chargeamont = prefee / gasprice;
			}
			Map sell = new HashMap();
			sell.put("f_stair1amount", stair1num);
			sell.put("f_stair2amount", stair2num);
			sell.put("f_stair3amount", stair3num);
			sell.put("f_stair4amount", stair4num);
			sell.put("f_stair1fee", stair1fee);
			sell.put("f_stair2fee", stair2fee);
			sell.put("f_stair3fee", stair3fee);
			sell.put("f_stair4fee", stair4fee);
			sell.put("f_stair1price", stair1price);
			sell.put("f_stair2price", stair2price);
			sell.put("f_stair3price", stair3price);
			sell.put("f_stair4price", stair4price);
			sell.put("f_allamont", sumamont);
			sell.put("chargeamont", chargeamont);
			sell.put("f_stardate", stardate);
			sell.put("f_enddate", enddate);
			sell.put("f_preamount", prefee);
			obj = MapToJson(sell);
		} catch (Exception e) {
			log.debug("�շ�ʧ��" + e.getMessage());
			throw new WebApplicationException(500);
		}
		return obj;
	}

	// ��ѯ�û�����������Ϣ �û���������Ľ���
	private void SearchStair(String userid) {
		try {
			// ������û�����������Ϣ
			final String usersql = "select isnull(uo.f_stairtype,'δ��')f_stairtype, isnull(uo.f_gasprice,0)f_gasprice, isnull(uo.f_stair1amount,0)f_stair1amount,isnull(uo.f_stair2amount,0)f_stair2amount,"
					+ "isnull(uo.f_stair3amount,0)f_stair3amount,isnull(uo.f_stair1price,0) f_stair1price,"
					+ "isnull(uo.f_stair2price,0) f_stair2price,isnull(uo.f_stair3price,0) f_stair3price,isnull(uo.f_stair4price,0) f_stair4price,"
					+ "isnull(uo.f_stairmonths,0)f_stairmonths,Substring( CONVERT(varchar(12), uo.f_yytdate, 111 ),1,10) f_yytdate,isnull(uo.f_zhye,0)* 100 f_zhye "
					+ " from t_userfiles um left join t_userinfo uo on um.f_userinfoid = uo.f_userid where um.f_userid='"
					+ userid + "'";
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
			if (userlist.size() != 1) {
				// ��ѯ���������ݣ��׳��쳣
				throw new WebApplicationException(500);
			}
			Map<String, Object> usermap = (Map<String, Object>) userlist.get(0);
			stairtype = usermap.get("f_stairtype").toString();
			gasprice = Double.parseDouble(usermap.get("f_gasprice").toString());
			stair1amount = Double.parseDouble(usermap.get("f_stair1amount")
					.toString());
			stair2amount = Double.parseDouble(usermap.get("f_stair2amount")
					.toString());
			stair3amount = Double.parseDouble(usermap.get("f_stair3amount")
					.toString());
			stair1price = Double.parseDouble(usermap.get("f_stair1price")
					.toString());
			stair2price = Double.parseDouble(usermap.get("f_stair2price")
					.toString());
			stair3price = Double.parseDouble(usermap.get("f_stair3price")
					.toString());
			stair4price = Double.parseDouble(usermap.get("f_stair4price")
					.toString());
			stairmonths = Integer.parseInt(usermap.get("f_stairmonths")
					.toString());
			zhye = Double.parseDouble(usermap.get("f_zhye").toString());
		} catch (Exception e) {
			log.debug("��ѯ�û�������Ϣʧ��" + e.getMessage());
			throw new WebApplicationException(500);
		}

	}

	// ���㿪ʼʱ�䷽��
	private void CountDate(String userid, HibernateTemplate hibernateTemplate) {
		// �ж��Ƿ������˽ӿڣ������ִ�нӿڣ����û�а�Ĭ�ϼ��㡣
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(ContextListener.getContext());
		if (applicationContext.containsBean("CountDate")) {
			ICountDate icount = (ICountDate) applicationContext
					.getBean("CountDate");
			stardate = icount.startdate(userid, hibernateTemplate);
			enddate = icount.enddate(userid, hibernateTemplate);
			return;
		}
		// ���㵱ǰ�����ĸ���������
		Calendar cal = Calendar.getInstance();
		int thismonth = cal.get(Calendar.MONTH) + 1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (stairmonths == 1) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		} else {
			/*
			 * ������ʼ����������ʼ�� = ��ǰ��/��������*��������+1������ = ��ǰ��/��������*��������+��������ע��������
			 * ��ǰ����12��ʱ����Ҫ��1 �����Ѿ������������Ϊ1����ʱ�Ľ��һ�����������������Ϊ������ ���Զ�������û��Ӱ��
			 */
			if (thismonth == 12) {
				thismonth = 11;
			}
			// ������ʼ��
			int star = Math.round(thismonth / stairmonths) * stairmonths + 1;
			// ���������
			int end = Math.round(thismonth / stairmonths) * stairmonths
					+ stairmonths;
			// �����ʼ���ںͽ�������
			cal.set(Calendar.MONTH, star - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.MONTH, end - 1);
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		}
	}

	// ��ѯ�����ܹ�����
	private void CountAmount(String userid) {
		try {
			// �ȼ��㿪ʼ�ͼ���ʱ��
			CountDate(userid, hibernateTemplate);

			final String gassql = " select count(*) times, sum(isnull(f_pregas,0))f_pregas from t_sellinggas "
					+ "where f_userid in (select f_userid from t_userfiles   where f_userinfoid=(select f_userinfoid from t_userfiles where f_userid='"
					+ userid
					+ "')) and f_deliverydate>='"
					+ stardate + "' and f_deliverydate<='" + enddate + "'";
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
			// ��ǰ������
			if (Double.parseDouble(gasmap.get("times").toString()) == 0) {
				sumamont = 0;
			} else {
				sumamont = Double
						.parseDouble(gasmap.get("f_pregas").toString());
			}
		} catch (Exception e) {
			log.debug("��ѯ�����ܹ�����ʧ��" + e.getMessage());
			throw new WebApplicationException(500);
		}
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
