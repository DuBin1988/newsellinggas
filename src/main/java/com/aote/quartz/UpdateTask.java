package com.aote.quartz;

import java.lang.reflect.Field;

import oracle.security.o5logon.a;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.iesGas;

public class UpdateTask {
	static Logger log = Logger.getLogger(UpdateTask.class);

	private HibernateTemplate hibernateTemplate;
	public boolean finished = true;
	
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate)
	{
		this.hibernateTemplate = hibernateTemplate;
	}	
	
	public void update() {
		finished = false;
		/*
		Agent agent = new Agent();
		try
		{
			Field f = agent.getClass().getDeclaredField("hibernateTemplate");
			f.setAccessible(true);
			f.set(agent, this.hibernateTemplate);
			agent.extractSlips();
		}
		catch(Exception e)
		{}
		*/
		iesGas iesGas = new iesGas();
		try
		{
			Field f = iesGas.getClass().getDeclaredField("hibernateTemplate");
			f.setAccessible(true);
			f.set(iesGas, this.hibernateTemplate);
			String Obj="[]";
			iesGas.tablecomand(Obj);
			iesGas.gasdjcomand(Obj);
			iesGas.gasczcomand(Obj);
			iesGas.userstatus(Obj);
			iesGas.usercomand(Obj);
			iesGas.bkcomand(Obj);
		}
		catch(Exception e)
		{}
		finished = true;
	}

}
