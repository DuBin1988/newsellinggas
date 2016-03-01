package com.aote.rs;

import java.io.RandomAccessFile;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.af.expression.Delegate;
import com.af.expression.Program;
import com.aote.rs.util.SqlHelper;


/**
 * �ṩsql��ѯ����
 */
@Path("sql")
@Component
public class SqlService {
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * ִ��sql,��sql�еĲ��������滻
	 */
	@POST
	public JSONArray txExecute(String str) {
		try {
			//�������ݹ����Ķ�������
			JSONObject json = new JSONObject(str);
			String sqlPath=json.getString("sql");
			String sql=getSql(sqlPath);
			sql="$"+sql;
			//�õ�json�������
			JSONObject parm=json.getJSONObject("params");
			sql=getExecSql(sql,parm);
			Session session = sessionFactory.getCurrentSession();
			JSONArray array=SqlHelper.query(session, sql);
			return array;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//�õ�sql�ַ���
	private String getSql(String str){
		String sql=null;
		try {
			String path = this.getClass().getClassLoader().getResource("/sqls").getPath();
			path+=str;
			RandomAccessFile file=new RandomAccessFile(path, "r");
			byte[] b=new byte[(int)file.length()];
			file.read(b);
			sql=new String(b);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sql;
	}
    
	
	//���ַ���ת���ɿ�ִ��sql
	private String getExecSql(String sql,JSONObject params){
		Program prog = new Program(sql);
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
		return result.toString();	
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