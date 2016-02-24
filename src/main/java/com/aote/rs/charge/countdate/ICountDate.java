package com.aote.rs.charge.countdate;

import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 计算阶梯开始日期和结束日期的接口
 * @author Administrator
 *
 */
public interface ICountDate {
	String startdate(String userid,HibernateTemplate hibernateTemplate);
	String enddate(String userid,HibernateTemplate hibernateTemplate);
}