package com.browsesoft.workflow.perform;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * 执行者定义
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
	 * 执行者对应的活动定义
	 */
	private List activitiesDef = new LinkedList();

	/**
	 * 使用DOM元素构造
	 */
	public ActorDef(Element e) {
		setElement(e);
	}

	/**
	 * 设置属性
	 */
	public void setElement(Element e) {
		super.setElement(e);
	}

	/**
	 * 得活动定义
	 */
	public List getActivities() {
		return this.activitiesDef;
	}

	/**
	 * 设置执行者的活动
	 */
	public void setActivities(ActivityDef activity) {
		this.activitiesDef.add(activity);
	}
}
