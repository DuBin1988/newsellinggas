package com.browsesoft.workflow.perform;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * 系统运行时的流程定义管理器
 */
public class ProcessDefManager {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7046680794343910842L;

	/**
	 * 单例
	 */
	private static ProcessDefManager instance;

	/**
	 * 流程定义名称与流程定义对照表
	 */
	private Hashtable nameAndProcesses = new Hashtable();

	public static ProcessDefManager getInstance() {
		return instance;
	}

	public ProcessDefManager() {
		instance = this;
	}

	/**
	 * 装入所有的流程定义
	 */
	public void load() throws Exception {
		// 取得所有的流程图
		DiagramManager manager = DiagramManager.getInstance();
		Enumeration v = manager.getDiagrams();
		while (v.hasMoreElements()) {
			// 对每一个流程图，产生流程定义
			Diagram diagram = (Diagram) v.nextElement();
			nameAndProcesses.put(diagram.getName(), new ProcessDef(diagram));
		}
	}

	/**
	 * 装入指定的流程定义
	 */
	public void reLoadProcessDefByName(Diagram d) throws Exception {
		// 得到旧的流程定义
		ProcessDef def = this.getProcessDef(d.getName());
		// 存在
		if (def != null) {
			def.setDiagram(d);
		}
		// 重新构造
		else {
			def = new ProcessDef(d);
		}
		nameAndProcesses.put(d.getName(), def);
	}

	/**
	 * 根据名字得到流程定义
	 * 
	 * @param name
	 *            流程名称
	 * @return 流程定义，不存在，返回null
	 */
	public ProcessDef getProcessDef(String name) {
		return (ProcessDef) nameAndProcesses.get(name);
	}

	/**
	 * 得到所有流程定义
	 * 
	 * @return 流程定义列表
	 */
	public Enumeration getProcesses() {
		return nameAndProcesses.elements();
	}

	/**
	 * 得到所有流程定义迭代器
	 * 
	 * @return 流程定义迭代器
	 */
	public Iterator getProcessesIterator() {
		return nameAndProcesses.values().iterator();
	}

}
