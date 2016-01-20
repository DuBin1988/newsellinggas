package com.browsesoft.workflow.perform;

import org.w3c.dom.Element;

/**
 * ����ʱ��ת�ƶ���
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
	 * ת��ͷ
	 */
	private ActivityDef head = null;

	/**
	 * ת��β
	 */
	private ActivityDef tail = null;

	/**
	 * ת�Ʊ��ʽ
	 */
	private String expression = null;

	/**
	 * ʹ��ͷβ����
	 */
	public DiversionDef(ActivityDef head, ActivityDef tail, Element e) {
		this.head = head;
		this.tail = tail;
		this.setElement(e);
	}

	/**
	 * ʹ��ͷβ�����ƣ�ID�����͹���
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
	 * ��������
	 * 
	 * @param e
	 *            ����
	 */
	public void setElement(Element e) {
		super.setElement(e);
		this.expression = e.getAttribute("expression");
	}

	public String getExpression() {
		return expression;
	}

	/**
	 * �õ�����
	 * 
	 * @return ����
	 */
	public String getType() {
		return expression;
	}

	/**
	 * �õ�ת��ͷ
	 * 
	 * @return ת��ͷ
	 */
	public ActivityDef getHead() {
		return head;
	}

	/**
	 * �õ�ת��β
	 * 
	 * @return ת��β
	 */
	public ActivityDef getTail() {
		return tail;
	}
}