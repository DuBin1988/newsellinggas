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
		String hql="from t_repairsys where f_havadeal='是' and f_havacomplete in ('未完成','二次派单') and (f_accepter=? or f_accepter='抢单平台')";
		if(first.equals("No")){
			hql+=" and f_downloadstatus is null"; 
		}
		List<Object> list = this.hibernateTemplate.find(hql,f_repairer2);
		for (Object obj : list) {
			// 把单个map转换成JSON对象
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = new JSONObject();
			try {
				json.put("f_userid",map.get("f_userid"));//用户ID
				json.put("f_username",map.get("f_username"));//用户姓名
				json.put("f_address",map.get("f_address"));//用户地址
				json.put("f_usertype",map.get("f_usertype"));//用户类别
				json.put("f_linktype",map.get("f_linktype"));//联系方式
				json.put("f_unitname",map.get("f_unitname"));//单位
				
				json.put("f_sender",map.get("f_sender"));//派单人
				json.put("f_senddate",map.get("f_senddate"));//派单日期
				json.put("f_sendtime",map.get("f_sendtime"));//派单时间
				json.put("f_cucode",map.get("f_cucode"));//报修编号
				json.put("f_repairtype",map.get("f_repairtype"));//报修类型
				json.put("f_phone",map.get("f_phone"));//来电电话
				json.put("f_repairreason","来电信息:\n"+map.get("f_repairreason")+"\n\n设备信息:\n产品类型: "+map.get("f_chanpinlx")+",产品品牌: "+map.get("f_chanpinpp")+"\n产品型号: "+map.get("f_chanpinxinghao")+",使用周期: "+map.get("shiyongzhouqi")+"\n产品故障: "+map.get("f_chanpinguzhang")+",维修部件: "+map.get("f_weixiubujian"));  //来电内容
				json.put("f_stopremark",map.get("f_stopremark"));//备注
				
				json.put("f_meternumber",map.get("f_meternumber"));//表号
				json.put("f_metertype",map.get("f_metertype"));//表型号
				json.put("f_aroundmeter",map.get("f_aroundmeter"));//左右表
				json.put("f_lastrecord",map.get("f_lastrecord"));//表读数
				json.put("f_gasmeteraccomodations",map.get("f_gasmeteraccomodations"));//表底数
				json.put("f_metergasnums",map.get("f_metergasnums"));//累计购气量
//				json.put("f_fireopening",map.get("f_fireopening"));//通气日期
//				json.put("f_restrictsparenum",map.get("f_restrictsparenum"));//限购气量余气量
//				json.put("f_forebuygas",map.get("f_forebuygas"));//上次购气量
				if("抢单平台".equals(map.get("f_accepter")))
					json.put("f_downloadstatus","抢工单");//累计购气量
				else
					json.put("f_downloadstatus","正常");//累计购气量
				json.put("f_workingdays",map.get("f_workingdays"));//累计购气量
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(json);
			//第一次下载记录时所作修改
			try {
				if(map.get("f_downloadstatus")==null||map.get("f_downloadstatus")==""){
					//是否有下载过				
					hql = "update t_repairsys set f_downloadstatus ='是' where f_cucode=? and f_accepter=?";
					hibernateTemplate.bulkUpdate(hql, new Object[]{map.get("f_cucode"),map.get("f_accepter")});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
						
		}
		//更新维修员在线信息
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			hql = "update repairsman set online ='在线',onlinetime='"+formatter.format(new Date())+"' where man=?";
			hibernateTemplate.bulkUpdate(hql, new Object[]{f_repairer2});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}
	
	//获取撤销的工单
	@GET
	@Path("revgongdan/{f_repairer2}/{first}")//f_repairer2=f_accepter
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray tx_revgongdan(@PathParam("f_repairer2") String f_repairer2,
			@PathParam("first") String first) {
		JSONArray array = new JSONArray();
		String hql="from t_repairsys where f_havadeal='是' and (f_accepter=? or f_accepter='抢单平台') and f_havacomplete in ('未完成','二次派单') and f_downloadstatus='工单已撤销'";
		List<Object> list = this.hibernateTemplate.find(hql,f_repairer2);
		for (Object obj : list) {
			// 把单个map转换成JSON对象
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = new JSONObject();
			try {
				json.put("f_userid",map.get("f_userid"));//用户ID
				json.put("f_cucode",map.get("f_cucode"));//报修编号
				json.put("f_downloadstatus",map.get("f_downloadstatus"));//累计购气量
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//更新任务状态
			try{
				hql = "update t_repairsys set f_havacomplete='已完成' where f_cucode=? and (f_accepter=? or f_accepter='抢单平台')";
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
				//上门维修记录        维修完成日期		维修完成日时间    		气表品牌			左右表			表读数		补气量		状态
				" f_smwxjl=?,f_completedate2=?,f_completetime=?,f_gaswatchbrand=?,f_aroundmeter=?,f_lastrecord=?,surplus=?,f_havacomplete=? "+
				" where f_downloadstatus in ('是','工单已撤销','工单已查阅','工单已处理') and f_cucode=?";
			int i=hibernateTemplate.bulkUpdate(hql, new Object[]{smwxjl.replaceAll("'", ""),sdf.parse(finishtime),sdf.parse(finishtime),gaswatchbrand,aroundmeter,Double.parseDouble(lastrecord),Double.parseDouble(surplus),havacomplete,cucode});
			if(i==1){
				result="ok";
			} else if(i==0){
				result="not";
			} else{
				result="error";
			}
			try{
				if("已完成".equals(havacomplete)){
					hql="update repairsman set f_repairsmanstate='空闲' where man=(select f_accepter from t_repairsys where f_cucode=?)";
					hibernateTemplate.bulkUpdate(hql, new Object[]{cucode});
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			//返回工单处理状态
			try {
				hql = "update t_repairsys set f_downloadstatus='工单已处理' where f_downloadstatus in ('是','工单已查阅','工单已处理') and f_cucode=?";
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
			// 对每一个数据，调用单个抄表数据处理过程
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
			hibernateTemplate.bulkUpdate(hql, new Object[]{"维修员退单",null,cucode});
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
			String hql = "update t_repairsys set f_downloadstatus='工单已查阅' where f_downloadstatus in ('是','工单已查阅') and f_cucode=? and f_accepter=?";
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
			final String sql = "select f_cucode from t_repairsys where f_downloadstatus !='工单已撤销' and f_accepter='抢单平台' and f_cucode='"+cucode+"'";
			List list = (List) hibernateTemplate
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							SQLQuery query = session.createSQLQuery(sql);
							return query.list();
						}
					});
			// 找到安检记录，判断日期和基表读数
			if (list.size() == 1) {
				String hql = "update t_repairsys set f_accepter=?,f_qdstatus=? where f_cucode=?";
				hibernateTemplate.bulkUpdate(hql, new Object[]{f_repairer2,"工单已被抢",cucode});
				return "{\"f_info\":\"抢单成功\",\"f_qdstatus\":\"ok\"}";
			}
			return "{\"f_info\":\"本次手气太差，下次努力呦\",\"f_qdstatus\":\"err\"}}";
		} catch (Exception e) {
			return "{\"f_info\":\"本次手气太差，下次努力呦\",\"f_qdstatus\":\"err\"}}";
		}		
	}
	
	
}

