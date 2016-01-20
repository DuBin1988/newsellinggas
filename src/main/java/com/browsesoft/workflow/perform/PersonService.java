package com.browsesoft.workflow.perform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

//��ʱ������Աӳ���ϵ
public class PersonService {
	public static void Run(String personExpression, Session session) {
		Map actor = new HashMap();
		actor.put("id", personExpression);
		Set persons = new HashSet();
		// �������Ա
		if (personExpression.startsWith("P")) {
			String name = personExpression.substring(2,
					personExpression.length() - 1);
			persons.add(name);
		}
		// �ǽ�ɫ
		else if (personExpression.startsWith("R")) {
			String name = personExpression.substring(2,
					personExpression.length() - 1);
			String sql = "select u.ename from t_user u join t_role r on charindex(r.id, u.roles)>0 "
					+ "where r.name='" + name + "'";
			for(Object o : session.createSQLQuery(sql).list()) {
				Map map = new HashMap();
				map.put("userid", o.toString());
				persons.add(map);
			}
		}
		actor.put("f_person", persons);
		session.saveOrUpdate("t_actor", actor);
	}
}
