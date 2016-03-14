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
import com.aote.rs.util.SynchronizedTools;
import com.aote.rs.util.UserTools;

/**
 * 档案服务
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
	 * 导入前台传入的档案信息，创建一户多表档案
	 * 
	 * @param files
	 *            需要保存的档案列表
	 * @param userinfoname
	 *            产生户编号的参数名
	 * @param useridname
	 *            产生表编号的参数名
	 * @param loginuserid
	 *            操作员id
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
				// 产生户档案,返回产生的户编号
				String userinfoid = inserthu(u, userinfoname, loginuserid);
				// 产生表档案
				insertfile(u, userinfoid, useridname, loginuserid);
			}
			result.put("success", "导入一户多表信息完成！");
		} catch (Exception e) {
			result.put("error", e.getMessage());
			throw new WebApplicationException(500);
		} finally {
			return result;
		}
	}

	/**
	 * 导入表信息
	 * 
	 * @param json
	 * @param userinfoname
	 *            户编号
	 * @param loginuserid
	 *            操作员id
	 * @return 表编号
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String inserthu(JSONObject json, String userinfoname,
			String loginuserid) throws ResultException, JSONException {
		json.remove("id");
		String result = "";
		// 获取阶梯信息
		String f_stairtype = json.getString("f_stairtype");
		if (f_stairtype == null || f_stairtype.equals("")) {
			throw new ResultException("属性f_stairtype没有设置值，不能生成阶梯信息！");
		}
		List list = this.hibernateTemplate
				.find("from t_stairprice where f_stairtype='" + f_stairtype
						+ "'");
		if (list.size() == 0) {
			throw new ResultException("没有找到名称为；" + f_stairtype + "的阶梯信息！");
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
		// 获得操作员，网点，分公司，组织信息
		Map user = UserTools.getUser(loginuserid,
				this.hibernateTemplate.getSessionFactory());
		// 操作员
		json.put("f_yytoper", user.get("name"));
		// 网点
		json.put("f_yytdepa", user.get("f_parentname"));
		// 分公司
		json.put("f_filiale", user.get("f_fengongsi"));
		// 分公司编号
		json.put("f_fengongsinum", user.get("f_fengongsinum"));
		// 组织
		json.put("f_OrgStr", user.get("orgpathstr"));
		// 产生户编号
		JSONObject j = SynchronizedTools
				.getSerialNumber(this.hibernateTemplate.getSessionFactory(),
						"from t_singlevalue where name='" + userinfoname + "'",
						"value");
		String userinfoid = user.get("f_fengongsinum") + j.getString("value");
		result = userinfoid;
		json.put("f_userid", userinfoid);
		this.hibernateTemplate.save("t_userinfo", json);
		return result;
	}

	/**
	 * 导入表信息
	 * 
	 * @param json
	 * @param userinfoid
	 *            户编号
	 * @param useridname
	 *            产生表编号的参数名
	 * @param loginuserid
	 *            操作员id
	 * @return 表编号
	 * @throws ResultException
	 * @throws JSONException
	 */
	private String insertfile(JSONObject json, String userinfoid,
			String useridname, String loginuserid) throws ResultException,
			JSONException {
		json.remove("id");
		String result = "";
		// 获取阶梯信息
		String f_stairtype = json.getString("f_stairtype");
		if (f_stairtype == null || f_stairtype.equals("")) {
			throw new ResultException("属性f_stairtype没有设置导入的阶梯名称！");
		}
		List list = this.hibernateTemplate
				.find("from t_stairprice where f_stairtype='" + f_stairtype
						+ "'");
		if (list.size() == 0) {
			throw new ResultException("没有找到名称为；" + f_stairtype + "的阶梯信息！");
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
		// 获得操作员，网点，分公司，组织信息
		Map user = UserTools.getUser(loginuserid,
				this.hibernateTemplate.getSessionFactory());
		// 操作员
		json.put("f_yytoper", user.get("name"));
		// 网点
		json.put("f_yytdepa", user.get("f_parentname"));
		// 分公司
		json.put("f_filiale", user.get("f_fengongsi"));
		// 分公司编号
		json.put("f_fengongsinum", user.get("f_fengongsinum"));
		// 组织
		json.put("f_OrgStr", user.get("orgpathstr"));
		// 产生户编号
		JSONObject j = SynchronizedTools.getSerialNumber(
				this.hibernateTemplate.getSessionFactory(),
				"from t_singlevalue where name='" + useridname + "'", "value");
		String userid = user.get("f_fengongsinum") + j.getString("value");
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
				throw new ResultException("测试");
			}
			System.out.println("try2");
		} catch (Exception e) {
			System.out.println("catch");
		} finally {
			System.out.println("finally");
		}
	}
}
