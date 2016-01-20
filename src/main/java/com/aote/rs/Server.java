package com.aote.rs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.browsesoft.workflow.perform.ActivityDef;
import com.browsesoft.workflow.perform.ActivityInstance;
import com.browsesoft.workflow.perform.ProcessDef;
import com.browsesoft.workflow.perform.ProcessDefManager;
import com.browsesoft.workflow.perform.ProcessInstance;

/**
 * 动态运行部分的主服务类
 */
@Path("workflow")
@Component
public class Server {
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 启动流程
	 * 
	 * @param name
	 *            流程名称
	 * @param person
	 *            流程启动者
	 * @return 启动流程后的HTML片断
	 */
	@POST
	@Path("startprocess/{name}/{username}/{userid}")
	public String txstartProcess(@PathParam("name") String name,
			@PathParam("username") String username,
			@PathParam("userid") String userid,
			String personExpression) {
		try {
			//解析传递过来的对象属性
			JSONArray array = new JSONArray(personExpression);
			JSONObject firstObj = array.getJSONObject(0);
			final String data = firstObj.getString("data");
			JSONObject dataObj = new JSONObject(data);
			String processId = dataObj.getString("id");
		
			Session session = sessionFactory.getCurrentSession();
			// 得到流程定义
			ProcessDef process = ProcessDefManager.getInstance().getProcessDef(
					name);
			if (process == null) {
				throw new RuntimeException("流程不存在：名字=" + name);
			}
			ActivityDef activity = process.getStartActivity();
			if (activity == null) {
				throw new RuntimeException("流程没有开始活动：名字=" + name);
			}
			// 产生流程实例
			ProcessInstance procIns = process.createInstance(session,processId);
		
			// 产生活动实例,前一个活动实例为空
			ActivityInstance actIns = new ActivityInstance(activity, procIns,
					"", username, userid, null);
		 	session.save(actIns);
			actIns.finish(session, personExpression, username, userid);
			JSONObject okObject = new JSONObject();
			okObject.put("ok","ok");
			return okObject.toString();
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 执行活动
	 * 
	 * @param request
	 *            HTTP请求，包含活动ID号
	 * @return 提示页面片断
	 */
	@POST
	@Path("perfomactivity/{acitityid}/{username}/{userid}")
	public String txperformActivity(@PathParam("acitityid") String acitityid,
			@PathParam("username") String username,
			@PathParam("userid") String userid,
			String personExpression) {
		try {
			Session session = sessionFactory.getCurrentSession();
			ActivityInstance actIns = (ActivityInstance) session.load(
					ActivityInstance.class, acitityid);
			actIns.finish(session, personExpression, username, userid);
			JSONObject okObject = new JSONObject();
			okObject.put("ok","ok");
			return okObject.toString();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}