package com.aote.rs.charge.enddate;

import java.util.Calendar;

import org.springframework.orm.hibernate3.HibernateTemplate;


public class NextMonthEndDate implements IEndDate {

	//ͨ�����㽻�ѽ�ֹ����
	@Override
	public Calendar enddate(String userid, HibernateTemplate hibernateTemplate) {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		//ÿ��1��20�ų���������һ�Ų������ɽ�
		if(day>=1 && day <=20){
			cal.add(Calendar.MONTH, 1);
		}else{
			//21���Ժ�ģ���������һ�Ų������ɽ�
			cal.add(Calendar.MONTH, 2);
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		System.out.println(cal.getTime());
		return cal;
	}

 
}
 
