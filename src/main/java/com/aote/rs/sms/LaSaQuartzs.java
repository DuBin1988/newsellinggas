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
		// �ҳ�����δ���͵Ķ���
		String info = null;
		String hql = "from t_sms where f_state='δ��'";
		List list = hibernateTemplate.find(hql);
		// ��ÿһ���û������д߷�
		try {
			for (Object obj : list) {
				// ��ȡ�绰���뼰��������
				Map map = (Map) obj;
				String phone = map.get("f_phone") == null ? "" : map.get(
						"f_phone").toString();
				if (phone == null || phone.equals("")) {
					map.put("f_error", "����Ϊ��");
					map.put("f_state", "����");
					Date now = new Date();
					map.put("f_senddate", now);
					map.put("f_sendtime", now);
					this.hibernateTemplate.update(map);
					continue;
				}
				String content = map.get("f_content") == null ? "" : map.get(
						"f_content").toString();
				if (content == null || content.equals("")) {
					map.put("f_error", "��������Ϊ��");
					map.put("f_state", "����");
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
				map.put("f_state", "�ѷ�");
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
	 * ���ö���socke����
	 * @param json
	 * @throws Exception
	 */
	private void sendsocket(JSONObject json) throws Exception {
		// Ϊ�˼���������е��쳣��ֱ��������
		String host = "127.0.0.1"; // Ҫ���ӵķ����IP��ַ
		int port = 17777; // Ҫ���ӵķ���˶�Ӧ�ļ����˿�
		// �����˽�������
		Socket client = new Socket(host, port);
		// �������Ӻ�Ϳ����������д������
		Writer writer = new OutputStreamWriter(client.getOutputStream());
		writer.write(json.toString());
		writer.flush();// д���Ҫ�ǵ�flush
		writer.close();
		client.close();
	}

	@Override
	public JSONObject sendsms(String phone, String msg, JSONObject attr) {
		// TODO Auto-generated method stub
		return null;
	}
}
