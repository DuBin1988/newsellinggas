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
import com.aote.rs.util.UserTools;

@Path("sell")
@Scope("prototype")
@Component
public class SellSer {
	static Logger log = Logger.getLogger(SellSer.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;

	// 查询欠费信息
	// 获取某个编号基本信息及欠费数据
	@GET
	@Path("bill/{userid}/{fengongsi}")
	public String getUserBill(@PathParam("userid") String userid,
			@PathParam("fengongsi") String fengongsi) throws Exception {
		String result = "{";

		// 获取所有单值
		Map<String, String> singles = getSingles();

		// 获取用户档案及抄表记录情况
		String sql = "select "
				+ "h.id,isnull(ui.f_zhye,0) f_zhye,isnull(ui.f_username,'空名字') f_username,isnull(ui.f_zherownum,13) f_zherownum,isnull(u.f_usertype,'民用') f_usertype,"
				+ "isnull(u.f_districtname,'空小区') f_districtname,isnull(u.f_address,'空地址') f_address,"
				+ "isnull(u.f_gasproperties,'普通民用') f_gasproperties,isnull(u.f_gaspricetype,'民用气价') f_gaspricetype,"
				+ "ui.f_userid infoid,isnull(u.f_gasprice,0) f_gasprice,isnull(u.f_dibaohu,0) f_dibaohu,"
				+ "isnull(u.f_payment,'现金') f_payment,isnull(u.f_stairtype,'未设') f_stairtype,isnull(ui.f_userstate,'正常') f_userstate," // ui
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
				+ "' and f_filiale='"
				+ fengongsi
				+ "') ui join t_userfiles u on ui.f_filiale=u.f_filiale and ui.f_userid=u.f_userinfoid and u.f_gasmeterstyle='机表'"
				+ "left join (select datediff(day,isnull(f_endjfdate,GETDATE()),GETDATE()) days,* from t_handplan where f_state='已抄表' and shifoujiaofei='否' and f_userinfoid='"
				+ userid + "' and f_filiale='" + fengongsi + "') h "
				+ "on u.f_userid=h.f_userid and h.f_filiale=u.f_filiale "
				+ "order by u.f_userid, h.lastinputdate, h.lastinputgasnum";
		log.debug("查询欠费sql:" + sql);
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(new HibernateSQLCall(sql));

		// 从第一条获取户数据
		Map<String, Object> userinfo = (Map<String, Object>) list.get(0);
		result += "infoid:'" +  (String) userinfo.get("infoid") + "'";
		result += ",f_username:'" + (String) userinfo.get("f_username") + "'";
		BigDecimal zz = new BigDecimal(userinfo.get("f_zherownum").toString());
		result += ",f_zherownum:" + zz;
		result += ",f_address:'" + (String) userinfo.get("f_address") + "'";
		// 用户结余
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
		// 欠费表的数据
		String hands = "";

		// 取滞纳金比率
		BigDecimal scale = null;

		if (f_usertype.equals("民用")) {
			scale = new BigDecimal(singles.get("民用滞纳金比率"));
		} else {
			scale = new BigDecimal(singles.get("非民用滞纳金比率"));
		}
		int i = 0;
		// 循环获取欠费数据
		for (Map<String, Object> hand : list) {
			if (!hands.equals("")) {
				hands += ",";
			}
			// 如果没有欠费数据，继续
			Object handId = hand.get("handId");
			if (handId == null) {
				continue;
			}
			hands += "{";
			hands += "id:'" + hand.get("id") + "'";
			//
			hands += ",f_userid:'" + hand.get("f_userid") + "'";
			// 用气量
			hands += ",oughtamount:" + hand.get("oughtamount");
			// 气费
			hands += ",oughtfee:" + hand.get("oughtfee");
			// 滞纳金金额=气费*比例*天数
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
			// 如果有滞纳金，计算基数去掉结余
			int equals = f_zhye.compareTo(new BigDecimal("0"));// 比较余额是否大于0
			if (equals > 0) {
				int bigDec = f_zhye.compareTo(oughtfee);// 判断余额是否大余气费
				oughtfee = bigDec > 0 ? new BigDecimal("0") : oughtfee
						.subtract(f_zhye);
				f_zhye = bigDec > 0 ? f_zhye.subtract(oughtfee)
						: new BigDecimal("0");
			}
			f_zhinajin = oughtfee.multiply(new BigDecimal(days + "")).multiply(
					scale);
			f_zhinajin = f_zhinajin.setScale(2, BigDecimal.ROUND_HALF_UP);
			hands += ",f_zhinajin:" + f_zhinajin;

			// 抄表日期
			hands += ",lastinputdate:'" + hand.get("lastinputdate") + "'";
			// 上期抄表底数
			hands += ",lastinputgasnum:" + hand.get("lastinputgasnum");
			// 本期抄表底数
			hands += ",lastrecord:" + hand.get("lastrecord");
			// 交费截止日期
			hands += ",f_endjfdate:'" + hand.get("f_endjfdate") + "'";
			// 滞纳金天数
			hands += ",days:" + days;
			// 网点
			hands += ",f_network:'" + hand.get("f_network") + "'";
			// 操作员
			hands += ",f_operator:'" + hand.get("f_operator") + "'";
			// 录入日期
			hands += ",f_inputdate:'" + hand.get("f_inputdate") + "'";
			hands += ",number:" + i++ + "";
			hands += "}";
		}

		result = result + hands + "]}";
		return result;

	}

	/**
	 * 定义sell方法，处理交费
	 * 
	 * @param userid
	 *            用户编号
	 * @param dMoney
	 *            收款
	 * @param dZhinajin
	 *            滞纳金
	 * @param payments
	 *            付款方式
	 * @param opid
	 *            操作员id
	 * @param orgstr
	 *            组织信息，前台获取后传入到后台处理
	 * @return
	 */

	@GET
	@Path("{userid}/{money}/{zhinajin}/{payment}/{opid}/{orgstr}/{handids}/{invoicenum}")
	public JSONObject txSell(@PathParam("userid") String userid,
			@PathParam("money") BigDecimal dMoney,
			@PathParam("zhinajin") double zhinajin,
			@PathParam("payment") String payments,
			@PathParam("opid") String opid, @PathParam("orgstr") String orgstr,
			@PathParam("handids") String handIds,
			@PathParam("invoicenum") String invoicenum) {
		JSONObject ret = new JSONObject();
		try {
			log.debug("售气交费 开始");
			// 获得操作员，网点，分公司，组织信息
			Map loginuser = UserTools.getUser(opid, this.hibernateTemplate);
			// 分公司
			String f_filiale = loginuser.get("f_fengongsi").toString();
			BigDecimal payMent = dMoney;
			// 查询用户
			Map user = this.findUserinfo(userid, f_filiale);
			// 查询户所有欠费信息
			// Map<String, Object> nopayMap = getnopayinfor(userid, f_filiale);
			// 获取每个表的阶梯信息
			// JSONObject files_stair = this.getfilesInfor(userid);
			List<Map<String, Object>> hands = this.findHands(userid, handIds);

			// 循环欠费记录，记录 欠费ids,最小指数，最大指数，欠费气量，金额合计userid
			BigDecimal lastinputgasnum = new BigDecimal(0);
			BigDecimal lastrecord = new BigDecimal(0);
			Date lastinputdate = null;
			BigDecimal debts = new BigDecimal(0);
			BigDecimal debtGas = new BigDecimal(0);
			String userids = null;
			String handdate = null;
			JSONArray array = new JSONArray();
			JsonTransfer transfer = new JsonTransfer();
			for (int i = 0; i < hands.size(); i++) {
				Map<String, Object> hand = (Map<String, Object>) hands.get(i);
				BigDecimal d = new BigDecimal(hand.get("oughtfee").toString());
				debts = debts.add(d);
				BigDecimal g = new BigDecimal(hand.get("oughtamount")
						.toString());
				debtGas = debtGas.add(g);
				// 一户多表，合计本次底数
				BigDecimal lastr = new BigDecimal(hand.get("lastrecord")
						.toString());
				lastrecord = lastrecord.add(lastr);
				// 一户多表，合计上次底数
				BigDecimal lastin = new BigDecimal(hand.get("lastinputgasnum")
						.toString());
				lastinputgasnum = lastinputgasnum.add(lastin);
				if (i == 0) {
					lastinputdate = (Date) hand.get("lastinputdate");
					handdate = formatmonth.format(lastinputdate);
				} else if (i == hands.size() - 1) {
					handdate = handdate
							+ "-"
							+ formatmonth.format((Date) hand
									.get("lastinputdate"));
				}
				// 拼接表编号
				String f_userid = hand.get("f_userid").toString();
				if (userids == null) {
					userids = f_userid;
				} else {
					userids = userids + "," + f_userid;
				}
				JSONObject json = (JSONObject) transfer.MapToJson(hand);
				array.put(json);
			}
			// 放入交费的表编号
			user.put("userids", userids);
			// 放入发票号
			user.put("invoicenum", invoicenum);
			// 先计算payment >=用户结余+用户欠费
			BigDecimal jieyu = new BigDecimal(user.get("f_zhye").toString());
			if (payMent.compareTo(debts.subtract(jieyu)) < 0) {
				throw new ResultException("交费金额:" + payMent + "不够缴纳本次欠费:"
						+ debts.subtract(jieyu));
			}
			// 计算结余,交费日期，表累计气量，总累计气量
			BigDecimal nowye = payMent.subtract(debts.subtract(jieyu))
					.subtract(new BigDecimal(zhinajin));

			BigDecimal metergasnums = new BigDecimal(user.get("f_metergasnums")
					.toString());
			BigDecimal newMeterGasNums = metergasnums.add(debtGas);
			BigDecimal cumuGas = new BigDecimal(user
					.get("f_cumulativepurchase").toString());
			BigDecimal newCumuGas = cumuGas.add(debtGas);
			// 更新用户
			this.updateUser(user, nowye, debtGas, newMeterGasNums, newCumuGas);
			// 更新抄表欠费为已缴费
			if (handIds != null && !handIds.equals("")) {
				updateHands(handIds);
			}
			// 插入交费记录
			int sellid = insertSell(user, nowye, lastinputgasnum.doubleValue(),
					lastrecord.doubleValue(), debts, debtGas, handIds,
					lastinputdate, payMent, metergasnums, cumuGas,
					newMeterGasNums, newCumuGas, opid, payments, orgstr,
					zhinajin);
			log.debug("售气交费成功!" + sellid);
			ret.put("success", "机表交费成功");
			ret.put("f_deliverydate", format.format(new Date()));
			ret.put("f_invoicenum", invoicenum);
			ret.put("f_username", user.get("f_username"));
			ret.put("f_address", user.get("f_address"));
			ret.put("f_userinfoid", user.get("f_userid"));
			ret.put("f_handdate", handdate);
			// 滞纳金
			ret.put("f_zhinajin", zhinajin);
			// 上次结余
			ret.put("f_zhye", user.get("f_zhye"));
			// 应收金额
			ret.put("f_totalcost", debts.doubleValue());
			// 实收金额
			ret.put("f_grossproceeds", payMent.doubleValue());
			// 本次结余
			ret.put("f_benqizhye", nowye);
			ret.put("id", sellid);
			ret.put("hands", array);
			// 抓取自定义异常
		} catch (Exception ex) {
			ex.printStackTrace();
			hibernateTemplate.getSessionFactory().getCurrentSession()
					.getTransaction().rollback();
			log.error("售气交费 失败!" + ex.getMessage());
			ret.put("error", ex.getMessage());
		} finally {
			return ret;
		}
	}

	// 查找登陆用户
	private Map<String, Object> findloginUser(String loginId) {
		String findUser = "from t_user where id='" + loginId + "'";
		List<Object> userList = this.hibernateTemplate.find(findUser);
		if (userList.size() != 1) {
			return null;
		}
		return (Map<String, Object>) userList.get(0);
	}

	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat formatmonth = new SimpleDateFormat("yyyyMM");

	/**
	 * 保存收费记录，返回保存后的id
	 * 
	 * @return
	 * @throws Exception
	 */
	public int insertSell(Map<String, Object> userMap, BigDecimal nowye,
			double lastinputgasnum, double lastrecord, BigDecimal debts,
			BigDecimal debtGas, String handIds, Date lastinputdate,
			BigDecimal payMent, BigDecimal metergasnums, BigDecimal cumuGas,
			BigDecimal newMeterGasNums, BigDecimal newCumuGas, String opid,
			String payments, String orgstr, double zhinajin) throws Exception {
		// 查找登陆用户,获取登陆网点,操作员
		Map<String, Object> loginUser = this.findloginUser(opid);
		loginUser.put("orgstr", orgstr);
		if (loginUser == null) {
			log.debug("机表缴费处理时未找到登陆用户,登陆id" + opid);
			throw new ResultException("机表缴费处理时未找到登陆用户,登陆id" + opid);
		}
		Map sale = new HashMap<String, Object>();
		Date now = new Date();
		sale.put("lastinputgasnum", lastinputgasnum);
		sale.put("lastrecord", lastrecord);
		sale.put("f_preamount", debts.doubleValue());
		sale.put("f_pregas", debtGas.doubleValue());
		sale.put("f_zhinajin", zhinajin);
		sale.put("lastinputdate", lastinputdate);
		sale.put("f_yhxz", userMap.get("f_yhxz"));
		sale.put("f_zhye", userMap.get("f_zhye"));
		double f_benqizhye = nowye.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		sale.put("f_benqizhye", f_benqizhye);
		sale.put("f_gasmeterstyle", "机表");
		sale.put("f_comtype", "天然气公司");
		sale.put("f_apartment", userMap.get("f_apartment"));
		sale.put("f_userid", userMap.get("userids"));
		sale.put("f_userinfoid", userMap.get("f_userid"));// 用户id
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
		sale.put("f_payment", "现金");
		sale.put("f_upbuynum", cumuGas.doubleValue());
		sale.put("f_premetergasnums", metergasnums.doubleValue());
		sale.put("f_grossproceeds", payMent.doubleValue());
		sale.put("f_totalcost", debts.doubleValue());
		sale.put("f_givechange", 0.0);
		sale.put("f_meternumber", userMap.get("f_meternumber"));
		sale.put("f_payment", payments); // 付款方式
		sale.put("f_paytype", "现金"); // 交费类型，银行代扣/现金
		sale.put("f_sgnetwork", loginUser.get("f_parentname").toString()); // 网点
		sale.put("f_sgoperator", loginUser.get("name").toString()); // 操 作 员
		sale.put("f_filiale", loginUser.get("f_fengongsi").toString()); // 分公司
		sale.put("f_fengongsinum", loginUser.get("f_fengongsinum").toString()); // 分公司编号
		sale.put("f_orgstr", loginUser.get("orgstr").toString()); // 组织信息
		sale.put("f_payfeetype", "机表收费"); // 交易类型
		sale.put("f_payfeevalid", "有效"); // 购气有效类型
		sale.put("f_useful", handIds); // 抄表记录id
		sale.put("f_deliverydate", now);
		sale.put("f_deliverytime", now);
		sale.put("f_jiezhangstate", "未结账");
		sale.put("f_wheatherduizhang", "未对账");
		sale.put("f_amountmaintenance", 0.0);
		sale.put("f_metergasnums", newMeterGasNums.doubleValue());
		sale.put("f_cumulativepurchase", newCumuGas.doubleValue());
		sale.put("f_stairtype", userMap.get("f_stairtype"));
		sale.put("f_invoicenum", userMap.get("invoicenum").toString());
		// sale.put("f_stair1price", nopayMap.get("f_stair1price"));
		// sale.put("f_stair1amount", nopayMap.get("f_stair1amount"));
		// sale.put("f_stair1fee", nopayMap.get("f_stair1fee"));
		// sale.put("f_stair2price", nopayMap.get("f_stair2price"));
		// sale.put("f_stair2amount", nopayMap.get("f_stair2amount"));
		// sale.put("f_stair2fee", nopayMap.get("f_stair2fee"));
		// sale.put("f_stair3price", nopayMap.get("f_stair3price"));
		// sale.put("f_stair3amount", nopayMap.get("f_stair3amount"));
		// sale.put("f_stair3fee", nopayMap.get("f_stair3fee"));
		sale.put("f_OrgStr", userMap.get("f_OrgStr") + "");
		sale.put("f_zherownum", userMap.get("f_zherownum")); // 折子号
		log.debug("交费记录保存信息：" + sale.toString());
		// session.save("t_sellinggas", sale);
		int sellId = (Integer) hibernateTemplate.save("t_sellinggas", sale);
		// 格式化交费日期
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// result.put("f_stair1price", nopayMap.get("f_stair1price"));
		// result.put("f_stair1amount", nopayMap.get("f_stair1amount"));
		// result.put("f_stair1fee", nopayMap.get("f_stair1fee"));
		// result.put("f_stair2price", nopayMap.get("f_stair2price"));
		// result.put("f_stair2amount", nopayMap.get("f_stair2amount"));
		// result.put("f_stair2fee", nopayMap.get("f_stair2fee"));
		// result.put("f_stair3price", nopayMap.get("f_stair3price"));
		// result.put("f_stair3amount", nopayMap.get("f_stair3amount"));
		// result.put("f_stair3fee", nopayMap.get("f_stair3fee"));
		// 更新抄表记录sellid
		if (handIds != null && !handIds.equals("") && !handIds.equals("0")) {
			String updateHandplan = "update t_handplan set f_sellid =" + sellId
					+ " where id in (" + handIds + ")";
			log.debug("更新抄表记录sql：" + updateHandplan);
			hibernateTemplate.bulkUpdate(updateHandplan);
		}
		return sellId;
	}

	/**
	 * 更新抄表记录为不欠费
	 */
	private void updateHands(String handIds) throws Exception {
		String sql = "update t_handplan set shifoujiaofei='是' where id in ("
				+ handIds + ")";
		// session.createQuery(sql).executeUpdate();
		log.debug("更新抄表户信息开始:" + sql);
		this.hibernateTemplate.bulkUpdate(sql);
	}

	/**
	 * 更新用户信息
	 */
	private void updateUser(Map user, BigDecimal nowye, BigDecimal debtGas,
			BigDecimal newMeterGasNums, BigDecimal newCumuGas) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String dt = format.format(now);
		String tm = format.format(now);
		// 折子行号
		String f_zherownum = user.get("f_zherownum") + "";
		if (f_zherownum == "" || user.get("f_zherownum") == null) {
			f_zherownum = "13";
		}
		int zherownum = Integer.parseInt(f_zherownum);
		// 折子行号为24，换行
		if (zherownum >= 24) {
			zherownum = 0;
		}
		user.put("f_zherownum", zherownum);
		double f_zhye = nowye.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		// 更新用户
		String sql = "update t_userinfo  set f_zhye=" + f_zhye
				+ ", f_finabuygasdate='" + dt + "', f_finabuygastime='" + tm
				+ "'," + " f_metergasnums=" + newMeterGasNums
				+ ", f_cumulativepurchase=" + newCumuGas + ",f_zherownum="
				+ (zherownum + 1) + " where f_userid='" + user.get("f_userid")
				+ "'";
		// this.session.createQuery(sql).executeUpdate();
		log.debug("更新户信息开始:" + sql);
		this.hibernateTemplate.bulkUpdate(sql);
	}

	/**
	 * 查找抄表欠费记录
	 * 
	 * @param userinfoId
	 *            用户编号
	 * @param handids
	 *            选择的欠费的抄表id
	 * @return 抄表记录
	 * @throws Exception
	 */
	private List findHands(String userinfoId, String handids) throws Exception {
		final String sql = "from t_handplan h" + " where h.f_userinfoid='"
				+ userinfoId + "' and h.id in (" + handids
				+ ") and h.shifoujiaofei='否' and h.lastrecord is not null "
				+ "and h.f_state='已抄表' order by h.id";
		// List list = session.createQuery(sql).list();
		log.debug("查询欠费信息开始:" + sql);
		List<Map<String, Object>> list = this.hibernateTemplate.find(sql);
		return list;
	}

	/**
	 * 获取每个表的阶梯信息
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
				+ "h.shifoujiaofei='否' and h.f_state='已抄表' and h.lastrecord is not null "
				+ "group by u.f_userid ,u.f_meternumber";
		log.debug("查询每块表具体阶梯信息开始:" + sql);
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
	 * 查询该户的欠费信息
	 * 
	 * @param userinfoId
	 * @return 针对一户多表，上期底数为每条欠费上期底数合计，本期同上
	 */
	private Map<String, Object> getnopayinfor(String userinfoId,
			String f_filiale) {
		final String sql = "select f_stair1price f_stair1price, f_stair1amount f_stair1amount, f_stair1fee f_stair1fee, f_stair2price f_stair2price, f_stair2amount f_stair2amount,"
				+ " f_stair2fee f_stair2fee, f_stair3price f_stair3price, f_stair3amount f_stair3amount, f_stair3fee f_stair3fee,isnull(lastinputgasnum,0) lastinputgasnum,isnull(lastrecord,0) lastrecord from "
				+ "(select sum(h.lastinputgasnum) lastinputgasnum,sum(h.lastrecord) lastrecord, min(u.f_stair1price) f_stair1price, Round(SUM(isnull(h.f_stair1amount,0)),2) f_stair1amount,"
				+ " Round(SUM(isnull(h.f_stair1fee,0)),2) f_stair1fee,min(u.f_stair2price) f_stair2price,"
				+ " Round(SUM(isnull(h.f_stair2amount,0)),2) f_stair2amount, Round(SUM(isnull(h.f_stair2fee,0)),2) f_stair2fee,"
				+ " min(u.f_stair3price) f_stair3price, Round(SUM(isnull(h.f_stair3amount,0)),2) f_stair3amount,"
				+ " Round(SUM(isnull(h.f_stair3fee,0)),2) f_stair3fee from(select * from t_userfiles"
				+ " where f_userinfoid='"
				+ userinfoId
				+ "' and f_filiale='"
				+ f_filiale
				+ "') u left join (select * from t_handplan where f_userinfoid='"
				+ userinfoId
				+ "' and f_filiale='"
				+ f_filiale
				+ "' and  shifoujiaofei='否'"
				+ " and f_state='已抄表' and lastrecord is not null) h on u.f_userid=h.f_userid) t";
		log.debug("查询具体阶梯信息开始:" + sql);
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(sqlCall);
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		return map;
	}

	// 定义sell方法，处理交费,冲正插入一条负的交费
	@GET
	@Path("{userid}/{id}/{opid}")
	public JSONObject txSell_cz(@PathParam("userid") String userid,
			@PathParam("id") String id, @PathParam("opid") String opid) {
		// 返回信息，为空则操作成功，不为空则操作失败，内容为错误信息
		JSONObject ret = new JSONObject();
		try {
			String sql = "from t_sellinggas where id=" + id;
			List list = this.hibernateTemplate.find(sql);
			// 找到安检记录，判断日期和基表读数
			if (list.size() == 1) {
				// 查找登陆用户,获取登陆网点,操作员
				Map<String, Object> loginUser = this.findloginUser(opid);
				String f_filiale = loginUser.get("f_fengongsi").toString();
				Map<String, Object> userinfo = this.findUserinfo(userid,
						f_filiale);
				if (loginUser == null) {
					log.debug("机表缴费处理时未找到登陆用户,登陆id" + opid);
					throw new RSException("机表缴费处理时未找到登陆用户,登陆id" + opid);
				}
				Map<String, Object> b_sell = (Map<String, Object>) list.get(0);
				// 产生交费记录
				Map<String, Object> sell = new HashMap<String, Object>();
				sell.putAll(b_sell);
				sell.remove("id");
				Date now = new Date();
				sell.put("f_deliverydate", now); // 交费日期
				sell.put("f_deliverytime", now); // 交费时间
				// 正常返回购气日期
				SimpleDateFormat fmt = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				// ret=fmt.format(now);
				sell.put("f_deliverydate_tb", fmt.format(now)); // 同步 交费时间
				sell.put("f_status_tb", "1"); // 同步 状态
				sell.put("f_grossproceeds",
						-Double.parseDouble(sell.get("f_grossproceeds") + ""));
				sell.put("f_pregas",
						-Double.parseDouble(sell.get("f_pregas") + "")); // 气量
				sell.put("f_preamount",
						-Double.parseDouble(sell.get("f_preamount") + "")); // 气费
				sell.put("f_payment", "冲正"); // 付款方式
				sell.put("f_paytype", "现金"); // 交费类型，银行代扣/现金
				sell.put("f_sgnetwork", loginUser.get("f_parentname")
						.toString()); // 网点
				sell.put("f_sgoperator", loginUser.get("name").toString()); // 操作员
				sell.put("f_filiale", loginUser.get("f_fengongsi").toString()); // 分公司
				sell.put("f_fengongsinum", loginUser.get("f_fengongsinum")
						.toString()); // 分公司编号
				sell.put("f_payfeevalid", "有效"); // 购气有效类型
				log.debug("交费记录保存信息：" + sell.toString());
				int sellId = (Integer) hibernateTemplate.save("t_sellinggas",
						sell);
				// ID 返回去
				if (sellId > 0) {
					execSQL("update t_sellinggas set f_payfeevalid='有效',f_payment='冲正'  where f_userinfoid='"
							+ userid + "' and id='" + id + "'" + "");
				} else
					ret.put("error", "noid");
			} else {
				ret.put("error", "noid");
			}
			ret.put("success", "机表冲正成功");
		} catch (RSException e) {
			log.debug("售气交费 失败!");
			ret.put("error", e.getMessage());
		} catch (Exception ex) {
			log.debug("售气交费 失败!" + ex.getMessage());
			ret.put("error", ex.getMessage());
		} finally {
			return ret;
		}
	}

	/**
	 * 添加账户结余
	 */
	private void addUserAccountzhye(String userid, BigDecimal money) {
		try {
			// 更新档案账户结余f_accountzhye
			String updateUserFile = "update t_userfiles set f_accountzhye=f_accountzhye+"
					+ money + " where f_userid='" + userid + "'";
			log.debug("实际不欠款更新档案账户结余" + updateUserFile);
			this.hibernateTemplate.bulkUpdate(updateUserFile);
			log.debug("实际不欠款更新档案账户成功");
		} catch (RuntimeException e) {
			throw new RSException("添加账户结余失败,用户" + userid + ",金额" + money);
		}
	}

	/**
	 * 单条欠费处理，计算欠费，产生清欠记录，计算结余，收款
	 * 
	 * @param hand
	 *            抄表记录
	 * @param accountZhye
	 *            档案结余
	 * @param shoukuan
	 *            收款
	 */
	private void financedetailDisp(Map<String, Object> loginUser,
			List<Map<String, Object>> hands, BigDecimal shoukuan, int sellid)
			throws Exception {
		// 否则，根据收款逐条处理抄表记录欠款，产生清欠费记录，并计算最新余额最后写入档案
		for (Map<String, Object> hand : hands) {
			log.debug("清欠一条抄表记录" + hand.toString());
			int handId = Integer.parseInt(hand.get("handid").toString());
			String userId = hand.get("f_userid").toString();
			BigDecimal unitPrice = new BigDecimal(hand.get("f_gasprice")
					.toString());
			String sgnetwork = loginUser.get("f_parentname").toString();
			String sgoperator = loginUser.get("name").toString();
			BigDecimal debtM = new BigDecimal(hand.get("f_debtmoney")
					.toString());
			// 原来结余
			BigDecimal oldAccountzhye = shoukuan;
			// 实收
			BigDecimal realMoney = new BigDecimal(0);
			// 新欠款
			BigDecimal newdebtmoney = new BigDecimal(0);
			BigDecimal newaccountzhye = new BigDecimal(0);
			// 不欠费，返回
			if (debtM.doubleValue() <= 0) {
				return;
			}
			// 收款 > 欠费 ，实收=欠费，抄表新欠费 =0 ,新结余=收款-欠费，设置总结余
			if (shoukuan.compareTo(debtM) >= 0) {
				realMoney = debtM;
				newdebtmoney = new BigDecimal(0);
				newaccountzhye = shoukuan.subtract(debtM);
				shoukuan = shoukuan.subtract(debtM);
			}
			// 收款小于欠费, 实收=收款，抄表新欠费 = 欠费-收款 ,新结余=0，设置总结余
			else {
				realMoney = shoukuan;
				newdebtmoney = debtM.subtract(shoukuan);
				newaccountzhye = new BigDecimal(0);
				shoukuan = new BigDecimal(0);
			}
			// 存清欠记录
			this.financedetailSave(handId, userId, debtM, oldAccountzhye,
					realMoney, unitPrice, newdebtmoney, newaccountzhye,
					sgnetwork, sgoperator, sellid, hand.get("lastinputdate"));
			// 更新档案账户结余f_accountzhye
			String updateUserFile = "update t_userfiles set f_accountzhye="
					+ newaccountzhye.doubleValue() + " where f_userid='"
					+ userId + "'";
			log.debug("更新档案账户结余" + updateUserFile);
			this.hibernateTemplate.bulkUpdate(updateUserFile);
			// 更新抄表记录实际欠费
			String updateHandplan = "update t_handplan set f_debtmoney="
					+ newdebtmoney.doubleValue() + " where id='" + handId + "'";
			log.debug("更新抄表欠费" + updateHandplan);
			this.hibernateTemplate.bulkUpdate(updateHandplan);
		}
	}

	/**
	 * 保存用户财务明细
	 */
	private void financedetailSave(int handId, String f_userid,
			BigDecimal ysMoney, BigDecimal oldAccountZhye, BigDecimal realmony,
			BigDecimal unitprice, BigDecimal debtmoney,
			BigDecimal newaccountzhye, String sgnetwork, String sgoperator,
			int sellid, Object chaobiaoDate) throws Exception {
		// 放入清欠数据
		Date now = new Date();
		Map<String, Object> finance = new HashMap<String, Object>();
		// <!--已收金额-->
		finance.put("f_realmoney", realmony.doubleValue());
		// <!--欠费金额-->
		finance.put("f_debtmoney", debtmoney.doubleValue());
		// <!--账户结余-->
		finance.put("f_accountzhye", newaccountzhye.doubleValue());
		// <!--用户编号-->
		finance.put("f_userid", f_userid);
		// 原账户余额
		finance.put("f_prevaccountzhye", oldAccountZhye.doubleValue());
		// <!--应收金额-->
		finance.put("f_oughtfee", ysMoney.doubleValue());
		// 单价
		finance.put("f_gasprice", unitprice.doubleValue());
		// 抄表记录的日期
		finance.put("f_debtdate", chaobiaoDate);
		// <!--是否有效(有效/无效)-->
		finance.put("f_payfeevalid", "有效");
		finance.put("f_payfeetype", "交费");
		// <!--网点-->
		finance.put("f_sgnetwork", sgnetwork);
		// <!--操作员-->
		finance.put("f_opertor", sgoperator);
		// 操作日期，时间
		finance.put("f_deliverydate", now);
		finance.put("f_deliverytime", now);
		// 抄表id
		finance.put("f_handid", handId);
		// 交费id
		finance.put("f_sellid", sellid);
		// 保存
		JSONObject financeJson = (JSONObject) new JsonTransfer()
				.MapToJson(finance);
		log.debug("保存清欠明细数据" + financeJson);
		Object idObj = hibernateTemplate.save("t_financedetail", finance);
		int saveId = Integer.parseInt(idObj.toString());
		log.debug("保存成功,数据id" + saveId);
	}

	// 查找抄表记录
	private List<Map<String, Object>> findHanplans(String userid) {
		String sql = " select u.f_zhye f_zhye, u.f_accountzhye f_accountzhye, u.f_username f_username,u.f_books f_books,u.f_cardid f_cardid, u.f_address f_address,u.f_districtname f_districtname,u.f_cusDom f_cusDom,u.f_cusDy f_cusDy,u.f_beginfee f_beginfee, u.f_metergasnums f_metergasnums, u.f_cumulativepurchase f_cumulativepurchase,"
				+ "u.f_idnumber f_idnumber, u.f_gaspricetype f_gaspricetype, u.f_gasprice f_gasprice, u.f_usertype f_usertype,"
				+ "u.f_gasproperties f_gasproperties, u.f_userid f_userid,u.f_zherownum f_zherownum, h.id handid, h.oughtamount oughtamount, h.lastinputgasnum lastinputgasnum,"
				+ "h.lastrecord lastrecord, h.shifoujiaofei shifoujiaofei, h.oughtfee oughtfee,h.f_debtmoney  f_debtmoney ,h.lastinputdate from t_userfiles u "
				+ "left join (select * from t_handplan where f_state = '已抄表' and shifoujiaofei = '否' and f_userid='"
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
	 * 查找户信息
	 * 
	 * @param userid
	 * @return
	 */
	private Map<String, Object> findUserinfo(String userid, String f_filiale) {
		final String userSql = "from t_userinfo  where f_filiale='" + f_filiale
				+ "' and f_userid='" + userid + "'  ";
		// List userlist = session.createQuery(userSql).list();
		log.debug("查询户信息开始:" + userSql);
		List<Object> userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return null;
		}
		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
		return userMap;
	}

	// 执行sql查询
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

	// 给字符串添加逗号分隔的内容
	private String add(String source, String str) {
		if (source.equals("")) {
			return source + str;
		} else {
			return source + "," + str;
		}
	}

	// 获取所有单值，转换成Map
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

	// 转换器，在转换期间会检查对象是否已经转换过，避免重新转换，产生死循环
	class JsonTransfer {
		// 保存已经转换过的对象
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		// 把单个map转换成JSON对象
		public Object MapToJson(Map<String, Object> map) {
			// 转换过，返回空对象
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					String key = entry.getKey();
					Object value = entry.getValue();
					// 空值转换成JSON的空对象
					if (value == null) {
						value = JSONObject.NULL;
					} else if (value instanceof HashMap) {
						value = MapToJson((Map<String, Object>) value);
					}
					// 如果是$type$，表示实体类型，转换成EntityType
					if (key.equals("$type$")) {
						json.put("EntityType", value);
					} else if (value instanceof Date) {
						Date d1 = (Date) value;
						Calendar c = Calendar.getInstance();
						long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
						json.put(key, time);
					} else if (value instanceof MapProxy) {
						// MapProxy没有加载，不管
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						// 没加载的集合不管
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

		// 把集合转换成Json数组
		public Object ToJson(PersistentSet set) {
			JSONArray array = new JSONArray();
			for (Object obj : set) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
				array.put(json);
			}
			return array;
		}

		// 判断已经转换过的内容里是否包含给定对象
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
