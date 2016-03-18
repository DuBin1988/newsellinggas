package com.aote.rs.sms;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import message.sms.SUBMIT;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.browsesoft.oa.InitAttributeGather;

import common.ComFun;
import common.SmsClient;

public class MianZhuSms implements ISms {
	
	public MianZhuSms() {
		System.out.println("���ض���");
		init();
	}

	// static Logger log = Logger.getLogger(MianZhuSms.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public JSONObject sendsms(String phone, String msg, JSONObject attr) {
		// ���ӷ���
//		SmsClient smsClient = new SmsClient();
//		smsClient.start();
		// �����̣߳����Ͷ���
		SubmitSms sms = new SubmitSms();
		sms.phone = phone;
		sms.message = msg;
		sms.start();
		return null;
	}

	public JSONObject lastMontQfyh(){
		JSONObject result = new JSONObject();
		try {
		//	String param = "{f_username:����}";
			JSONObject attr = new JSONObject();
			
			String hql = "from t_userfiles";
			List list = hibernateTemplate.find(hql);
			for (Object obj : list) {
				// ��ȡ�绰���뼰��������
				Map map = (Map) obj;
				String phone = map.get("f_phone").toString();
				String userid = map.get("f_userid").toString();
				
				String msg =  "������ȼ�����𾴵���Ȼ���û�[#" + userid + "#]������������Ȼ��Ƿ�ѽ��Ϊ[#money#]�����ڱ���24��ǰ���շѴ����������ѣ�����ͣ�������������㡣�˶���N";
				
				result = sendsms(phone, msg, attr);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
	}

	public static void main(String[] args) {
		MianZhuSms sms = new MianZhuSms();
		sms.sendsms("13201702256", "���", null);
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
	
	SmsClient smsClient;
	
	@Override
	public void init(){
		smsClient = new SmsClient();
		smsClient.start();
	}
	
	@Override
	public void destroy(){
		smsClient.stop();
	}
}
