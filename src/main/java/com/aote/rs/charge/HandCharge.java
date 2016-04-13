package com.aote.rs.charge;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor.TURQUOISE;
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

import com.aote.listener.ContextListener;
import com.aote.rs.charge.countdate.ICountDate;
import com.aote.rs.charge.enddate.IEndDate;
import com.aote.rs.exception.RSException;
import com.aote.rs.sms.ISms;
import com.aote.rs.sms.MianZhuSms;
import com.aote.rs.sms.SmsService;
import com.aote.rs.util.BeanUtil;
import com.aote.rs.util.JSONHelper;

@Path("handcharge")
@Scope("prototype")
@Component
public class HandCharge {

	static Logger log = Logger.getLogger(HandCharge.class);

	// 是否发送短信。 不需要的话 给 false
	private boolean sendMsg = true;

	@Autowired
	private HibernateTemplate hibernateTemplate;
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	private String stardate;
	private String enddate;

	// 总累计购气量
	BigDecimal sumamont = new BigDecimal(0);

	// 计算阶梯气价的中间结果
	BigDecimal stair1num = new BigDecimal(0);
	BigDecimal stair2num = new BigDecimal(0);
	BigDecimal stair3num = new BigDecimal(0);
	BigDecimal stair4num = new BigDecimal(0);
	BigDecimal stair1fee = new BigDecimal(0);
	BigDecimal stair2fee = new BigDecimal(0);
	BigDecimal stair3fee = new BigDecimal(0);
	BigDecimal stair4fee = new BigDecimal(0);
	// 一阶梯剩余可购
	BigDecimal stair1surplus = new BigDecimal(0);
	// 二阶梯剩余可购
	BigDecimal stair2surplus = new BigDecimal(0);
	// 三阶梯剩余可购
	BigDecimal stair3surplus = new BigDecimal(0);
	private int stairmonths;

	// 抄表单下载，返回JSON串
	// operator 抄表员中文名
	@GET
	@Path("{operator}")
	@Produces("application/json")
	public JSONArray ReadRecordInput(@PathParam("operator") String operator) {
		JSONArray array = new JSONArray();
		List<Object> list = this.hibernateTemplate.find(
				"from t_handplan where f_userid is not null and f_inputtor=? and f_state='未抄表'",
				operator);
		for (Object obj : list) {
			// 把单个map转换成JSON对象
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		return array;
	}

	// 查询批量抄表单
	@POST
	@Path("download")
	public String downLoadRecord(String condition) {

		String sql = "select top 1000 h.id,u.f_userinfoid,u.f_userid,u.f_username,u.f_address,u.lastinputgasnum,info.f_zhye "
				+ "from t_handplan h left join t_userfiles u on h.f_userid = u.f_userid "
				+ "left join t_userinfo info on u.f_userinfoid=info.f_userid where h.shifoujiaofei='否' "
				+ "and u.f_userstate!='注销' and h.f_state='未抄表' and "
				+ condition + "	order by u.f_address,u.f_apartment";
		List<Object> list = this.hibernateTemplate
				.executeFind(new HibernateSQLCall(sql));
		// String result="[";
		boolean check = false;
		JSONArray array = new JSONArray();
		for (Object obj : list) {
			JSONObject json = (JSONObject) new JsonTransfer()
					.MapToJson((Map<String, Object>) obj);
			// Map<String, Object> map = (Map<String, Object>) json;
			// if(!result.equals("[")){
			// result+=",";
			// }
			// String item="";
			// //计划月份用户编号用户姓名地址上次底数本次底数用气量
			// item+="{";
			// item+="f_userid:'"+map.get("f_userid")+"',";
			// item+="f_username:'"+map.get("f_username")+"',";
			// item+="f_address:'"+map.get("f_address")+"',";
			// item+="lastinputgasnum:"+map.get("lastinputgasnum");
			// item+="}";
			//
			// result += item;
			array.put(json);
		}
		// result+="]";
		// System.out.println(result);

		return array.toString();
	}

	// 单块表抄表录入
	// 本方法不可重入
	@SuppressWarnings("unchecked")
	@GET
	@Path("record/one/{userid}/{reading}/{sgnetwork}/{sgoperator}/{lastinputdate}/{handdate}/{meterstate}")
	@Produces("application/json")
	public String RecordInputForOne(@PathParam("userid") String userid,
			@PathParam("reading") double reading,
			@PathParam("sgnetwork") String sgnetwork,
			@PathParam("sgoperator") String sgoperator,
			@PathParam("lastinputdate") String lastinputdate,
			@PathParam("handdate") String handdate,
			@PathParam("meterstate") String meterstate) {
		String ret = "";
		try {
			return afrecordInput(userid, 0, reading, sgnetwork, sgoperator,
					lastinputdate, handdate, 0, meterstate, 1, "");
		} catch (Exception e) {
			log.debug(e.getMessage());
			ret = e.getMessage();
		} finally {
			return ret;
		}
	}

	// 根据前台录入购气量计算各阶梯气量金额
	@GET
	@Path("/num/{userid}/{pregas}/{enddate}")
	public JSONObject pregas(@PathParam("userid") String userid, // 用户编号
			@PathParam("pregas") double pregas, // 用气量
			@PathParam("enddate") String endjddate // 结束日期, 格式为yyyymmdd
	) {
		final String usersql = "select isnull(f_stairtype,'未设')f_stairtype, isnull(f_gasprice,0)f_gasprice, "
				+ "isnull(f_stair1amount,0)f_stair1amount,isnull(f_stair2amount,0)f_stair2amount,"
				+ "isnull(f_stair3amount,0)f_stair3amount,isnull(f_stair1price,0)f_stair1price,"
				+ "isnull(f_stair2price,0)f_stair2price,isnull(f_stair3price,0)f_stair3price,"
				+ "isnull(f_stair4price,0)f_stair4price,isnull(f_stairmonths,0)f_stairmonths,isnull(f_zhye,0)f_zhye "
				+ "from t_userinfo where f_userid = '" + userid + "'";
		List<Map<String, Object>> list = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(usersql);
						q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});
		// 取出阶梯气价资料
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		BigDecimal gasprice = new BigDecimal(map.get("f_gasprice").toString());
		BigDecimal stair1amount = new BigDecimal(map.get("f_stair1amount")
				.toString());
		BigDecimal stair2amount = new BigDecimal(map.get("f_stair2amount")
				.toString());
		BigDecimal stair3amount = new BigDecimal(map.get("f_stair3amount")
				.toString());
		BigDecimal stair1price = new BigDecimal(map.get("f_stair1price")
				.toString());
		BigDecimal stair2price = new BigDecimal(map.get("f_stair2price")
				.toString());
		BigDecimal stair3price = new BigDecimal(map.get("f_stair3price")
				.toString());
		BigDecimal stair4price = new BigDecimal(map.get("f_stair4price")
				.toString());
		stairmonths = Integer.parseInt(map.get("f_stairmonths").toString());
		String stairtype = map.get("f_stairtype").toString();

		// 转换结束日期
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(endjddate.substring(0, 4));
		int month = Integer.parseInt(endjddate.substring(4, 6));
		int day = Integer.parseInt(endjddate.substring(6, 8));
		cal.set(year, month - 1, day);

		BigDecimal chargenum = stair(userid, new BigDecimal(pregas), cal,
				stairtype, gasprice, stairmonths, stair1amount, stair2amount,
				stair3amount, stair1price, stair2price, stair3price,
				stair4price);

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
		sell.put("f_chargenum", chargenum);
		sell.put("f_stardate", stardate);
		sell.put("f_enddate", enddate);

		JSONObject json = (JSONObject) new JsonTransfer().MapToJson(sell);
		return json;
	}

	/**
	 * 单块表抄表录入的内部方法，支持卡表及机表，卡表可录入余气量。
	 * 
	 * @param userid
	 * @param lastreading
	 * @param reading
	 * @param sgnetwork
	 * @param sgoperator
	 * @param lastinputdate
	 * @param handdate
	 * @param leftgas
	 * @param meterstate
	 * @param flag
	 * @param orgpathstr
	 *            操作员组织信息
	 * @return
	 * @throws Exception
	 */
	public String afrecordInput(String userid, double lastreading,
			double reading, String sgnetwork, String sgoperator,
			String lastinputdate, String handdate, double leftgas,
			String meterstate, int flag, String orgpathstr) throws Exception {
		// 查找用户未抄表记录
		Map map = this.findHandPlan(userid);
		if (map == null) {
			return "";
		}
		Map user = this.findUser(userid);
		// 获取表类型
		String meterType = map.get("f_gasmeterstyle").toString();
		// 下面程序执行hql变量
		String hql = "";
		// Map<String, String> singles = getSingles();// 获取所有单值
		// BigDecimal chargenum = new BigDecimal(0);
		// BigDecimal sumamont = new BigDecimal(0);
		BigDecimal gasprice = new BigDecimal(map.get("f_gasprice").toString());
		String stairtype = map.get("f_stairtype").toString();
		BigDecimal stair1amount = new BigDecimal(map.get("f_stair1amount")
				.toString());
		BigDecimal stair2amount = new BigDecimal(map.get("f_stair2amount")
				.toString());
		BigDecimal stair3amount = new BigDecimal(map.get("f_stair3amount")
				.toString());
		BigDecimal stair1price = new BigDecimal(user.get("f_stair1price")
				.toString());
		BigDecimal stair2price = new BigDecimal(user.get("f_stair2price")
				.toString());
		BigDecimal stair3price = new BigDecimal(user.get("f_stair3price")
				.toString());
		BigDecimal stair4price = new BigDecimal(user.get("f_stair4price")
				.toString());
		stairmonths = Integer.parseInt(user.get("f_stairmonths").toString());

		BigDecimal gas = new BigDecimal(0);// 用气量
		BigDecimal lrg = new BigDecimal(0);// 上次指数
		if (1 == flag) {
			// 如果是单块表调用的(手机)，上期读数（上期的本次抄表底数）从档案中取
			BigDecimal lastReading = new BigDecimal(map.get("lastinputgasnum")
					+ "");
			lrg = lastReading;
			// 气量
			gas = new BigDecimal(reading).subtract(lastReading);
		} else {
			// 气量 否则从界面上获取上期指数
			gas = new BigDecimal(reading).subtract(new BigDecimal(lastreading));
			lrg = new BigDecimal(lastreading);
		}
		// 如果气量为负数抛出异常，主要针对其他抄表系统发来的数据
		if (gas.compareTo(BigDecimal.ZERO) < 0) {
			throw new RSException(map.get("f_userid") + "用气量为："
					+ gas.doubleValue() + ",不能录入!");
		}

		// 上期读数（上期的本次抄表底数）上期底数（）
		// BigDecimal lastReading = new BigDecimal(map.get("lastinputgasnum") +
		// "");
		// 气量
		// BigDecimal gas = new BigDecimal(reading).subtract(lastReading);
		// 从户里取出余额(上期余额)
		BigDecimal f_zhye = new BigDecimal(user.get("f_zhye") + "");
		// 用户地址
		String address = map.get("f_address").toString();
		// 用户姓名
		String username = map.get("f_username").toString();
		// 以前欠费条数
		int items = Integer.parseInt(map.get("c") + "");
		// 抄表id
		String handid = map.get("id") + "";
		// 用户缴费类型
		String payment = map.get("f_payment").toString();
		// 责任部门
		String zerenbumen = "空";
		// 门站
		String menzhan = "空";
		// 抄表员
		String inputtor = map.get("f_inputtor") + "";
		// 如果抄表员为空则抛出异常，交由上层处理
		if (inputtor.equals("")) {
			throw new RSException(map.get("f_userid") + "没有抄表员，不能录入。");
		}
		// 计划年份，用于阶梯划价，处理跨年录入的问题
		String handdatee = map.get("f_handdate") + "";
		if ("计划空".equals(handdatee)) {
			throw new RSException(map.get("f_userid")
					+ "抄表计划日期空，不能录入。请重新生成抄表单！");
		}
		Calendar cald = Calendar.getInstance();
		int year = Integer.parseInt(handdatee.substring(0, 4));
		cald.set(Calendar.YEAR, year);
		// 最后一次抄表日期
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = lastinputdate.substring(0, 10);
		Date lastinputDate = df.parse(dateStr);
		// 取出抄表日期得到缴费截止日期DateFormat.parse(String s)
		Date date = endDate(lastinputdate, userid);// 缴费截止日期
		// 录入日期
		Date inputdate = new Date();
		// 计划月份
		DateFormat hd = new SimpleDateFormat("yyyy-MM");
		String dateStr1 = handdate.substring(0, 7);
		Date handDate = hd.parse(dateStr1);
		// 当前表累计购气量 （暂）
		BigDecimal f_metergasnums = new BigDecimal(map.get("f_metergasnums")
				+ "");
		// f_cumulativepurchase 总累计购气量
		BigDecimal f_cumulativepurchase = new BigDecimal(
				map.get("f_cumulativepurchase") + "");
		// 户累计购气量 （暂）
		BigDecimal f_metergasnumsu = new BigDecimal(user.get("f_metergasnums")
				+ "");
		// f_cumulativepurchase 总累计购气量
		BigDecimal f_cumulativepurchaseu = new BigDecimal(
				user.get("f_cumulativepurchase") + "");
		// 表状态
		String meterState = meterstate;
		// 针对设置阶梯气价的用户运算
		// 阶梯起价处理
		BigDecimal chargenum = stair(userid, gas, Calendar.getInstance(),
				stairtype, gasprice, stairmonths, stair1amount, stair2amount,
				stair3amount, stair1price, stair2price, stair3price,
				stair4price);
		// 气费大于0,结余够，前面无欠费，自动下账
		if (chargenum.compareTo(BigDecimal.ZERO) > 0
				&& chargenum.compareTo(f_zhye) <= 0 && items < 1) {
			// 自动下账

			// 配置绵竹短信类 存在 再发短信
			ISms sms = (ISms) BeanUtil.getBean(ISms.class);
			if (sms != null) {
				zdSendMsg(user, chargenum, f_zhye.subtract(chargenum)
						.doubleValue());
			}

			double grossproceeds = 0;
			Map<String, Object> sell = new HashMap<String, Object>();
			sell.put("f_userid", map.get("f_userid")); // 表ID
			sell.put("f_userinfoid", user.get("f_userid"));// 用户id
			sell.put("f_orgstr", orgpathstr);// 操作员组织信息
			sell.put("f_payfeevalid", "有效");// 交费是否有效
			sell.put("f_payfeetype", "自动下账");// 收费类型
			// 修改上期指数
			sell.put("lastinputgasnum", lrg.doubleValue()); // 上期底数
			sell.put("lastrecord", reading); // 本期底数
			sell.put("f_totalcost", chargenum.doubleValue()); // 应交金额
			sell.put("f_grossproceeds", grossproceeds); // 收款
			sell.put("f_deliverydate", new Date()); // 交费日期
			sell.put("f_deliverytime", new Date()); // 交费时间
			sell.put("f_zhye", f_zhye.doubleValue()); // 上期结余
			sell.put("f_benqizhye", f_zhye.subtract(chargenum).doubleValue()); // 本期结余
			sell.put("f_gasmeterstyle", map.get("f_gasmeterstyle")); // 气表类型
			sell.put("f_comtype", "天然气公司"); // 公司类型，分为天然气公司、银行
			sell.put("f_username", map.get("f_username")); // 用户/单位名称
			sell.put("f_address", map.get("f_address")); // 地址
			sell.put("f_districtname", map.get("f_districtname")); // 小区名称
			sell.put("f_cusDom", map.get("f_cusDom")); // 楼号
			sell.put("f_cusDy", map.get("f_cusDy")); // 单元
			sell.put("f_idnumber", map.get("f_idnumber")); // 身份证号
			sell.put("f_gaswatchbrand", map.get("f_gaswatchbrand")); // 气表品牌
			sell.put("f_gaspricetype", map.get("f_gaspricetype")); // 气价类型
			sell.put("f_gasprice", gasprice.doubleValue()); // 气价
			sell.put("f_usertype", map.get("f_usertype")); // 用户类型
			sell.put("f_gasproperties", map.get("f_gasproperties")); // 用气性质
			sell.put("f_pregas", gas.doubleValue()); // 气量
			sell.put("f_preamount", chargenum.doubleValue()); // 金额
			sell.put("f_payment", "现金"); // 付款方式
			sell.put("f_sgnetwork", sgnetwork); // 网点
			sell.put("f_sgoperator", sgoperator); // 操 作 员
			sell.put("f_cardid", map.get("f_cardid"));// 卡号
			sell.put("f_filiale", map.get("f_filiale")); // 分公司
			sell.put("f_useful", handid); // 抄表id
			sell.put("f_stair1amount", stair1num.doubleValue());
			sell.put("f_stairtype", user.get("f_stairtype")); // 阶梯气价类型
			sell.put("f_stair2amount", stair2num.doubleValue());
			sell.put("f_stair3amount", stair3num.doubleValue());
			sell.put("f_stair4amount", stair4num.doubleValue());
			sell.put("f_stair1fee", stair1fee.doubleValue());
			sell.put("f_stair2fee", stair2fee.doubleValue());
			sell.put("f_stair3fee", stair3fee.doubleValue());
			sell.put("f_stair4fee", stair4fee.doubleValue());
			sell.put("f_stair1price", stair1price.doubleValue());
			sell.put("f_stair2price", stair2price.doubleValue());
			sell.put("f_stair3price", stair3price.doubleValue());
			sell.put("f_stair4price", stair4price.doubleValue());
			sell.put("f_stardate", stardate);
			sell.put("f_enddate", enddate);
			sell.put("f_allamont", sumamont.doubleValue());
			int sellid = (Integer) hibernateTemplate.save("t_sellinggas", sell);
			this.updateUser(user, f_zhye.subtract(chargenum),
					f_metergasnumsu.add(gas), f_cumulativepurchaseu.add(gas));

			hql = "update t_userfiles set lastinputgasnum=?,"
					+
					// 本次抄表日期
					"  lastinputdate=? "
					+
					// 当前表累计购气量 （暂） 总累计购气量
					",f_metergasnums=  ?, f_cumulativepurchase= ? ,"
					// 最后购气量 最后购气日期 最后购气时间
					+ "f_finallybought= ?, f_finabuygasdate=?, f_finabuygastime=? "
					+ "where f_userid=?";
			hibernateTemplate.bulkUpdate(
					hql,
					new Object[] { reading, lastinputDate,
							f_metergasnums.add(gas).doubleValue(),
							f_cumulativepurchase.add(gas).doubleValue(),
							gas.doubleValue(), inputdate, inputdate, userid });
			String sellId = sellid + "";
			// 更新抄表记录
			hql = "update t_handplan set f_state='已抄表',shifoujiaofei='是',f_handdate=?,f_stairtype='"
					+ stairtype
					+ "',lastinputdate=?,f_zerenbumen='"
					+ zerenbumen
					+ "',f_menzhan='"
					+ menzhan
					+ "', f_inputtor='"
					+ inputtor
					+ "',lastrecord="
					+ reading
					+ " ,"
					+ "oughtamount="
					+ gas
					+ " ,oughtfee="
					+ chargenum
					+ " ,f_address='"
					+ address
					+ "', f_username='"
					+ username
					+ "', f_zhye="
					+ f_zhye
					+ ", f_bczhye="
					+ f_zhye.subtract(chargenum)
					+ ","
					+ "f_stair1amount="
					+ stair1num
					+ ",f_stair2amount="
					+ stair2num
					+ ",f_stair3amount="
					+ stair3num
					+ ",f_stair4amount="
					+ stair4num
					+ ",f_stair1fee="
					+ stair1fee
					+ ",f_stair2fee="
					+ stair2fee
					+ ",f_stair3fee="
					+ stair3fee
					+ ",f_stair4fee="
					+ stair4fee
					+ ",f_stair1price="
					+ stair1price
					+ ",f_stair2price="
					+ stair2price
					+ ",f_stair3price="
					+ stair3price
					+ ",f_stair4price="
					+ stair4price
					+ ","
					+ "f_stardate='"
					+ stardate
					+ "',f_enddate='"
					+ enddate
					+ "',f_allamont="
					+ sumamont
					+ " ,f_sellid="
					+ sellId
					+ ", f_leftgas="
					+ leftgas
					+ ", lastinputgasnum=" // 上期指数
					+ lrg
					+ " , f_inputdate=?,f_meterstate=?,f_network='"
					+ sgnetwork
					+ "',f_filiale='"
					+ map.get("f_filiale")
					+ "',f_operator='"
					+ sgoperator
					+ "'"
					+ "where f_userid='"
					+ userid + "' and f_state='未抄表' and id=" + handid;
			hibernateTemplate.bulkUpdate(hql, new Object[] { handDate,
					lastinputDate, inputdate, meterState });
		} else {
			// 尊敬的天然气用户[{yhbh}][{yhxm}]：您的[{yhdz}]本月天然气用量为[{ql}]
			// 方.气费金额[{qf}]元，请于本月底前到我公司缴纳气费
			// 抄表欠费短信
			ISms sms = (ISms) BeanUtil.getBean(ISms.class);
			if (sms != null) {
				qfSendMsg(user, chargenum, gas);
			}

			// 更新用户档案
			hql = "update t_userfiles " +
			// 本次抄表底数 本次抄表日期
					"set lastinputgasnum=? ,  lastinputdate=?  where f_userid=?";
			hibernateTemplate.bulkUpdate(hql, new Object[] { reading,
					lastinputDate, userid });
			// 欠费,更新抄表记录的状态f_state、抄表日期、本次抄表底数
			// 如果气费 =0 ,是否交费为"是"
			String shifoujiaofei = "否";
			if (chargenum.compareTo(BigDecimal.ZERO) <= 0) {
				shifoujiaofei = "是";
			}
			hql = "update t_handplan set f_state ='已抄表', shifoujiaofei='"
					+ shifoujiaofei
					+ "',f_handdate=?,lastinputdate=?,f_zerenbumen='"
					+ zerenbumen + "', f_menzhan='" + menzhan
					+ "', f_inputtor='" + inputtor + "', lastrecord=" + reading
					+ " ,f_stairtype='" + stairtype + "'," + "oughtamount="
					+ gas + ",  f_endjfdate=?, oughtfee=" + chargenum
					+ ", f_inputdate=?,f_meterstate=?,f_network='" + sgnetwork
					+ "',f_filiale='" + map.get("f_filiale") + ""
					+ "',f_operator='" + sgoperator + "' ,f_address='"
					+ address + "', f_username='" + username + "',"
					+ "f_stair1amount=" + stair1num + ",f_stair2amount="
					+ stair2num + ",f_stair3amount=" + stair3num
					+ ",f_stair4amount=" + stair4num + ",f_stair1fee="
					+ stair1fee + ",f_stair2fee=" + stair2fee + ",f_stair3fee="
					+ stair3fee + ",f_stair4fee=" + stair4fee + ","
					+ "f_stair1price=" + stair1price + ",f_stair2price="
					+ stair2price + ",f_stair3price=" + stair3price
					+ ",f_stair4price=" + stair4price + "," + "f_stardate='"
					+ stardate + "',f_enddate='" + enddate + "',f_allamont="
					+ sumamont + ", f_leftgas= "
					+ leftgas
					+ ", lastinputgasnum=" // 上期指数
					+ lrg + " where f_userid='" + userid
					+ "' and f_state='未抄表' and id=" + handid;
			hibernateTemplate.bulkUpdate(hql, new Object[] { handDate,
					lastinputDate, date, inputdate, meterState });
		}
		// 保存用户清欠账务,并更新档案中账户余额
		if (meterType != null && meterType.equals("机表")
				&& gas.doubleValue() > 0) {
			financedetailDisp(map, gas, chargenum, sgnetwork, sgoperator);
		}
		return userid;
	}

	// 计算阶梯气价，由于重构原因，会返回sumamont，放在全局变量里
	// 计算出来的各阶段阶梯气量及阶梯金额存放在类变量里
	// 返回：总价格
	private BigDecimal stair(String userid, BigDecimal gas, Calendar cal,
			String stairtype, BigDecimal gasprice, int stairmonths,
			BigDecimal stair1amount, BigDecimal stair2amount,
			BigDecimal stair3amount, BigDecimal stair1price,
			BigDecimal stair2price, BigDecimal stair3price,
			BigDecimal stair4price) {
		BigDecimal chargenum = new BigDecimal(0);
		stair1num = new BigDecimal(0);
		stair1fee = new BigDecimal(0);
		stair2num = new BigDecimal(0);
		stair2fee = new BigDecimal(0);
		stair3num = new BigDecimal(0);
		stair3fee = new BigDecimal(0);
		stair4num = new BigDecimal(0);
		stair4fee = new BigDecimal(0);
		stair1surplus = new BigDecimal(0);
		stair2surplus = new BigDecimal(0);
		stair3surplus = new BigDecimal(0);
		// 针对设置阶梯气价的用户运算
		CountDate(userid, hibernateTemplate);
		if (!stairtype.equals("未设")) {
			final String gassql = " select isnull(sum(h.oughtamount),0)oughtamount "
					+ "from t_handplan h left join t_userfiles u on h.f_state='已抄表' and u.f_userid=h.f_userid "
					+ "where u.f_userinfoid=(select f_userinfoid from t_userfiles where f_userid='"
					+ userid
					+ "')"
					+ " and h.f_handdate>='"
					+ stardate
					+ "' and h.f_handdate<='" + enddate + "'";
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
			// 当前购气量
			BigDecimal sumamont = new BigDecimal(gasmap.get("oughtamount")
					.toString());
			// 累计购气量
			BigDecimal allamont = sumamont.add(gas);
			// 当前购气量在第一阶梯
			if (sumamont.compareTo(stair1amount) < 0) {
				if (allamont.compareTo(stair1amount) < 0) {
					stair1surplus = stair1amount.subtract(allamont);
					stair2surplus = stair2amount.subtract(stair1amount);
					stair3surplus = stair3amount.subtract(stair2amount);
					stair1num = gas;
					stair1fee = gas.multiply(stair1price);
					chargenum = gas.multiply(stair1price);
				} else if (allamont.compareTo(stair1amount) >= 0
						&& allamont.compareTo(stair2amount) < 0) {
					stair2surplus = stair2amount.subtract(allamont);
					stair3surplus = stair3amount.subtract(stair2amount);
					stair1num = stair1amount.subtract(sumamont);
					stair1fee = (stair1amount.subtract(sumamont))
							.multiply(stair1price);
					stair2num = allamont.subtract(stair1amount);
					stair2fee = (allamont.subtract(stair1amount))
							.multiply(stair2price);
					chargenum = stair1fee.add(stair2fee);
				} else if (allamont.compareTo(stair2amount) >= 0
						&& allamont.compareTo(stair3amount) < 0) {
					stair3surplus = stair3amount.subtract(allamont);
					stair1num = stair1amount.subtract(sumamont);
					stair1fee = (stair1amount.subtract(sumamont))
							.multiply(stair1price);
					stair2num = stair2amount.subtract(stair1amount);
					stair2fee = (stair2amount.subtract(stair1amount))
							.multiply(stair2price);
					stair3num = allamont.subtract(stair2amount);
					stair3fee = (allamont.subtract(stair2amount))
							.multiply(stair3price);
					chargenum = stair1fee.add(stair2fee).add(stair3fee);
				} else if (allamont.compareTo(stair3amount) >= 0) {
					stair1num = stair1amount.subtract(sumamont);
					stair1fee = (stair1amount.subtract(sumamont))
							.multiply(stair1price);
					stair2num = stair2amount.subtract(stair1amount);
					stair2fee = (stair2amount.subtract(stair1amount))
							.multiply(stair2price);
					stair3num = stair3amount.subtract(stair2amount);
					stair3fee = (stair3amount.subtract(stair2amount))
							.multiply(stair3price);
					stair4num = allamont.subtract(stair3amount);
					stair4fee = (allamont.subtract(stair3amount))
							.multiply(stair4price);
					chargenum = stair1fee.add(stair2fee).add(stair3fee)
							.add(stair4fee);
				}
				// 当前已购气量在阶梯二内
			} else if (sumamont.compareTo(stair1amount) >= 0
					&& sumamont.compareTo(stair2amount) < 0) {
				if (allamont.compareTo(stair2amount) < 0) {
					stair2surplus = stair2amount.subtract(allamont);
					stair3surplus = stair3amount.subtract(stair2amount);
					stair2num = gas;
					stair2fee = gas.multiply(stair2price);
					chargenum = stair2fee;
				} else if (allamont.compareTo(stair2amount) >= 0
						&& allamont.compareTo(stair3amount) < 0) {
					stair3surplus = stair3amount.subtract(allamont);
					stair2num = stair2amount.subtract(sumamont);
					stair2fee = (stair2amount.subtract(sumamont))
							.multiply(stair2price);
					stair3num = allamont.subtract(stair2amount);
					stair3fee = (allamont.subtract(stair2amount))
							.multiply(stair3price);
					chargenum = stair2fee.add(stair3fee);
				} else {
					stair3surplus = stair3amount.subtract(stair2amount);
					stair2num = stair2amount.subtract(sumamont);
					stair2fee = (stair2amount.subtract(sumamont))
							.multiply(stair2price);
					stair3num = stair3amount.subtract(stair2amount);
					stair3fee = (stair3amount.subtract(stair2amount))
							.multiply(stair3price);
					stair4num = allamont.subtract(stair3amount);
					stair4fee = (allamont.subtract(stair3amount))
							.multiply(stair4price);
					chargenum = stair2fee.add(stair3fee).add(stair4fee);
				}
				// 当前已购气量在阶梯三内
			} else if (sumamont.compareTo(stair2amount) >= 0
					&& sumamont.compareTo(stair3amount) < 0) {
				if (allamont.compareTo(stair3amount) < 0) {
					stair3surplus = stair3amount.subtract(allamont);
					stair3num = gas;
					stair3fee = gas.multiply(stair3price);
					chargenum = stair3fee;
				} else {
					stair3num = stair3amount.subtract(sumamont);
					stair3num = stair3amount.subtract(sumamont);
					stair3fee = (stair3amount.subtract(sumamont))
							.multiply(stair3price);
					stair4num = allamont.subtract(stair3amount);
					stair4fee = (allamont.subtract(stair3amount))
							.multiply(stair4price);
					chargenum = stair3fee.add(stair4fee);
				}
				// 当前已购气量超过阶梯三
			} else if (sumamont.compareTo(stair3amount) >= 0) {
				stair4num = gas;
				stair4fee = gas.multiply(stair4price);
				chargenum = stair4fee;
			}
			// 该用户未设置阶梯气价
		} else {
			chargenum = gas.multiply(gasprice);
			stair1num = new BigDecimal(0);
			stair2num = new BigDecimal(0);
			stair3num = new BigDecimal(0);
			stair4num = new BigDecimal(0);
			stair1fee = new BigDecimal(0);
			stair2fee = new BigDecimal(0);
			stair3fee = new BigDecimal(0);
			stair4fee = new BigDecimal(0);
		}

		return chargenum;
	}

	/**
	 * 保存用户财务明细
	 */
	private void financedetailDisp(Map<String, Object> handplan,
			BigDecimal gas, BigDecimal money, String sgnetwork,
			String sgoperator) throws Exception {
		// 取出账户结余
		String acczhyeStr = handplan.get("f_accountzhye").toString();
		if (acczhyeStr == null || acczhyeStr.equals("")) {
			throw new RSException(handplan.get("f_userid") + "没有账户实际结!");
		}
		BigDecimal accountzhye = new BigDecimal(acczhyeStr);
		// 如果账户余额 = 0，抄表气费为欠费，不产生清欠记录
		if (accountzhye.doubleValue() <= 0) {
			String handId = handplan.get("id").toString();
			noBalance(handId, money);
			return;
		}
		// 清欠处理 ,计算实收，欠费，账户最新结余
		BigDecimal realmony = new BigDecimal(0);
		BigDecimal debtmoney = new BigDecimal(0);
		BigDecimal newaccountzhye = new BigDecimal(0);
		// 结余 > 应收 ， 已收金额 =应收金额, 欠费金额= 0,账户余额 = 结余- 应收
		if (accountzhye.compareTo(money) >= 0) {
			realmony = money;
			debtmoney = new BigDecimal(0);
			newaccountzhye = accountzhye.subtract(money);
		}
		// 结余小于应收, 已收金额=结余金额, ,欠费金额=应收金额-结余金额,
		// 账户余额= 0
		else if (accountzhye.compareTo(money) < 0) {
			realmony = accountzhye;
			debtmoney = money.subtract(accountzhye);
			newaccountzhye = new BigDecimal(0);
		}
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
		finance.put("f_userid", handplan.get("f_userid"));
		// 原账户余额
		finance.put("f_prevaccountzhye", accountzhye.doubleValue());
		// <!--应收气量-->
		finance.put("f_oughtamount", gas.doubleValue());
		// <!--应收金额-->
		finance.put("f_oughtfee", money.doubleValue());
		// 单价
		BigDecimal gasPrice = new BigDecimal(handplan.get("f_gasprice")
				.toString());
		finance.put("f_gasprice", gasPrice.doubleValue());
		// 抄表记录的日期
		finance.put("f_debtdate", now);
		// <!--是否有效(有效/无效)-->
		finance.put("f_payfeevalid", "有效");
		finance.put("f_payfeetype", "抄表");
		// <!--网点-->
		finance.put("f_sgnetwork", sgnetwork);
		// <!--操作员-->
		finance.put("f_opertor", sgoperator);
		// 操作日期，时间
		finance.put("f_deliverydate", now);
		finance.put("f_deliverytime", now);
		// 抄表id
		finance.put("f_handid", handplan.get("id"));
		// 保存
		JSONObject financeJson = (JSONObject) new JsonTransfer()
				.MapToJson(finance);
		log.debug("保存清欠明细数据" + financeJson);
		Object idObj = hibernateTemplate.save("t_financedetail", finance);
		int saveId = Integer.parseInt(idObj.toString());
		log.debug("保存成功,数据id" + saveId);
		// 更新档案账户结余f_accountzhye
		String uid = finance.get("f_userid").toString();
		String accZhye = finance.get("f_accountzhye").toString();
		String updateUserFile = "update t_userfiles set f_accountzhye="
				+ accZhye + " where f_userid='" + uid + "'";
		log.debug("更新档案账户结余" + updateUserFile);
		this.hibernateTemplate.bulkUpdate(updateUserFile);
		log.debug("更新档案账户成功");
		// 更新抄表记录实际欠费
		String handId = handplan.get("id").toString();
		String updateHandplan = "update t_handplan set f_debtmoney="
				+ debtmoney + " where id='" + handId + "'";
		log.debug("更新抄表欠费" + updateHandplan);
		this.hibernateTemplate.bulkUpdate(updateHandplan);
	}

	// 无账户实际结余处理
	private void noBalance(String handId, BigDecimal money) {
		try {
			String updateHandplan = "update t_handplan set f_debtmoney="
					+ money.doubleValue() + " where id='" + handId + "'";
			log.debug("账户实际结余0,更新抄表" + updateHandplan + "欠款"
					+ money.doubleValue());
			this.hibernateTemplate.bulkUpdate(updateHandplan);
		} catch (RuntimeException e) {
			throw new RSException("结余为0时,更新抄表" + handId + "失败");
		}

	}

	/**
	 * 查找用户未抄表记录
	 */
	private Map<String, Object> findHandPlan(String userid) {
		Map<String, Object> result = null;
		String hql = "";
		final String sql = "select isnull(u.f_userid,'') f_userid, isnull(u.f_zhye,'') f_zhye ,isnull(u.f_accountzhye,'') f_accountzhye ,  isnull(u.lastinputgasnum,'') lastinputgasnum, isnull(u.f_gasprice,0) f_gasprice, isnull(u.f_username,'')  f_username,"
				+ "isnull(u.f_stair1amount,0)f_stair1amount,isnull(u.f_stair2amount,0)f_stair2amount,isnull(u.f_stair3amount,0)f_stair3amount,isnull(u.f_stair1price,0)f_stair1price,isnull(u.f_stair2price,0)f_stair2price,isnull(u.f_stair3price,0)f_stair3price,isnull(u.f_stair4price,0)f_stair4price,isnull(u.f_stairmonths,0)f_stairmonths,isnull(u.f_stairtype,'未设')f_stairtype,"
				+ "isnull(u.f_address,'')f_address ,isnull(u.f_districtname,'')f_districtname,isnull(u.f_cusDom,'')f_cusDom,isnull(u.f_cusDy,'')f_cusDy,isnull(u.f_gasmeterstyle,'') f_gasmeterstyle, isnull(u.f_idnumber,'') f_idnumber, isnull(u.f_gaswatchbrand,'')f_gaswatchbrand, isnull(u.f_usertype,'')f_usertype, "
				+ "isnull(u.f_gasproperties,'')f_gasproperties,isnull(u.f_dibaohu,0)f_dibaohu,isnull(u.f_payment,'')f_payment,isnull(u.f_zerenbumen,'')f_zerenbumen,isnull(u.f_menzhan,'')f_menzhan,isnull(u.f_inputtor,'')f_inputtor, isnull(q.c,0) c,"
				+ "isnull(u.f_metergasnums,0) f_metergasnums,isnull(u.f_cumulativepurchase,0)f_cumulativepurchase, "
				+ "isnull(u.f_finallybought,0)f_finallybought,isnull(u.f_cardid,'NULL') f_cardid,isnull(u.f_filiale,'NULL')f_filiale,"
				+ "h.id id, isnull(CONVERT(varchar(12), h.f_handdate, 120 ),'计划空') f_handdate from (select * from t_handplan where f_state='未抄表' and f_userid='"
				+ userid
				+ "') h "
				+ "left join (select f_userid, COUNT(*) c from t_handplan where f_state='已抄表' and f_userid='"
				+ userid
				+ "' and shifoujiaofei='否' "
				+ "group by f_userid) q on h.f_userid=q.f_userid join t_userfiles u on h.f_userid=u.f_userid";
		List<Map<String, Object>> list = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(sql);
						q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});
		// 取出未抄表记录以及资料
		if (list.size() > 0) {
			result = (Map<String, Object>) list.get(0);
			return result;
		} else {
			return null;
		}
	}

	// 计算开始时间方法
	private void CountDate(String userid, HibernateTemplate hibernateTemplate) {
		// 判断是否配置了接口，如果有执行接口，如果没有按默认计算。
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(ContextListener.getContext());
		if (applicationContext.containsBean("CountDate")) {
			ICountDate icount = (ICountDate) applicationContext
					.getBean("CountDate");
			stardate = icount.startdate(userid, hibernateTemplate);
			enddate = icount.enddate(userid, hibernateTemplate);
			return;
		}
		// 计算当前月在哪个阶梯区间
		Calendar cal = Calendar.getInstance();
		int thismonth = cal.get(Calendar.MONTH) + 1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (stairmonths == 1) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		} else if (stairmonths == 0) {
			stardate = "";
			enddate = "";
		} else {
			/*
			 * 阶梯起始月数计算起始月 = 当前月/阶梯月数*阶梯月数+1结束月 = 当前月/阶梯月数*阶梯月数+阶梯月数注：该运算
			 * 当前月是12月时则需要剪1 上面已经算出阶梯月数为1个月时的金额一下运算阶梯月数至少为两个月 所以对算区间没有影响
			 */
			if (thismonth == 12) {
				thismonth = 11;
			}
			// 计算起始月
			int star = Math.round(thismonth / stairmonths) * stairmonths + 1;
			// 计算结束月
			int end = Math.round(thismonth / stairmonths) * stairmonths
					+ stairmonths;
			// 获得起始日期和结束日期
			cal.set(Calendar.MONTH, star - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.MONTH, end - 1);
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
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

	private Object String(String zerenbumen) {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("record/batch/App")
	@POST
	public String afAPPUploadBatch(String data) {
		log.debug("App抄表记录上传 开始");
		JSONObject jo = new JSONObject();
		try {
			JSONArray rows = new JSONArray(data);
			String re = "";
			// 对每一个数据，调用单个抄表数据处理过程
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				String userid = row.getString("f_userid");
				double reading = Double
						.parseDouble(row.getString("lastrecord"));
				String handdate = row.getString("f_handdate");
				String network = row.getString("f_network");
				String operator = row.getString("f_operator");
				String inputdate = row.getString("f_inputdate");
				String meterstate = row.getString("f_meterstate");
				double lastreading = row.getDouble("lastreading");
				// 获取余气量，机表录入，没有余气量，传Double.NaN
				double leftgas = 0;
				if (row.has("leftgas")) {
					leftgas = row.getDouble("leftgas");
				}
				if ("noPlan".equals(row.getString("source"))) {
				} else {
					if (findHandPlan(userid) == null) {
						jo.put(userid, "null");
					} else {
						re = afrecordInput(userid, lastreading, reading,
								network, operator, inputdate, handdate,
								leftgas, meterstate, 2, "");
						jo.put(re, "ok");
					}
				}
			}
			return jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return jo.toString();
		}
	}

	// 批量抄表记录上传
	// data以JSON格式上传，[{userid:'用户编号', showNumber:本期抄表数},{}]
	@Path("record/batch/{handdate}/{sgnetwork}/{sgoperator}/{lastinputdate}/{meterstate}/{orgpathstr}")
	@POST
	public String afRecordInputForMore(String data,
			@PathParam("sgnetwork") String sgnetwork,
			@PathParam("sgoperator") String sgoperator,
			@PathParam("lastinputdate") String lastinputdate,
			@PathParam("handdate") String handdate,
			@PathParam("meterstate") String meterstate,
			@PathParam("orgpathstr") String orgpathstr) {
		log.debug("批量抄表记录上传 开始");
		String ret = "";
		// 错误信息
		String error = "";
		try {
			// 取出所有数据
			JSONArray rows = new JSONArray(data);
			// 对每一个数据，调用单个抄表数据处理过程
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				String userid = row.getString("userid");
				double reading = row.getDouble("reading");
				double lastreading = row.getDouble("lastreading");
				// 获取余气量，机表录入，没有余气量，传Double.NaN
				double leftgas = 0;
				if (row.has("leftgas")) {
					leftgas = row.getDouble("leftgas");
				}

				try {
					afrecordInput(userid, lastreading, reading, sgnetwork,
							sgoperator, lastinputdate, handdate, leftgas,
							meterstate, 2, orgpathstr);
					// 获得自定义异常
				} catch (RSException e) {
					// 拼接错误信息
					error += e.getMessage();
				}
			}
			// 如果有错误信息则抛出异常，返回到前台提示
			if (!error.equals("")) {
				throw new RSException(error);

			}
			log.debug("批量抄表记录上传 结束");
		} catch (Exception e) {
			log.debug("批量抄表记录上传 失败：" + e.getMessage());
			ret = e.getMessage();
		} finally {
			return ret;
		}
	}

	/**
	 * 抄表撤销
	 * 
	 * @param handid
	 *            抄表id
	 * @return
	 */
	@Path("handrevoke/{handid}")
	@GET
	public JSONObject afhandinputrevoke(@PathParam("handid") String handid) {
		JSONObject result = new JSONObject();
		// 查询抄表记录
		String hql = "from t_handplan where id=" + handid;
		List list = this.hibernateTemplate.find(hql);
		if (list.size() > 1) {
			// 查询出多条
		}
		// 抄表对象
		Map<String, Object> hand = (Map<String, Object>) list.get(0);
		String f_state = hand.get("f_state") + "";
		if (!f_state.equals("已抄表")) {
			// 没有抄表，不能撤销
		}
		// 撤销抄表记录
		hand.put("f_state", "未抄表");
		return result;
	}

	// 批量抄表走收录入
	@Path("record/payfeeforhand")
	@POST
	public String afRecordInputForZS(String data,
			@PathParam("handdate") String handdate) {
		log.debug("批量抄表记录上传 开始");
		String ret = "";
		// 错误信息
		String error = "";
		try {
			// 取出所有数据
			JSONArray rows = new JSONArray(data);
			// 对每一个数据，调用单个抄表数据处理过程
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				Map map = JSONHelper.toHashMap(row);
				String userid = row.getString("f_userid");
				double lastinputgasnum = row.getDouble("lastinputgasnum");
				double lastrecord = row.getDouble("lastrecord");
				double oughtamount = row.getDouble("oughtamount");
				afhandinput(map);
			}
			log.debug("批量抄表记录上传 结束");
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("批量抄表记录上传 失败：" + e.getMessage());
			ret = e.getMessage();
		} finally {
			return ret;
		}
	}

	/**
	 * 单个抄表，只处理抄表记录
	 * 
	 * @param hand
	 * @throws JSONException
	 */
	private void afhandinput(Map data) throws Exception {
		String userid = data.get("f_userid") + "";
		Map userinfo = this.findUser(userid);
		Map hand = this.findHandPlan(userid);
		// 获取表类型
		String meterType = (String) hand.get("f_gasmeterstyle");
		String stairtype = (String) userinfo.get("f_stairtype");
		BigDecimal gas = new BigDecimal(data.get("oughtamount") + "");
		BigDecimal stair1amount = new BigDecimal(hand.get("f_stair1amount")
				.toString());
		BigDecimal stair2amount = new BigDecimal(hand.get("f_stair2amount")
				.toString());
		BigDecimal stair3amount = new BigDecimal(hand.get("f_stair3amount")
				.toString());
		BigDecimal stair1price = new BigDecimal(hand.get("f_stair1price")
				.toString());
		BigDecimal stair2price = new BigDecimal(hand.get("f_stair2price")
				.toString());
		BigDecimal stair3price = new BigDecimal(hand.get("f_stair3price")
				.toString());
		BigDecimal stair4price = new BigDecimal(hand.get("f_stair4price")
				.toString());
		BigDecimal stair1fee = new BigDecimal(data.get("f_stair1fee")
				.toString());
		BigDecimal stair2fee = new BigDecimal(data.get("f_stair2fee")
				.toString());
		BigDecimal stair3fee = new BigDecimal(data.get("f_stair3fee")
				.toString());
		BigDecimal stair4fee = new BigDecimal(data.get("f_stair4fee")
				.toString());
		BigDecimal f_zhye = new BigDecimal(userinfo.get("f_zhye") + "");
		BigDecimal chargenum = new BigDecimal(data.get("f_chargenum") + "");
		// 产生交费记录
		BigDecimal grossproceeds = new BigDecimal(data.get("f_grossproceeds")
				+ "");
		BigDecimal lastinputgasnum = new BigDecimal(data.get("lastinputgasnum")
				+ "");
		BigDecimal lastrecord = new BigDecimal(data.get("lastrecord") + "");
		BigDecimal oughtfee = new BigDecimal(data.get("oughtfee") + "");
		Map<String, Object> sell = new HashMap<String, Object>();
		sell.put("f_userid", data.get("f_userid")); // 表ID
		sell.put("f_userinfoid", userinfo.get("f_userid"));// 用户id
		sell.put("f_orgstr", data.get("f_orgstr"));// 操作员组织信息
		sell.put("f_payfeevalid", "有效");// 交费是否有效
		sell.put("f_payfeetype", "机表交费");// 收费类型
		// 修改上期指数
		sell.put("lastinputgasnum", lastinputgasnum.doubleValue()); // 上期底数
		sell.put("lastrecord", lastrecord.doubleValue()); // 本期底数
		sell.put("f_totalcost", oughtfee.doubleValue()); // 应交金额
		sell.put("f_grossproceeds", grossproceeds.doubleValue()); // 收款
		sell.put("f_deliverydate", new Date()); // 交费日期
		sell.put("f_deliverytime", new Date()); // 交费时间
		sell.put("f_zhye", userinfo.get("f_zhye")); // 上期结余
		BigDecimal f_benqizhye = new BigDecimal(f_zhye.subtract(chargenum)
				.doubleValue());
		if (f_benqizhye.compareTo(new BigDecimal(0)) < 0) {
			f_benqizhye = new BigDecimal(0);
		}
		sell.put("f_benqizhye", f_benqizhye.doubleValue()); // 本期结余
		sell.put("f_gasmeterstyle", hand.get("f_gasmeterstyle")); // 气表类型
		sell.put("f_comtype", "天然气公司"); // 公司类型，分为天然气公司、银行
		sell.put("f_username", userinfo.get("f_username")); // 用户/单位名称
		sell.put("f_address", userinfo.get("f_address")); // 地址
		sell.put("f_districtname", userinfo.get("f_districtname")); // 小区名称
		sell.put("f_cusDom", userinfo.get("f_cusDom")); // 楼号
		sell.put("f_gasmeterstyle", meterType);
		sell.put("f_cusDy", userinfo.get("f_cusDy")); // 单元
		sell.put("f_idnumber", userinfo.get("f_idnumber")); // 身份证号
		sell.put("f_gaswatchbrand", hand.get("f_gaswatchbrand")); // 气表品牌
		sell.put("f_gaspricetype", hand.get("f_gaspricetype")); // 气价类型
		sell.put("f_usertype", hand.get("f_usertype")); // 用户类型
		sell.put("f_gasproperties", hand.get("f_gasproperties")); // 用气性质
		sell.put("f_pregas", gas.doubleValue()); // 气量
		BigDecimal gasfee = new BigDecimal(data.get("gasfee") + "");
		sell.put("f_preamount", gasfee.doubleValue()); // 金额
		sell.put("f_payment", "现金"); // 付款方式
		sell.put("f_sgnetwork", data.get("f_sgnetwork")); // 网点
		sell.put("f_sgoperator", data.get("f_sgoperator")); // 操 作 员
		sell.put("f_filiale", data.get("f_filiale")); // 分公司
		sell.put("f_useful", data.get("id") + ""); // 抄表id
		sell.put("f_stair1amount", stair1amount.doubleValue());
		sell.put("f_stairtype", userinfo.get("f_stairtype")); // 阶梯气价类型
		sell.put("f_stair2amount", stair2amount.doubleValue());
		sell.put("f_stair3amount", stair3amount.doubleValue());
		sell.put("f_stair1fee", stair1fee.doubleValue());
		sell.put("f_stair2fee", stair2fee.doubleValue());
		sell.put("f_stair3fee", stair3fee.doubleValue());
		sell.put("f_stair4fee", stair4fee.doubleValue());
		sell.put("f_stair1price", stair1price.doubleValue());
		sell.put("f_stair2price", stair2price.doubleValue());
		sell.put("f_stair3price", stair3price.doubleValue());
		sell.put("f_stair4price", stair4price.doubleValue());
		sell.put("f_stardate", data.get("f_stardate"));
		sell.put("f_enddate", data.get("f_enddate"));
		int sellid = (Integer) hibernateTemplate.save("t_sellinggas", sell);
		// 户累计购气量 （暂）
		BigDecimal f_metergasnumsu = new BigDecimal(
				userinfo.get("f_metergasnums") + "");
		BigDecimal f_cumulativepurchaseu = new BigDecimal(
				userinfo.get("f_cumulativepurchase") + "");
		// 更新户
		this.updateUser(userinfo, f_zhye.subtract(chargenum),
				f_metergasnumsu.add(gas), f_cumulativepurchaseu.add(gas));
		// 当前表累计购气量 （暂）
		BigDecimal f_metergasnums = new BigDecimal(hand.get("f_metergasnums")
				+ "");
		// f_cumulativepurchase 总累计购气量
		BigDecimal f_cumulativepurchase = new BigDecimal(
				hand.get("f_cumulativepurchase") + "");
		String hql = "update t_userfiles set lastinputgasnum="
				+ data.get("lastrecord") + ","
				+
				// 本次抄表日期
				"  lastinputdate=? "
				+
				// 当前表累计购气量 （暂） 总累计购气量
				",f_metergasnums=  ?, f_cumulativepurchase= ? ,"
				// 最后购气量 最后购气日期 最后购气时间
				+ "f_finallybought= ?, f_finabuygasdate=?, f_finabuygastime=? "
				+ "where f_userid=?";
		hibernateTemplate.bulkUpdate(hql, new Object[] { new Date(),
				f_metergasnums.add(gas).doubleValue(),
				f_cumulativepurchase.add(gas).doubleValue(), gas.doubleValue(),
				new Date(), new Date(), userid });
		String sellId = sellid + "";
		// 更新抄表记录
		hql = "update t_handplan set f_state='已抄表',shifoujiaofei='是',f_handdate='"
				+ data.get("handdate")
				+ "',f_stairtype='"
				+ stairtype
				+ "',lastinputdate=?, f_inputtor='"
				+ userinfo.get("f_inputtor")
				+ "',lastrecord="
				+ data.get("lastrecord")
				+ " ,"
				+ "oughtamount="
				+ gas.doubleValue()
				+ " ,oughtfee="
				+ chargenum.doubleValue()
				+ " ,f_address='"
				+ userinfo.get("f_address")
				+ "', f_username='"
				+ userinfo.get("f_username")
				+ "', f_zhye="
				+ f_zhye
				+ ", f_bczhye="
				+ f_zhye.subtract(chargenum)
				+ ","
				+ "f_stair1amount="
				+ stair1num
				+ ",f_stair2amount="
				+ stair2num
				+ ",f_stair3amount="
				+ stair3num
				+ ",f_stair4amount="
				+ stair4num
				+ ",f_stair1fee="
				+ stair1fee
				+ ",f_stair2fee="
				+ stair2fee
				+ ",f_stair3fee="
				+ stair3fee
				+ ",f_stair4fee="
				+ stair4fee
				+ ",f_stair1price="
				+ stair1price
				+ ",f_stair2price="
				+ stair2price
				+ ",f_stair3price="
				+ stair3price
				+ ",f_stair4price="
				+ stair4price
				+ ","
				+ "f_stardate='"
				+ data.get("stardate")
				+ "',f_enddate='"
				+ data.get("enddate")
				+ "',f_allamont="
				+ data.get("sumamont")
				+ " ,f_sellid="
				+ sellId
				+ ", lastinputgasnum=" // 上期指数
				+ data.get("lastinputgasnum")
				+ " , f_inputdate=?,f_meterstate=?,f_network='"
				+ data.get("f_sgnetwork")
				+ "',f_filiale='"
				+ data.get("f_filiale")
				+ "',f_operator='"
				+ data.get("f_sgoperator")
				+ "'  "
				+ "where f_userid='"
				+ userid + "' and f_state='未抄表' and id=" + data.get("id");
		hibernateTemplate.bulkUpdate(hql, new Object[] { new Date(),
				new Date(), data.get("f_meterstate") });

	}

	private String SolitaryCopyMeter() {
		return null;

	}

	// 产生交费截止日期
	private Date endDate(String str, String userid) throws ParseException {
		// 查找是否配置了截止日期处理类，如果有执行处理类
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(ContextListener.getContext());
		if (applicationContext.containsBean("EndDate")) {
			IEndDate end = (IEndDate) applicationContext.getBean("EndDate");
			return end.enddate(userid, hibernateTemplate).getTime();
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = str.substring(0, 10);
		Date now = df.parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DATE, 10);
		return c.getTime();
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

	/**
	 * 查找用户信息
	 */
	private Map<String, Object> findUser(String userid) {
		final String userSql = "from t_userinfo  where f_userid= (select f_userinfoid from t_userfiles where  f_userid = '"
				+ userid + "')";
		// List userlist = session.createQuery(userSql).list();
		log.debug("查询户信息开始:" + userSql);
		List<Object> userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return null;
		}
		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
		return userMap;
	}

	/**
	 * 更新用户信息
	 */
	private void updateUser(Map user, BigDecimal nowye,
			BigDecimal newMeterGasNums, BigDecimal newCumuGas) throws Exception {
		if (nowye.compareTo(new BigDecimal(0)) < 0) {
			nowye = new BigDecimal(0);
		}
		// 更新用户
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String dt = format.format(now);
		String tm = format.format(now);
		String sql = "update t_userinfo  set f_zhye=" + nowye.doubleValue()
				+ ", f_finabuygasdate='" + dt + "', f_finabuygastime='" + tm
				+ "'," + " f_metergasnums=" + newMeterGasNums.doubleValue()
				+ ", f_cumulativepurchase=" + newCumuGas.doubleValue()
				+ " where f_userid='" + user.get("f_userid") + "'";
		log.debug("更新户信息开始:" + sql);
		this.hibernateTemplate.bulkUpdate(sql);
	}

	private void zdSendMsg(Map user, BigDecimal chargenum, Double f_benqizhye) {
		JSONObject attr = new JSONObject();
		JSONObject rt = new JSONObject();
		SmsService smsService = new SmsService();
		smsService.setHibernateTemplate(hibernateTemplate); // new时候应该设置模板
		// [{f_userid=11007035, f_username=lyf}] qf syje
		String param = "{f_userid=" + user.get("f_userid").toString()
				+ ", f_username=" + user.get("f_username").toString() + ",qf="
				+ chargenum.toString() + ",syje=" + f_benqizhye.toString()
				+ "}";

		rt = smsService.sendTemplate(param, user.get("f_phone").toString(),
				"自动下账");
	}

	private void qfSendMsg(Map user, BigDecimal chargenum, BigDecimal gas) {
		JSONObject attr = new JSONObject();
		JSONObject rt = new JSONObject();
		SmsService smsService = new SmsService();
		smsService.setHibernateTemplate(hibernateTemplate); // new时候应该设置模板
		String param = "{f_userid=" + user.get("f_userid").toString()
				+ ", f_username=" + user.get("f_username").toString() + ",ql="
				+ gas.toString() + ",qf=" + chargenum.toString() + "}";

		rt = smsService.sendTemplate(param, user.get("f_phone").toString(),
				"抄表扣费发送");
	}
}
