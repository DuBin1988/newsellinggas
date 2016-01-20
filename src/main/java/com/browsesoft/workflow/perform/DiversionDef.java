package com.browsesoft.workflow.perform;

import org.w3c.dom.Element;

/**
 * 运行时的转移定义
 * 
 * @author Browsesoft
 * @version 1.0
 */
public class DiversionDef extends FlowDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 977257371974584935L;

	/**
	 * 转移头
	 */
	private ActivityDef head = null;

	/**
	 * 转移尾
	 */
	private ActivityDef tail = null;

	/**
	 * 转移表达式
	 */
	private String expression = null;

	/**
	 * 使用头尾构造
	 */
	public DiversionDef(ActivityDef head, ActivityDef tail, Element e) {
		this.head = head;
		this.tail = tail;
		this.setElement(e);
	}

	/**
	 * 使用头尾，名称，ID，类型构造
	 */
	public DiversionDef(ActivityDef head, ActivityDef tail, String name,
			String ID, String expression) {
		this.head = head;
		this.tail = tail;
		this.name = name;
		this.ID = ID;
		this.expression = expression;
	}

	/**
	 * 设置属性
	 * 
	 * @param e
	 *            属性
	 */
	public void setElement(Element e) {
		super.setElement(e);
		this.expression = e.getAttribute("expression");
	}

	public String getExpression() {
		return expression;
	}

	/**
	 * 得到类型
	 * 
	 * @return 类型
	 */
	public String getType() {
		return expression;
	}

	/**
	 * 得到转移头
	 * 
	 * @return 转移头
	 */
	public ActivityDef getHead() {
		return head;
	}

	/**
	 * 得到转移尾
	 * 
	 * @return 转移尾
	 */
	public ActivityDef getTail() {
		return tail;
	}
}