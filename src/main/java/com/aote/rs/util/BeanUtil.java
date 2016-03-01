package com.aote.rs.util;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aote.listener.ContextListener;
import com.aote.rs.charge.countdate.ICountDate;

/**
 * spinrg  bean帮助类
 * @author Administrator
 *
 */
public class BeanUtil {

	/**
	 * 根据类型获得配置中的第一个对象
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static Object getBean(Class type) throws Exception{
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(ContextListener.getContext());
		Map beans =  applicationContext.getBeansOfType(type);
		if(beans.values().size()==0){
			throw new Exception("在applicationContext.xml中没有找到类型为"+type+"的配置");
		}
		return beans.values().iterator().next();
	}
}
