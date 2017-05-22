package com.aote.rs.weixin;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * ��ֵ��ɺ󣬴������û��ĳ�ֵ�ɹ������࣬�޸�t_userfiles���˻�����t_sellinggas��������һ���շѼ�¼
 * @author Administrator
 *
 */

@Component
public class ICChongZhi {
	
	static Logger log = Logger.getLogger(WeiXinService.class);
	// 1.�õ�΢�ŵĽ��׼�¼����ȥt_sellinggas���в�ѯ�Ƿ��ж�Ӧ�Ľ��׼�¼
	// �У�ֱ�ӷ��أ���������ʽ����Ѿ��������
	// �񣬼���
	// 2.��������
	// 3.���û����е��˻���������ӳ�ֵ���
	// 4.��������������һ���շѼ�¼
	// 5.�ύ��ع�
    // 6.��finally�йر�����
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	/**
	 * ��ֵ�ɹ������
	 * @param userid �û����
	 * @param money ��ֵ���
	 * @param transation_id ΢��֧��������
	 * @param attach �̼����ݰ���ԭ�����أ�΢��֧���󷵻أ�
	 * @return 0ʧ�ܣ�1�ɹ�
	 */
	public void saveandmodify(String userid, String money, String transation_id,String attach, String time_end) {
		// ��ѯ�û�
		String sql = "from t_userfiles where f_userid = '" + userid + "'";
		System.out.println("��ѯ�˻���" + sql);
		log.debug("��ѯ�˻���" + sql);
		List list = this.hibernateTemplate.find(sql);
		if(list.size() == 0) {
			System.out.println("û�в�ѯ������û�");
			log.debug("û�в�ѯ������û�");
			return ;
		}else if(list.size() > 1) {
			System.out.println("�û���Ų�Ψһ");
			log.debug("�û���Ų�Ψһ");
			return ;
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		// ȡ���˻����
		double zhye = (Double) map.get("f_zhye");
		System.out.println("��ǰ�˻����Ϊ��" + zhye);
		log.debug("��ǰ�˻����Ϊ��" + zhye);
		if(isexist(transation_id)){
			log.debug("�����Ѿ�����");
			return ;
		}
		BigDecimal chongzhi = new BigDecimal(money).divide(BigDecimal.valueOf(100));
		BigDecimal jqzhye = BigDecimal.valueOf(zhye);
		double newzhye = Double.valueOf(jqzhye.add(chongzhi).toString());
		System.out.println("��ֵ����˻����Ϊ��" + newzhye);
		log.debug("��ֵ����˻����Ϊ��" + newzhye);
		sam(userid, chongzhi.doubleValue(), transation_id, attach, newzhye, map, time_end);
	}
	

	/**
	 * ��ѯ��¼���Ƿ��Ѿ�����΢��֧��������
	 * @param transation_id
	 * @return
	 */
	public boolean isexist(String transation_id) {
		String sql = "from t_sellinggas where f_banksn= '" + transation_id + "'";
		List list = this.hibernateTemplate.find(sql);
		if (list.size() != 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param userid
	 * @param money
	 * @param transation_id
	 * @param attach
	 * @param newzhye
	 */
	public void sam(final String userid, final double money, final String transation_id,
			String attach,final double newzhye, final Map map, final String time_end) {
		this.hibernateTemplate.execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				 Transaction tx = session.beginTransaction();
				 tx.begin();
				// �����û������е��˻����
				String updatesql1 = "update t_userfiles set f_zhye = " + newzhye + "where f_userid= '" + userid + "'";
				Query query1=session.createQuery(updatesql1);
				query1.executeUpdate();
				
				// ����һ���շѼ�¼
				Map<String, Object> savemap = new HashMap<String, Object>();
				savemap.put("f_apartment", map.get("f_apartment"));
				savemap.put("f_userid", map.get("f_userid"));
				savemap.put("f_username", map.get("f_username"));
				savemap.put("f_address", map.get("f_address"));
				savemap.put("f_districtname", map.get("f_districtname"));
				savemap.put("f_gaswatchbrand", map.get("f_gaswatchbrand"));
				savemap.put("f_metertype", map.get("f_metertype"));
				savemap.put("f_gaspricetype", map.get("f_gaspricetype"));
				savemap.put("f_gasprice", map.get("f_gasprice"));
				savemap.put("f_usertype", map.get("f_usertype"));
				savemap.put("f_gasproperties", map.get("f_gasproperties"));
				savemap.put("f_payment", "�ֽ�");
				savemap.put("f_meternumber", map.get("f_meternumber"));
				savemap.put("f_filiale", "΢��");
				savemap.put("f_comtype", "΢��");
				savemap.put("f_gasmeterstyle", "����");
				savemap.put("f_zhye", newzhye);
				savemap.put("f_banksn", transation_id); // ΢��֧���������
				savemap.put("f_yhxz", map.get("f_yhxz"));
				savemap.put("f_grossproceeds", money); // �տ���
				savemap.put("f_sgnetwork", "΢��"); //����
				savemap.put("f_sgoperator", "΢��"); //����Ա
				savemap.put("f_payfeetype", "΢��֧��");
				savemap.put("f_jiezhangstate", "δ����"); // ����״̬
				savemap.put("f_deliverytime", toDate(time_end)); //�ɷ�ʱ��
				savemap.put("f_deliverydate", toDate(time_end)); // �ɷ�����
				session.save("t_sellinggas", savemap);
				log.debug("�����ύǰ����Ŀ����շѼ�¼"+savemap.toString());
				tx.commit();
				return null;
			}
		});
	}
	
	public Date toDate(String str) {
//		String datestr = str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + 
//				str.substring(6, 8) + " " + str.substring(8, 10) + ":" + 
//				str.substring(10, 12) + ":" + str.substring(12, 14);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			System.out.println("���ڽ���ʧ��");
			e.printStackTrace();
			return new Date(1);
		}
		return date;
	}
	
}
