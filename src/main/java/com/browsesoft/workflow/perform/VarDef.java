package com.browsesoft.workflow.perform;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * ��������
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
	 * ʹ��DOMԪ�ع���
	 */
	public VarDef(Element e) {
		setElement(e);
	}

	/**
	 * ��������
	 */
	public void setElement(Element e) {
		super.setElement(e);
	}
}
