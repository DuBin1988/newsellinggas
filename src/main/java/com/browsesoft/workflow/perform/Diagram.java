package com.browsesoft.workflow.perform;


/**
 * 流程图，保留了名字和内容
 */
public class Diagram {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2567973644574455068L;

	// 流程建模名字
	String name = "";

	// 流程建模内容

	String context = "";

	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return name;
	}

	public void setContext(String c) {
		this.context = c;
	}

	public String getContext() {
		return context;
	}
}