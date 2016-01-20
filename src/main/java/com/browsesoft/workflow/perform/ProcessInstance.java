package com.browsesoft.workflow.perform;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;

import com.af.expression.Delegate;
import com.af.expression.Program;

/**
 * 运行时的流程实例
 */
public class ProcessInstance {

	public Set<VarInstance> getVars() {
		return vars;
	}

	public void setVars(Set<VarInstance> vars) {
		this.vars = vars;
	}

	private static final long serialVersionUID = -1697195877147493493L;

	/**
	 * 活动列表
	 */
	private Set activities = new HashSet();

	/**
	 * 变量列表
	 */
	private Set<VarInstance> vars = new HashSet();
	
	/**
	 * 流程定义名称及定义本身
	 */
	private String name;

	private ProcessDef define;

	/**
	 * 状态
	 */
	private String state = "";

	/**
	 * id
	 */
	private String id;

	/**
	 * 挂起原因
	 */
	private String handUpReason = "无";

	/**
	 * 执行者列表
	 */
	private List actors = new LinkedList();
	
	public ProcessInstance() {
		super();
	}

	/**
	 * 使用流程定义构造
	 * 
	 * @param define
	 *            流程定义
	 */
	public ProcessInstance(ProcessDef define,String id) {
		this.id = id;
		String name = define.getName();
		this.setName(name);
		
		//给流程中所有变量初始化成null
		initVar();
	}
	
	//给流程中所有变量初始化成null
	private void initVar() {
		for(VarDef var : this.getDefine().getVars()) {
			VarInstance varIns = new VarInstance(var.getName(), null);
			this.vars.add(varIns);
		}
	}
	
	//给流程中的变量赋值
	public void putVar(JSONObject vars) {
		for(VarInstance var : this.vars) {
			try {
				if(vars.has(var.getName())) {
					String value = vars.getString(var.getName());
					var.setValue(value);
				}
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 得到流程状态
	 * 
	 * @return 流程状态
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * 设置流程状态
	 * 
	 * @param state
	 *            状态
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 得到流程定义
	 */
	public ProcessDef getDefine() {
		if (this.define != null) {
			return this.define;
		}
		// 根据流程定义名称从流程定义管理器中的得到流程定义
		String name = this.getName();
		this.define = ProcessDefManager.getInstance().getProcessDef(name);
		if (this.define == null) {
			throw new RuntimeException("流程实例找不到定义：定义名=" + name);
		}
		return this.define;
	}

	public Set getAllActivities() {
		return this.activities;
	}

	public String toString() {
		return this.name;
	}

	public void setHandUpReason(String reason) {
		this.handUpReason = reason;
	}

	public String getHandUpReason() {
		return this.handUpReason;
	}

	public void add(ActivityInstance ai) {
		this.activities.add(ai);
	}

	public ActivityInstance getFirstActivity() {
		Iterator iter = this.getActivities().iterator();
		while (iter.hasNext()) {
			ActivityInstance act = (ActivityInstance) iter.next();
			if (act.getForeActivityInstance() == null) {
				return act;
			}
		}
		return null;
	}

	public void stop() throws Exception {
		this.setState("结束");
	}

	/**
	 * 挂起活动
	 */
	public void suspend() throws Exception {
		if (this.getState().equals("等待")) {
			this.setState("挂起");
		}
	}

	/**
	 * 重启活动
	 */
	public void resume() throws Exception {
		if (this.getState().equals("挂起")) {
			this.setState("等待");
		}
	}

	public Set getActivities() {
		return activities;
	}

	public void setActivities(Set activities) {
		this.activities = activities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// 计算表达式，变量从流程中取
	public boolean getExpressionValue(String expression) {
		// 用字符串构造
		Program prog = new Program(expression);

		// 解析
		Delegate d = prog.CommaExp().Compile();

		// getParamNames返回所有参数名，把找到的对象用putParam放回
		Set<String> objectNames = d.objectNames.keySet();
		for (String name : objectNames ) {
			// 根据name找到对象，需自己编写
			Object obj = getVarValue(name);
			// 把对象放回objectNames
			d.objectNames.put(name, obj);
		}
		
		Object result = d.invoke();
		return (Boolean)result;
	}
	
	// 根据变量名取变量值
	private String getVarValue(String name) {
		for(VarInstance var : this.vars) {
			if(var.getName().equals(name)) {
				return var.getValue();
			}
		}
		
		throw new RuntimeException("变量不存在: " + name);
	}
}
