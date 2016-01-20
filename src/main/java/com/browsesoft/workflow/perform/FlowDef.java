package com.browsesoft.workflow.perform;

import java.io.Serializable;

import org.w3c.dom.Element;

/**
 * 基本的流程定义
 * 
 * @author Browsesoft
 * @version 1.0
 */
public class FlowDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 903200361815906617L;

	/**
	 * ID号
	 */
	protected String ID = "";

	/**
	 * 元素e
	 */
	private Element e;

	/**
	 * 名称
	 */
	protected String name = "";

	/**
	 * 得到ID号
	 * 
	 * @return ID号
	 */
	public String getID() {
		return ID;
	}

	/**
	 * 得到名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置属性
	 */
	public void setElement(Element e) {
		// 读取ID号
		ID = e.getAttribute("id");
		// 读取名称
		name = e.getAttribute("name");
		//
		this.e = e;
	}

	/**
	 * 得到元素e
	 * 
	 * @return 元素
	 */
	public Element getElement() {
		return e;
	}
}