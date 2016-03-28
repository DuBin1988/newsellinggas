package com.aote.rs.sms;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.sms.MianZhuSms.SubmitSms;
import com.mascloud.sdkclient.Client;

public class LaSaSms {
	public LaSaSms(){
		try {

			final Client client = Client.getInstance();
			client.login("http://mas.ecloud.10086.cn/app/sdk/login", "lasaSMS",
			 "lasaSMS","拉萨市暖心热力有限责任公司");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public  void send() {
		SubmitSms sms = new SubmitSms(hibernateTemplate);
		sms.start();
		System.out.println("send");
	}
	
	class SubmitSms extends Thread {
		public SubmitSms(HibernateTemplate hibernateTemplate) {
			this.hibernateTemplate=hibernateTemplate;
		}
		@Autowired
		private HibernateTemplate hibernateTemplate;
		public void run() {
			try {
				final Client client = Client.getInstance();
				// 正式环境IP，登录验证URL，用户名，密码，集团客户名称
				//client.login("http://mas.ecloud.10086.cn/app/sdk/login",
				//		"SDK账号名称（不是页面端账号）", "密码", "集团客户名称");
				// 测试环境IP
				
				client.login("http://mas.ecloud.10086.cn/app/sdk/login", "lasaSMS",
				 "lasaSMS","拉萨市暖心热力有限责任公司");
				
				// 找出所有未发送的短信
				String hql = "from t_sms where f_state='未发'";
				List list = hibernateTemplate.find(hql);
				
				for (Object obj : list) {
					// 获取电话号码及短信内容
					Map map = (Map) obj;
					String phone = map.get("f_phone").toString();
					String content = map.get("f_content").toString();
					
					int sendResult = client. sendDSMS (new String[] {phone},
							content, "",  1,"46feYuwf", UUID.randomUUID().toString(),true);
					
					switch (sendResult) {
					case 1:
						map.put("f_state", "已发");
						break;
					case 101:
						map.put("f_state", "出错");
						map.put("f_error", "短信内容为空");
						break;
					case 102:
						map.put("f_state", "出错");
						map.put("f_error", "号码数组为空");
						break;
					case 103:
						map.put("f_state", "出错");
						map.put("f_error", "号码数组为空数组");
						break;
					case 104:
						map.put("f_state", "出错");
						map.put("f_error", "批次短信的号码中存在非法号码， SDK带有号码的验证处理");
						break;
					case 105:
						map.put("f_state", "出错");
						map.put("f_error", "未进行身份认证或认证失败，用户请确认输入的用户名，密码和企业名是否正确");
						break;
					case 106:
						map.put("f_state", "出错");
						map.put("f_error", "网关签名为空，用户需要填写网关签名编号");
						break;
					case 107:
						map.put("f_state", "出错");
						map.put("f_error", "其他错误");
						break;
					case 108:
						map.put("f_state", "出错");
						map.put("f_error", "JMS异常，需要联系移动集团维护人员");
						break;
					case 109:
						map.put("f_state", "出错");
						map.put("f_error", "批次短信号码中存在重复号码");
						break;				
					default:
						break;
					}								
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
	public static void main(String[] args) {
		LaSaSms l=new LaSaSms();
		//l.send();
	}
}
