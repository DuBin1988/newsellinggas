package com.aote.rs.charge.enddate;

import java.util.Calendar;

import org.springframework.orm.hibernate3.HibernateTemplate;


public class NextMonthEndDate implements IEndDate {

	//通化计算交费截止日期
	@Override
	public Calendar enddate(String userid, HibernateTemplate hibernateTemplate) {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		//每月1到20号抄表。从下月一号产生滞纳金
		if(day>=1 && day <=20){
			cal.add(Calendar.MONTH, 1);
		}else{
			//21号以后的，从下下月一号产生滞纳金
			cal.add(Calendar.MONTH, 2);
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		System.out.println(cal.getTime());
		return cal;
	}

}