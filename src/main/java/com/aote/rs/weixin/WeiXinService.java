package com.aote.rs.weixin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.tcp.TcpService;
import com.aote.rs.util.StringUtil;
import com.sun.xml.bind.v2.model.core.PropertyInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

@Path("weixin")
@Scope("prototype")
@Component
public class WeiXinService {
	static Logger log = Logger.getLogger(WeiXinService.class);

	/**
	 * 获得授权code
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("getcode")
	public String getcode(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws Exception {
		System.out.println("weixin-getcode");
		String id = request.getSession().getId();
		String state = request.getParameter("state");
		String header = request.getHeader("user-agent");
		System.out.println("header-" + header);
		String[] str = header.split("MicroMessenger/");
		// StringTokenizer st = new StringTokenizer(header,"/");
		// st.nextToken();
		// 得到用户的浏览器名
		// String banben = st.nextToken();
		// System.out.println("banben-"+str[1]);
		// 得到用户的操作系统名
		// String useros = st.nextToken();
		// System.out.println("useros-"+useros);
		// System.out.println(state);
		System.out.println("sessionid=" + id);
		String result = "";
		try {
			// http://weixin.uxinxin.com
			// http://4504a3ef.nat123.net/rs/weixin/notify
			String appid = Configure.getAppid();
			
			// String redirect_uri = "http://weixin.uxinxin.com/rs/weixin/getopenid";
//			String redirect_uri = "http://aofeng.s1.natapp.cc/rs/weixin/getopenid";
			String redirect_uri = "http://weixin.uxinxin.com/test/rs/weixin/getopenid";
//			System.out.println("==============");
			String code_uri = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
					+ appid
					+ "&redirect_uri="
					+ redirect_uri
					+ "&response_type=code&scope=snsapi_base&state=" + state;
			response.sendRedirect(code_uri);
			System.out.println("++++++++++++++++++++++++");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得用户openid
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GET
	@Path("getopenid")
	public String getopenid(@Context HttpServletRequest request,
			@Context HttpServletResponse response)
			throws UnsupportedEncodingException {
		System.out.println("weixin-getopendid");
		String id = request.getSession().getId();
		System.out.println("sessionid=" + id);
		// code 说明 ： code 作为换取 access_token 的票据，每次用户授权带上的 code 将不一样， code 只能使用一
		// 次， 5 分钟未被使用自动过期。
		String code = request.getParameter("code");
		// 如果code为空，说明若用户禁止授权
		if (code == null) {
		}

		String state = request.getParameter("state");
		System.out.println(state);
		String openid = "";
		try {
			String appid = Configure.getAppid();
			String secret = Configure.getSecret();
			// 发送code，获得openid
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(
					"https://api.weixin.qq.com/sns/oauth2/access_token?appid="
							+ appid + "&secret=" + secret + "&code=" + code
							+ "&grant_type=authorization_code");
			HttpResponse httpResponse;
			httpResponse = httpclient.execute(getRequest);
			HttpEntity entity = httpResponse.getEntity();
			String uuid = UUID.randomUUID().toString();

			if (entity != null) {
				String str = EntityUtils.toString(entity, "UTF-8");
				JSONObject obj = new JSONObject(str);
				System.out.println(obj.toString());
				openid = obj.getString("openid");
			} else {

			}
			if (state.equals("relieve")) {
				String redirect_url = "/jbind.jsp?openid=" + openid
						+ "&showwxpaytitle=1" + "&uuid=" + uuid;
				response.sendRedirect(redirect_url);
			}else if(state.equals("qianysoft")){
				Map<String, Object> map = selList(openid);
				if(map == null){
					// 重定向到绑定页面
					String return_url = "http://aftest.qynetwork.net/mobile/index.php";
					response.sendRedirect(return_url);
				}else{
				String userid = map.get("f_userid").toString();
				String to_Url = "http://aftest.qynetwork.net/mobile/index.php?member_u_key=" + userid;
				response.sendRedirect(to_Url);
				}
			} else {
				Map<String, Object> map = selList(openid);
				if (map == null) {
					// 重定向到绑定页面
					String redirect_url = "/test/bind.jsp?openid=" + openid
							+ "&showwxpaytitle=1" + "&uuid=" + uuid;
					response.sendRedirect(redirect_url);
				} else {
					String userid = map.get("f_userid").toString();
					// String f_username = map.get("f_username").toString();

					// f_username = f_username.getBytes("utf-8").toString();
					// f_username="测试".getBytes("GBK").toString();

					// 重定向到预支付界面
					JSONObject object = selectqf(userid);
					JSONArray arr = object.getJSONArray("arr");
					double f_zhye = object.getDouble("zhye");
					double money = object.getDouble("money");
					double zhinajin = object.getDouble("zhinajin");
					System.out.println("f_zhye" + f_zhye + "money" + money
							+ "zhinajin" + zhinajin);
					// 为卡表用户添加的获取账户余额
					double ic_zhye = object.getDouble("ic_zhye");
					
					
					String f_userid = object.getString("f_userid");
					String f_username = URLEncoder.encode(
							object.getString("f_username"), "utf-8");
					String f_address = URLEncoder.encode(
							object.getString("f_address"), "utf-8");

					// 为卡表用户添加
					String f_gasmeterstyle = URLEncoder.encode(
							object.getString("f_gasmeterstyle"), "utf-8");
					String f_cardid = object.getString("f_cardid");

					double pregas = getPregas(f_userid);
					System.out.println("pregas" + pregas);

					String redirect_url = "/test/qf.html?openid=" + openid
							+ "&showwxpaytitle=1" + "&f_zhye=" + f_zhye
							+ "&money=" + money + "&zhinajin=" + zhinajin
							+ "&arr=" + arr + "&f_userid=" + f_userid
							+ "&f_username=" + f_username + "&f_address="
							+ f_address + "&uuid=" + uuid + "&f_gasmeterstyle="
							+ f_gasmeterstyle + "&f_cardid=" + f_cardid
							+ "&ic_zhye=" + ic_zhye+ "&pregas=" + pregas;
					System.out.println(redirect_url);
					response.sendRedirect(redirect_url);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject selectqf(String f_userid) throws JSONException,
			UnsupportedEncodingException {
		// 查询欠费金额;
		JSONObject object = wxquery(f_userid);
		byte[] b = object.getString("return").getBytes();
		int len = Integer.parseInt(new String(b, 0, 8));
		JSONObject obj1 = new JSONObject();
		double zhye = Double.parseDouble(new String(b, 98, 10)) / 100;
		String money = new String(b, 108, 10);
		double zhinajin = Double.parseDouble(new String(b, 118, 10)) / 100;
		double zhyemoney;
		int index1 = money.indexOf("-");
		if (index1 != -1) {
			String newStr = money.replaceAll("-", "0");
			System.out.println(newStr);
			zhyemoney = Double.parseDouble(newStr) / 100;
			System.out.println(zhyemoney);

		} else {
			zhyemoney = -Double.parseDouble(new String(b, 108, 10)) / 100;
		}
		int x = Integer.parseInt(new String(b, 128, 3));
		JSONObject obj = null;
		JSONArray arr = new JSONArray();
		obj1.put("zhye", zhye);
		obj1.put("money", zhyemoney);
		obj1.put("zhinajin", zhinajin);
		JSONObject o = selList1(f_userid);
		obj1.put("f_userid", f_userid);
		obj1.put("f_username", o.getString("f_username"));
		obj1.put("f_gasmeterstyle", o.getString("f_gasmeterstyle"));
		obj1.put("f_cardid", o.getString("f_cardid"));
		obj1.put("ic_zhye", o.getString("f_zhye"));
		// 又来判断是卡表还是基表用户
		obj1.put("f_address", o.getString("f_address"));

		for (int i = x; i > 0; i--) {
			obj = new JSONObject();
			String le = new String(b, len + 8 - 68 * i, 68);
			System.out.println(le);

			obj.put("f_userid", new String(b, 12, 10));

			String name = o.getString("f_username");
			String f_username = URLEncoder.encode(name, "utf-8");
			obj.put("f_name", f_username);
			obj.put("lastinputdate", le.substring(0, 8));
			obj.put("astinputgasnum", Integer.parseInt(le.substring(9, 18)));
			obj.put("lastrecord", Integer.parseInt(le.substring(19, 28)));
			obj.put("oughtmount", Integer.parseInt(le.substring(29, 38)));
			obj.put("totaloughtfee",
					Double.parseDouble(le.substring(39, 48)) / 100);
			obj.put("oughtfeed", Double.parseDouble(le.substring(49, 58)) / 100);
			obj.put("oughtfee", Double.parseDouble(le.substring(59, 68)) / 100);
			arr.put(obj);
		}
		obj1.put("arr", arr);
		System.out.println(arr);
		return obj1;
	}

	/**
	 * 获得预下单编号，用于前台调取支付界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GET
	@Path("getprepayid")
	public JSONObject getprepayid(@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			System.out.println("weixin-getprepayid");
			String id = request.getSession().getId();
			System.out.println("sessionid=" + id);
			String openid = request.getParameter("openid");
			System.out.println("openid=" + openid);
			String money = request.getParameter("money");
			System.out.println("money=" + money);
			WxPaySendData data = new WxPaySendData();
			data.setAppid(Configure.getAppid());
			data.setAttach("微信支付");
			data.setBody("微信公众号支付");
			data.setMch_id(Configure.getMchid());
			data.setNonce_str(WxSign.getNonceStr());
			data.setNotify_url(Configure.NOTIFY_URL);
			data.setOut_trade_no(WxSign.getNonceStr());
			data.setTotal_fee((int) (Double.parseDouble(money) * 100));// 单位：分
			data.setTrade_type("JSAPI");
			data.setSpbill_create_ip(request.getRemoteAddr());
			data.setOpenid(openid);
			// 统一下单
			String returnXml = unifiedOrder(data, Configure.getKey());
			WxPayReturnData reData = new WxPayReturnData();
			XStream xs1 = new XStream(new DomDriver());
			xs1.alias("xml", WxPayReturnData.class);
			reData = (WxPayReturnData) xs1.fromXML(returnXml);
			// 判断返回数据
			String return_code = reData.getReturn_code();
			if (return_code.equals("SUCCESS")) {
				if (reData.getResult_code().equals("SUCCESS")) {

				} else {
					result.put(
							"error",
							"返回错误：" + reData.getErr_code()
									+ reData.getErr_code_des());
				}
			} else {
				result.put("error", "系统错误：" + reData.getReturn_msg());
			}
			// 跳转支付页面
			SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
			String appid2 = Configure.getAppid();
			String timestamp = WxSign.getTimeStamp();
			String nonceStr2 = WxSign.getNonceStr();
			String prepay_id2 = "prepay_id=" + reData.getPrepay_id();
			String packages = prepay_id2;
			finalpackage.put("appId", appid2);
			finalpackage.put("timeStamp", timestamp);
			finalpackage.put("nonceStr", nonceStr2);
			finalpackage.put("package", packages);
			finalpackage.put("signType", "MD5");
			String finalsign = WxSign.createSign(finalpackage,
					Configure.getKey());
			// String redirect_url = "/weixin.html?appId=" + appid2
			// + "&timeStamp=" + timestamp + "&nonceStr=" + nonceStr2
			// + "&pg=" + reData.getPrepay_id() + "&sign=" + finalsign
			// + "&signType=MD5";
			// System.out.println(redirect_url);
			// response.sendRedirect(redirect_url);
			result.put("appId", appid2);
			result.put("timeStamp", timestamp);
			result.put("nonceStr", nonceStr2);
			result.put("pg", reData.getPrepay_id());
			result.put("sign", finalsign);
			result.put("signType", "MD5");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/**
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static String unifiedOrder(WxPaySendData data, String key) {
		// 统一下单支付
		String returnXml = null;
		try {
			// 生成sign签名
			SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
			parameters.put("appid", data.getAppid());
			parameters.put("attach", data.getAttach());
			parameters.put("body", data.getBody());
			parameters.put("mch_id", data.getMch_id());
			parameters.put("nonce_str", data.getNonce_str());
			parameters.put("notify_url", data.getNotify_url());
			parameters.put("out_trade_no", data.getOut_trade_no());
			parameters.put("total_fee", (Integer) data.getTotal_fee());
			parameters.put("trade_type", data.getTrade_type());
			parameters.put("spbill_create_ip", data.getSpbill_create_ip());
			parameters.put("openid", data.getOpenid());
			parameters.put("device_info", data.getDevice_info());
			data.setSign(WxSign.createSign(parameters, key));
			XStream xs = new XStream(new DomDriver("UTF-8",
					new XmlFriendlyNameCoder("-_", "_")));
			xs.alias("xml", WxPaySendData.class);
			String xml = xs.toXML(data);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(Configure.DOWN_PAY_API);
			StringEntity postEntity = new StringEntity(xml, "UTF-8");
			postRequest.addHeader("Content-Type", "text/xml");
			postRequest.setEntity(postEntity);
			HttpResponse httpResponse = httpclient.execute(postRequest);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				returnXml = EntityUtils.toString(entity, "UTF-8");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnXml;
	}

	/**
	 * 支付完成回调地址
	 * 
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("notify")
	public String notify(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException {
		System.out.println("微信支付weixin-notify");
		log.debug("微信支付weixin-notify");
		JSONObject ob = xml(request, response);
		// String redirect_url = "/success.jsp?openid=" + openid
		// + "&showwxpaytitle=1";
		// response.sendRedirect(redirect_url);
		return "";
	}

	public boolean GetWeChatPayResult(String OrderFormID) {
		// 通过OrderFormID查询微信结果表
		return false;
	}

	public synchronized JSONObject xml(@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		try {
			// 下账
			request.setCharacterEncoding("UTF-8");
			// // response.setCharacterEncoding("UTF-8");
			PrintWriter out;
			// 读取接收到的xml消息
			StringBuffer sb = new StringBuffer();
			InputStream is = request.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			String xml = sb.toString();
			System.out.println(xml);
			log.debug(xml);
			WxNofityReturnData reData = new WxNofityReturnData();
			XStream xs1 = new XStream(new DomDriver());
			xs1.alias("xml", WxNofityReturnData.class);
			reData = (WxNofityReturnData) xs1.fromXML(xml);
			System.out.println(reData);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("f_return_code", reData.getResult_code());
			map.put("f_return_msg", reData.getReturn_msg());
			map.put("f_appid", reData.getAppid());
			map.put("f_mch_id", reData.getMch_id());
			map.put("f_device_info", reData.getDevice_info());
			map.put("f_nonce_str", reData.getNonce_str());
			map.put("f_sign", reData.getSign());
			map.put("f_result_code", reData.getResult_code());
			map.put("f_err_code", reData.getErr_code());
			map.put("f_err_code_des", reData.getErr_code_des());
			map.put("f_openid", reData.getOpenid());
			map.put("f_is_subscribe", reData.getIs_subscribe());
			map.put("f_trade_type", reData.getTrade_type());
			map.put("f_bank_type", reData.getBank_type());
			map.put("f_total_fee", reData.getTotal_fee());
			map.put("f_fee_type", reData.getFee_type());
			map.put("f_cash_fee", reData.getCash_fee());
			map.put("f_cash_fee_type", reData.getCash_fee_type());
			map.put("f_coupon_fee", reData.getCoupon_fee());
			map.put("f_coupon_count", reData.getCoupon_count());
			map.put("f_coupon_fee_$n", reData.getCoupon_fee_$n());
			map.put("f_coupon_id_$n", reData.getCoupon_id_$n());
			map.put("f_transaction_id", reData.getTransaction_id());
			map.put("f_out_trade_no", reData.getOut_trade_no());
			map.put("f_attach", reData.getAttach());
			//对账用
			map.put("f_message", "未对账");
			map.put("f_time_end", reData.getTime_end());
			Map<String, Object> r = selList(reData.getOpenid());
			//为空   查解绑表20170324
			if(r==null){
				Map<String, Object> ru = selunbundlingList(reData.getOpenid(),reData.getTransaction_id());
				map.put("f_userid", ru.get("f_userid").toString());
			}else{
			map.put("f_userid", r.get("f_userid").toString());
			}

			Map<String, Object> row = selweixin(reData.getTransaction_id());
			if (row == null) {
				// 将微信返回的信息存储到t_weixinreturnxml中
				hibernateTemplate.saveOrUpdate("t_weixinreturnxml", map);

				String f_openid = reData.getOpenid();
				Map<String, Object> row1 = selList(f_openid);
				String f_total_fee = reData.getTotal_fee() + "";
				System.out.println(row1.get("f_userid").toString());
				JSONObject object = wxpay(row1.get("f_userid").toString(),
						f_total_fee, reData.getTransaction_id(),
						reData.getAttach(), reData.getTime_end());
				// byte[] b = object.getString("return").getBytes();
				// String len = new String(b, 0, 108);
				// String code = new String(b, 22, 2);
				// double zhye = 0;
				// if (code.equals("00")) {
				// zhye = Double.parseDouble(len.substring(61, 70)) / 100;
				//
				// } else {
				// }
				// JSONObject ob = new JSONObject();
				// ob.put("zhye", zhye);
				// ob.put("openid", reData.getOpenid());
				// return ob;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("", e);
		}

		// String result = "";
		// out = response.getWriter();
		// result = new WechatProcess().processWechatMag(xml);
		// System.out.println(result);
		// out.print(result);
		// out.close();
		// out = null;
		return null;
	}

	/**
	 * 银行代码
	 */
	private String yhno = "weixin";
	/**
	 * 机构号
	 */
	private String jgno = "weixin";
	/**
	 * 银行方终端设备代码或营业厅编码
	 */
	private String sbno = "weixin";

	/**
	 * 根据用户编号生成查询报文
	 * 
	 * @param userid
	 * @return
	 */
	private String get1001(String userid) {
		String result = "";
		result = "000000731001";
		userid = StringUtil.joint(userid, 10, " ");
		System.out.println(userid);
		// 银行交易流水号：YYYYMMDD+20位流水号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String pipeline = sdf.format(new Date()) + StringUtil.grandom(14);
		result += userid + StringUtil.joint(yhno, 10, " ") + pipeline + "0"
				+ StringUtil.joint(jgno, 10, " ")
				+ StringUtil.joint(sbno, 10, " ");
		return result;
	}

	/**
	 * 发送交费报文
	 * 
	 * @param userid
	 *            用户编号
	 * @param money
	 *            交费金额
	 * @return
	 */
	private String get1002(String userid, String money, String transation_id,
			String attach) {
		String result = "";
		result = "000000831002";
		userid = StringUtil.joint(userid, 10, " ");
		// 银行交易流水号：YYYYMMDD+12位流水号
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		// String pipeline = sdf.format(new Date()) + StringUtil.grandom(6);
		// byte[] byBuffer = new byte[200];
		// byBuffer = attach.getBytes();
		result += userid + StringUtil.joint(yhno, 10, " ") + transation_id
				+ "0" + StringUtil.joint(jgno, 10, " ")
				+ StringUtil.joint(sbno, 10, " ");
		// BigDecimal j = new BigDecimal(money);
		// j = j.multiply(new BigDecimal(100));
		money = StringUtil.jointleft(money, 10, "0");
		result += money;
		System.out.println(result);
		return result;
	}

	public JSONObject wxquery(String userid) {
		JSONObject result = new JSONObject();
		TcpService tcp = new TcpService();
		result = tcp.send(get1001(userid));
		return result;
	}

	public JSONObject wxpay(String userid, String money, String transation_id,
			String attach, String time_end) {
		// 以前没有，自己添加 (判断是卡表用户还是基表用户)
		String meterstyle = getmeterstyle(userid);
		if (meterstyle.equals("卡表")) {
			ICChongZhi ic = new ICChongZhi();
			ic.saveandmodify(userid, money, transation_id, attach, time_end);
			log.debug("卡表用户缴费");
		} else if (meterstyle == "null") {

		} else {
			JSONObject result = new JSONObject();
			TcpService tcp = new TcpService();
			result = tcp.send(get1002(userid, money, transation_id, attach));
			System.out.println(result);
			return result;
		}
		return null;
	}

	@Autowired
	private HibernateTemplate hibernateTemplate;

//	@Autowired
//	private ICChongZhi ic;

	/**
	 * List execute sql in hibernate 查询用户是否绑定，绑定后就查询到的数据返回，未绑定返回null
	 * 
	 * @param sql
	 */
	private Map<String, Object> selList(String openid) {

		String sql = "from t_userfiles   where f_openid='" + openid + "'";
		List list = this.hibernateTemplate.find(sql);
		log.debug("查询用户是否绑定" + sql);
		int x = list.size();
		System.out.println(x);
		if (list.size() != 1)
			return null;
		else
			return (Map<String, Object>) list.get(0);
	}
	
	/**
	 * 查解绑表20170324
 
	 * @param openid
	 * @return
	 */
	private Map<String, Object> selunbundlingList(String openid,String f_transaction_id) {

		String sql = "from t_weixinunbundling  where f_openid='"+openid+"' and f_transaction_id="+f_transaction_id+"'";
		List list = this.hibernateTemplate.find(sql);
		log.debug("查询用户id" + sql);
		int x = list.size();
		System.out.println(x);
		if (list.size() != 1)
			return null;
		else
			return (Map<String, Object>) list.get(0);
	}



	// 查询微信交易码
	/**
	 * 根据微信交易码，查询t_weixinreturnxml表，查询到就将查询的数据返回，没有则返回null
	 * 
	 * @param f_transaction_id
	 * @return
	 */
	private Map<String, Object> selweixin(String f_transaction_id) {

		String sql = "from t_weixinreturnxml   where f_transaction_id='"
				+ f_transaction_id + "'";
		List listwx = this.hibernateTemplate.find(sql);
		log.debug("查询微信交易码" + sql);
		int x = listwx.size();

		if (listwx.size() != 1)
			return null;
		else
			return (Map<String, Object>) listwx.get(0);
	}

	// 查询用户
	@GET
	@Path("/one/{f_userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject selList1(@PathParam("f_userid") String f_userid) {
		try {

			String sql = "from t_userfiles   where f_userid='" + f_userid + "'";

			List list = this.hibernateTemplate.find(sql);
			log.debug("查询用户基本信息" + sql);
			Map<String, Object> map = (Map<String, Object>) list.get(0);
			JSONObject jo = new JSONObject();

			if (list.size() == 0) {
				jo.put("message", "请检查您输入的用户编号是否正确");
				return jo;
			} else {

				String f_username = map.get("f_username").toString();
				String f_address = map.get("f_address").toString();
				String f_openid = (String) map.get("f_openid");
				String f_gasmeterstyle = (String) map.get("f_gasmeterstyle");
				String f_cardid = (String) map.get("f_cardid");
				double f_zhye = (Double) map.get("f_zhye");
				if (f_openid == null) {
					f_openid = "";
				}
				// 自己添加类型
				jo.put("f_cardid", f_cardid);
				jo.put("f_gasmeterstyle", f_gasmeterstyle);
				jo.put("f_username", f_username);
				jo.put("f_address", f_address);
				jo.put("f_openid", f_openid);
				jo.put("f_zhye", f_zhye);
				return jo;
			}
		} catch (Exception e) {
			return null;
		}
	}

	// 绑定
	@GET
	@Path("/one/{f_userid}/{openid}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getSerialNumber(@PathParam("f_userid") String f_userid,
			@PathParam("openid") String openid) throws JSONException,
			IOException {
		JSONObject obj = selList1(f_userid);
		String f_openid = obj.getString("f_openid");
		String sqluser = "from t_userfiles   where f_openid='" + openid + "'";
		List list = this.hibernateTemplate.find(sqluser);
		log.debug(list.size());
		if (f_openid.equals("") && list.size() == 0) {
			String sql = "update t_userfiles set f_openid='" + openid
					+ "'  where " + " f_userid='" + f_userid + "'";
			int length = this.hibernateTemplate.bulkUpdate(sql);
			log.debug("用户绑定" + sql);
			if (length == 0) {
				return null;
			} else {
				JSONObject ob = selectqf(f_userid);
				ob.put("message", "绑定成功");
				log.debug(ob);
				System.out.println(ob);
				return ob;
			}
		} else {
			if (!f_openid.equals("")) {
				JSONObject object = new JSONObject();
				object.put("message", "此号已经被别的用户绑定");
				System.out.println(object);
				log.debug(object);
				return object;
			}
			if (list.size() > 1) {
				JSONObject object = new JSONObject();
				object.put("message", "要绑定新的用户编号，请先对已绑定的用户编号解除绑定");
				System.out.println(object);
				log.debug(object);
				return object;
			} else {
				if (list.size() == 1) {
					Map<String, Object> map = (Map<String, Object>) list.get(0);
					String userid = map.get("f_userid").toString();
					if (userid.equals(f_userid)) {
						JSONObject obj1 = selectqf(f_userid);
						obj1.put("message", "您已绑定");
						log.debug(obj1);
						// System.out.println(obj1);
						return obj1;
					} else {
						JSONObject object = new JSONObject();
						object.put("message", "要绑定新的用户编号，请先对已绑定的用户编号解除绑定");
						// System.out.println(object);
						log.debug(object);
						return object;
					}

				}
			}
		}
		return null;
	}

	// 解绑
	@GET
	@Path("/one/delete/{f_userid}/{openid}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject d(@PathParam("f_userid") String f_userid,
			@PathParam("openid") String openid) throws JSONException,
			IOException {
		JSONObject obj = selList1(f_userid);
		String f_openid = obj.getString("f_openid");
		JSONObject object = new JSONObject();
		if (!f_openid.equals(openid)) {
			object.put("message", "请您检查输入编号是否正确,或您尚未绑定");

		} else {
			//sava解绑表
			String f_transaction_id="";
			Map<String, Object> row = selweixinsn(f_openid);

			if(row == null){
				f_transaction_id="0";
			}else{
				f_transaction_id=row.get("f_transaction_id").toString();
			}
																								 
										
			try {
				insertunbundling(f_userid,openid,f_transaction_id);
			} catch (ParseException e) {
				log.debug("存解绑出错"+e);
				e.printStackTrace();
	 
			}
			String sql = "update t_userfiles set f_openid=NULL  where "
					+ " f_userid='" + f_userid + "'";

			int length = this.hibernateTemplate.bulkUpdate(sql);
			log.debug("用户解绑" + sql+"解绑openid:"+openid+"解绑用户编号:"+f_userid);
			object.put("message", "解绑成功");

		}
		return object;
	}

	/**
	 *  查询微信交易码
	 * @param f_transaction_id
	 * @return
	 */
		private Map<String, Object> selweixinsn(String f_openid) {

			String sql = "from t_weixinreturnxml   where f_openid='"+f_openid+"'";
											   
			List listwx = this.hibernateTemplate.find(sql);
			log.debug("查询微信交易码" + sql);
			int x = listwx.size();

			if (listwx.size() != 1)
				return null;
			else
				return (Map<String, Object>) listwx.get(0);
		}
		/**
		 *  存解绑20170324
		 * @param f_userid
		 * @param openid
		 * @param date
		 * @throws ParseException 
		 */
		public void insertunbundling(String f_userid,String openid,String tid) throws ParseException{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("f_userid", f_userid);
			map.put("f_openid", openid);
																									 
			map.put("f_transaction_id", tid);
																								  
												
			hibernateTemplate.saveOrUpdate("t_weixinunbundling", map);
		}
	/**
	 * 获取对应用户的用气类型，以前没有，增加卡表用户时增加.查询用户的类型，用来判断是不是卡表
	 * 
	 * @param userid
	 * @return 返回基表、卡表、null
	 */
	public String getmeterstyle(String userid) {
		String sql = "from t_userfiles where f_userid= '" + userid + "'";
		List list = this.hibernateTemplate.find(sql);
		if (list.size() == 0) {
			return null;
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		String style = map.get("f_gasmeterstyle").toString();
		return style;
	}

	@GET
	@Path("sign")
	public String initSign(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException,
			UnsupportedEncodingException {
		String url = request.getParameter("url").replaceAll("\\$", "&");
		// url = java.net.URLEncoder.encode(url, "UTF-8");
		JSONObject json = new JSONObject();
		String appid = Configure.getAppid();
		String appsecret = Configure.getSecret();
		// TODO 暂时写死
		// String appid = "wxb6ee18127275ee6e";
		// String appsecret = "253fca9d1b3120a8a776b5168e653cf0";
		String noncestr = WxSign.getNonceStr();
		String timestamp = WxSign.getTimeStamp();
		System.out.println("appid-->" + appid);
		System.out.println("appsecret-->" + appsecret);
		System.out.println("noncestr-->" + noncestr);
		System.out.println("timestamp-->" + timestamp);
		System.out.println("url-->" + url);
		String signature = WxCertificate.getSign(appid, appsecret, noncestr,
				timestamp, url);
		json.put("appid", appid);
		json.put("noncestr", noncestr);
		json.put("timestamp", timestamp);
		json.put("signature", signature);
		System.out.println(json.toString());
		return json.toString();
	}

	@GET
	@Path("bind")
	public JSONObject bind(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) throws JSONException {
		System.out.println("bind开始");
		String data = req.getParameter("data");
		System.out.println(data);
		String device = req.getParameter("device");
		String marke = req.getParameter("marke");
		String openid = req.getParameter("openid");
		String ticket = req.getParameter("ticket");
		String token = WxCertificate.getToken(Configure.getAppid(),
				Configure.getSecret());
		System.out.println("token--->" + token);
		System.out.println("device-->" + device + "marke-->" + marke
				+ "openid-->" + openid + "ticket-->" + ticket);
		String url = "";
		if (marke.equals("1")) {// 绑定
			System.out.println("绑定" + marke);
			url = "https://api.weixin.qq.com/device/bind?access_token=" + token;

		} else { // 解绑
			System.out.println("解绑" + marke);
			url = "https://api.weixin.qq.com/device/unbind?access_token="
					+ token;
		}
		String result = WxCertificate.doPost(url, ticket, device, openid);
		
		return new JSONObject(result);
	}
	/**
	 * 
	 * @param f_cardid 卡号
	 * @param f_userid 用户编号
	 * @param writegas 写卡气量
	 * @param money 写卡金额
	 * @param ljmoney 表累计购气量（真兰表）
	 * @param times 写卡次数
	 * @param factory 卡厂家
	 * @return "success" 表示成功，返回其余为错误信息
	 * @throws Exception 存入表的数据转换异常时抛出
	 */
	@SuppressWarnings("rawtypes")
	public String writeIc(String f_cardid, String f_userid, double writegas, double money, double ljmoney, int times, String factory) throws Exception {
		// 写卡存入修改账户余额
		String sql = "from t_userfiles where f_userid = '" + f_userid + "'";
		System.out.println("查询账户余额：" + sql);
		log.debug("查询账户余额：" + sql);
		List list = this.hibernateTemplate.find(sql);
		if (list.size() == 0) {
			System.out.println("没有查询到相关用户");
			log.debug("没有查询到相关用户");
			return "没有查询到相关用户";
		} else if (list.size() > 1) {
			System.out.println("卡号不唯一");
			log.debug("卡号不唯一");
			return "";
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		// 取出账户余额
		double zhye = (Double) map.get("f_zhye");
		System.out.println("当前账户余额为：" + zhye);
		log.debug("当前账户余额为：" + zhye);
		
		BigDecimal xieka = BigDecimal.valueOf(money);
		BigDecimal jfzhye = BigDecimal.valueOf(zhye);
		double newzhye = Double.valueOf(jfzhye.subtract(xieka).toString());
		System.out.println("充值后的账户余额为：" + newzhye);
		log.debug("充值后的账户余额为：" + newzhye);
		
		double f_metergasnums = (Double) map.get("f_metergasnums") + writegas;
		double f_cumulativepurchase = (Double) map.get("f_cumulativepurchase") + writegas;
		System.out.println("充值后的账户余额为：" + newzhye);
		log.debug("充值后的账户余额为：" + newzhye);

		String writeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Map<String, Object> mapWrite = new HashMap<String, Object>();
		mapWrite.put("f_userid", map.get("f_userid"));
		mapWrite.put("f_username", map.get("f_username"));
		mapWrite.put("f_address", map.get("f_address"));
		mapWrite.put("f_iccard", f_cardid);
		mapWrite.put("f_writefee", xieka.doubleValue());
		mapWrite.put("f_writetype", "微信蓝牙");
		mapWrite.put("f_writemark", "未写卡"); // 写卡标记，写卡完成后修改
		mapWrite.put("f_beforwriteye", (Double) map.get("f_zhye"));
		mapWrite.put("f_afterwriteye", newzhye);
		mapWrite.put("f_writegas", writegas);
		mapWrite.put("f_beforwritegas", (Double) map.get("f_metergasnums")); // 当前表累计购气量
		mapWrite.put("f_afterwritegas", f_metergasnums); // 写卡后的累计购汽量
		mapWrite.put("f_beforljjine", ljmoney); // 写卡后的累计购汽量
		mapWrite.put("f_afertljjine", money + ljmoney); // 写卡后的累计购汽量
		mapWrite.put("f_writedate",writeDate);
		mapWrite.put("f_factory",factory);
		
		mapWrite.put("f_yhxz", map.get("f_yhxz"));//用户性质
		mapWrite.put("f_districtname", map.get("f_districtname")); //小区名称
		mapWrite.put("f_gaspricetype", map.get("f_gaspricetype"));//气价类型
		mapWrite.put("f_gasprice", map.get("f_gasprice"));//气价
		mapWrite.put("f_usertype", map.get("f_usertype"));//用户类型f_cumulativemoney
		mapWrite.put("f_gasproperties", map.get("f_gasproperties"));//用户类型
		mapWrite.put("f_cumulativemoney", map.get("f_cumulativemoney"));//用户类型
		txUpdate(money, newzhye, (String)map.get("f_userid"), writegas, f_metergasnums, f_cumulativepurchase, mapWrite,times);

		return "success";
	}
	/**
	 * 写卡成功回调的函数，将写卡记录中的未写卡改为已写卡，修改写卡密码
	 * @param req
	 * @param resp
	 * @return 返回大于0时，成功，否则失败
	 */
	@GET
	@Path("success")
	public String writeIcSuccess(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) {
		// 写卡成功，修改写卡记录中标识符
		String f_iccard = req.getParameter("cardid");
		String writeDate = req.getParameter("writeDate");
		String sql = "update t_writeiccard set f_writemark = '已写卡' where f_iccard = '"
				+ f_iccard + "' and f_writedate = '" + writeDate + "'";
		this.hibernateTemplate.bulkUpdate(sql);
		System.out.println("修改写卡标示为已写卡：" + sql);
		log.debug("修改写卡标示为已写卡：" + sql);
		
		String kmm = req.getParameter("kmm");
		String f_userid = req.getParameter("userid");
//		String f_userid = "11006875";
		String sql2 = "update t_userfiles set kmm = '"+ kmm +"' where f_userid = '"
				+ f_userid + "'";
		this.hibernateTemplate.bulkUpdate(sql2);
		log.debug("修改写卡标示为已写卡：" + sql);
		return "success";
	}

	/**
	 * 增加一条写卡记录，（修改卡密码放在写卡成功后），修改账户余额(f_zhye)，修改累计购气量(f_metergasnums)
	 * ,总累计购气量(f_metergasnums),最后购气量（f_finallybought），最后购气日期（f_finabuygasdate），
	 * 最后购汽时间（f_finabuygastime）
	 * @param jine 写卡金额
	 * @param newzhye 最新余额
	 * @param f_userid 用户编号
	 * @param writegas 写卡气量
	 * @param f_metergasnums 当前表的累计购气量
	 * @param f_cumulativepurchase 总累计购气量
	 * @param map 要增加的写卡记录数据集合
	 * @param times 写卡次数
	 * @throws Exception 数据类型转换异常
	 */
	public void txUpdate(double jine, double newzhye,
			String f_userid,double writegas, double f_metergasnums, 
			double f_cumulativepurchase, final Map map, int times) throws Exception {
		
		String date = WxCertificate.getDate("yyyy-MM-dd HH:mm:ss");
		String[] dates = date.split(" ");
		
		// 修改用户档案中的数据f_times
		final String sqla = "update t_userfiles set f_zhye = " + newzhye
				+ ", f_finallybought = " + writegas
				+ ", f_metergasnums = " + f_metergasnums
				+ ", f_cumulativepurchase = " + f_cumulativepurchase
				+ ", f_finabuygasdate = '" + dates[0]
				+ "', f_finabuygastime = '" + dates[1]
				+ "', f_times = '" + times
				+ "' where f_userid = '" + f_userid + "'";
		System.out.println("修改用户档案表的sql为" + sqla);
		
		// 增加一条收费记录
		final Map<String, Object> savemap = new HashMap<String, Object>();
		savemap.put("f_userid", map.get("f_userid")); // 用户编号
		savemap.put("f_username", map.get("f_username")); // 用户姓名
		savemap.put("f_address", map.get("f_address")); // 用户地址
		savemap.put("f_districtname", map.get("f_districtname")); //小区名称
		savemap.put("f_gaspricetype", map.get("f_gaspricetype"));//气价类型
		savemap.put("f_gasprice", map.get("f_gasprice"));//气价
		savemap.put("f_usertype", map.get("f_usertype"));//用户类型
		savemap.put("f_gasproperties", map.get("f_gasproperties"));//用户类型
		savemap.put("f_payment", "账户余额");//付款方式
		savemap.put("f_filiale", "微信蓝牙");//分公司
		savemap.put("f_comtype", "微信蓝牙");//公司类型，分为天然气公司、银行
		savemap.put("f_gasmeterstyle", "卡表");//
		savemap.put("f_zhye", newzhye);//
		savemap.put("f_banksn", ""); // 微信支付订单编号
		savemap.put("f_yhxz", map.get("f_yhxz"));//用户性质
		savemap.put("f_grossproceeds", 0.00); // 收款金额
		savemap.put("f_sgnetwork", "微信蓝牙"); //网点
		savemap.put("f_sgoperator", "微信蓝牙"); //操作员
		savemap.put("f_payfeetype", "余额扣除");//收费类型
		savemap.put("f_jiezhangstate", "已结账"); // 结账状态
		savemap.put("f_deliverytime", WxCertificate.getDate("yyyy-MM-dd", dates[0])); //缴费时间
		savemap.put("f_deliverydate", WxCertificate.getDate("yyyy-MM-dd", dates[0])); // 缴费日期
		
		savemap.put("f_payfeevalid", "有效"); // 有效无效
		savemap.put("f_preamount", jine); // 应交金额
		savemap.put("f_pregas", writegas); // 预购气量
		savemap.put("f_allamont", f_metergasnums); // 
		savemap.put("f_stairtype", ""); // 
		savemap.put("f_benqizhye", 0.00); //
		savemap.put("f_zhinajin", 0.00); //
		
		log.debug("修改用户档案表的sql为" + sqla);
		log.debug("增加充值记录数据为：" + map.toString());
		// 配置文件中的事务不起作用，所以使用这种方法进行事务操作
		this.hibernateTemplate.execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				session.beginTransaction();
				session.createQuery(sqla).executeUpdate();
				session.save("t_writeiccard", map);
				session.save("t_sellinggas", savemap);
				session.getTransaction().commit();
				return null;
			}
		});
		// 修改用户档案中的数据f_times
//		String sqla = "update t_userfiles set f_zhye = " + newzhye
//				+ ", f_finallybought = " + writegas
//				+ ", f_metergasnums = " + f_metergasnums
//				+ ", f_cumulativepurchase = " + f_cumulativepurchase
//				+ ", f_finabuygasdate = '" + dates[0]
//				+ "', f_finabuygastime = '" + dates[1]
//				+ "', f_times = '" + times
//				+ "' where f_userid = '" + f_userid + "'";
//		System.out.println("修改用户档案表的sql为" + sqla);
//		this.hibernateTemplate.bulkUpdate(sqla);
//		// 保存一条写卡记录记录
//		log.debug("修改用户档案表的sql为" + sqla);
//		System.out.println("增加充值记录数据为：" + map.toString());
//		this.hibernateTemplate.save("t_writeiccard", map);
//		log.debug("增加充值记录数据为：" + map.toString());
//		// 增加一条类似自动下账记录，售气记录实收为0（所有的收费金额都为零）
//		// 增加一条收费记录
//		Map<String, Object> savemap = new HashMap<String, Object>();
//		savemap.put("f_userid", map.get("f_userid")); // 用户编号
//		savemap.put("f_username", map.get("f_username")); // 用户姓名
//		savemap.put("f_address", map.get("f_address")); // 用户地址
//		savemap.put("f_districtname", map.get("f_districtname")); //小区名称
//		savemap.put("f_gaspricetype", map.get("f_gaspricetype"));//气价类型
//		savemap.put("f_gasprice", map.get("f_gasprice"));//气价
//		savemap.put("f_usertype", map.get("f_usertype"));//用户类型
//		savemap.put("f_gasproperties", map.get("f_gasproperties"));//用户类型
//		savemap.put("f_payment", "账户余额");//付款方式
//		savemap.put("f_filiale", "微信蓝牙");//分公司
//		savemap.put("f_comtype", "微信蓝牙");//公司类型，分为天然气公司、银行
//		savemap.put("f_gasmeterstyle", "卡表");//
//		savemap.put("f_zhye", newzhye);//
//		savemap.put("f_banksn", ""); // 微信支付订单编号
//		savemap.put("f_yhxz", map.get("f_yhxz"));//用户性质
//		savemap.put("f_grossproceeds", 0.00); // 收款金额
//		savemap.put("f_sgnetwork", "微信蓝牙"); //网点
//		savemap.put("f_sgoperator", "微信蓝牙"); //操作员
//		savemap.put("f_payfeetype", "余额扣除");//收费类型
//		savemap.put("f_jiezhangstate", "已结账"); // 结账状态
//		savemap.put("f_deliverytime", WxCertificate.getDate("yyyy-MM-dd", dates[0])); //缴费时间
//		savemap.put("f_deliverydate", WxCertificate.getDate("yyyy-MM-dd", dates[0])); // 缴费日期
//		
//		savemap.put("f_payfeevalid", "有效"); // 有效无效
//		savemap.put("f_preamount", jine); // 应交金额
//		savemap.put("f_pregas", writegas); // 预购气量
//		savemap.put("f_allamont", f_metergasnums); // 
//		savemap.put("f_stairtype", ""); // 
//		savemap.put("f_benqizhye", 0.00); //
//		savemap.put("f_zhinajin", 0.00); //
//		// 增加售气记录
////		throw new RuntimeException();
//		this.hibernateTemplate.save("t_sellinggas", savemap);
//		
	}

	/**
	 * 查询写卡失败记录
	 * 
	 * @param userid
	 * @return
	 */
	@GET
	@Path("WriteFail")
	public String getWriteFail(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) {
		System.out.println("写卡失败记录查询开始userid--->" + req.getParameter("userid"));
		String sql = "from t_writeiccard where f_writemark='未写卡' and f_userid='"
				+ req.getParameter("userid") + "'";
		log.debug("查询写卡失败记录为：" + sql);
		List list = this.hibernateTemplate.find(sql);
		JSONArray arr = new JSONArray();
		for (Object o : list) {
			JSONObject jo = new JSONObject();
			StringBuffer buffer = new StringBuffer(o.toString());
			buffer.deleteCharAt(0);
			buffer.deleteCharAt(buffer.length() - 1);
			String[] str = buffer.toString().split(",");
			for (String s : str) {
				String[] ts = s.split("=");
				try {
					jo.put(ts[0], ts[1]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			arr.put(jo);
		}
		System.out.println(arr.toString());

		return arr.toString().replaceAll(" ", "");
	}


	/**
	 * 根据用户编号查询用户的当月累计购气量
	 * 
	 * @param userid
	 * @return 当月累计购气量
	 */
	public double getPregas(String userid) {
//		final String sql = "select f_userid,SUM(f_pregas) f_pregas from"
//				+ "(select f_userid,SUM(f_pregas) f_pregas from "
//				+ "t_sellinggas where "
//				+ "f_deliverydate>=SUBSTRING(CONVERT(varchar(100),GETDATE(),23),0,9)+'01 00:00:00' "
//				+ "and f_deliverydate<=dateadd(ms,-3,DATEADD(mm,DATEDIFF(m,0,getdate())+1,0)) "
//				+ "and f_payfeevalid='有效' "
//				+ "and f_userid='"+userid+"' group by f_userid union all select "
//				+ "f_userid,SUM(f_writegas) f_pregas  "
//				+ "from t_writeiccard where "
//				+ "f_writedate>=SUBSTRING(CONVERT(varchar(100),GETDATE(),23),0,9)+'01 00:00:00' "
//				+ "and f_writedate<=dateadd(ms,-3,DATEADD(mm,DATEDIFF(m,0,getdate())+1,0)) "
//				+ "and f_userid='"+userid+"'"
//				+ " group by f_userid) t group by f_userid";
		
		
		final String sql = "select f_userid,f_username,SUM(f_pregas) f_pregas from t_sellinggas"
				+ " where f_deliverydate>=SUBSTRING(CONVERT(varchar(100),GETDATE(),23),0,9)+'01 00:00:00'"
				+ " and f_deliverydate<=dateadd(ms,-3,DATEADD(mm,DATEDIFF(m,0,getdate())+1,0))"
				+ " and f_payfeevalid='有效'"
				+ " and f_userid='"
				+ userid
				+ "'"
				+ " group by f_userid,f_username";
		System.out.println("获取用户"+userid+"的本月累计购气量sql：" + sql);
		log.debug("获取用户"+userid+"的本月累计购气量sql：" + sql);
		List list = this.hibernateTemplate.executeFind(new HibernateCallback() {
				
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				return session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			}
		});
		//System.out.println(query.toString());
		System.out.println(list.toString());
		if(list.size() == 0) {
			return 0;
		}
		System.out.println(list.get(0).toString());
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		//sf.close();
		// 取出账户余额
		double f_pregas = (Double) map.get("f_pregas");
		System.out.println("本月累计购汽" + f_pregas);
		log.debug("编号为:"+userid+"的用户本月累计购汽:" + f_pregas);
		return f_pregas;
	}
	@GET
	@Path("datas")
	public String getDatas(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) {
		String datas = req.getParameter("cardinfo");
		System.out.println(datas);
		String data = WxCertificate.doPost(datas);
		System.out.println(data);
		return data;
	}
	@GET
	@Path("writedata")
	public String getWrite(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) throws Exception{
		// 读到的卡数据
		String datas = req.getParameter("cardinfo");
		// 表编号
//		String f_userid = req.getParameter("f_userid");
		String f_userid = "11006875";
		// 购气量的卡数据
		String gas = req.getParameter("gas");
		String factory = req.getParameter("factory");
		String cardid = req.getParameter("cardid");
		String jine = req.getParameter("jine");
		String times = req.getParameter("times");
		// 真蓝表真实的显示气量，输入的金额只是用来计算总金额的
		// 创源固定传0
		String ljine = req.getParameter("ljine");
		System.out.println("datas-->"+datas);
		System.out.println("f_userid-->"+f_userid);
		System.out.println("gas-->"+gas);
		System.out.println("jine-->"+jine);
		System.out.println("factory-->"+factory);
		System.out.println("cardid-->"+cardid);
		System.out.println("times-->"+times);
		System.out.println("ljine-->"+ljine);
		log.debug("读卡数据："+datas);
		log.debug("用户编号："+f_userid);
		log.debug("写卡气量："+gas);
		log.debug("写卡金额："+jine);
		log.debug("厂家："+factory);
		log.debug("卡号："+cardid);
		log.debug("写卡次数："+times);
		log.debug("真兰使用，当前表累计购汽金额："+ljine);
		
		String sql = "from t_userfiles where f_userid = '" + f_userid + "'";
		System.out.println("调取卡云服务前查询用户信息"+ sql);
		List list = this.hibernateTemplate.find(sql);
		System.out.println("执行hibernate size=" + list.size());
		if (list.size() == 0) {
			System.out.println("没有查询到相关用户");
			log.debug("没有查询到相关用户");
			return "没有查询到相关用户";
		} else if (list.size() > 1) {
			System.out.println("卡号不唯一");
			log.debug("卡号不唯一");
			return "卡号不唯一";
		}
		
		Map map = (Map) list.get(0);
		System.out.println("map" + map.toString());
		String data = WxCertificate.doPost(datas, map, gas, jine, ljine, factory, 
				cardid, getPrice(), Integer.parseInt(times)+1); // Integer.parseInt(times) + 1
		// 从云服务中拿到正确的数据之后，增加一条写卡记录，并修档案中相应的数据
		String writeDate = writeIc(cardid, f_userid, Double.parseDouble(gas),
				Double.parseDouble(jine), Double.parseDouble(ljine), Integer.parseInt(times), factory);
		String date = "\"writeDate\":\"" + writeDate + "\",";
		System.out.println("卡云服务返回的数据为："+data);
		
		StringBuffer dataBuffer = new StringBuffer(data);
		dataBuffer.insert(1, date);
		System.out.println("调用云服务写卡函数返回数据添加日期后："+dataBuffer.toString());
		log.debug("调用云服务写卡函数返回数据添加日期后：" + dataBuffer.toString());
		return dataBuffer.toString();
	}
	/**
	 * 获取阶梯气价气量,巩义划价不包含低保户和非民用用气
	 * @param req
	 * @param resp
	 * @return 获取到的阶梯气价与气量（一二阶）
	 * @throws JSONException 
	 */
	@GET
	@Path("price")
	public JSONObject getPrice() throws JSONException {
		final String sql = "select (select value  from t_singlevalue "
				+ "where name='民用气价')  f_stair1price, "
				+ " (select value from t_singlevalue where name='民用阶梯气量1') f_stair1amount, "
				+ " (select value from t_singlevalue where name='临界外气价') f_stair2price";
//		SessionFactory sf=this.hibernateTemplate.getSessionFactory();
//		Session session=sf.openSession();
//		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//		List list = query.list();
	/*	List list = hibernateTemplate.executeFind(new HibernateCallback() {  
            public Object doInHibernate(Session session) throws HibernateException,  
                    SQLException {  
                Query query = session.createSQLQuery(sql);    
                List list = query.list();  
                return list;  
            }  
        });*/ 
		
		@SuppressWarnings("rawtypes")
		List list = this.hibernateTemplate.executeFind(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				return session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			}
		});
		System.out.println(sql);
//		List list = this.hibernateTemplate.find(sql);
		//System.out.println(query.toString());
		System.out.println(list.toString());
		if(list.size() == 0) {
			return null;
		}
		System.out.println(list.get(0).toString());
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		log.debug(map.toString());
		System.out.println(map.toString());
		JSONObject obj = new JSONObject();
		obj.put("f_stair1amount", map.get("f_stair1amount"));
		obj.put("f_stair1price", map.get("f_stair1price"));
		obj.put("f_stair2price", map.get("f_stair2price"));
		return obj;
		
	}
	
	
	/**
	 * 获取账户余额，供蓝牙写卡界面使用，保证界面余额的实时与准确性
	 * @param req
	 * @param resp
	 * @return 账户余额
	 * @throws JSONException 
	 */
	@GET
	@Path("zhye")
	public JSONObject getZhye(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) throws JSONException {
		String userid = req.getParameter("userid");
		System.out.println(userid);
		final String sql = "select f_zhye from t_userfiles where f_userid = '" + userid + "'";
		
		List list = this.hibernateTemplate.executeFind(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				return session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			}
		});
		System.out.println(sql);
		System.out.println(list.toString());
		if(list.size() == 0) {
			return null;
		}
		System.out.println(list.get(0).toString());
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		
		
		double gas = getPregas(userid); // 获取当月累计购气量
		log.debug("查询到" + userid + "当前账户当月累计购气量为：" + map.get("gas"));
		log.debug("查询到" + userid + "当前账户当月累计购气量为：" + map.get("f_zhye"));
		System.out.println(map.toString());
		JSONObject obj = new JSONObject();
		obj.put("gas", gas);
		obj.put("f_zhye", map.get("f_zhye"));
		return obj;
	}
}
