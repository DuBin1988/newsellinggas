package com.browsesoft.workflow.perform;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 运行时的流程定义
 */
public class ProcessDef extends FlowDef {
	static Logger log = Logger.getLogger(ProcessDef.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8854627878683187720L;

	/**
	 * 流程所包含的活动列表
	 */
	private LinkedList activities = new LinkedList();

	/**
	 * 流程所包含的转移列表
	 */
	private LinkedList diversions = new LinkedList();

	/**
	 * 流程所包含的执行者列表
	 */
	private LinkedList actors = new LinkedList();

	/**
	 * 流程所包含的变量列表
	 */
	private LinkedList<VarDef> vars = new LinkedList();

	/**
	 * 使用文件构造流程定义
	 * 
	 * @param file
	 *            文件
	 */
	public ProcessDef(Diagram diagram) throws Exception {
		this.name = diagram.getName();
		setDiagram(diagram);
	}

	public LinkedList<VarDef> getVars() {
		return vars;
	}

	/**
	 * 根据活动id号得到活动
	 */
	public ActivityDef getActivity(String ID) {
		for (int i = 0; i < activities.size(); i++) {
			ActivityDef activity = (ActivityDef) activities.get(i);
			if (activity.getID().equals(ID)) {
				return activity;
			}
		}
		throw new InvalidActivityInstanceException("流程定义里没有给定号的活动定义：流程定义="
				+ this.name + "活动定义号=" + ID);
	}

	/**
	 * 创建流程实例
	 * 
	 * @return 流程实例
	 */
	public ProcessInstance createInstance(Session session,String id) throws Exception {
		// 生成流程实例
		ProcessInstance ins = new ProcessInstance(this,id);
		// 设置流程属性
		ins.setState("开始");
		session.save(ins);
		return ins;
	}

	/**
	 * 得到开始活动定义
	 * 
	 * @return 开始活动
	 */
	public ActivityDef getStartActivity() {
		return (ActivityDef) activities.get(0);
	}

	/**
	 * 取得所有转移
	 * 
	 * @return 转移列表
	 */
	public LinkedList getDiversions() {
		return diversions;
	}

	/**
	 * 根据头尾活动得到转移定义
	 */
	public DiversionDef getDiverison(ActivityDef head, ActivityDef tail) {
		for (int i = 0; i < diversions.size(); i++) {
			DiversionDef def = (DiversionDef) diversions.get(i);
			if (def.getHead().getID().equals(head.getID())
					&& def.getTail().getID().equals(tail.getID())
					&& !def.getType().equals("结束")) {
				return def;
			}
		}
		return null;
	}

	public DiversionDef getDiversionDefById(String id) {
		for (int i = 0; i < this.getDiversions().size(); i++) {
			DiversionDef diversionDef = (DiversionDef) getDiversions().get(i);
			if (diversionDef.getID().equals(id)) {
				return diversionDef;
			}
		}
		return null;
	}

	/**
	 * 设置流程定义内容
	 */
	public void setDiagram(Diagram diagram) {
		try {
			// 初始化流程定义对象
			this.clearList();
			// 生成Document
			String context = diagram.getContext();
			Reader reader = new StringReader(context);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new InputSource(reader));
			reader.close();
			// 读取流程内容
			NodeList wnl = document.getElementsByTagName("WorkflowDiagram");
			Element e = (Element) wnl.item(0);
			this.setElement(e);
		} catch (Exception e) {
			System.out.println("流程文件" + diagram.getName() + "生成Document时出现异常！");
			e.printStackTrace();
		}
	}

	public void setElement(Element e) {
		// 读取流程中传递的类的名称
		LinkedList oldActors = (LinkedList) actors.clone();
		actors.clear();
		LinkedList oldActivities = (LinkedList) activities.clone();
		activities.clear();
		LinkedList oldDiversions = (LinkedList) diversions.clone();
		diversions.clear();
		vars.clear();
		// 从正常流程元素中加载流传项
		loadItems(e, oldActors, oldActivities, oldDiversions);
	}

	private void loadItems(Element e, LinkedList oldActors,
			LinkedList oldActivities, LinkedList oldDiversions) {
		// 处理actor
		NodeList nl = e.getElementsByTagName("Actor");
		for (int i = 0; i < nl.getLength(); i++) {
			Element ee = (Element) nl.item(i);
			// 找到actor
			ActorDef actor = (ActorDef) this.getDefByID(oldActors.iterator(),
					ee.getAttribute("id"));
			if (e.getTagName().equals("invalids")) {
				log.debug("load actor:" + ee.getAttribute("id"));
			}
			if (actor == null) {
				// 将执行者添加到执行者列表中
				actor = new ActorDef(ee);
			} else {
				actor.setElement(ee);
			}
			actors.add(actor);
			// 读取该执行者的所有活动
			NodeList children = ee.getElementsByTagName("Activity");
			for (int j = 0; j < children.getLength(); j++) {
				Element child = (Element) children.item(j);
				ActivityDef activity = (ActivityDef) this.getDefByID(
						oldActivities.iterator(), child.getAttribute("id"));
				if (e.getTagName().equals("invalids")) {
					log.debug("load activity:" + child.getAttribute("id"));
				}
				if (activity == null) {
					activity = new ActivityDef(actor, this, child);
				} else {
					activity.setElement(child);
				}
				// 设置人员表达式
				if (child.hasAttribute("personexpression")) {
					String personExpression = child
							.getAttribute("personexpression");
					activity.setPersonExpression(personExpression);
				}
				actor.setActivities(activity);
				activities.add(activity);
			}
		}
		log.debug("actors end.");
		// 构造转移
		nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (!nl.item(i).getNodeName().equals("Diversion")) {
				continue;
			}
			Element ee = (Element) nl.item(i);
			try {
				ActivityDef headDef = getActivity(ee.getAttribute("head"));
				ActivityDef tailDef = getActivity(ee.getAttribute("tail"));
				DiversionDef def = (DiversionDef) this.getDefByID(oldDiversions
						.iterator(), ee.getAttribute("id"));
				if (def == null) {
					def = new DiversionDef(headDef, tailDef, ee);
				} else {
					def.setElement(ee);
				}
				diversions.add(def);
			} catch (InvalidActivityInstanceException ex) {
				System.out.println("error diversion is: "
						+ ee.getAttribute("id"));
				throw ex;
			}
		}
		log.debug("diversions end.");

		//加载变量
		nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (!nl.item(i).getNodeName().equals("Var")) {
				continue;
			}
			Element ee = (Element) nl.item(i);
			VarDef varDef = new VarDef(ee);
			this.vars.add(varDef);
		}
		log.debug("diversions end.");
	}

	/**
	 * 根据id号，得到向量里的定义
	 * 
	 * @param v
	 *            向量
	 * @param ID
	 *            ID号
	 * @return 定义
	 */
	private FlowDef getDefByID(Iterator v, String ID) {
		while (v.hasNext()) {
			FlowDef def = (FlowDef) v.next();
			if (def.getID().equals(ID)) {
				return def;
			}
		}
		return null;
	}

	public String toString() {
		return this.getName();
	}

	/**
	 * 清空流程定义中的原先对象列表
	 */
	public void clearList() {
		// 清空活动列表
		this.activities.clear();
		// 清空执行者列表
		this.actors.clear();
		// 清空转移列表
		this.diversions.clear();
	}

	/**
	 * 根据活动名得活动
	 */
	public ActivityDef getActivityByName(String name) {
		Iterator iter = this.activities.iterator();
		while (iter.hasNext()) {
			ActivityDef activity = (ActivityDef) iter.next();
			if (activity.name.equals(name)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 流程的所有活动
	 */
	public List getActivities() {
		return this.activities;
	}

	/**
	 * 取所有执行者
	 */
	public List getActors() {
		return this.actors;
	}

	/**
	 * 根据活动ID得执行者定义
	 */
	public ActorDef getActorByActivityId(String id) {
		Iterator iter = this.actors.iterator();
		while (iter.hasNext()) {
			ActorDef actor = (ActorDef) iter.next();
			Iterator iterator = actor.getActivities().iterator();
			while (iterator.hasNext()) {
				ActivityDef act = (ActivityDef) iterator.next();
				if (act.ID.equals(id)) {
					return actor;
				}
			}
		}
		return null;
	}
}
