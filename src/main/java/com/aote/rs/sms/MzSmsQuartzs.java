package com.aote.rs.sms;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.util.JsonTransfer;
import com.aote.rs.sms.SmsService;

public class MzSmsQuartzs{
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	//预存用户余额不足20
	public void zhyeLt20()
	{
		String sql = "select f_userid, f_username, f_phone from t_userinfo where f_zhye <20 and f_zhye >=0";
		String templatename = "预存用户余额不足";
		smsQuartz(sql, templatename);
	}
	
	//上月欠费用户
	public void lastMonthQf(){
		String sql = "select f_username ,f_phone , SUM(money) as money "
					+ "from "
					+"( select u2.f_username,u.f_phone,(f.oughtfee-u2.f_zhye) as money ,  u.f_userinfoid "
				  + "from (select f_userid,sum(oughtfee) as oughtfee from t_handplan "
				  + "where f_state  =  '已抄表' and shifoujiaofei  =  '否'  and   convert(char(7),f_inputdate,120)=convert(char(7), dateadd(month,-1,getdate()) ,120)" 
				  +"group by f_userid ) f  "
				  + "left join t_userfiles u  "
				  + "on f.f_userid  = u.f_userid "
				  +"left join t_userinfo u2 "
				  +  "on u.f_userinfoid = u2.f_userid) t"
				  +" group by f_username ,f_phone ";
		String templatename = "上月欠费用户催费短信";
		smsQuartz(sql, templatename);
	}
	
	//本月商业欠费
	public void thisMonthSy(){
		String sql =  "select f_username ,f_phone , SUM(money) as money "
				+" from "
				+"( select u2.f_username,u.f_phone,(f.oughtfee-u.f_zhye) as money ,  u.f_userinfoid ,u.f_stairtype "
			   +"from (select f_userid,sum(oughtfee) as oughtfee from t_handplan "
			   +"where f_state  =  '已抄表' and shifoujiaofei  =  '否' and f_stairtype like '%商业%' and convert(char(7),f_inputdate ,120)=convert(char(7),getdate(),120) "
			  +"group by f_userid ) f  "
			  +" left join t_userfiles u  "
			  +" on f.f_userid  = u.f_userid "
			  + "left join t_userinfo u2 "
			  + "  on u.f_userinfoid = u2.f_userid)t "
			  +" where t.f_stairtype like '%商业%' group by f_username ,f_phone ";
		String templatename = "本月商用未缴用户";
		smsQuartz(sql, templatename);
	}
	
	public void  smsQuartz(String thissql, String templatename){
		JSONObject result = new JSONObject();
		SmsService smsService = new SmsService();
		try {
			smsService.setHibernateTemplate(hibernateTemplate);   //new时候应该设置模板
			JSONObject res = new JSONObject();
			String param;
			final String sql = thissql;
			List<Map<String, Object>> list = (List<Map<String, Object>>) hibernateTemplate
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							Query q = session.createSQLQuery(sql);
							q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
							List result = q.list();
							return result;
						}
					});
			Iterator it = list.iterator();
			while(it.hasNext()){
				Map<String, Object> map = (Map<String, Object>) it.next();
				res = (JSONObject) new JsonTransfer().MapToJson(map);
				param = res.toString();
				JSONObject attr = new JSONObject();
				result = smsService.sendTemplate(param, map.get("f_phone").toString(), templatename);
			} 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}