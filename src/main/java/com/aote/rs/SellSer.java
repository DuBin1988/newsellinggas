package com.aote.rs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

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

import com.aote.rs.util.RSException;

@Path("sell")
@Scope("prototype")
@Component
public class SellSer {
	static Logger log = Logger.getLogger(SellSer.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	//��ѯǷ����Ϣ
		// ��ȡĳ����Ż�����Ϣ��Ƿ������
			@GET
			@Path("bill/{userid}")
			public String getUserBill(@PathParam("userid") String userid) {
				try {
					String result = "{";

					// ��ȡ���е�ֵ
					Map<String,String> singles = getSingles();
					
					// ��ȡ�û������������¼���
					String sql = "select "
							+ "ui.f_zhye f_zhye,ui.f_username f_username,u.f_usertype f_usertype,"
							+ "u.f_districtname f_districtname,u.f_address f_address,"
							+ "u.f_gasproperties f_gasproperties,u.f_gaspricetype f_gaspricetype,"
							+ "ui.f_userid infoid,u.f_gasprice f_gasprice,u.f_dibaohu f_dibaohu,"
							+ "u.f_payment f_payment,u.f_stairtype f_stairtype,ui.f_userstate f_userstate," // ui t_userinfo
							+ "" // u t_userfiles
							+ "h.days days,h.f_userid f_userid,h.oughtamount oughtamount,"
							+ "h.oughtfee oughtfee,h.lastinputdate lastinputdate,h.lastinputgasnum lastinputgasnum,"
							+ "h.lastrecord lastrecord,h.f_endjfdate f_endjfdate,h.f_operator f_operator,"
							+ "h.f_inputdate f_inputdate,h.f_network f_network,h.f_handdate f_handdate,"
							+ "h.id handId "
							+ // h t_handplan
							"from (select * from t_userinfo where f_userid='"
							+ userid
							+ "') ui join t_userfiles u on ui.f_userid=u.f_userinfoid "
							+ "left join (select datediff(day,f_endjfdate,GETDATE()) days,* from t_handplan where f_state='�ѳ���' and shifoujiaofei='��') h "
							+ "on u.f_userid=h.f_userid "
							+ "order by u.f_userid, h.lastinputdate, h.lastinputgasnum";
					List<Map<String, Object>> list = this.hibernateTemplate.executeFind(new HibernateSQLCall(sql));
					// �ӵ�һ����ȡ������
					Map<String, Object> userinfo = (Map<String, Object>) list.get(0);
					result += "infoid:" + userinfo.get("infoid") + "";
					result += ",f_username:'" + (String) userinfo.get("f_username") + "'";
					result += ",f_address:'" + (String) userinfo.get("f_address") + "'";
					
					// �û�����
					BigDecimal f_zhye = new BigDecimal(userinfo.get("f_zhye").toString());
					result += ",f_zhye:" + f_zhye;
					String f_usertype=(String) userinfo.get("f_usertype");
					result += ",f_usertype:'" + f_usertype + "'";
					result += ",f_districtname:'" + (String) userinfo.get("f_districtname")
							+ "'";
					result += ",f_gasproperties:'"
							+ (String) userinfo.get("f_gasproperties") + "'";
					result += ",f_gaspricetype:'" + (String) userinfo.get("f_gaspricetype")
							+ "'";
					result += ",f_gasprice:" + userinfo.get("f_gasprice");
					result += ",f_dibaohu:" + userinfo.get("f_dibaohu");
					result += ",f_payment:'" + (String) userinfo.get("f_payment") + "'";
					result += ",f_userstate:'" + (String) userinfo.get("f_userstate") + "'";
					result += ",f_stairtype:'" + (String) userinfo.get("f_stairtype") + "'"; 
					result += ", f_hands:[";
					// Ƿ�ѱ������
					String hands = "";
					
					// ȡ���ɽ����
					BigDecimal scale = null;
					/*
					if(f_usertype.equals("����")) {
						scale = new BigDecimal(singles.get("�������ɽ����"));
					} else {
						scale = new BigDecimal(singles.get("���������ɽ����"));			
					}*/
					
					// ѭ����ȡǷ������
					for (Map<String, Object> hand : list) {
						if (!hands.equals("")) {
							hands += ",";
						}
						// ���û��Ƿ�����ݣ�����
						Object handId = hand.get("handId");
						if (handId == null) {
							continue;
						}
						hands += "{";
						//
						hands += "f_userid:'" + hand.get("f_userid") + "'";
						// ������
						hands += ",oughtamount:" + hand.get("oughtamount");
						// ����
						hands += ",oughtfee:" + hand.get("oughtfee");
						// ���ɽ���=����*����*����
						BigDecimal oughtfee = new BigDecimal(hand.get("oughtfee").toString());
						int days = Integer.parseInt(hand.get("days") + "");
						days = days > 0 ? days : 0;
						
						BigDecimal f_zhinajin = new BigDecimal("0");
						// ��������ɽ𣬼������ȥ������
						int equals=f_zhye.compareTo(new BigDecimal("0"));//�Ƚ�����Ƿ����0
						if(equals > 0) {
							int bigDec=f_zhye.compareTo(oughtfee);//�ж�����Ƿ��������
							oughtfee = bigDec>0 ? new BigDecimal("0") : oughtfee.subtract(f_zhye);
							f_zhye = bigDec>0 ? f_zhye.subtract(oughtfee) : new BigDecimal("0");
						}
						//f_zhinajin=oughtfee.multiply(new BigDecimal(days+"")).multiply(scale);
						//f_zhinajin=f_zhinajin.setScale(2, BigDecimal.ROUND_HALF_UP);
						hands += ",f_zhinajin:" + f_zhinajin;
						
						// ��������
						hands += ",lastinputdate:'" + hand.get("lastinputdate") + "'";
						// ���ڳ������
						hands += ",lastinputgasnum:" + hand.get("lastinputgasnum");
						// ���ڳ������
						hands += ",lastrecord:" + hand.get("lastrecord");
						// ���ѽ�ֹ����
						hands += ",f_endjfdate:'" + hand.get("f_endjfdate") + "'";
						// ���ɽ�����
						hands += ",days:" + days;
						// ����
						hands += ",f_network:'" + hand.get("f_network") + "'";
						// ����Ա
						hands += ",f_operator:'" + hand.get("f_operator") + "'";
						// ¼������
						hands += ",f_inputdate:'" + hand.get("f_inputdate") + "'";
						hands += "}";
					}
			
					result = result + hands + "]}";
					return result;
					
				} catch (Exception ex) {
					// �Ǽ��쳣��Ϣ
					log.error(ex.getMessage());
					throw new WebApplicationException(401);
				}
			}
			
			// ��ȡ���е�ֵ��ת����Map
			private Map<String, String> getSingles() {
				Map result = new HashMap<String, String>();
				
				String sql = "select name,value from t_singlevalue";
				List<Map<String, Object>> list = this.hibernateTemplate.executeFind(new HibernateSQLCall(sql));
				for (Map<String, Object> hand : list) {
					result.put(hand.get("name"), hand.get("value"));
				}
				return result;
			}

	// ����sell������������
	@GET
	@Path("{userid}/{money}/{zhinajin}/{payment}/{opid}")
	public String txSell(@PathParam("userid") String userid,
			@PathParam("money") double dMoney,
			@PathParam("zhinajin") double dZhinajin,
			@PathParam("payment") String payment, @PathParam("opid") String opid) {
		// ������Ϣ��Ϊ��������ɹ�����Ϊ�������ʧ�ܣ�����Ϊ������Ϣ
		String ret = "";
		try {
			log.debug("�������� ��ʼ");
			// ���ҵ�½�û�,��ȡ��½����,����Ա
			Map<String, Object> loginUser = this.findUser(opid);
			if (loginUser == null) {
				log.debug("����ɷѴ���ʱδ�ҵ���½�û�,��½id" + opid);
				throw new RSException("����ɷѴ���ʱδ�ҵ���½�û�,��½id" + opid);
			}
			// �����û�����ҵ��û������е���Ϣ,�Լ������¼
			List<Map<String, Object>> list = this.findHanplans(userid);
			// �տ���ɽ�
			BigDecimal money = new BigDecimal(dMoney + "");
			BigDecimal zhinajin = new BigDecimal(dZhinajin + "");
			// ȡ����һ����¼���Ա���û�������ȡ����
			Map<String, Object> userinfo = (Map<String, Object>) list.get(0);
			// ���û�������ȡ���ۼƹ�����
			BigDecimal f_metergasnums = new BigDecimal(userinfo.get(
					"f_metergasnums").toString());
			BigDecimal f_cumulativepurchase = new BigDecimal(userinfo.get(
					"f_cumulativepurchase").toString());
			// ��¼�ϴι�����������ʱʹ�ã�
			BigDecimal oldf_metergasnums = new BigDecimal(userinfo.get(
					"f_metergasnums").toString());// �ɵı�ǰ�ۼƹ�����
			BigDecimal oldf_cumulativepurchase = new BigDecimal(userinfo.get(
					"f_cumulativepurchase").toString());// �ɵ����ۼƹ�����
			// ���û�������ȡ�����
			BigDecimal f_zhye = new BigDecimal(userinfo.get("f_zhye")
					.toString());
			// �տ��ȥ���ɽ�
			money.subtract(zhinajin);
			// �����+ʵ���շѽ��-���ɽ� �ٺ�Ӧ�����Ƚϣ��ж�δ���ѵĳ����¼�Ƿ��ܹ�����
			BigDecimal total = f_zhye.add(money).subtract(zhinajin);
			//  ����ָ��
			BigDecimal lastqinum = new BigDecimal("0");
			//   ����ָ��
			BigDecimal benqinum = new BigDecimal("0");
			// ������
			BigDecimal gasSum = new BigDecimal("0");
			// ������
			BigDecimal feeSum = new BigDecimal("0");
			// �����¼id
			String handIds = "";
			// �˻�ʵ�ʽ���,ʵ���տ�տ�-���ɽ�)
			BigDecimal f_accountZhye = new BigDecimal(userinfo.get(
					"f_accountzhye").toString());
			BigDecimal accReceMoney = money.subtract(zhinajin);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map) list.get(i);
			 	// ȡ��Ӧ�����
				String h = (map.get("oughtfee") + "");
				if (h.equals("null")) {
					h = "0.0";
				} else {
				}
				BigDecimal oughtfee = new BigDecimal(h);
				oughtfee = oughtfee.setScale(2, BigDecimal.ROUND_HALF_UP);
				// ��ǰ�û�ʵ�ʽɷѹ�������۳������Ѽ�¼��Ϊ�ѽ�
				int equals = total.compareTo(oughtfee);// �ж�total��oughtfee�Ĵ�С
				if (equals >= 0) {
					// ��һ������ȡ����ָ��
					if (i == 0) {
						String lastinputgasnum1 = (map.get("lastinputgasnum") + "");
						if (!lastinputgasnum1.equals("null"))
							lastqinum = new BigDecimal(lastinputgasnum1);
					}
					if (i == list.size() - 1) {
						String lastrecordstr = (map.get("lastrecord") + "");
						if (!lastrecordstr.equals("null"))
							benqinum = new BigDecimal(lastrecordstr);
					}
					// �۷ѣ��������������
					total = total.subtract(oughtfee);
				 	// �������
					String oughtamount1 = (map.get("oughtamount") + "");
					if (oughtamount1.equals("null"))
						oughtamount1 = "0.0";
					BigDecimal gas = new BigDecimal(oughtamount1);
					gasSum = gasSum.add(gas);
					// �ۼƹ�����
					f_metergasnums = f_metergasnums.add(gasSum);
					f_cumulativepurchase = f_cumulativepurchase.add(gasSum);
					// �������
					feeSum = feeSum.add(oughtfee);
					// ��ȡ�����¼ID
					Integer handId1 = (Integer) map.get("handid");
					if (handId1 == null)
						handId1 = 0;
					int handId = handId1;
					// �����¼Ids
					handIds = add(handIds, handId + "");
					// ���³����¼
					if (handId != 0) {
						String updateHandplan = "update t_handplan set shifoujiaofei='��' where id="
								+ handId;
						log.debug("���³����¼ sql:" + updateHandplan);
						hibernateTemplate.bulkUpdate(updateHandplan);
					}
				}
			}
			// �����û�����
			String updateUserinfo = "update t_userfiles set f_zhye=" + total
					+ " ,f_metergasnums=" + f_metergasnums
					+ " ,f_cumulativepurchase=" + f_cumulativepurchase
					+ " where f_userid='" + userid + "'";
			log.debug("�����û��ĵ���sql��" + updateUserinfo);
			hibernateTemplate.bulkUpdate(updateUserinfo);
			// �������Ѽ�¼
			Map<String, Object> sell = new HashMap<String, Object>();

			sell.put("f_userid", userid); // ����id
			sell.put("lastinputgasnum",
					lastqinum.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()); // ����ָ��
			sell.put("lastrecord",
					benqinum.setScale(1, BigDecimal.ROUND_HALF_UP)
							.doubleValue()); // ����ָ��
			sell.put("f_totalcost", zhinajin.add(feeSum).subtract(f_zhye)
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); // Ӧ�����
			sell.put("f_grossproceeds",
					money.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); // �տ�
			sell.put("f_zhinajin",
					zhinajin.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue()); // ���ɽ�

			Date now = new Date();
			sell.put("f_deliverydate", now); // ��������
			sell.put("f_deliverytime", now); // ����ʱ��

			sell.put("f_zhye", f_zhye.setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue()); // ���ڽ���
			sell.put("f_benqizhye", total.setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue()); // ���ڽ���
			sell.put("f_beginfee", userinfo.get("f_beginfee")); // ά�ܷ�
			sell.put("f_premetergasnums",
					oldf_metergasnums.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue()); // ���ϴ��ۼƹ�����
			sell.put(
					"f_upbuynum",
					oldf_cumulativepurchase.setScale(2,
							BigDecimal.ROUND_HALF_UP).doubleValue()); // �ϴ����ۼƹ�����
			sell.put("f_gasmeterstyle", "����"); // ��������
			sell.put("f_comtype", "��Ȼ����˾"); // ��˾���ͣ���Ϊ��Ȼ����˾������
			sell.put("f_username", userinfo.get("f_username")); // �û�/��λ����
			sell.put("f_address", userinfo.get("f_address")); // ��ַ
			sell.put("f_districtname", userinfo.get("f_districtname")); // ��ַ
			sell.put("f_cusDom", userinfo.get("f_cusDom")); // ��ַ
			sell.put("f_cusDy", userinfo.get("f_cusDy")); // ��ַ
			sell.put("f_idnumber", userinfo.get("f_idnumber")); // ���֤��
			sell.put("f_gaswatchbrand", "����"); // ����Ʒ��
			sell.put("f_gaspricetype", userinfo.get("f_gaspricetype")); // ��������
			sell.put("f_gasprice", userinfo.get("f_gasprice")); // ����
			sell.put("f_usertype", userinfo.get("f_usertype")); // �û�����
			sell.put("f_gasproperties", userinfo.get("f_gasproperties"));// ��������
			// �����У���������Ϊ�洢���Ӻţ�����������Ϣ�ֶ�
			if (userinfo.containsKey("f_cardid")
					&& userinfo.get("f_cardid") != null) {
				String kh = userinfo.get("f_cardid").toString();
				sell.put("f_cardid", kh);
			}

			sell.put("f_pregas", gasSum.setScale(1, BigDecimal.ROUND_HALF_UP)
					.doubleValue()); // ����
			sell.put("f_preamount", feeSum
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); // ����
			sell.put("f_payment", payment); // ���ʽ
			sell.put("f_paytype", "�ֽ�"); // �������ͣ����д���/�ֽ�
			sell.put("f_sgnetwork", loginUser.get("f_parentname").toString()); // ����
			sell.put("f_sgoperator", loginUser.get("name").toString()); // �� �� Ա
			sell.put("f_filiale", loginUser.get("f_fengongsi").toString()); // �ֹ�˾
			sell.put("f_fengongsinum", loginUser.get("f_fengongsinum")
					.toString()); // �ֹ�˾���
			sell.put("f_payfeetype", "�����շ�"); // ��������
			sell.put("f_payfeevalid", "��Ч"); // ������Ч����
			sell.put("f_useful", handIds); // �����¼id
			log.debug("���Ѽ�¼������Ϣ��" + sell.toString());
			int sellId = (Integer) hibernateTemplate.save("t_sellinggas", sell);
			// ��ʽ����������
			SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String result = "{id:" + sellId + ", f_deliverydate:'"
					+ f2.format(now) + "'}";
			// ���³����¼sellid
			if (handIds != null && !handIds.equals("") && !handIds.equals("0")) {
				String updateHandplan = "update t_handplan set f_sellid ="
						+ sellId + " where id in (" + handIds + ")";
				log.debug("���³����¼sql��" + updateHandplan);
				hibernateTemplate.bulkUpdate(updateHandplan);
			}
			log.debug("�������� ����");
			log.debug("��Ƿ�ѿ�ʼ" + userid);
			// ��Ƿ����,�˻�ʵ��������>0,˵��ʵ�ʲ�Ƿ�ѣ������������տ���µ���
			if (f_accountZhye.compareTo(BigDecimal.ZERO) > 0) {
				addUserAccountzhye(userid, accReceMoney);
				return ret;
			}
			// �Ƿ���ʵ�ĳ����¼
			if (userinfo.get("handid") == null) {
				addUserAccountzhye(userid, accReceMoney);
				return ret;
			}
			// ��Ƿ�Ѵ���
			financedetailDisp(loginUser,list,accReceMoney,sellId);
 			// ץȡ�Զ����쳣
		} catch (RSException e) {
			log.debug("�������� ʧ��!");
			ret = e.getMessage();
		} catch (Exception ex) {
			log.debug("�������� ʧ��!" + ex.getMessage());
			ret = ex.getMessage();
		} finally {
			return ret;
		}
	}

	/**
	 * ����˻�����
	 */
	private void addUserAccountzhye(String userid, BigDecimal money) {
		try {
			// ���µ����˻�����f_accountzhye
			String updateUserFile = "update t_userfiles set f_accountzhye=f_accountzhye+"
					+ money + " where f_userid='" + userid + "'";
			log.debug("ʵ�ʲ�Ƿ����µ����˻�����" + updateUserFile);
			this.hibernateTemplate.bulkUpdate(updateUserFile);
			log.debug("ʵ�ʲ�Ƿ����µ����˻��ɹ�");
		} catch (RuntimeException e) {
			throw new RSException("����˻�����ʧ��,�û�" + userid + ",���" + money);
		}
	}

	/**
	 * ����Ƿ�Ѵ�������Ƿ�ѣ�������Ƿ��¼��������࣬�տ�
	 * 
	 * @param hand
	 *            �����¼
	 * @param accountZhye
	 *            ��������
	 * @param shoukuan
	 *            �տ�
	 */
	private void financedetailDisp(Map<String, Object> loginUser,
		 List<Map<String,Object>> hands,	   BigDecimal shoukuan, int sellid)
			throws Exception {
		// ���򣬸����տ������������¼Ƿ�������Ƿ�Ѽ�¼������������������д�뵵��
		for (Map<String, Object> hand : hands) {
			log.debug("��Ƿһ�������¼" + hand.toString());
			int handId = Integer.parseInt(hand.get("handid").toString());
			String userId = hand.get("f_userid").toString();
			BigDecimal unitPrice = new BigDecimal(hand.get("f_gasprice")
					.toString());
			String sgnetwork = loginUser.get("f_parentname").toString();
			String sgoperator = loginUser.get("name").toString();
			BigDecimal debtM = new BigDecimal(hand.get("f_debtmoney")
					.toString());
			//ԭ������
			BigDecimal oldAccountzhye= shoukuan;
			// ʵ��
			BigDecimal realMoney = new BigDecimal(0);
			// ��Ƿ��
			BigDecimal newdebtmoney = new BigDecimal(0);
			BigDecimal newaccountzhye = new BigDecimal(0);
			// ��Ƿ�ѣ�����
			if (debtM.doubleValue() <= 0) {
				return;
			}
			// �տ� > Ƿ�� ��ʵ��=Ƿ�ѣ�������Ƿ�� =0 ,�½���=�տ�-Ƿ�ѣ������ܽ���
			if (shoukuan.compareTo(debtM) >= 0) {
				realMoney = debtM;
				newdebtmoney = new BigDecimal(0);
				newaccountzhye = shoukuan.subtract(debtM);
				shoukuan = shoukuan.subtract(debtM);
			}
			// �տ�С��Ƿ��, ʵ��=�տ������Ƿ�� = Ƿ��-�տ� ,�½���=0�������ܽ���
			else {
				realMoney = shoukuan;
				newdebtmoney = debtM.subtract(shoukuan);
				newaccountzhye = new BigDecimal(0);
				shoukuan = new BigDecimal(0);
			}
			//����Ƿ��¼
			this.financedetailSave(handId, userId, debtM, oldAccountzhye,
					realMoney, unitPrice, newdebtmoney, newaccountzhye,
					sgnetwork, sgoperator, sellid, hand.get("lastinputdate"));
			// ���µ����˻�����f_accountzhye
			String updateUserFile = "update t_userfiles set f_accountzhye="
					+ newaccountzhye.doubleValue() + " where f_userid='"
					+ userId + "'";
			log.debug("���µ����˻�����" + updateUserFile);
			this.hibernateTemplate.bulkUpdate(updateUserFile);
			// ���³����¼ʵ��Ƿ��
			String updateHandplan = "update t_handplan set f_debtmoney="
					+ newdebtmoney.doubleValue() + " where id='" + handId + "'";
			log.debug("���³���Ƿ��" + updateHandplan);
			this.hibernateTemplate.bulkUpdate(updateHandplan);
		}
	}

	/**
	 * �����û�������ϸ
	 */
	private void financedetailSave(int handId, String f_userid,
			BigDecimal ysMoney, BigDecimal oldAccountZhye, BigDecimal realmony,
			BigDecimal unitprice, BigDecimal debtmoney,
			BigDecimal newaccountzhye, String sgnetwork, String sgoperator,
			int sellid, Object chaobiaoDate) throws Exception {
		// ������Ƿ����
		Date now = new Date();
		Map<String, Object> finance = new HashMap<String, Object>();
		// <!--���ս��-->
		finance.put("f_realmoney", realmony.doubleValue());
		// <!--Ƿ�ѽ��-->
		finance.put("f_debtmoney", debtmoney.doubleValue());
		// <!--�˻�����-->
		finance.put("f_accountzhye", newaccountzhye.doubleValue());
		// <!--�û����-->
		finance.put("f_userid", f_userid);
		// ԭ�˻����
		finance.put("f_prevaccountzhye", oldAccountZhye.doubleValue());
		// <!--Ӧ�ս��-->
		finance.put("f_oughtfee", ysMoney.doubleValue());
		// ����
		finance.put("f_gasprice", unitprice.doubleValue());
		// �����¼������
		finance.put("f_debtdate", chaobiaoDate);
		// <!--�Ƿ���Ч(��Ч/��Ч)-->
		finance.put("f_payfeevalid", "��Ч");
		finance.put("f_payfeetype", "����");
		// <!--����-->
		finance.put("f_sgnetwork", sgnetwork);
		// <!--����Ա-->
		finance.put("f_opertor", sgoperator);
		// �������ڣ�ʱ��
		finance.put("f_deliverydate", now);
		finance.put("f_deliverytime", now);
		// ����id
		finance.put("f_handid", handId);
		// ����id
		finance.put("f_sellid", sellid);
		// ����
		JSONObject financeJson = (JSONObject) new JsonTransfer()
				.MapToJson(finance);
		log.debug("������Ƿ��ϸ����" + financeJson);
		Object idObj = hibernateTemplate.save("t_financedetail", finance);
		int saveId = Integer.parseInt(idObj.toString());
		log.debug("����ɹ�,����id" + saveId);
	}

	// ���ҳ����¼
	private List<Map<String, Object>> findHanplans(String userid) {
		String sql = " select u.f_zhye f_zhye, u.f_accountzhye f_accountzhye, u.f_username f_username,u.f_cardid f_cardid, u.f_address f_address,u.f_districtname f_districtname,u.f_cusDom f_cusDom,u.f_cusDy f_cusDy,u.f_beginfee f_beginfee, u.f_metergasnums f_metergasnums, u.f_cumulativepurchase f_cumulativepurchase,"
				+ "u.f_idnumber f_idnumber, u.f_gaspricetype f_gaspricetype, u.f_gasprice f_gasprice, u.f_usertype f_usertype,"
				+ "u.f_gasproperties f_gasproperties, u.f_userid f_userid, h.id handid, h.oughtamount oughtamount, h.lastinputgasnum lastinputgasnum,"
				+ "h.lastrecord lastrecord, h.shifoujiaofei shifoujiaofei, h.oughtfee oughtfee,h.f_debtmoney  f_debtmoney ,h.lastinputdate from t_userfiles u "
				+ "left join (select * from t_handplan where f_state = '�ѳ���' and shifoujiaofei = '��') h on u.f_userid = h.f_userid where u.f_userid = '"
				+ userid
				+ "' "
				+ "order by u.f_userid, h.lastinputdate, h.lastinputgasnum";
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> result = this.hibernateTemplate
				.executeFind(sqlCall);
		return result;
	}

	// ���ҵ�½�û�
	private Map<String, Object> findUser(String loginId) {
		String findUser = "from t_user where id='" + loginId + "'";
		List<Object> userList = this.hibernateTemplate.find(findUser);
		if (userList.size() != 1) {
			return null;
		}
		return (Map<String, Object>) userList.get(0);
	}

	// ִ��sql��ѯ
	class HibernateSQLCall implements HibernateCallback {
		String sql;

		public HibernateSQLCall(String sql) {
			this.sql = sql;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List result = q.list();
			return result;
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

	// ת��������ת���ڼ��������Ƿ��Ѿ�ת��������������ת����������ѭ��
	class JsonTransfer {
		// �����Ѿ�ת�����Ķ���
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		// �ѵ���mapת����JSON����
		public Object MapToJson(Map<String, Object> map) {
			// ת���������ؿն���
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					String key = entry.getKey();
					Object value = entry.getValue();
					// ��ֵת����JSON�Ŀն���
					if (value == null) {
						value = JSONObject.NULL;
					} else if (value instanceof HashMap) {
						value = MapToJson((Map<String, Object>) value);
					}
					// �����$type$����ʾʵ�����ͣ�ת����EntityType
					if (key.equals("$type$")) {
						json.put("EntityType", value);
					} else if (value instanceof Date) {
						Date d1 = (Date) value;
						Calendar c = Calendar.getInstance();
						long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
						json.put(key, time);
					} else if (value instanceof MapProxy) {
						// MapProxyû�м��أ�����
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						// û���صļ��ϲ���
						if (set.wasInitialized()) {
							json.put(key, ToJson(set));
						}
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
		public Object ToJson(PersistentSet set) {
			JSONArray array = new JSONArray();
			for (Object obj : set) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
				array.put(json);
			}
			return array;
		}

		// �ж��Ѿ�ת�������������Ƿ������������
		public boolean contains(Map<String, Object> obj) {
			for (Map<String, Object> map : this.transed) {
				if (obj == map) {
					return true;
				}
			}
			return false;
		}
	}

}
