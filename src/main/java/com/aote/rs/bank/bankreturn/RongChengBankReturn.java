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
 * �ٳɷ����ļ�����ʵ��
 * 
 * @author Administrator
 *
 */
public class RongChengBankReturn implements BankReturnInterface {

	static Logger log = Logger.getLogger(BankService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	private String f_bankname = "����";

	public void process(JSONArray array) {
		try {
	 		// ����data�е�ÿһ������
			String name = "";
			String bankcard = "";
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
		 	    String f_usermc = object.getString("f_usermc");
			    String f_idofcard = object.getString("f_idofcard");
			    String paymenstate = object.getString("paymenstate");
			    //Ӧ��
			    String oughtfeeStr = object.getString("oughtfee");
			    oughtfeeStr = oughtfeeStr.substring(0,oughtfeeStr.length() -1);
			    double bankOughtfee = Double.parseDouble(oughtfeeStr);
			    String dealfeeStr = object.getString("dealfee");
			    dealfeeStr = dealfeeStr.substring(0,dealfeeStr.length() -1);
			    double bankDealfee = Double.parseDouble(dealfeeStr);
		 	    object.put("oughtfee", bankOughtfee);
			    object.put("dealfee", bankDealfee);
			    object.put("bankname", f_bankname);
			 				// ����ۿ�ɹ����ҳ����С��˺š����һ�£��������̣���������С�ڵ�ǰ���ڵ�����
				if (paymenstate.equals("����ɹ�")) {
					// ����Ӧ�ɷѳ����¼
					// List l = findHandplans();
					String hql = "select sum(t.oughtfee) oughtfee,t.f_userid,t.f_username,t.f_address,t.f_address,t.f_usertype"
							+ ",min(t.lastinputgasnum) lastinputgasnum,max(t.lastrecord) lastrecord"
							+ " from t_handplan t,t_userfiles u where t.users=u.id and u.f_idofcard='"
							+ f_idofcard
							+ "' and u.f_bankname='"
							+ f_bankname
						 	+ "' and t.f_state='�ѳ���' and shifoujiaofei='��'"
							+ " and f_sendtime is not null and f_sendtime<getdate()"
							+ " group by t.f_userid,t.f_username,t.f_address,t.f_usertype";
			 		SessionFactory sf = hibernateTemplate.getSessionFactory();
					Session session = sf.openSession();
					List l = session.createSQLQuery(hql).list();
					// hibernateTemplate.get*/
					// û���ҵ�����Ǹü�¼״̬Ϊ�޴��˺�
					if (l.size() == 0) {
						object.put("f_result", "���ɹ�");
						object.put("f_resultnote", "δ�ҵ����û���Ϣ��");
					} else {
						for (int j = 0; j < l.size(); j++) {
							object.put("f_result", "���ɹ�");
							object.put("f_resultnote", "���̽���볭�������");
							Object[] record = (Object[]) l.get(j);
							// ÿ��Ƿ��
							if (record[0] != null) {
								double oughtfee = Double.parseDouble(record[0]
										+ "");
								oughtfee = (double) Math.round(oughtfee * 100) / 100;

								if (oughtfee == bankOughtfee) {
									// Ƿ�Ѻϼ���ȣ�����Ƿ�ѱ�ʶ
									object.put("f_result", "�ɹ�");
									object.put("f_resultnote", "");
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
									Double f_preamount = bankOughtfee;
									Double f_totalcost = bankDealfee;
									// �ϴγ������
									Double lastinputgasnum = Double
											.parseDouble(((Object[]) l.get(j))[5]
													+ "");
									// ���γ������
									Double lastrecord = Double
											.parseDouble(((Object[]) l.get(j))[6]
													+ "");
									// �ϴγ�������
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
									sell.put("f_sgoperator", "����Ա");
									sell.put("f_sgnetwork", "����");
									sell.put("f_comtype", "��Ȼ����˾");
									sell.put("f_payfeevalid", "��Ч");
									sell.put("f_payfeetype", "���д���");
									sell.put("f_filiale", "�ٳɸۻ�ȼ�����޹�˾");
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
									sell.put("lastinputgasnum", lastinputgasnum);
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
											+ " and f_sendtime is not null and f_sendtime<getdate() and f_userid='"
											+ hand + "'";
									hibernateTemplate.bulkUpdate(sql);
									break;
								}
							} else {
								object.put("f_result", "���ɹ�");
								object.put("f_resultnote", "δ�ҵ����û�������Ϣ��");
							}
						}
					}
					session.close();
				} else {
			 
					object.put("f_result", "���ɹ�");
					object.put("f_resultnote", paymenstate);
				}
				// �����̱��в����¼
		 		Map saveObj = JSONHelper.toHashMap(object);
				hibernateTemplate.save("t_bankreturn", saveObj);
	 		 	// ���±�״̬Ϊ����
				String sql = "update t_userfiles set f_userstate='����'"
						+ "  where f_idofcard='" + f_idofcard
						+ "' and f_bankname='" + f_bankname + "'";
				hibernateTemplate.bulkUpdate(sql);
 		}
 	} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);

		}
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
