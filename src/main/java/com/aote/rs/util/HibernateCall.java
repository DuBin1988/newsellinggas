package com.aote.rs.util;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class HibernateCall implements HibernateCallback {
	String hql;
	int page;
	int rows;

	public HibernateCall(String hql, int page, int rows) {
		this.hql = hql;
		this.page = page;
		this.rows = rows;
	}

	public Object doInHibernate(Session session) {
		Query q = session.createQuery(hql);
		List result = q.setFirstResult(page * rows).setMaxResults(rows).list();
		return result;
	}
}
