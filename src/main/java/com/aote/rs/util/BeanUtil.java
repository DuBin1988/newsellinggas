package com.aote.rs.util;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aote.listener.ContextListener;
import com.aote.rs.charge.countdate.ICountDate;

/**
 * spinrg  bean������
 * @author Administrator
 *
 */
public class BeanUtil {

	/**
	 * �������ͻ�������еĵ�һ������
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static Object getBean(Class type) throws Exception{
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(ContextListener.getContext());
		Map beans =  applicationContext.getBeansOfType(type);
		if(beans.values().size()==0){
			throw new Exception("��applicationContext.xml��û���ҵ�����Ϊ"+type+"������");
		}
		return beans.values().iterator().next();
	}
}
