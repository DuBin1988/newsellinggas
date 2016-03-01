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
 * 提供sql查询服务
 */
@Path("sql")
@Component
public class SqlService {
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 执行sql,对sql中的参数进行替换
	 */
	@POST
	public JSONArray txExecute(String str) {
		try {
			//解析传递过来的对象属性
			JSONObject json = new JSONObject(str);
			String sqlPath=json.getString("sql");
			String sql=getSql(sqlPath);
			sql="$"+sql;
			//拿到json对象参数
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
	
	//拿到sql字符串
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
    
	
	//把字符串转换成可执行sql
	private String getExecSql(String sql,JSONObject params){
		Program prog = new Program(sql);
		// 解析
		Delegate d = prog.CommaExp().Compile();
		// getParamNames返回所有参数名，把找到的对象用putParam放回
		Set<String> objectNames = d.objectNames.keySet();
		for (String name : objectNames ) {
			// 根据name找到对象，需自己编写
			Object obj = getVarValue(name, params);
			// 把对象放回objectNames
			d.objectNames.put(name, obj);
		}
		Object result = d.invoke();
		return result.toString();	
	}

	// 根据变量名取变量值
	private Object getVarValue(String name, JSONObject params) {
		try {
			//如果是this，返回逻辑服务自身
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