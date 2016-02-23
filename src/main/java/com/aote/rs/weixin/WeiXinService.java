package com.aote.rs.weixin;

import java.io.IOException;
import java.net.URLEncoder;
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

@Path("weixin")
@Scope("prototype")
@Component
public class WeiXinService {

	/**
	 * �����Ȩcode
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
	 * ����û�openid
	 * 
	 * @param request
	 * @return
	 */
	@GET
	@Path("getopenid")
	public String getopenid(@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		System.out.println("weixin-getopendid");
		// code ˵�� �� code ��Ϊ��ȡ access_token ��Ʊ�ݣ�ÿ���û���Ȩ���ϵ� code ����һ���� code ֻ��ʹ��һ
		// �Σ� 5 ����δ��ʹ���Զ����ڡ�
		String code = request.getParameter("code");
		// ���codeΪ�գ�˵�����û���ֹ��Ȩ
		if (code == null) {

		}
		String state = request.getParameter("state");
		System.out.println(state);
		String openid = "";
		try {
			String appid = Configure.getAppid();
			String secret = Configure.getSecret();
			// ����code�����openid
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
			// �ض���Ԥ֧������
			String redirect_url = "/weixin.html?openid=" + openid
					+ "&showwxpaytitle=1";
			response.sendRedirect(redirect_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ���Ԥ�µ���ţ�����ǰ̨��ȡ֧������
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
			data.setAttach("΢��֧��");
			data.setBody("΢�Ź��ں�֧��");
			data.setMch_id(Configure.getMchid());
			data.setNonce_str(WxSign.getNonceStr());
			data.setNotify_url(Configure.NOTIFY_URL);
			data.setOut_trade_no(WxSign.getNonceStr());
			data.setTotal_fee((int) (Double.parseDouble(money) * 100));// ��λ����
			data.setTrade_type("JSAPI");
			data.setSpbill_create_ip(request.getRemoteAddr());
			data.setOpenid(openid);
			// ͳһ�µ�
			String returnXml = unifiedOrder(data, Configure.getKey());
			WxPayReturnData reData = new WxPayReturnData();
			XStream xs1 = new XStream(new DomDriver());
			xs1.alias("xml", WxPayReturnData.class);
			reData = (WxPayReturnData) xs1.fromXML(returnXml);
			// �жϷ�������
			String return_code = reData.getReturn_code();
			if (return_code.equals("SUCCESS")) {
				if (reData.getResult_code().equals("SUCCESS")) {

				} else {
					result.put(
							"error",
							"���ش���" + reData.getErr_code()
									+ reData.getErr_code_des());
				}
			} else {
				result.put("error", "ϵͳ����" + reData.getReturn_msg());
			}
			// ��ת֧��ҳ��
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
		// ͳһ�µ�֧��
		String returnXml = null;
		try {
			// ����signǩ��
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
	 * ֧����ɻص���ַ
	 * 
	 * @param request
	 * @return
	 */
	@GET
	@Path("notify")
	public String notify(@Context HttpServletRequest request) {
		System.out.println("΢��֧��weixin-notify");
		return "";
	}
}
