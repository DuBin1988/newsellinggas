package com.aote.rs.charge;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aote.listener.ContextListener;
import com.aote.rs.charge.countdate.ICountDate;
import com.aote.rs.util.RSException;

@Path("handcharge")
@Scope("prototype")
@Component
public class HandCharge {

	static Logger log = Logger.getLogger(HandCharge.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	//private int stairmonths;

	private String stardate;
	private String enddate;

	// ���ۼƹ�����
	BigDecimal sumamont = new BigDecimal(0);

	// ����������۵��м���
	BigDecimal stair1num = new BigDecimal(0);
	BigDecimal stair2num = new BigDecimal(0);
	BigDecimal stair3num = new BigDecimal(0);
	BigDecimal stair4num = new BigDecimal(0);
	BigDecimal stair1fee = new BigDecimal(0);
	BigDecimal stair2fee = new BigDecimal(0);
	BigDecimal stair3fee = new BigDecimal(0);
	BigDecimal stair4fee = new BigDecimal(0);
	private int stairmonths;
	
	// �������أ�����JSON��
	// operator ����Ա������
	@GET
	@Path("{operator}")
	@Produces("application/json")
	public JSONArray ReadRecordInput(@PathParam("operator") String operator) {
		JSONArray array = new JSONArray();
		List<Object> list = this.hibernateTemplate.find(
				"from t_handplan where f_inputtor=? and f_state='δ����'",
				operator);
		for (Object obj : list) {
			// �ѵ���mapת����JSON����
			Map<String, Object> map = (Map<String, Object>) obj;
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		return array;
	}

	// ��ѯ��������
	@POST
	@Path("download")
	public String downLoadRecord(String condition) {

		String sql = "select top 1000 u.f_userid,u.f_username,u.f_address,u.lastinputgasnum "
				+ "from t_handplan h left join t_userfiles u on h.f_userid = u.f_userid where h.shifoujiaofei='��' and u.f_userstate!='ע��' and h.f_state='δ����' and "
				+ condition + "	order by u.f_address,u.f_apartment";
		List<Object> list = this.hibernateTemplate
				.executeFind(new HibernateSQLCall(sql));
		// String result="[";
		boolean check = false;
		JSONArray array = new JSONArray();
		for (Object obj : list) {
			JSONObject json = (JSONObject) new JsonTransfer()
					.MapToJson((Map<String, Object>) obj);
			// Map<String, Object> map = (Map<String, Object>) json;
			// if(!result.equals("[")){
			// result+=",";
			// }
			// String item="";
			// //�ƻ��·��û�����û�������ַ�ϴε������ε���������
			// item+="{";
			// item+="f_userid:'"+map.get("f_userid")+"',";
			// item+="f_username:'"+map.get("f_username")+"',";
			// item+="f_address:'"+map.get("f_address")+"',";
			// item+="lastinputgasnum:"+map.get("lastinputgasnum");
			// item+="}";
			//
			// result += item;
			array.put(json);
		}
		// result+="]";
		// System.out.println(result);

		return array.toString();
	}

	// �������¼��
	// ��������������
	@SuppressWarnings("unchecked")
	@GET
	@Path("record/one/{userid}/{reading}/{sgnetwork}/{sgoperator}/{lastinputdate}/{handdate}/{meterstate}")
	@Produces("application/json")
	public String RecordInputForOne(@PathParam("userid") String userid,
			@PathParam("reading") double reading,
			@PathParam("sgnetwork") String sgnetwork,
			@PathParam("sgoperator") String sgoperator,
			@PathParam("lastinputdate") String lastinputdate,
			@PathParam("handdate") String handdate,
			@PathParam("meterstate") String meterstate) {
		String ret = "";
		try {
			return afrecordInput(userid, 0, reading, sgnetwork, sgoperator,
					lastinputdate, handdate, 0, meterstate,1);
		} catch (Exception e) {
			log.debug(e.getMessage());
			ret = e.getMessage();
		} finally {
			return ret;
		}
	}

	// ����ǰ̨¼�빺��������������������
	@GET
	@Path("/num/{userid}/{pregas}/{enddate}")
	public JSONObject pregas(@PathParam("userid") String userid, // �û����
			@PathParam("pregas") double pregas, // ������
			@PathParam("enddate") String enddate // ��������, ��ʽΪyyyymmdd
	) {
		final String usersql = "select isnull(f_stairtype,'δ��')f_stairtype, isnull(f_gasprice,0)f_gasprice, "
				+ "isnull(f_stair1amount,0)f_stair1amount,isnull(f_stair2amount,0)f_stair2amount,"
				+ "isnull(f_stair3amount,0)f_stair3amount,isnull(f_stair1price,0)f_stair1price,"
				+ "isnull(f_stair2price,0)f_stair2price,isnull(f_stair3price,0)f_stair3price,"
				+ "isnull(f_stair4price,0)f_stair4price,isnull(f_stairmonths,0)f_stairmonths,isnull(f_zhye,0)f_zhye "
				+ "from t_userfiles where f_userid = '" + userid + "'";
		List<Map<String, Object>> list = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(usersql);
						q
								.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});
		// ȡ��������������
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		BigDecimal gasprice = new BigDecimal(map.get("f_gasprice").toString());
		BigDecimal stair1amount = new BigDecimal(map.get("f_stair1amount")
				.toString());
		BigDecimal stair2amount = new BigDecimal(map.get("f_stair2amount")
				.toString());
		BigDecimal stair3amount = new BigDecimal(map.get("f_stair3amount")
				.toString());
		BigDecimal stair1price = new BigDecimal(map.get("f_stair1price")
				.toString());
		BigDecimal stair2price = new BigDecimal(map.get("f_stair2price")
				.toString());
		BigDecimal stair3price = new BigDecimal(map.get("f_stair3price")
				.toString());
		BigDecimal stair4price = new BigDecimal(map.get("f_stair4price")
				.toString());
		int stairmonths = Integer.parseInt(map.get("f_stairmonths").toString());
		String stairtype = map.get("f_stairtype").toString();

		// ת����������
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(enddate.substring(0, 4));
		int month = Integer.parseInt(enddate.substring(4, 6));
		int day = Integer.parseInt(enddate.substring(6, 8));
		cal.set(year, month - 1, day);

		BigDecimal chargenum = stair(userid, new BigDecimal(pregas), cal,
				stairtype, gasprice, stairmonths, stair1amount, stair2amount,
				stair3amount, stair1price, stair2price, stair3price,
				stair4price);

		Map sell = new HashMap();
		sell.put("f_stair1amount", stair1num);
		sell.put("f_stair2amount", stair2num);
		sell.put("f_stair3amount", stair3num);
		sell.put("f_stair4amount", stair4num);
		sell.put("f_stair1fee", stair1fee);
		sell.put("f_stair2fee", stair2fee);
		sell.put("f_stair3fee", stair3fee);
		sell.put("f_stair4fee", stair4fee);
		sell.put("f_stair1price", stair1price);
		sell.put("f_stair2price", stair2price);
		sell.put("f_stair3price", stair3price);
		sell.put("f_stair4price", stair4price);
		sell.put("f_allamont", sumamont);
		sell.put("f_chargenum", chargenum);
		sell.put("f_stardate", stardate);
		sell.put("f_enddate", enddate);

		JSONObject json = (JSONObject) new JsonTransfer().MapToJson(sell);
		return json;
	}

	// �������¼����ڲ�������֧�ֿ������������¼����������
	public String afrecordInput(String userid, double lastreading, double reading,
			String sgnetwork, String sgoperator, String lastinputdate,
			String handdate, double leftgas, String meterstate,int flag)
			throws Exception {
		// �����û�δ�����¼
		Map map = this.findHandPlan(userid);
		if(map == null){
			return "";
		}
		// ��ȡ������
		String meterType = map.get("f_gasmeterstyle").toString();
		// �������ִ��hql����
		String hql = "";
		// Map<String, String> singles = getSingles();// ��ȡ���е�ֵ
		//BigDecimal chargenum = new BigDecimal(0);
		//BigDecimal sumamont = new BigDecimal(0);
		BigDecimal gasprice = new BigDecimal(map.get("f_gasprice").toString());
		String stairtype = map.get("f_stairtype").toString();
		BigDecimal stair1amount = new BigDecimal(map.get("f_stair1amount")
				.toString());
		BigDecimal stair2amount = new BigDecimal(map.get("f_stair2amount")
				.toString());
		BigDecimal stair3amount = new BigDecimal(map.get("f_stair3amount")
				.toString());
		BigDecimal stair1price = new BigDecimal(map.get("f_stair1price")
				.toString());
		BigDecimal stair2price = new BigDecimal(map.get("f_stair2price")
				.toString());
		BigDecimal stair3price = new BigDecimal(map.get("f_stair3price")
				.toString());
		BigDecimal stair4price = new BigDecimal(map.get("f_stair4price")
				.toString());
		int stairmonths = Integer.parseInt(map.get("f_stairmonths").toString());

		BigDecimal gas = new BigDecimal(0);//������
		BigDecimal lrg = new BigDecimal(0);//�ϴ�ָ��
		if(1 == flag)
		{
			// ����ǵ������õ�(�ֻ�)�����ڶ��������ڵı��γ���������ӵ�����ȡ
			BigDecimal lastReading = new BigDecimal(map.get("lastinputgasnum") + "");
			lrg = lastReading;
			// ����
			gas = new BigDecimal(reading).subtract(lastReading);
		}
		else
		{
			// ���� ����ӽ����ϻ�ȡ����ָ��
			gas = new BigDecimal(reading).subtract(new BigDecimal(lastreading));
			lrg = new BigDecimal(lastreading);
		}



		// ���ڶ��������ڵı��γ�����������ڵ�������
		//BigDecimal lastReading = new BigDecimal(map.get("lastinputgasnum") + "");
		// ����
		//BigDecimal gas = new BigDecimal(reading).subtract(lastReading);
		// �ӻ���ȡ�����(�������)
		BigDecimal f_zhye = new BigDecimal(map.get("f_zhye") + "");
		// �û���ַ
		String address = map.get("f_address").toString();
		// �û�����
		String username = map.get("f_username").toString();
		// ��ǰǷ������
		int items = Integer.parseInt(map.get("c") + "");
		// ����id
		String handid = map.get("id") + "";
		// �û��ɷ�����
		String payment = map.get("f_payment").toString();
		// ���β���
		String zerenbumen = "��";
		// ��վ
		String menzhan = "��";
		// ����Ա
		String inputtor = map.get("f_inputtor") + "";
		// �������ԱΪ�����׳��쳣�������ϲ㴦��
		if (inputtor.equals("")) {
			throw new RSException(map.get("f_userid") + "û�г���Ա������¼�롣");
		}
		// ���һ�γ�������
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = lastinputdate.substring(0, 10);
		Date lastinputDate = df.parse(dateStr);
		// ȡ���������ڵõ��ɷѽ�ֹ����DateFormat.parse(String s)
		Date date = endDate(lastinputdate);// �ɷѽ�ֹ����
		// ¼������
		Date inputdate = new Date();
		// �ƻ��·�
		DateFormat hd = new SimpleDateFormat("yyyy-MM");
		String dateStr1 = handdate.substring(0, 7);
		Date handDate = hd.parse(dateStr1);
		// ��ǰ���ۼƹ����� ���ݣ�
		BigDecimal f_metergasnums = new BigDecimal(map.get("f_metergasnums")
				+ "");
		// f_cumulativepurchase ���ۼƹ�����
		BigDecimal f_cumulativepurchase = new BigDecimal(
				map.get("f_cumulativepurchase") + "");
		// ��״̬
		String meterState = meterstate;
		// ������ý������۵��û�����
		// ������۴���
		BigDecimal chargenum = stair(userid, gas, Calendar.getInstance(),
				stairtype, gasprice, stairmonths, stair1amount, stair2amount,
				stair3amount, stair1price, stair2price, stair3price,
				stair4price);
		// ���Ѵ���0,���๻��ǰ����Ƿ�ѣ��Զ�����
		if (chargenum.compareTo(BigDecimal.ZERO) > 0
				&& chargenum.compareTo(f_zhye) <= 0 && items < 1) {
			// �Զ�����
			double grossproceeds = 0;
			Map<String, Object> sell = new HashMap<String, Object>();
			sell.put("f_userid", map.get("f_userid")); // �û�ID
			sell.put("f_payfeevalid", "��Ч");// �����Ƿ���Ч
			sell.put("f_payfeetype", "�Զ�����");// �շ�����
			//�޸�����ָ��
			sell.put("lastinputgasnum", lrg); // ���ڵ���
			sell.put("lastrecord", reading); // ���ڵ���
			sell.put("f_totalcost", chargenum.doubleValue()); // Ӧ�����
			sell.put("f_grossproceeds", grossproceeds); // �տ�
			sell.put("f_deliverydate", new Date()); // ��������
			sell.put("f_zhye", f_zhye.doubleValue()); // ���ڽ���
			sell.put("f_benqizhye", f_zhye.subtract(chargenum).doubleValue()); // ���ڽ���
			sell.put("f_gasmeterstyle", map.get("f_gasmeterstyle")); // ��������
			sell.put("f_comtype", "��Ȼ����˾"); // ��˾���ͣ���Ϊ��Ȼ����˾������
			sell.put("f_username", map.get("f_username")); // �û�/��λ����
			sell.put("f_address", map.get("f_address")); // ��ַ
			sell.put("f_districtname", map.get("f_districtname")); // С������
			sell.put("f_cusDom", map.get("f_cusDom")); // ¥��
			sell.put("f_cusDy", map.get("f_cusDy")); // ��Ԫ
			sell.put("f_idnumber", map.get("f_idnumber")); // ���֤��
			sell.put("f_gaswatchbrand", map.get("f_gaswatchbrand")); // ����Ʒ��
			sell.put("f_gaspricetype", map.get("f_gaspricetype")); // ��������
			sell.put("f_gasprice", gasprice.doubleValue()); // ����
			sell.put("f_usertype", map.get("f_usertype")); // �û�����
			sell.put("f_gasproperties", map.get("f_gasproperties")); // ��������
			sell.put("f_pregas", gas.doubleValue()); // ����
			sell.put("f_preamount", chargenum.doubleValue()); // ���
			sell.put("f_payment", "�ֽ�"); // ���ʽ
			sell.put("f_sgnetwork", sgnetwork); // ����
			sell.put("f_sgoperator", sgoperator); // �� �� Ա
			sell.put("f_cardid", map.get("f_cardid"));// ����
			sell.put("f_filiale", map.get("f_filiale")); // �ֹ�˾
			sell.put("f_useful", handid); // ����id
			sell.put("f_stair1amount", stair1num.doubleValue());
			sell.put("f_stair2amount", stair2num.doubleValue());
			sell.put("f_stair3amount", stair3num.doubleValue());
			sell.put("f_stair4amount", stair4num.doubleValue());
			sell.put("f_stair1fee", stair1fee.doubleValue());
			sell.put("f_stair2fee", stair2fee.doubleValue());
			sell.put("f_stair3fee", stair3fee.doubleValue());
			sell.put("f_stair4fee", stair4fee.doubleValue());
			sell.put("f_stair1price", stair1price.doubleValue());
			sell.put("f_stair2price", stair2price.doubleValue());
			sell.put("f_stair3price", stair3price.doubleValue());
			sell.put("f_stair4price", stair4price.doubleValue());
			sell.put("f_stardate", stardate);
			sell.put("f_enddate", enddate);
			sell.put("f_allamont", sumamont.doubleValue());
			int sellid = (Integer) hibernateTemplate.save("t_sellinggas", sell);
			hql = "update t_userfiles set f_zhye=?,lastinputgasnum=?,"
					+
					// ���γ�������
					"  lastinputdate=? "
					+
					// ��ǰ���ۼƹ����� ���ݣ� ���ۼƹ�����
					",f_metergasnums=  ?, f_cumulativepurchase= ? ,"
					// ������� ��������� �����ʱ��
					+ "f_finallybought= ?, f_finabuygasdate=?, f_finabuygastime=? "
					+ "where f_userid=?";
			hibernateTemplate.bulkUpdate(
					hql,
					new Object[] { f_zhye.subtract(chargenum).doubleValue(),
							reading, lastinputDate,
							f_metergasnums.add(gas).doubleValue(),
							f_cumulativepurchase.add(gas).doubleValue(),
							gas.doubleValue(), inputdate, inputdate, userid });
			String sellId = sellid + "";
			// ���³����¼
			hql = "update t_handplan set f_state='�ѳ���',shifoujiaofei='��',f_handdate=?,f_stairtype='"
					+ stairtype
					+ "',lastinputdate=?,f_zerenbumen='"
					+ zerenbumen
					+ "',f_menzhan='"
					+ menzhan
					+ "', f_inputtor='"
					+ inputtor
					+ "',lastrecord="
					+ reading
					+ " ,"
					+ "oughtamount="
					+ gas
					+ " ,oughtfee="
					+ chargenum
					+ " ,f_address='"
					+ address
					+ "', f_username='"
					+ username
					+ "', f_zhye="
					+ f_zhye
					+ ", f_bczhye="
					+ f_zhye.subtract(chargenum)
					+ ","
					+ "f_stair1amount="
					+ stair1num
					+ ",f_stair2amount="
					+ stair2num
					+ ",f_stair3amount="
					+ stair3num
					+ ",f_stair4amount="
					+ stair4num
					+ ",f_stair1fee="
					+ stair1fee
					+ ",f_stair2fee="
					+ stair2fee
					+ ",f_stair3fee="
					+ stair3fee
					+ ",f_stair4fee="
					+ stair4fee
					+ ",f_stair1price="
					+ stair1price
					+ ",f_stair2price="
					+ stair2price
					+ ",f_stair3price="
					+ stair3price
					+ ",f_stair4price="
					+ stair4price
					+ ","
					+ "f_stardate='"
					+ stardate
					+ "',f_enddate='"
					+ enddate
					+ "',f_allamont="
					+ sumamont
					+ " ,f_sellid="
					+ sellId
					+ ", f_leftgas="
					+ leftgas
					+ ", lastinputgasnum=" //����ָ��
					+ lrg
					+ " , f_inputdate=?,f_meterstate=?,f_network='"
					+ sgnetwork
					+ "',f_operator='"
					+ sgoperator
					+ "'  "
					+ "where f_userid='" + userid + "' and f_state='δ����' and id="+handid;
			hibernateTemplate.bulkUpdate(hql, new Object[] { handDate,
					lastinputDate, inputdate, meterState });
		} else {
			// �����û�����
			hql = "update t_userfiles " +
			// ���γ������ ���γ�������
					"set lastinputgasnum=? ,  lastinputdate=?  where f_userid=?";
			hibernateTemplate.bulkUpdate(hql, new Object[] { reading,
					lastinputDate, userid });
			// Ƿ��,���³����¼��״̬f_state���������ڡ����γ������
			// ������� =0 ,�Ƿ񽻷�Ϊ"��"
			String shifoujiaofei = "��";
			if (chargenum.compareTo(BigDecimal.ZERO) <= 0) {
				shifoujiaofei = "��";
			}
			hql = "update t_handplan set f_state ='�ѳ���', shifoujiaofei='"
					+ shifoujiaofei
					+ "',f_handdate=?,lastinputdate=?,f_zerenbumen='"
					+ zerenbumen + "', f_menzhan='" + menzhan
					+ "', f_inputtor='" + inputtor + "', lastrecord=" + reading
					+ " ,f_stairtype='" + stairtype + "'," + "oughtamount="
					+ gas + ",  f_endjfdate=?, oughtfee=" + chargenum
					+ ", f_inputdate=?,f_meterstate=?,f_network='" + sgnetwork
					+ "',f_operator='" + sgoperator + "' ,f_address='"
					+ address + "', f_username='" + username + "',"
					+ "f_stair1amount=" + stair1num + ",f_stair2amount="
					+ stair2num + ",f_stair3amount=" + stair3num
					+ ",f_stair4amount=" + stair4num + ",f_stair1fee="
					+ stair1fee + ",f_stair2fee=" + stair2fee + ",f_stair3fee="
					+ stair3fee + ",f_stair4fee=" + stair4fee + ","
					+ "f_stair1price=" + stair1price + ",f_stair2price="
					+ stair2price + ",f_stair3price=" + stair3price
					+ ",f_stair4price=" + stair4price + "," + "f_stardate='"
					+ stardate + "',f_enddate='" + enddate + "',f_allamont="
					+ sumamont + ", f_leftgas= " + leftgas + ", lastinputgasnum=" //����ָ��
					+ lrg
					+ " where f_userid='" + userid + "' and f_state='δ����' and id="+handid;
			hibernateTemplate.bulkUpdate(hql, new Object[] { handDate,
					lastinputDate, date, inputdate, meterState });
		}
		// �����û���Ƿ����,�����µ������˻����
		if (meterType != null && meterType.equals("����")
				&& gas.doubleValue() > 0) {
			financedetailDisp(map, gas, chargenum, sgnetwork, sgoperator);
		}
		return userid;
	}

	// ����������ۣ������ع�ԭ�򣬻᷵��sumamont������ȫ�ֱ�����
	// ��������ĸ��׶ν������������ݽ�������������
	// ���أ��ܼ۸�
	private BigDecimal stair(String userid, BigDecimal gas, Calendar cal,
			String stairtype, BigDecimal gasprice, int stairmonths,
			BigDecimal stair1amount, BigDecimal stair2amount,
			BigDecimal stair3amount, BigDecimal stair1price,
			BigDecimal stair2price, BigDecimal stair3price,
			BigDecimal stair4price) {
		BigDecimal chargenum = new BigDecimal(0);
		// ������ý������۵��û�����
		CountDate(userid, hibernateTemplate);
		if (!stairtype.equals("δ��")) {
			final String gassql = " select isnull(sum(oughtamount),0)oughtamount from t_handplan "
					+ "where f_userid='"
					+ userid
					+ "' and lastinputdate>='"
					+ stardate + "' and lastinputdate<='" + enddate + "'";
			List<Map<String, Object>> gaslist = (List<Map<String, Object>>) hibernateTemplate
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							Query q = session.createSQLQuery(gassql);
							q
									.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
							List result = q.list();
							return result;
						}
					});
			Map<String, Object> gasmap = (Map<String, Object>) gaslist.get(0);
			// ��ǰ������
			BigDecimal sumamont = new BigDecimal(gasmap.get("oughtamount")
					.toString());
			// �ۼƹ�����
			BigDecimal allamont = sumamont.add(gas);
			// ��ǰ�������ڵ�һ����
			if (sumamont.compareTo(stair1amount) < 0) {
				if (allamont.compareTo(stair1amount) < 0) {
					stair1num = gas;
					stair1fee = gas.multiply(stair1price);
					chargenum = gas.multiply(stair1price);
				} else if (allamont.compareTo(stair1amount) >= 0
						&& allamont.compareTo(stair2amount) < 0) {
					stair1num = stair1amount.subtract(sumamont);
					stair1fee = (stair1amount.subtract(sumamont))
							.multiply(stair1price);
					stair2num = allamont.subtract(stair1amount);
					stair2fee = (allamont.subtract(stair1amount))
							.multiply(stair2price);
					chargenum = stair1fee.add(stair2fee);
				} else if (allamont.compareTo(stair2amount) >= 0
						&& allamont.compareTo(stair3amount) < 0) {
					stair1num = stair1amount.subtract(sumamont);
					stair1fee = (stair1amount.subtract(sumamont))
							.multiply(stair1price);
					stair2num = stair2amount.subtract(stair1amount);
					stair2fee = (stair2amount.subtract(stair1amount))
							.multiply(stair2price);
					stair3num = allamont.subtract(stair2amount);
					stair3fee = (allamont.subtract(stair2amount))
							.multiply(stair3price);
					chargenum = stair1fee.add(stair2fee).add(stair3fee);
				} else if (allamont.compareTo(stair3amount) >= 0) {
					stair1num = stair1amount.subtract(sumamont);
					stair1fee = (stair1amount.subtract(sumamont))
							.multiply(stair1price);
					stair2num = stair2amount.subtract(stair1amount);
					stair2fee = (stair2amount.subtract(stair1amount))
							.multiply(stair2price);
					stair3num = stair3amount.subtract(stair2amount);
					stair3fee = (stair3amount.subtract(stair2amount))
							.multiply(stair3price);
					stair4num = allamont.subtract(stair3amount);
					stair4fee = (allamont.subtract(stair3amount))
							.multiply(stair4price);
					chargenum = stair1fee.add(stair2fee).add(stair3fee).add(
							stair4fee);
				}
				// ��ǰ�ѹ������ڽ��ݶ���
			} else if (sumamont.compareTo(stair1amount) >= 0
					&& sumamont.compareTo(stair2amount) < 0) {
				if (allamont.compareTo(stair2amount) < 0) {
					stair2num = gas;
					stair2fee = gas.multiply(stair2price);
					chargenum = stair2fee;
				} else if (allamont.compareTo(stair2amount) >= 0
						&& allamont.compareTo(stair3amount) < 0) {
					stair2num = stair2amount.subtract(sumamont);
					stair2fee = (stair2amount.subtract(sumamont))
							.multiply(stair2price);
					stair3num = allamont.subtract(stair2amount);
					stair3fee = (allamont.subtract(stair2amount))
							.multiply(stair3price);
					chargenum = stair2fee.add(stair3fee);
				} else {
					stair2num = stair2amount.subtract(sumamont);
					stair2fee = (stair2amount.subtract(sumamont))
							.multiply(stair2price);
					stair3num = stair3amount.subtract(stair2amount);
					stair3fee = (stair3amount.subtract(stair2amount))
							.multiply(stair3price);
					stair4num = allamont.subtract(stair3amount);
					stair4fee = (allamont.subtract(stair3amount))
							.multiply(stair4price);
					chargenum = stair2fee.add(stair3fee).add(stair4fee);
				}
				// ��ǰ�ѹ������ڽ�������
			} else if (sumamont.compareTo(stair2amount) >= 0
					&& sumamont.compareTo(stair3amount) < 0) {
				if (allamont.compareTo(stair3amount) < 0) {
					stair3num = gas;
					stair3fee = gas.multiply(stair3price);
					chargenum = stair3fee;
				} else {
					stair3num = stair3amount.subtract(sumamont);
					stair3fee = (stair3amount.subtract(sumamont))
							.multiply(stair3price);
					stair4num = allamont.subtract(stair3amount);
					stair4fee = (allamont.subtract(stair3amount))
							.multiply(stair4price);
					chargenum = stair3fee.add(stair4fee);
				}
				// ��ǰ�ѹ���������������
			} else if (sumamont.compareTo(stair3amount) >= 0) {
				stair4num = gas;
				stair4fee = gas.multiply(stair4price);
				chargenum = stair4fee;
			}
			// ���û�δ���ý�������
		} else {
			chargenum = gas.multiply(gasprice);
			stair1num = new BigDecimal(0);
			stair2num = new BigDecimal(0);
			stair3num = new BigDecimal(0);
			stair4num = new BigDecimal(0);
			stair1fee = new BigDecimal(0);
			stair2fee = new BigDecimal(0);
			stair3fee = new BigDecimal(0);
			stair4fee = new BigDecimal(0);
		}

		return chargenum;
	}

	/**
	 * �����û�������ϸ
	 */
	private void financedetailDisp(Map<String, Object> handplan,
			BigDecimal gas, BigDecimal money, String sgnetwork,
			String sgoperator) throws Exception {
		// ȡ���˻�����
		String acczhyeStr = handplan.get("f_accountzhye").toString();
		if (acczhyeStr == null || acczhyeStr.equals("")) {
			throw new RSException(handplan.get("f_userid") + "û���˻�ʵ�ʽ�!");
		}
		BigDecimal accountzhye = new BigDecimal(acczhyeStr);
		// ����˻���� = 0����������ΪǷ�ѣ���������Ƿ��¼
		if (accountzhye.doubleValue() <= 0) {
			String handId = handplan.get("id").toString();
			noBalance(handId, money);
			return;
		}
		// ��Ƿ���� ,����ʵ�գ�Ƿ�ѣ��˻����½���
		BigDecimal realmony = new BigDecimal(0);
		BigDecimal debtmoney = new BigDecimal(0);
		BigDecimal newaccountzhye = new BigDecimal(0);
		// ���� > Ӧ�� �� ���ս�� =Ӧ�ս��, Ƿ�ѽ��= 0,�˻���� = ����- Ӧ��
		if (accountzhye.compareTo(money) >= 0) {
			realmony = money;
			debtmoney = new BigDecimal(0);
			newaccountzhye = accountzhye.subtract(money);
		}
		// ����С��Ӧ��, ���ս��=������, ,Ƿ�ѽ��=Ӧ�ս��-������,
		// �˻����= 0
		else if (accountzhye.compareTo(money) < 0) {
			realmony = accountzhye;
			debtmoney = money.subtract(accountzhye);
			newaccountzhye = new BigDecimal(0);
		}
		// ������Ƿ����
		Date now = new Date();
		Map<String, Object> finance = new HashMap<String, Object>();
		// <!--���ս��-->
		finance.put("f_realmoney", realmony.doubleValue());
		// <!--Ƿ�ѽ��-->
		finance.put("f_debtmoney", debtmoney.doubleValue());
		// <!--�˻�����-->
		finance.put("f_accountzhye", newaccountzhye.doubleValue());
		// <!--�û����-->
		finance.put("f_userid", handplan.get("f_userid"));
		// ԭ�˻����
		finance.put("f_prevaccountzhye", accountzhye.doubleValue());
		// <!--Ӧ������-->
		finance.put("f_oughtamount", gas.doubleValue());
		// <!--Ӧ�ս��-->
		finance.put("f_oughtfee", money.doubleValue());
		// ����
		BigDecimal gasPrice = new BigDecimal(handplan.get("f_gasprice")
				.toString());
		finance.put("f_gasprice", gasPrice.doubleValue());
		// �����¼������
		finance.put("f_debtdate", now);
		// <!--�Ƿ���Ч(��Ч/��Ч)-->
		finance.put("f_payfeevalid", "��Ч");
		finance.put("f_payfeetype", "����");
		// <!--����-->
		finance.put("f_sgnetwork", sgnetwork);
		// <!--����Ա-->
		finance.put("f_opertor", sgoperator);
		// �������ڣ�ʱ��
		finance.put("f_deliverydate", now);
		finance.put("f_deliverytime", now);
		// ����id
		finance.put("f_handid", handplan.get("id"));
		// ����
		JSONObject financeJson = (JSONObject) new JsonTransfer()
				.MapToJson(finance);
		log.debug("������Ƿ��ϸ����" + financeJson);
		Object idObj = hibernateTemplate.save("t_financedetail", finance);
		int saveId = Integer.parseInt(idObj.toString());
		log.debug("����ɹ�,����id" + saveId);
		// ���µ����˻�����f_accountzhye
		String uid = finance.get("f_userid").toString();
		String accZhye = finance.get("f_accountzhye").toString();
		String updateUserFile = "update t_userfiles set f_accountzhye="
				+ accZhye + " where f_userid='" + uid + "'";
		log.debug("���µ����˻�����" + updateUserFile);
		this.hibernateTemplate.bulkUpdate(updateUserFile);
		log.debug("���µ����˻��ɹ�");
		// ���³����¼ʵ��Ƿ��
		String handId = handplan.get("id").toString();
		String updateHandplan = "update t_handplan set f_debtmoney="
				+ debtmoney + " where id='" + handId + "'";
		log.debug("���³���Ƿ��" + updateHandplan);
		this.hibernateTemplate.bulkUpdate(updateHandplan);
	}

	// ���˻�ʵ�ʽ��ദ��
	private void noBalance(String handId, BigDecimal money) {
		try {
			String updateHandplan = "update t_handplan set f_debtmoney="
					+ money.doubleValue() + " where id='" + handId + "'";
			log.debug("�˻�ʵ�ʽ���0,���³���" + updateHandplan + "Ƿ��"
					+ money.doubleValue());
			this.hibernateTemplate.bulkUpdate(updateHandplan);
		} catch (RuntimeException e) {
			throw new RSException("����Ϊ0ʱ,���³���" + handId + "ʧ��");
		}

	}

	/**
	 * �����û�δ�����¼
	 */
	private Map<String, Object> findHandPlan(String userid) {
		Map<String, Object> result = null;
		String hql = "";
		final String sql = "select isnull(u.f_userid,'') f_userid, isnull(u.f_zhye,'') f_zhye ,isnull(u.f_accountzhye,'') f_accountzhye ,  isnull(u.lastinputgasnum,'') lastinputgasnum, isnull(u.f_gasprice,0) f_gasprice, isnull(u.f_username,'')  f_username,"
				+ "isnull(u.f_stair1amount,0)f_stair1amount,isnull(u.f_stair2amount,0)f_stair2amount,isnull(u.f_stair3amount,0)f_stair3amount,isnull(u.f_stair1price,0)f_stair1price,isnull(u.f_stair2price,0)f_stair2price,isnull(u.f_stair3price,0)f_stair3price,isnull(u.f_stair4price,0)f_stair4price,isnull(u.f_stairmonths,0)f_stairmonths,isnull(u.f_stairtype,'δ��')f_stairtype,"
				+ "isnull(u.f_address,'')f_address ,isnull(u.f_districtname,'')f_districtname,isnull(u.f_cusDom,'')f_cusDom,isnull(u.f_cusDy,'')f_cusDy,isnull(u.f_gasmeterstyle,'') f_gasmeterstyle, isnull(u.f_idnumber,'') f_idnumber, isnull(u.f_gaswatchbrand,'')f_gaswatchbrand, isnull(u.f_usertype,'')f_usertype, "
				+ "isnull(u.f_gasproperties,'')f_gasproperties,isnull(u.f_dibaohu,0)f_dibaohu,isnull(u.f_payment,'')f_payment,isnull(u.f_zerenbumen,'')f_zerenbumen,isnull(u.f_menzhan,'')f_menzhan,isnull(u.f_inputtor,'')f_inputtor, isnull(q.c,0) c,"
				+ "isnull(u.f_metergasnums,0) f_metergasnums,isnull(u.f_cumulativepurchase,0)f_cumulativepurchase, "
				+ "isnull(u.f_finallybought,0)f_finallybought,isnull(u.f_cardid,'NULL') f_cardid,isnull(u.f_filiale,'NULL')f_filiale,"
				+ "h.id id from (select * from t_handplan where f_state='δ����' and f_userid='"
				+ userid
				+ "') h "
				+ "left join (select f_userid, COUNT(*) c from t_handplan where f_state='�ѳ���' and shifoujiaofei='��' "
				+ "group by f_userid) q on h.f_userid=q.f_userid join t_userfiles u on h.f_userid=u.f_userid order by id";
		List<Map<String, Object>> list = (List<Map<String, Object>>) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session.createSQLQuery(sql);
						q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						List result = q.list();
						return result;
					}
				});
		// ȡ��δ�����¼�Լ�����
		if(list.size()>0){
			result = (Map<String, Object>) list.get(0);
			return result;
		}else{
			return null;
		}
	}

	// ���㿪ʼʱ�䷽��
	private void CountDate(String userid, HibernateTemplate hibernateTemplate) {
		// �ж��Ƿ������˽ӿڣ������ִ�нӿڣ����û�а�Ĭ�ϼ��㡣
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(ContextListener.getContext());
		if (applicationContext.containsBean("CountDate")) {
			ICountDate icount = (ICountDate) applicationContext
					.getBean("CountDate");
			stardate = icount.startdate(userid, hibernateTemplate);
			enddate = icount.enddate(userid, hibernateTemplate);
			return;
		}
		// ���㵱ǰ�����ĸ���������
		Calendar cal = Calendar.getInstance();
		int thismonth = cal.get(Calendar.MONTH) + 1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (stairmonths == 1) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		} else if (stairmonths == 0) {
			stardate = "";
			enddate = "";
		} else {
			/*
			 * ������ʼ����������ʼ�� = ��ǰ��/��������*��������+1������ = ��ǰ��/��������*��������+��������ע��������
			 * ��ǰ����12��ʱ����Ҫ��1 �����Ѿ������������Ϊ1����ʱ�Ľ��һ�����������������Ϊ������ ���Զ�������û��Ӱ��
			 */
			if (thismonth == 12) {
				thismonth = 11;
			}
			// ������ʼ��
			int star = Math.round(thismonth / stairmonths) * stairmonths + 1;
			// ���������
			int end = Math.round(thismonth / stairmonths) * stairmonths
					+ stairmonths;
			// �����ʼ���ںͽ�������
			cal.set(Calendar.MONTH, star - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			stardate = format.format(cal.getTime());
			cal.set(Calendar.MONTH, end - 1);
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			enddate = format.format(cal.getTime());
		}
	}

	// ��ȡ���е�ֵ��ת����Map
	private Map<String, String> getSingles() {
		Map result = new HashMap<String, String>();

		String sql = "select name,value from t_singlevalue";
		List<Map<String, Object>> list = this.hibernateTemplate
				.executeFind(new HibernateSQLCall(sql));
		for (Map<String, Object> hand : list) {
			result.put(hand.get("name"), hand.get("value"));
		}
		return result;
	}

	private Object String(String zerenbumen) {
		// TODO Auto-generated method stub
		return null;
	}
	@Path("record/batch/App")
	@POST
	public String afAPPUploadBatch(String data) {
		log.debug("App�����¼�ϴ� ��ʼ");
		JSONObject jo = new JSONObject();
		try {
			JSONArray rows = new JSONArray(data);
			String re = "";
			// ��ÿһ�����ݣ����õ����������ݴ������
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				String userid = row.getString("f_userid");
				double reading = Double.parseDouble(row.getString("lastrecord"));
				String handdate = row.getString("f_handdate");
				String network = row.getString("f_network");
				String operator = row.getString("f_operator");
				String inputdate = row.getString("f_inputdate");
				String meterstate = row.getString("f_meterstate");
				double lastreading = row.getDouble("lastreading");
				// ��ȡ������������¼�룬û������������Double.NaN
				double leftgas = 0;
				if (row.has("leftgas")) {
					leftgas = row.getDouble("leftgas");
				}
				if("noPlan".equals(row.getString("source"))){
				}else{
					if(findHandPlan(userid)==null){
						jo.put(userid, "null");
					}else{
						re= afrecordInput(userid, lastreading, reading, network, operator,
								inputdate, handdate, leftgas,meterstate, 2);
						jo.put(re, "ok");
					}
				}
			}
			return jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return jo.toString();
		}
	}
	// ���������¼�ϴ�
	// data��JSON��ʽ�ϴ���[{userid:'�û����', showNumber:���ڳ�����},{}]
	@Path("record/batch/{handdate}/{sgnetwork}/{sgoperator}/{lastinputdate}/{meterstate}")
	@POST
	public String afRecordInputForMore(String data,
			@PathParam("sgnetwork") String sgnetwork,
			@PathParam("sgoperator") String sgoperator,
			@PathParam("lastinputdate") String lastinputdate,
			@PathParam("handdate") String handdate,
			@PathParam("meterstate") String meterstate) {
		log.debug("���������¼�ϴ� ��ʼ");
		String ret = "";
		// ������Ϣ
		String error = "";
		try {
			// ȡ����������
			JSONArray rows = new JSONArray(data);
			// ��ÿһ�����ݣ����õ����������ݴ������
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				String userid = row.getString("userid");
				double reading = row.getDouble("reading");
				double lastreading = row.getDouble("lastreading");
				// ��ȡ������������¼�룬û������������Double.NaN
				double leftgas = 0;
				if (row.has("leftgas")) {
					leftgas = row.getDouble("leftgas");
				}

				try {
					afrecordInput(userid, lastreading, reading, sgnetwork, sgoperator,
							lastinputdate, handdate, leftgas, meterstate,2);
					// ����Զ����쳣
				} catch (RSException e) {
					// ƴ�Ӵ�����Ϣ
					error += e.getMessage();
				}
			}
			// ����д�����Ϣ���׳��쳣�����ص�ǰ̨��ʾ
			if (!error.equals("")) {
				throw new RSException(error);

			}
			log.debug("���������¼�ϴ� ����");
		} catch (Exception e) {
			log.debug("���������¼�ϴ� ʧ�ܣ�" + e.getMessage());
			ret = e.getMessage();
		} finally {
			return ret;
		}
	}

	private String SolitaryCopyMeter() {
		return null;

	}

	
	// �������ѽ�ֹ����
	private Date endDate(String str) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = str.substring(0, 10);
		Date now = df.parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DATE, 10);
		return c.getTime();
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
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						// û���صļ��ϲ���
						if (set.wasInitialized()) {
							json.put(key, ToJson(set));
						}
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
			JSONArray array = new JSONArray();
			for (Object obj : set) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
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

	class HibernateSQLCall implements HibernateCallback {
		String sql;

		public HibernateSQLCall(String sql) {
			this.sql = sql;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List result = q.list();
			return result;
		}
	}

}
