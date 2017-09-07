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
 * 充值完成后，处理卡表用户的充值成功后处理类，修改t_userfiles中账户余额，在t_sellinggas表中增加一条收费记录
 * @author Administrator
 *
 */

@Component
public class ICChongZhi {
	
	static Logger log = Logger.getLogger(WeiXinService.class);
	// 1.拿到微信的交易记录，先去t_sellinggas表中查询是否有对应的交易记录
	// 有，直接返回，表明对这笔交易已经处理过了
	// 否，继续
	// 2.开启事务
	// 3.在用户表中的账户余额中增加充值金额
	// 4.在售气表中增加一条收费记录
	// 5.提交或回滚
    // 6.在finally中关闭事务
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	/**
	 * 充值成功后操作
	 * @param userid 用户标号
	 * @param money 充值金额
	 * @param transation_id 微信支付订单号
	 * @param attach 商家数据包，原样返回（微信支付后返回）
	 * @return 0失败，1成功
	 */
	public void saveandmodify(String userid, String money, String transation_id,String attach, String time_end) {
		// 查询用户
		String sql = "from t_userfiles where f_userid = '" + userid + "'";
		System.out.println("查询账户余额：" + sql);
		log.debug("查询账户余额：" + sql);
		List list = this.hibernateTemplate.find(sql);
		if(list.size() == 0) {
			System.out.println("没有查询到相关用户");
			log.debug("没有查询到相关用户");
			return ;
		}else if(list.size() > 1) {
			System.out.println("用户编号不唯一");
			log.debug("用户编号不唯一");
			return ;
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		// 取出账户余额
		double zhye = (Double) map.get("f_zhye");
		System.out.println("当前账户余额为：" + zhye);
		log.debug("当前账户余额为：" + zhye);
		if(isexist(transation_id)){
			log.debug("交易已经存在");
			return ;
		}
		BigDecimal chongzhi = new BigDecimal(money).divide(BigDecimal.valueOf(100));
		BigDecimal jqzhye = BigDecimal.valueOf(zhye);
		double newzhye = Double.valueOf(jqzhye.add(chongzhi).toString());
		System.out.println("充值后的账户余额为：" + newzhye);
		log.debug("充值后的账户余额为：" + newzhye);
		sam(userid, chongzhi.doubleValue(), transation_id, attach, newzhye, map, time_end);
	}
	

	/**
	 * 查询记录中是否已经存在微信支付订单号
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
				// 更新用户档案中的账户余额
				String updatesql1 = "update t_userfiles set f_zhye = " + newzhye + "where f_userid= '" + userid + "'";
				Query query1=session.createQuery(updatesql1);
				query1.executeUpdate();
				
				// 增加一条收费记录
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
				savemap.put("f_payment", "现金");
				savemap.put("f_meternumber", map.get("f_meternumber"));
				savemap.put("f_filiale", "微信");
				savemap.put("f_comtype", "微信");
				savemap.put("f_gasmeterstyle", "卡表");
				savemap.put("f_zhye", newzhye);
				savemap.put("f_banksn", transation_id); // 微信支付订单编号
				savemap.put("f_yhxz", map.get("f_yhxz"));
				savemap.put("f_grossproceeds", money); // 收款金额
				savemap.put("f_sgnetwork", "微信"); //网点
				savemap.put("f_sgoperator", "微信"); //操作员
				savemap.put("f_payfeetype", "微信支付");
				savemap.put("f_jiezhangstate", "未结账"); // 结账状态
				savemap.put("f_deliverytime", toDate(time_end)); //缴费时间
				savemap.put("f_deliverydate", toDate(time_end)); // 缴费日期
				session.save("t_sellinggas", savemap);
				log.debug("事务提交前保存的卡表收费记录"+savemap.toString());
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
			System.out.println("日期解析失败");
			e.printStackTrace();
			return new Date(1);
		}
		return date;
	}
	
}
