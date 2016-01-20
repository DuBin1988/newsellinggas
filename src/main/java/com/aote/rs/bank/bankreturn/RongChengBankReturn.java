package com.aote.rs.bank.bankreturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.aote.rs.BankService;
import com.aote.rs.util.JSONHelper;

/**
 * 荣成返盘文件处理实现
 * 
 * @author Administrator
 *
 */
public class RongChengBankReturn implements BankReturnInterface {

	static Logger log = Logger.getLogger(BankService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	private String f_bankname = "工行";

	public void process(JSONArray array) {
		try {
	 		// 对于data中的每一条数据
			String name = "";
			String bankcard = "";
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
		 	    String f_usermc = object.getString("f_usermc");
			    String f_idofcard = object.getString("f_idofcard");
			    String paymenstate = object.getString("paymenstate");
			    //应缴
			    String oughtfeeStr = object.getString("oughtfee");
			    oughtfeeStr = oughtfeeStr.substring(0,oughtfeeStr.length() -1);
			    double bankOughtfee = Double.parseDouble(oughtfeeStr);
			    String dealfeeStr = object.getString("dealfee");
			    dealfeeStr = dealfeeStr.substring(0,dealfeeStr.length() -1);
			    double bankDealfee = Double.parseDouble(dealfeeStr);
		 	    object.put("oughtfee", bankOughtfee);
			    object.put("dealfee", bankDealfee);
			    object.put("bankname", f_bankname);
			 				// 如果扣款成功，找出银行、账号、金额一致，且已送盘，送盘日期小于当前日期的数据
				if (paymenstate.equals("处理成功")) {
					// 查找应缴费抄表记录
					// List l = findHandplans();
					String hql = "select sum(t.oughtfee) oughtfee,t.f_userid,t.f_username,t.f_address,t.f_address,t.f_usertype"
							+ ",min(t.lastinputgasnum) lastinputgasnum,max(t.lastrecord) lastrecord"
							+ " from t_handplan t,t_userfiles u where t.users=u.id and u.f_idofcard='"
							+ f_idofcard
							+ "' and u.f_bankname='"
							+ f_bankname
						 	+ "' and t.f_state='已抄表' and shifoujiaofei='否'"
							+ " and f_sendtime is not null and f_sendtime<getdate()"
							+ " group by t.f_userid,t.f_username,t.f_address,t.f_usertype";
			 		SessionFactory sf = hibernateTemplate.getSessionFactory();
					Session session = sf.openSession();
					List l = session.createSQLQuery(hql).list();
					// hibernateTemplate.get*/
					// 没有找到，标记该记录状态为无此账号
					if (l.size() == 0) {
						object.put("f_result", "不成功");
						object.put("f_resultnote", "未找到该用户信息！");
					} else {
						for (int j = 0; j < l.size(); j++) {
							object.put("f_result", "不成功");
							object.put("f_resultnote", "返盘金额与抄表金额不符！");
							Object[] record = (Object[]) l.get(j);
							// 每笔欠费
							if (record[0] != null) {
								double oughtfee = Double.parseDouble(record[0]
										+ "");
								oughtfee = (double) Math.round(oughtfee * 100) / 100;

								if (oughtfee == bankOughtfee) {
									// 欠费合计相等，更新欠费标识
									object.put("f_result", "成功");
									object.put("f_resultnote", "");
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
									Double f_preamount = bankOughtfee;
									Double f_totalcost = bankDealfee;
									// 上次抄表底数
									Double lastinputgasnum = Double
											.parseDouble(((Object[]) l.get(j))[5]
													+ "");
									// 本次抄表底数
									Double lastrecord = Double
											.parseDouble(((Object[]) l.get(j))[6]
													+ "");
									// 上次抄表日期
							 		String f_bankname1 = f_bankname;
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
									sell.put("f_sgoperator", "导入员");
									sell.put("f_sgnetwork", "财务部");
									sell.put("f_comtype", "天然气公司");
									sell.put("f_payfeevalid", "有效");
									sell.put("f_payfeetype", "银行代扣");
									sell.put("f_filiale", "荣成港华燃气有限公司");
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
									sell.put("lastinputgasnum", lastinputgasnum);
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
											+ " and f_sendtime is not null and f_sendtime<getdate() and f_userid='"
											+ hand + "'";
									hibernateTemplate.bulkUpdate(sql);
									break;
								}
							} else {
								object.put("f_result", "不成功");
								object.put("f_resultnote", "未找到改用户抄表信息！");
							}
						}
					}
					session.close();
				} else {
			 
					object.put("f_result", "不成功");
					object.put("f_resultnote", paymenstate);
				}
				// 往返盘表中插入记录
		 		Map saveObj = JSONHelper.toHashMap(object);
				hibernateTemplate.save("t_bankreturn", saveObj);
	 		 	// 更新表状态为正常
				String sql = "update t_userfiles set f_userstate='正常'"
						+ "  where f_idofcard='" + f_idofcard
						+ "' and f_bankname='" + f_bankname + "'";
				hibernateTemplate.bulkUpdate(sql);
 		}
 	} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);

		}
	}

	// 给字符串添加逗号分隔的内容
	private String add(String source, String str) {
		if (source.equals("")) {
			return source + str;
		} else {
			return source + "," + str;
		}
	}
}
