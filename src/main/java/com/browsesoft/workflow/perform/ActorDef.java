package com.browsesoft.workflow.perform;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * ִ���߶���
 * 
 * @author Browsesoft
 * @version 1.0
 */
public class ActorDef extends FlowDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5164705896837393419L;

	/**
	 * ִ���߶�Ӧ�Ļ����
	 */
	private List activitiesDef = new LinkedList();

	/**
	 * ʹ��DOMԪ�ع���
	 */
	public ActorDef(Element e) {
		setElement(e);
	}

	/**
	 * ��������
	 */
	public void setElement(Element e) {
		super.setElement(e);
	}

	/**
	 * �û����
	 */
	public List getActivities() {
		return this.activitiesDef;
	}

	/**
	 * ����ִ���ߵĻ
	 */
	public void setActivities(ActivityDef activity) {
		this.activitiesDef.add(activity);
	}
}
