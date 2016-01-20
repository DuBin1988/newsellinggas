package com.aote.rs;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.util.CalculateStairsPrice;


@Path("iis")
@Scope("prototype")
@Component
public class IndoorInspectService {
	static Logger log = Logger.getLogger(IndoorInspectService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Path("update")
	@POST
	@Produces("application/json")
	public String updateOrSave(String stringifiedObj) {
		log.debug("����İ����¼��" + stringifiedObj);
		try {
			JSONObject row = new JSONObject(stringifiedObj);
			//uuid���򣺵�½�û�id_����ƻ���ID
			String uuid = row.getString("ID");
			String planId = row.getString("CHECKPLAN_ID");
			String state = row.getString("CONDITION");
			DeletePossiblePriorRow(uuid, state);
			if(InsertNewRow(row))
				return "{\"ok\":\"ok\"}";
			else
				return "{\"ok\":\"nok\"}";				
		} catch (JSONException e) {
			return "{\"ok\":\"nok\"}";
		}
	}


	@Path("saveRepair")
	@POST
	@Produces("application/json")
	public String saveRepairResult(String stringifiedObj) {
		log.debug("�����ά�޽����" + stringifiedObj);
		try {
			//{ID:111111, 'option1':'option1', 'option2':'option2'}
			JSONObject row = new JSONObject(stringifiedObj);
			String uuid = row.getString("ID");
			//ɾ�����п��ܵ�ά�޽��
			String hql = "delete from T_REPAIR_RESULT where INSPECTION_ID ='" + uuid +"'";
			this.hibernateTemplate.bulkUpdate(hql);
			Iterator<String> keys = row.keys();
			while(keys.hasNext())
			{
				String value = keys.next();
				if(value.equals("ID"))
					continue;
				String sql = "INSERT INTO T_REPAIR_RESULT(ID, INSPECTION_ID, CONTENT)values('" 
					+ UUID.randomUUID().toString().replace("-", "") + "', '" + uuid + "', '"
					+ value + "')";
				execSQL(sql);
			}
			String dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			//���°���״̬
			String sql = "update T_INSPECTION set REPAIR_STATE='��ά��', REPAIR_DATE='" + dt + "' where id='" + uuid +"'";
			execSQL(sql);
			return "{\"ok\":\"ok\"}";				
		} catch (Exception e) {
			return "{\"ok\":\"nok\"}";
		}
	}
	
	private void DeletePossiblePriorRow(String uuid, String state) {
		String hql = "delete from T_INSPECTION_LINE where INSPECTION_ID ='" + uuid + "_1' or INSPECTION_ID ='" + uuid + "'";
		this.hibernateTemplate.bulkUpdate(hql);
		hql = "delete from T_INSPECTION where id ='" + uuid + "_1' or id ='" + uuid + "'";
		this.hibernateTemplate.bulkUpdate(hql);
	}



	/**
	 * �����뻧��������
	 * @param row
	 * @throws JSONException 
	 */
	private boolean InsertNewRow(JSONObject row) throws JSONException {
			String uuid = row.getString("ID");
			String planId = row.getString("CHECKPLAN_ID");
			boolean isEntryInspection = row.getString("CONDITION").equals("����");
			
			//�ӱ��д��������
			Map<String, String> redundantCols = new HashMap<String, String>();
			if(isEntryInspection)
			{
				redundantCols.put("CARD_ID", row.getString("CARD_ID"));
				//redundantCols.put("CARD_ID", row.getString("CARD_ID"));
				redundantCols.put("USER_NAME", row.getString("USER_NAME"));
				redundantCols.put("ROAD", row.getString("ROAD"));
				redundantCols.put("UNIT_NAME", row.getString("UNIT_NAME"));
				redundantCols.put("CUS_DOM", row.getString("CUS_DOM"));
				redundantCols.put("CUS_DY", row.getString("CUS_DY"));
				redundantCols.put("CUS_FLOOR", row.getString("CUS_FLOOR"));
				redundantCols.put("CUS_ROOM", row.getString("CUS_ROOM"));
				redundantCols.put("TELPHONE", row.getString("TELPHONE"));
				redundantCols.put("SAVE_PEOPLE", row.getString("SAVE_PEOPLE"));
				redundantCols.put("SAVE_DATE", row.getString("DEPARTURE_TIME"));
				redundantCols.put("IC_METER_NAME", row.getString("IC_METER_NAME"));
				redundantCols.put("JB_METER_NAME", row.getString("JB_METER_NAME"));
				redundantCols.put("JB_NUMBER", row.getString("JB_NUMBER"));
				redundantCols.put("SURPLUS_GAS", row.getString("SURPLUS_GAS"));
			}
			
			String sql1 = "INSERT INTO T_INSPECTION(ID";
			String sql2 = ") VALUES('" + uuid +"'";
			//�������¼
			Map<String, String> masterMap = new HashMap<String, String>();
			Map<String, String> slaveMap = new HashMap<String, String>();
			Iterator<String> itr = row.keys();
			while(itr.hasNext())
			{
				String key = itr.next();
				if(key.equals("ID"))
					continue;
				if(key.matches(".*_\\d{1,2}$"))
					slaveMap.put(key, row.getString(key));
				else
				{
					sql1 += "," + key;
					if(key.equals("JB_NUMBER") || key.equals("SURPLUS_GAS"))
						sql2 += "," + (row.getString(key).length()>0 ? row.getString(key) :  "NULL");
					else
						sql2 += ",'" + row.getString(key).replace("'", "''") + "'";
				}
			}
			//����Ӽ�¼
			sql1 += sql2 +")";
			execSQL(sql1);
			if(isEntryInspection)
				InsertPrecaution(uuid, redundantCols, slaveMap);
			DeletePics(uuid);
			return true;
	}
	
	private void DeletePics(String uuid) {
		String hql = "from T_INSPECTION t where t.id =?";
		List lst = hibernateTemplate.find(hql, uuid);
		if(lst.size() == 0)
			return;
		Object obj = lst.get(0);
		Map<String, Object> map = (Map<String, Object>) obj;
		String photo = (String)map.get("USER_SIGN");		
		if(photo == null || photo.length()==0)
		{
			hql = "delete from T_INSPECTION where id ='" + uuid + "_sign'";
			this.hibernateTemplate.bulkUpdate(hql);
		}
		photo = (String)map.get("PHOTO_FIRST");		
		if(photo == null || photo.length()==0)
		{
			hql = "delete from T_INSPECTION where id ='" + uuid + "_1'";
			this.hibernateTemplate.bulkUpdate(hql);
		}
		photo = (String)map.get("PHOTO_SECOND");		
		if(photo == null || photo.length()==0)
		{
			hql = "delete from T_INSPECTION where id ='" + uuid + "_2'";
			this.hibernateTemplate.bulkUpdate(hql);
		}
		photo = (String)map.get("PHOTO_THIRD");		
		if(photo == null || photo.length()==0)
		{
			hql = "delete from T_INSPECTION where id ='" + uuid + "_3'";
			this.hibernateTemplate.bulkUpdate(hql);
		}
		photo = (String)map.get("PHOTO_FOUTH");		
		if(photo == null || photo.length()==0)
		{
			hql = "delete from T_INSPECTION where id ='" + uuid + "_4'";
			this.hibernateTemplate.bulkUpdate(hql);
		}
		photo = (String)map.get("PHOTO_FIFTH");	
		if(photo == null || photo.length()==0)
		{
			hql = "delete from T_INSPECTION where id ='" + uuid + "_5'";
			this.hibernateTemplate.bulkUpdate(hql);
		}
	}


	/**
	 * execute sql in hibernate
	 * @param sql
	 */
	private void execSQL(final String sql) {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                session.createSQLQuery(sql).executeUpdate();
                return null;
            }
        });		
	}


	/**
	 * ������������
	 * @param uuid
	 * @param redundantCols
	 * @param slaveMap
	 * @param db
	 */
	private void InsertPrecaution(String uuid,
			Map<String, String> redundantCols, Map<String, String> slaveMap) {
		String snippet1 = "";
		String snippet2 = "";
		Map<String, String> map = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : redundantCols.entrySet()) {
		    snippet1 += "," +  entry.getKey();
			if(entry.getKey().equals("JB_NUMBER") || entry.getKey().equals("SURPLUS_GAS"))
				snippet2 += "," + (entry.getValue().length()>0?entry.getValue():"NULL") + "";
			else
				snippet2 += ",'" + entry.getValue().replace("'", "''") + "'";
		}
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			int offset = 2;
			if(entry.getKey().charAt(entry.getKey().length()-3) == '_')
				offset = 3;
			String sql = "INSERT INTO T_INSPECTION_LINE(ID,INSPECTION_ID" + snippet1 + ",EQUIPMENT,CONTENT" 
			+  ") VALUES('" + UUID.randomUUID().toString().replace("-", "") + "','" + uuid +"'" + snippet2  +",'"
			+ entry.getKey().substring(0, entry.getKey().length()-offset) +"','"
			+ entry.getValue().replace("'", "''") + "')";
			execSQL(sql);
		}
	}
	
	@Path("CAupdate/{operator}/{department}")
	@POST
	@Produces("application/json")
	public String CAUpdateOrSave(@PathParam("operator") String operator,
								@PathParam("department") String department,
								String stringifiedObj) {
		log.debug("����İ����¼��" + stringifiedObj);
		try {
			JSONObject row = new JSONObject(stringifiedObj);
			//uuid���򣺵�½�û�id_����ƻ���ID
			String uuid = row.getString("ID");
			String planId = row.getString("CHECKPLAN_ID");
			String state = row.getString("CONDITION");
			CADeletePossiblePriorRow(uuid, state);
			if(CAInsertNewRow(row,operator,department))
				return "{\"ok\":\"ok\"}";
			else
				return "{\"ok\":\"nok\"}";				
		} catch (JSONException e) {
			return "{\"ok\":\"nok\"}";
		}
	}
	
	/**
	 * ��������û�ʵʱ����
	 */
	@GET
	@Path("CalcCurrentBalance/{UserID}/{MeterNum}")
	@Produces(MediaType.APPLICATION_JSON)
	public String CalculateCurrentBalance(@PathParam("UserID") String UserID, @PathParam("MeterNum") String MeterNum/*���γ���ָ��*/)
	{
		log.debug("����ı��γ���ָ����" + MeterNum + ", �����UserID��" + UserID);
		String FindUserFileSql = "from t_userfiles where f_userid = '" + UserID + "'";
		List<Object> UserFileList = new ArrayList<Object>();
		Map<String, Object> UserFileMap = new HashMap<String, Object>();
		String GetArrearsMoneysql = "from t_handplan where f_userid = '" + UserID + "' and f_gasmeterstyle = '����' and shifoujiaofei='��' and f_state = '�ѳ���'";
		List<Object> GetArrearsMoneyList = new ArrayList<Object>();
		List<Map<String, Object>> GetArrearsMoneyMapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> GetArrearsMoneyMap = new HashMap<String, Object>();
		String ZHYEStr = "";
		String LastRecordStr = "";
		String ArrearsMoneyStr = "";
		//�˻����
		double ZHYE = 0;
		//�ϴγ���ָ��
		double LastRecord = 0;
		//����������
		double GasNum = 0;
		//�����������
		double GasMoney = 0;
		//�ۼ�Ƿ�ѽ��
		double ArrearsMoney = 0;
		//��ǰ���� = ���� - �ۼ�Ƿ�� - �����������
		double CurrentBalance = 0;
		try {
			UserFileList = this.hibernateTemplate.find(FindUserFileSql);
			if(UserFileList.size() != 1)
			{
				//��ͨ��userid����û�������ֹһ�����򷵻ش���
				return "{\"ok\":\"err1\"}";
			}
			UserFileMap = (Map<String, Object>) UserFileList.get(0);
			ZHYEStr = UserFileMap.get("f_zhye") == null ? "0" : UserFileMap.get("f_zhye").toString();
			//�õ��˻����
			ZHYE = Double.parseDouble(ZHYEStr);
			LastRecordStr = UserFileMap.get("lastrecord") == null ? "0" : UserFileMap.get("lastrecord").toString();
			LastRecord = Double.parseDouble(LastRecordStr);
			if(Double.parseDouble(MeterNum) < LastRecord)
			{
				//�����γ���ָ��С���ϴγ���ָ�����򷵻ش���
				return "{\"ok\":\"err2\"}";
			}
			GasNum = Double.parseDouble(MeterNum) - LastRecord;
			//���㱾���������������������
			Map<String, Object> GasMoneyMap = new HashMap<String, Object>();
			GasMoneyMap = CalculateStairsPrice.CalculateGasMoney(GasNum, UserFileMap);
			String GasMoneyStr = GasMoneyMap.get("chargenum") == null ? "0" : GasMoneyMap.get("chargenum").toString();
			//�õ������������
			GasMoney = Double.parseDouble(GasMoneyStr);
			CurrentBalance = ZHYE - GasMoney;
			GetArrearsMoneyList = this.hibernateTemplate.find(GetArrearsMoneysql);
			if(GetArrearsMoneyList.size() == 0)
			{
				return "{\"ok\":\"" + CurrentBalance + "\"}";
			}
			for(int i = 0; i < GetArrearsMoneyList.size(); i++)
    		{
				GetArrearsMoneyMapList.add((Map<String, Object>) GetArrearsMoneyList.get(i));
    		}
			for(int j = 0; j < GetArrearsMoneyMapList.size(); j++)
			{
				ArrearsMoneyStr = GetArrearsMoneyMapList.get(j).get("oughtfee").toString();
				ArrearsMoney += Double.parseDouble(ArrearsMoneyStr);
			}
			CurrentBalance = ZHYE - ArrearsMoney - GasMoney;
			return "{\"ok\":\"" + CurrentBalance + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			//�����쳣���򷵻ش���
			return "{\"ok\":\"err4\"}";
		}
	}

	/**
	 * 
	 * @param road
	 * @param unit
	 * @param building
	 * @param dy
	 * @param floor
	 * @param room
	 * @param uuid
	 * @param dt
	 * @param reading
	 * @return
	 */
	@GET
	@Path("CAValidate/{newmeter}/{road}/{unit}/{building}/{dy}/{floor}/{room}/{uid}/{dt}/{reading}")
	@Produces(MediaType.APPLICATION_JSON)
	public String CAValidate(@PathParam("newmeter") String newmeter,
			@PathParam("road") String road, @PathParam("unit") String unit,
			@PathParam("building") String building, @PathParam("dy") String dy,
			@PathParam("floor") String floor, @PathParam("room") String room,
			@PathParam("uid") String uid, @PathParam("dt") String dt,
			@PathParam("reading") String reading) {
		log.debug("����Ĳ�����" + dt + "'" + reading);
		// �ڰ����¼�в�����һ�ΰ������ںͶ������������С���ϴΣ��ж��ڼ��Ƿ񻻱�
		// �������ڱ�������ϴ�
		try {
			final String sql = "select top 1 f_jbdushu, arrival_time from (select f_jbdushu, arrival_time from t_inspection "
					+ " where ROAD ='"
					+ road
					+ "' and UNIT_NAME='"
					+ unit
					+ "' and CUS_DOM='"
					+ building
					+ "' and CUS_DY='"
					+ dy
					+ "' and CUS_FLOOR='"
					+ floor
					+ "' and CUS_ROOM='"
					+ room
					+ "' and condition='����'"
					+ " ) t order by arrival_time desc";
			List list = (List) hibernateTemplate
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							SQLQuery query = session.createSQLQuery(sql);
							return query.list();
						}
					});
			// �ҵ������¼���ж����ںͻ������
			if (list.size() == 1) {
				Object[] obj = (Object[]) list.get(0);
				String olddt = (String) obj[1];
				int oldreading = Integer.parseInt(obj[0].toString());
				// ������ڴ��ڵ�ǰ���죬���ش���
				if (olddt.compareTo(dt) > 0)
					return "{\"ok\":\"nok\", \"msg\":\"�Ѵ��ڵ�ǰ�û��ĳ�ǰ���졣�ϴΰ�������Ϊ"
							+ olddt + "\"}";
				// �����������ڵ�ǰ�������ж�ʱ������Ƿ񻻱�
				int newreading = Integer.parseInt(reading);
				if (oldreading > newreading  && !newmeter.equals("true")) {
					return "{\"ok\":\"nok\", \"msg\":\"�ϴγ���������ڱ��Ρ��ϴλ������Ϊ"	+ oldreading + "\"}";
				}
			}
			return "{\"ok\":\"ok\"}";
		} catch (Exception e) {
			return "{\"ok\":\"nok\"}";
		}
	}

	private void CADeletePossiblePriorRow(String uuid, String state) {
		String hql = "delete from T_INSPECTION_LINE where INSPECTION_ID ='" + uuid.replace("'", "") + "_1' or INSPECTION_ID =" + uuid;
		this.hibernateTemplate.bulkUpdate(hql);
		hql = "delete from T_INSPECTION where id ='" + uuid.replace("'", "") +"_1' or id =" + uuid;
		this.hibernateTemplate.bulkUpdate(hql);	
	}

	/**
	 * �����뻧��������
	 * @param row
	 * @throws JSONException 
	 */
	private boolean CAInsertNewRow(JSONObject row,String operator,String department) throws JSONException {
			String uuid = row.getString("ID");
			String condition = row.getString("CONDITION");
			String userid = row.getString("f_userid");
			String road = row.getString("ROAD");
			String unitName = row.getString("UNIT_NAME");
			String cusDom = row.getString("CUS_DOM");
			String cusDy = row.getString("CUS_DY");
			String cusFloor = row.getString("CUS_FLOOR");
			String cusRoom = row.getString("CUS_ROOM");
			String checkPlanID = row.getString("CHECKPLAN_ID");
			String checkDate = row.getString("ARRIVAL_TIME");
			
			int conditionFlag = 0;
			if(condition.equals("'����'"))
			{
				conditionFlag = 3;
			}
			else if(condition.equals("'�ܼ�'"))
			{
				conditionFlag = 33;
			}
			else if(condition.equals("'����'"))
			{
				conditionFlag = 65;
			}
			if(row.has("NEEDS_REPAIR"))
			{
				if(row.getString("NEEDS_REPAIR").equals("'��'"))
				{
					conditionFlag += 16;
				}
			}
			String suggestions = null; 
			String sql1 = "INSERT INTO T_INSPECTION(ID";
			String sql2 = ") VALUES(" + uuid;
			//�������¼
			Iterator<String> itr = row.keys();
			while(itr.hasNext())
			{
				String key = itr.next();
				if(key.equals("suggestions"))
				{
					suggestions = row.getString(key);
					continue;
				}
				if(key.equals("ID"))
					continue;
				sql1 += "," + key;
				sql2 += "," + row.getString(key);
			}
			//����Ӽ�¼
			sql1 += sql2 +")";
			execSQL(sql1);
			//����T_IC_SAFECHECK_PAPER�а�������û��İ���״̬
			if(road != null && unitName != null && cusDom != null && cusDy != null && cusFloor != null && cusRoom != null && checkPlanID != null)
			{
				String updateSql = "update T_IC_SAFECHECK_PAPER set CONDITION = '" + conditionFlag + "' where f_userid = " + userid;
				execSQL(updateSql);
			}
			sql1 = "update t_inspection set f_anjianriqi= substring(arrival_time,1,10) where id=" + uuid;
			execSQL(sql1);
			//�����û����������Ǽ��´ΰ�������
			String updateSQL = "update t_userfiles set f_toBeInspected = '��', " +
							"f_nextCheckDate = DATEADD(YEAR, 1, SUBSTRING(" + checkDate + ", 1, 10)) " +
							"where f_userid = " + userid;
			execSQL(updateSQL);
			StringBuffer sb = new StringBuffer("");
			if(suggestions != null)
			{
				JSONArray lines = new JSONArray(suggestions);
				for(int i=0; i<lines.length(); i++)
				{
					JSONObject line = lines.getJSONObject(i);
					sql1 = "insert into T_INSPECTION_LINE(id,EQUIPMENT,PARAM,VALUE,NAME,BZ,INSPECTION_ID) VALUES('" + UUID.randomUUID().toString() 
					+ "','" + line.getString("EQUIPMENT") 
					+ "','" + line.getString("PARAM") 
					+ "','" + line.getString("VALUE") 
					+ "','" + line.getString("NAME") 
					+ "','" + line.getString("BZ") 
					+ "','" + line.getString("INSPECTION_ID")  + "')";
					execSQL(sql1);
					sb.append(line.getString("EQUIPMENT")+":"+line.getString("NAME")+"\n");
				}
				String state = "'δ�ɷ�'";
				String accepter = null;
				String substate = null;
				String sender = null;
				String senddate = null;
				String sendtime = null;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d = new Date();
				String date = sdf.format(d);
				List list = (List) hibernateTemplate
						.execute(new HibernateCallback() {
							public Object doInHibernate(Session session)
									throws HibernateException {
								SQLQuery query = session.createSQLQuery("select value from t_singlevalue where name='����ģʽ'");
								return query.list();
							}
						});
				if(list.size()==1){
					String sta = list.get(0) + "";
					if("ֱ���ɷ���APPģʽ".equals(sta)){
						state = "'���ɷ�'";
						accepter = row.getString("REPAIRMAN");
						substate = "'δ����'";
						sender = "'"+operator+"'";
						senddate = "'"+date.substring(0,10)+"'";
						sendtime = "'"+date.substring(11,19)+"'";
					}
				}
				String uuid2 = UUID.randomUUID().toString();
				updateSQL = "insert into t_repairsys(id,anjianid,f_userid,f_username,f_linktype,"
						+ "f_address,f_metergasnums,f_operator,f_usertype,f_meetunit,"
						+ "f_repairreason,f_department,f_teltype,f_repairtype,"
						+ "f_dealtype,f_dealonline,f_havacomplete,f_havadeal,f_meternumber,"
						+ "f_gasmeteraccomodations,f_metertype,f_aroundmeter,f_recorddate,f_reporttime,"
						+ "f_cucode,f_state,f_accepter,f_substate,f_sender,"
						+ "f_senddate,f_sendtime)" +
						"VALUES('"+uuid2+"',"+uuid+","+userid+","+row.getString("f_consumername")+","+row.getString("f_consumerphone")+",'"+
						rejectionNull(road)+rejectionNull(unitName)+rejectionNull(cusDom)+rejectionNull(cusDy)+rejectionNull(cusFloor)+rejectionNull(cusRoom)+"',"+row.getString("f_buygas")+",'"+operator+"',(select f_usertype from t_userfiles where f_userid="+userid+"),"+row.getString("f_department")+",'"+
						sb.toString()+"','"+department+"',"+"'����תά��'"+","+"'����תά��'"+","+
						"'����תά��'"+","+"'����תά��'"+","+"'δ���'"+","+"'��'"+","+row.getString("f_biaohao")+","+
						row.getString("f_jbdushu")+","+row.getString("f_meter_type")+","+row.getString("f_rqbiaoxing")+",'"+date.substring(11,19)+"','"+date.substring(0,10)+"','aj"+
						new Date().getTime()+"',"+state+","+accepter+","+substate+","+sender+","+
						senddate+","+sendtime+")";
				execSQL(updateSQL);
			}
			DeletePics(uuid.replace("'", ""));
			return true;
	}
	private String rejectionNull(String s){
		if("'null'".equals(s.toLowerCase()))
			return "";
		return s.replace("'", "");
	}
}
