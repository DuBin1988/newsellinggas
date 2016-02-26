package com.aote.rs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;


@Path("iesgas")
@Component
public class iesGas {
	static Logger log = Logger.getLogger(iesGas.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public String hostc="http://127.0.0.1:8080/rs/reiesgas/";	
	public String gasmeterstyle="f_gasmeterstyle in ('��������','������','Զ�̱�','Զ����','���ű�') ";
	int CONNECTION_TIMEOUT=1000*10;
	int SO_TIMEOUT=1000*60;
		
	/**
	 * execute sql in hibernate
	 * @param sql
	 */
	private void execSQL(final String sql) {
		try {
			hibernateTemplate.execute(new HibernateCallback() {
	            public Object doInHibernate(Session session)
	                    throws HibernateException {
	                session.createSQLQuery(sql).executeUpdate();
	                return null;
	            }
	        });
		} catch (Exception e) {
		}        		
	}
	
	/**
	 * execute sql in hibernate
	 * @param sql ����Ӱ�������
	 */
	private Object execSQLnum(final String sql) {
		try {
			return hibernateTemplate.execute(new HibernateCallback() {
				 public Object doInHibernate(Session session)
		                    throws HibernateException {
					 return session.createSQLQuery(sql).executeUpdate();
		            }
	        });
		} catch (Exception e) {
			return null;
		}		
	}
	
	/**
	 * List
	 * execute sql in hibernate
	 * @param sql 
	 */
	private List relList(final String sql) {
		try {
			return (List)hibernateTemplate.execute(new HibernateCallback() {
				public Object doInHibernate(Session session)throws HibernateException {
					SQLQuery query = session.createSQLQuery(sql);
					return query.list();
				}
			});
		} catch (Exception e) {
			return null;
		}		
	}
	
	
	
	
	//����״̬ ---------------------------------------------------------------------------------------------------->
	//����״̬ - ���� --���շ�
	/**
	 * 
	 * @param Obj 
	 *  [{"customer_code":"11055492","status":"����","record_date":"2015-12-21 16:45:59"},
	 *   {"customer_code":"11055493","status":"�쳣","record_date":"2015-02-21 16:45:59"}]
	 * @param f_userid ���
	 * @param f_tablestatus ���״̬
	 * @param f_statusdate  ʱ��
	 * @return
	 */
	@Path("table/statusp")
	@POST
	@Produces("application/json")
	public String tablestatusp(String Obj){
		log.debug("����ϵͳ�ϴ����״̬��" + Obj);
		try {
			JSONArray rows=new JSONArray(Obj);
			JSONArray rerows=new JSONArray();
			Date now = new Date();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String f_tbdate=fmt.format(now);
			for(int i=0;i<rows.length();i++){
				String f_userid="";
				String returnvalue="0";
				try{
					JSONObject row = rows.getJSONObject(i);
					f_userid = row.getString("customer_code");
					String f_tablestatus = row.getString("status");
					String f_tablesmessage = row.getString("message");
					if("0".equals(f_tablestatus)){
						f_tablestatus="����";
					}else if("1".equals(f_tablestatus)){
						f_tablestatus="Ƿ��";
					}else if("2".equals(f_tablestatus)){
						f_tablestatus="�쳣";
					}					
					String f_statusdate = row.getString("record_date");
					execSQL("insert into t_cbstatuslog (f_userid,f_tablestatus,f_statusdate,f_tablesmessage,f_inertdate,f_username,f_address,meter_phone,f_meternumber,f_gaswatchbrand,f_aliasname) "
							+ "(select top 1 '"+f_userid+"','"+f_tablestatus+"','"+f_statusdate+"','"+f_tablesmessage+",'"+f_tbdate+"',f_username,f_address,meter_phone,f_meternumber,f_gaswatchbrand,f_aliasname from t_userfiles where f_userid='"+f_userid+"' and "+gasmeterstyle+")");
					execSQL("update t_userfiles set f_tablestatus='"+f_tablestatus+"',f_tablesmessage='"+f_tablesmessage+"',f_statusdate='"+f_statusdate+"' where f_userid='"+f_userid+"' and "+gasmeterstyle);
				}catch(Exception e){
					returnvalue="1";
				}
				rerows.put(new JSONObject("{\"customer_code\":\""+f_userid+"\",\"returnvalue\":\""+returnvalue+"\"}"));
			}			
			execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+"table/statusp"+"','"+"table/statusp"+"','"+"table/statusp"+"','"+Obj.replace("'", "/")+"','"+""+"','"+"��������"+"','"+"�������"+"','"+rerows.toString()+"')");
			return rerows.toString();
		} catch (JSONException e) {
			return "[]";
		}
	}

	//�������� ---------------------------------------------------------------------------------------------------->
	//�������� - ���� --���շ�
	/**
	 * 
	 * @param Obj 
	 *  [{"customer_code":"11055492","read_date":"0","read_val":"0"},
	 *   {"customer_code":"11055493","read_date":"0","read_val":"0"}]
	 *  customer_code��ţ�read_date����ʱ�䣬read_val����ָ��
	 *  t_cbnum ������������
	 * @return returnvalue 0���գ�1δ����
	 */
	@Path("table/cbnum")
	@POST
	@Produces("application/json")
	public String cbnum(String Obj){
		log.debug("����ϵͳÿ�춨ʱ�ϴ�������Ϣ ͬ����" + Obj);
		try {
			JSONArray rows=new JSONArray(Obj);
			JSONArray rerows=new JSONArray();
			Date now = new Date();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String f_tbdate=fmt.format(now);
			for(int i=0;i<rows.length();i++){
				String f_userid = "";
				String returnvalue="0";
				try{
					JSONObject row = rows.getJSONObject(i);
					f_userid = row.getString("customer_code");
					String f_gasdate = row.getString("read_date");
					Double f_gasnum = row.getDouble("read_val");
					Double jval = -0.0001;
					try {
						jval = row.getDouble("read_jval");
					} catch (Exception e) {
						jval = -0.0001;
					}															
					final String sql = "select top 1 f_userid from t_cbnum where f_userid='"+f_userid+"' and f_gasdate='"+f_gasdate+"' and f_gasnum="+f_gasnum+" and jval="+jval+"";
					List list = (List)hibernateTemplate.execute(new HibernateCallback() {
								public Object doInHibernate(Session session)throws HibernateException {
									SQLQuery query = session.createSQLQuery(sql);
									return query.list();
								}
							});
					// �������ݼ�¼�Ƿ����
					if (list.size() != 1){
						execSQL("insert into t_cbnum (f_userid,f_gasdate,f_gasnum,jval,f_inertdate,f_meternumber,f_gaswatchbrand,f_aliasname) "
								+ "(select top 1 '"+f_userid+"','"+f_gasdate+"',"+f_gasnum+","+jval+",'"+f_tbdate+"',f_meternumber,f_gaswatchbrand,f_aliasname from t_userfiles where f_userid='"+f_userid+"' and "+gasmeterstyle+")");
						execSQL("update t_userfiles set lastinputdate_cb='"+f_gasdate+"',lastinputgasnum_cb="+f_gasnum+",lastinputjval_cb="+jval+" where f_userid='"+f_userid+"' and "+gasmeterstyle);
					}
				}catch(Exception e){
					returnvalue="1";
				}
				rerows.put(new JSONObject("{\"customer_code\":\""+f_userid+"\",\"returnvalue\":\""+returnvalue+"\"}"));
			}			
			execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+"table/cbnum"+"','"+"table/cbnum"+"','"+"table/cbnum"+"','"+Obj.replace("'", "/")+"','"+""+"','"+"��������"+"','"+"�������"+"','"+rerows.toString()+"')");
			return rerows.toString();
		} catch (JSONException e) {
			return "[]";
		}
	}

	//����ָ��---------------------------------------------------------------------------------------------------->
	/**
	 * 
	 * @param customer_code �û����
	 * @param type �ط�ָ�0���ã�1ͣ�ã�2�쳣��3Ƿ��
	 *        [{"customer_code":"11055492","type":"0"},{"customer_code":"11055493","type":"0"}]
	 * @author 
	 * @return
	 * @throws Exception
	 */
	@Path("table/comand")
	@POST
	@Produces("application/json")
	public String tablecomand(String Obj) throws Exception{
		log.debug("���Ų��� ͬ����" + Obj);
		return jsonpost(5,Obj,"valveControl","");
	}
	
	//���۵��� ---------------------------------------------------------------------------------------------------->
		/**
		 * @param Obj
		 * {"f_stairtype":"�Ǿ���",
		 *  "f_stair1price":"1","f_stair1amount":"100","f_stair2price":"2","f_stair2amount":"200","f_stair3price":"3","f_stair3amount":"300",
		 *  "f_stair4price":"4","f_stair4amount":"null","f_stair5price":"4","f_stair5amount":"null",
		 *  "f_stairmonths","12",
		 *  "idlist":[{"f_userid":"123456"},{"f_userid":"123456"}, {"f_userid":"123456"}]}
		 * @return
		 *  {"idlist":[{"f_userid":"123456","returnvalue":"0"}, {"f_userid":"123456","returnvalue":"0"}, {"f_userid":"123456","returnvalue":"0"}]}
		 * @throws Exception
		 */
		@Path("gasdj/comand")
		@POST
		@Produces("application/json")
		public String gasdjcomand(String Obj) throws Exception{
			log.debug("���۵��� ͬ����" + Obj);
			return jsonpost(4,Obj,"priceChange","");
		}
		

		//��ֵ���� ---------------------------------------------------------------------------------------------------->
		/**
		 * 
		 * @param Obj  mode 0��Ч��1��Ч
		 *  [{"mode":"0","customer_code":"11055492","money":"100","charge_date":"2015-12-21 16:45:59"},
		 *   {"mode":"0","customer_code":"11055492","money":"200","charge_date":"2015-12-21 16:45:59"}]
		 * @return
		 * [{"customer_code":"11055492","returnvalue":"0"},
		 *  {"customer_code":"11055492","returnvalue":"0"}]
		 * @throws Exception
		 */
		@Path("gascz/comand")
		@POST
		@Produces("application/json")
		public String gasczcomand(String Obj) throws Exception{
			log.debug("��ֵ����������ͬ����" + Obj);
			return jsonpost(3,Obj,"chargesMeter","");
		}	
		
		//��ֵ���� ---------------------------------------------------------------------------------------------------->
		/**
		 * 
		 * @param Obj  mode 0��Ч��1��Ч
		 *  [{"mode":"0","customer_code":"11055492","money":"100","charge_date":"2015-12-21 16:45:59"},
		 *   {"mode":"0","customer_code":"11055492","money":"200","charge_date":"2015-12-21 16:45:59"}]
		 * @return
		 * [{"customer_code":"11055492","returnvalue":"0"},
		 *  {"customer_code":"11055492","returnvalue":"0"}]
		 * @throws Exception
		 */
		@Path("gasbk/comand")
		@POST
		@Produces("application/json")
		public String bkcomand(String Obj) throws Exception{
			log.debug("��ֵ����������ͬ����" + Obj);
			return jsonpost(6,Obj,"chargesMeter","");
		}	
		
		//����״̬---------------------------------------------------------------------------------------------------->
		/**
		 * 
		 * @param Obj  type 2����/1ͣ��/0����
	     *        [{"customer_code":"11055492","type":"0"},{"customer_code":"11055493","type":"0"}]
		 * @return
		 *  [{"customer_code":"11055492","returnvalue":"0"},
		 *   {"customer_code":"11055492","returnvalue":"0"}]
		 * @throws Exception
		 */
		@Path("user/status")
		@POST
		@Produces("application/json")
		public String userstatus(String Obj) throws Exception{
			log.debug("ͬ���û�״̬��" + Obj);
			return jsonpost(2,Obj,"customerStateChange","");
		}
	
		//��������  ---------------------------------------------------------------------------------------------------->
		/**
		 * 
		 * @param Obj  
		 *  [{"type":"0����/1���/2����","f_userid":"123456" ,"customer_name":"","create_date":"","phone":"","customer_type":"","use_nature":"������","area_name":"","steal_no":"steal_no","start_val":"start_val","f_stairtype":"������" ,"f_stair1price":"1","f_stair1amount":"100","f_stair2price":"2","f_stair2amount":"200","f_stair3price":"3","f_stair3amount":"300","f_stair4price":"4","f_stair4amount":"null","f_stair5price":"4","f_stair5amount":"null","f_stairmonths","12"},
	         {"type":"0����/1���/2����","f_userid":"123456" ,"customer_name":"","create_date":"","phone":"","customer_type":"","use_nature":"������","area_name":"","steal_no":"steal_no","start_val":"start_val","f_stairtype":"������" ,"f_stair1price":"1","f_stair1amount":"100","f_stair2price":"2","f_stair2amount":"200","f_stair3price":"3","f_stair3amount":"300","f_stair4price":"4","f_stair4amount":"null","f_stair5price":"4","f_stair5amount":"null","f_stairmonths","12"}
	        ]
		 * @return
		 * [{"customer_code":"11055492","returnvalue":"0"},
		 *  {"customer_code":"11055492","returnvalue":"0"}]
		 * @throws Exception
		 */
		@Path("user/comand")
		@POST
		@Produces("application/json")
		public String usercomand(String Obj) throws Exception{
			log.debug("ͬ���û�������" + Obj);
			return jsonpost(1,Obj,"syncFile","");
		}
		
		/**
		 * ͳһ�������������Post���� json���飬ֻ����ID������״̬
		 */
		public String jsonpost(int type,String Obj,String url,String sql) {
			Date now = new Date();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String f_tbdate=fmt.format(now);
			getcanshu();
			boolean danganisdanjia=false;
			try {
				//�������
				String jsonString="";
				//�����Ƿ�ˢ�³���ϵͳ���棬�����ӿ�ʹ�� , ��ָ������Ϊ1������ϵͳ��Ҫ��ʱˢ�»��档�� �����ȫ������ָ��Ϊ0������ϵͳ����Ҫ��ʱˢ�»��档
				String refreshCache="0";
				JSONArray rows=new JSONArray(Obj);
				switch (type)
				{
				   case 1: //���ͬ�����ǵ���������ȡ�����ݿ�״̬��1�ģ���ӵ�����������
					   try{
						   List list=null;
						   if("[]".equals(Obj)){
							   list=relList("select u.f_userid f_userid,f_username ,f_districtname ,f_address ,f_dateofopening ,f_phone ,f_usertype ,f_gasproperties ,f_area ,terminal_name ,f_flownum ,meter_phone ,lastinputgasnum ,u.f_meternumber f_meternumber,f_gasmeterstyle ,f_gasmetermanufacturers ,u.f_stairtype f_stairtype,j.f_stair1price f_stair1price,j.f_stair1amount f_stair1amount,j.f_stair2price f_stair2price,j.f_stair2amount f_stair2amount,j.f_stair3price f_stair3price,j.f_stair3amount f_stair3amount,j.f_stair4price f_stair4price,j.f_stairmonths f_stairmonths,f_danganstatus,old_steal_no,old_val,lastinputgasnum,f_channel_type,switch_op,refreshCache "+
														"from t_userfiles u "+
														"left join "+
														"(select f_stairtype ,f_stair1price ,f_stair1amount ,f_stair2price ,f_stair2amount ,f_stair3price ,f_stair3amount ,f_stair4price ,f_stairmonths from t_stairprice) j "+
														"on u.f_stairtype=j.f_stairtype "+
														"where u.f_danganreturnvalue='1' and u.f_userid is not null and u."+gasmeterstyle);
								
						   }else{
							   JSONObject rowid = rows.getJSONObject(0);
							   list=relList("select u.f_userid f_userid,f_username ,f_districtname ,f_address ,f_dateofopening ,f_phone ,f_usertype ,f_gasproperties ,f_area ,terminal_name ,f_flownum ,meter_phone ,lastinputgasnum ,u.f_meternumber f_meternumber,f_gasmeterstyle ,f_gasmetermanufacturers ,u.f_stairtype f_stairtype,j.f_stair1price f_stair1price,j.f_stair1amount f_stair1amount,j.f_stair2price f_stair2price,j.f_stair2amount f_stair2amount,j.f_stair3price f_stair3price,j.f_stair3amount f_stair3amount,j.f_stair4price f_stair4price,j.f_stairmonths f_stairmonths,f_danganstatus,old_steal_no,old_val,lastinputgasnum,f_channel_type,switch_op,refreshCache "+
														"from t_userfiles u "+
														"left join "+
														"(select f_stairtype ,f_stair1price ,f_stair1amount ,f_stair2price ,f_stair2amount ,f_stair3price ,f_stair3amount ,f_stair4price ,f_stairmonths from t_stairprice) j "+
														"on u.f_stairtype=j.f_stairtype "+
														"where u.f_danganreturnvalue='1' and u.f_userid is not null and u."+gasmeterstyle +" and f_userid='"+rowid.getString("customer_code")+"'");
						   }
						   rows=new JSONArray();
						   if (null != list && list.size() >= 1){
								 for(int i=0;i<list.size();i++){
										Object[] tmp1 = (Object[])list.get(i);
										String f_channel_type= tmp1[29]+""; 
										String f_userid= tmp1[0]+""; 
										String f_username= tmp1[1]+""; 
										String f_districtname= tmp1[2]+""; 
										String f_address= tmp1[3]+""; 
										String f_dateofopening= tmp1[4]+""; 
										String f_phone= tmp1[5]+""; 
										String f_usertype= tmp1[6]+""; 
										String f_gasproperties= tmp1[7]+""; 
										String f_area= tmp1[8]+""; 
										String terminal_name= tmp1[9]+""; 
										String f_flownum= tmp1[10]+""; 
										String meter_phone= tmp1[11]+""; 
										String f_gasmeteraccomodations= tmp1[12]+""; 
										String f_meternumber= tmp1[13]+""; 
										String f_gasmeterstyle= tmp1[14]+""; 
										String f_gasmetermanufacturers= tmp1[15]+""; 
										String f_stairtype= tmp1[16]+""; 
										String f_stair1price= tmp1[17]+""; 
										String f_stair1amount= tmp1[18]+""; 
										String f_stair2price= tmp1[19]+""; 
										String f_stair2amount= tmp1[20]+""; 
										String f_stair3price= tmp1[21]+""; 
										String f_stair3amount= tmp1[22]+""; 
										String f_stair4price= tmp1[23]+""; 
										String f_stairmonths= tmp1[24]+"";
										String f_danganstatus= tmp1[25]+"";
										String type_da="0";
										String old_steal_no= "";
										String old_val= "";
										String switch_op= tmp1[30]+"";
										refreshCache= tmp1[31]!=null?tmp1[31]+"":"0";
										//if("��������".equals(f_gasmeterstyle))  //���������޸�
										//	f_districtname= "��������Ƭ��";  
										
										if("����".equals(f_danganstatus)){
											type_da="0";
											danganisdanjia=true;
										}
										if("���".equals(f_danganstatus)){
											type_da="1";
											//danganisdanjia=true;
										}
										if("����".equals(f_danganstatus))
											type_da="2";
										if("����".equals(f_danganstatus)){
											type_da="3";
											old_steal_no= tmp1[26]+"";
											old_val= tmp1[27]+""; 
										}
										rows.put(new JSONObject("{type:\""+type_da+
												  "\",channel_type:\"" + f_channel_type +
												  "\",customer_code:\"" + f_userid +
			                                      "\",area_name:\"" + f_districtname +
			                                      "\",customer_name:\"" + f_username +
			                                      "\",address:\"" + f_address +
			                                      "\",create_date:\"" + f_dateofopening +
			                                      "\",phone:\"" + f_phone +
			                                      "\",customer_type:\"" + f_usertype +
			                                      "\",use_nature:\"" + f_stairtype +
			                                      "\",meter_type_name:\"" + f_gasmeterstyle +
			                                      "\",terminal_name:\"" + terminal_name.trim() +
			                                      "\",caliber:\"" + f_flownum +
			                                      "\",meter_phone:\"" + meter_phone.trim() +
			                                      "\",steal_no:\"" + f_meternumber.trim() +
			                                      "\",start_val:\"" + f_gasmeteraccomodations +
			                                      "\",old_steal_no:\"" +old_steal_no+
			                                      "\",old_val:\"" + old_val+
			                                      "\",vender:\"" + f_gasmetermanufacturers +
			                                      "\",switch_op:\"" + switch_op +
			                                    /*  "\",price_type:\"" + f_stairtype +
			                                      "\",money1:\"" + f_stair1price +
			                                      "\",limit1:\"" + f_stair1amount +
			                                      "\",money2:\"" + f_stair2price +
			                                      "\",limit2:\"" + f_stair2amount +
			                                      "\",money3:\"" + f_stair3price +
			                                      "\",limit3:\"" + f_stair3amount +
			                                      "\",money4:\"" + f_stair4price +
			                                      "\",limit4:\"999999999" +
			                                      "\",money5:\"" + f_stair4price +
			                                      "\",limit5:\"999999999" +
			                                      "\",cycle:\"" + f_stairmonths + */
																	"\"}"));								
								 }
						   }
						}catch(Exception e){
							e.printStackTrace();
						}
					   jsonString="{\"refreshCache\":\""+refreshCache+"\", \"row\":"+rows.toString()+"}";
					   if("[]".equals(rows.toString()))
						   jsonString="[]";
				      break;
				   case 2: //���ͬ�������û�״̬������ȡ�����ݿ�״̬��1�ģ���ӵ�����������
					   try{
						   List list=null;
						   if("[]".equals(Obj)){
							   list=relList("select f_userid ,f_userstate "+
														"from t_userfiles "+
														"where f_userstatus='1' and "+gasmeterstyle);
								
						   }else{
							   JSONObject rowid = rows.getJSONObject(0);
							   list=relList("select f_userid ,f_userstate "+
										"from t_userfiles "+
										"where f_userstatus='1' and "+gasmeterstyle +" and f_userid='"+rowid.getString("customer_code")+"'");
						   }
						   rows=new JSONArray();
						   // �������ݼ�¼�Ƿ����
						   if (null != list && list.size() >= 1){
								for(int i=0;i<list.size();i++){
									Object[] tmp1 = (Object[])list.get(i);
									String f_userid= tmp1[0]+""; 
									String f_userstate= tmp1[1]+"";
									if("����".equals(f_userstate))
										rows.put(new JSONObject("{type:\"2\",customer_code:\"" + f_userid + "\"}"));
									if("ͣ��".equals(f_userstate))
										rows.put(new JSONObject("{type:\"1\",customer_code:\"" + f_userid + "\"}"));
									if("����".equals(f_userstate))
										rows.put(new JSONObject("{type:\"0\",customer_code:\"" + f_userid + "\"}"));
								}
						  }
						}catch(Exception e){}
					   jsonString=rows.toString();
				       break;
				   case 3: //���ͬ�����ǳ�ֵ������ȡ�����ݿ�״̬��1�ģ���ӵ�����������
					   try{
						   List list=null;
						   if("[]".equals(Obj)){
							   list=relList("select f_userid,f_grossproceeds,f_deliverydate_tb,f_payfeevalid,id,f_useful from t_sellinggas "
							   		+ "where f_status_tb='1' and "
							   		+ "f_userid in ("
							   		+ "select f_userid from t_userfiles where f_jllx='����' and " +gasmeterstyle+" )");
								
						   }else{
							   JSONObject rowid = rows.getJSONObject(0);
							   list=relList("select f_userid,f_grossproceeds,f_deliverydate_tb,f_payfeevalid,id,f_useful from t_sellinggas "
								   		+ "where f_status_tb='1' and "
								   		+ "f_userid in ("
								   		+ "select f_userid from t_userfiles where f_jllx='����' and " +gasmeterstyle+" )" +" and f_userid='"+rowid.getString("customer_code") +"' and id='"+rowid.getString("id")+"'");
						   }
						   rows=new JSONArray();
						   // �������ݼ�¼�Ƿ����
						   if (null != list && list.size() >= 1){
								for(int i=0;i<list.size();i++){
									Object[] tmp1 = (Object[])list.get(i);
									String f_userid = tmp1[0]+"";
									String f_grossproceeds = tmp1[1]+"";
									String f_deliverydate = tmp1[2]+"";
									String f_payfeevalid = tmp1[3]+"";
									String f_useful = tmp1[5]+"";
									String id = tmp1[4]+"";
									if(f_useful.indexOf("����")<0 && f_useful.indexOf("�۷�")<0){
										rows.put(new JSONObject("{mode:\"0\",customer_code:\"" + f_userid +
																"\",money:\"" + f_grossproceeds +
																"\",charge_date:\"" + f_deliverydate +
																"\",id:\"" + id +
																"\"}"));
									}else{
										rows.put(new JSONObject("{mode:\"1\",customer_code:\"" + f_userid +
												"\",money:\"" + f_grossproceeds +
												"\",charge_date:\"" + f_deliverydate +
												"\",id:\"" + id +
												"\"}"));										
									}
								}								
						   }
						}catch(Exception e){}
					   	jsonString=rows.toString();
				       	break;
				   case 4: //���ͬ�����ǽ��ݱ��������ȡ�����ݿ�״̬��1�ģ���ӵ�����������
					   try{
						   try{
							    JSONObject rowid = rows.getJSONObject(0);
							    if("[]".equals(rowid.getString("idlist"))){ 
							    	if("2".equals(rowid.getString("type"))){
										List list=relList("select f_userid from t_userfiles where f_jllx='����' and f_stairtype='"+rowid.getString("price_type")+"' and "+gasmeterstyle);
										// �������ݼ�¼�Ƿ����
										if (null != list && list.size() >= 1){
											execSQL("update t_userfiles set f_returnvaluedj='1',f_stairtype='"  + rowid.getString("price_type")
													+ "',f_stair1amount='" + rowid.getString("limit1")
													+ "',f_stair1price='" + rowid.getString("money1")
													+ "',f_stair2amount='" + rowid.getString("limit2")
													+ "',f_stair2price='" + rowid.getString("money2")
													+ "',f_stair3amount='" + rowid.getString("limit3")
													+ "',f_stair3price='" + rowid.getString("money3")
													+ "',f_stair4price='" + rowid.getString("money4")
													+ "',f_stairmonths='" + rowid.getString("cycle")
													+ "' where f_stairtype='"+rowid.getString("price_type")+"' and "+gasmeterstyle);
											JSONArray tmp0=new JSONArray();
											for(int i=0;i<list.size();i++){											
												tmp0.put(new JSONObject("{customer_code:\"" + (String)list.get(i) +"\"}"));
											}
											rowid.put("idlist", tmp0);
										}else{
											rowid.put("type", "0");
											//rows=new JSONArray("[]");
										}
							    	}
								}else{ 
									if("1".equals(rowid.getString("type"))){									
										JSONArray sa=new JSONArray(rowid.getString("idlist"));
										JSONObject so=sa.getJSONObject(0);
										String sw=so.getString("search");
										List list=relList("select f_userid from t_userfiles where f_jllx='����' and f_stairtype='"+rowid.getString("price_type")+"' and "+gasmeterstyle +" and "+sw);
										// �������ݼ�¼�Ƿ����
										if (null != list && list.size() >= 1){
											execSQL("update t_userfiles set f_returnvaluedj='1',f_stairtype='"  + rowid.getString("price_type")
													+ "',f_stair1amount='" + rowid.getString("limit1")
													+ "',f_stair1price='" + rowid.getString("money1")
													+ "',f_stair2amount='" + rowid.getString("limit2")
													+ "',f_stair2price='" + rowid.getString("money2")
													+ "',f_stair3amount='" + rowid.getString("limit3")
													+ "',f_stair3price='" + rowid.getString("money3")
													+ "',f_stair4price='" + rowid.getString("money4")
													+ "',f_stairmonths='" + rowid.getString("cycle")
													+ "' where f_stairtype='"+rowid.getString("price_type")+"' and "+gasmeterstyle +" and "+sw);
											JSONArray tmp0=new JSONArray();
											for(int i=0;i<list.size();i++){
												tmp0.put(new JSONObject("{customer_code:\"" + (String)list.get(i) +"\"}"));
											}
											rowid.put("idlist", tmp0);
										}else{
											rowid.put("type", "0");
											rowid.put("idlist", "[]");
											//rows=new JSONArray("[]");
										}
									}
								}
						   }catch(Exception e){}
							//����֮ǰδͬ���ģ�
						   if("[]".equals(Obj)){
							   List list=relList("SELECT f_stairtype, f_stair1amount, f_stair1price, f_stair2amount, f_stair2price, f_stair3amount, f_stair3price, f_stair4price,f_stairmonths "+
										"FROM t_stairprice where f_stairtype in (select f_stairtype from t_userfiles where f_returnvaluedj='1' and f_jllx='����' and "+gasmeterstyle+" group by f_stairtype)");
								// �������ݼ�¼�Ƿ����
								if (null != list && list.size() >= 1){
									for(int i=0;i<list.size();i++){
										Object[] tmp1 = (Object[])list.get(i);
										String price_type= tmp1[0]+"";
										String money1= tmp1[2]+"";
										String limit1= tmp1[1]+"";
										String money2= tmp1[4]+"";
										String limit2= tmp1[3]+"";
										String money3= tmp1[6]+"";
										String limit3= tmp1[5]+"";
										String money4= tmp1[7]+"";
										String limit4= "";
										String money5= tmp1[7]+"";
										String limit5= "";
										String cycle= tmp1[8]+"";
										JSONObject row_tmp=new JSONObject("{type:\"1\",price_type:\"" + price_type +
			                                      "\",money1:\"" + money1 +
			                                      "\",limit1:\"" + limit1 +
			                                      "\",money2:\"" + money2 +
			                                      "\",limit2:\"" + limit2 +
			                                      "\",money3:\"" + money3 +
			                                      "\",limit3:\"" + limit3 +
			                                      "\",money4:\"" + money4 +
			                                      "\",limit4:\"999999999" +
			                                      "\",money5:\"" + money4 +
			                                      "\",limit5:\"999999999" +
			                                      "\",cycle:\"" + cycle +
	
			                                      "\"}");
										List list_t1 =relList("select f_userid from t_userfiles where f_returnvaluedj='1' and f_jllx='����' and f_stairtype='"+price_type+"' and "+gasmeterstyle);
										// �������ݼ�¼�Ƿ����
										if (null != list_t1 && list_t1.size() >= 1){
											JSONArray tmp0=new JSONArray();
											for(int m=0;m<list_t1.size();m++){
												tmp0.put(new JSONObject("{customer_code:\"" + (String)list_t1.get(m) +"\"}"));
											}
											row_tmp.put("idlist", tmp0);
											rows.put(row_tmp);
										}
										
									}
								}	
						   }						   
						}catch(Exception e){}
					   jsonString=rows.toString();
				       break;
				   case 5: //���ͬ�����Ǳ�ָ�����ȡ�����ݿ�״̬��1�ģ���ӵ�����������
					   try{
						   List list=null;
						   if("[]".equals(Obj)){
							   list=relList("select f_userid ,f_operate_zl "+
										"from t_userfiles "+
										"where switch_op='2' and f_returnvalueoperate='1' and "+gasmeterstyle);
								
						   }else{
							   JSONObject rowid = rows.getJSONObject(0);
							   list=relList("select f_userid ,f_operate_zl "+
										"from t_userfiles "+
										"where switch_op='2' and f_returnvalueoperate='1' and "+gasmeterstyle +" and "+rowid.getString("search")+"");
						   }
						   rows=new JSONArray();
						   // �������ݼ�¼�Ƿ����
						   if (null != list && list.size() >= 1){
								for(int i=0;i<list.size();i++){
									Object[] tmp1 = (Object[])list.get(i);
									String f_userid= tmp1[0]+""; 
									String f_operate_zl= tmp1[1]+"";
									if("Ƿ��".equals(f_operate_zl))
										rows.put(new JSONObject("{type:\"3\",customer_code:\"" + f_userid + "\"}"));
									if("�쳣".equals(f_operate_zl))
										rows.put(new JSONObject("{type:\"2\",customer_code:\"" + f_userid + "\"}"));
									if("ͣ��".equals(f_operate_zl))
										rows.put(new JSONObject("{type:\"1\",customer_code:\"" + f_userid + "\"}"));
									if("����".equals(f_operate_zl))
										rows.put(new JSONObject("{type:\"0\",customer_code:\"" + f_userid + "\"}"));
								}
						    }
						}catch(Exception e){}
					   jsonString=rows.toString();
				       break;
				   case 6: //���ͬ�����ǲ��ѡ��۷ѣ�����ȡ�����ݿ�״̬��1�ģ���ӵ�����������
					   try{
						   List list=null;
						   if("[]".equals(Obj)){
							   list=relList("select f_userid,f_grossproceeds,f_deliverydate_tb,f_payfeetype,id from t_cbgas "
							   		+ "where f_status_tb='1' and "
							   		+ "f_userid in ("
							   		+ "select f_userid from t_userfiles where f_jllx='����' and " +gasmeterstyle+" )");
								
						   }else{
							   JSONObject rowid = rows.getJSONObject(0);
							   list=relList("select f_userid,f_grossproceeds,f_deliverydate_tb,f_payfeetype,id from t_cbgas "
								   		+ "where f_status_tb='1' and "
								   		+ "f_userid in ("
								   		+ "select f_userid from t_userfiles where f_jllx='����' and " +gasmeterstyle+" )" +" and f_userid='"+rowid.getString("customer_code") 
								   		+"' and f_deliverydate_tb='"+rowid.getString("f_deliverydate_tb")
								   		+"' and f_grossproceeds='"+rowid.getString("f_grossproceeds")
								   		+"' and f_sgoperator='"+rowid.getString("f_sgoperator")
								   		+"' and f_payfeetype='"+rowid.getString("f_payfeetype")
								   		+"'");
						   }
						   rows=new JSONArray();
						   // �������ݼ�¼�Ƿ����
						   if (null != list && list.size() >= 1){
								for(int i=0;i<list.size();i++){
									Object[] tmp1 = (Object[])list.get(i);
									String f_userid = tmp1[0]+"";
									String f_grossproceeds = tmp1[1]+"";
									String f_deliverydate = tmp1[2]+"";
									String f_payfeetype = tmp1[3]+"";
									String id = tmp1[4]+"";
									if(f_payfeetype.indexOf("����")<0 && f_payfeetype.indexOf("�۷�")<0){
										rows.put(new JSONObject("{mode:\"0\",customer_code:\"" + f_userid +
																"\",money:\"" + f_grossproceeds +
																"\",charge_date:\"" + f_deliverydate +
																"\",id:\"" +"B"+ id +
																"\"}"));
									}else{
										rows.put(new JSONObject("{mode:\"1\",customer_code:\"" + f_userid +
												"\",money:\"" + "-"+f_grossproceeds +
												"\",charge_date:\"" + f_deliverydate +
												"\",id:\"" +"B"+ id +
												"\"}"));										
									}
								}								
						   }
						}catch(Exception e){}
					   	jsonString=rows.toString();
				       	break;
				   default: 
					   break;
				}
				
				//��������
				if(null != jsonString && !"[]".equals(jsonString) && !"".equals(jsonString)){					
					DefaultHttpClient httpclient = new DefaultHttpClient();
					httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);//����ʱ��
					httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);//���ݴ���ʱ��
				    HttpPost getRequest = new HttpPost(hostc +url);
				    getRequest.setHeader("Content-type", "application/json");
				    getRequest.setEntity(new StringEntity(jsonString, "UTF8"));
				    try {
				    	HttpResponse httpResponse = httpclient.execute(getRequest);
					    HttpEntity entity = httpResponse.getEntity();
					    int code = httpResponse.getStatusLine().getStatusCode();
					    if(200 == code  && entity != null){
					    	try {
					    		JSONArray rerows=new JSONArray(EntityUtils.toString(entity,"UTF8"));
								for(int i=0;i<rerows.length();i++){
									try{
										JSONObject rerow = rerows.getJSONObject(i);
										//ͨ�÷�������
										boolean issql=true;
										String f_returnvalue = rerow.getString("returnvalue");
										String f_userid = rerow.getString("customer_code");
										//�������ʹ���								
										String id="";
										String f_stairtype="";
										String f_operate_status="";
										String f_operate_date="";
										if(type==4){
											f_userid = rerow.getString("customer_code");
											sql="update t_userfiles set f_returnvaluedj='"+f_returnvalue+"' where f_userid='"+f_userid+"'" ;
										}else{
											f_userid = rerow.getString("customer_code");
											if(type==1){
												sql="update t_userfiles set f_danganreturnvalue='"+f_returnvalue+"' where f_userid='"+f_userid+"'" ;										
											}
											if(type==2){
												/*if("0".equals(f_returnvalue)){
													f_operate_status="����";
												}else if("1".equals(f_returnvalue)){
													f_operate_status="�ط�";
												}
												f_operate_date = rerow.getString("operate_date"); */
												sql="update t_userfiles set f_userstatus='"+f_returnvalue+"' where f_userid='"+f_userid+"'" ;
											}
											if(type==3){
												id = rerow.getString("id");
												sql="update t_sellinggas set f_status_tb='"+f_returnvalue+"' where f_userid='"+f_userid+"' and id='"+id+"'" ;
											}
											if(type==5){
												/*if("0".equals(f_returnvalue)){
													f_operate_status="����";
												}else if("1".equals(f_returnvalue)){
													f_operate_status="�ط�";
												}
												f_operate_date = rerow.getString("operate_date"); */
												sql="update t_userfiles set f_returnvalueoperate='"+f_returnvalue+"' where f_userid='"+f_userid+"'";
											}
											if(type==6){
												id = rerow.getString("id").replace("B", "");
												sql="update t_cbgas set f_status_tb='"+f_returnvalue+"' where f_userid='"+f_userid+"' and id='"+id+"'" ;
											}
										}
										if(issql){
												execSQL(sql);
												//�����������ٷ���һ�ε���
												if (danganisdanjia && "0".equals(f_returnvalue)) {
													try {
														execSQL("update t_userfiles set f_returnvaluedj='1' where f_userid='"+f_userid+"'");
														jsonpost(4,"[]","priceChange","");
													} catch (Exception e) {
														// TODO: handle exception
													}													
												}
										}
									}catch(Exception e){}
								}
								execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+gasmeterstyle.replace("'", "/")+"','"+hostc+url+"','"+url+"','"+Obj.replace("'", "/")+"','"+jsonString.replace("'", "/")+"','"+"����ɹ�"+"','"+"���ݴ�������"+"','"+rerows.toString().replace("'", "/")+"')");
								return "{\"ok\":\"ok\"}";
							} catch (JSONException e) {
								execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+gasmeterstyle.replace("'", "/")+"','"+hostc+url+"','"+url+"','"+Obj.replace("'", "/")+"','"+jsonString.replace("'", "/")+"','"+"����ɹ�"+"','"+"����json���ݣ���������"+"','"+""+"')");
								//e.printStackTrace();
								return "{\"err\":\"http\"}";
							}
					      }
					    execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+gasmeterstyle.replace("'", "/")+"','"+hostc+url+"','"+url+"','"+Obj.replace("'", "/")+"','"+jsonString.replace("'", "/")+"','"+"����ɹ�"+"','"+"״̬��:"+code+"�����߷�������Ϊ��"+"','"+""+"')");
					    return "{\"ok\":\"noredata\"}";
					} catch (Exception e) {
						execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+gasmeterstyle.replace("'", "/")+"','"+hostc+url+"','"+url+"','"+Obj.replace("'", "/")+"','"+jsonString.replace("'", "/")+"','"+"�������"+"','"+"����ʱ�����߷������ݳ�ʱ"+"','"+""+"')");
					    return "{\"ok\":\"errhttp\"}";
					}
				}
				//execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+gasmeterstyle.replace("'", "/")+"','"+hostc+url+"','"+url+"','"+Obj.replace("'", "/")+"','"+jsonString.replace("'", "/")+"','"+"û������"+"','"+"jsonΪ��"+"','"+""+"')");
			    return "{\"ok\":\"nohttp\"}";
			}catch(Exception e){
				execSQL("insert into t_cbtblog (f_tbdate,f_gasmeterstyle,f_tburl,f_tburi,f_json,f_tbjson,f_tbhttp,f_tbstatus,f_rejson) values ('"+f_tbdate+"','"+gasmeterstyle.replace("'", "/")+"','"+hostc+url+"','"+url+"','"+Obj.replace("'", "/")+"','"+""+"','"+"û������"+"','"+"json��������"+"','"+""+"')");
				//e.printStackTrace();
				return "{\"err\":\"post\"}";
			}
		}

		//ִ��SQL����
		public void chaobiaodan(String sql) {
			execSQL(sql);
		}
		
		//���õĲ���
		public void getcanshu() {
			try {
				Map map = this.findsinglevalue("Զ����URL"); 
				Map map1 = this.findsinglevalue("���ͬ������");
				Map map2 = this.findsinglevalue("Զ����URL���ӳ�ʱ");
				Map map3 = this.findsinglevalue("Զ����URL���ݴ��䳬ʱ");
				if(map != null){
					hostc=map.get("value").toString();
				}
				if(map1 != null){
					gasmeterstyle=map1.get("value").toString().trim()+" ";
				}
				if(map2 != null){
					CONNECTION_TIMEOUT=Integer.parseInt(map2.get("value").toString().trim());
				}
				if(map3 != null){
					SO_TIMEOUT=Integer.parseInt(map3.get("value").toString().trim());
				}
			} catch (Exception e) {}
		}
		
		//��ѯ��Ҫ���ɳ����¼������
		public String getcbjson(String cbdata){
			try{
				final String sql_1 = "select h.f_userid,cm.f_gasnum,cm.f_gasdate,h.lastinputgasnum,h.scinputdate,cm.cid,cm.jval from  t_handplan h "+
									 "left join "+
									 "(select c.f_userid,IsNull(c.f_gasnum,0)f_gasnum,c.f_gasdate,c.id cid,IsNull(c.jval,0)jval from t_cbnum c "+
									 "right join "+
									 "(select f_userid f_userid,max(f_gasdate) f_gasdate from t_cbnum where f_userid is not null and f_gasdate<='"+cbdata+"' group by f_userid) m "+
									 "on c.f_userid=m.f_userid and c.f_gasdate=m.f_gasdate) cm "+
									 "on h.f_userid=cm.f_userid "+
									 "where f_state ='δ����' and shifoujiaofei ='��' and h."+gasmeterstyle+" ";
				List list = (List)hibernateTemplate.execute(new HibernateCallback() {
							public Object doInHibernate(Session session)throws HibernateException {
								SQLQuery query = session.createSQLQuery(sql_1);
								return query.list();
							}
						});
				// �������ݼ�¼�Ƿ����
				if (list.size() >= 1){
					JSONArray rows =new JSONArray();
					for(int i=0;i<list.size();i++){
						Object[] tmp = (Object[])list.get(i);
						String f_userid= tmp[0]+""; 
						String f_gasnum= tmp[1]+"";
						String lastinputgasnum= tmp[3]+"";
						String lastinputdate= tmp[2]+"";
						String cid= tmp[5]+"";
						String jval= tmp[6]+"";
						if(null !=f_gasnum && !"".equals(f_gasnum) && !"null".equals(f_gasnum) && !"NULL".equals(f_gasnum)){
							rows.put(new JSONObject("{userid:\""+f_userid+"\",reading:\"" + f_gasnum+"\",cid:\"" + cid+"\",jval:\"" + jval+"\",lastinputdate:\"" + lastinputdate +"\",lastreading:\"" + lastinputgasnum + "\"}"));
							execSQL("update t_cbnum set f_isuse='1' where id='"+cid+"'");
						}
					}
					return rows.toString();
				}
				list.clear();
			}catch(Exception e){}
			return "[]";
		}
		
		//���ҵ�ֵ
		private Map<String, Object> findsinglevalue(String name) {
			String singlevalue = "from t_singlevalue where name='" + name + "'";
			List<Object> singlevalueList = this.hibernateTemplate.find(singlevalue);
			if (singlevalueList.size() != 1) {
				return null;
			}
			return (Map<String, Object>) singlevalueList.get(0);
		}
		
}
