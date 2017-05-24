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
	 * �����Ȩcode
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
		// �õ��û����������
		// String banben = st.nextToken();
		// System.out.println("banben-"+str[1]);
		// �õ��û��Ĳ���ϵͳ��
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
	 * ����û�openid
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
			String uuid = UUID.randomUUID().toString();

			if (entity != null) {
				String str = EntityUtils.toString(entity, "UTF-8");
				JSONObject obj = new JSONObject(str);
				System.out.println(obj.toString());
				openid = obj.getString("openid");
			} else {

			}
			if (state.equals("relieve")) {
				String redirect_url = "/test/jbind.jsp?openid=" + openid
						+ "&showwxpaytitle=1" + "&uuid=" + uuid;
				response.sendRedirect(redirect_url);
			}else if(state.equals("qianysoft")){
				Map<String, Object> map = selList(openid);
				if(map == null){
					// �ض��򵽰�ҳ��
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
					// �ض��򵽰�ҳ��
					String redirect_url = "/test/bind.jsp?openid=" + openid
							+ "&showwxpaytitle=1" + "&uuid=" + uuid;
					response.sendRedirect(redirect_url);
				} else {
					String userid = map.get("f_userid").toString();
					// String f_username = map.get("f_username").toString();

					// f_username = f_username.getBytes("utf-8").toString();
					// f_username="����".getBytes("GBK").toString();

					// �ض���Ԥ֧������
					JSONObject object = selectqf(userid);
					JSONArray arr = object.getJSONArray("arr");
					double f_zhye = object.getDouble("zhye");
					double money = object.getDouble("money");
					double zhinajin = object.getDouble("zhinajin");
					System.out.println("f_zhye" + f_zhye + "money" + money
							+ "zhinajin" + zhinajin);
					// Ϊ�����û���ӵĻ�ȡ�˻����
					double ic_zhye = object.getDouble("ic_zhye");
					
					
					String f_userid = object.getString("f_userid");
					String f_username = URLEncoder.encode(
							object.getString("f_username"), "utf-8");
					String f_address = URLEncoder.encode(
							object.getString("f_address"), "utf-8");

					// Ϊ�����û����
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
		// ��ѯǷ�ѽ��;
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
		// �����ж��ǿ����ǻ����û�
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
			String id = request.getSession().getId();
			System.out.println("sessionid=" + id);
			String openid = request.getParameter("openid");
			System.out.println("openid=" + openid);
			String money = request.getParameter("money");
			System.out.println("money=" + money);
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
	 * ֧����ɻص���ַ
	 * 
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("notify")
	public String notify(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException {
		System.out.println("΢��֧��weixin-notify");
		log.debug("΢��֧��weixin-notify");
		JSONObject ob = xml(request, response);
		// String redirect_url = "/success.jsp?openid=" + openid
		// + "&showwxpaytitle=1";
		// response.sendRedirect(redirect_url);
		return "";
	}

	public boolean GetWeChatPayResult(String OrderFormID) {
		// ͨ��OrderFormID��ѯ΢�Ž����
		return false;
	}

	public synchronized JSONObject xml(@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		try {
			// ����
			request.setCharacterEncoding("UTF-8");
			// // response.setCharacterEncoding("UTF-8");
			PrintWriter out;
			// ��ȡ���յ���xml��Ϣ
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
			//������
			map.put("f_message", "δ����");
			map.put("f_time_end", reData.getTime_end());
			Map<String, Object> r = selList(reData.getOpenid());
			//Ϊ��   �����20170324
			if(r==null){
				Map<String, Object> ru = selunbundlingList(reData.getOpenid(),reData.getTransaction_id());
				map.put("f_userid", ru.get("f_userid").toString());
			}else{
			map.put("f_userid", r.get("f_userid").toString());
			}

			Map<String, Object> row = selweixin(reData.getTransaction_id());
			if (row == null) {
				// ��΢�ŷ��ص���Ϣ�洢��t_weixinreturnxml��
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
	 * ���д���
	 */
	private String yhno = "weixin";
	/**
	 * ������
	 */
	private String jgno = "weixin";
	/**
	 * ���з��ն��豸�����Ӫҵ������
	 */
	private String sbno = "weixin";

	/**
	 * �����û�������ɲ�ѯ����
	 * 
	 * @param userid
	 * @return
	 */
	private String get1001(String userid) {
		String result = "";
		result = "000000731001";
		userid = StringUtil.joint(userid, 10, " ");
		System.out.println(userid);
		// ���н�����ˮ�ţ�YYYYMMDD+20λ��ˮ��
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String pipeline = sdf.format(new Date()) + StringUtil.grandom(14);
		result += userid + StringUtil.joint(yhno, 10, " ") + pipeline + "0"
				+ StringUtil.joint(jgno, 10, " ")
				+ StringUtil.joint(sbno, 10, " ");
		return result;
	}

	/**
	 * ���ͽ��ѱ���
	 * 
	 * @param userid
	 *            �û����
	 * @param money
	 *            ���ѽ��
	 * @return
	 */
	private String get1002(String userid, String money, String transation_id,
			String attach) {
		String result = "";
		result = "000000831002";
		userid = StringUtil.joint(userid, 10, " ");
		// ���н�����ˮ�ţ�YYYYMMDD+12λ��ˮ��
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
		// ��ǰû�У��Լ���� (�ж��ǿ����û����ǻ����û�)
		String meterstyle = getmeterstyle(userid);
		if (meterstyle.equals("����")) {
			ICChongZhi ic = new ICChongZhi();
			ic.saveandmodify(userid, money, transation_id, attach, time_end);
			log.debug("�����û��ɷ�");
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
	 * List execute sql in hibernate ��ѯ�û��Ƿ�󶨣��󶨺�Ͳ�ѯ�������ݷ��أ�δ�󶨷���null
	 * 
	 * @param sql
	 */
	private Map<String, Object> selList(String openid) {

		String sql = "from t_userfiles   where f_openid='" + openid + "'";
		List list = this.hibernateTemplate.find(sql);
		log.debug("��ѯ�û��Ƿ��" + sql);
		int x = list.size();
		System.out.println(x);
		if (list.size() != 1)
			return null;
		else
			return (Map<String, Object>) list.get(0);
	}
	
	/**
	 * �����20170324
 
	 * @param openid
	 * @return
	 */
	private Map<String, Object> selunbundlingList(String openid,String f_transaction_id) {

		String sql = "from t_weixinunbundling  where f_openid='"+openid+"' and f_transaction_id="+f_transaction_id+"'";
		List list = this.hibernateTemplate.find(sql);
		log.debug("��ѯ�û�id" + sql);
		int x = list.size();
		System.out.println(x);
		if (list.size() != 1)
			return null;
		else
			return (Map<String, Object>) list.get(0);
	}



	// ��ѯ΢�Ž�����
	/**
	 * ����΢�Ž����룬��ѯt_weixinreturnxml����ѯ���ͽ���ѯ�����ݷ��أ�û���򷵻�null
	 * 
	 * @param f_transaction_id
	 * @return
	 */
	private Map<String, Object> selweixin(String f_transaction_id) {

		String sql = "from t_weixinreturnxml   where f_transaction_id='"
				+ f_transaction_id + "'";
		List listwx = this.hibernateTemplate.find(sql);
		log.debug("��ѯ΢�Ž�����" + sql);
		int x = listwx.size();

		if (listwx.size() != 1)
			return null;
		else
			return (Map<String, Object>) listwx.get(0);
	}

	// ��ѯ�û�
	@GET
	@Path("/one/{f_userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject selList1(@PathParam("f_userid") String f_userid) {
		try {

			String sql = "from t_userfiles   where f_userid='" + f_userid + "'";

			List list = this.hibernateTemplate.find(sql);
			log.debug("��ѯ�û�������Ϣ" + sql);
			Map<String, Object> map = (Map<String, Object>) list.get(0);
			JSONObject jo = new JSONObject();

			if (list.size() == 0) {
				jo.put("message", "������������û�����Ƿ���ȷ");
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
				// �Լ��������
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

	// ��
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
			log.debug("�û���" + sql);
			if (length == 0) {
				return null;
			} else {
				JSONObject ob = selectqf(f_userid);
				ob.put("message", "�󶨳ɹ�");
				log.debug(ob);
				System.out.println(ob);
				return ob;
			}
		} else {
			if (!f_openid.equals("")) {
				JSONObject object = new JSONObject();
				object.put("message", "�˺��Ѿ�������û���");
				System.out.println(object);
				log.debug(object);
				return object;
			}
			if (list.size() > 1) {
				JSONObject object = new JSONObject();
				object.put("message", "Ҫ���µ��û���ţ����ȶ��Ѱ󶨵��û���Ž����");
				System.out.println(object);
				log.debug(object);
				return object;
			} else {
				if (list.size() == 1) {
					Map<String, Object> map = (Map<String, Object>) list.get(0);
					String userid = map.get("f_userid").toString();
					if (userid.equals(f_userid)) {
						JSONObject obj1 = selectqf(f_userid);
						obj1.put("message", "���Ѱ�");
						log.debug(obj1);
						// System.out.println(obj1);
						return obj1;
					} else {
						JSONObject object = new JSONObject();
						object.put("message", "Ҫ���µ��û���ţ����ȶ��Ѱ󶨵��û���Ž����");
						// System.out.println(object);
						log.debug(object);
						return object;
					}

				}
			}
		}
		return null;
	}

	// ���
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
			object.put("message", "��������������Ƿ���ȷ,������δ��");

		} else {
			//sava����
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
				log.debug("�������"+e);
				e.printStackTrace();
	 
			}
			String sql = "update t_userfiles set f_openid=NULL  where "
					+ " f_userid='" + f_userid + "'";

			int length = this.hibernateTemplate.bulkUpdate(sql);
			log.debug("�û����" + sql+"���openid:"+openid+"����û����:"+f_userid);
			object.put("message", "���ɹ�");

		}
		return object;
	}

	/**
	 *  ��ѯ΢�Ž�����
	 * @param f_transaction_id
	 * @return
	 */
		private Map<String, Object> selweixinsn(String f_openid) {

			String sql = "from t_weixinreturnxml   where f_openid='"+f_openid+"'";
											   
			List listwx = this.hibernateTemplate.find(sql);
			log.debug("��ѯ΢�Ž�����" + sql);
			int x = listwx.size();

			if (listwx.size() != 1)
				return null;
			else
				return (Map<String, Object>) listwx.get(0);
		}
		/**
		 *  ����20170324
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
	 * ��ȡ��Ӧ�û����������ͣ���ǰû�У����ӿ����û�ʱ����.��ѯ�û������ͣ������ж��ǲ��ǿ���
	 * 
	 * @param userid
	 * @return ���ػ�������null
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
		// TODO ��ʱд��
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
		System.out.println("bind��ʼ");
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
		if (marke.equals("1")) {// ��
			System.out.println("��" + marke);
			url = "https://api.weixin.qq.com/device/bind?access_token=" + token;

		} else { // ���
			System.out.println("���" + marke);
			url = "https://api.weixin.qq.com/device/unbind?access_token="
					+ token;
		}
		String result = WxCertificate.doPost(url, ticket, device, openid);
		
		return new JSONObject(result);
	}
	/**
	 * 
	 * @param f_cardid ����
	 * @param f_userid �û����
	 * @param writegas д������
	 * @param money д�����
	 * @param ljmoney ���ۼƹ�������������
	 * @param times д������
	 * @param factory ������
	 * @return "success" ��ʾ�ɹ�����������Ϊ������Ϣ
	 * @throws Exception ����������ת���쳣ʱ�׳�
	 */
	@SuppressWarnings("rawtypes")
	public String writeIc(String f_cardid, String f_userid, double writegas, double money, double ljmoney, int times, String factory) throws Exception {
		// д�������޸��˻����
		String sql = "from t_userfiles where f_userid = '" + f_userid + "'";
		System.out.println("��ѯ�˻���" + sql);
		log.debug("��ѯ�˻���" + sql);
		List list = this.hibernateTemplate.find(sql);
		if (list.size() == 0) {
			System.out.println("û�в�ѯ������û�");
			log.debug("û�в�ѯ������û�");
			return "û�в�ѯ������û�";
		} else if (list.size() > 1) {
			System.out.println("���Ų�Ψһ");
			log.debug("���Ų�Ψһ");
			return "";
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		log.debug("���Ʒ��񷵻����ݺ��ѯ"+f_userid +"�˻����ص����ݣ�" + map.toString());
		// ȡ���˻����
		double zhye = (Double) map.get("f_zhye");
		System.out.println("��ǰ�˻����Ϊ��" + zhye);
		log.debug(f_userid + "��ǰ�˻����Ϊ��" + zhye);
		
		BigDecimal xieka = BigDecimal.valueOf(money);
		BigDecimal jfzhye = BigDecimal.valueOf(zhye);
		double newzhye = Double.valueOf(jfzhye.subtract(xieka).toString());
		System.out.println(f_userid + "��ֵ����˻����Ϊ��" + newzhye);
		log.debug(f_userid + "��ֵ����˻����Ϊ��" + newzhye);
		
		double f_metergasnums = (Double) map.get("f_metergasnums") + writegas;
		double f_cumulativepurchase = (Double) map.get("f_cumulativepurchase") + writegas;
		System.out.println(f_userid + "��ֵ����˻����Ϊ��" + newzhye);
		log.debug(f_userid + "��ֵ����˻����Ϊ��" + newzhye);

		String writeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Map<String, Object> mapWrite = new HashMap<String, Object>();
		mapWrite.put("f_userid", map.get("f_userid"));
		mapWrite.put("f_username", map.get("f_username"));
		mapWrite.put("f_address", map.get("f_address"));
		mapWrite.put("f_iccard", f_cardid);
		mapWrite.put("f_writefee", xieka.doubleValue());
		mapWrite.put("f_writetype", "΢������");
		mapWrite.put("f_writemark", "δд��"); // д����ǣ�д����ɺ��޸�
		mapWrite.put("f_beforwriteye", (Double) map.get("f_zhye"));
		mapWrite.put("f_afterwriteye", newzhye);
		mapWrite.put("f_writegas", writegas);
		mapWrite.put("f_beforwritegas", (Double) map.get("f_metergasnums")); // ��ǰ���ۼƹ�����
		mapWrite.put("f_afterwritegas", f_metergasnums); // д������ۼƹ�����
		mapWrite.put("f_beforljjine", ljmoney); // д������ۼƹ�����
		mapWrite.put("f_afertljjine", money + ljmoney); // д������ۼƹ�����
		mapWrite.put("f_writedate",writeDate);
		mapWrite.put("f_factory",factory);
		
		mapWrite.put("f_yhxz", map.get("f_yhxz"));//�û�����
		mapWrite.put("f_districtname", map.get("f_districtname")); //С������
		mapWrite.put("f_gaspricetype", map.get("f_gaspricetype"));//��������
		mapWrite.put("f_gasprice", map.get("f_gasprice"));//����
		mapWrite.put("f_usertype", map.get("f_usertype"));//�û�����f_cumulativemoney
		mapWrite.put("f_gasproperties", map.get("f_gasproperties"));//�û�����
		mapWrite.put("f_cumulativemoney", map.get("f_cumulativemoney"));//�û�����
		txUpdate(money, newzhye, (String)map.get("f_userid"), writegas, f_metergasnums, f_cumulativepurchase, mapWrite,times);

		return writeDate;
	}
	/**
	 * д���ɹ��ص��ĺ�������д����¼�е�δд����Ϊ��д�����޸�д������
	 * @param req
	 * @param resp
	 * @return ���ش���0ʱ���ɹ�������ʧ��
	 */
	@GET
	@Path("success")
	public String writeIcSuccess(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) {
		// д���ɹ����޸�д����¼�б�ʶ��
		String f_iccard = req.getParameter("cardid");
		String writeDate = req.getParameter("writeDate");
		String sql = "update t_writeiccard set f_writemark = '��д��' where f_iccard = '"
				+ f_iccard + "' and f_writedate = '" + writeDate + "'";
		this.hibernateTemplate.bulkUpdate(sql);
		System.out.println("�޸�д����ʾΪ��д����" + sql);
		log.debug("�޸�д����ʾΪ��д����" + sql);
		
		String kmm = req.getParameter("kmm");
		String f_userid = req.getParameter("userid");
		String sql2 = "update t_userfiles set kmm = '"+ kmm +"' where f_userid = '"
				+ f_userid + "'";
		this.hibernateTemplate.bulkUpdate(sql2);
		log.debug("�޸�д����ʾΪ��д����" + sql);
		return "success";
	}

	/**
	 * ����һ��д����¼�����޸Ŀ��������д���ɹ��󣩣��޸��˻����(f_zhye)���޸��ۼƹ�����(f_metergasnums)
	 * ,���ۼƹ�����(f_metergasnums),���������f_finallybought������������ڣ�f_finabuygasdate����
	 * �����ʱ�䣨f_finabuygastime��
	 * @param jine д�����
	 * @param newzhye �������
	 * @param f_userid �û����
	 * @param writegas д������
	 * @param f_metergasnums ��ǰ����ۼƹ�����
	 * @param f_cumulativepurchase ���ۼƹ�����
	 * @param map Ҫ���ӵ�д����¼���ݼ���
	 * @param times д������
	 * @throws Exception ��������ת���쳣
	 */
	public void txUpdate(double jine, double newzhye,
			String f_userid,double writegas, double f_metergasnums, 
			double f_cumulativepurchase, final Map map, int times) throws Exception {
		
		String date = WxCertificate.getDate("yyyy-MM-dd HH:mm:ss");
		String[] dates = date.split(" ");
		
		// �޸��û������е�����f_times
		final String sqla = "update t_userfiles set f_zhye = " + newzhye
				+ ", f_finallybought = " + writegas
				+ ", f_metergasnums = " + f_metergasnums
				+ ", f_cumulativepurchase = " + f_cumulativepurchase
				+ ", f_finabuygasdate = '" + dates[0]
				+ "', f_finabuygastime = '" + dates[1]
				+ "', f_times = '" + times
				+ "' where f_userid = '" + f_userid + "'";
		System.out.println("�޸��û��������sqlΪ" + sqla);
		
		// ����һ���շѼ�¼
		final Map<String, Object> savemap = new HashMap<String, Object>();
		savemap.put("f_userid", map.get("f_userid")); // �û����
		savemap.put("f_username", map.get("f_username")); // �û�����
		savemap.put("f_address", map.get("f_address")); // �û���ַ
		savemap.put("f_districtname", map.get("f_districtname")); //С������
		savemap.put("f_gaspricetype", map.get("f_gaspricetype"));//��������
		savemap.put("f_gasprice", map.get("f_gasprice"));//����
		savemap.put("f_usertype", map.get("f_usertype"));//�û�����
		savemap.put("f_gasproperties", map.get("f_gasproperties"));//�û�����
		savemap.put("f_payment", "�˻����");//���ʽ
		savemap.put("f_filiale", "΢������");//�ֹ�˾
		savemap.put("f_comtype", "΢������");//��˾���ͣ���Ϊ��Ȼ����˾������
		savemap.put("f_gasmeterstyle", "����");//
		savemap.put("f_zhye", newzhye);//
		savemap.put("f_banksn", ""); // ΢��֧���������
		savemap.put("f_yhxz", map.get("f_yhxz"));//�û�����
		savemap.put("f_grossproceeds", 0.00); // �տ���
		savemap.put("f_sgnetwork", "΢������"); //����
		savemap.put("f_sgoperator", "΢������"); //����Ա
		savemap.put("f_payfeetype", "���۳�");//�շ�����
		savemap.put("f_jiezhangstate", "�ѽ���"); // ����״̬
		savemap.put("f_deliverytime", WxCertificate.getDate("HH:mm:ss", dates[1])); //�ɷ�ʱ��
		savemap.put("f_deliverydate", WxCertificate.getDate("yyyy-MM-dd", dates[0])); // �ɷ�����
		
		savemap.put("f_payfeevalid", "��Ч"); // ��Ч��Ч
		savemap.put("f_preamount", jine); // Ӧ�����
		savemap.put("f_pregas", writegas); // Ԥ������
		savemap.put("f_allamont", f_metergasnums); // 
		savemap.put("f_stairtype", ""); // 
		savemap.put("f_benqizhye", 0.00); //
		savemap.put("f_zhinajin", 0.00); //
		
		log.debug("�޸��û��������sqlΪ" + sqla);
		log.debug("���ӳ�ֵ��¼����Ϊ��" + map.toString());
		// �����ļ��е����������ã�����ʹ�����ַ��������������
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
		// �޸��û������е�����f_times
//		String sqla = "update t_userfiles set f_zhye = " + newzhye
//				+ ", f_finallybought = " + writegas
//				+ ", f_metergasnums = " + f_metergasnums
//				+ ", f_cumulativepurchase = " + f_cumulativepurchase
//				+ ", f_finabuygasdate = '" + dates[0]
//				+ "', f_finabuygastime = '" + dates[1]
//				+ "', f_times = '" + times
//				+ "' where f_userid = '" + f_userid + "'";
//		System.out.println("�޸��û��������sqlΪ" + sqla);
//		this.hibernateTemplate.bulkUpdate(sqla);
//		// ����һ��д����¼��¼
//		log.debug("�޸��û��������sqlΪ" + sqla);
//		System.out.println("���ӳ�ֵ��¼����Ϊ��" + map.toString());
//		this.hibernateTemplate.save("t_writeiccard", map);
//		log.debug("���ӳ�ֵ��¼����Ϊ��" + map.toString());
//		// ����һ�������Զ����˼�¼��������¼ʵ��Ϊ0�����е��շѽ�Ϊ�㣩
//		// ����һ���շѼ�¼
//		Map<String, Object> savemap = new HashMap<String, Object>();
//		savemap.put("f_userid", map.get("f_userid")); // �û����
//		savemap.put("f_username", map.get("f_username")); // �û�����
//		savemap.put("f_address", map.get("f_address")); // �û���ַ
//		savemap.put("f_districtname", map.get("f_districtname")); //С������
//		savemap.put("f_gaspricetype", map.get("f_gaspricetype"));//��������
//		savemap.put("f_gasprice", map.get("f_gasprice"));//����
//		savemap.put("f_usertype", map.get("f_usertype"));//�û�����
//		savemap.put("f_gasproperties", map.get("f_gasproperties"));//�û�����
//		savemap.put("f_payment", "�˻����");//���ʽ
//		savemap.put("f_filiale", "΢������");//�ֹ�˾
//		savemap.put("f_comtype", "΢������");//��˾���ͣ���Ϊ��Ȼ����˾������
//		savemap.put("f_gasmeterstyle", "����");//
//		savemap.put("f_zhye", newzhye);//
//		savemap.put("f_banksn", ""); // ΢��֧���������
//		savemap.put("f_yhxz", map.get("f_yhxz"));//�û�����
//		savemap.put("f_grossproceeds", 0.00); // �տ���
//		savemap.put("f_sgnetwork", "΢������"); //����
//		savemap.put("f_sgoperator", "΢������"); //����Ա
//		savemap.put("f_payfeetype", "���۳�");//�շ�����
//		savemap.put("f_jiezhangstate", "�ѽ���"); // ����״̬
//		savemap.put("f_deliverytime", WxCertificate.getDate("yyyy-MM-dd", dates[0])); //�ɷ�ʱ��
//		savemap.put("f_deliverydate", WxCertificate.getDate("yyyy-MM-dd", dates[0])); // �ɷ�����
//		
//		savemap.put("f_payfeevalid", "��Ч"); // ��Ч��Ч
//		savemap.put("f_preamount", jine); // Ӧ�����
//		savemap.put("f_pregas", writegas); // Ԥ������
//		savemap.put("f_allamont", f_metergasnums); // 
//		savemap.put("f_stairtype", ""); // 
//		savemap.put("f_benqizhye", 0.00); //
//		savemap.put("f_zhinajin", 0.00); //
//		// ����������¼
////		throw new RuntimeException();
//		this.hibernateTemplate.save("t_sellinggas", savemap);
//		
	}

	/**
	 * ��ѯд��ʧ�ܼ�¼
	 * 
	 * @param userid
	 * @return
	 */
	@GET
	@Path("WriteFail")
	public String getWriteFail(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) {
		System.out.println("д��ʧ�ܼ�¼��ѯ��ʼuserid--->" + req.getParameter("userid"));
		String sql = "from t_writeiccard where f_writemark='δд��' and f_userid='"
				+ req.getParameter("userid") + "'";
		log.debug("��ѯд��ʧ�ܼ�¼Ϊ��" + sql);
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
	 * �����û���Ų�ѯ�û��ĵ����ۼƹ�����
	 * 
	 * @param userid
	 * @return �����ۼƹ�����
	 */
	public double getPregas(String userid) {
//		final String sql = "select f_userid,SUM(f_pregas) f_pregas from"
//				+ "(select f_userid,SUM(f_pregas) f_pregas from "
//				+ "t_sellinggas where "
//				+ "f_deliverydate>=SUBSTRING(CONVERT(varchar(100),GETDATE(),23),0,9)+'01 00:00:00' "
//				+ "and f_deliverydate<=dateadd(ms,-3,DATEADD(mm,DATEDIFF(m,0,getdate())+1,0)) "
//				+ "and f_payfeevalid='��Ч' "
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
				+ " and f_payfeevalid='��Ч'"
				+ " and f_userid='"
				+ userid
				+ "'"
				+ " group by f_userid,f_username";
		System.out.println("��ȡ�û�"+userid+"�ı����ۼƹ�����sql��" + sql);
		log.debug("��ȡ�û�"+userid+"�ı����ۼƹ�����sql��" + sql);
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
		// ȡ���˻����
		double f_pregas = (Double) map.get("f_pregas");
		System.out.println("�����ۼƹ���" + f_pregas);
		log.debug("���Ϊ:"+userid+"���û������ۼƹ���:" + f_pregas);
		return f_pregas;
	}
	@GET
	@Path("datas")
	public String getDatas(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) {
		String datas = req.getParameter("cardinfo");
		log.debug("�ӿ��ж��������ݣ�"+datas);
		System.out.println(datas);
		String data = WxCertificate.doPost(datas);
		System.out.println(data);
		return data;
	}
	@GET
	@Path("writedata")
	public String getWrite(@Context HttpServletRequest req,
			@Context HttpServletResponse resp) throws Exception{
		// �����Ŀ�����
		String datas = req.getParameter("cardinfo");
		// ����
		String f_userid = req.getParameter("f_userid");
		// �������Ŀ�����
		String gas = req.getParameter("gas");
		String factory = req.getParameter("factory");
		String cardid = req.getParameter("cardid");
		String jine = req.getParameter("jine");
		String times = req.getParameter("times");
		// ��������ʵ����ʾ����������Ľ��ֻ�����������ܽ���
		// ��Դ�̶���0
		String ljine = req.getParameter("ljine");
		System.out.println("datas-->"+datas);
		System.out.println("f_userid-->"+f_userid);
		System.out.println("gas-->"+gas);
		System.out.println("jine-->"+jine);
		System.out.println("factory-->"+factory);
		System.out.println("cardid-->"+cardid);
		System.out.println("times-->"+times);
		System.out.println("ljine-->"+ljine);
		log.debug("�������ݣ�"+datas);
		log.debug("�û���ţ�"+f_userid);
		log.debug("д��������"+gas);
		log.debug("д����"+jine);
		log.debug("���ң�"+factory);
		log.debug("���ţ�"+cardid);
		log.debug("д��������"+times);
		log.debug("����ʹ�ã���ǰ���ۼƹ�����"+ljine);
		
		String sql = "from t_userfiles where f_userid = '" + f_userid + "'";
		System.out.println("��ȡ���Ʒ���ǰ��ѯ�û���Ϣ"+ sql);
		List list = this.hibernateTemplate.find(sql);
		System.out.println("ִ��hibernate size=" + list.size());
		if (list.size() == 0) {
			System.out.println("û�в�ѯ������û�");
			log.debug("û�в�ѯ������û�");
			return "û�в�ѯ������û�";
		} else if (list.size() > 1) {
			System.out.println("���Ų�Ψһ");
			log.debug("���Ų�Ψһ");
			return "���Ų�Ψһ";
		}
		
		Map map = (Map) list.get(0);
		System.out.println("map" + map.toString());
		log.debug("map" + map.toString());
		String data = WxCertificate.doPost(datas, map, gas, jine, ljine, factory, 
				cardid, getPrice(), Integer.parseInt(times)+1); // Integer.parseInt(times) + 1
		// ���Ʒ������õ���ȷ������֮������һ��д����¼�����޵�������Ӧ������
		String writeDate = writeIc(cardid, f_userid, Double.parseDouble(gas),
				Double.parseDouble(jine), Double.parseDouble(ljine), Integer.parseInt(times), factory);
		String date = "\"writeDate\":\"" + writeDate + "\",";
		System.out.println("���Ʒ��񷵻ص�����Ϊ��"+data);
		
		StringBuffer dataBuffer = new StringBuffer(data);
		dataBuffer.insert(1, date);
		System.out.println("�����Ʒ���д��������������������ں�"+dataBuffer.toString());
		log.debug("�����Ʒ���д��������������������ں�" + dataBuffer.toString());
		return dataBuffer.toString();
	}
	/**
	 * ��ȡ������������,���廮�۲������ͱ����ͷ���������
	 * @param req
	 * @param resp
	 * @return ��ȡ���Ľ���������������һ���ף�
	 * @throws JSONException 
	 */
	@GET
	@Path("price")
	public JSONObject getPrice() throws JSONException {
		final String sql = "select (select value  from t_singlevalue "
				+ "where name='��������')  f_stair1price, "
				+ " (select value from t_singlevalue where name='���ý�������1') f_stair1amount, "
				+ " (select value from t_singlevalue where name='�ٽ�������') f_stair2price";
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
	 * ��ȡ�˻���������д������ʹ�ã���֤��������ʵʱ��׼ȷ��
	 * @param req
	 * @param resp
	 * @return �˻����
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
		
		
		double gas = getPregas(userid); // ��ȡ�����ۼƹ�����
		log.debug("��ѯ��" + userid + "��ǰ�˻������ۼƹ�����Ϊ��" + map.get("gas"));
		log.debug("��ѯ��" + userid + "��ǰ�˻������ۼƹ�����Ϊ��" + map.get("f_zhye"));
		System.out.println(map.toString());
		JSONObject obj = new JSONObject();
		obj.put("gas", gas);
		obj.put("f_zhye", map.get("f_zhye"));
		return obj;
	}
}
