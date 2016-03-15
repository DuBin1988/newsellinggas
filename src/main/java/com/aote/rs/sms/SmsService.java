package com.aote.rs.sms;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.context.annotation.Scope;

import com.aote.rs.util.BeanUtil;

/**
 * ���ŷ���
 * 
 * @author Administrator
 *
 */
@Path("/sms")
@Scope("prototype")
public class SmsService {

	@Path("/send/{phone}/{templatename}")
	@POST
	public JSONObject sendTemplate(String param,
			@QueryParam("phone") String phone,
			@QueryParam("templatename") String template) {
		JSONObject result = new JSONObject();
		try {
			//���ģ������
			String msg ="";
			//msg = getSmsTemplateByName(template);
			// ������õĶ���ʵ����
			ISms sms = (ISms) BeanUtil.getBean(ISms.class);
			JSONObject attr = new JSONObject();
			result = sms.sendsms(phone, msg, attr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Path("/send/{phone}/{msg}")
	public JSONObject sendmsg(@QueryParam("phone") String phone,
			@QueryParam("msg") String msg) {
		JSONObject result = new JSONObject();
		try {
			// ������õĶ���ʵ����
			ISms sms = (ISms) BeanUtil.getBean(ISms.class);
			JSONObject attr = new JSONObject();
			result = sms.sendsms(phone, msg, attr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
