package com.aote.helper;



import javax.ws.rs.WebApplicationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class FunctionObject {

	/**
	 * 权限对象，用户从资源服务获取用户权限
	 */

	// 后台资源服务地址
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	// 功能节点
	public String rootname;

	public String getRootname() {
		return rootname;
	}

	public void setRootname(String rootname) {
		this.rootname = rootname;
	}

	// 需要获取的权限的父名称
	private String parentfuns;

	public String getParentfuns() {
		return parentfuns;
	}

	public void setparentfuns(String parentfuns) {
		this.parentfuns = parentfuns;
	}

	// 获得用户权限
	public JSONObject getRoot(String username, String password) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet getRequest = new HttpGet(getUrl() + username + "/"
					+ password + "/" + rootname);
			HttpResponse httpResponse = httpclient.execute(getRequest);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				String str = EntityUtils.toString(entity, "UTF-8");
				return new JSONObject(str);
			} else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(400);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	// 得到用户权限
	public String getRights(String username, String password) {
		
		try {
			JSONObject json = this.getRoot(username, password);
			// 默认获得二级权限
			if (this.parentfuns == null || this.parentfuns.equals("")) {
				return this.getSecondFun(json);
			}
			// 根据配置获得权限
			else {
				return this.getSpecRights(json);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 获得二级权限
	public String getSecondFun(JSONObject userFun) {
		try {
			String result = "";
			JSONArray array = userFun.getJSONArray("functions");
			for (int i = 0; i < array.length(); i++) {
				// 获得二级权限的所有名称
				JSONArray arr = array.getJSONObject(i).getJSONArray("children");
				for (int j = 0; j < arr.length(); j++) {
					result = result + arr.getJSONObject(j).get("name") + ",";
				}
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 得到指定的权限
	public String getSpecRights(JSONObject userFun) {
		try {
			String result = "";
			JSONArray array = userFun.getJSONArray("functions");
			result = filterRights(array, result);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String filterRights(JSONArray array, String result) {
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject one = array.getJSONObject(i);
				String name = (String) one.get("name");
				if (this.parentfuns.indexOf(name) != -1) {
					JSONArray arr = one.getJSONArray("children");
					for (int j = 0; j < arr.length(); j++) {
						result += arr.getJSONObject(j).get("name") + ",";
					}
				}
				if (one.has("children")) {
					result+=this.filterRights(one.getJSONArray("children"), result);
				}
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
