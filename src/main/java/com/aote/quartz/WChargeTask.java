package com.aote.quartz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
/**
 * ΢�Ų���
 * @author Administrator
 *
 */
public class WChargeTask {
	
	static Logger log = Logger.getLogger(WChargeTask.class);
	@Autowired
	private HibernateTemplate hibernateTemplate;
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
				
				Map userMap = this.findUser(userId);
				double nowmoney= (Double) userMap.get("f_zhye");
				BigDecimal nm = new BigDecimal(nowmoney);
				//���½���
				BigDecimal zhye=payMent.add(nm);
				//�շ�ʱ��
				String paytime=user.get("f_time_end").toString();
				SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
				Date deliverydate =formatDate.parse(paytime.substring(0,8));
				formatDate = new SimpleDateFormat("HHmmss");
				Date deliverytime = formatDate.parse(paytime.substring(8,14));
				Map sale = new HashMap<String, Object>();
//				sale.put("f_preamount", debts.subtract(zhinajin).doubleValue());
//				sale.put("f_pregas", debtGas.doubleValue());
//				sale.put("f_zhinajin", zhinajin.doubleValue());
//				sale.put("f_useful", handIds);
//				sale.put("lastinputdate", lastinputdate);
//				sale.put("f_benqizhye", nowye.doubleValue());
//				sale.put("f_totalcost", f_totalfee.doubleValue());
//				sale.put("f_finallygas", debtGas.doubleValue());
//				sale.put("f_finallybought", debtGas.doubleValue());
				sale.put("f_yhxz", userMap.get("f_yhxz"));
				sale.put("f_zhye", userMap.get("f_zhye"));
				sale.put("f_gasmeterstyle", userMap.get("f_gasmeterstyle"));
				sale.put("f_apartment", userMap.get("f_apartment"));
				sale.put("f_userid", userMap.get("f_userid"));
				sale.put("f_username", userMap.get("f_username"));
				sale.put("f_address", userMap.get("f_address"));
				sale.put("f_districtname", userMap.get("f_districtname"));
				sale.put("f_gaswatchbrand", userMap.get("f_gaswatchbrand"));
				sale.put("f_metertype", userMap.get("f_metertype"));
				sale.put("f_gaspricetype", userMap.get("f_gaspricetype"));
				sale.put("f_gasprice", userMap.get("f_gasprice"));
				sale.put("f_usertype", userMap.get("f_usertype"));
				sale.put("f_gasproperties", userMap.get("f_gasproperties"));
				sale.put("f_beginfee", userMap.get("f_beginfee"));
				sale.put("f_payment", "�ֽ�");
				sale.put("f_grossproceeds", payMent.doubleValue());
				sale.put("f_givechange", 0.0);
				sale.put("f_meternumber", userMap.get("f_meternumber"));
				sale.put("f_finabuygasdate", deliverydate);
				sale.put("f_deliverydate", deliverydate);
				sale.put("f_deliverytime", deliverytime);
				
					sale.put("f_filiale", "΢��");
					sale.put("f_sgnetwork", "΢��");
					sale.put("f_sgoperator", "΢��");
					sale.put("f_payfeetype", "΢��֧��");
					sale.put("f_comtype", "΢��");

				sale.put("f_jiezhangstate", "δ����");

				sale.put("f_payfeevalid", "��Ч");
				sale.put("f_amountmaintenance", 0.0);
				sale.put("f_banksn", sn);
				sale.put("f_beizhu", "΢�Ų���");
				String OrgStr = userMap.get("f_OrgStr") + "";
				sale.put("f_OrgStr", OrgStr.substring(0, OrgStr.lastIndexOf(".") + 1)
						+ "΢��");
				this.hibernateTemplate.save("t_sellinggas", sale);
				// �����û�����
				this.updateUser(userId, zhye.doubleValue());
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
	 * �����û�����
	 */
	private void updateUser(String userId, double zhye) throws Exception {
		// �����û�
		String sql = "update t_userfiles  set f_zhye=" + zhye
				+" where f_userid='"+userId+ "'";
		this.hibernateTemplate.bulkUpdate(sql);
		log.debug(" �����û�����:" + sql);
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
	/**
	 * �����û���Ϣ
	 */
	private Map<String, Object> findUser(String userid) {
		final String userSql = "from t_userfiles  where f_userid='" + userid
				+ "'  ";
		List userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return null;
		}
		Map<String, Object> userMap = (Map<String, Object>) userlist.get(0);
		log.debug(" �����û���Ϣ:" + userSql);
		return userMap;
	}
	/**
	 * �����շ���Ϣ
	 */
	private int findCharge(String sn) {
		int a=0;
		final String userSql = "from t_sellinggas  where f_banksn='"+sn
				+ "'";
		List userlist = this.hibernateTemplate.find(userSql);
		if (userlist.size() != 1) {
			return a;
		}else{
			a=1;
		}
		log.debug(" �����շ���Ϣ:" + userSql);
		return a;
	}
	
}
