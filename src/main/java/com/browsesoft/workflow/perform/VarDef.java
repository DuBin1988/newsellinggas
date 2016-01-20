package com.browsesoft.workflow.perform;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * 变量定义
 * 
 * @author Browsesoft
 * @version 1.0
 */
public class VarDef extends FlowDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5164705896837393419L;

	/**
	 * 使用DOM元素构造
	 */
	public VarDef(Element e) {
		setElement(e);
	}

	/**
	 * 设置属性
	 */
	public void setElement(Element e) {
		super.setElement(e);
	}
}
