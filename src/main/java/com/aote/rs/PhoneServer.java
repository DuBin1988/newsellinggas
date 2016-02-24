package com.aote.rs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.aspectj.apache.bcel.generic.NEW;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.hql.ast.tree.FromClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Path("phone")
@Component
public class PhoneServer {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@GET
	@Path("download/{f_repairer2}/{first}")//f_repairer2=f_accepter
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray tx_Download(@PathParam("f_repairer2") String f_repairer2,
			@PathParam("first") String first) {
		JSONArray array = new JSONArray();
		String hql="from t_repairsys where f_havadeal='��' and f_havacomplete in ('δ���','�����ɵ�') and (f_accepter=? or f_accepter='����ƽ̨')";
		if(first.equals("No")){
			hql+=" and f_downloadstatus is null"; 
		}
		List<Object> list = this.hibernateTemplate.find(hql,f_repairer2);
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = new JSONObject();
			try {
				json.put("f_userid",map.get("f_userid"));//�û�ID
				json.put("f_username",map.get("f_username"));//�û�����
				json.put("f_address",map.get("f_address"));//�û���ַ
				json.put("f_usertype",map.get("f_usertype"));//�û����
				json.put("f_linktype",map.get("f_linktype"));//��ϵ��ʽ
				json.put("f_unitname",map.get("f_unitname"));//��λ
				
				json.put("f_sender",map.get("f_sender"));//�ɵ���
				json.put("f_senddate",map.get("f_senddate"));//�ɵ�����
				json.put("f_sendtime",map.get("f_sendtime"));//�ɵ�ʱ��
				json.put("f_cucode",map.get("f_cucode"));//���ޱ��
				json.put("f_repairtype",map.get("f_repairtype"));//��������
				json.put("f_phone",map.get("f_phone"));//����绰
				json.put("f_repairreason","������Ϣ:\n"+map.get("f_repairreason")+"\n\n�豸��Ϣ:\n��Ʒ����: "+map.get("f_chanpinlx")+",��ƷƷ��: "+map.get("f_chanpinpp")+"\n��Ʒ�ͺ�: "+map.get("f_chanpinxinghao")+",ʹ������: "+map.get("shiyongzhouqi")+"\n��Ʒ����: "+map.get("f_chanpinguzhang")+",ά�޲���: "+map.get("f_weixiubujian"));  //��������
				json.put("f_stopremark",map.get("f_stopremark"));//��ע
				
				json.put("f_meternumber",map.get("f_meternumber"));//���
				json.put("f_metertype",map.get("f_metertype"));//���ͺ�
				json.put("f_aroundmeter",map.get("f_aroundmeter"));//���ұ�
				json.put("f_lastrecord",map.get("f_lastrecord"));//�����
				json.put("f_gasmeteraccomodations",map.get("f_gasmeteraccomodations"));//�����
				json.put("f_metergasnums",map.get("f_metergasnums"));//�ۼƹ�����
//				json.put("f_fireopening",map.get("f_fireopening"));//ͨ������
//				json.put("f_restrictsparenum",map.get("f_restrictsparenum"));//�޹�����������
//				json.put("f_forebuygas",map.get("f_forebuygas"));//�ϴι�����
				if("����ƽ̨".equals(map.get("f_accepter")))
					json.put("f_downloadstatus","������");//�ۼƹ�����
				else
					json.put("f_downloadstatus","����");//�ۼƹ�����
				json.put("f_workingdays",map.get("f_workingdays"));//�ۼƹ�����
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(json);
			//��һ�����ؼ�¼ʱ�����޸�
			try {
				if(map.get("f_downloadstatus")==null||map.get("f_downloadstatus")==""){
					//�Ƿ������ع�				
					hql = "update t_repairsys set f_downloadstatus ='��' where f_cucode=? and f_accepter=?";
					hibernateTemplate.bulkUpdate(hql, new Object[]{map.get("f_cucode"),map.get("f_accepter")});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
						
		}
		//����ά��Ա������Ϣ
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			hql = "update repairsman set online ='����',onlinetime='"+formatter.format(new Date())+"' where man=?";
			hibernateTemplate.bulkUpdate(hql, new Object[]{f_repairer2});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}
	
	//��ȡ�����Ĺ���
	@GET
	@Path("revgongdan/{f_repairer2}/{first}")//f_repairer2=f_accepter
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray tx_revgongdan(@PathParam("f_repairer2") String f_repairer2,
			@PathParam("first") String first) {
		JSONArray array = new JSONArray();
		String hql="from t_repairsys where f_havadeal='��' and (f_accepter=? or f_accepter='����ƽ̨') and f_havacomplete in ('δ���','�����ɵ�') and f_downloadstatus='�����ѳ���'";
		List<Object> list = this.hibernateTemplate.find(hql,f_repairer2);
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = new JSONObject();
			try {
				json.put("f_userid",map.get("f_userid"));//�û�ID
				json.put("f_cucode",map.get("f_cucode"));//���ޱ��
				json.put("f_downloadstatus",map.get("f_downloadstatus"));//�ۼƹ�����
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//��������״̬
			try{
				hql = "update t_repairsys set f_havacomplete='�����' where f_cucode=? and (f_accepter=? or f_accepter='����ƽ̨')";
				hibernateTemplate.bulkUpdate(hql, new Object[]{map.get("f_cucode"),map.get("f_accepter")});
			}catch (Exception e) {
				e.printStackTrace();
			}
			array.put(json);
		}		
		return array;
	}
	
	@GET
	@Path("upload/{cucode}/{smwxjl}/{finishtime}/{gaswatchbrand}/{aroundmeter}/{lastrecord}/{surplus}/{havacomplete}")
	public String tx_Upload(@PathParam("cucode")String cucode,
			@PathParam("smwxjl")String smwxjl,
			@PathParam("finishtime")String finishtime,
			@PathParam("gaswatchbrand")String gaswatchbrand,
			@PathParam("aroundmeter")String aroundmeter,
			@PathParam("lastrecord")String lastrecord,
			@PathParam("surplus")String surplus,
			@PathParam("havacomplete")String havacomplete) {
		String result="";
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
		try {
			String hql = "update t_repairsys set "+
				//����ά�޼�¼        ά���������		ά�������ʱ��    		����Ʒ��			���ұ�			�����		������		״̬
				" f_smwxjl=?,f_completedate2=?,f_completetime=?,f_gaswatchbrand=?,f_aroundmeter=?,f_lastrecord=?,surplus=?,f_havacomplete=? "+
				" where f_downloadstatus in ('��','�����ѳ���','�����Ѳ���','�����Ѵ���') and f_cucode=?";
			int i=hibernateTemplate.bulkUpdate(hql, new Object[]{smwxjl.replaceAll("'", ""),sdf.parse(finishtime),sdf.parse(finishtime),gaswatchbrand,aroundmeter,Double.parseDouble(lastrecord),Double.parseDouble(surplus),havacomplete,cucode});
			if(i==1){
				result="ok";
			} else if(i==0){
				result="not";
			} else{
				result="error";
			}
			try{
				if("�����".equals(havacomplete)){
					hql="update repairsman set f_repairsmanstate='����' where man=(select f_accepter from t_repairsys where f_cucode=?)";
					hibernateTemplate.bulkUpdate(hql, new Object[]{cucode});
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			//���ع�������״̬
			try {
				hql = "update t_repairsys set f_downloadstatus='�����Ѵ���' where f_downloadstatus in ('��','�����Ѳ���','�����Ѵ���') and f_cucode=?";
				hibernateTemplate.bulkUpdate(hql, new Object[]{cucode});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@POST
	@Path("batchUpload")
	public String BatchUpload(String jsonarray) throws Exception {
		String r="";
		try {
			JSONArray rows = new JSONArray(jsonarray);
			// ��ÿһ�����ݣ����õ����������ݴ������
			JSONObject row =null;
			for (int i = 0; i < rows.length(); i++) {
				row = rows.getJSONObject(i);
				String str=tx_Upload(row.getString("cucode"),row.getString("smwxjl"),row.getString("finishtime"),row.getString("f_gaswatchbrand"),row.getString("f_aroundmeter"),row.getString("f_lastrecord"),row.getString("surplus"),row.getString("completion"));
				if(r!=null&&r!=""){
					r+=",";
				}
				r+=row.getString("cucode")+","+str;
			}
		} catch (Exception e) {
			r="error";
		}
		return r;
	}

	@GET
	@Path("resultState/{cucode}")
	@Produces(MediaType.APPLICATION_JSON)
	public String resultState(@PathParam("cucode") String cucode) {
		try {
			String hql = "update t_repairsys set f_downloadstatus=?,f_accepter=? where f_cucode=?";
			hibernateTemplate.bulkUpdate(hql, new Object[]{"ά��Ա�˵�",null,cucode});
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	@GET
	@Path("checkState/{cucode}/{f_repairer2}")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkState(@PathParam("cucode") String cucode,@PathParam("f_repairer2") String f_repairer2) {
		try {
			String hql = "update t_repairsys set f_downloadstatus='�����Ѳ���' where f_downloadstatus in ('��','�����Ѳ���') and f_cucode=? and f_accepter=?";
			hibernateTemplate.bulkUpdate(hql, new Object[]{cucode,f_repairer2});
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	@GET
	@Path("qiangdan/{cucode}/{f_repairer2}")
	@Produces(MediaType.APPLICATION_JSON)
	public String qiangdan(@PathParam("cucode") String cucode,@PathParam("f_repairer2") String f_repairer2) {
		try {
			final String sql = "select f_cucode from t_repairsys where f_downloadstatus !='�����ѳ���' and f_accepter='����ƽ̨' and f_cucode='"+cucode+"'";
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
				String hql = "update t_repairsys set f_accepter=?,f_qdstatus=? where f_cucode=?";
				hibernateTemplate.bulkUpdate(hql, new Object[]{f_repairer2,"�����ѱ���",cucode});
				return "{\"f_info\":\"�����ɹ�\",\"f_qdstatus\":\"ok\"}";
			}
			return "{\"f_info\":\"��������̫��´�Ŭ����\",\"f_qdstatus\":\"err\"}}";
		} catch (Exception e) {
			return "{\"f_info\":\"��������̫��´�Ŭ����\",\"f_qdstatus\":\"err\"}}";
		}		
	}
	
	
}

