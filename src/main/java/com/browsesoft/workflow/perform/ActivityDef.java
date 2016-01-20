package com.browsesoft.workflow.perform;

import java.util.LinkedList;

import org.w3c.dom.Element;

/**
 * �����
 * 
 * @author Browsesoft
 * @version 1.0
 */
public class ActivityDef extends FlowDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6631734491662904499L;

	/**
	 * ���������
	 */
	private ProcessDef process = null;

	/**
	 * ��Ĳ�����
	 */
	private ActorDef actor = null;
	
	/**
	 * ִ���߱��ʽ
	 */
	private String personExpression = null;

	public String getPersonExpression() {
		return personExpression;
	}

	public void setPersonExpression(String personExpression) {
		this.personExpression = personExpression;
	}

	/**
	 * ʹ�ò������Լ�DOMԪ�ع���
	 * 
	 * @param parent
	 */
	public ActivityDef(ActorDef actor, ProcessDef process, Element e) {
		this.actor = actor;
		this.process = process;
		setElement(e);
	}

	/**
	 * ��������
	 */
	public void setElement(Element e) {
		super.setElement(e);
	}

	/**
	 * �õ����������
	 * 
	 * @return ���������
	 */
	public ProcessDef getProcess() {
		return process;
	}

	/**
	 * �õ������������
	 */
	public ActorDef getActor() {
		return actor;
	}

	/**
	 * �õ��������ǰ��ת��
	 * 
	 * @return �������ǰ��
	 */
	public LinkedList getJoins() {
		LinkedList result = new LinkedList();
		LinkedList diversions = process.getDiversions();
		for (int i = 0; i < diversions.size(); i++) {
			DiversionDef diversion = (DiversionDef) diversions.get(i);
			// ת�ƵĽ���Ϊ�û
			if (diversion.getTail() == this) {
				result.add(diversion);
			}
		}
		return result;
	}

	/**
	 * �õ���ĺ��ת��
	 */
	public LinkedList<DiversionDef> getSplits() {
		LinkedList result = new LinkedList();
		LinkedList diversions = process.getDiversions();
		for (int i = 0; i < diversions.size(); i++) {
			DiversionDef diversion = (DiversionDef) diversions.get(i);
			// ת�ƵĿ�ʼΪ�û
			if (diversion.getHead() == this) {
				result.add(diversion);
			}
		}
		return result;
	}
}
