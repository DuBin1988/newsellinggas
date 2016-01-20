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

//������
@Path("sms")
@Scope("prototype")
@Component
public class SMSService {
	static Logger log = Logger.getLogger(SMSService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	// ���Զ��ŷ���
	@GET
	public String test() {
		try {
			// ����post����
			HttpPost post = new HttpPost(
					"http://hb.ums86.com:8899/sms/Api/Send.do");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("SpCode", "206655"));
			nvps.add(new BasicNameValuePair("LoginName", "mc_trq"));
			nvps.add(new BasicNameValuePair("Password", "trq1001"));
			nvps.add(new BasicNameValuePair("MessageContent",
				"�𾴵������������α���123������24������30.25Ԫ���������12.35���뼰ʱ�ɷѡ����2993016"));
			nvps.add(new BasicNameValuePair("UserNumber", "18681830185"));
			nvps.add(new BasicNameValuePair("SerialNumber", ""));
			nvps.add(new BasicNameValuePair("ScheduleTime", ""));
			nvps.add(new BasicNameValuePair("f", "1"));
			post.setEntity(new UrlEncodedFormEntity(nvps, "GB2312"));
			// ִ��post����
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			// ��ȡ���ؽ��
			HttpEntity entity = response.getEntity();
			byte[] buf = EntityUtils.toByteArray(entity);
			String result = new String(buf, "GB2312");
			log.debug(result);
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// �Ӷ��ŷ��ͱ���ȡ������δ���͵Ķ��Ž��з���
	@Path("/send")
	@GET
	public String send() {
		// �ҳ�����δ���͵Ķ���
		String hql = "from t_sms where f_state='δ��'";
		List list = hibernateTemplate.find(hql);
		// ��ÿһ���û������д߷�
		try {
			for (Object obj : list) {
				// ��ȡ�绰���뼰��������
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
				
				// ����post����
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
				// ִ��post����
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(post);
				// ��ȡ���ؽ��
				HttpEntity entity = response.getEntity();
				byte[] buf = EntityUtils.toByteArray(entity);
				String result = new String(buf, "GB2312");
				Map mresult = urlToMap(result);
				// ���ڴ߷ѳɹ����û������±��
				String ret = mresult.get("result").toString();
				if(ret.equals("0")) {
					map.put("f_state", "�ѷ�");
					Date now = new Date(); 
					map.put("f_senddate", now);
					map.put("f_sendtime", now);
				} 
				// ���ڴ߷Ѳ��ɹ����û������ش�������
				else {
					map.put("f_state", "����");
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
	
	//��result=&description=��������&faillist=ʧ�ܺ����б���ʽ���ַ�������ΪMap
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
