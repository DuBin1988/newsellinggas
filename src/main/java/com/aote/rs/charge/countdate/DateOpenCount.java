package com.aote.rs.charge.countdate;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 根据开始日期结算阶梯的开始日期和结束日期
 * @author Administrator
 *
 */
public class DateOpenCount implements ICountDate {

	@Override
	public String startdate(String userid, HibernateTemplate hibernateTemplate) {
		return "2015-01-01";
	}

	@Override
	public String enddate(String userid, HibernateTemplate hibernateTemplate) {
		return "2016-11-30";
	}

}