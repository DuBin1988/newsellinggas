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
		System.out.println("加载短信");
		init();
	}

	// static Logger log = Logger.getLogger(MianZhuSms.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public JSONObject sendsms(String phone, String msg, JSONObject attr) {
		// 链接服务
//		SmsClient smsClient = new SmsClient();
//		smsClient.start();
		// 启动线程，发送短信
		SubmitSms sms = new SubmitSms();
		sms.phone = phone;
		sms.message = msg;
		sms.start();
		return null;
	}

	public JSONObject lastMontQfyh(){
		JSONObject result = new JSONObject();
		try {
		//	String param = "{f_username:刘欢}";
			JSONObject attr = new JSONObject();
			
			String hql = "from t_userfiles";
			List list = hibernateTemplate.find(hql);
			for (Object obj : list) {
				// 获取电话号码及短信内容
				Map map = (Map) obj;
				String phone = map.get("f_phone").toString();
				String userid = map.get("f_userid").toString();
				
				String msg =  "【中民燃气】尊敬的天然气用户[#" + userid + "#]：您的上月天然气欠费金额为[#money#]，请于本月24日前到收费大厅缴纳气费，以免停气给您带来不便。退订回N";
				
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
		sms.sendsms("13201702256", "你好", null);
	}

	class SubmitSms extends Thread {
		public String phone = null;
		public String message = null;
		private Logger logger = Logger.getLogger(SubmitSms.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		public void run() {
			// 判断号码或者内容不能为空
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
