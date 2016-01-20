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
 * ����ʱ�����̶���
 */
public class ProcessDef extends FlowDef {
	static Logger log = Logger.getLogger(ProcessDef.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8854627878683187720L;

	/**
	 * �����������Ļ�б�
	 */
	private LinkedList activities = new LinkedList();

	/**
	 * ������������ת���б�
	 */
	private LinkedList diversions = new LinkedList();

	/**
	 * ������������ִ�����б�
	 */
	private LinkedList actors = new LinkedList();

	/**
	 * �����������ı����б�
	 */
	private LinkedList<VarDef> vars = new LinkedList();

	/**
	 * ʹ���ļ��������̶���
	 * 
	 * @param file
	 *            �ļ�
	 */
	public ProcessDef(Diagram diagram) throws Exception {
		this.name = diagram.getName();
		setDiagram(diagram);
	}

	public LinkedList<VarDef> getVars() {
		return vars;
	}

	/**
	 * ���ݻid�ŵõ��
	 */
	public ActivityDef getActivity(String ID) {
		for (int i = 0; i < activities.size(); i++) {
			ActivityDef activity = (ActivityDef) activities.get(i);
			if (activity.getID().equals(ID)) {
				return activity;
			}
		}
		throw new InvalidActivityInstanceException("���̶�����û�и����ŵĻ���壺���̶���="
				+ this.name + "������=" + ID);
	}

	/**
	 * ��������ʵ��
	 * 
	 * @return ����ʵ��
	 */
	public ProcessInstance createInstance(Session session,String id) throws Exception {
		// ��������ʵ��
		ProcessInstance ins = new ProcessInstance(this,id);
		// ������������
		ins.setState("��ʼ");
		session.save(ins);
		return ins;
	}

	/**
	 * �õ���ʼ�����
	 * 
	 * @return ��ʼ�
	 */
	public ActivityDef getStartActivity() {
		return (ActivityDef) activities.get(0);
	}

	/**
	 * ȡ������ת��
	 * 
	 * @return ת���б�
	 */
	public LinkedList getDiversions() {
		return diversions;
	}

	/**
	 * ����ͷβ��õ�ת�ƶ���
	 */
	public DiversionDef getDiverison(ActivityDef head, ActivityDef tail) {
		for (int i = 0; i < diversions.size(); i++) {
			DiversionDef def = (DiversionDef) diversions.get(i);
			if (def.getHead().getID().equals(head.getID())
					&& def.getTail().getID().equals(tail.getID())
					&& !def.getType().equals("����")) {
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
	 * �������̶�������
	 */
	public void setDiagram(Diagram diagram) {
		try {
			// ��ʼ�����̶������
			this.clearList();
			// ����Document
			String context = diagram.getContext();
			Reader reader = new StringReader(context);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new InputSource(reader));
			reader.close();
			// ��ȡ��������
			NodeList wnl = document.getElementsByTagName("WorkflowDiagram");
			Element e = (Element) wnl.item(0);
			this.setElement(e);
		} catch (Exception e) {
			System.out.println("�����ļ�" + diagram.getName() + "����Documentʱ�����쳣��");
			e.printStackTrace();
		}
	}

	public void setElement(Element e) {
		// ��ȡ�����д��ݵ��������
		LinkedList oldActors = (LinkedList) actors.clone();
		actors.clear();
		LinkedList oldActivities = (LinkedList) activities.clone();
		activities.clear();
		LinkedList oldDiversions = (LinkedList) diversions.clone();
		diversions.clear();
		vars.clear();
		// ����������Ԫ���м���������
		loadItems(e, oldActors, oldActivities, oldDiversions);
	}

	private void loadItems(Element e, LinkedList oldActors,
			LinkedList oldActivities, LinkedList oldDiversions) {
		// ����actor
		NodeList nl = e.getElementsByTagName("Actor");
		for (int i = 0; i < nl.getLength(); i++) {
			Element ee = (Element) nl.item(i);
			// �ҵ�actor
			ActorDef actor = (ActorDef) this.getDefByID(oldActors.iterator(),
					ee.getAttribute("id"));
			if (e.getTagName().equals("invalids")) {
				log.debug("load actor:" + ee.getAttribute("id"));
			}
			if (actor == null) {
				// ��ִ������ӵ�ִ�����б���
				actor = new ActorDef(ee);
			} else {
				actor.setElement(ee);
			}
			actors.add(actor);
			// ��ȡ��ִ���ߵ����л
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
				// ������Ա���ʽ
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
		// ����ת��
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

		//���ر���
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
	 * ����id�ţ��õ�������Ķ���
	 * 
	 * @param v
	 *            ����
	 * @param ID
	 *            ID��
	 * @return ����
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
	 * ������̶����е�ԭ�ȶ����б�
	 */
	public void clearList() {
		// ��ջ�б�
		this.activities.clear();
		// ���ִ�����б�
		this.actors.clear();
		// ���ת���б�
		this.diversions.clear();
	}

	/**
	 * ���ݻ���û
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
	 * ���̵����л
	 */
	public List getActivities() {
		return this.activities;
	}

	/**
	 * ȡ����ִ����
	 */
	public List getActors() {
		return this.actors;
	}

	/**
	 * ���ݻID��ִ���߶���
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
