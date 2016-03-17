package com.aote.rs.files;

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
	@Path("touinfo/{userinfoname}/{useridname}/{loginuserid}")
	@POST
	public JSONObject txtoinfo(String files,
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
	 * �������Ϣ
	 * 
	 * @param json
	 * @param userinfoname
	 *            �����
	 * @param loginuserid
	 *            ����Աid
	 * @return ����
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String inserthu(JSONObject json, String userinfoname,
			String loginuserid) throws ResultException, JSONException {
		json.remove("id");
		String result = "";
		// ��ȡ������Ϣ
		String f_stairtype = json.getString("f_stairtype");
		if (f_stairtype == null || f_stairtype.equals("")) {
			throw new ResultException("����f_stairtypeû������ֵ���������ɽ�����Ϣ��");
		}
		List list = this.hibernateTemplate
				.find("from t_stairprice where f_stairtype='" + f_stairtype
						+ "'");
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
		// ��ò���Ա�����㣬�ֹ�˾����֯��Ϣ
		Map user = UserTools.getUser(loginuserid, this.hibernateTemplate);
		// ����Ա
		json.put("f_yytoper", user.get("name"));
		// ����
		json.put("f_yytdepa", user.get("f_parentname"));
		// �ֹ�˾
		json.put("f_filiale", user.get("f_fengongsi"));
		// �ֹ�˾���
		json.put("f_fengongsinum", user.get("f_fengongsinum") + "");
		// ��֯
		json.put("f_OrgStr", user.get("orgpathstr"));
		// ���������
		JSONObject j = SynchronizedTools.getSerialNumber(
				this.hibernateTemplate, "from t_singlevalue where name='"
						+ userinfoname + "'", "value");
		String userinfoid = user.get("f_fengongsinum") + j.getString("value");
		result = userinfoid;
		json.put("f_userid", userinfoid);
		Map userinfo = JSONHelper.toHashMap(json, hibernateTemplate,
				"t_userinfo");
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
		// ��ȡ������Ϣ
		String f_stairtype = json.getString("f_stairtype");
		if (f_stairtype == null || f_stairtype.equals("")) {
			throw new ResultException("����f_stairtypeû�����õ���Ľ������ƣ�");
		}
		List list = this.hibernateTemplate
				.find("from t_stairprice where f_stairtype='" + f_stairtype
						+ "'");
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
		// ��ò���Ա�����㣬�ֹ�˾����֯��Ϣ
		Map user = UserTools.getUser(loginuserid, this.hibernateTemplate);
		// ����Ա
		json.put("f_yytoper", user.get("name"));
		// ����
		json.put("f_yytdepa", user.get("f_parentname"));
		// �ֹ�˾
		json.put("f_filiale", user.get("f_fengongsi"));
		// �ֹ�˾���
		json.put("f_fengongsinum", user.get("f_fengongsinum"));
		// ��֯
		json.put("f_OrgStr", user.get("orgpathstr"));
		// ���������
		JSONObject j = SynchronizedTools.getSerialNumber(
				this.hibernateTemplate, "from t_singlevalue where name='"
						+ useridname + "'", "value");
		String userid = user.get("f_fengongsinum") + j.getString("value");
		result = userid;
		json.put("f_userid", userid);
		json.put("f_userinfoid", userinfoid);
		this.hibernateTemplate.save("t_userfiles",
				JSONHelper.toHashMap(json, hibernateTemplate, "t_userfiles"));
		return result;
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
}
