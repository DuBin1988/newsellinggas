package com.browsesoft.workflow.perform;

import java.io.Serializable;

import org.w3c.dom.Element;

/**
 * ���������̶���
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
	 * ID��
	 */
	protected String ID = "";

	/**
	 * Ԫ��e
	 */
	private Element e;

	/**
	 * ����
	 */
	protected String name = "";

	/**
	 * �õ�ID��
	 * 
	 * @return ID��
	 */
	public String getID() {
		return ID;
	}

	/**
	 * �õ�����
	 * 
	 * @return ����
	 */
	public String getName() {
		return name;
	}

	/**
	 * ��������
	 */
	public void setElement(Element e) {
		// ��ȡID��
		ID = e.getAttribute("id");
		// ��ȡ����
		name = e.getAttribute("name");
		//
		this.e = e;
	}

	/**
	 * �õ�Ԫ��e
	 * 
	 * @return Ԫ��
	 */
	public Element getElement() {
		return e;
	}
}