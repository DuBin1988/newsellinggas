package com.aote.rs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aote.helper.FunctionObject;
import com.aote.helper.XmlDocument;
import com.aote.helper.XmlHelper;
import com.aote.listener.ContextListener;

@Path("waitwork")
@Component
public class WaitWorkService {

	static Logger log = Logger.getLogger(DBService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@GET
	@Path("/user/{username}/{password}/{pageIndex}/{pageSize}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray userPage(@PathParam("username") String username,
			@PathParam("password") String password,
			@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex) {
		try {
			// ����û�Ȩ����
			String right = getUserRight("functionobj", username, password);
			// ��ȡ�û�Ȩ�޶�Ӧ������/�����ѯ���
			String query = "select * from ("+findSqls(right)+") t where f_rwbj=(select NAME from t_user where ENAME='"+username+"' and PASSWORD='"+password+"') or f_rwbj is null order by f_state";
			JSONArray array = new JSONArray();
			log.debug(query + ", size=" + pageSize + ", index=" + pageIndex);
			// ���pageIndexС��0��ֱ�ӷ���
			if (pageIndex < 0) {
				return array;
			}
			HibernateSQLCall sqlCall = new HibernateSQLCall(query, pageIndex,
					pageSize);
			sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
			List<Map<String, Object>> list = this.hibernateTemplate
					.executeFind(sqlCall);
			for (Map<String, Object> map : list) {
				JSONObject json = (JSONObject) new JsonTransfer()
						.MapToJson(map);
				array.put(json);
			}
			log.debug(array.toString());
			return array;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("/user/{username}/{password}/{sumnames}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject userCount(@PathParam("username") String username,
			@PathParam("password") String password,
			@PathParam("sumnames") String sumnames) {
		JSONObject result = new JSONObject();
		try {
			// ����û�Ȩ����
			String right = getUserRight("functionobj", username, password);
			// ��ȡ�û�Ȩ�޶�Ӧ������/�����ѯ���
			String sql = findSqls(right);
			sql = "select count(*) as Count, count(*) as Count " + " from ("
					+ sql + ") t where f_rwbj=(select NAME from t_user where ENAME='"+username+"' and PASSWORD='"+password+"') or f_rwbj is null";
			log.debug(sql);
			HibernateSQL call = new HibernateSQL(sql);
			List list = (List) hibernateTemplate.execute(call);
			// ��mapת����json����
			Object[] objs = (Object[]) list.get(0);
			Map<String, Object> map = new HashMap<String, Object>();
			// �Ȱ�Count�Ž�ȥ��ͷһ��������
			map.put("Count", objs[1]);
			result = (JSONObject) new JsonTransfer().MapToJson(map);
			log.debug(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	// ��õ�¼�û���Ϣ
	public String getUserRight(String beanname, String username, String password) {
		String result = "";
		ServletContext sc = ContextListener.getContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(sc);
		FunctionObject obj = (FunctionObject) ctx.getBean(beanname);
		JSONObject json;
		try {
			result = obj.getRights(username, password);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * ����Ȩ�޻�ȡ�����ļ��е����
	 */
	public String findSqls(String rights) {
		String result = "";
		ServletContext sc = ContextListener.getContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(sc);
		XmlDocument xml = (XmlDocument) ctx.getBean("waitwork");
		Document doc = xml.getDocument();
		Element rootElem = doc.getDocumentElement();
		String[] rs = rights.split(",");
		for (int i = 0; i < rs.length; i++) {
			String str = XmlHelper.findElemSignValue(rootElem, "NAME", rs[i],
					"SQL");
			if (str != null && !str.equals("")) {
				result += str + " union ";
			}
		}
		if (result.endsWith("union ")) {
			result = result.substring(0, result.length() - 6);
		}
		return result;
	}

	// ������ҳ��ִ��sql��ѯ
	class HibernateSQL implements HibernateCallback {
		String sql;

		public HibernateSQL(String sql) {
			this.sql = sql;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			List result = q.list();
			return result;
		}
	}

	// ִ��sql��ҳ��ѯ���������ʽ��������
	class HibernateSQLCall implements HibernateCallback {
		String sql;
		int page;
		int rows;
		// ��ѯ���ת����������ת����Map�ȡ�
		public ResultTransformer transformer = null;

		public HibernateSQLCall(String sql, int page, int rows) {
			this.sql = sql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			// ��ת����������ת����
			if (transformer != null) {
				q.setResultTransformer(transformer);
			}
			List result = q.setFirstResult(page * rows).setMaxResults(rows)
					.list();
			return result;
		}
	}

	// ת��������ת���ڼ��������Ƿ��Ѿ�ת��������������ת����������ѭ��
	class JsonTransfer {
		// �����Ѿ�ת�����Ķ���
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		// �ѵ���mapת����JSON����
		public Object MapToJson(Map<String, Object> map) {
			// ת���������ؿն���
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					String key = entry.getKey();
					Object value = entry.getValue();
					// ��ֵת����JSON�Ŀն���
					if (value == null) {
						value = JSONObject.NULL;
					} else if (value instanceof HashMap) {
						value = MapToJson((Map<String, Object>) value);
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						value = ToJson(set);
					}
					// �����$type$����ʾʵ�����ͣ�ת����EntityType
					if (key.equals("$type$")) {
						json.put("EntityType", value);
					} else if (value instanceof Date) {
						Date d1 = (Date) value;
						Calendar c = Calendar.getInstance();
						long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
						json.put(key, time);
					} else if (value instanceof MapProxy) {
						// MapProxyû�м��أ�����
					} else {
						json.put(key, value);
					}
				} catch (JSONException e) {
					throw new WebApplicationException(400);
				}
			}
			return json;
		}

		// �Ѽ���ת����Json����
		public Object ToJson(PersistentSet set) {
			// û���صļ��ϵ�����
			if (!set.wasInitialized()) {
				return JSONObject.NULL;
			}
			JSONArray array = new JSONArray();
			for (Object obj : set) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
				array.put(json);
			}
			return array;
		}

		// �ж��Ѿ�ת�������������Ƿ������������
		public boolean contains(Map<String, Object> obj) {
			for (Map<String, Object> map : this.transed) {
				if (obj == map) {
					return true;
				}
			}
			return false;
		}
	}

}
