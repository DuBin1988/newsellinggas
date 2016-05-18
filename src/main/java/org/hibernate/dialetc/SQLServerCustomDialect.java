package org.hibernate.dialetc;

import org.hibernate.Hibernate;
import org.hibernate.dialect.SQLServerDialect;

//自定义sqlserver方言，处理数据类型匹配不上的问题
public class SQLServerCustomDialect extends SQLServerDialect {
	public SQLServerCustomDialect() {
		super();
		registerHibernateType(-16, Hibernate.STRING.getName());
	}
}
