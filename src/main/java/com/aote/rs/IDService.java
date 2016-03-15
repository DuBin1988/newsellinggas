package com.aote.rs;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Path("id")
@Component
public class IDService {
	static Logger log = Logger.getLogger(IDService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@GET
	@Path("{applyId}")
	public String Submit(@PathParam("applyId") int applyId) {
		//����״̬Ϊ���
		String updateApply = "update t_apply set f_state='ɢ���������',f_substate=null,f_tqdjzt='�����' " +
				" where id=? and f_substate='�����'";
		hibernateTemplate.bulkUpdate(updateApply, applyId);
		//��ȡ�û����
		String idQuery = "from t_singlevalue where name='�û����'";
		List<Object> list = this.hibernateTemplate.find(idQuery);
		// �ѵ���mapת����JSON����
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		String f_userid = map.get("value").toString();
		long attrVal = Long.parseLong(f_userid);
		map.put("value", attrVal + 1 + "");
		this.hibernateTemplate.update(map);
		// �����û���¼

		/**
		 * �����ֶ�
		 * ɢ����������������	   ����	   ���ý�������������
		 * f_usegastype              f_gasproperties
		 * f_usertype				f_usertype
		 * f_whethergivecard		'δ��'
		 */
	//f_userid,  '" + f_userid + "'	
		final String insertUserfiles = "insert into t_userfiles " +
		"(f_userstate,f_whethergivecard,f_usertype,f_username,f_address,f_districtname,f_phone,f_idnumber,f_meternumber,f_gasmeteraccomodations,f_aroundmeter,f_gasproperties)" +
		"select  '�����','δ��',f_usertype,f_username,f_address,f_district,f_phone,f_sfzhm,f_meternumber,f_gasmeteraccomodations,f_aroundmeter,f_usegastype " +
		"from t_apply where id=" + applyId;

		
		this.hibernateTemplate.execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session arg0) throws HibernateException,
					SQLException {
				// TODO Auto-generated method stub
				arg0.createSQLQuery(insertUserfiles).executeUpdate();
				return null;
			}
		});
		return "";
	}
}
