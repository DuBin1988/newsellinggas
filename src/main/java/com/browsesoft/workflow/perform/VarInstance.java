package com.browsesoft.workflow.perform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.af.expression.Delegate;
import com.af.expression.Program;

/**
 * 运行时的变量实例
 */
public class VarInstance {

	private static final long serialVersionUID = -1697195877147493493L;

	/**
	 * 变量名称
	 */
	private String name;

	/**
	 * 变量值
	 */
	private String value;

	/**
	 * id
	 */
	private String id;

	public VarInstance() {
		super();
	}
	
	public VarInstance(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
