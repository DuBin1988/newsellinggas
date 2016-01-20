package com.aote.expression.paramprocessor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.expression.Param;

/**
 * 取单值，可以根据参数指明是否自动加1
 */
public class SingleProcessor implements ParamProcessor {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	// 根据name取数据
	public String process(Param param) {
		String result;
		Map paramData = param.getParams();
		String name = (String) paramData.get("name");
		String length = (String) paramData.get("length");
		String add = (String) paramData.get("add");
		// 根据名称找到单值
		String query = "from t_singlevalue where name='" + name + "'";
		List<Object> list = this.hibernateTemplate.find(query);
		if (list.size() != 1) {
			// 查询到多条数据，跑出异常
			throw new RuntimeException("单值" + name + "不存在");
		}
		// 把单个map转换成JSON对象
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		long attrVal = Long.parseLong(map.get("value").toString());
		// 设定了取到值后加1
		if (add != null && add.equals("true")) {
			map.put("value", attrVal + 1 + "");
		}
		this.hibernateTemplate.update(map);
		// 根据设定的长度补零
		result = attrVal + "";
		if (length == null || length.equals("")) {
			return result;
		}
		// 长度不足得载编号前加0
		int defLength = Integer.parseInt(length);
		int numLength = result.length();
		for (int i = 0; i < (defLength - numLength); i++) {
			result = "0" + result;
		}
		return result;
	}
}
