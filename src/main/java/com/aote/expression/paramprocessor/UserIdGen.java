package com.aote.expression.paramprocessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.expression.Param;

//用户编号产生器   根据小区编号从数据库中查找当前最大编号加1 返回
public class UserIdGen implements ParamProcessor {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	public String process(Param param) throws NoFitValueException {
		String result = "";
		Map paramData = param.getParams();
		String areaNum = (String) paramData.get("area");
		String length = (String) paramData.get("length");
		final String sql = "select max(to_number(nvl(substr(f_userid,5,8),0)))+1 from t_userfiles where f_userid like '"
				+ areaNum + "%' group by   substr(f_userid,0,4)";
		List list = (List) hibernateTemplate.execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				return session.createSQLQuery(sql).list();
			}
		});
		if (list.size() == 0) {
			result = "1";
		} else {
			BigDecimal bd = (BigDecimal) list.get(0);
			result =  bd.toString();
		}
		// 根据设定的长度补零
		// 长度不足得载编号前加0
		int defLength = Integer.parseInt(length);
		int numLength = result.length();
		for (int i = 0; i < (defLength - numLength); i++) {
			result = "0" + result;
		}
		return areaNum+result;
	}

}
