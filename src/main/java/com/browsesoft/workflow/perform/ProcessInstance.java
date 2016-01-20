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
 * ����ʱ������ʵ��
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
	 * ��б�
	 */
	private Set activities = new HashSet();

	/**
	 * �����б�
	 */
	private Set<VarInstance> vars = new HashSet();
	
	/**
	 * ���̶������Ƽ����屾��
	 */
	private String name;

	private ProcessDef define;

	/**
	 * ״̬
	 */
	private String state = "";

	/**
	 * id
	 */
	private String id;

	/**
	 * ����ԭ��
	 */
	private String handUpReason = "��";

	/**
	 * ִ�����б�
	 */
	private List actors = new LinkedList();
	
	public ProcessInstance() {
		super();
	}

	/**
	 * ʹ�����̶��幹��
	 * 
	 * @param define
	 *            ���̶���
	 */
	public ProcessInstance(ProcessDef define,String id) {
		this.id = id;
		String name = define.getName();
		this.setName(name);
		
		//�����������б�����ʼ����null
		initVar();
	}
	
	//�����������б�����ʼ����null
	private void initVar() {
		for(VarDef var : this.getDefine().getVars()) {
			VarInstance varIns = new VarInstance(var.getName(), null);
			this.vars.add(varIns);
		}
	}
	
	//�������еı�����ֵ
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
	 * �õ�����״̬
	 * 
	 * @return ����״̬
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * ��������״̬
	 * 
	 * @param state
	 *            ״̬
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * �õ����̶���
	 */
	public ProcessDef getDefine() {
		if (this.define != null) {
			return this.define;
		}
		// �������̶������ƴ����̶���������еĵõ����̶���
		String name = this.getName();
		this.define = ProcessDefManager.getInstance().getProcessDef(name);
		if (this.define == null) {
			throw new RuntimeException("����ʵ���Ҳ������壺������=" + name);
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
		this.setState("����");
	}

	/**
	 * ����
	 */
	public void suspend() throws Exception {
		if (this.getState().equals("�ȴ�")) {
			this.setState("����");
		}
	}

	/**
	 * �����
	 */
	public void resume() throws Exception {
		if (this.getState().equals("����")) {
			this.setState("�ȴ�");
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
	
	// ������ʽ��������������ȡ
	public boolean getExpressionValue(String expression) {
		// ���ַ�������
		Program prog = new Program(expression);

		// ����
		Delegate d = prog.CommaExp().Compile();

		// getParamNames�������в����������ҵ��Ķ�����putParam�Ż�
		Set<String> objectNames = d.objectNames.keySet();
		for (String name : objectNames ) {
			// ����name�ҵ��������Լ���д
			Object obj = getVarValue(name);
			// �Ѷ���Ż�objectNames
			d.objectNames.put(name, obj);
		}
		
		Object result = d.invoke();
		return (Boolean)result;
	}
	
	// ���ݱ�����ȡ����ֵ
	private String getVarValue(String name) {
		for(VarInstance var : this.vars) {
			if(var.getName().equals(name)) {
				return var.getValue();
			}
		}
		
		throw new RuntimeException("����������: " + name);
	}
}
