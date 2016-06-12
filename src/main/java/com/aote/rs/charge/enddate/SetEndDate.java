package com.aote.rs.charge.enddate;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.exception.ResultException;

/**
 * 根据滞纳金日期设置功能，获得该分公司的滞纳金日期
 * 
 * @author Administrator
 * 
 */
public class SetEndDate implements IEndDate {

	@Override
	public Calendar enddate(String userinfoid, HibernateTemplate hibernateTemplate,
			Map<String, Object> loginuser) throws Exception {
		// TODO Auto-generated method stub
		// 获得分公司
		String f_filiale = loginuser.get("f_fengongsi").toString();
		// 获得用户类型
		String hql = "from t_userinfo where f_filiale='" + f_filiale
				+ "' and f_userid='" + userinfoid + "'";
		List<Object> list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("没有找到户编号：" + userinfoid + "的信息！");
		}
		Map<String, Object> hu = (Map<String, Object>) list.get(0);
		String usertype = hu.get("f_usertype") + "";
		if (usertype == "" || usertype.equals("")) {
			throw new ResultException("没有找到户编号：" + userinfoid
					+ "的用户类型 (f_usertype)信息！");
		}
		// 查询滞纳金日期设置表是否有该分公司的设置
		hql = "from t_zhinajindate where f_usertype='" + usertype
				+ "' and f_filiale='" + f_filiale + "'";
		list = hibernateTemplate.find(hql);
		if (list.size() == 0) {
			throw new ResultException("没有找到分公司名：" + f_filiale + ",用户类型: "
					+ usertype + "的滞纳金设置信息！");
		}
		Calendar cal = Calendar.getInstance();
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		String type = map.get("f_type").toString();
		if (type.equals("按天推后")) {
			String day = map.get("f_day").toString();
			cal.add(Calendar.DATE, Integer.parseInt(day));
		} else if (type.equals("按月推后")) {
			String month = map.get("f_month").toString();
			String day = map.get("f_monthday").toString();
			cal.add(Calendar.MONTH, Integer.parseInt(month));
			cal.set(Calendar.DATE, Integer.parseInt(day));
		} else {
			throw new ResultException("滞纳金设置没有找到类型为：" + type + "的处理程序！");
		}
		return cal;
	}

}
