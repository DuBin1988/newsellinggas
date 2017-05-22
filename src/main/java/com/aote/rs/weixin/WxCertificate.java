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
 * 硬件接入时需要的签名
 * @author Administrator
 *
 */
public class WxCertificate {
	static Logger log = Logger.getLogger(WxCertificate.class);
	
	
	// 自己存的token
	public static String token = "";
	// 请求当前存储token的时间，要进行判断是否失效
	public static String oldtime = "0";
	// 请求拿到的ticket
	public static String jsapi_ticket = "";
	
	/**
	 * 获取access_token和ticket需要的get请求
	 * @param url 请求地址并携带参数
	 * @return 返回请求后的结果，已经将字符串转化为JSONObject
	 * @throws Exception 请求异常
	 */
	public static JSONObject doGet(String url) throws Exception {
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		
		String result = EntityUtils.toString(response.getEntity());
		
		System.out.println("get请求到的数据为：" + result);
		return new JSONObject(result);
	}
	
	/**
	 * 获取access_token,注意token的有效时间为7200秒（2小时），要自己存起来
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
			System.out.println("get请求获得token为-->" + access_token);
			token = access_token;
			
			// 将请求时的时间戳存起来
			oldtime = WxSign.getTimeStamp();
			return access_token;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("请求access_token异常");
		}
		
	}
	
	/**
	 * 拿到access_token后获得ticket
	 * @param access_token 请求所用的参数
	 * @return 请求返回的jsapi_ticket
	 */
	public static String getTicket(String access_token) {
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket"
				+ "?access_token=" + access_token
				+ "&type=jsapi";
		
		try {
			JSONObject result = doGet(url);
			String ticket = result.getString("ticket");
			System.out.println("get请求获得ticket为-->" + ticket);
			jsapi_ticket = ticket;
			return ticket;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("请求jsapi_tiket时异常");
		}
	}
	/**
	 * 获得签名
	 * @param appid
	 * @param appsecret
	 * @param noncestr 随机字符串
	 * @param timestamp 生成签名时的时间戳
	 * @param url 当前网页的URL，不包含#及其后面部分
	 * @return 生成后的签名
	 */
	public static String getSign(String appid, String appsecret, String noncestr, String timestamp, String url) {
		// 生成签名时的时间戳（要判断ticket是否有效）
		String newtime = WxSign.getTimeStamp();
		int timediffent = Integer.valueOf(newtime) - Integer.valueOf(oldtime);
		System.out.println("请求的时间差为-->" + timediffent);
		// 失效重新请求
		if(timediffent > 7000) {
			getToken(appid, appsecret);
			getTicket(token);
		}
		
		SortedMap<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("jsapi_ticket", jsapi_ticket);
		parameters.put("noncestr", noncestr);
		parameters.put("timestamp", timestamp);
		parameters.put("url", url);
		// 对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string
		StringBuffer sb = new StringBuffer();
		Set es = parameters.entrySet();
		// 所有参与传参的参数按照accsii排序（升序）
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
	 * 对字符串进行sha-1加密签名
	 * @param str 要加密的字符串
	 * @return 加密后的签名
	 */
	public static String sha1(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(str.getBytes());
			//获取字节数组  
            byte messageDigest[] = digest.digest();  
            // Create Hex String  
            StringBuffer hexString = new StringBuffer();  
            // 字节数组转换为 十六进制 数  
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
			throw new RuntimeException("对字符sha1加密失败");
		}
	}
	/**
	 * 
	 * @param url 请求地址
	 * @param ticket 操作凭证
	 * @param device_id 设备id
	 * @param openid 用户openid
	 * @return 操作是否成功
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
			throw new RuntimeException("请求绑定解绑失败");
		} 
		
	}
	/**
	 * 
	 * @param data 从卡上读到的数据
	 * @param f_userid 表编号
	 * @param gas 购气量
	 * @return 写卡数据和卡密码，用于校验
	 */
	public static String doPost(String data, Map<String, Object> map, String gas,
			String jine,String ljine,String factory,String cardid, JSONObject price, int times)
	{
		System.out.println("请求写卡开始");
//		创源使用
		String date = getDate("yyyyMMdd");
		System.out.println("获取日期为"+date);
		int pirce = (int)((Double)map.get("f_stair1price") *100);
		System.out.println(pirce);
		int f_metergasnums = (int)((Double)map.get("f_metergasnums") * 1);
		// 真蓝使用
		System.out.println("写卡写卡次数" + times);
		
		System.out.println("picre="+pirce+",f_metergasnums="+f_metergasnums);
		String url = "";
		JSONObject param = new JSONObject();
		try {
			if(factory.equals("ZhenLan")) {
				url = "http://127.0.0.1:8001/WriteGasCard"
						+ "/" + factory          //厂家
						+ "/" + map.get("kmm")  //卡密码，写卡后返回新密码map.get("kmm")
						+ "/" + cardid           //卡号
						+ "/" + map.get("dqdm")  //地区代码，从气表管理里取
						+ "/" + (int)(Double.parseDouble(gas))     //气量
						+ "/" + 0                //上次购气量，有些表需要传
						+ "/" + 0                //上上次购气量，有些表需要传
						+ "/" + times              //购气次数
						+ "/" + f_metergasnums //当前表累计购气量
						+ "/" + map.get("bjql")         //报警气量
						+ "/" + map.get("czsx")         //充值上限，可以在气表管理中设置
						+ "/" + map.get("tzed")        //透支额度，可以在气表管理中设置
						+ "/" + date         //售气日期，格式为YYYYMMDD
						+ "/" + date         //上次售气日期，格式为YYYYMMDD
						+ "/" + pirce      //旧单价，价格管理中取map.get("f_stair1price") 
						+ "/" + pirce       //新单价，价格管理中取
						+ "/" + date         //生效日期，价格管理中取
						+ "/" + 1;         //生效标记
				param.put("CardType", "1");
				param.put("operatetype", "1");
				param.put("opid", "1");
				param.put("CardData", data);
				param.put("stairprice1", price.getString("f_stair1price"));
				param.put("stairgas1", price.getString("f_stair1amount"));
				param.put("stairprice2", price.getString("f_stair2price"));
				param.put("stairgas2", "1000"); // 巩义现有两阶气价
				param.put("stairprice3", price.getString("f_stair2price"));// 三阶气价也用二阶气价
				param.put("stairgas3", "1000"); // 给一个固定值
				param.put("money", jine);
				param.put("totalmoney", (Integer.parseInt(ljine)+Integer.parseInt(jine)) + ""); // f_cumulativemoney
				System.out.println("真兰向卡服务写卡前请求url-->" + url);
				log.debug("真兰向卡服务写卡前请求url-->" + url);
				System.out.println("真兰向卡服务写卡前请求参数-->" + param.toString());
				log.debug("真兰向卡服务写卡前请求参数-->" + param.toString());
			}else if(factory.equals("ChuangYuan")) {
				url = "http://127.0.0.1:8001/WriteGasCard"
						+ "/" + factory          //厂家
						+ "/" + map.get("kmm")  //卡密码，写卡后返回新密码map.get("kmm")
						+ "/" + cardid           //卡号
						+ "/" + map.get("dqdm")  //地区代码，从气表管理里取
						+ "/" + gas              //气量
						+ "/" + 0                //上次购气量，有些表需要传
						+ "/" + 0                //上上次购气量，有些表需要传
						+ "/" + 0                //购气次数
						+ "/" + f_metergasnums //当前表累计购气量
						+ "/" + map.get("bjql")         //报警气量
						+ "/" + map.get("czsx")         //充值上限，可以在气表管理中设置
						+ "/" + map.get("tzed")        //透支额度，可以在气表管理中设置
						+ "/" + date         //售气日期，格式为YYYYMMDD
						+ "/" + date         //上次售气日期，格式为YYYYMMDD
						+ "/" + pirce      //旧单价，价格管理中取map.get("f_stair1price") 
						+ "/" + pirce       //新单价，价格管理中取
						+ "/" + date         //生效日期，价格管理中取
						+ "/" + 1;         //生效标记
				param.put("CardType", "1");
				param.put("operatetype", "1");
				param.put("opid", "1");
				param.put("CardData", data);
				System.out.println("创源向卡服务写卡前请求url-->" + url);
				log.debug("创源向卡服务写卡前请求url-->" + url);
				System.out.println("创源向卡服务写卡前请求参数-->" + param.toString());
				log.debug("创源向卡服务写卡前请求参数-->" + param.toString());
			}
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			StringEntity postEntity = new StringEntity(param.toString(),"UTF-8");
			postRequest.setEntity(postEntity);
			HttpResponse httpResponse = httpclient.execute(postRequest);
			HttpEntity entity=null;
			String result=null;
			System.out.println("返回码"+httpResponse.getStatusLine().getStatusCode());
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				entity = httpResponse.getEntity();
				result=EntityUtils.toString(entity, "UTF-8");
				System.out.println("写卡云服务返回数据："+result);
			}
			return  result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("请求云服务失败");
		}
	}
	
	/**
	 * 
	 * @param data 从卡上读到的数据
	 * @return 返回云服务解析的数据
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
			System.out.println("返回码"+httpResponse.getStatusLine().getStatusCode());
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				entity = httpResponse.getEntity();
				result=EntityUtils.toString(entity, "UTF-8");
				System.out.println("读卡云服务返回数据："+result);
			}
			return  result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("请求云服务失败");
		}
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String getDate(String str) {
		System.out.println("获取时间的格式为：" + str);
		SimpleDateFormat format = new SimpleDateFormat(str);
		System.out.println("format" + format);
		String s = format.format(new Date());
		System.out.println(s);
		return s;
	}
	
	public static Date getDate(String fm, String str) throws ParseException {
		System.out.println("获取时间的格式为：" + str);
		SimpleDateFormat format = new SimpleDateFormat(fm);
		return format.parse(str);
	}
}

