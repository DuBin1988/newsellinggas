package com.aote.rs.bank.bankreturn;

import org.codehaus.jettison.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.aote.expression.Param;
import com.aote.expression.paramprocessor.NoFitValueException;

/**
 * 银行返盘接口
 * 
 * @author Administrator
 *
 */
public interface BankReturnInterface {

	
	
	/**
	 * Excel文件读取行
	 */

	/**
	 * 处理函数
	 * 
	 * @param param
	 * @return
	 */
	public abstract void process(JSONArray datas);

}
