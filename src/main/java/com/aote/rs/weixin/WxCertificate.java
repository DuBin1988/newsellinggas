package com.aote.rs.weixin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.dialect.IngresDialect;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Ӳ������ʱ��Ҫ��ǩ��
 * @author Administrator
 *
 */
public class WxCertificate {
	static Logger log = Logger.getLogger(WxCertificate.class);
	
	
	// �Լ����token
	public static String token = "";
	// ����ǰ�洢token��ʱ�䣬Ҫ�����ж��Ƿ�ʧЧ
	public static String oldtime = "0";
	// �����õ���ticket
	public static String jsapi_ticket = "";
	
	/**
	 * ��ȡaccess_token��ticket��Ҫ��get����
	 * @param url �����ַ��Я������
	 * @return ���������Ľ�����Ѿ����ַ���ת��ΪJSONObject
	 * @throws Exception �����쳣
	 */
	public static JSONObject doGet(String url) throws Exception {
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		
		String result = EntityUtils.toString(response.getEntity());
		
		System.out.println("get���󵽵�����Ϊ��" + result);
		return new JSONObject(result);
	}
	
	/**
	 * ��ȡaccess_token,ע��token����Чʱ��Ϊ7200�루2Сʱ����Ҫ�Լ�������
	 * @param appid
	 * @param appsecret
	 * @return
	 */
	public static String getToken(String appid, String appsecret) {
		String newtime = WxSign.getTimeStamp();
		int timediffent = Integer.valueOf(newtime) - Integer.valueOf(oldtime);
		if( timediffent < 7000){
			return token;
		}
		String url = "https://api.weixin.qq.com/cgi-bin/token?"
				+ "grant_type=client_credential"
				+ "&appid=" + appid
				+ "&secret=" + appsecret;
		try {
			
			JSONObject result = doGet(url);
			String access_token = result.getString("access_token");
			System.out.println("get������tokenΪ-->" + access_token);
			token = access_token;
			
			// ������ʱ��ʱ���������
			oldtime = WxSign.getTimeStamp();
			return access_token;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("����access_token�쳣");
		}
		
	}
	
	/**
	 * �õ�access_token����ticket
	 * @param access_token �������õĲ���
	 * @return ���󷵻ص�jsapi_ticket
	 */
	public static String getTicket(String access_token) {
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket"
				+ "?access_token=" + access_token
				+ "&type=jsapi";
		
		try {
			JSONObject result = doGet(url);
			String ticket = result.getString("ticket");
			System.out.println("get������ticketΪ-->" + ticket);
			jsapi_ticket = ticket;
			return ticket;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("����jsapi_tiketʱ�쳣");
		}
	}
	/**
	 * ���ǩ��
	 * @param appid
	 * @param appsecret
	 * @param noncestr ����ַ���
	 * @param timestamp ����ǩ��ʱ��ʱ���
	 * @param url ��ǰ��ҳ��URL��������#������沿��
	 * @return ���ɺ��ǩ��
	 */
	public static String getSign(String appid, String appsecret, String noncestr, String timestamp, String url) {
		// ����ǩ��ʱ��ʱ�����Ҫ�ж�ticket�Ƿ���Ч��
		String newtime = WxSign.getTimeStamp();
		int timediffent = Integer.valueOf(newtime) - Integer.valueOf(oldtime);
		System.out.println("�����ʱ���Ϊ-->" + timediffent);
		// ʧЧ��������
		if(timediffent > 7000) {
			getToken(appid, appsecret);
			getTicket(token);
		}
		
		SortedMap<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("jsapi_ticket", jsapi_ticket);
		parameters.put("noncestr", noncestr);
		parameters.put("timestamp", timestamp);
		parameters.put("url", url);
		// �����д�ǩ�����������ֶ�����ASCII ���С���������ֵ��򣩺�ʹ��URL��ֵ�Եĸ�ʽ����key1=value1&key2=value2����ƴ�ӳ��ַ���string
		StringBuffer sb = new StringBuffer();
		Set es = parameters.entrySet();
		// ���в��봫�εĲ�������accsii��������
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		System.out.println(sb.toString().substring(0, sb.toString().length()-1));
		String sign = sha1(sb.toString().substring(0, sb.toString().length()-1));
		String signature = sign;
		System.out.println("sing-->" + sign);
		return signature;
	}
	/**
	 * ���ַ�������sha-1����ǩ��
	 * @param str Ҫ���ܵ��ַ���
	 * @return ���ܺ��ǩ��
	 */
	public static String sha1(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(str.getBytes());
			//��ȡ�ֽ�����  
            byte messageDigest[] = digest.digest();  
            // Create Hex String  
            StringBuffer hexString = new StringBuffer();  
            // �ֽ�����ת��Ϊ ʮ������ ��  
            for (int i = 0; i < messageDigest.length; i++) {  
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
                if (shaHex.length() < 2) {  
                    hexString.append(0);  
                }  
                hexString.append(shaHex);  
            }  
            return hexString.toString();  
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("���ַ�sha1����ʧ��");
		}
	}
	/**
	 * 
	 * @param url �����ַ
	 * @param ticket ����ƾ֤
	 * @param device_id �豸id
	 * @param openid �û�openid
	 * @return �����Ƿ�ɹ�
	 */
	public static String doPost(String url, String ticket, String device_id, String openid) {
		System.out.println(url);
		System.out.println("openid-->"+ openid);
		try {
			JSONObject param = new JSONObject();
			param.put("ticket", ticket);
			param.put("device_id", device_id);
			param.put("openid", openid);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			StringEntity postEntity = new StringEntity(param.toString(),"UTF-8");
			postRequest.addHeader("Content-Type", "text/xml");
			postRequest.setEntity(postEntity);
			HttpResponse httpResponse = httpclient.execute(postRequest);
			HttpEntity entity = httpResponse.getEntity();
			
			return EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("����󶨽��ʧ��");
		} 
		
	}
	/**
	 * 
	 * @param data �ӿ��϶���������
	 * @param f_userid ����
	 * @param gas ������
	 * @return д�����ݺͿ����룬����У��
	 */
	public static String doPost(String data, Map<String, Object> map, String gas,
			String jine,String ljine,String factory,String cardid, JSONObject price, int times)
	{
		
		try {
			System.out.println("����д����ʼ");
//			��Դʹ��
			String date = getDate("yyyyMMdd");
			System.out.println("��ȡ����Ϊ"+date);
			int pirce = (int)(Double.parseDouble(price.getString("f_stair1price")) * 100);
			System.out.println(pirce);
			int f_metergasnums = 0;
			Double gasnums =  (Double) map.get("f_metergasnums");
			if (gasnums != null) {
				f_metergasnums = (int)(gasnums.doubleValue());
			}
			
			// ����ʹ��
			System.out.println("д��д������" + times);
			
			System.out.println("picre="+pirce+",f_metergasnums="+f_metergasnums);
			String url = "";
			JSONObject param = new JSONObject();
			if(factory.equals("ZhenLan")) {
				url = "http://127.0.0.1:8001/WriteGasCard"
						+ "/" + factory          //����
						+ "/" + map.get("kmm")  //�����룬д���󷵻�������map.get("kmm")
						+ "/" + cardid           //����
						+ "/" + map.get("dqdm")  //�������룬�����������ȡ
						+ "/" + (int)(Double.parseDouble(gas))     //����
						+ "/" + 0                //�ϴι���������Щ����Ҫ��
						+ "/" + 0                //���ϴι���������Щ����Ҫ��
						+ "/" + times              //��������
						+ "/" + f_metergasnums //��ǰ���ۼƹ�����
						+ "/" + map.get("bjql")         //��������
						+ "/" + map.get("czsx")         //��ֵ���ޣ��������������������
						+ "/" + map.get("tzed")        //͸֧��ȣ��������������������
						+ "/" + date         //�������ڣ���ʽΪYYYYMMDD
						+ "/" + date         //�ϴ��������ڣ���ʽΪYYYYMMDD
						+ "/" + pirce      //�ɵ��ۣ��۸������ȡmap.get("f_stair1price") 
						+ "/" + pirce       //�µ��ۣ��۸������ȡ
						+ "/" + date         //��Ч���ڣ��۸������ȡ
						+ "/" + 1;         //��Ч���
				param.put("CardType", "1");
				param.put("operatetype", "1");
				param.put("opid", "1");
				param.put("CardData", data);
				param.put("stairprice1", price.getString("f_stair1price"));
				param.put("stairgas1", price.getString("f_stair1amount"));
				param.put("stairprice2", price.getString("f_stair2price"));
				param.put("stairgas2", "1000"); // ����������������
				param.put("stairprice3", price.getString("f_stair2price"));// ��������Ҳ�ö�������
				param.put("stairgas3", "1000"); // ��һ���̶�ֵ
				param.put("money", jine);
				param.put("totalmoney", (Integer.parseInt(ljine)+Integer.parseInt(jine)) + ""); // f_cumulativemoney
				System.out.println("�����򿨷���д��ǰ����url-->" + url);
				log.debug("�����򿨷���д��ǰ����url-->" + url);
				System.out.println("�����򿨷���д��ǰ�������-->" + param.toString());
				log.debug("�����򿨷���д��ǰ�������-->" + param.toString());
			}else if(factory.equals("ChuangYuan")) {
				url = "http://127.0.0.1:8001/WriteGasCard"
						+ "/" + factory          //����
						+ "/" + map.get("kmm")  //�����룬д���󷵻�������map.get("kmm")
						+ "/" + cardid           //����
						+ "/" + map.get("dqdm")  //�������룬�����������ȡ
						+ "/" + gas              //����
						+ "/" + 0                //�ϴι���������Щ����Ҫ��
						+ "/" + 0                //���ϴι���������Щ����Ҫ��
						+ "/" + 0                //��������
						+ "/" + f_metergasnums //��ǰ���ۼƹ�����
						+ "/" + map.get("bjql")         //��������
						+ "/" + map.get("czsx")         //��ֵ���ޣ��������������������
						+ "/" + map.get("tzed")        //͸֧��ȣ��������������������
						+ "/" + date         //�������ڣ���ʽΪYYYYMMDD
						+ "/" + date         //�ϴ��������ڣ���ʽΪYYYYMMDD
						+ "/" + pirce      //�ɵ��ۣ��۸������ȡmap.get("f_stair1price") 
						+ "/" + pirce       //�µ��ۣ��۸������ȡ
						+ "/" + date         //��Ч���ڣ��۸������ȡ
						+ "/" + 1;         //��Ч���
				param.put("CardType", "1");
				param.put("operatetype", "1");
				param.put("opid", "1");
				param.put("CardData", data);
				System.out.println("��Դ�򿨷���д��ǰ����url-->" + url);
				log.debug("��Դ�򿨷���д��ǰ����url-->" + url);
				System.out.println("��Դ�򿨷���д��ǰ�������-->" + param.toString());
				log.debug("��Դ�򿨷���д��ǰ�������-->" + param.toString());
			}
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			StringEntity postEntity = new StringEntity(param.toString(),"UTF-8");
			postRequest.setEntity(postEntity);
			HttpResponse httpResponse = httpclient.execute(postRequest);
			HttpEntity entity=null;
			String result=null;
			System.out.println("������"+httpResponse.getStatusLine().getStatusCode());
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				entity = httpResponse.getEntity();
				result=EntityUtils.toString(entity, "UTF-8");
				System.out.println("д���Ʒ��񷵻����ݣ�"+result);
			}
			return  result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("�����Ʒ���ʧ��");
		}
	}
	
	/**
	 * 
	 * @param data �ӿ��϶���������
	 * @return �����Ʒ������������
	 */
	public static String doPost(String data)
	{
//		String url = "http://aofeng2.s1.natapp.cc/ReadCard";
		String url = "http://127.0.0.1:8001/ReadCard";
		try {
			JSONObject param = new JSONObject();
			param.put("CardType", "1");
			param.put("operatetype", "1");
			param.put("opid", "1");
			param.put("cardinfo", data);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			StringEntity postEntity = new StringEntity(param.toString(),"UTF-8");
			//postRequest.addHeader("Content-Type", "application/json");
			postRequest.setEntity(postEntity);
			HttpResponse httpResponse = httpclient.execute(postRequest);
			HttpEntity entity=null;
			String result=null;
			System.out.println("������"+httpResponse.getStatusLine().getStatusCode());
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				entity = httpResponse.getEntity();
				result=EntityUtils.toString(entity, "UTF-8");
				System.out.println("�����Ʒ��񷵻����ݣ�"+result);
			}
			return  result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("�����Ʒ���ʧ��");
		}
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String getDate(String str) {
		System.out.println("��ȡʱ��ĸ�ʽΪ��" + str);
		SimpleDateFormat format = new SimpleDateFormat(str);
		System.out.println("format" + format);
		String s = format.format(new Date());
		System.out.println(s);
		return s;
	}
	
	public static Date getDate(String fm, String str) throws ParseException {
		System.out.println("��ȡʱ��ĸ�ʽΪ��" + str);
		SimpleDateFormat format = new SimpleDateFormat(fm);
		return format.parse(str);
	}
}

