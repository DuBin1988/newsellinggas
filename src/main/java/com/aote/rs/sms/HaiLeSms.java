package com.aote.rs.sms;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HaiLeSms{
	/**
	 * @param args
	 */
	@Autowired
	private HibernateTemplate hibernateTemplate;

	public  void send() {
		// 找出所有未发送的短信
		String info = null;
		String hql = "from t_sms where f_state='未发'";
		List list = hibernateTemplate.find(hql);
		// 对每一个用户，进行催费
		try {
			for (Object obj : list) {
				// 获取电话号码及短信内容
				Map map = (Map) obj;
				String phone = map.get("f_phone") == null ? "" : map.get(
						"f_phone").toString();
				if (phone == null || phone.equals("")) {
					map.put("f_error", "号码为空");
					map.put("f_state", "出错");
					Date now = new Date();
					map.put("f_senddate", now);
					map.put("f_sendtime", now);
					this.hibernateTemplate.update(map);
					continue;
				}
				String content = map.get("f_content") == null ? "" : map.get(
						"f_content").toString();
				if (content == null || content.equals("")) {
					map.put("f_error", "短信内容为空");
					map.put("f_state", "出错");
					Date now = new Date();
					map.put("f_senddate", now);
					map.put("f_sendtime", now);
					this.hibernateTemplate.update(map);
					continue;
				}

				HttpClient httpclient = new HttpClient();
				PostMethod post = new PostMethod("https://smsapi.ums86.com:9600/sms/Api/Send.do");//
				post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"gbk");
				post.addParameter("SpCode", "225318");
				post.addParameter("LoginName", "hlrq");
				post.addParameter("Password", "hlrq15006739955");
				post.addParameter("MessageContent", content);
				post.addParameter("UserNumber", phone);
				post.addParameter("SerialNumber", "");
				post.addParameter("ScheduleTime", "");
				post.addParameter("f", "1");
				
				httpclient.executeMethod(post);
				info = new String(post.getResponseBody(),"gbk");

				map.put("f_state", "已发");
				Date now = new Date();
				map.put("f_senddate", now);
				map.put("f_sendtime", now);
				this.hibernateTemplate.update(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}