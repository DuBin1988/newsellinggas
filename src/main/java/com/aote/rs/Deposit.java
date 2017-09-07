package com.aote.rs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
/**
 * 自动对账补账
 * @author Administrator
 *
 */
@Component
public class Deposit {
	static Logger log = Logger.getLogger(Deposit.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * payment为金额字符串， 如缴费100.00，传入100.00 返回最新结余
	 */
	public String deposit(String userId, String dt, String tm, String payment,
			String zhinajin, String sn, String bankCode, String jiGouNo,
			String guiYuanNo) {
		try {
			BigDecimal payMent = new BigDecimal(payment);
			log.debug(payMent);
			BigDecimal zhiNajin = new BigDecimal(zhinajin);
			BigDecimal zhiNaJin = zhiNajin
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			// 查找用户
			Map user = this.findUser(userId);
			List<Map<String, Object>> hands = this.findHands(userId);
			// 循环欠费记录，记录 欠费ids,最小指数，最大指数，欠费气量，金额合计
			String handIds = "";
			double lastinputgasnum = 0;
			double lastrecord = 0;
			Date lastinputdate = null;
			Date f_chaozqbegin = null;
			Date f_chaozqend = null;
			BigDecimal debts = new BigDecimal(0);
			BigDecimal debtGas = new BigDecimal(0);
			debts = debts.add(zhiNaJin);
			for (int i = 0; i < hands.size(); i++) {
				Map<String, Object> hand = (Map<String, Object>) hands.get(i);
				BigDecimal d = new BigDecimal(hand.get("oughtfee").toString());
				debts = debts.add(d);

				BigDecimal g = new BigDecimal(hand.get("oughtamount")
						.toString());
				debtGas = debtGas.add(g);
				handIds += hand.get("id") + ",";
				// 最大指数
				if (i == 0) {
					lastrecord = Double.parseDouble(hand.get("lastrecord")
							.toString());
					lastinputdate = (Date) hand.get("lastinputdate");
				}
				// 最小指数
				if (i == hands.size() - 1) {
					lastinputgasnum = Double.parseDouble(hand.get(
							"lastinputgasnum").toString());
				}
				if (i == 0) {
					f_chaozqbegin = (Date) hand.get("lastinputdate");
					f_chaozqend = (Date) hand.get("lastinputdate");

				} else {
					f_chaozqbegin = (Date) hand.get("lastinputdate");
				}
			}
			if (handIds.endsWith(",")) {
				handIds = handIds.substring(0, handIds.length() - 1);
			}
			// 先计算payment >=用户结余+用户欠费
			BigDecimal jieyu = new BigDecimal(user.get("f_zhye").toString());
			if (payMent.compareTo(debts.subtract(jieyu)) < 0) {
				return "";
			}
			// 计算结余,交费日期，表累计气量，总累计气量
			BigDecimal nowye = payMent.subtract(debts.subtract(jieyu));
			// 如果有抄表欠费，减去上期结余
			BigDecimal f_totalfee = new BigDecimal(0);
			if (hands.size() > 0) {
				f_totalfee = debts.subtract(jieyu);
			}
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
			Date deliverydate = (Date) formatDate.parse(dt);
			formatDate = new SimpleDateFormat("HHmmss");
			Date deliverytime = (Date) formatDate.parse(tm);
			// BigDecimal metergasnums = new
			// BigDecimal(user.get("f_metergasnums")
			// .toString());
			// BigDecimal newMeterGasNums = metergasnums.add(debtGas);
			// BigDecimal cumuGas = new BigDecimal(user
			// .get("f_cumulativepurchase").toString());
			// BigDecimal newCumuGas = cumuGas.add(debtGas);
			// 更新用户
			this.updateUser(user, nowye, deliverydate, deliverytime, debtGas);
			// 更新抄表欠费为已缴费
			if (handIds != null && !handIds.equals("")) {
				updateHands(handIds);
			}

			// 插入交费记录
			Map sale = insertSell(user, nowye, deliverydate, deliverytime,
					lastinputgasnum, lastrecord, debts, debtGas, handIds,
					lastinputdate, payMent, sn, bankCode, jiGouNo, guiYuanNo,
					zhiNaJin, hands, f_chaozqbegin, f_chaozqend, f_totalfee);

			log.debug("产生收费记录" + sale.get("id"));
			JSONObject json = new JSONObject();
			json.put("id", sale.get("id"));
			json.put("nowye", nowye.toString());

			return json.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public Map insertSell(Map<String, Object> userMap, BigDecimal nowye,
			Date deliverydate, Date deliverytime, double lastinputgasnum,
			double lastrecord, BigDecimal debts, BigDecimal debtGas,
			String handIds, Date lastinputdate, BigDecimal payMent, String sn,
			String bankCode, String jiGouNo, String guiYuanNo,
			BigDecimal zhinajin, List<Map<String, Object>> hands,
			Date f_chaozqbegin, Date f_chaozqend, BigDecimal f_totalfee) {

		Map sale = new HashMap<String, Object>();
		if (hands.size() != 0) {
			sale.put("lastinputgasnum", lastinputgasnum);
			sale.put("lastrecord", lastrecord);

		}
		sale.put("f_chaozqbegin", f_chaozqbegin);
		sale.put("f_chaozqend", f_chaozqend);

		sale.put("f_preamount", debts.subtract(zhinajin).doubleValue());
		sale.put("f_pregas", debtGas.doubleValue());
		sale.put("f_zhinajin", zhinajin.doubleValue());
		sale.put("f_useful", handIds);
		sale.put("lastinputdate", lastinputdate);
		sale.put("f_yhxz", userMap.get("f_yhxz"));
		sale.put("f_zhye", userMap.get("f_zhye"));
		sale.put("f_benqizhye", nowye.doubleValue());
		sale.put("f_gasmeterstyle", userMap.get("f_gasmeterstyle"));

		sale.put("f_apartment", userMap.get("f_apartment"));
		sale.put("f_userid", userMap.get("f_userid"));
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
		sale.put("f_finabuygasdate", deliverydate);
		sale.put("f_payment", "现金");
		sale.put("f_grossproceeds", payMent.doubleValue());
		sale.put("f_totalcost", f_totalfee.doubleValue());
		sale.put("f_givechange", 0.0);
		sale.put("f_meternumber", userMap.get("f_meternumber"));
		sale.put("f_deliverydate", deliverydate);
		sale.put("f_deliverytime", deliverytime);
		if (guiYuanNo.equals("weixin")) {
			sale.put("f_filiale", "微信");
			sale.put("f_sgnetwork", "微信");
			sale.put("f_sgoperator", "微信");
			sale.put("f_payfeetype", "微信支付");
			sale.put("f_comtype", "微信");

		} else {
			sale.put("f_filiale", "建设银行");
			sale.put("f_sgnetwork", "建设银行");
			sale.put("f_sgoperator", "建设银行");
			sale.put("f_payfeetype", "银行代收");
			sale.put("f_comtype", "银行");

		}
		sale.put("f_jiezhangstate", "未结账");
		sale.put("f_wheatherduizhang", "未对账");

		sale.put("f_payfeevalid", "有效");
		sale.put("f_amountmaintenance", 0.0);
		sale.put("f_banksn", sn);
		String OrgStr = userMap.get("f_OrgStr") + "";
		sale.put("f_OrgStr", OrgStr.substring(0, OrgStr.lastIndexOf(".") + 1)
				+ jiGouNo);
		sale.put("f_beizhu", "微信补账");
		this.hibernateTemplate.save("t_sellinggas", sale);
		return sale;
	}

	/**
	 * 更新用户信息
	 */
	private void updateUser(Map user, BigDecimal nowye, Date deliverydate,
			Date deliverytime, BigDecimal debtGas) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dt = format.format(deliverydate);
		String tm = format.format(deliverytime);
		// 更新用户
		String sql = "update t_userfiles  set f_zhye=" + nowye
				+ ", f_finabuygasdate='" + dt + "', f_finabuygastime='" + tm
				+ "' where f_userid='" + user.get("f_userid") + "'";
		this.hibernateTemplate.bulkUpdate(sql);
		log.debug(" 更新用户:" + sql);
	}

	/**
	 * 更新抄表记录为不欠费
	 */
	private void updateHands(String handIds) throws Exception {
		String sql = "update t_handplan set shifoujiaofei='是' where id in ("
				+ handIds + ")";
		this.hibernateTemplate.bulkUpdate(sql);
		log.debug(" 更新抄表记录为不欠费:" + sql);
	}

	/**
	 * 查找抄表欠费记录
	 */
	private List findHands(String userId) throws Exception {
		final String sql = "from t_handplan h left join fetch h.users u where u.f_userid='"
				+ userId
				+ "' and h.shifoujiaofei='否' and h.lastrecord is not null and h.f_state='已抄表' order by h.id desc";
		List list = this.hibernateTemplate.find(sql);
		log.debug(" 查找抄表欠费记录:" + sql);
		return list;

	}

	/**
	 * 查找用户信息
	 */
	private Map<String, Object> findUser(String userid) {
		final String userSql = "from t_userfiles  where f_userid='" + userid
				+ "'  ";
		List userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return null;
		}
		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
		log.debug(" 查找用户信息:" + userSql);
		return userMap;
	}

	private String findHand(String userId) throws Exception {
		final String sql = "from t_handplan where shifoujiaofei='否' and f_state='已抄表' and lastrecord is not null and f_userid='"
				+ userId + "' order by id desc";
		log.debug(sql);
		return sql;

	}
}
