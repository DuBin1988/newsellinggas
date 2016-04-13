package com.aote.rs.sms;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class LaSaQuartzs implements ISms{
	/**
	 * @param args
	 */
	@Autowired
	private HibernateTemplate hibernateTemplate;

	public void send() {
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
				JSONObject json = new JSONObject();
				json.put("phone", phone);
				json.put("content", content);
				sendsocket(json);
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

	/**
	 * 调用短信socke发送
	 * @param json
	 * @throws Exception
	 */
	private void sendsocket(JSONObject json) throws Exception {
		// 为了简单起见，所有的异常都直接往外抛
		String host = "127.0.0.1"; // 要连接的服务端IP地址
		int port = 17777; // 要连接的服务端对应的监听端口
		// 与服务端建立连接
		Socket client = new Socket(host, port);
		// 建立连接后就可以往服务端写数据了
		Writer writer = new OutputStreamWriter(client.getOutputStream());
		writer.write(json.toString());
		writer.flush();// 写完后要记得flush
		writer.close();
		client.close();
	}

	@Override
	public JSONObject sendsms(String phone, String msg, JSONObject attr) {
		// TODO Auto-generated method stub
		return null;
	}
}
