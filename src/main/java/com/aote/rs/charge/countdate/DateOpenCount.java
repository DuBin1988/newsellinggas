package com.aote.rs.charge.countdate;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * ���ݿ�ʼ���ڽ�����ݵĿ�ʼ���ںͽ�������
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
