package com.aote.expression.upkeep;

import java.util.Map;

import org.hibernate.SessionFactory;

public interface UpkeepInterface {

	/**
	 * ����ά����
	 */
	public String computeUpkeep(Map map, SessionFactory sessionFactory);
}
