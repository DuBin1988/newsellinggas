package com.aote.rs.sms;

import java.text.SimpleDateFormat;

import message.sms.SUBMIT;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import common.ComFun;
import common.SmsClient;

public class MianZhuSms implements ISms {

	public MianZhuSms() {
		System.out.println("���ض���");
		init();
	}

	// static Logger log = Logger.getLogger(MianZhuSms.class);

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

	SmsClient smsClient;

	@Override
	public void init() {
		smsClient = new SmsClient();
		smsClient.start();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		smsClient.stop();
	}
}
