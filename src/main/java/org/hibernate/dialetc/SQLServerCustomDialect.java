package org.hibernate.dialetc;

import org.hibernate.Hibernate;
import org.hibernate.dialect.SQLServerDialect;

//�Զ���sqlserver���ԣ�������������ƥ�䲻�ϵ�����
public class SQLServerCustomDialect extends SQLServerDialect {
	public SQLServerCustomDialect() {
		super();
		registerHibernateType(-16, Hibernate.STRING.getName());
	}
}
