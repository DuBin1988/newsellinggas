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
 * ��̬���в��ֵ���������
 */
@Path("workflow")
@Component
public class Server {
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * ��������
	 * 
	 * @param name
	 *            ��������
	 * @param person
	 *            ����������
	 * @return �������̺��HTMLƬ��
	 */
	@POST
	@Path("startprocess/{name}/{username}/{userid}")
	public String txstartProcess(@PathParam("name") String name,
			@PathParam("username") String username,
			@PathParam("userid") String userid,
			String personExpression) {
		try {
			//�������ݹ����Ķ�������
			JSONArray array = new JSONArray(personExpression);
			JSONObject firstObj = array.getJSONObject(0);
			final String data = firstObj.getString("data");
			JSONObject dataObj = new JSONObject(data);
			String processId = dataObj.getString("id");
		
			Session session = sessionFactory.getCurrentSession();
			// �õ����̶���
			ProcessDef process = ProcessDefManager.getInstance().getProcessDef(
					name);
			if (process == null) {
				throw new RuntimeException("���̲����ڣ�����=" + name);
			}
			ActivityDef activity = process.getStartActivity();
			if (activity == null) {
				throw new RuntimeException("����û�п�ʼ�������=" + name);
			}
			// ��������ʵ��
			ProcessInstance procIns = process.createInstance(session,processId);
		
			// �����ʵ��,ǰһ���ʵ��Ϊ��
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
	 * ִ�л
	 * 
	 * @param request
	 *            HTTP���󣬰����ID��
	 * @return ��ʾҳ��Ƭ��
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