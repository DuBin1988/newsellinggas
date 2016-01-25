package com.aote.expression.upkeep;

import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ά���ѹ���
 * 
 */
public class UpkeepFactory {

	private static UpkeepFactory instance = null;

	private static HashMap map = new HashMap();

	private UpkeepFactory() {

	}

	public static UpkeepFactory getInstance() {
		/**
		 * װ��
		 */
		if (instance == null) {
			instance = new UpkeepFactory();
			loadUpkeepComputers();
		}
		return instance;
	}

	private static void loadUpkeepComputers() {
		map.put("����", "com.aote.expression.upkeep.InhabitantUpkeep");
		map.put("����", "com.aote.expression.upkeep.GongFuUpkeep");
		map.put("��ҵ", "com.aote.expression.upkeep.GongFuUpkeep");
		map.put("����վ", "com.aote.expression.upkeep.GongFuUpkeep");
		map.put("��ҵ", "com.aote.expression.upkeep.BussinesUpkeep");
		map.put("ѧУ", "com.aote.expression.upkeep.BussinesUpkeep");
	}

	/**
	 * �õ�ά���Ѽ�����
	 */
	public UpkeepInterface getUpkeepComputer(String type) {
		String className = (String) map.get(type);
		try {
			return (UpkeepInterface) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("����ά���Ѽ���������", e);
		}
	}
}
