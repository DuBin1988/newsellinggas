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

import com.aote.rs.BankService;
import com.aote.rs.exception.RSException;
import com.aote.rs.exception.ResultException;
import com.aote.rs.util.SynchronizedTools;

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
	 * @return
	 */
	@SuppressWarnings("finally")
	@Path("touinfo/{userinfoname}/{useridname}")
	@POST
	public JSONObject txtoinfo(String files,
			@PathParam("userinfoname") String userinfoname,
			@PathParam("useridname") String useridname) {
		JSONObject result = new JSONObject();
		try {
			JSONArray list = new JSONArray(files);
			for (int l = 0; l < list.length(); l++) {
				JSONObject u = list.getJSONObject(l);
				// ����������,���ز����Ļ����
				String userinfoid = inserthu(u, userinfoname);
				// ��������
				insertfile(u, userinfoid, useridname);
			}
			result.put("success", "����һ�������Ϣ��ɣ�");
		} catch (Exception e) {
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
	 * @return ����
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String inserthu(JSONObject json, String userinfoname)
			throws ResultException, JSONException {
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
		json.put("f_stair1amount", map.get("f_stair1amount"));
		json.put("f_stair1price", map.get("f_stair1price"));
		json.put("f_stair2amount", map.get("f_stair2amount"));
		json.put("f_stair2price", map.get("f_stair2price"));
		json.put("f_stair3amount", map.get("f_stair3amount"));
		json.put("f_stair3price", map.get("f_stair3price"));
		json.put("f_stair4amount", map.get("f_stair4amount"));
		json.put("f_stair4price", map.get("f_stair4price"));
		json.put("f_stairmonths", map.get("f_stairmonths"));
		// ���������
		JSONObject j = SynchronizedTools
				.getSerialNumber(this.hibernateTemplate.getSessionFactory(),
						"from t_singlevalue where name='" + userinfoname + "'",
						"value");
		String userinfoid = j.getString("value");
		result = userinfoid;
		json.put("f_userid", userinfoid);
		this.hibernateTemplate.save("t_userinfo", json);
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
	 * @return ����
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String insertfile(JSONObject json, String userinfoid,
			String useridname) throws ResultException, JSONException {
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
		json.put("f_stair1amount", map.get("f_stair1amount"));
		json.put("f_stair1price", map.get("f_stair1price"));
		json.put("f_stair2amount", map.get("f_stair2amount"));
		json.put("f_stair2price", map.get("f_stair2price"));
		json.put("f_stair3amount", map.get("f_stair3amount"));
		json.put("f_stair3price", map.get("f_stair3price"));
		json.put("f_stair4amount", map.get("f_stair4amount"));
		json.put("f_stair4price", map.get("f_stair4price"));
		json.put("f_stairmonths", map.get("f_stairmonths"));
		// ���������
		JSONObject j = SynchronizedTools.getSerialNumber(
				this.hibernateTemplate.getSessionFactory(),
				"from t_singlevalue where name='" + useridname + "'", "value");
		String userid = j.getString("value");
		result = userid;
		json.put("f_userid", userid);
		json.put("f_userinfoid", userinfoid);
		this.hibernateTemplate.save("t_userfiles", json);
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
