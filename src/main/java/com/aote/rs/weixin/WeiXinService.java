package com.aote.rs.weixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.tcp.TcpService;
import com.aote.rs.util.StringUtil;
import com.tencent.WXPay;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

@Path("weixin")
@Scope("prototype")
@Component
public class WeiXinService {

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
			String redirect_uri = "http://weixin.uxinxin.com/rs/weixin/getopenid";
			System.out.println("==============");
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
				openid = obj.getString("openid");
			} else {

			}
			if (state.equals("relieve")) {
				String redirect_url = "/jbind.jsp?openid=" + openid
						+ "&showwxpaytitle=1" + "&uuid" + uuid;
				response.sendRedirect(redirect_url);

			} else {
				Map<String, Object> map = selList(openid);
				if (map == null) {
					// �ض��򵽰�ҳ��
					String redirect_url = "/bind.jsp?openid=" + openid
							+ "&showwxpaytitle=1" + "&uuid" + uuid;
					response.sendRedirect(redirect_url);
				} else {
					String f_userid = map.get("f_userid").toString();
					// �ض���Ԥ֧������
					JSONObject object = selectqf(f_userid);
					JSONArray arr = object.getJSONArray("arr");
					double f_zhye = object.getDouble("zhye");
					double money = object.getDouble("money");
					double zhinajin = object.getDouble("zhinajin");
					String redirect_url = "/qf1.jsp?openid=" + openid
							+ "&showwxpaytitle=1" + "&f_zhye=" + f_zhye
							+ "&money=" + money + "&zhinajin=" + zhinajin
							+ "&arr=" + arr + "&uuid" + uuid;
					response.sendRedirect(redirect_url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject selectqf(String f_userid) throws JSONException {
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
		obj1.put("zhye", zhye);
		obj1.put("zhinajin", zhinajin);
		for (int i = x; i > 0; i--) {
			obj = new JSONObject();
			String le = new String(b, len + 8 - 70 * i, 70);
			System.out.println(le);

			obj.put("f_userid", new String(b, 12, 8));
			obj.put("lastinputdate", le.substring(0, 10));
			obj.put("astinputgasnum", Integer.parseInt(le.substring(11, 20)));
			obj.put("lastrecord", Integer.parseInt(le.substring(21, 30)));
			obj.put("oughtmount", Integer.parseInt(le.substring(31, 40)));
			obj.put("totaloughtfee",
					Double.parseDouble(le.substring(41, 50)) / 100);
			obj.put("oughtfeed", Double.parseDouble(le.substring(51, 60)) / 100);
			obj.put("oughtfee", Double.parseDouble(le.substring(61, 70)) / 100);
			arr.put(obj);
		}
		obj1.put("arr", arr);
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
			System.out.println(data);
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
			System.out.println(finalpackage);
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
		JSONObject ob = xml(request, response);
		// String redirect_url = "/success.jsp?openid=" + openid
		// + "&showwxpaytitle=1";
		// response.sendRedirect(redirect_url);
		return "";
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
			map.put("f_time_end", reData.getTime_end());
			Map<String, Object> row = selweixin(reData.getTransaction_id());
			if (row == null) {
				hibernateTemplate.saveOrUpdate("t_weixinreturnxml", map);
				String f_openid = reData.getOpenid();
				Map<String, Object> row1 = selList(f_openid);
				String f_total_fee = reData.getTotal_fee() + "";
				JSONObject object = wxpay(row1.get("f_userid").toString(),
						f_total_fee, reData.getTransaction_id(),
						reData.getAttach());
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
	private String yhno = "0000000051";
	/**
	 * ������
	 */
	private String jgno = "0000000051";
	/**
	 * ���з��ն��豸�����Ӫҵ������
	 */
	private String sbno = "0000000051";

	/**
	 * �����û�������ɲ�ѯ����
	 * 
	 * @param userid
	 * @return
	 */
	private String get1001(String userid) {
		String result = "";
		result = "000000651001";
		userid = StringUtil.joint(userid, 10, " ");
		System.out.println(userid);
		// ���н�����ˮ�ţ�YYYYMMDD+12λ��ˮ��
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String pipeline = sdf.format(new Date()) + StringUtil.grandom(6);
		result += userid + yhno + pipeline + "0" + jgno + sbno;
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
		result += userid + yhno + transation_id + "0" + jgno + sbno;
		// BigDecimal j = new BigDecimal(money);
		// j = j.multiply(new BigDecimal(100));
		money = StringUtil.jointleft(money, 10, "0");
		result += money;
		return result;
	}

	public JSONObject wxquery(String userid) {
		JSONObject result = new JSONObject();
		TcpService tcp = new TcpService();
		result = tcp.send(get1001(userid));
		return result;
	}

	public JSONObject wxpay(String userid, String money, String transation_id,
			String attach) {
		JSONObject result = new JSONObject();
		TcpService tcp = new TcpService();
		result = tcp.send(get1002(userid, money, transation_id, attach));
		return result;
	}

	@Autowired
	private HibernateTemplate hibernateTemplate;

	/**
	 * List execute sql in hibernate
	 * 
	 * @param sql
	 */
	private Map<String, Object> selList(String openid) {

		String sql = "from t_userfiles   where f_openid='" + openid + "'";
		List list = this.hibernateTemplate.find(sql);
		int x = list.size();
		if (list.size() != 1)
			return null;
		else
			return (Map<String, Object>) list.get(0);
	}

	// ��ѯ΢�Ž�����
	private Map<String, Object> selweixin(String f_transaction_id) {

		String sql = "from t_weixinreturnxml   where f_transaction_id='"
				+ f_transaction_id + "'";
		List listwx = this.hibernateTemplate.find(sql);
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
			Map<String, Object> map = (Map<String, Object>) list.get(0);
			String f_username = map.get("f_username").toString();
			String f_address = map.get("f_address").toString();
			String f_openid = (String) map.get("f_openid");
			if (f_openid == null) {
				f_openid = "";

			}
			JSONObject jo = new JSONObject();
			jo.put("f_username", f_username);
			jo.put("f_address", f_address);
			jo.put("f_openid", f_openid);
			return jo;
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
		if (f_openid.equals("")) {
			String sql = "update t_userfiles set f_openid='" + openid
					+ "'  where " + " f_userid='" + f_userid + "'";
			int length = this.hibernateTemplate.bulkUpdate(sql);
			if (length == 0) {
				return null;
			} else {
				JSONObject ob = selectqf(f_userid);
				ob.put("message", "�󶨳ɹ�");
				return ob;
			}
		} else {
			JSONObject object = new JSONObject();
			if(f_openid==openid){
				object.put("message", "���Ѱ�");
			}else{
			object.put("message", "�˺��Ѿ�������û���");
			System.out.println(object);
			return object;
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
			String sql = "update t_userfiles set f_openid=NULL  where " + " f_userid='" + f_userid + "'";
			int length = this.hibernateTemplate.bulkUpdate(sql);
			object.put("message", "���ɹ�");
			  
		}
		return object;
	};
	
}
