package com.aote.expression.paramprocessor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.expression.Param;

/**
 * ȡ��ֵ�����Ը��ݲ���ָ���Ƿ��Զ���1
 */
public class SingleProcessor implements ParamProcessor {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	// ����nameȡ����
	public String process(Param param) {
		String result;
		Map paramData = param.getParams();
		String name = (String) paramData.get("name");
		String length = (String) paramData.get("length");
		String add = (String) paramData.get("add");
		// ���������ҵ���ֵ
		String query = "from t_singlevalue where name='" + name + "'";
		List<Object> list = this.hibernateTemplate.find(query);
		if (list.size() != 1) {
			// ��ѯ���������ݣ��ܳ��쳣
			throw new RuntimeException("��ֵ" + name + "������");
		}
		// �ѵ���mapת����JSON����
		Map<String, Object> map = (Map<String, Object>) list.get(0);
		long attrVal = Long.parseLong(map.get("value").toString());
		// �趨��ȡ��ֵ���1
		if (add != null && add.equals("true")) {
			map.put("value", attrVal + 1 + "");
		}
		this.hibernateTemplate.update(map);
		// �����趨�ĳ��Ȳ���
		result = attrVal + "";
		if (length == null || length.equals("")) {
			return result;
		}
		// ���Ȳ�����ر��ǰ��0
		int defLength = Integer.parseInt(length);
		int numLength = result.length();
		for (int i = 0; i < (defLength - numLength); i++) {
			result = "0" + result;
		}
		return result;
	}
}
