package com.aote.rs.charge.countdate;

import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * ������ݿ�ʼ���ںͽ������ڵĽӿ�
 * @author Administrator
 *
 */
public interface ICountDate {
	String startdate(String userid,HibernateTemplate hibernateTemplate);
	String enddate(String userid,HibernateTemplate hibernateTemplate);
}
