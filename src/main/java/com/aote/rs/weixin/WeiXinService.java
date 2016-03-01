package com.aote.rs.weixin;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.util.HSSFColor.RED;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aote.rs.tcp.TcpService;
import com.aote.rs.util.StringUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

@Path("weixin")
@Scope("prototype")
@Component
public class WeiXinService {

	/**
	 * 获得授权code
	 * 
	 * @param response
	 * @return
	 */
	@GET
	@Path("getcode")
	public String getcode(@Context HttpServletResponse response) {
		System.out.println("weixin-getcode");
		String result = "";
		try {
			String appid = Configure.getAppid();
			String redirect_uri = "http://4504a3ef.nat123.net/rs/weixin/getopenid";
			String code_uri = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
					+ appid
					+ "&redirect_uri="
					+ redirect_uri
					+ "&response_type=code&scope=snsapi_base&state=GYWXPAY";
			response.sendRedirect(code_uri);
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
	 */
	@GET
	@Path("getopenid")
	public String getopenid(@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		System.out.println("weixin-getopendid");
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
			if (entity != null) {
				String str = EntityUtils.toString(entity, "UTF-8");
				JSONObject obj = new JSONObject(str);
				openid = obj.getString("openid");
			} else {

			}
			// 重定向到预支付界面
			String redirect_url = "/weixin.html?openid=" + openid
					+ "&showwxpaytitle=1";
			response.sendRedirect(redirect_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			String openid = request.getParameter("openid");
			String money = request.getParameter("money");
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
			System.out.println(result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

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
			parameters.put("total_fee", data.getTotal_fee());
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
	 */
	@GET
	@Path("notify")
	public String notify(@Context HttpServletRequest request) {
		System.out.println("微信支付weixin-notify");
		return "";
	}

	/**
	 * 银行代码
	 */
	private String yhno = "0000000051";
	/**
	 * 机构号
	 */
	private String jgno = "0000000051";
	/**
	 * 银行方终端设备代码或营业厅编码
	 */
	private String sbno = "0000000051";

	/**
	 * 根据用户编号生成查询报文
	 * 
	 * @param userid
	 * @return
	 */
	private String get1001(String userid) {
		String result = "";
		result = "000000651001";
		userid = StringUtil.joint(userid, 10, " ");
		System.out.println(userid);
		// 银行交易流水号：YYYYMMDD+12位流水号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String pipeline = sdf.format(new Date()) + StringUtil.grandom(6);
		result += userid + yhno + pipeline + "0" + jgno + sbno;
		return result;
	}

	/**
	 * 发送交费报文
	 * @param userid 用户编号
	 * @param money 交费金额
	 * @return
	 */
	private String get1002(String userid, String money) {
		String result = "";
		result = "000000751002";
		userid = StringUtil.joint(userid, 10, " ");
		// 银行交易流水号：YYYYMMDD+12位流水号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String pipeline = sdf.format(new Date()) + StringUtil.grandom(6);
		result += userid + yhno + pipeline + "0" + jgno + sbno;
		BigDecimal j = new BigDecimal(money);
		j = j.multiply(new BigDecimal(100));
		money = StringUtil.jointleft(j.intValue() + "", 10, "0");
		result += money;
		return result;
	}


	public JSONObject wxquery(String userid) {
		JSONObject result = new JSONObject();
		TcpService tcp = new TcpService();
		result = tcp.send(get1001(userid));
		return result;
	}

	public JSONObject wxpay(String userid, String money) {
		JSONObject result = new JSONObject();
		TcpService tcp = new TcpService();
		result = tcp.send(get1002(userid, money));
		return result;
	}
}
