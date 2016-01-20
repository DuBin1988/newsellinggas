package com.aote.rs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aote.listener.ContextListener;
import com.aote.rs.DBService.HibernateCall;
import com.aote.rs.bank.bankreturn.BankExcelSet;
import com.aote.rs.bank.bankreturn.BankReturnInterface;
import com.aote.rs.util.ExcelHelper;
import com.aote.rs.util.FileHelper;

@Path("bank")
@Scope("prototype")
@Component
public class BankService {
	static Logger log = Logger.getLogger(BankService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	// ���������ݣ�data��JSON��ʽ�ķ�������
	@POST
	public void procReturn(String data) {
		try {
			JSONArray array = new JSONArray(data);
			// ����data�е�ÿһ������
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
				// ����Щ���ݲ��뵽���̱���
				Map map = new HashMap();
				map.put("f_usermc", f_usermc);
				map.put("paymenstate", paymenstate);
				map.put("f_idofcard", f_idofcard);
				map.put("f_bankname", f_bankname);
				map.put("oughtfee", b_oughtfee);
				map.put("f_fanpandate", f_fanpandate);
				// ͨ���û������������˺ŵ���Ϣ�����û���ϸ��Ϣ
				final String Sql = "select ISNULL(u.f_phone,'null') f_phone,isnull(u.f_bankname,'null') f_bankname,ISNULL( u.f_idofcard,'null') f_idofcard,isnull(u.f_userid,'null') f_userid,isnull(u.f_username,'null') f_username,isnull(u.f_address,'null') f_address,isnull(h.a,GETDATE()) a,isnull(h.f_inputtor,'null') f_inputtor,isnull(h.id,0) id  from t_userfiles u left join "
						+ "(select id,shifoujiaofei,f_state,f_username,f_inputtor,stuff((select ',' + substring(convert(varchar(50),lastinputdate,120),1,10) from t_handplan "
						+ " where f_username= '"
						+ f_usermc
						+ "' and f_idofcard='"
						+ f_idofcard
						+ "' and f_state='�ѳ���' and shifoujiaofei='��' "
						+ " FOR XML PATH('')),1,1,'') as a from t_handplan) h "
						+ " on u.f_username = h.f_username "
						+ " where u.f_username= '"
						+ f_usermc
						+ "' and u.f_idofcard='"
						+ f_idofcard
						+ "' and h.f_state='�ѳ���' and h.shifoujiaofei='��' ";
				List<Map<String, Object>> list = (List<Map<String, Object>>) hibernateTemplate
						.execute(new HibernateCallback() {
							public Object doInHibernate(Session session)
									throws HibernateException {
								Query q = session.createSQLQuery(Sql);
								q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
								List result = q.list();
								return result;
							}
						});
				// ȡ��δ�����¼�Լ��û���Ϣ
				Map<String, Object> handmap = (Map<String, Object>) list.get(0);
				String username = handmap.get("f_username").toString();
				String userid = handmap.get("f_userid").toString();
				String address = handmap.get("f_address").toString();
				String inputtor = handmap.get("f_inputtor").toString();
				String defaultMonth = handmap.get("a").toString();
				String phone = handmap.get("f_phone").toString();
				String handIds = "";
				for (Map<String, Object> hand : list) {
					int handId = (Integer) hand.get("id");

					// �����¼Ids
					handIds = add(handIds, handId + "");
				}
				// ����Щ���ݲ��뵽���̱���
				Map maps = new HashMap();
				maps.put("f_usermc", f_usermc);
				maps.put("paymenstate", paymenstate);
				maps.put("f_idofcard", f_idofcard);
				maps.put("f_bankname", f_bankname);
				maps.put("oughtfee", b_oughtfee);
				maps.put("f_fanpandate", f_fanpandate);
				maps.put("f_username", username);
				maps.put("f_userid", userid);
				maps.put("f_address", address);
				maps.put("f_inputtor", inputtor);
				maps.put("f_defaultmonth", defaultMonth);
				maps.put("f_phone", phone);
				// ����ʱ��
				maps.put("f_returntime", new java.util.Date());
				// ����ۿ�ɹ�������볭���¼�е������Ƿ�һ��
				if (paymenstate.equals("�ɹ�")) {
					String hql = "select sum(t.oughtfee) oughtfee,t.f_userid,t.f_username,t.f_address,t.f_address,t.f_usertype"
							+ ",min(t.lastinputgasnum) lastinputgasnum,max(t.lastrecord) lastrecord"
							+ " from t_handplan t,t_userfiles u where t.users=u.id and u.f_idofcard='"
							+ f_idofcard
							+ "' and u.f_bankname='"
							+ f_bankname
							// "' and t.oughtfee=" + b_oughtfee +
							+ "' and t.f_state='�ѳ���' and shifoujiaofei='��'"
							+ " and f_sendtime is not null and f_sendtime<getdate()"
							+ " group by t.f_userid,t.f_username,t.f_address,t.f_usertype";

					SessionFactory sf = hibernateTemplate.getSessionFactory();
					Session session = sf.openSession();
					List l = session.createSQLQuery(hql).list();
					// û���ҵ�����Ǹü�¼״̬Ϊ�޴��˺�
					if (l.size() == 0) {
						map.put("f_result", "���ɹ�");
					} else {

						for (int j = 0; j < l.size(); j++) {
							map.put("f_result", "���ɹ�");
							Object[] record = (Object[]) l.get(j);
							// ÿ��Ƿ��
							double oughtfee = Double
									.parseDouble(record[0] + "");
							oughtfee = (double) Math.round(oughtfee * 100) / 100;

							// Ƿ�Ѻϼ���ȣ�����Ƿ�ѱ�ʶ
							if (oughtfee == b_oughtfee) {
								map.put("f_result", "�ɹ�");
								String f_userid = record[1] + "";
								String sql = "update t_handplan set shifoujiaofei='��'"
										+ " where f_state='�ѳ���' and shifoujiaofei='��'"
										+ " and f_sendtime is not null and f_sendtime<getdate() and f_userid='"
										+ f_userid + "'";
								hibernateTemplate.bulkUpdate(sql);
								// ���뽻�Ѽ�¼
								Map sell = new HashMap();
								String f_username = record[2] + "";
								String f_address = record[3] + "";
								String f_districtname = record[4] + "";
								String f_usertype = record[5] + "";
								Double f_preamount = b_oughtfee;
								Double f_totalcost = b_oughtfee;
								// �ϴγ������
								Double lastinputgasnum = Double
										.parseDouble(((Object[]) l.get(j))[6]
												+ "");
								// ���γ������
								Double lastrecord = Double
										.parseDouble(((Object[]) l.get(j))[7]
												+ "");
								// �ϴγ�������
								String f_fanpandate1 = f_fanpandate;
								String f_bankname1 = f_bankname;
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
								sell.put("f_filiale", "��˳��ܵ���ȼ�����޹�˾");
								sell.put("f_fanpandate", f_fanpandate1);
								sell.put("f_preamount", f_preamount);
								sell.put("f_totalcost", f_totalcost);
								// ʵ�ս��
								sell.put("f_grossproceeds", f_totalcost);
								// ��������
								sell.put("f_deliverydate", new java.util.Date());
								sell.put("f_deliverytime", new java.util.Date());
								// �ϴγ������
								sell.put("lastinputgasnum", lastinputgasnum);
								// ���γ������
								sell.put("lastrecord", lastrecord);
								// ȡ��sellid
								int sellId = (Integer) hibernateTemplate.save(
										"t_sellinggas", sell);

								final String Sql1 = "select id from t_sellinggas where f_userid ='"
										+ userid
										+ "' order by f_deliverydate desc";
								List<Map<String, Object>> sellList = (List<Map<String, Object>>) hibernateTemplate
										.execute(new HibernateCallback() {
											public Object doInHibernate(
													Session session)
													throws HibernateException {
												Query q = session
														.createSQLQuery(Sql1);
												q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
												List result = q.list();
												return result;
											}
										});
								// ȡ��δ�����¼�Լ��û���Ϣ
								Map<String, Object> handmap1 = (Map<String, Object>) sellList
										.get(0);
								String selId = handmap1.get("id").toString();
								// ���³����¼sellid
								String updateHandplan = "update t_handplan set f_sellid ="
										+ selId
										+ " where id in ("
										+ handIds
										+ ")";
								hibernateTemplate.bulkUpdate(updateHandplan);
								break;
							}
						}
					}
				} else {
					map.put("f_result", "���ɹ�");
				}
				// �����̱��в����¼
				hibernateTemplate.save("t_bankreturn", map);
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

	/**
	 * �����ļ��ϴ���ʽ��������
	 */
	@Path("/payment/{fileid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String payment(@PathParam("fileid") String fileid ) {
		String result = "";
		try {
			// ��ȡExcel������Ϊjson��ʽ
			ServletContext sc = ContextListener.getContext();
			ApplicationContext ctx = WebApplicationContextUtils
					.getWebApplicationContext(sc);
			BankExcelSet setBean = (BankExcelSet) ctx
					.getBean("BankExcelSet");
			String fullPath = getFileFullPath(fileid);
			JSONArray datas = ExcelHelper.convertFromFile(fullPath,
					setBean.getStartRow(), setBean.getFields());
			// ����ҵ����ӿ�
			BankReturnInterface bankBean = (BankReturnInterface) ctx
					.getBean("BankReturnDispose");
			bankBean.process(datas);
		} catch (Exception e) {
			// ��ѯ���������ݣ��׳��쳣
			throw new WebApplicationException(500);
		}
		return result;
	}

	/**
	 * ��ȡ�ļ�����ȫ·��
	 * @throws UnsupportedEncodingException 
	 */
	private String getFileFullPath(String bolbId) throws UnsupportedEncodingException
	{
		String hql = "from t_blob where id='" + bolbId + "'";
		List list = this.hibernateTemplate.find(hql);
		if (list.size() == 0)
			return "";
		Map map = (Map) list.get(0);
		// ����ļ���
		String filefullpath = (String) map.get("filefullpath");
 		return filefullpath;
	}
	
	 
	

}
