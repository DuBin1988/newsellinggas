package com.aote.rs.sms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * 短信服务
 * 
 * @author Administrator
 *
 */
@Path("sms")
@Scope("prototype")
@Component
public class SmsService {

	static Logger log = Logger.getLogger(SmsService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@GET
	@Path("/send/{param}/{phone}/{templatename}")
	public JSONObject sendTemplate(@PathParam("param") String param,
			@PathParam("phone") String phone,
			@PathParam("templatename") String template) {
		JSONObject result = new JSONObject();
		try {
			//获取总开关状态
			Map<String, Object> flagM = getSmsTemplateByName("短信功能开关");
			
			// 获得模板内容
			Map<String, Object> msgM = null;
			msgM = getSmsTemplateByName(template);
			String msg = msgM.get("f_content").toString();
			// 如果模板为空，不发送
			if (msg == null) {
				result.put("success", "模板未启用不发送");
				return result;
			}
			// 替换参数
			JSONObject p = new JSONObject(param);
			Iterator iter = p.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				msg = msg.replace("#" + key + "#", p.getString(key));
			}

			// 短信信息保存到 sms 表中。
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String time = df.format(date);
			Date createdate = (Date)df.parse(time);
			Map<String, Object> sms = new HashMap<String, Object>();
			sms.put("f_username", p.get("f_username").toString()); // 用户姓名
			sms.put("f_content", msg);// 短信内容
			sms.put("f_phone", phone);// 电话	
			sms.put("f_templatename", template);
			if(flagM.equals("关闭") || msgM.get("f_state").toString().equals("关闭")){
			//如果短息功能开关关闭，或者短信模板关闭
				sms.put("f_state", "待发");//短信状态
			}else {
				sms.put("f_state", "未发");// 短信状态				
			}
			sms.put("f_createdate", createdate);// 生成日期
			hibernateTemplate.save("t_sms", sms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@GET
	@Path("/send/{f_username}/{f_phone}/{f_content}/{f_templatename}")
	public JSONObject sendUser(@PathParam("f_username") String f_username,
			@PathParam("f_phone") String f_phone,
			@PathParam("f_content") String f_content,
			@PathParam("f_templatename") String f_templatename
			) {
		JSONObject result = new JSONObject();
		try {
			
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String time = df.format(date);
			Date createdate = (Date)df.parse(time);
			Map<String, Object> sms = new HashMap<String, Object>();
			sms.put("f_username", f_username); // 用户姓名
			sms.put("f_content", f_content);// 短信内容
			sms.put("f_phone", f_phone);// 电话	
			sms.put("f_templatename", f_templatename);	//模板名	
			sms.put("f_state", "未发");//短信状态			
			sms.put("f_createdate", createdate);// 生成日期
			hibernateTemplate.save("t_sms", sms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据短信模板名获得内容
	 * 
	 * @param name
	 * @return
	 */
	public Map<String, Object> getSmsTemplateByName(String name) {
		List list = this.hibernateTemplate
				.find("from t_smstemplate  where f_name ='" + name + "'");
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		return map;
	}

}
