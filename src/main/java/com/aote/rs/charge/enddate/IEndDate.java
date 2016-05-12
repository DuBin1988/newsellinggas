package com.aote.rs.charge.enddate;

import java.util.Calendar;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 计算抄表交费截止日期
 * 
 * @author Administrator
 * 
 */
public interface IEndDate {
	Calendar enddate(String userid, HibernateTemplate hibernateTemplate,
			Map<String, Object> loginuser) throws Exception;
}
