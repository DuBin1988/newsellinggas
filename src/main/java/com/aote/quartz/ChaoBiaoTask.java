package com.aote.quartz;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aote.rs.iesGas;
import com.aote.rs.charge.HandCharge;

public class ChaoBiaoTask {
	
	static Logger log = Logger.getLogger(ChaoBiaoTask.class);

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
		HandCharge HandCharge =new HandCharge();
		String json="";
		Date now = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat handdate = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat cbdate = new SimpleDateFormat("yyyy-MM-dd");
		//���ɳ���
		try
		{
			Field f = iesGas.getClass().getDeclaredField("hibernateTemplate");
			f.setAccessible(true);
			f.set(iesGas, this.hibernateTemplate);
			iesGas.getcanshu();
			iesGas.chaobiaodan("delete from t_handplan where f_state ='δ����' and shifoujiaofei ='��' and "+iesGas.gasmeterstyle+" ");
			
			iesGas.chaobiaodan("insert into t_handplan(f_userid, f_username, lastinputgasnum, f_gaswatchbrand, f_metertype, "+
								"f_address, f_districtname, f_usertype, f_gasprice, f_gaspricetype, f_dibaohu, f_apartment, "+
								"f_phone, scinputdate, f_inputtor, f_yhxz, f_weizhi, f_menzhan, "+
								"f_zerenbumen, f_state, shifoujiaofei, users, f_cusDom, f_cusDy,f_gasmeterstyle,zhye) "+
								"select f_userid, f_username, IsNull(lastinputgasnum,0)lastinputgasnum, f_gaswatchbrand, f_metertype, "+
								"f_address, f_districtname, f_usertype, f_gasprice, f_gaspricetype, f_dibaohu, f_apartment, "+
								"f_phone, isnull(lastinputdate,GETDATE())lastinputdate, f_inputtor, f_yhxz, f_weizhi, f_menzhan, "+
								"f_zerenbumen, 'δ����', '��', id, f_cusDom, f_cusDy,f_gasmeterstyle,IsNull(f_zhye,0)f_zhye "+
								"from t_userfiles where f_userstate='����' and "+iesGas.gasmeterstyle+" and f_userid not in "+
								"(select distinct f_userid from t_handplan where f_state='δ����') and f_userid not in "+
								"(select distinct f_userid from t_handplan where f_state='�ѳ���' and SUBSTRING(CONVERT(varchar(100),f_inputdate,25),1,10)='"+cbdate.format(now)+"') "+" ");	
			json=iesGas.getcbjson(cbdate.format(now)+" 23:59:59");
		}
		catch(Exception e){}
		//����
		try
		{
			Field f = HandCharge.getClass().getDeclaredField("hibernateTemplate");
			f.setAccessible(true);
			f.set(HandCharge, this.hibernateTemplate);
			if(!"[]".equals(json))
				HandCharge.afRecordInputForMore(json,"��ȼ����˾","����ϵͳ",fmt.format(now),handdate.format(now),"����ʹ��");
					
		}
		catch(Exception e){}
		finished = true;
	}
	
}
