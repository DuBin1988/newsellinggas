package com.aote.rs.sms;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.util.BeanUtil;
import com.aote.rs.util.JsonTransfer;

/**
 * ���ŷ���
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
			// ���ģ������
			String msg = "";
			msg = getSmsTemplateByName(template);
			// �滻����
			JSONObject p = new JSONObject(param);
			Iterator iter = p.keys();
			while(iter.hasNext()){
				String key = (String)iter.next();
				msg=msg.replace("#"+key+"#", p.getString(key));
			}
			msg = "2" + msg;
			// ������õĶ���ʵ����
			MianZhuSms sms = (MianZhuSms) BeanUtil.getBean(MianZhuSms.class);
			JSONObject attr = new JSONObject();
			result = sms.sendsms(phone, msg, attr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	/**
	 * ���ݶ���ģ�����������
	 * @param name
	 * @return
	 */
	public String getSmsTemplateByName(String name) {
		String result = "";
		List list = this.hibernateTemplate.find("from t_smstemplate  where f_name ='" + name + "'");
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = map.get("f_content").toString();
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
