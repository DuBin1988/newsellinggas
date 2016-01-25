package com.aote.rs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.modelmbean.XMLParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentSet;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.ListType;
import org.hibernate.type.LongType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.SetType;
import org.hibernate.type.TimeType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.aote.expression.ExpressionGenerator;
import com.aote.expression.upkeep.UpkeepFactory;
import com.aote.expression.upkeep.UpkeepInterface;
import com.aote.helper.Util;
import com.aote.rs.util.FileHelper;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * �������� query,get��ͷ����ֻ������ tx��ʼ���Ƕ�д���� xt��ͷ�����ֶ���������
 * 
 * @author grain
 *
 */
@Path("db")
@Scope("prototype")
@Component
public class DBService {
	static Logger log = Logger.getLogger(DBService.class);

	@Autowired
	private SessionFactory sessionFactory;

	// ��ȡ����ʵ���������Ϣ
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getMetas(@QueryParam("tables") String tables) {
		String[] sTables = null;
		// ��Ϊ�գ���Ҫȡ�ı���������
		if(tables != null) {
			sTables = tables.split(",");
			Arrays.sort(sTables);
		}
		
		JSONObject result = new JSONObject();
		// ��ȡ����ʵ��
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		for (Map.Entry<String, ClassMetadata> entry : map.entrySet()) {
			try {
				String key = entry.getKey();
				
				// ���key���������������
				if(sTables != null && Arrays.binarySearch(sTables, key) == -1) {
					continue;
				}
				
				JSONObject attrs = new JSONObject();
				for (String name : entry.getValue().getPropertyNames()) {
					Type type = entry.getValue().getPropertyType(name);
					attrs.put(name, TypeToString(type));
				}
				// ���id��id��û�е������Ի�ȡ
				String idName = entry.getValue().getIdentifierPropertyName();
				Type idType = entry.getValue().getIdentifierType();
				attrs.put(idName, TypeToString(idType));
				result.put(key, attrs);
			} catch (JSONException e) {
				throw new WebApplicationException(400);
			}
		}
		log.debug(result);
		return result;
	}

	private String TypeToString(Type type) {
		if (type instanceof ManyToOneType) {
			ManyToOneType t = (ManyToOneType) type;
			String entityName = t.getAssociatedEntityName();
			return entityName;
		} else if (type instanceof SetType) {
			String entityName = getCollectionEntityName((SetType) type);
			return entityName + "[]";
		} else {
			return type.getName();
		}
	}

	// �õ��������͵Ĺ���ʵ������
	private String getCollectionEntityName(SetType type) {
		SessionFactoryImplementor sf = (SessionFactoryImplementor) sessionFactory;
		String entityName = type.getAssociatedEntityName(sf);
		return entityName;
	}

	@GET
	@Path("{hql}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray query(@PathParam("hql") String query) {
		return xtArray(sessionFactory.getCurrentSession(), query);
	}

	private JSONArray xtArray(Session session, String query) {
		// %��·���в��ܳ��֣���%�ĳ���^
		// query = query.replaceAll("0x25", "%");
		// sql���г��ŵ�ʱ���滻
		query = query.replace("|", "/");
		log.debug(query);
		JSONArray array = new JSONArray();
		// List<Object> list = this.hibernateTemplate.find(query);
		// ���β�ѯ�Ӽ�¼������
		List list = executeFind(session, new HibernateCall(query, 0, 10000));
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		log.debug(array.toString());
		return array;

	}

	private List executeFind(Session session, HibernateCall hibernateCall) {
		return (List) hibernateCall.doInHibernate(session);
	}

	@GET
	@Path("/agg/{hql}/{attrname}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray query(@PathParam("hql") String query,
			@PathParam("attrname") String names) {
		// sql���г��ŵ�ʱ���滻
		query = query.replace("|", "/");
		log.debug(query);
		JSONArray array = new JSONArray();
		// List<Object> list = this.hibernateTemplate.find(query);
		// ���β�ѯ�Ӽ�¼������
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10000));
		for (Object obj : list) {
			// ������Ϊÿһ���������Ӧ������
			String[] snames = names.split(",");
			// select����ʱ������ĸ���Ϊ��һ�����ļ�������
			Object[] objs = (Object[]) obj;
			Map<String, Object> map = (Map<String, Object>) objs[0];
			for (int i = 1; i < objs.length; i++) {
				map.put(snames[i - 1], objs[i]);
			}
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		log.debug(array.toString());
		return array;
	}

	@GET
	@Path("/one/{hql}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject queryOne(@PathParam("hql") String query) {
		// %��·���в��ܳ��֣���%�ĳ���^
		// query = query.replaceAll("\\^", "%");
		log.debug(query);
		JSONObject result = new JSONObject();
		// List<Object> list = this.hibernateTemplate.find(query);
		// ���β�ѯ�Ӽ�¼������
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10000));
		if (list.size() != 1) {
			// ��ѯ���������ݣ��ܳ��쳣
			throw new WebApplicationException(500);
		}
		// �ѵ���mapת����JSON����
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		log.debug(result.toString());
		return result;
	}

	@GET
	@Path("/sernum/{hql}/{attrname}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getSerialNumber(@PathParam("hql") String query,
			@PathParam("attrname") String attrname) {
		log.debug(query);
		JSONObject result = new JSONObject();
		// ���β�ѯ�Ӽ�¼������
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10000));
		if (list.size() != 1) {
			// ��ѯ���������ݣ��ܳ��쳣
			throw new WebApplicationException(500);
		}
		// �ѵ���mapת����JSON����
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		long attrVal = Long.parseLong(map.get(attrname).toString());
		map.put(attrname, attrVal + 1 + "");
		sessionFactory.getCurrentSession().update(map);
		log.debug(result.toString());
		return result;
	}

	@GET
	@Path("{hql}/{pageIndex}/{pageSize}")
	@Produces(MediaType.APPLICATION_JSON)
	// ��ȡһҳ����
	public JSONArray query(@PathParam("hql") String query,
			@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex) {
		JSONArray array = new JSONArray();
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, pageIndex, pageSize));
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		return array;
	}

	// ִ�з�ҳ��ѯ
	class HibernateCall implements HibernateCallback {
		String hql;
		int page;
		int rows;

		public HibernateCall(String hql, int page, int rows) {
			this.hql = hql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createQuery(hql);
			List result = q.setFirstResult(page * rows).setMaxResults(rows)
					.list();
			return result;
		}
	}

	/**
	 * // �ѵ���mapת����JSON���� private JSONObject MapToJson(Map<String, Object> map)
	 * { JSONObject json = new JSONObject(); for (Map.Entry<String, Object>
	 * entry : map.entrySet()) { try { String key = entry.getKey(); Object value
	 * = entry.getValue(); // ��ֵת����JSON�Ŀն��� if (value == null) { value =
	 * JSONObject.NULL; } else if (value instanceof PersistentSet) {
	 * PersistentSet set = (PersistentSet) value; value = ToJson(set); } //
	 * �����$type$����ʾʵ�����ͣ�ת����EntityType if (key.equals("$type$")) {
	 * json.put("EntityType", value); } else if (value instanceof Date) { Date
	 * d1 = (Date) value; Calendar c = Calendar.getInstance(); long time =
	 * d1.getTime() + c.get(Calendar.ZONE_OFFSET); json.put(key, time); } else
	 * if (value instanceof HashMap) { JSONObject json1 = MapToJson((Map<String,
	 * Object>) value); json.put(key, json1); } else { json.put(key, value); } }
	 * catch (JSONException e) { throw new WebApplicationException(400); } }
	 * return json; }
	 * 
	 * // �Ѽ���ת����Json���� private Object ToJson(PersistentSet set) { // û���صļ��ϵ�����
	 * if (!set.wasInitialized()) { return JSONObject.NULL; } JSONArray array =
	 * new JSONArray(); for (Object obj : set) { Map<String, Object> map =
	 * (Map<String, Object>) obj; JSONObject json = MapToJson(map);
	 * array.put(json); } return array; }
	 **/

	@GET
	@Path("{hql}/{sumNames}")
	@Produces(MediaType.APPLICATION_JSON)
	// ������
	public JSONObject querySum(@PathParam("hql") String query,
			@PathParam("sumNames") String names) {
		JSONObject result = new JSONObject();
		// ��֯sums��
		String sums = "";
		for (String name : names.split(",")) {
			if (!sums.equals(""))
				sums += ",";
			sums += "sum(" + name + ") as " + name;
		}
		String hql = "";
		if (!sums.equals("")) {
			hql = "select new map(count(*) as Count, " + sums + ") " + query;
		} else {
			hql = "select new map(count(*) as Count) " + query;
		}
		// hql = "select new map(count(*) as Count, " + sums + ") " + query;
		List<Object> l = sessionFactory.getCurrentSession().find(hql);
		// ��mapת����json����
		Map<String, Object> map = (Map<String, Object>) l.get(0);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				result.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				throw new WebApplicationException(400);
			}
		}
		return result;
	}

	@GET
	@Path("sql/{sql}")
	@Produces(MediaType.APPLICATION_JSON)
	// ��ȡһҳ����
	public JSONArray querySQL(@PathParam("sql") String query) {
		JSONArray array = new JSONArray();
		// sql���г��ŵ�ʱ���滻
		query = query.replace("|", "/");
		final String sql = query;
		HibernateSQLCall sqlCall = new HibernateSQLCall(query, 0, 10000);
		List<Map<String, Object>> list = executeFind(
				sessionFactory.getCurrentSession(), sqlCall);
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Object[] c = (Object[]) obj;
			JSONObject json = new JSONObject();
			for (int i = 0; i < c.length; i++) {
				try {
					json.put("col" + i, c[i]);
				} catch (JSONException e) {
					throw new WebApplicationException(400);
				}
			}
			array.put(json);
		}
		return array;
	}

	private List<Map<String, Object>> executeFind(Session session,
			HibernateSQLCall sqlCall) {
		return (List<Map<String, Object>>) sqlCall.doInHibernate(session);
	}

	/*
	 * ִ��һ������������������棬ɾ����hql��䣬hql�����������, sql���ȡ� ��json����ʾ�� json����ʽΪ [һ�����]��
	 * ����ʽΪ {operator:'�������', entity:'ʵ������', data:����, name:ǰ̨���õ�����} ��������� save
	 * ����, delete ɾ��, hql ִ��hql, hqlAll ��һ������ִ��hql, sql ִ��sql, reference
	 * ��������ʹӶ���֮�佨������ ʵ������ ��ִ��hql������sql����ʱ��Ϊ�ա� ����
	 * ����ʱΪһ��json����ɾ��ʱΪid�ţ�ִ��hqlʱΪhql��䣬ִ��sqlʱΪsql��� ����hqlִ��δ�����趨�ĸ�ʽΪ
	 * {hql:'hql���'��ids:['id','id']}
	 */
	@POST
	public JSONObject xtExecute(@Context HttpServletResponse response,
			String values) {
		log.debug(values);
		// open a new session since we dont use spring here
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			// ���غ�̨����Ľ��,��ʽΪ {������:{����ֵ}}
			JSONObject result = new JSONObject();
			// һ����ִ��
			JSONArray array = new JSONArray(values);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String oper = object.getString("operator");
				final String data = object.getString("data");
				// ����
				if (oper.equals("save")) {
					JSONObject obj = save(session, object.getString("entity"),
							object.getJSONObject("data"));
					result.put(object.getString("name"), obj);
				} else if (oper.equals("delete")) {
					String hql = "delete from " + object.getString("entity")
							+ " where id=" + object.getInt("data");
					log.debug(hql);
					bulkUpdate(session, hql);
				} else if (oper.equals("hql")) {
					// ִ��hql
					bulkUpdate(session, object.getString("data"));
				} else if (oper.equals("sql")) {
					bulkSqlUpdate(session, data);
				} else if (oper.equals("hqlAll")) {
					// ִ������hql
					throw new NotImplementedException();
				} else if (oper.equals("reference")) {
					MasterDetailAssociationHandler(session, object);
				} else {
					throw new WebApplicationException(500);
				}
			}
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

	/**
	 * ����updateCount
	 * 
	 * @param session
	 * @param sql
	 * @return
	 */
	private int bulkUpdate(Session session, String sql) {
		Query queryObject = session.createQuery(sql);
		return new Integer(queryObject.executeUpdate()).intValue();
	}

	/**
	 * ����updateCount
	 * 
	 * @param session
	 * @param sql
	 * @return
	 */
	private int bulkSqlUpdate(Session session, String sql) {
		Query queryObject = session.createSQLQuery(sql);
		return new Integer(queryObject.executeUpdate()).intValue();
	}

	/**
	 * ����������õ�ID ����sql/hqlȡ�ôӶ��� ����hibernate���ý������ӹ�����������Ӷ���
	 * 
	 * @param object
	 */
	private void MasterDetailAssociationHandler(Session session,
			JSONObject object) throws Exception {
		JSONObject row = object.getJSONObject("data");
		String entity = object.getString("entity");
		String entity2 = object.getString("entity2");
		// �ȱ��������󣬵õ�id
		JSONObject obj = save(session, entity, row);
		if (obj.has("ID"))
			row.put("ID", obj.get("ID"));
		if (obj.has("id"))
			row.put("id", obj.get("id"));
		// �ѴӶ��󼯸����������prop
		String prop = object.getString("reference");
		// ��sql����hql
		String type = object.getString("path");
		// ȡ��ִ�е����
		String hql = object.getString("hql");
		if (type.equals("sql")) {
			JSONArray array = new JSONArray();
			HibernateSQLCall sqlCall = new HibernateSQLCall(hql, 0, 9999999);
			sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
			List<Map<String, Object>> list = executeFind(session, sqlCall);
			for (Map<String, Object> map : list) {
				JSONObject json = (JSONObject) new JsonTransfer()
						.MapToJson(map);
				array.put(json);
			}

			for (int i = 0; i < array.length(); i++) {
				array.getJSONObject(i).put("EntityType", entity2);
			}
			row.put(prop, array);
		} else if (type.equals("hql")) {
			row.put(prop, this.xtArray(session, hql));
		} else
			throw new WebApplicationException(500);
		// ���������󣬽�������
		save(session, entity, row);
	}

	@POST
	@Path("{entity}")
	public String xtSave(@Context HttpServletResponse response,
			@PathParam("entity") String entityName, String values)
			throws Exception {
		log.debug("entity:" + entityName + ", values:" + values);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			JSONObject object = new JSONObject(values);
			save(session, entityName, object);
			session.getTransaction().commit();
			return "ok";
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

	// �ڲ�������̣�nameΪ�����ϴ�������Ҫ�������֣����ص��Ǻ�̨���ʽ�����Ķ�������
	private JSONObject save(Session session, String entityName,
			JSONObject object) throws Exception {
		// ����ʵ������ȥ������������Ϣ
		ClassMetadata classData = sessionFactory.getClassMetadata(entityName);
		JSONObject result = new JSONObject();
		// ��json����ת����map
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = object.get(key);
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (Exception e) {

			}

			if (object.isNull(key)) {
				// �յ�id�Ų���������ţ��Ա㰴��������
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray && propType instanceof SetType) {
				// Json����ת����һ�Զ��ϵ��Set
				Set<Map<String, Object>> set = saveSet(session, (JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONArray && propType instanceof ListType) {
				// Json����ת����һ�Զ��ϵ��Set
				List<Map<String, Object>> set = saveList(session, (JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = (String) obj.get("EntityType");
				Map<String, Object> set = saveWithoutExp(session, type,
						(JSONObject) value);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				long l = 0;
				if (value instanceof Double) {
					l = ((Double) value).longValue();
				} else if (value instanceof Long) {
					l = ((Long) value).longValue();
				} else if (value instanceof Integer) {
					l = ((Integer) value).intValue();
				}
				Date d = new Date(l);
				map.put(key, d);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// intֱ��ת����double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof Integer && propType instanceof LongType) {
				Long v = Long.valueOf(value.toString());
				map.put(key, v.longValue());
			} else {
				// ����Ҫ��̨����ı��ʽ����̨����󣬰ѽ������
				if (value instanceof String
						&& value.toString().indexOf("#") != -1) {
					// ���ñ��ʽ����
					try {
						value = ExpressionGenerator.getExpressionValue(value
								.toString());
					} catch (Exception e) {
						log.debug(value + "���ʽ�������쳣,ʹ��selfֵ");
					}
					result.put(key, value);
				}
				map.put(key, value);
			}
		}
		session.saveOrUpdate(entityName, map);
		if (map.containsKey("id"))
			result.put("id", map.get("id"));
		if (map.containsKey("ID"))
			result.put("ID", map.get("ID"));
		return result;
	}

	// ����JsonObject����ת��ΪMap������ʱ��������̨���ʽ���㣬����һ�Զ��ϵ�е��ӵı���
	private Map<String, Object> saveWithoutExp(Session session,String entityName,
			JSONObject object) throws JSONException {
		// ����ʵ������ȥ������������Ϣ
		ClassMetadata classData = sessionFactory.getClassMetadata(entityName);
		// ��json����ת����map
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (Exception e) {

			}

			Object value = object.get(key);
			if (object.isNull(key)) {
				// �յ�id�Ų���������ţ��Ա㰴��������
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray) {
				// Json����ת����һ�Զ��ϵ��Set
				Set<Map<String, Object>> set = saveSet(session, (JSONArray) value);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				long l = 0;
				if (value instanceof Double) {
					l = ((Double) value).longValue();
				} else if (value instanceof Long) {
					l = ((Long) value).longValue();
				}
				Date d = new Date(l);
				map.put(key, d);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// intֱ��ת����double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = (String) obj.get("EntityType");
				Map<String, Object> set = saveWithoutExp(session,type,
						(JSONObject) value);
				map.put(key, set);
			} else {
				map.put(key, value);
			}
		}
		session.saveOrUpdate(entityName, map);
		return map;
	}

	// ����JSONArray��Ķ��󣬲�ת��ΪSet
	private Set<Map<String, Object>> saveSet(Session session, JSONArray array)
			throws JSONException {
		Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = saveWithoutExp(session, type, obj);
			set.add(map);
		}
		return set;
	}

	// ����JSONArray��Ķ��󣬲�ת��ΪSet
	private List<Map<String, Object>> saveList(Session session, JSONArray array)
			throws JSONException {
		List<Map<String, Object>> set = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = saveWithoutExp(session, type, obj);
			set.add(map);
		}
		return set;
	}
	
	
	@GET
	@Path("/one/{hql}/{attrname}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject queryOne(@PathParam("hql") String query,
			@PathParam("attrname") String names) {
		log.debug(query);
		JSONObject result = new JSONObject();
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, 0, 10000));
		if (list.size() != 1) {
			// ��ѯ���������ݣ��׳��쳣
			throw new WebApplicationException(500);
		}
		// ��������ĵ�һ��Ϊ���صĶ��󣬺��������Ϊ���������
		String[] snames = names.split(",");
		Object[] objs = (Object[]) list.get(0);
		Map<String, Object> map = (Map<String, Object>) objs[0];
		for (int i = 1; i < objs.length; i++) {
			map.put(snames[i - 1], objs[i]);
		}
		result = (JSONObject) new JsonTransfer().MapToJson(map);
		log.debug(result.toString());
		return result;
	}

	@POST
	@Path("hql/{pageIndex}/{pageSize}")
	@Produces(MediaType.APPLICATION_JSON)
	// ��ȡһҳ����
	public JSONArray queryPost(@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex, String query) {
		JSONArray array = new JSONArray();
		log.debug(query + ", size=" + pageSize + ", index=" + pageIndex);
		// ���pageIndexС��0��ֱ�ӷ���
		if (pageIndex < 0) {
			return array;
		}
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, pageIndex, pageSize));
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		log.debug(array.toString());
		return array;
	}

	@GET
	@Path("{hql}/{attrname}/{pageIndex}/{pageSize}")
	@Produces(MediaType.APPLICATION_JSON)
	// ��ȡһҳ���ݣ����Դ������ֶ�
	public JSONArray query(@PathParam("hql") String query,
			@PathParam("attrname") String names,
			@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex) {
		JSONArray array = new JSONArray();
		log.debug(query + ", names=" + names + ", size=" + pageSize
				+ ", index=" + pageIndex);
		// ���pageIndexС��0��ֱ�ӷ���
		if (pageIndex < 0) {
			return array;
		}
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(query, pageIndex, pageSize));
		for (Object obj : list) {
			// ������Ϊÿһ���������Ӧ������
			String[] snames = names.split(",");
			// select����ʱ������ĸ���Ϊ��һ�����ļ�������
			Object[] objs = (Object[]) obj;
			Map<String, Object> map = (Map<String, Object>) objs[0];
			for (int i = 1; i < objs.length; i++) {
				map.put(snames[i - 1], objs[i]);
			}
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		log.debug(array.toString());
		return array;
	}

	@POST
	@Path("hql/{sumNames}")
	@Produces(MediaType.APPLICATION_JSON)
	// ������
	public JSONObject postQuerySum(@PathParam("sumNames") String names,
			String query) {
		JSONObject result = new JSONObject();
		// ��֯sums��
		String sums = "";
		for (String name : names.split(",")) {
			if (!sums.equals(""))
				sums += ",";
			sums += "sum(" + name + ") as " + name;
		}
		String hql = "";
		if (!sums.equals("")) {
			hql = "select new map(count(*) as Count, " + sums + ") " + query;
		} else {
			hql = "select new map(count(*) as Count) " + query;
		}
		log.debug(hql);
		List<Object> l = sessionFactory.getCurrentSession().find(hql);
		// ��mapת����json����
		Map<String, Object> map = (Map<String, Object>) l.get(0);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				result.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				throw new WebApplicationException(400);
			}
		}
		log.debug(result);
		return result;
	}

	@POST
	@Path("sql/{sumNames}")
	// ������
	public JSONObject queryPostSQLSum(@PathParam("sumNames") String names,
			String query) {
		// ��֯sums��
		String sums = "";
		String[] snames = names.split(",");
		for (String name : snames) {
			if (!sums.equals(""))
				sums += ",";
			sums += "sum(" + name + ") as " + name;
		}
		String sql = "";
		if (!sums.equals("")) {
			// ǰ������һ��count���Ա�ͳһ�������飬�����ǵ�������
			sql = "select count(*) as Count, count(*) as Count, " + sums
					+ " from (" + query + ") t";
		} else {
			// ǰ������һ��count���Ա�ͳһ�������飬�����ǵ�������
			sql = "select count(*) as Count, count(*) as Count " + " from ("
					+ query + ") t";
		}
		log.debug(sql);
		HibernateSQL call = new HibernateSQL(sql);
		List list = findAll(sessionFactory.getCurrentSession(), call);
		// ��mapת����json����
		Object[] objs = (Object[]) list.get(0);
		Map<String, Object> map = new HashMap<String, Object>();
		// �Ȱ�Count�Ž�ȥ��ͷһ��������
		map.put("Count", objs[1]);
		for (int i = 2; i < objs.length; i++) {
			map.put(snames[i - 2], objs[i]);
		}
		JSONObject result = (JSONObject) new JsonTransfer().MapToJson(map);
		log.debug(result);
		return result;
	}

	private List findAll(org.hibernate.classic.Session session,
			HibernateSQL call) {
		return (List) call.doInHibernate(session);
	}

	@POST
	@Path("sql/{pageIndex}/{pageSize}")
	// ��sql��ʽִ�к󣬻�ȡһҳ���ݣ��ֶ�����SQL������
	public JSONArray queryPostSQLPage(@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex, String query) {
		JSONArray array = new JSONArray();
		log.debug(query + ", size=" + pageSize + ", index=" + pageIndex);
		// ���pageIndexС��0��ֱ�ӷ���
		if (pageIndex < 0) {
			return array;
		}
		HibernateSQLCall sqlCall = new HibernateSQLCall(query, pageIndex,
				pageSize);
		sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
		List<Map<String, Object>> list = executeFind(
				sessionFactory.getCurrentSession(), sqlCall);
		for (Map<String, Object> map : list) {
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		log.debug(array.toString());
		return array;
	}

	@POST
	@Path("sql/{names}/{pageIndex}/{pageSize}")
	// ��sql��ʽִ�к󣬻�ȡһҳ���ݣ��ֶ�����ǰ̨����
	public JSONArray queryPostSQLPage(@PathParam("names") String names,
			@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex, String query) {
		JSONArray array = new JSONArray();
		log.debug(query + ", size=" + pageSize + ", index=" + pageIndex);
		// ���pageIndexС��0��ֱ�ӷ���
		if (pageIndex < 0) {
			return array;
		}
		HibernateSQLCall sqlCall = new HibernateSQLCall(query, pageIndex,
				pageSize);
		List list = executeFind(sessionFactory.getCurrentSession(), sqlCall);
		for (Object obj : list) {
			// ������sql���ִ�к�ı���
			String[] snames = names.split(",");
			// ��sql�����ת����map
			Object[] objs = (Object[]) obj;
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < objs.length; i++) {
				map.put(snames[i], objs[i]);
			}
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		log.debug(array.toString());
		return array;
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
					 else if (value instanceof PersistentList) {
						 PersistentList set = (PersistentList) value;
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

		// �������б�ת����Json����
		private Object ToJson(PersistentList list) {
			// û���صļ��ϵ�����
			if (!list.wasInitialized()) {
				return JSONObject.NULL;
			}
			JSONArray array = new JSONArray();
			for (Object obj : list) {
				if(obj == null)
					continue;
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject)MapToJson(map);
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

	@POST
	@Path("{entity}/{id}")
	public String txDelete(@PathParam("entity") String entityName,
			@PathParam("id") int id) {
		String hql = "delete from " + entityName + " where id=" + id;
		log.debug(hql);
		bulkUpdate(sessionFactory.getCurrentSession(), hql);
		return "ok";
	}

	// �����ļ�
	@SuppressWarnings("finally")
	@Path("savefile")
	@POST
	public String txSavefile(byte[] file,
			@QueryParam("FileName") String filename,
			@QueryParam("BlobId") String blob_id,
			@QueryParam("EntityName") String EntityName,
			@QueryParam("SaveMode") String SaveMode,
			@QueryParam("BusinessType") String BusinessType) {
		String result = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("filename", filename);
			map.put("id", blob_id);
			map.put("saveMode", SaveMode);
			map.put("businesstype", BusinessType);
			// �ļ�����ģʽ
			if (SaveMode != null && SaveMode.equals("file")) {
				String fileFullPath = this.getFilePath() + "\\" + blob_id + "_"
						+ filename;
				FileHelper.createFile(fileFullPath, file);
				map.put("filefullpath", fileFullPath);
			} else {
				map.put("blob", Hibernate.createBlob(file));
			}
			this.sessionFactory.getCurrentSession().saveOrUpdate(EntityName,
					map);
			this.sessionFactory.getCurrentSession().flush();
			result = "";
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		} finally {
			return result;
		}
	}

	private String getFilePath() {
		String result = "";
		String hql = "from t_singlevalue where name='�ļ��洢·��'";
		List list = executeFind(sessionFactory.getCurrentSession(),
				new HibernateCall(hql, 0, 10));
		if (list.size() != 1) {
			throw new RuntimeException("��ֵ �洢�ļ�·�� ������");
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		result = map.get("value").toString();
		return result;
	}

	// ���ͼƬ
	@Path("file/{blobid}")
	public String getImage(@Context HttpServletResponse response,
			@PathParam("blobid") String blobid) {
		try {
			List list = sessionFactory.getCurrentSession().find(
					"from t_blob where id='" + blobid + "'");
			if (list.size() == 0)
				return "";
			Map map = (Map) list.get(0);
			// ����ļ���
			String filename = (String) map.get("filename");
			filename = URLEncoder.encode(filename, "UTF-8");
			//��ȡ�洢��ģʽ
			String SaveMode=(String) map.get("saveMode");
			//��ȡ�ļ���·��
			String fileFullPath=(String) map.get("filefullpath");
			InputStream is;
			// �ļ�����ģʽ
			if (SaveMode != null && SaveMode.equals("file")) {
			
				is=new FileInputStream(fileFullPath);
				
			} else {
				// ����ļ�
				Blob file = (Blob) map.get("blob");
				is = file.getBinaryStream();
				
			}
			
			
			// ���ļ�������������Ӧ����
			response.setHeader("Pragma","No-cache"); 
			response.setHeader("Cache-Control","no-cache"); 
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ filename + "\"");
			OutputStream os = new BufferedOutputStream(
					response.getOutputStream());
			transformStream(is, os);
			is.close();
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// ��excel
	@GET
	@Path("excel/{hql}/{count}/{cols}")
	// ��ȡһҳ����
	public String queryToExcel(@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("hql") String query, @PathParam("count") int count,
			@PathParam("cols") String cols) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFFont font = workbook.createFont();
			font.setColor((short) HSSFFont.COLOR_NORMAL);
			font.setBoldweight((short) HSSFFont.BOLDWEIGHT_BOLD);
			// ���ø�ʽ
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment((short) HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setFont(font);
			HSSFRow row = null;
			HSSFCell cell = null;
			// ������0�� ����
			int rowNum = 0;
			row = sheet.createRow((short) rowNum);
			String[] colsStr = cols.split("\\|");
			for (int titleCol = 0; titleCol < colsStr.length; titleCol++) {
				cell = row.createCell((short) (titleCol));
				// ����������
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				// �����е��ַ���Ϊ����
				cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
				// ��������
				String[] names = colsStr[titleCol].split(":");
				// �б���,��������Ϊ����������Ϊ�ֶ�����
				if (names.length > 1) {
					cell.setCellValue(names[1]);
				} else {
					cell.setCellValue(colsStr[titleCol]);
				}
				cell.setCellStyle(cellStyle);
			}

			// �����ļ�����
			int pageSize = 20;
			int pageCount = count % pageSize == 0 ? (count / pageSize)
					: (count / pageSize) + 1;
			for (int i = 0; i <= pageCount; i++) {
				List list = null;
				// ��sql:��ʼ��˵����ִ��sql��䣬����ִ��hql���
				if (query.startsWith("sql:")) {
					String sql = query.substring(4);
					List objList = executeFind(
							sessionFactory.getCurrentSession(),
							new HibernateSQLCall(sql, i, pageSize));
					list = new ArrayList();
					// ��sql���Ľ��ת����map
					for (Object obj : objList) {
						// ��sql�����ת����map
						Object[] objs = (Object[]) obj;
						Map<String, Object> map = new HashMap<String, Object>();
						for (int j = 0; j < objs.length; j++) {
							map.put("col" + j, objs[j]);
						}
						list.add(map);
					}
				} else {
					list = executeFind(sessionFactory.getCurrentSession(),
							new HibernateCall(query, i, pageSize));
				}
				for (int j = 0; j < list.size(); j++) {
					Object obj = list.get(j);
					// �ѵ���mapת����JSON����
					Map<String, Object> map = (Map<String, Object>) obj;
					JSONObject json = (JSONObject) new JsonTransfer()
							.MapToJson(map);
					rowNum++;
					row = sheet.createRow((short) rowNum);
					for (int z = 0; z < colsStr.length; z++) {
						// �õ�����
						String[] names = colsStr[z].split(":");
						// �б������ֶ���Ϊ��һ������������ֶ���
						String colName = colsStr[z];
						if (names.length > 1) {
							colName = names[0];
						}
						String data = "";
						if (map.get(colName) != null) {
							data = map.get(colName).toString();
						}
						cell = row.createCell((short) (z));
						// ����������
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// �����е��ַ���Ϊ����
						cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(data);
					}
				}
			}
			// ������ʱ�ļ�
			File file = File.createTempFile("temp", ".xls");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// д�ļ�
			workbook.write(fileOutputStream);
			// �建��
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/octet-stream;charset=\"gb2312\"");
			response.addHeader("Content-Disposition", "attachment;filename=\" "
					+ file.getName() + "\"");
			// ���ļ�������������Ӧ����
			byte[] b = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			BufferedOutputStream bos = new BufferedOutputStream(
					response.getOutputStream());
			while (bis.read(b) != -1) {
				bos.write(b);
			}
			bis.close();
			file.deleteOnExit();
			bos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return "";
	}

	@GET
	@Path("fapiao/{hql}/{count}")
	// ��ȡһҳ����
	public String queryInvoice(@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("hql") String query, @PathParam("count") int count) {
		try {
			query = query.replaceAll("\\^", "<");
			// ����xml
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("wlfpML",
					"http://www.12366.ha.cn");
			root.addAttribute("cnName", "ͨ��ģ�屨��");
			root.addAttribute("name", "String");
			root.addAttribute("version", "wlfp2010");
			root.addAttribute("schemaLocation",
					"http://www.12366.ha.cn wlfpML.xsd");
			root.addAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			Element tax = root.addElement("TAXPAYER", "http://www.12366.ha.cn"
					+ " ");
			tax.addAttribute("NSRMC", "������ȼ�����޹�˾");
			tax.addAttribute("NSRSBH", "410181712641199");
			tax.addAttribute("RECORDCOUNT", count + "");
			Element xsRecords = root.addElement("xsRecords");
			// ��ʼ����Ʊ��
			String stratPiaoHao = "";
			String endPiaoHao = "";
			String fapiaodam = "";
			// �����ļ�����
			int pageSize = 20;
			int pageCount = count % pageSize == 0 ? (count / pageSize)
					: (count / pageSize) + 1;
			for (int i = 0; i <= pageCount; i++) {
				List list = executeFind(sessionFactory.getCurrentSession(),
						new HibernateCall(query, i, pageSize));
				for (int j = 0; j < list.size(); j++) {
					Object obj = list.get(j);

					// �ѵ���mapת����JSON����
					Map<String, Object> map = (Map<String, Object>) obj;
					JSONObject json = (JSONObject) new JsonTransfer()
							.MapToJson(map);
					// ��ӵ�����ƱԪ��
					Element record = xsRecords.addElement("xsRecord");
					Element head = record.addElement("xsHead");
					// ��Ʊ����
					String fpdm = getJsonAttr(json, "f_invoiceid");
					Element fpdmElem = head.addElement("FPDM");
					fpdmElem.setText(fpdm);
					if (i == 0 && j == 0) {
						fapiaodam = fpdm;
					}
					// ��Ʊ����
					String fphm = getJsonAttr(json, "f_invoicenum");
					Element fphmElem = head.addElement("FPHM");
					fphmElem.setText(fphm);
					if (i == 0 && j == 0) {
						stratPiaoHao = fphm;
					}
					endPiaoHao = fphm;
					// ��Ʊ����3
					Element kprqElem = head.addElement("KPRQ");
					Date kpDate = (Date) map.get("f_fapiaodate");
					if (kpDate != null) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						String d = sdf.format(kpDate);
						kprqElem.setText(d);
					} else {
						kprqElem.setText("");
					}
					// ��Ʊ���
					String fpje = getJsonAttr(json, "f_money");
					Element fpjeElem = head.addElement("FPJE");
					fpjeElem.setText(fpje);
					// �û����
					String yhbh = getJsonAttr(json, "f_userid");
					Element yhbhElem = head.addElement("FKDWSBH");
					yhbhElem.setText(yhbh);
					// ��λ����
					String dwmc = getJsonAttr(json, "f_username");
					Element dwmcElem = head.addElement("FKDWMC");
					dwmcElem.setText(dwmc);
					// ��Ʊ״̬
					String fpzt = getJsonAttr(json, "f_fapiaostatue");
					if (fpzt.equals("����")) {
						fpzt = "10";
					} else if (fpzt.equals("����")) {
						fpzt = "20";
					} else if (fpzt.equals("�հ�����")) {
						fpzt = "30";
					}
					Element fpztElem = head.addElement("FPZT_DM");
					fpztElem.setText(fpzt);

					String fpztzt = getJsonAttr(json, "f_fapiaostatue");
					if (fpztzt.indexOf("����") != -1) {
						// ��������3
						Element ZFRQElem = head.addElement("ZFRQ");
						Date zfDate = (Date) map.get("f_fapiaodate");
						if (zfDate != null) {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd");
							String d = sdf.format(zfDate);
							ZFRQElem.setText(d);
						} else {
							ZFRQElem.setText("");
						}
					} else {
						Element ZFRQElem = head.addElement("ZFRQ ");
					}

					Element TPDMElem = head.addElement("TPDM ");
					Element TPHMElem = head.addElement("TPHM ");

					Element xsMXElem = record.addElement("xsMX");
					// <xh> �ͺ�
					Element xhElem = xsMXElem.addElement("XH");
					xhElem.setText("1");
					// ��Ʒ����
					Element mcElem = xsMXElem.addElement("SPMC");
					mcElem.setText("ú����");
					// ��λ
					Element dwElem = xsMXElem.addElement("SPDW");
					dwElem.setText("��");
					// ����
					String dj = getJsonAttr(json, "f_gasprice");
					// ����
					String num = getJsonAttr(json, "f_gas");
					// ����=���/���� ������λ
					dj = String.format("%.2f", Double.parseDouble(fpje)
							/ Double.parseDouble(num));
					if (Double.parseDouble(fpje) == 0) {
						dj = "0.0";
					}
					Element djElem = xsMXElem.addElement("SPDJ");
					djElem.setText(dj);
					Element slElem = xsMXElem.addElement("SPSL");
					slElem.setText(num);
					// ���
					Element jeElem = xsMXElem.addElement("SPJE");
					jeElem.setText(fpje);

				}
			}
			// ����
			String path = request.getSession().getServletContext()
					.getRealPath("");
			// String uuid = UUID.randomUUID().toString();
			String fname = "WLFP_TY_410181712641199_" + fapiaodam + "_"
					+ stratPiaoHao + "_" + endPiaoHao;
			path = path + "\\" + fname + ".xml";
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path),
					format);
			xmlWriter.write(document);
			xmlWriter.close();

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ fname + ".xml" + "\"");
			// ���ļ�������������Ӧ����
			File file = new File(path);
			InputStream is = new FileInputStream(file);
			OutputStream os = new BufferedOutputStream(
					response.getOutputStream());
			transformStream(is, os);
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getJsonAttr(JSONObject json, String attr) {
		try {

			if (!json.has(attr)) {
				return "";
			}
			Object obj = json.get(attr);
			if (obj == null) {
				return "";
			}
			return obj.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void transformStream(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[1024];
			// ��ȡ��ʵ�ʳ���
			int length = is.read(buffer);
			while (length != -1) {
				os.write(buffer, 0, length);
				length = is.read(buffer);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ���Դ���

	@GET
	@Path("testException")
	// ��ȡһҳ����
	public String txTestException() throws Exception {
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=1");
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=2");
		throw new XMLParseException();
	}

	@GET
	@Path("testRuntime")
	// ��ȡһҳ����
	public String txTestRuntime() {
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=1");
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=2");
		throw new ArithmeticException();
	}

	@GET
	@Path("test")
	// ��ȡһҳ����
	public String txTestSQL() {
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=1");
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id0=1");
		return "";
	}

	@GET
	@Path("test2")
	// ��ȡһҳ����
	public String txTestSQL2() {
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=1");
		bulkSqlUpdate(sessionFactory.getCurrentSession(),
				"delete from t_test where id=2");
		return "";
	}
	
	// ���ݿ�ʼ���ںͽ������ڻ��ά���ѽ��
	@GET
	@Path("upkeep/{userid}/{usertype}/{metertype}/{startmonth}/{endmonth}/{oughtamount}")
	public JSONObject upfee(@PathParam("userid") String userid,
	@PathParam("usertype") String usertype,
	@PathParam("metertype") String metertype,
	@PathParam("startmonth") String startmonth,
	@PathParam("endmonth") String endmonth,
	@PathParam("oughtamount") String oughtamount) {
	JSONObject result = new JSONObject();
	String value = "";
	try {
	UpkeepInterface upkeep = UpkeepFactory.getInstance()
	.getUpkeepComputer(usertype);
	Map map = new HashMap();
	map.put("userid", userid);
	map.put("metertype", metertype);
	map.put("consumertype", usertype);

	// ��ȡ�û����ϣ�������������ҳ���
	Session session = sessionFactory.openSession();
	session.beginTransaction();

	String sql = "select f_finabuygasdate from t_userfiles where f_userid='"
	+ userid + "'";
	HibernateSQLCall sqlCall = new HibernateSQLCall(sql, 0, 10);
	List<Object> list = (List<Object>) sqlCall.doInHibernate(session);
	String f_finabuygasdate = list.get(0) + "";
	session.getTransaction().commit();
	session.close();
	if (f_finabuygasdate != null && !f_finabuygasdate.equals("")
	&& !f_finabuygasdate.equals("null")) {
	map.put("f_finabuygasdate", f_finabuygasdate.substring(0, 10));
	}
	map.put("startmonth", startmonth);
	map.put("endmonth", endmonth);
	map.put("oughtamount", oughtamount);
	value = upkeep.computeUpkeep(map, this.sessionFactory);
	result.put("upkeep", value);

	} catch (Exception e) {
	throw new RuntimeException(e);
	}
	return result;
	}
}
