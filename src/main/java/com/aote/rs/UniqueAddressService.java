package com.aote.rs;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * JSON service 把按模式生成的地址转换成树
 * 
 * @author lgy
 *
 */
@Path("uas")
@Scope("prototype")
@Component
public class UniqueAddressService {
	static Logger log = Logger.getLogger(UniqueAddressService.class);
	
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test() throws JSONException {
		//sort the address
		String sql = "from t_gasaddress order by f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment";
		List gridList = hibernateTemplate.find(sql);
		for (Object srcRow : gridList) {
			// 把单个map转换成JSON对象
			Map<String, Object> map = (Map<String, Object>) srcRow;
			//if the address is in tree, continue
			Object addressId = map.get("ID");
			sql = "from t_design_address where addressId='" + addressId +"'";
			List cntList = hibernateTemplate.find(sql);
			if(cntList.size()>0)
				continue;
			//else
			else
			{
				//get residential area id
				Object residentialAreaId = getResidentialAreaId(map.get("f_districtname"));
				if(residentialAreaId == null)
				{
					log.debug(map.get("f_districtname") + "在小区表中不存在。");
					continue;
				}
				//check B-U-F
				String json = findBUFR(residentialAreaId, 
						map.get("f_cusDom") + " " + map.get("f_cusDy") + " " + map.get("f_cusFloor"),   //BUF
						map.get("f_cusDom") + " " + map.get("f_cusDy"),  //BU
						map.get("f_cusDom"));  //B
				if(json == null)
				{
					log.debug(map.get("f_cusDom") + " " + map.get("f_cusDy") + " " + map.get("f_cusFloor") + "在树中查找失败。");
					continue;
				}
				
				JSONObject obj = new JSONObject(json);
				//创建楼、单元、层、房号
				if(obj.getString("bufr").equals("BUFR"))
					createBUFR(residentialAreaId, map.get("f_cusDom"), map.get("f_cusDy"), map.get("f_cusFloor"), map.get("f_apartment"), addressId);
				if(obj.getString("bufr").equals("BUF"))
					createR(obj.getString("data"), map.get("f_apartment"), addressId);
				if(obj.getString("bufr").equals("BU"))
					createFR(obj.getString("data"), map.get("f_cusFloor"), map.get("f_apartment"), addressId);
				if(obj.getString("bufr").equals("B"))
					createUFR(obj.getString("data"), map.get("f_cusDy"), map.get("f_cusFloor"), map.get("f_apartment"), addressId);
			}
		}	
		return "";
	}

	private void createUFR(String pid, Object U, Object F, Object R, Object addressId) {
		//get max U code of this residentialArea
		final String sql = "select max(iid) as tt, max(rank) as aaa from t_design_address where level=1 and pid='" + pid + "'";
		List list = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery query = session.createSQLQuery(sql);
						return query.list();
					}
				});
		
		int rank = 0;
		String iid;
		Object[] maxiid = (Object[])list.get(0);
		if(maxiid[0] == null)
		{
			iid = pid + "000001";
		}
		else
		{	
			iid = maxiid[0].toString();
			iid = iid.substring(0,  iid.length()-6) + padLeft(6, (Integer.parseInt(iid.substring(iid.length()-6)) +1)+"");
			rank = Integer.parseInt(maxiid[1].toString()) + 1;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("iid", iid);
		map.put("name", U.toString());
		map.put("level", 1);
		map.put("pid", pid);
		map.put("rank", rank);
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);

		map = new HashMap<String, Object>();
		map.put("iid", iid+"000001");
		map.put("name", F.toString());
		map.put("level", 2);
		map.put("pid", iid);
		map.put("rank", 1);
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);

		map = new HashMap<String, Object>();
		map.put("iid", iid+"000001000001");
		map.put("name", R.toString());
		map.put("level", 3);
		map.put("pid", iid+"000001");
		map.put("rank", 1);
		map.put("addressId", addressId.toString());
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);
	}

	private String padLeft(int width, String num) {
		return String.format("%6s", num).replace(' ', '0');
	}

	private void createFR(String pid, Object F, Object R, Object addressId) {
		//get max F code of this residentialArea
		final String sql = "select max(iid) as tt, max(rank) as aaa from t_design_address where level=2 and pid='" + pid + "'";
		List list = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery query = session.createSQLQuery(sql);
						return query.list();
					}
				});

		int rank = 1;
		String iid;
		Object[] maxiid = (Object[])list.get(0);
		if(maxiid[0] == null)
		{
			iid = pid + "000001";
		}
		else
		{	
			iid = maxiid[0].toString();
			iid = iid.substring(0,  iid.length()-6) + padLeft(6, (Integer.parseInt(iid.substring(iid.length()-6)) +1)+"");
			rank = Integer.parseInt(maxiid[1].toString()) + 1;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("iid", iid);
		map.put("name", F.toString());
		map.put("level", 2);
		map.put("pid", pid);
		map.put("rank", rank);
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);

		map = new HashMap<String, Object>();
		map.put("iid", iid+"000001");
		map.put("name", R.toString());
		map.put("level", 3);
		map.put("pid", iid);
		map.put("rank", 1);
		map.put("addressId", addressId.toString());
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);
	}
	
	private void createR(String pid, Object R, Object addressId) {
		//get max R code of this residentialArea
		final String sql = "select max(iid) as tt, max(rank) as aaa from t_design_address where level=3 and pid='" + pid + "'";
		List list = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery query = session.createSQLQuery(sql);
						return query.list();
					}
				});
		
		int rank = 1;
		String iid;
		Object[] maxiid = (Object[])list.get(0);
		if(maxiid[0] == null)
		{
			iid = pid + "000001";
		}
		else
		{	
			iid = maxiid[0].toString();
			iid = iid.substring(0,  iid.length()-6) + padLeft(6, (Integer.parseInt(iid.substring(iid.length()-6)) +1)+"");
			rank = Integer.parseInt(maxiid[1].toString()) + 1;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("iid", iid);
		map.put("name", R.toString());
		map.put("level", 3);
		map.put("pid", pid);
		map.put("rank", rank);
		map.put("addressId", addressId.toString());
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);
	}
	
	private void createBUFR(Object residentialAreaId, Object B, Object U, Object F, Object R, Object addressId) {
		//get max building code of this residentialArea
		final String sql = "select max(iid) as tt, max(rank) as aaa from t_design_address where level=0 and pid='" + residentialAreaId + "'";
		List list = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery query = session.createSQLQuery(sql);
						return query.list();
					}
				});
		int rank = 1;
		String iid;
		Object[] maxiid = (Object[])list.get(0);
		if(maxiid[0] == null)
		{
			iid = residentialAreaId.toString();
			iid = padLeft(6, iid);
			iid = iid +"000001";
		}
		else
		{
			iid = maxiid[0].toString();
			iid = iid.substring(0,  iid.length()-6) + padLeft(6, (Integer.parseInt(iid.substring(iid.length()-6)) +1)+"");
			rank = Integer.parseInt(maxiid[1].toString()) + 1;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("iid", iid);
		map.put("name", B.toString());
		map.put("level", 0);
		map.put("pid", residentialAreaId.toString());
		map.put("rank", rank);
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);

		map = new HashMap<String, Object>();
		map.put("iid", iid+"000001");
		map.put("name", U.toString());
		map.put("level", 1);
		map.put("pid", iid);
		map.put("rank", 1);
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);

		map = new HashMap<String, Object>();
		map.put("iid", iid+"000001000001");
		map.put("name", F.toString());
		map.put("level", 2);
		map.put("pid", iid+"000001");
		map.put("rank", 1);
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);
		
		map = new HashMap<String, Object>();
		map.put("iid", iid+"000001000001000001");
		map.put("name", R.toString());
		map.put("level", 3);
		map.put("pid", iid+"000001000001");
		map.put("rank", 1);
		map.put("addressId", addressId.toString());
		map.put("remark", "");
		hibernateTemplate.save("t_design_address", map);
	}

	private String findBUFR(Object residentialAreaId, String BUF, String BU, Object B) {
		final String sql = "with rt AS "
        + " ( "
        + " select IID, Name, Remark, Level, PID, ResidentialAreaName, rank, ID, addressId, CAST(name AS varchar(2001)) as sn from t_design_address "
        + " where level=0 and pid='" + residentialAreaId + "' "
        + " union ALL "
        + " select t.IID, t.Name, t.Remark, t.Level, t.PID, t.ResidentialAreaName, t.rank, t.ID, t.addressId,  CAST(t2.sn AS varchar(1000)) + ' ' + CAST(t.name AS varchar(1000)) as sn from t_design_address t INNER JOIN rt t2 "
        + " on t.pid = t2.iid "
        + " ) "
        + " select * FROM rt where sn ='" + BUF + "' or sn ='" + BU + "' or sn ='" + B + "' order by sn desc";
		
		List list = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery query = session.createSQLQuery(sql);
						return query.list();
					}
				});
		if(list.size() == 0)
			return "{\"bufr\": \"BUFR\", \"data\": {}}";
		else
		{
			for(Object obj : list)
			{
				Object[] map = (Object[])obj;
				if(map[9].equals(BUF))
					return "{\"bufr\": \"BUF\", \"data\": \"" + map[0] + "\"}";
				if(map[9].equals(BU))
					return "{\"bufr\": \"BU\", \"data\": \"" + map[0] + "\"}";
				if(map[9].equals(B))
					return "{\"bufr\": \"B\", \"data\": \"" + map[0] + "\"}";
			}
			return null;
		}
	}

	private Object getResidentialAreaId(Object name) {
		String sql = "from t_area where f_districtname='" + name +"'";
		List cntList = hibernateTemplate.find(sql);
		if(cntList.size()>0)
		{
			Map<String, Object> map = (Map<String, Object>) cntList.get(0);
			return map.get("id");
		}
		else
			return null;
	}
}
