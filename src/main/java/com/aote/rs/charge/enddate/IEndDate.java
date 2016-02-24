package com.aote.rs.charge.enddate;

import java.util.Calendar;

import org.springframework.orm.hibernate3.HibernateTemplate;

public interface IEndDate {
	Calendar enddate(String userid,HibernateTemplate hibernateTemplate);
}