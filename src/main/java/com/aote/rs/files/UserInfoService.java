package com.aote.rs.files;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.aote.rs.exception.ResultException;
import com.aote.rs.util.JSONHelper;
import com.aote.rs.util.JsonTransfer;
import com.aote.rs.util.SynchronizedTools;
import com.aote.rs.util.UserTools;

/**
 * ��������
 * 
 * @author Administrator
 *
 */
@Path("files")
@Scope("prototype")
@Component
public class UserInfoService {
	static Logger log = Logger.getLogger(UserInfoService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	/**
	 * ����ǰ̨����ĵ�����Ϣ������һ�������
	 * 
	 * @param files
	 *            ��Ҫ����ĵ����б�
	 * @param userinfoname
	 *            ��������ŵĲ�����
	 * @param useridname
	 *            �������ŵĲ�����
	 * @param loginuserid
	 *            ����Աid
	 * @return
	 */
	@SuppressWarnings("finally")
	@Path("save/{userinfoname}/{useridname}/{loginuserid}")
	@POST
	public JSONObject txsave(String files,
			@PathParam("userinfoname") String userinfoname,
			@PathParam("useridname") String useridname,
			@PathParam("loginuserid") String loginuserid) {
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray(files);
			// ����Ϣ
			JSONObject operator = array.getJSONObject(0);
			JSONObject hu = operator.getJSONObject("data");
			Map humap = JSONHelper.toHashMap(hu);
			// �������Ϣ
			String child = hu.getString("child");
			JSONArray list = new JSONArray(child);
			// ����������,���ز����Ļ����
			String userinfoid = inserthu(hu, userinfoname, loginuserid);
			JsonTransfer Transfer = new JsonTransfer();
			Map savem;
			String userid = "";
			for (int l = 0; l < list.length(); l++) {
				JSONObject u = list.getJSONObject(l);
				Map umap = JSONHelper.toHashMap(u);
				savem = new HashMap<String, Object>();
				savem.putAll(humap);
				savem.putAll(umap);
				// ��������
				String uid = insertfile((JSONObject) Transfer.MapToJson(savem),
						userinfoid, useridname, loginuserid);
				if (userid == "") {
					userid = uid;
				} else {
					userid = uid + "," + userid;
				}
			}
			if (flag) {
				result.put("success", userinfoid + "�û���Ϣ�Ѵ��ڣ�������������ɣ����ţ�"
						+ userid);
			} else {
				result.put("success", userinfoid + "�û�������ɣ����ţ�" + userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", e.getMessage());
		} finally {
			return result;
		}
	}

	/**
	 * ����ǰ̨����ĵ�����Ϣ������һ�������
	 * 
	 * @param files
	 *            ��Ҫ����ĵ����б�
	 * @param userinfoname
	 *            ��������ŵĲ�����
	 * @param useridname
	 *            �������ŵĲ�����
	 * @param loginuserid
	 *            ����Աid
	 * @return
	 */
	@SuppressWarnings("finally")
	@Path("hubiao/{userinfoname}/{useridname}/{loginuserid}")
	@POST
	public JSONObject txhubiao(String files,
			@PathParam("userinfoname") String userinfoname,
			@PathParam("useridname") String useridname,
			@PathParam("loginuserid") String loginuserid) {
		JSONObject result = new JSONObject();
		try {
			JSONArray list = new JSONArray(files);
			for (int l = 0; l < list.length(); l++) {
				JSONObject u = list.getJSONObject(l);
				// ����������,���ز����Ļ����
				String userinfoid = inserthu(u, userinfoname, loginuserid);
				// ��������
				insertfile(u, userinfoid, useridname, loginuserid);
			}
			result.put("success", "����һ�������Ϣ��ɣ�");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", e.getMessage());
			throw new WebApplicationException(500);
		} finally {
			return result;
		}
	}

	/**
	 * ֻ��������Ϣ
	 * 
	 * @param files
	 * @param userinfoname
	 *            ��������ŵĲ�����
	 * @param loginuserid
	 *            ����Աid
	 * @return
	 */
	@SuppressWarnings("finally")
	@Path("hu/{userinfoname}/{loginuserid}")
	@POST
	public JSONObject txhu(String files,
			@PathParam("userinfoname") String userinfoname,
			@PathParam("loginuserid") String loginuserid) {
		JSONObject result = new JSONObject();
		try {
			JSONArray list = new JSONArray(files);
			for (int l = 0; l < list.length(); l++) {
				JSONObject u = list.getJSONObject(l);
				// ����������,���ز����Ļ����
				String userinfoid = inserthu(u, userinfoname, loginuserid);
			}
			result.put("success", "���뻧��Ϣ��ɣ�");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", e.getMessage());
			throw new WebApplicationException(500);
		} finally {
			return result;
		}
	}

	/**
	 * ֻ��������Ϣ
	 * 
	 * @param files
	 * @param useridname
	 *            �������ŵĲ�����
	 * @param loginuserid
	 *            ����Աid
	 * @return
	 */
	@SuppressWarnings("finally")
	@Path("biao/{useridname}/{loginuserid}")
	@POST
	public JSONObject txbiao(String files,
			@PathParam("useridname") String useridname,
			@PathParam("loginuserid") String loginuserid) {
		JSONObject result = new JSONObject();
		try {
			JSONArray list = new JSONArray(files);
			for (int l = 0; l < list.length(); l++) {
				JSONObject u = list.getJSONObject(l);
				// �����
				String userinfoid = u.getString("f_userinfoid");
				// ����������,���ز����Ļ����
				String userid = insertfile(u, userinfoid, useridname,
						loginuserid);
			}
			result.put("success", "�������Ϣ��ɣ�");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", e.getMessage());
			throw new WebApplicationException(500);
		} finally {
			return result;
		}
	}

	/**
	 * �Ƿ��Ѿ��л�������ʾ
	 */
	private boolean flag = false;

	/**
	 * ���뻧��Ϣ
	 * 
	 * @param json
	 * @param userinfoname
	 *            ��������ŵĵ�ֵ
	 * @param loginuserid
	 *            ����Աid
	 * @return �����
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String inserthu(JSONObject json, String userinfoname,
			String loginuserid) throws ResultException, JSONException {
		flag = false;
		json.remove("id");
		// ���ݵ�ַ�жϻ��Ƿ��Ѿ����ڣ��������ֱ�ӷ��ػ����
		if (!json.has("f_address")) {
			throw new ResultException("��ַ����f_addressû������ֵ���������ɻ���Ϣ��");
		}
		// ��ò���Ա�����㣬�ֹ�˾����֯��Ϣ
		Map user = UserTools.getUser(loginuserid, this.hibernateTemplate);
		if (user.get("f_fengongsi") == null || user.get("f_fengongsi") == "") {
			throw new ResultException("����Ա" + user.get("name")
					+ "û�����÷ֹ�˾��Ϣ�����ܻ�ȡ�ֹ�˾��ţ�");
		}
		String f_address = json.getString("f_address");
		List list = this.hibernateTemplate
				.find("from t_userinfo where f_address='" + f_address
						+ "' and f_filiale='" + user.get("f_fengongsi") + "'");
		// ���ڻ������ػ����
		if (list.size() > 0) {
			flag = true;
			Map<String, Object> map = (Map<String, Object>) list.get(0);
			return map.get("f_userid") + "";
		}
		String result = "";
		// ��ȡ������Ϣ
		String f_stairtype = json.getString("f_stairtype");
		if (f_stairtype == null || f_stairtype.equals("")) {
			throw new ResultException("����f_stairtypeû������ֵ���������ɽ�����Ϣ��");
		}
		list = this.hibernateTemplate
				.find("from t_stairprice where f_stairtype='" + f_stairtype
						+ "' and f_branch='" + user.get("f_fengongsi") + "'");
		if (list.size() == 0) {
			throw new ResultException(user.get("f_fengongsi") + "û���ҵ�����Ϊ��"
					+ f_stairtype + "�Ľ�����Ϣ��");
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		json.put("f_stair1amount",
				Double.parseDouble(map.get("f_stair1amount") + ""));
		json.put("f_stair1price",
				Double.parseDouble(map.get("f_stair1price") + ""));
		json.put("f_stair2amount",
				Double.parseDouble(map.get("f_stair2amount") + ""));
		json.put("f_stair2price",
				Double.parseDouble(map.get("f_stair2price") + ""));
		json.put("f_stair3amount",
				Double.parseDouble(map.get("f_stair3amount") + ""));
		json.put("f_stair3price",
				Double.parseDouble(map.get("f_stair3price") + ""));
		json.put("f_stair4price",
				Double.parseDouble(map.get("f_stair4price") + ""));
		json.put("f_stairmonths",
				Integer.parseInt(map.get("f_stairmonths") + ""));
		// ����Ա
		json.put("f_yytoper", user.get("name"));
		// ����
		json.put("f_yytdepa", user.get("f_parentname"));
		// �ֹ�˾
		json.put("f_filiale", user.get("f_fengongsi"));
		// �ֹ�˾���
		json.put("f_fengongsinum", user.get("f_fengongsinum") + "");
		// ��֯
		json.put("f_orgstr", user.get("orgpathstr"));
		// ���������
		JSONObject j = SynchronizedTools
				.getSerialNumber(
						this.hibernateTemplate,
						"from t_singlevalue where name='"
								+ user.get("f_fengongsi") + userinfoname + "'",
						"value");
		String userinfoid = user.get("f_fengongsinum") + j.getString("value");
		result = userinfoid;
		json.put("f_userid", userinfoid);
		Map userinfo = JSONHelper.toHashMap(json, hibernateTemplate,
				"t_userinfo");
		userinfo.put("f_yytdate", new Date());
		userinfo.put("f_yyttime", new Date());
		userinfo.put("f_zhye", 0.0);
		userinfo.put("f_accountzhye", 0.0);
		userinfo.put("f_metergasnums", 0.0);
		userinfo.put("f_cumulativepurchase", 0.0);
		userinfo.put("f_state", "����");
		userinfo.put("f_userstate", "����");
		userinfo.put("f_substate", "���");
		userinfo.put("f_whethergivecard", "δ��");
		userinfo.put("f_whethergivepassbook", "δ��");
		userinfo.put("f_zherownum", 13);
		userinfo.put("refreshCache", 1);
		userinfo.put("lastinputgasnum", 0.0);
		userinfo.put("lastinputdate", new Date());
		userinfo.put("f_metergasnums", 0.0);
		userinfo.put("f_cumulativepurchase", 0.0);
		this.hibernateTemplate.save("t_userinfo", userinfo);
		return result;
	}

	/**
	 * �������Ϣ
	 * 
	 * @param json
	 * @param userinfoid
	 *            �����
	 * @param useridname
	 *            �������ŵĲ�����
	 * @param loginuserid
	 *            ����Աid
	 * @return ����
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String insertfile(JSONObject json, String userinfoid,
			String useridname, String loginuserid) throws ResultException,
			JSONException {
		json.remove("id");
		String result = "";
		// ��ò���Ա�����㣬�ֹ�˾����֯��Ϣ
		Map user = UserTools.getUser(loginuserid, this.hibernateTemplate);
		if (user.get("f_fengongsi") == null || user.get("f_fengongsi") == "") {
			throw new ResultException("����Ա��" + user.get("name")
					+ " û�����÷ֹ�˾��Ϣ�����ܻ�ȡ�ֹ�˾��ţ�");
		}
		// ��ȡ������Ϣ
		String f_stairtype = json.getString("f_stairtype");
		if (f_stairtype == null || f_stairtype.equals("")) {
			throw new ResultException("����f_stairtypeû�����õ���Ľ������ƣ�");
		}
		List list = this.hibernateTemplate
				.find("from t_stairprice where f_stairtype='" + f_stairtype
						+ "' and f_branch='" + user.get("f_fengongsi") + "'");
		if (list.size() == 0) {
			throw new ResultException("û���ҵ�����Ϊ��" + f_stairtype + "�Ľ�����Ϣ��");
		}
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		json.put("f_stair1amount",
				Double.parseDouble(map.get("f_stair1amount") + ""));
		json.put("f_stair1price",
				Double.parseDouble(map.get("f_stair1price") + ""));
		json.put("f_stair2amount",
				Double.parseDouble(map.get("f_stair2amount") + ""));
		json.put("f_stair2price",
				Double.parseDouble(map.get("f_stair2price") + ""));
		json.put("f_stair3amount",
				Double.parseDouble(map.get("f_stair3amount") + ""));
		json.put("f_stair3price",
				Double.parseDouble(map.get("f_stair3price") + ""));
		json.put("f_stair4price",
				Double.parseDouble(map.get("f_stair4price") + ""));
		json.put("f_stairmonths",
				Integer.parseInt(map.get("f_stairmonths") + ""));
		// ����Ա
		json.put("f_yytoper", user.get("name"));
		// ����
		json.put("f_yytdepa", user.get("f_parentname"));
		// �ֹ�˾
		json.put("f_filiale", user.get("f_fengongsi"));
		// �ֹ�˾���
		json.put("f_fengongsinum", user.get("f_fengongsinum"));
		// ��֯
		json.put("f_orgstr", user.get("orgpathstr"));
		// ���������
		JSONObject j = SynchronizedTools.getSerialNumber(
				this.hibernateTemplate, "from t_singlevalue where name='"
						+ user.get("f_fengongsi") + useridname + "'", "value");
		String userid = user.get("f_fengongsinum") + j.getString("value");
		result = userid;
		json.put("f_userid", userid);
		json.put("f_userinfoid", userinfoid);
		Map userfile = JSONHelper.toHashMap(json, hibernateTemplate,
				"t_userfiles");
		userfile.put("f_yytdate", new Date());
		userfile.put("f_yyttime", new Date());
		userfile.put("f_zhye", 0.0);
		userfile.put("f_accountzhye", 0.0);
		userfile.put("f_state", "����");
		userfile.put("f_userstate", "����");
		userfile.put("f_substate", "���");
		userfile.put("f_whethergivecard", "δ��");
		userfile.put("f_whethergivepassbook", "δ��");
		userfile.put("f_zherownum", 13);
		// userfile.put("refreshCache", 1);
		userfile.put("lastinputgasnum",
				Double.parseDouble(userfile.get("lastinputgasnum") + ""));
		userfile.put("lastinputdate", new Date());
		userfile.put("f_metergasnums", 0.0);
		userfile.put("f_cumulativepurchase", 0.0);
		// bjql klx czsx tzed
		if (userfile.containsKey("bjql")) {
			String bjql = (String) userfile.get("bjql");
			if (bjql != null) {
				userfile.put("bjql", Integer.parseInt(bjql));
			}
		}
		if (userfile.containsKey("klx")) {
			String klx = (String) userfile.get("klx");
			if (klx != null) {
				userfile.put("klx", Integer.parseInt(klx));
			}
		}
		if (userfile.containsKey("czsx")) {
			String czsx = (String) userfile.get("czsx");
			if (czsx != null) {
				userfile.put("czsx", Integer.parseInt(czsx));
			}
		}
		if (userfile.containsKey("tzed")) {
			String tzed = (String) userfile.get("tzed");
			if (tzed != null) {
				userfile.put("tzed", Integer.parseInt(tzed));
			}
		}
		this.hibernateTemplate.save("t_userfiles", userfile);
		// �Ƿ���ָ��
		String f_send = json.getString("f_send");
		if (f_send.equals("��")) {
			send(json, "�û�����", �û�����.����);
		}
		return result;
	}

	/**
	 * ����Զ����ָ��
	 * 
	 * @param json
	 * @param optype
	 *            ��������
	 * @param sendtype
	 *            ָ������
	 * @throws JSONException
	 */
	private void send(JSONObject json, String optype, String sendtype)
			throws JSONException {
		Map info = new HashMap<String, Object>();
		info.put("f_aliasname", json.getString("f_aliasname"));
		// ��������
		info.put("f_type", optype);
		// ָ������
		info.put("f_typesun", sendtype);
		info.put("f_json", json.toString());
		// �ַ�״̬
		info.put("f_status", "1");
		info.put("f_taskStatus", "1");
		info.put("f_datetime", new Date());
		info.put("f_datetimetime", new Date());
		info.put("f_ffdatetime", new Date());
		info.put("f_ffdatetimetime", new Date());
		this.hibernateTemplate.save("t_infolist", info);
	}

	public static void main(String[] args) {
		try {
			System.out.println("try");
			if (1 == 1) {
				throw new ResultException("����");
			}
			System.out.println("try2");
		} catch (Exception e) {
			System.out.println("catch");
		} finally {
			System.out.println("finally");
		}
	}
	
	static class �û�����{
		public static String ����="0";
		public static String ���="1";
		public static String ����="2";
		public static String ����="3";
	}
}
