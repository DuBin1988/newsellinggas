package com.aote.rs.sms;

import java.text.SimpleDateFormat;

import message.sms.SUBMIT;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import common.ComFun;
import common.SmsClient;

public class MianZhuSms implements ISms {

	// static Logger log = Logger.getLogger(MianZhuSms.class);

	@Override
	public JSONObject sendsms(String phone, String msg, JSONObject attr) {
		// 链接服务
		SmsClient smsClient = new SmsClient();
		smsClient.start();
		// 启动线程，发送短信
		SubmitSms sms = new SubmitSms();
		sms.phone = phone;
		sms.message = msg;
		sms.start();
		return null;
	}

	public static void main(String[] args) {
		MianZhuSms sms = new MianZhuSms();
		sms.sendsms("13689262869,17791214879", "你好", null);
	}

	class SubmitSms extends Thread {
		public String phone = null;
		public String message = null;
		private Logger logger = Logger.getLogger(SubmitSms.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		public void run() {
			//判断号码或者内容不能为空
			if (phone == null || message == null || phone.equals("")
					|| message.equals("")) {
				logger.error("发送的电话号码或者内容不能为空！");
				return;
			}
			// 多个号码，循环发送
			String[] phones = phone.split(",");
			for (int i = 0; i < phones.length; i++) {
				try {
					SUBMIT mt = new SUBMIT();
					mt.setDestID(phones[i]); // 用户号码
					mt.setServiceID("106573090670");// 下行通道。（或下行短信业务）
					mt.setMsgContent(message);// 短信内容
					String retVal = "1";
					while ("1".equals(retVal) || "2".equals(retVal)
							|| "3".equals(retVal)) {
						retVal = ComFun.sendSMS(mt);
						if ("1".equals(retVal)) {
							logger.error("the SubmitPool was full.");
						} else if ("2".equals(retVal)) {
							logger.error("没有建立连接");
						} else if ("3".equals(retVal)) {
							logger.error("流量控制错误");
							Thread.sleep(1000);
						} else {
							logger.info("企业下行消息标识 [" + retVal + "]，" + "目的号码 ["
									+ mt.getDestID() + "]，" + "短信内容 ["
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
