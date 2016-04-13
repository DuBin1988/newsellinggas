package com.aote.rs.sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import message.sms.SUBMIT;

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
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import common.ComFun;
import common.SmsClient;

public class MianZhuSms implements ISms {

	// static Logger log = Logger.getLogger(MianZhuSms.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public JSONObject sendsms(String phone, String msg, JSONObject attr) {
		// ���ӷ���
		SmsClient smsClient = new SmsClient();
		smsClient.start();
		// �����̣߳����Ͷ���
		SubmitSms sms = new SubmitSms();
		sms.phone = phone;
		sms.message = msg;
		sms.start();
		return null;
	}

	public void send() {
		// �ҳ�����δ���͵Ķ���
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

				MianZhuSms sms = new MianZhuSms();
				sms.sendsms(phone, content, null);
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

	public static void main(String[] args) {
		MianZhuSms sms = new MianZhuSms();
		sms.sendsms("13689262869,17791214879", "���", null);
	}

	class SubmitSms extends Thread {
		public String phone = null;
		public String message = null;
		private Logger logger = Logger.getLogger(SubmitSms.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		public void run() {
			// �жϺ���������ݲ���Ϊ��
			if (phone == null || message == null || phone.equals("")
					|| message.equals("")) {
				logger.error("���͵ĵ绰����������ݲ���Ϊ�գ�");
				return;
			}
			// ������룬ѭ������
			String[] phones = phone.split(",");
			for (int i = 0; i < phones.length; i++) {
				try {
					SUBMIT mt = new SUBMIT();
					mt.setDestID(phones[i]); // �û�����
					mt.setServiceID("106573090670");// ����ͨ�����������ж���ҵ��
					mt.setMsgContent(message);// ��������
					String retVal = "1";
					while ("1".equals(retVal) || "2".equals(retVal)
							|| "3".equals(retVal)) {
						retVal = ComFun.sendSMS(mt);
						if ("1".equals(retVal)) {
							logger.error("the SubmitPool was full.");
						} else if ("2".equals(retVal)) {
							logger.error("û�н�������");
						} else if ("3".equals(retVal)) {
							logger.error("�������ƴ���");
							Thread.sleep(1000);
						} else {
							logger.info("��ҵ������Ϣ��ʶ [" + retVal + "]��" + "Ŀ�ĺ��� ["
									+ mt.getDestID() + "]��" + "�������� ["
									+ mt.getMsgContent() + "]");
						}
					}
				} catch (Exception e) {
					logger.error(this, e);
				}
			}
		}
	}
}
