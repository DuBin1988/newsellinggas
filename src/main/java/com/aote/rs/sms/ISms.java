package com.aote.rs.sms;

import org.codehaus.jettison.json.JSONObject;

/**
 * 定时自动发送短信接口
 * @author Administrator
 *
 */
public interface ISms {

	/**
	 * 发送短信接口，
	 * @param phone 电话号码，多个电话号码用逗号隔开
	 * @param msg 发送信息
	 * @param attr 其他参数
	 * @return 返回json对象
	 */
	public JSONObject sendsms(String phone,String msg,JSONObject attr);
	
	
	public void init();
	
	public void destroy();
}
