package com.aote.rs;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.af.expression.Delegate;
import com.af.expression.Program;
import com.aote.helper.Util;
import com.browsesoft.workflow.perform.VarInstance;

/**
 * �������� query,get��ͷ����ֻ������ tx��ʼ���Ƕ�д���� xt��ͷ�����ֶ���������
 * 
 * @author grain
 *
 */
@Path("logic")
@Scope("prototype")
@Component
public class LogicService {
	static Logger log = Logger.getLogger(LogicService.class);

	@Autowired
	private SessionFactory sessionFactory;

	private JSONObject object;


	// ִ��һ��ҵ���߼�, nameΪҵ���߼���
	@POST
	@Path("{name}")
	public JSONObject xtExecute(@Context HttpServletResponse response,
			@PathParam("name") String name,
			String values) {
		log.debug(values);
		// open a new session since we dont use spring here
		Session session = sessionFactory.openSession();
		try {
			JSONObject result = null;
			if(values.equals("")){
		     object=new JSONObject();
			}else{
			object = new JSONObject(values);
			}	
			// ��ȡҵ���߼�����
			//���ļ�
			String fileName = "d://name/" + name;
			File file=new File(fileName);
			byte[]b=new byte[1024];
			FileInputStream fis=new FileInputStream(file);
			StringBuilder stringBuilder=new StringBuilder();
			int total = fis.read(b); 
			while(total!=-1){
				stringBuilder.append(new String(b, 0, total));
				total= fis.read(b);
			}
			String str = stringBuilder.toString();
			
			String exp = str;
			// ����ҵ���߼����ݻ�ȡ���ʽֵ
			Object value = this.getExpressionValue(exp, object);
			// ��ֵת����JSON��
			session.beginTransaction();
			session.getTransaction().commit();
			return result;
		} catch (Exception e) {
			session.getTransaction().rollback();
			if (e instanceof org.hibernate.StaleObjectStateException) {
				response.setHeader("Warning",
						Util.encode("Ŀǰ�Ķ�����ڳ¾ɣ���Ϊ���������ط��Ѿ����޸ġ�"));
				throw new WebApplicationException(501);
			} else {
				response.setHeader("Warning", Util.encode(e.toString()));
				throw new WebApplicationException(501);
			}
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	// ������ʽ��������������ȡ
	public Object getExpressionValue(String expression, JSONObject params) {
		// ���ַ�������
		Program prog = new Program(expression);
		// ����
		Delegate d = prog.CommaExp().Compile();

		// getParamNames�������в����������ҵ��Ķ�����putParam�Ż�
		Set<String> objectNames = d.objectNames.keySet();
		for (String name : objectNames ) {
			// ����name�ҵ��������Լ���д
			Object obj = getVarValue(name, params);
			// �Ѷ���Ż�objectNames
			d.objectNames.put(name, obj);
		}
		
		Object result = d.invoke();
		return result;
	}
	
	// ���ݱ�����ȡ����ֵ
	private Object getVarValue(String name, JSONObject params) {
		try {
			//�����this�������߼���������
			if(name.equals("this")) {
				return this;
			}
			
			if(params.has(name)) {
				return params.get(name);
			}
			return null;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
