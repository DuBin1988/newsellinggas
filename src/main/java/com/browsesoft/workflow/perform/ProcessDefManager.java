package com.browsesoft.workflow.perform;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * ϵͳ����ʱ�����̶��������
 */
public class ProcessDefManager {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7046680794343910842L;

	/**
	 * ����
	 */
	private static ProcessDefManager instance;

	/**
	 * ���̶������������̶�����ձ�
	 */
	private Hashtable nameAndProcesses = new Hashtable();

	public static ProcessDefManager getInstance() {
		return instance;
	}

	public ProcessDefManager() {
		instance = this;
	}

	/**
	 * װ�����е����̶���
	 */
	public void load() throws Exception {
		// ȡ�����е�����ͼ
		DiagramManager manager = DiagramManager.getInstance();
		Enumeration v = manager.getDiagrams();
		while (v.hasMoreElements()) {
			// ��ÿһ������ͼ���������̶���
			Diagram diagram = (Diagram) v.nextElement();
			nameAndProcesses.put(diagram.getName(), new ProcessDef(diagram));
		}
	}

	/**
	 * װ��ָ�������̶���
	 */
	public void reLoadProcessDefByName(Diagram d) throws Exception {
		// �õ��ɵ����̶���
		ProcessDef def = this.getProcessDef(d.getName());
		// ����
		if (def != null) {
			def.setDiagram(d);
		}
		// ���¹���
		else {
			def = new ProcessDef(d);
		}
		nameAndProcesses.put(d.getName(), def);
	}

	/**
	 * �������ֵõ����̶���
	 * 
	 * @param name
	 *            ��������
	 * @return ���̶��壬�����ڣ�����null
	 */
	public ProcessDef getProcessDef(String name) {
		return (ProcessDef) nameAndProcesses.get(name);
	}

	/**
	 * �õ��������̶���
	 * 
	 * @return ���̶����б�
	 */
	public Enumeration getProcesses() {
		return nameAndProcesses.elements();
	}

	/**
	 * �õ��������̶��������
	 * 
	 * @return ���̶��������
	 */
	public Iterator getProcessesIterator() {
		return nameAndProcesses.values().iterator();
	}

}
