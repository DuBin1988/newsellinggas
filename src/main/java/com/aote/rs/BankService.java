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

	// ���������ݣ�data��JSON��ʽ�ķ�������
	@POST
	public String procReturn(String data) {
		JSONObject returnJson = new JSONObject();
		try {
			JSONArray array = new JSONArray(data);
			// ����data�е�ÿһ������
			String name = "";
			String bankcard = "";
			for (int i = 0; i < array.length(); i++) {
				JSONObject oper = array.getJSONObject(i);
				JSONObject object = oper.getJSONObject("data");
				// ��ȡ�û����������˺ŵ���Ϣ
				String f_usermc = object.getString("f_usermc");
				String paymenstate = object.getString("paymenstate");
				String f_idofcard = object.getString("f_idofcard");
				String f_bankname = object.getString("f_bankname");
				double b_oughtfee = object.getDouble("oughtfee");
				String f_fanpandate = object.getString("f_fanpandate");
				String operator = object.getString("oper");
				// ͨ���û������������˺ŵ���Ϣ�����û���ϸ��Ϣ
				Map map = new HashMap();
				map.put("f_usermc", f_usermc);
				map.put("paymenstate", paymenstate);
				map.put("f_idofcard", f_idofcard);
				map.put("f_bankname", f_bankname);
				map.put("oughtfee", b_oughtfee);
				map.put("f_fanpandate", f_fanpandate);
				map.put("f_returntime", new java.util.Date());
                
				

				// ����ۿ�ɹ����ҳ����С��˺š����һ�£��������̣���������С�ڵ�ǰ���ڵ�����
				if (paymenstate.equals("�ɹ�")) {
					//����Ӧ�ɷѳ����¼
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
							+ " and t.f_state='�ѳ���' and shifoujiaofei='��'"
							+ " and f_sendtime is not null and f_sendtime<getdate() and CONVERT(varchar(12) ,f_sendtime, 112 )>CONVERT(varchar(12) ,f_inputdate, 112 ) group by t.f_userid,u.f_userinfoid,t.f_address,t.f_usertype)hu on o.f_userid = hu.f_userinfoid"
							+ " where o.f_idofcard='"
							+ f_idofcard
							+ "' and o.f_bankname='"
							+ f_bankname
							+ "' and (o.f_userstate = '���пۿ�' or o.f_userstate = '����')";
					// HibernateSQLCall sqlCall = new HibernateSQLCall(hql);
					SessionFactory sf = hibernateTemplate.getSessionFactory();
					Session session = sf.openSession();
					List l = session.createSQLQuery(hql).list();

					// hibernateTemplate.get*/
					// û���ҵ�����Ǹü�¼״̬Ϊ�޴��˺�
					if (l.size() == 0) {
						map.put("f_result", "���ɹ�");
						map.put("f_resultnote", "δ�ҵ����û���Ϣ��");

					} else {
						for (int j = 0; j < l.size(); j++) {
							map.put("f_result", "���ɹ�");
							map.put("f_resultnote", "���̽���볭�������");
							Object[] record = (Object[]) l.get(j);
							//����
							String f_yytdepa=record[10]+"";
							map.put("f_yytdepa", f_yytdepa);
							// ÿ��Ƿ��
							if (record[0] != null) {
								double oughtfee = Double.parseDouble(record[0]
										+ "");
								oughtfee = (double) Math.round(oughtfee * 100) / 100;

								if (oughtfee == b_oughtfee) {
									// Ƿ�Ѻϼ���ȣ�����Ƿ�ѱ�ʶ
									map.put("f_result", "�ɹ�");
									map.put("f_resultnote", "");
									String f_userid = record[1] + "";// �������
									String hand = record[9] + "";// �����¼�����f_userid
									// ���뽻�Ѽ�¼
									Map sell = new HashMap();
									String f_username = record[2] + "";
									String f_address = record[3] + "";
									String f_districtname = record[4] + "";
									String f_usertype = record[7] + "";
									// String f_inputtor = record[8]+"";
									String f_phone = record[8] + "";
									Double f_preamount = b_oughtfee;
									Double f_totalcost = b_oughtfee;
									// �ϴγ������
									Double lastinputgasnum = Double
											.parseDouble(((Object[]) l.get(j))[5]
													+ "");
									// ���γ������
									Double lastrecord = Double
											.parseDouble(((Object[]) l.get(j))[6]
													+ "");
									// �ϴγ�������
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
									sell.put("f_comtype", "��Ȼ����˾");
									sell.put("f_payfeevalid", "��Ч");
									sell.put("f_payfeetype", "���д���");
									sell.put("f_filiale", "��˳��ܵ���ȼ�����޹�˾");
									sell.put("f_fanpandate", f_fanpandate1);
									sell.put("f_preamount", f_preamount);
									sell.put("f_totalcost", f_totalcost);
									// ʵ�ս��
									sell.put("f_grossproceeds", f_totalcost);
									// ��������
									sell.put("f_deliverydate",
											new java.util.Date());
									sell.put("f_deliverytime",
											new java.util.Date());
									// �ϴγ������
									sell
											.put("lastinputgasnum",
													lastinputgasnum);
									// ���γ������
									sell.put("lastrecord", lastrecord);
									// ȡ��sellid
									int sellId = (Integer) hibernateTemplate
											.save("t_sellinggas", sell);
									// ���³����¼sellid
									String sql = "update t_handplan set shifoujiaofei='��', f_sellid ="
											+ sellId
											+ ""
											+ " where f_state='�ѳ���' and shifoujiaofei='��'"
											+ " and f_sendtime is not null and f_sendtime<getdate() and CONVERT(varchar(12) ,f_sendtime, 112 )>CONVERT(varchar(12) ,f_inputdate, 112 ) and f_userid='"
											+ hand + "'";
									hibernateTemplate.bulkUpdate(sql);
									break;
								}
							} else {
								map.put("f_result", "���ɹ�");
								map.put("f_resultnote", "δ�ҵ����û�������Ϣ��");
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
					map.put("f_result", "���ɹ�");
					map.put("f_resultnote", paymenstate);
				}
				// �����̱��в����¼
				hibernateTemplate.save("t_bankreturn", map);
				// ���»�״̬Ϊ����
				String sql = "update t_userinfo set f_userstate='����'"
						+ " where id in (select distinct f_userinfoid from t_userfiles where f_idofcard='"
						+ f_idofcard + "' and f_bankname='" + f_bankname
						+ "') and (f_userstate = '���пۿ�' or f_userstate = '����')";// ���̵�
																				// ������
																				// ���ܰ������ĸ�������
				hibernateTemplate.bulkUpdate(sql);
				// ���±�״̬Ϊ����
				sql = "update t_userfiles set f_userstate='����'"
						+ "  where f_idofcard='" + f_idofcard
						+ "' and f_bankname='" + f_bankname
						+ "' and (f_userstate = '���пۿ�' or f_userstate = '����')";
				hibernateTemplate.bulkUpdate(sql);

			}
			returnJson.put("success", "�����ɹ�!");
		} catch (Exception e) {
			log.error(e.getMessage() + usernames + "aaaaaaa�ģ�");
			throw new WebApplicationException(500);
		}
		return returnJson.toString();
	}
	
	


	// ���ַ�����Ӷ��ŷָ�������
	private String add(String source, String str) {
		if (source.equals("")) {
			return source + str;
		} else {
			return source + "," + str;
		}
	}
}
