package com.aote.expression.upkeep;

import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 维护费工厂
 * 
 */
public class UpkeepFactory {

	private static UpkeepFactory instance = null;

	private static HashMap map = new HashMap();

	private UpkeepFactory() {

	}

	public static UpkeepFactory getInstance() {
		/**
		 * 装载
		 */
		if (instance == null) {
			instance = new UpkeepFactory();
			loadUpkeepComputers();
		}
		return instance;
	}

	private static void loadUpkeepComputers() {
		map.put("居民", "com.aote.expression.upkeep.InhabitantUpkeep");
		map.put("公福", "com.aote.expression.upkeep.GongFuUpkeep");
		map.put("工业", "com.aote.expression.upkeep.GongFuUpkeep");
		map.put("加气站", "com.aote.expression.upkeep.GongFuUpkeep");
		map.put("商业", "com.aote.expression.upkeep.BussinesUpkeep");
		map.put("学校", "com.aote.expression.upkeep.BussinesUpkeep");
	}

	/**
	 * 得到维护费计算器
	 */
	public UpkeepInterface getUpkeepComputer(String type) {
		String className = (String) map.get(type);
		try {
			return (UpkeepInterface) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("创建维护费计算器错误", e);
		}
	}
}
