package com.aote.rs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;


@Path("returns")
@Scope("prototype")
@Component

public class Returns {
	static Logger log = Logger.getLogger(Returns.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;
	//����rewrite�����������д
	@GET
	@Path("{userid}")
	public String Rewrite(@PathParam("userid") String userid
			) {
		try{
		//�����û���ţ��ҳ����һ��������¼���û���д
		String sql = "select f_upbuynum,f_userid,f_premetergasnums,f_beginfee,f_finallybought,f_finabuygasdate,f_initcardsellgas,id from t_sellinggas s  left join " +
"(select MAX(id) d from t_sellinggas where f_userid = '"+ userid +"')m on s.id = m.d where s.id = m.d";
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> list = this.hibernateTemplate.executeFind(sqlCall);
		//ȡ���շѼ�¼
		Map<String, Object> sellinfo = (Map<String, Object>) list.get(0);
		//��������¼��ȡ�����ڻ�д��ֵ
		BigDecimal f_upbuynum = new BigDecimal(sellinfo.get("f_upbuynum")+"");
		BigDecimal f_premetergasnums = new BigDecimal(sellinfo.get("f_premetergasnums")+"");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date f_beginfee = sdf.parse(sellinfo.get("f_beginfee")+"");
		BigDecimal f_finallybought = new BigDecimal(sellinfo.get("f_finallybought")+"");
		String initcardsellgas = sellinfo.get("f_initcardsellgas")+"";
		BigDecimal id = new BigDecimal(sellinfo.get("id").toString());
		BigDecimal kid = new BigDecimal(sellinfo.get("kid").toString());
		Date f_finabuygasdate = sdf.parse(sellinfo.get("f_finabuygasdate")+"");
			//��д�û�����
		Date d = new Date();
			String updateUserinfo = "update t_userfiles set f_cumulativepurchase = '"+f_upbuynum+"', f_metergasnums = '"+f_premetergasnums+"', " +
					"f_beginfee = '"+ f_beginfee +"', f_finallybought = '"+f_finallybought+"', f_finabuygasdate = '"+f_finabuygasdate+"', f_initcardsellgas = '"+initcardsellgas+"'," +
							"f_times = f_times - 1 where f_userid = '" + userid + "' ";
			hibernateTemplate.bulkUpdate(updateUserinfo);
		//��д������¼
		String updateSellinfo = "update t_sellinggas set f_payfeevalid = '��Ч' where id = '"+id+"'";
		hibernateTemplate.bulkUpdate(updateSellinfo);
		return "";
	}catch (Exception ex) {
		// �Ǽ��쳣��Ϣ
		log.error(ex.getMessage());
		throw new WebApplicationException(401);
	}
}
	
	// ִ��sql��ѯ
	class HibernateSQLCall implements HibernateCallback {
		String sql;

		public HibernateSQLCall(String sql) {
			this.sql = sql;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List result = q.list();
			return result;
		}
	}
}
