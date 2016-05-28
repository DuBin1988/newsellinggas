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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.exception.RSException;
import com.aote.rs.exception.ResultException;

@Path("sell")
@Scope("prototype")
@Component
public class SellSer {
	static Logger log = Logger.getLogger(SellSer.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;

	// ��ѯǷ����Ϣ
	// ��ȡĳ����Ż�����Ϣ��Ƿ������
	@GET
	@Path("bill/{userid}")
	public String getUserBill(@PathParam("userid") String userid)
			throws Exception {
		String result = "{";

		// ��ȡ���е�ֵ
		Map<String, String> singles = getSingles();

		// ��ȡ�û������������¼���
		String sql = "select "
				+ "isnull(ui.f_zhye,0) f_zhye,isnull(ui.f_username,'������') f_username,isnull(ui.f_zherownum,13) f_zherownum,isnull(u.f_usertype,'����') f_usertype,"
				+ "isnull(u.f_districtname,'��С��') f_districtname,isnull(u.f_address,'�յ�ַ') f_address,"
				+ "isnull(u.f_gasproperties,'��ͨ����') f_gasproperties,isnull(u.f_gaspricetype,'��������') f_gaspricetype,"
				+ "ui.f_userid infoid,isnull(u.f_gasprice,0) f_gasprice,isnull(u.f_dibaohu,0) f_dibaohu,"
				+ "isnull(u.f_payment,'�ֽ�') f_payment,isnull(u.f_stairtype,'δ��') f_stairtype,isnull(ui.f_userstate,'����') f_userstate," // ui
																																		// t_userinfo
				+ "" // u t_userfiles
				+ "h.days days,h.f_userid f_userid,isnull(h.oughtamount,0) oughtamount,"
				+ "isnull(h.oughtfee,0) oughtfee,h.lastinputdate lastinputdate,h.lastinputgasnum lastinputgasnum,"
				+ "h.lastrecord lastrecord,h.f_endjfdate f_endjfdate,h.f_operator f_operator,"
				+ "h.f_inputdate f_inputdate,h.f_network f_network,h.f_handdate f_handdate,"
				+ "h.id handId ,isnull(h.f_stair1amount,0) f_stair1amount ,isnull(h.f_stair1price,0) f_stair1price, isnull(h.f_stair1fee,0) f_stair1fee, isnull(h.f_stair2amount,0) f_stair2amount, isnull(h.f_stair2price,0) f_stair2price, isnull(h.f_stair2fee,0) f_stair2fee"
				+ ", isnull(h.f_stair3amount,0) f_stair3amount, isnull(h.f_stair3price,0) f_stair3price, isnull(h.f_stair3fee,0) f_stair3fee"
				+ // h t_handplan
				"  from (select * from t_userinfo where f_userid='"
				+ userid
				+ "') ui join t_userfiles u on ui.f_userid=u.f_userinfoid and u.f_gasmeterstyle='����'"
				+ "left join (select datediff(day,isnull(f_endjfdate,GETDATE()),GETDATE()) days,* from t_handplan where f_state='�ѳ���' and shifoujiaofei='��') h "
				+ "on u.f_userid=h.f_userid "
				+ "order by u.f_userid, h.lastinputdate, h.lastinputgasnum";
		log.debug("��ѯǷ��sql:" + sql);
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(new HibernateSQLCall(sql));

		// �ӵ�һ����ȡ������
		Map<String, Object> userinfo = (Map<String, Object>) list.get(0);
		result += "infoid:" + userinfo.get("infoid") + "";
		result += ",f_username:'" + (String) userinfo.get("f_username") + "'";
		BigDecimal zz = new BigDecimal(userinfo.get("f_zherownum").toString());
		result += ",f_zherownum:" + zz;
		result += ",f_address:'" + (String) userinfo.get("f_address") + "'";
		// �û�����
		BigDecimal f_zhye = new BigDecimal(userinfo.get("f_zhye").toString());
		result += ",f_zhye:" + f_zhye;
		String f_usertype = (String) userinfo.get("f_usertype");
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

		if (f_usertype.equals("����")) {
			scale = new BigDecimal(singles.get("�������ɽ����"));
		} else {
			scale = new BigDecimal(singles.get("���������ɽ����"));
		}

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
			BigDecimal oughtfee = new BigDecimal(hand.get("oughtfee")
					.toString());

			hands += ",f_stair1amount:" + hand.get("f_stair1amount");
			hands += ",f_stair1price:" + hand.get("f_stair1price");
			hands += ",f_stair1fee:" + hand.get("f_stair1fee");

			hands += ",f_stair2amount:" + hand.get("f_stair2amount");
			hands += ",f_stair2price:" + hand.get("f_stair2price");
			hands += ",f_stair2fee:" + hand.get("f_stair2fee");

			hands += ",f_stair3amount:" + hand.get("f_stair3amount");
			hands += ",f_stair3price:" + hand.get("f_stair3price");
			hands += ",f_stair3fee:" + hand.get("f_stair3fee");

			int days = Integer.parseInt(hand.get("days") + "");
			days = days > 0 ? days : 0;

			BigDecimal f_zhinajin = new BigDecimal("0");
			// ��������ɽ𣬼������ȥ������
			int equals = f_zhye.compareTo(new BigDecimal("0"));// �Ƚ�����Ƿ����0
			if (equals > 0) {
				int bigDec = f_zhye.compareTo(oughtfee);// �ж�����Ƿ��������
				oughtfee = bigDec > 0 ? new BigDecimal("0") : oughtfee
						.subtract(f_zhye);
				f_zhye = bigDec > 0 ? f_zhye.subtract(oughtfee)
						: new BigDecimal("0");
			}
			f_zhinajin = oughtfee.multiply(new BigDecimal(days + "")).multiply(
					scale);
			f_zhinajin = f_zhinajin.setScale(2, BigDecimal.ROUND_HALF_UP);
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

	}

	/**
	 * ����sell������������
	 * 
	 * @param userid
	 *            �û����
	 * @param dMoney
	 *            �տ�
	 * @param dZhinajin
	 *            ���ɽ�
	 * @param payments
	 *            ���ʽ
	 * @param opid
	 *            ����Աid
	 * @param orgstr
	 *            ��֯��Ϣ��ǰ̨��ȡ���뵽��̨����
	 * @return
	 */

	@GET
	@Path("{userid}/{money}/{zhinajin}/{payment}/{opid}/{orgstr}")
	public JSONObject txSell(@PathParam("userid") String userid,
			@PathParam("money") BigDecimal dMoney,
			@PathParam("zhinajin") double zhinajin,
			@PathParam("payment") String payments,
			@PathParam("opid") String opid, @PathParam("orgstr") String orgstr) {
		JSONObject ret = new JSONObject();
		try {
			log.debug("�������� ��ʼ");
			BigDecimal payMent = dMoney;
			// ��ѯ�û�
			Map user = this.findUserinfo(userid);
			// ��ѯ������Ƿ����Ϣ
			Map<String, Object> nopayMap = getnopayinfor(userid);
			// ��ȡÿ����Ľ�����Ϣ
			// JSONObject files_stair = this.getfilesInfor(userid);
			List<Map<String, Object>> hands = this.findHands(userid);

			// ѭ��Ƿ�Ѽ�¼����¼ Ƿ��ids,��Сָ�������ָ����Ƿ�����������ϼ�userid
			String handIds = "";
			double lastinputgasnum = 0;
			double lastrecord = 0;
			Date lastinputdate = null;
			BigDecimal debts = new BigDecimal(0);
			BigDecimal debtGas = new BigDecimal(0);
			for (int i = 0; i < hands.size(); i++) {
				Map<String, Object> hand = (Map<String, Object>) hands.get(i);
				BigDecimal d = new BigDecimal(hand.get("oughtfee").toString());
				debts = debts.add(d);
				BigDecimal g = new BigDecimal(hand.get("oughtamount")
						.toString());
				debtGas = debtGas.add(g);
				handIds += hand.get("id") + ",";
				// ���ָ��
				if (i == 0) {
					lastrecord = Double.parseDouble(hand.get("lastrecord")
							.toString());
					lastinputdate = (Date) hand.get("lastinputdate");
				}
				// ��Сָ��
				if (i == hands.size() - 1) {
					lastinputgasnum = Double.parseDouble(hand.get(
							"lastinputgasnum").toString());
				}
			}
			if (handIds.endsWith(",")) {
				handIds = handIds.substring(0, handIds.length() - 1);
			}
			// �ȼ���payment >=�û�����+�û�Ƿ��
			BigDecimal jieyu = new BigDecimal(user.get("f_zhye").toString());
			if (payMent.compareTo(debts.subtract(jieyu)) < 0) {
				throw new ResultException("���ѽ��:" + payMent + "�������ɱ���Ƿ��:"
						+ debts.subtract(jieyu));
			}
			// �������,�������ڣ����ۼ����������ۼ�����
			BigDecimal nowye = payMent.subtract(debts.subtract(jieyu));

			BigDecimal metergasnums = new BigDecimal(user.get("f_metergasnums")
					.toString());
			BigDecimal newMeterGasNums = metergasnums.add(debtGas);
			BigDecimal cumuGas = new BigDecimal(user
					.get("f_cumulativepurchase").toString());
			BigDecimal newCumuGas = cumuGas.add(debtGas);
			// �����û�
			this.updateUser(user, nowye, debtGas, newMeterGasNums, newCumuGas);
			// ���³���Ƿ��Ϊ�ѽɷ�
			if (handIds != null && !handIds.equals("")) {
				updateHands(handIds);
			}
			// ���뽻�Ѽ�¼
			ret = insertSell(user, nopayMap, nowye, lastinputgasnum,
					lastrecord, debts, debtGas, handIds, lastinputdate,
					payMent, metergasnums, cumuGas, newMeterGasNums,
					newCumuGas, opid, payments, orgstr, zhinajin);
			log.debug("�������ѳɹ�!" + ret);
			ret.put("success", "�����ѳɹ�");
			// ץȡ�Զ����쳣
		} catch (Exception ex) {
			ex.printStackTrace();
			hibernateTemplate.getSessionFactory().getCurrentSession()
					.getTransaction().rollback();
			log.error("�������� ʧ��!" + ex.getMessage());
			ret.put("error", ex.getMessage());
		} finally {
			return ret;
		}
	}

	// ���ҵ�½�û�
	private Map<String, Object> findloginUser(String loginId) {
		String findUser = "from t_user where id='" + loginId + "'";
		List<Object> userList = this.hibernateTemplate.find(findUser);
		if (userList.size() != 1) {
			return null;
		}
		return (Map<String, Object>) userList.get(0);
	}

	/**
	 * �����շѼ�¼�����ر�����id
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject insertSell(Map<String, Object> userMap,
			Map<String, Object> nopayMap, BigDecimal nowye,
			double lastinputgasnum, double lastrecord, BigDecimal debts,
			BigDecimal debtGas, String handIds, Date lastinputdate,
			BigDecimal payMent, BigDecimal metergasnums, BigDecimal cumuGas,
			BigDecimal newMeterGasNums, BigDecimal newCumuGas, String opid,
			String payments, String orgstr, double zhinajin) throws Exception {
		JSONObject result = new JSONObject();
		// ���ҵ�½�û�,��ȡ��½����,����Ա
		Map<String, Object> loginUser = this.findloginUser(opid);
		loginUser.put("orgstr", orgstr);
		if (loginUser == null) {
			log.debug("����ɷѴ���ʱδ�ҵ���½�û�,��½id" + opid);
			throw new ResultException("����ɷѴ���ʱδ�ҵ���½�û�,��½id" + opid);
		}
		Map sale = new HashMap<String, Object>();
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sale.put("lastinputgasnum", nopayMap.get("lastinputgasnum"));
		sale.put("lastrecord", nopayMap.get("lastrecord"));
		sale.put("f_preamount", debts.doubleValue());
		sale.put("f_pregas", debtGas.doubleValue());
		sale.put("f_zhinajin", zhinajin);
		sale.put("lastinputdate", lastinputdate);
		sale.put("f_yhxz", userMap.get("f_yhxz"));
		sale.put("f_zhye", userMap.get("f_zhye"));
		sale.put("f_benqizhye", nowye.doubleValue());
		sale.put("f_gasmeterstyle", "����");
		sale.put("f_comtype", "��Ȼ����˾");
		sale.put("f_apartment", userMap.get("f_apartment"));
		sale.put("f_userid", userMap.get("f_userid"));
		sale.put("f_userinfoid", userMap.get("f_userid"));// �û�id
		sale.put("f_username", userMap.get("f_username"));
		sale.put("f_address", userMap.get("f_address"));
		sale.put("f_districtname", userMap.get("f_districtname"));
		// sale.put("f_idnumber", userMap.get("f_idnumber"));
		sale.put("f_gaswatchbrand", userMap.get("f_gaswatchbrand"));
		sale.put("f_metertype", userMap.get("f_metertype"));
		sale.put("f_gaspricetype", userMap.get("f_gaspricetype"));
		sale.put("f_gasprice", userMap.get("f_gasprice"));
		sale.put("f_usertype", userMap.get("f_usertype"));
		sale.put("f_gasproperties", userMap.get("f_gasproperties"));
		sale.put("f_beginfee", userMap.get("f_beginfee"));
		sale.put("f_finallygas", debtGas.doubleValue());
		sale.put("f_finallybought", debtGas.doubleValue());
		sale.put("f_finabuygasdate", now);
		sale.put("f_payment", "�ֽ�");
		sale.put("f_upbuynum", cumuGas.doubleValue());
		sale.put("f_premetergasnums", metergasnums.doubleValue());
		sale.put("f_grossproceeds", payMent.doubleValue());
		sale.put("f_totalcost", debts.doubleValue());
		sale.put("f_givechange", 0.0);
		sale.put("f_meternumber", userMap.get("f_meternumber"));
		sale.put("f_payment", payments); // ���ʽ
		sale.put("f_paytype", "�ֽ�"); // �������ͣ����д���/�ֽ�
		sale.put("f_sgnetwork", loginUser.get("f_parentname").toString()); // ����
		sale.put("f_sgoperator", loginUser.get("name").toString()); // �� �� Ա
		sale.put("f_filiale", loginUser.get("f_fengongsi").toString()); // �ֹ�˾
		sale.put("f_fengongsinum", loginUser.get("f_fengongsinum").toString()); // �ֹ�˾���
		sale.put("f_orgstr", loginUser.get("orgstr").toString()); // ��֯��Ϣ
		sale.put("f_payfeetype", "�����շ�"); // ��������
		sale.put("f_payfeevalid", "��Ч"); // ������Ч����
		sale.put("f_useful", handIds); // �����¼id
		sale.put("f_deliverydate", now);
		sale.put("f_deliverytime", now);

		sale.put("f_jiezhangstate", "δ����");
		sale.put("f_wheatherduizhang", "δ����");

		sale.put("f_amountmaintenance", 0.0);
		sale.put("f_metergasnums", newMeterGasNums.doubleValue());
		sale.put("f_cumulativepurchase", newCumuGas.doubleValue());
		sale.put("f_stairtype", userMap.get("f_stairtype"));
		sale.put("f_stair1price", nopayMap.get("f_stair1price"));
		sale.put("f_stair1amount", nopayMap.get("f_stair1amount"));
		sale.put("f_stair1fee", nopayMap.get("f_stair1fee"));
		sale.put("f_stair2price", nopayMap.get("f_stair2price"));
		sale.put("f_stair2amount", nopayMap.get("f_stair2amount"));
		sale.put("f_stair2fee", nopayMap.get("f_stair2fee"));
		sale.put("f_stair3price", nopayMap.get("f_stair3price"));
		sale.put("f_stair3amount", nopayMap.get("f_stair3amount"));
		sale.put("f_stair3fee", nopayMap.get("f_stair3fee"));
		sale.put("f_OrgStr", userMap.get("f_OrgStr") + "");
		sale.put("f_zherownum", userMap.get("f_zherownum")); // ���Ӻ�
		log.debug("���Ѽ�¼������Ϣ��" + sale.toString());
		// session.save("t_sellinggas", sale);
		int sellId = (Integer) hibernateTemplate.save("t_sellinggas", sale);
		// ��ʽ����������
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		result.put("id", sellId);
		result.put("f_deliverydate", f2.format(now));
		result.put("f_stair1price", nopayMap.get("f_stair1price"));
		result.put("f_stair1amount", nopayMap.get("f_stair1amount"));
		result.put("f_stair1fee", nopayMap.get("f_stair1fee"));
		result.put("f_stair2price", nopayMap.get("f_stair2price"));
		result.put("f_stair2amount", nopayMap.get("f_stair2amount"));
		result.put("f_stair2fee", nopayMap.get("f_stair2fee"));
		result.put("f_stair3price", nopayMap.get("f_stair3price"));
		result.put("f_stair3amount", nopayMap.get("f_stair3amount"));
		result.put("f_stair3fee", nopayMap.get("f_stair3fee"));
		// ���³����¼sellid
		if (handIds != null && !handIds.equals("") && !handIds.equals("0")) {
			String updateHandplan = "update t_handplan set f_sellid =" + sellId
					+ " where id in (" + handIds + ")";
			log.debug("���³����¼sql��" + updateHandplan);
			hibernateTemplate.bulkUpdate(updateHandplan);
		}
		return result;
	}

	/**
	 * ���³����¼Ϊ��Ƿ��
	 */
	private void updateHands(String handIds) throws Exception {
		String sql = "update t_handplan set shifoujiaofei='��' where id in ("
				+ handIds + ")";
		// session.createQuery(sql).executeUpdate();
		log.debug("���³�����Ϣ��ʼ:" + sql);
		this.hibernateTemplate.bulkUpdate(sql);
	}

	/**
	 * �����û���Ϣ
	 */
	private void updateUser(Map user, BigDecimal nowye, BigDecimal debtGas,
			BigDecimal newMeterGasNums, BigDecimal newCumuGas) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String dt = format.format(now);
		String tm = format.format(now);
		// �����к�
		String f_zherownum = user.get("f_zherownum") + "";
		if (f_zherownum == "" || user.get("f_zherownum") == null) {
			f_zherownum = "13";
		}
		int zherownum = Integer.parseInt(f_zherownum);
		// �����к�Ϊ24������
		if (zherownum >= 24) {
			zherownum = 0;
		}
		user.put("f_zherownum", zherownum);
		// �����û�
		String sql = "update t_userinfo  set f_zhye=" + nowye
				+ ", f_finabuygasdate='" + dt + "', f_finabuygastime='" + tm
				+ "'," + " f_metergasnums=" + newMeterGasNums
				+ ", f_cumulativepurchase=" + newCumuGas + ",f_zherownum="
				+ (zherownum + 1) + " where f_userid='" + user.get("f_userid")
				+ "'";
		// this.session.createQuery(sql).executeUpdate();
		log.debug("���»���Ϣ��ʼ:" + sql);
		this.hibernateTemplate.bulkUpdate(sql);
	}

	/**
	 * ���ҳ���Ƿ�Ѽ�¼
	 */
	private List findHands(String userId) throws Exception {
		final String sql = "select h.oughtfee oughtfee, h.oughtamount oughtamount, h.id id,h.lastrecord lastrecord, h.lastinputdate lastinputdate,h.lastinputgasnum lastinputgasnum from t_handplan h , t_userfiles u where u.f_userinfoid='"
				+ userId
				+ "' and h.shifoujiaofei='��' and h.lastrecord is not null and h.f_state='�ѳ���' and h.f_userid=u.f_userid order by h.id desc";
		// List list = session.createQuery(sql).list();
		log.debug("��ѯǷ����Ϣ��ʼ:" + sql);
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(sqlCall);
		return list;
	}

	/**
	 * ��ȡÿ����Ľ�����Ϣ
	 * 
	 * @param userinfoId
	 * @return
	 * @throws Exception
	 */
	private JSONObject getfilesInfor(String userinfoId) throws Exception {
		final String sql = "select   u.f_userid f_userid,ISNULL(u.f_meternumber,' ') f_meternumber,MIN(h.lastinputgasnum) lastinputgasnum,MAX(h.lastrecord) lastrecord, min(h.f_handdate) f_handdatemin, max(h.f_handdate) f_handdatemax, min(u.f_stair1price) f_stair1price, Round(SUM(isnull(h.f_stair1amount,0)),2) f_stair1amount,"
				+ "Round(SUM(isnull(h.f_stair1fee,0)),2) f_stair1fee,min(u.f_stair2price) f_stair2price,"
				+ "Round(SUM(isnull(h.f_stair2amount,0)),2) f_stair2amount, Round(SUM(isnull(h.f_stair2fee,0)),2) f_stair2fee,"
				+ "min(u.f_stair3price) f_stair3price, Round(SUM(isnull(h.f_stair3amount,0)),2) f_stair3amount,"
				+ "Round(SUM(isnull(h.f_stair3fee,0)),2) f_stair3fee "
				+ "  from t_userfiles u,t_handplan h where u.f_userid=h.f_userid and "
				+ "u.f_userinfoid='"
				+ userinfoId
				+ "' and "
				+ "h.shifoujiaofei='��' and h.f_state='�ѳ���' and h.lastrecord is not null "
				+ "group by u.f_userid ,u.f_meternumber";
		log.debug("��ѯÿ�����������Ϣ��ʼ:" + sql);
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(sqlCall);
		String result = "{";
		if (list.size() > 0) {
			Map<String, Object> map0 = (Map<String, Object>) list.get(0);

			result += "userid1:'" + map0.get("f_userid").toString() + "'";
			result += ",meterid1:'" + map0.get("f_meternumber").toString()
					+ "'";
			result += ",minnum1:" + map0.get("lastinputgasnum") + "";
			result += ",maxnum1:" + map0.get("lastrecord") + "";

			result += ",f_stair1amount1:" + map0.get("f_stair1amount") + "";
			result += ",f_stair1fee1:" + map0.get("f_stair1fee") + "";

			result += ",f_stair2amount1:" + map0.get("f_stair2amount") + "";
			result += ",f_stair2fee1:" + map0.get("f_stair2fee") + "";

			result += ",f_stair3amount1:" + map0.get("f_stair3amount") + "";
			result += ",f_stair3fee1:" + map0.get("f_stair3fee") + "";
			result += ",minyue1:'" + map0.get("f_handdatemin") + "'";
			result += ",maxyue1:'" + map0.get("f_handdatemax") + "'";
		} else {
			result += "userid1:" + 0 + "";
			result += ",meterid1:" + 0 + "";
			result += ",minnum1:" + 0 + "";
			result += ",maxnum1:" + 0 + "";
			result += ",f_stair1amount1:" + 0 + "";
			result += ",f_stair1fee1:" + 0 + "";
			result += ",f_stair2amount1:" + 0 + "";
			result += ",f_stair2fee1:" + 0 + "";
			result += ",f_stair3amount1:" + 0 + "";
			result += ",f_stair3fee1:" + 0 + "";
			result += ",minyue1:" + 0 + "";
			result += ",maxyue1:" + 0 + "";
		}

		if (list.size() > 1) {
			Map<String, Object> map1 = (Map<String, Object>) list.get(1);
			result += ",userid2:'" + map1.get("f_userid").toString() + "'";
			result += ",meterid2:'" + map1.get("f_meternumber").toString()
					+ "'";
			result += ",minnum2:" + map1.get("lastinputgasnum") + "";
			result += ",maxnum2:" + map1.get("lastrecord") + "";
			result += ",f_stair1amount2:" + map1.get("f_stair1amount") + "";
			result += ",f_stair1fee2:" + map1.get("f_stair1fee") + "";
			result += ",f_stair2amount2:" + map1.get("f_stair2amount") + "";
			result += ",f_stair2fee2:" + map1.get("f_stair2fee") + "";
			result += ",f_stair3amount2:" + map1.get("f_stair3amount") + "";
			result += ",f_stair3fee2:" + map1.get("f_stair3fee") + "";
			result += ",minyue2:'" + map1.get("f_handdatemin") + "'";
			result += ",maxyue2:'" + map1.get("f_handdatemax") + "'";
		} else {
			result += ",userid2:" + 0 + "";
			result += ",meterid2:" + 0 + "";
			result += ",minnum2:" + 0 + "";
			result += ",maxnum2:" + 0 + "";
			result += ",f_stair1amount2:" + 0 + "";
			result += ",f_stair1fee2:" + 0 + "";
			result += ",f_stair2amount2:" + 0 + "";
			result += ",f_stair2fee2:" + 0 + "";
			result += ",f_stair3amount2:" + 0 + "";
			result += ",f_stair3fee2:" + 0 + "";
			result += ",minyue2:" + 0 + "";
			result += ",maxyue2:" + 0 + "";
		}

		result += "}";
		JSONObject r = new JSONObject(result);
		return r;
	}

	/**
	 * ��ѯ�û���Ƿ����Ϣ
	 * 
	 * @param userinfoId
	 * @return ���һ��������ڵ���Ϊÿ��Ƿ�����ڵ����ϼƣ�����ͬ��
	 */
	private Map<String, Object> getnopayinfor(String userinfoId) {
		final String sql = "select f_stair1price f_stair1price, f_stair1amount f_stair1amount, f_stair1fee f_stair1fee, f_stair2price f_stair2price, f_stair2amount f_stair2amount,"
				+ " f_stair2fee f_stair2fee, f_stair3price f_stair3price, f_stair3amount f_stair3amount, f_stair3fee f_stair3fee from "
				+ "(select sum(h.lastinputgasnum) lastinputgasnum,sum(h.lastrecord) lastrecord, min(u.f_stair1price) f_stair1price, Round(SUM(isnull(h.f_stair1amount,0)),2) f_stair1amount,"
				+ " Round(SUM(isnull(h.f_stair1fee,0)),2) f_stair1fee,min(u.f_stair2price) f_stair2price,"
				+ " Round(SUM(isnull(h.f_stair2amount,0)),2) f_stair2amount, Round(SUM(isnull(h.f_stair2fee,0)),2) f_stair2fee,"
				+ " min(u.f_stair3price) f_stair3price, Round(SUM(isnull(h.f_stair3amount,0)),2) f_stair3amount,"
				+ " Round(SUM(isnull(h.f_stair3fee,0)),2) f_stair3fee from(select * from t_userfiles"
				+ " where f_userinfoid='"
				+ userinfoId
				+ "') u left join (select * from t_handplan where shifoujiaofei='��'"
				+ " and f_state='�ѳ���' and lastrecord is not null) h on u.f_userid=h.f_userid) t";
		log.debug("��ѯ���������Ϣ��ʼ:" + sql);
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(sqlCall);
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		return map;
	}

	// ����sell������������,��������һ�����Ľ���
	@GET
	@Path("{userid}/{id}/{opid}")
	public JSONObject txSell_cz(@PathParam("userid") String userid,
			@PathParam("id") String id, @PathParam("opid") String opid) {
		// ������Ϣ��Ϊ��������ɹ�����Ϊ�������ʧ�ܣ�����Ϊ������Ϣ
		JSONObject ret = new JSONObject();
		try {
			// final String sql_1 =
			// "SELECT id,f_userid,isnull(lastinputgasnum,0)lastinputgasnum,isnull(lastrecord,0)lastrecord,isnull(f_totalcost,0)f_totalcost,isnull(f_grossproceeds,0)f_grossproceeds,"
			// +
			// "isnull(f_zhinajin,0)f_zhinajin,isnull(f_zhye,0)f_zhye,isnull(f_benqizhye,0)f_benqizhye,f_beginfee,isnull(f_premetergasnums,0)f_premetergasnums,isnull(f_upbuynum,0)f_upbuynum,f_gasmeterstyle,"
			// +
			// "f_comtype,f_username,f_address,f_districtname,f_cusDom,f_cusDy,f_idnumber,f_gaswatchbrand,"
			// +
			// "f_gaspricetype,f_gasprice,f_usertype,f_gasproperties,f_cardid,isnull(f_pregas,0)f_pregas,isnull(f_preamount,0)f_preamount,f_payment,"
			// +
			// "f_sgnetwork,f_sgoperator,f_filiale,f_fengongsinum,f_payfeetype,f_useful FROM t_sellinggas where f_userid='"
			// + userid + "' and id='" + id + "'" + "";
			// List list = (List) hibernateTemplate
			// .execute(new HibernateCallback() {
			// public Object doInHibernate(Session session)
			// throws HibernateException {
			// SQLQuery query = session.createSQLQuery(sql_1);
			// return query.list();
			// }
			// });

			String sql = "from t_sellinggas where id=" + id;
			List list = this.hibernateTemplate.find(sql);
			// �ҵ������¼���ж����ںͻ������
			if (list.size() == 1) {
				// ���ҵ�½�û�,��ȡ��½����,����Ա
				Map<String, Object> loginUser = this.findloginUser(opid);
				Map<String, Object> userinfo = this.findUserinfo(userid);
				if (loginUser == null) {
					log.debug("����ɷѴ���ʱδ�ҵ���½�û�,��½id" + opid);
					throw new RSException("����ɷѴ���ʱδ�ҵ���½�û�,��½id" + opid);
				}
				Map<String, Object> b_sell = (Map<String, Object>) list.get(0);
				// �������Ѽ�¼
				Map<String, Object> sell = new HashMap<String, Object>();
				sell.putAll(b_sell);
				sell.remove("id");
				Date now = new Date();
				sell.put("f_deliverydate", now); // ��������
				sell.put("f_deliverytime", now); // ����ʱ��
				// �������ع�������
				SimpleDateFormat fmt = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				// ret=fmt.format(now);
				sell.put("f_deliverydate_tb", fmt.format(now)); // ͬ�� ����ʱ��
				sell.put("f_status_tb", "1"); // ͬ�� ״̬
				sell.put("f_grossproceeds",
						-Double.parseDouble(sell.get("f_grossproceeds") + ""));
				sell.put("f_pregas",
						-Double.parseDouble(sell.get("f_pregas") + "")); // ����
				sell.put("f_preamount",
						-Double.parseDouble(sell.get("f_preamount") + "")); // ����
				sell.put("f_payment", "����"); // ���ʽ
				sell.put("f_paytype", "�ֽ�"); // �������ͣ����д���/�ֽ�
				sell.put("f_sgnetwork", loginUser.get("f_parentname")
						.toString()); // ����
				sell.put("f_sgoperator", loginUser.get("name").toString()); // ����Ա
				sell.put("f_filiale", loginUser.get("f_fengongsi").toString()); // �ֹ�˾
				sell.put("f_fengongsinum", loginUser.get("f_fengongsinum")
						.toString()); // �ֹ�˾���
				sell.put("f_payfeevalid", "��Ч"); // ������Ч����
				log.debug("���Ѽ�¼������Ϣ��" + sell.toString());
				int sellId = (Integer) hibernateTemplate.save("t_sellinggas",
						sell);
				// ID ����ȥ
				if (sellId > 0) {
					execSQL("update t_sellinggas set f_payfeevalid='��Ч',f_payment='����'  where f_userid='"
							+ userid + "' and id='" + id + "'" + "");
				} else
					ret.put("error", "noid");
			} else {
				ret.put("error", "noid");
			}
			ret.put("success", "��������ɹ�");
		} catch (RSException e) {
			log.debug("�������� ʧ��!");
			ret.put("error", e.getMessage());
		} catch (Exception ex) {
			log.debug("�������� ʧ��!" + ex.getMessage());
			ret.put("error", ex.getMessage());
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
			List<Map<String, Object>> hands, BigDecimal shoukuan, int sellid)
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
			// ԭ������
			BigDecimal oldAccountzhye = shoukuan;
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
			// ����Ƿ��¼
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
		String sql = " select u.f_zhye f_zhye, u.f_accountzhye f_accountzhye, u.f_username f_username,u.f_books f_books,u.f_cardid f_cardid, u.f_address f_address,u.f_districtname f_districtname,u.f_cusDom f_cusDom,u.f_cusDy f_cusDy,u.f_beginfee f_beginfee, u.f_metergasnums f_metergasnums, u.f_cumulativepurchase f_cumulativepurchase,"
				+ "u.f_idnumber f_idnumber, u.f_gaspricetype f_gaspricetype, u.f_gasprice f_gasprice, u.f_usertype f_usertype,"
				+ "u.f_gasproperties f_gasproperties, u.f_userid f_userid,u.f_zherownum f_zherownum, h.id handid, h.oughtamount oughtamount, h.lastinputgasnum lastinputgasnum,"
				+ "h.lastrecord lastrecord, h.shifoujiaofei shifoujiaofei, h.oughtfee oughtfee,h.f_debtmoney  f_debtmoney ,h.lastinputdate from t_userfiles u "
				+ "left join (select * from t_handplan where f_state = '�ѳ���' and shifoujiaofei = '��' and f_userid='"
				+ userid
				+ "') h on u.f_userid = h.f_userid where u.f_userid = '"
				+ userid
				+ "' "
				+ "order by u.f_userid, h.lastinputdate, h.lastinputgasnum";
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> result = this.hibernateTemplate
				.executeFind(sqlCall);
		return result;
	}

	/**
	 * ���һ���Ϣ
	 * 
	 * @param userid
	 * @return
	 */
	private Map<String, Object> findUserinfo(String userid) {
		final String userSql = "from t_userinfo  where f_userid='" + userid
				+ "'  ";
		// List userlist = session.createQuery(userSql).list();
		log.debug("��ѯ����Ϣ��ʼ:" + userSql);
		List<Object> userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return null;
		}
		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
		return userMap;
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

	// ��ȡ���е�ֵ��ת����Map
	private Map<String, String> getSingles() {
		Map result = new HashMap<String, String>();
		String sql = "select name,value from t_singlevalue";
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(new HibernateSQLCall(sql));
		for (Map<String, Object> hand : list) {
			result.put(hand.get("name"), hand.get("value"));
		}
		return result;
	}

	/**
	 * execute sql in hibernate
	 * 
	 * @param sql
	 */
	private void execSQL(final String sql) {
		hibernateTemplate.execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				session.createSQLQuery(sql).executeUpdate();
				return null;
			}
		});
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
