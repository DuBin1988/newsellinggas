package com.aote.rs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

//卡服务
@Path("sms")
@Scope("prototype")
@Component
public class SMSService {
	static Logger log = Logger.getLogger(SMSService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	// 测试短信发送
	@GET
	public String test() {
		try {
			// 产生post请求
			HttpPost post = new HttpPost(
					"http://hb.ums86.com:8899/sms/Api/Send.do");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("SpCode", "206655"));
			nvps.add(new BasicNameValuePair("LoginName", "mc_trq"));
			nvps.add(new BasicNameValuePair("Password", "trq1001"));
			nvps.add(new BasicNameValuePair("MessageContent",
				"尊敬的张三：您本次表码123，气量24，气费30.25元，本次余额12.35，请及时缴费。详电2993016"));
			nvps.add(new BasicNameValuePair("UserNumber", "18681830185"));
			nvps.add(new BasicNameValuePair("SerialNumber", ""));
			nvps.add(new BasicNameValuePair("ScheduleTime", ""));
			nvps.add(new BasicNameValuePair("f", "1"));
			post.setEntity(new UrlEncodedFormEntity(nvps, "GB2312"));
			// 执行post请求
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			// 获取返回结果
			HttpEntity entity = response.getEntity();
			byte[] buf = EntityUtils.toByteArray(entity);
			String result = new String(buf, "GB2312");
			log.debug(result);
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// 从短信发送表里取出所有未发送的短信进行发送
	@Path("/send")
	@GET
	public String send() {
		// 找出所有未发送的短信
		String hql = "from t_sms where f_state='未发'";
		List list = hibernateTemplate.find(hql);
		// 对每一个用户，进行催费
		try {
			for (Object obj : list) {
				// 获取电话号码及短信内容
				Map map = (Map) obj;
				String phone = map.get("f_phone") == null ? "" : map.get("f_phone").toString();
				if(phone ==null || phone.equals(""))
				{
					continue;
				}
				String content = map.get("f_content") == null ? "" : map.get("f_content").toString();  
				if(content== null ||content.equals(""))
				{
					continue;
				}
				
				// 产生post请求
				HttpPost post = new HttpPost(
						"http://hb.ums86.com:8899/sms/Api/Send.do");
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("SpCode", "206655"));
				nvps.add(new BasicNameValuePair("LoginName", "mc_trq"));
				nvps.add(new BasicNameValuePair("Password", "trq1001"));
				nvps.add(new BasicNameValuePair("MessageContent", content));
				nvps.add(new BasicNameValuePair("UserNumber", phone));
				nvps.add(new BasicNameValuePair("SerialNumber", ""));
				nvps.add(new BasicNameValuePair("ScheduleTime", ""));
				nvps.add(new BasicNameValuePair("f", "1"));
				post.setEntity(new UrlEncodedFormEntity(nvps, "GB2312"));
				// 执行post请求
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(post);
				// 获取返回结果
				HttpEntity entity = response.getEntity();
				byte[] buf = EntityUtils.toByteArray(entity);
				String result = new String(buf, "GB2312");
				Map mresult = urlToMap(result);
				// 对于催费成功的用户，更新标记
				String ret = mresult.get("result").toString();
				if(ret.equals("0")) {
					map.put("f_state", "已发");
					Date now = new Date(); 
					map.put("f_senddate", now);
					map.put("f_sendtime", now);
				} 
				// 对于催费不成功的用户，返回错误内容
				else {
					map.put("f_state", "出错");
					String desc = mresult.get("description").toString();
					map.put("f_error", desc);
				}
				this.hibernateTemplate.update(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "";
	}
	
	//把result=&description=错误描述&faillist=失败号码列表形式的字符出解析为Map
	private Map urlToMap(String url) {
		Map result = new HashMap();
		String[] strs = url.split("&");
		for(String str : strs) {
			String[] values = str.split("=");
			if(values.length == 2) {
				result.put(values[0], values[1]);
			}
		}
		return result;
	}
}
