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
	//定义rewrite方法，处理回写
	@GET
	@Path("{userid}")
	public String Rewrite(@PathParam("userid") String userid
			) {
		try{
		//根据用户编号，找出最后一条购气记录，用户回写
		String sql = "select f_upbuynum,f_userid,f_premetergasnums,f_beginfee,f_finallybought,f_finabuygasdate,f_initcardsellgas,id from t_sellinggas s  left join " +
"(select MAX(id) d from t_sellinggas where f_userid = '"+ userid +"')m on s.id = m.d where s.id = m.d";
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql);
		List<Map<String, Object>> list = this.hibernateTemplate.executeFind(sqlCall);
		//取出收费记录
		Map<String, Object> sellinfo = (Map<String, Object>) list.get(0);
		//从售气记录中取出用于回写的值
		BigDecimal f_upbuynum = new BigDecimal(sellinfo.get("f_upbuynum")+"");
		BigDecimal f_premetergasnums = new BigDecimal(sellinfo.get("f_premetergasnums")+"");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date f_beginfee = sdf.parse(sellinfo.get("f_beginfee")+"");
		BigDecimal f_finallybought = new BigDecimal(sellinfo.get("f_finallybought")+"");
		String initcardsellgas = sellinfo.get("f_initcardsellgas")+"";
		BigDecimal id = new BigDecimal(sellinfo.get("id").toString());
		BigDecimal kid = new BigDecimal(sellinfo.get("kid").toString());
		Date f_finabuygasdate = sdf.parse(sellinfo.get("f_finabuygasdate")+"");
			//回写用户档案
		Date d = new Date();
			String updateUserinfo = "update t_userfiles set f_cumulativepurchase = '"+f_upbuynum+"', f_metergasnums = '"+f_premetergasnums+"', " +
					"f_beginfee = '"+ f_beginfee +"', f_finallybought = '"+f_finallybought+"', f_finabuygasdate = '"+f_finabuygasdate+"', f_initcardsellgas = '"+initcardsellgas+"'," +
							"f_times = f_times - 1 where f_userid = '" + userid + "' ";
			hibernateTemplate.bulkUpdate(updateUserinfo);
		//回写售气记录
		String updateSellinfo = "update t_sellinggas set f_payfeevalid = '无效' where id = '"+id+"'";
		hibernateTemplate.bulkUpdate(updateSellinfo);
		return "";
	}catch (Exception ex) {
		// 登记异常信息
		log.error(ex.getMessage());
		throw new WebApplicationException(401);
	}
}
	
	// 执行sql查询
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
