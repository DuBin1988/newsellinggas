package com.aote.rs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.exception.ResultException;
import com.aote.rs.util.UserTools;

import sun.security.jca.GetInstance;
import sun.util.resources.CalendarData;

@SuppressWarnings("unused")
@Path("bank")
@Component
public class BankService {
	static Logger log = Logger.getLogger(BankService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	private String usernames = "";

	// 处理返盘数据，data是JSON格式的返盘数据
	@POST
	public String procReturn(String data) {
		JSONObject returnJson = new JSONObject();
		try {
			JSONArray array = new JSONArray(data);
			// 对于data中的每一条数据
			String name = "";
			String bankcard = "";
			for (int i = 0; i < array.length(); i++) {
				JSONObject oper = array.getJSONObject(i);
				JSONObject object = oper.getJSONObject("data");
				// 获取用户名，银行账号等信息
				String f_usermc = object.getString("f_usermc");
				String paymenstate = object.getString("paymenstate");
				String f_idofcard = object.getString("f_idofcard");
				String f_bankname = object.getString("f_bankname");
				double b_oughtfee = object.getDouble("oughtfee");
				String f_fanpandate = object.getString("f_fanpandate");
				String operatorid = object.getString("operid");
				
				Map map = new HashMap();
				map.put("f_usermc", f_usermc);
				map.put("paymenstate", paymenstate);
				map.put("f_idofcard", f_idofcard);
				map.put("f_bankname", f_bankname);
				map.put("oughtfee", b_oughtfee);
				map.put("f_fanpandate", f_fanpandate);
				map.put("f_returntime", new java.util.Date());
				
				
				Map loginuser = UserTools.getUser(operatorid, this.hibernateTemplate);
				// 分公司
				String f_filiale = loginuser.get("f_fengongsi").toString();
				
				Map<String, Object> userinfo = this.findUserinfo(
						f_filiale,f_idofcard,f_bankname);
				loginuser.put("orgstr", userinfo.get("f_orgstr").toString());
				// 如果扣款成功，找出银行、账号、金额一致，且已送盘，送盘日期小于当前日期的数据
				if (paymenstate.equals("成功")) {
					//查找应缴费抄表记录
					//List l = findHandplans();
					String hql = "select (hu.oughtfee-isnull(o.f_zhye,0)) oughtfee, o.f_userid f_userid, o.f_username f_username, o.f_address f_address, o.f_districtname f_districtname,"
							+ "hu.lastinputgasnum lastinputgasnum, hu.lastrecord lastrecord, o.f_usertype f_usertype,o.f_phone f_phone,hu.f_userid hand,o.f_yytdepa f_yytdepa  from t_userinfo o left join "
							+ "(select sum(t.oughtfee) oughtfee,t.f_userid, u.f_userinfoid f_userinfoid"
							+ ",min(t.lastinputgasnum) lastinputgasnum,max(t.lastrecord) lastrecord"
							+ " from t_handplan t,t_userfiles u where t.users=u.id and u.f_idofcard='"
							+ f_idofcard
							+ "' and u.f_bankname='"
							+ f_bankname
							+ "' "
							+ " and t.f_state='已抄表' and shifoujiaofei='否'"
							+ " and f_sendtime is not null and f_sendtime<getdate() and CONVERT(varchar(12) ,f_sendtime, 120 )>CONVERT(varchar(12) ,f_inputdate, 120 ) group by t.f_userid,u.f_userinfoid,t.f_address,t.f_usertype)hu on o.f_userid = hu.f_userinfoid"
							+ " where o.f_idofcard='"
							+ f_idofcard
							+ "' and o.f_bankname='"
							+ f_bankname
							+ "' and (o.f_userstate = '银行扣款' or o.f_userstate = '正常')";
					// HibernateSQLCall sqlCall = new HibernateSQLCall(hql);
					SessionFactory sf = hibernateTemplate.getSessionFactory();
					Session session = sf.openSession();
					List l = session.createSQLQuery(hql).list();

					// hibernateTemplate.get*/
					// 没有找到，标记该记录状态为无此账号
					if (l.size() == 0) {
						map.put("f_result", "不成功");
						map.put("f_resultnote", "未找到改用户信息！");

					} else {
						for (int j = 0; j < l.size(); j++) {
							map.put("f_result", "不成功");
							map.put("f_resultnote", "返盘金额与抄表金额不符！");
							Object[] record = (Object[]) l.get(j);
							//网点
							String f_yytdepa=record[10]+"";
							map.put("f_yytdepa", f_yytdepa);
							// 每笔欠费
							if (record[0] != null) {
								double oughtfee = Double.parseDouble(record[0]
										+ "");
								oughtfee = (double) Math.round(oughtfee * 100) / 100;

								if (oughtfee == b_oughtfee) {
									// 欠费合计相等，更新欠费标识
									map.put("f_result", "成功");
									map.put("f_resultnote", "");
									String f_userid = record[1] + "";// 户里面的
									String hand = record[9] + "";// 抄表记录里面的f_userid
									// 插入交费记录
									Map sell = new HashMap();
									String f_username = record[2] + "";
									String f_address = record[3] + "";
									String f_districtname = record[4] + "";
									String f_usertype = record[7] + "";
									// String f_inputtor = record[8]+"";
									String f_phone = record[8] + "";
									Double f_preamount = b_oughtfee;
									Double f_totalcost = b_oughtfee;
									// 上次抄表底数
									Double lastinputgasnum = Double
											.parseDouble(((Object[]) l.get(j))[5]
													+ "");
									// 本次抄表底数
									Double lastrecord = Double
											.parseDouble(((Object[]) l.get(j))[6]
													+ "");
									// 上次抄表日期
									String f_fanpandate1 = f_fanpandate;
									String f_bankname1 = f_bankname;

									map.put("f_userid", f_userid);
									map.put("f_username", f_username);
									map.put("f_address", f_address);
									map.put("f_bankname", f_bankname);
									// map.put("f_inputtor", f_inputtor);
									// Date date =
									// DateFormat.getDateInstance().parse(f_deliverydate);
									// xxx.setDate(1,date);
									sell.put("f_bankname", f_bankname1);
									sell.put("f_userid", f_userid);
									sell.put("f_username", f_username);
									sell.put("f_address", f_address);
									sell.put("f_districtname", f_districtname);
									sell.put("f_usertype", f_usertype);
									// sell.put("f_preamount", f_preamount);
									//sell.put("f_sgoperator",operator);
									sell.put("f_sgnetwork", f_yytdepa);
									sell.put("f_comtype", "天然气公司");
									sell.put("f_payfeevalid", "有效");
									sell.put("f_payfeetype", "银行代扣");
									sell.put("f_sgnetwork", loginuser.get("f_parentname").toString()); // 网点
									sell.put("f_sgoperator", loginuser.get("name").toString()); // 操 作 员
									sell.put("f_filiale", loginuser.get("f_fengongsi").toString()); // 分公司
									sell.put("f_fengongsinum", loginuser.get("f_fengongsinum").toString()); // 分公司编号
									sell.put("f_orgstr", loginuser.get("orgstr").toString()); // 组织信息
									//sell.put("f_filiale", "安顺达管道天燃气有限公司");
									sell.put("f_fanpandate", f_fanpandate1);
									sell.put("f_preamount", f_preamount);
									sell.put("f_totalcost", f_totalcost);
									// 实收金额
									sell.put("f_grossproceeds", f_totalcost);
									// 交费日期
									sell.put("f_deliverydate",
											new java.util.Date());
									sell.put("f_deliverytime",
											new java.util.Date());
									// 上次抄表底数
									sell
											.put("lastinputgasnum",
													lastinputgasnum);
									// 本次抄表底数
									sell.put("lastrecord", lastrecord);
									// 取出sellid
									int sellId = (Integer) hibernateTemplate
											.save("t_sellinggas", sell);
									// 更新抄表记录sellid
									String sql = "update t_handplan set shifoujiaofei='是', f_sellid ="
											+ sellId
											+ ""
											+ " where f_state='已抄表' and shifoujiaofei='否'"
											+ " and f_sendtime is not null and f_sendtime<getdate() and CONVERT(varchar(12) ,f_sendtime, 112 )>CONVERT(varchar(12) ,f_inputdate, 112 ) and f_userid='"
											+ hand + "'";
									hibernateTemplate.bulkUpdate(sql);
									break;
								}
							} else {
								map.put("f_result", "不成功");
								map.put("f_resultnote", "未找到改用户抄表信息！");
							}
						}
					}
					session.close();
				} else {
					String sql2 = "select o.f_userid,o.f_username,o.f_address,o.f_phone,s.f_inputtor,o.f_yytdepa from "
						+ "t_userinfo o left join t_userfiles s on o.f_userid=s.f_userinfoid where "
						+ "o.f_idofcard='"
						+ f_idofcard
						+ "' and o.f_bankname='" + f_bankname + "'";
					SessionFactory sf1 = hibernateTemplate.getSessionFactory();
					Session session1 = sf1.openSession();
					List l1 = session1.createSQLQuery(sql2).list();
					if (l1.size() != 0) {
						Object[] records = (Object[]) l1.get(0);
						String f_username = records[1] + "";
						String f_address = records[2] + "";
						String f_userid = records[0] + "";
						String f_phone = records[3] + "";
						String f_inputtor = records[4] + "";
						String f_yytdepa = records[5]+"";
						map.put("f_userid", f_userid);
						map.put("f_username", f_username);
						map.put("f_address", f_address);
						map.put("f_bankname", f_bankname);
						map.put("f_inputtor", f_inputtor);
						map.put("f_phone", f_phone);
						map.put("f_yytdepa", f_yytdepa);
				}
					session1.close();
					map.put("f_result", "不成功");
					map.put("f_resultnote", paymenstate);
				}
				// 往返盘表中插入记录
				hibernateTemplate.save("t_bankreturn", map);
				// 更新户状态为正常
				String sql = "update t_userinfo set f_userstate='正常'"
						+ " where id in (select distinct f_userinfoid from t_userfiles where f_idofcard='"
						+ f_idofcard + "' and f_bankname='" + f_bankname
						+ "') and (f_userstate = '银行扣款' or f_userstate = '正常')";// 送盘的
																				// 加条件
																				// 不能把销户的给更新了
				hibernateTemplate.bulkUpdate(sql);
				// 更新表状态为正常
				sql = "update t_userfiles set f_userstate='正常'"
						+ "  where f_idofcard='" + f_idofcard
						+ "' and f_bankname='" + f_bankname
						+ "' and (f_userstate = '银行扣款' or f_userstate = '正常')";
				hibernateTemplate.bulkUpdate(sql);

			}
			returnJson.put("success", "操作成功!");
		} catch (Exception e) {
			log.error(e.getMessage() + usernames + "aaaaaaa的！");
			throw new WebApplicationException(500);
		}
		return returnJson.toString();
	}

	// 给字符串添加逗号分隔的内容
	private String add(String source, String str) {
		if (source.equals("")) {
			return source + str;
		} else {
			return source + "," + str;
		}
	}
	
	
	
	public static Map<String, Object> getUser(String operator,
			HibernateTemplate hibernateTemplate) throws ResultException {
		Map<String, Object> user = new HashMap<String, Object>();
		String hql = "from t_user where name=" + operator;
		List list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("没有找到name为" + operator + "的操作员信息");
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		
		return map;
	}
	
	
	/**
	 * 查找户信息
	 * 
	 * @param userid
	 * @return
	 */
	private Map<String, Object> findUserinfo( String f_filiale,String f_idofcard,String f_bankname) {
		final String userSql = "from t_userinfo  where f_filiale='" + f_filiale
				+"' and f_idofcard='"
						+ f_idofcard
						+ "' and f_bankname='"
						+ f_bankname
						+ "' and (f_userstate = '银行扣款' or f_userstate = '正常')";
		// List userlist = session.createQuery(userSql).list();
		log.debug("查询户信息开始:" + userSql);
		List<Object> userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return null;
		}
		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
		return userMap;
	}
}
