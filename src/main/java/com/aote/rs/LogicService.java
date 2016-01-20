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
 * 命名规则： query,get开头的是只读事务 tx开始的是读写事务 xt开头的是手动控制事务
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


	// 执行一个业务逻辑, name为业务逻辑名
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
			// 获取业务逻辑内容
			//读文件
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
			// 根据业务逻辑内容获取表达式值
			Object value = this.getExpressionValue(exp, object);
			// 把值转换成JSON串
			session.beginTransaction();
			session.getTransaction().commit();
			return result;
		} catch (Exception e) {
			session.getTransaction().rollback();
			if (e instanceof org.hibernate.StaleObjectStateException) {
				response.setHeader("Warning",
						Util.encode("目前的对象过于陈旧，因为它在其他地方已经被修改。"));
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
	
	// 计算表达式，变量从流程中取
	public Object getExpressionValue(String expression, JSONObject params) {
		// 用字符串构造
		Program prog = new Program(expression);
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
		return result;
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
