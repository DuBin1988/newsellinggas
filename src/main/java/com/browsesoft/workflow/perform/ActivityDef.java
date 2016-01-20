package com.browsesoft.workflow.perform;

import java.util.LinkedList;

import org.w3c.dom.Element;

/**
 * 活动定义
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
	 * 活动所属流程
	 */
	private ProcessDef process = null;

	/**
	 * 活动的参与者
	 */
	private ActorDef actor = null;
	
	/**
	 * 执行者表达式
	 */
	private String personExpression = null;

	public String getPersonExpression() {
		return personExpression;
	}

	public void setPersonExpression(String personExpression) {
		this.personExpression = personExpression;
	}

	/**
	 * 使用参与者以及DOM元素构造
	 * 
	 * @param parent
	 */
	public ActivityDef(ActorDef actor, ProcessDef process, Element e) {
		this.actor = actor;
		this.process = process;
		setElement(e);
	}

	/**
	 * 设置属性
	 */
	public void setElement(Element e) {
		super.setElement(e);
	}

	/**
	 * 得到活动所属流程
	 * 
	 * @return 活动所属流程
	 */
	public ProcessDef getProcess() {
		return process;
	}

	/**
	 * 得到活动所属参与者
	 */
	public ActorDef getActor() {
		return actor;
	}

	/**
	 * 得到活动的所有前驱转移
	 * 
	 * @return 活动的所有前驱
	 */
	public LinkedList getJoins() {
		LinkedList result = new LinkedList();
		LinkedList diversions = process.getDiversions();
		for (int i = 0; i < diversions.size(); i++) {
			DiversionDef diversion = (DiversionDef) diversions.get(i);
			// 转移的结束为该活动
			if (diversion.getTail() == this) {
				result.add(diversion);
			}
		}
		return result;
	}

	/**
	 * 得到活动的后继转移
	 */
	public LinkedList<DiversionDef> getSplits() {
		LinkedList result = new LinkedList();
		LinkedList diversions = process.getDiversions();
		for (int i = 0; i < diversions.size(); i++) {
			DiversionDef diversion = (DiversionDef) diversions.get(i);
			// 转移的开始为该活动
			if (diversion.getHead() == this) {
				result.add(diversion);
			}
		}
		return result;
	}
}
