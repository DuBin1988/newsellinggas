package com.browsesoft.workflow.perform;


/**
 * ����ͼ�����������ֺ�����
 */
public class Diagram {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2567973644574455068L;

	// ���̽�ģ����
	String name = "";

	// ���̽�ģ����

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