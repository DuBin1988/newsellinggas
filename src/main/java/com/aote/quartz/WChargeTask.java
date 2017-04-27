package com.aote.quartz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.rs.Deposit;
import com.aote.rs.OverdueService;
/**
 * ΢�Ų���
 * @author Administrator
 *
 */
public class WChargeTask {
	
	static Logger log = Logger.getLogger(WChargeTask.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public OverdueService overdueService=new OverdueService();
	public Deposit deposit=new Deposit();
	public boolean finished = true;
	
	public void update() throws Exception {
		finished = false;
		
		//δ�����û�
		List list= findWeixin();
		if(list.size()!=0){
			for(int i = 0;i < list.size(); i ++){
				Map<String, Object> user = (Map<String, Object>) list.get(i);
				String  sn=user.get("f_transaction_id").toString();
				String  userId=user.get("f_userid").toString();
				int  pay= (Integer) user.get("f_total_fee");
				BigDecimal payMent = new BigDecimal(pay);
//				payMent=payMent.divide(new BigDecimal(100), 2);
				payMent=payMent.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
				int a=this.findCharge(sn);
				if(a!=0){
					//����Ϊ�Ѷ���
					this.updateWeixinUser(sn);
					continue;
				}else{
				//�շ�ʱ��
				String paytime=user.get("f_time_end").toString();
				SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
				Date dt =formatDate.parse(paytime.substring(0,8));
				formatDate = new SimpleDateFormat("HHmmss");
				Date tm = formatDate.parse(paytime.substring(8,14));
				//���ɽ�
				double zhinajin=getzhinajin(userId);
				String bankCode="";
				String jiGouNo="";
				String guiYuanNo="weixin";
				deposit.setHibernateTemplate(hibernateTemplate);
				deposit.deposit(userId, new SimpleDateFormat("yyyyMMdd").format(dt), new SimpleDateFormat("HHmmss").format(tm), 
						          payMent.toString(), zhinajin+"", sn, bankCode, jiGouNo, guiYuanNo);
		
				//����Ϊ�Ѷ���
				this.updateWeixinUser(sn);
				}
			}
			
		}else{
			return;
		}
	
		finished = true;
	}
	
	/**
	 * �����û���Ϣ
	 */
	private List findWeixin() {
		final String userSql = "from t_weixinreturnxml  where f_message='δ����'";
		List userlist = this.hibernateTemplate.find(userSql);
		log.debug(" �����û���Ϣ:" + userSql);
		return userlist;
	}
	
	/**
	 * ����΢��Ϊ�Ѷ���
	 */
	private void updateWeixinUser(String sn)  {
		// �����û�
		String sql = "update t_weixinreturnxml set f_message='�Ѷ���' where f_transaction_id='"+sn+"'";
		this.hibernateTemplate.bulkUpdate(sql);
		log.debug(" �����Ѷ���:" + sql);
	}
//	/**
//	 * �����û���Ϣ
//	 */
//	private Map<String, Object> findUser(String userid) {
//		final String userSql = "from t_userfiles  where f_userid='" + userid
//				+ "'  ";
//		List userlist = this.hibernateTemplate.find(userSql);
//		if (userlist.size() != 1) {
//			return null;
//		}
//		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
//		log.debug(" �����û���Ϣ:" + userSql);
//		return userMap;
//	}
	/**
	 * �����շ���Ϣ
	 */
	private int findCharge(String sn) {
		int a=0;
		final String userSql = "from t_sellinggas  where f_banksn='"+sn
				+ "'";
		List userlist = this.hibernateTemplate.find(userSql);
		if (userlist == null ||userlist.size() != 1) {
			return a;
		}else{
			a=1;
		}
		log.debug(" �����շ���Ϣ:" + userSql);
		return a;
	}
	/**
	 * ��Ƿ��
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private String findHands(String userId) throws Exception {
		final String sql = "from t_handplan where shifoujiaofei='��' and f_state='�ѳ���' and lastrecord is not null and f_userid='"
				+ userId + "' order by id";
		log.debug(sql);
		return sql;
	}
	/**
	 * �������ɽ�
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public double getzhinajin(String userId) throws Exception {
		double zhinajin=0;
		String hql = findHands(userId);
		overdueService.setHibernateTemplate(hibernateTemplate);
		JSONArray arr =overdueService.invoke(hql);
		log.debug(arr);
		if (arr.length() == 0) {
			zhinajin = 0;
 		} else {
 			for (int i = 0; i < arr.length(); i++) {
			JSONObject object = arr.getJSONObject(i);
			zhinajin = object.getDouble("f_zhinajin")+zhinajin;
			log.debug(zhinajin);
			log.debug("�������ɽ�:" + zhinajin);
 			}
		}
		log.debug("��ѯʱ�������ɽ������" + userId);
		return zhinajin;
	}

}
