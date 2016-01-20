package com.browsesoft.workflow.perform;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;

/**
 * �ʵ��
 */
public class ActivityInstance {
	/**
	 * �ID��
	 */
	private String id;

	/**
	 * �����
	 */
	private ActivityDef define = null;

	private String defid;

	/**
	 * ���������
	 */
	private String defname;

	/**
	 * ִ���߱��ʽ
	 */
	private String actorexpression = null;

	/**
	 * ����ʵ��
	 */
	private ProcessInstance process = null;

	/**
	 * ��������
	 */
	private String processName = null;

	/**
	 * �״̬
	 */
	private String state = "";

	private Date sendTime;

	/**
	 * ������id
	 */
	private String senderId = null;

	/**
	 * ����������
	 */
	private String sender = null;

	private Date finishTime;

	/**
	 * �ִ����id
	 */
	private String userid;

	/**
	 * �ִ��������
	 */
	private String person;

	/**
	 * ��һ���ʵ��
	 */
	private ActivityInstance foreActivityInstance = null;

	/**
	 * ����Ļʵ���б�
	 */
	private Set backActivities = new HashSet();

	/**
	 * Ĭ�Ϲ��죬Hibernate����
	 * 
	 * @return
	 */
	public ActivityInstance() {

	}

	public String getSender() {
		return sender;
	}

	/**
	 * ���û���塢����ʵ���Լ��������ID�Ź���
	 * 
	 * @param define
	 *            �����
	 * @param process
	 *            ����ʵ��
	 * @param person
	 *            �������
	 */
	public ActivityInstance(ActivityDef define, ProcessInstance process,
			String actorExpression, String sender, String sendid,
			ActivityInstance foreActivityIns) throws Exception {
		this.define = define;
		this.defid = define.getID();
		this.process = process;
		this.actorexpression = actorExpression;

		this.processName = this.process.getName();
		this.defname = this.define.getName();

		// ������ˣ�����ʱ��
		this.setSender(sender);
		this.setSenderId(sendid);
		this.setSendTime(new Date());

		this.foreActivityInstance = foreActivityIns;
		// ���û״̬
		state = "��ʼ�";
		// ��ӵ�������
		process.add(this);
	}

	/**
	 * ����ʱ��
	 * 
	 * @param date
	 *            ʱ��
	 */
	public void setTime(java.util.Date date) {
		// gc.setTime(date);
	}

	/**
	 * �õ���̻
	 * 
	 * @return ��̻�б�
	 */
	public Set getActivities() {
		return backActivities;
	}

	/**
	 * ��Ӻ�̻
	 * 
	 * @param activity
	 *            ��̻
	 */
	public void add(ActivityInstance activity) {
		if (!this.backActivities.contains(activity)) {
			this.backActivities.add(activity);
		}
	}

	/**
	 * ����״̬
	 * 
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * �õ�����ʵ��
	 */
	public ProcessInstance getProcess() {
		return process;
	}

	/**
	 * �õ������
	 * 
	 * @return �����
	 */
	public ActivityDef getDefine() {
		if (this.define != null) {
			return this.define;
		}
		// ���Ϊ������װ�ڣ��������̶�����õ����̶���
		ProcessDef pDef = this.getProcess().getDefine();
		if (pDef == null) {
			throw new RuntimeException("�ʵ���Ҳ������̶���");
		}
		return pDef.getActivity(this.defid);
	}

	/**
	 * �����
	 */
	public synchronized void finish(Session session, String personExpression,
			String username, String userid) throws Exception {
		// �������ݹ����Ķ�������
		JSONArray array = new JSONArray(personExpression);
		JSONObject firstObj = array.getJSONObject(0);
		final String data = firstObj.getString("data");
		JSONObject dataObj = new JSONObject(data);

		// ��ǰ̨�������ģ����Ӧ����Ա��ת����json��
		state = "����";
		// ��������ˣ����ʱ��
		this.setPerson(username);
		this.setUserid(userid);
		this.setFinishTime(new Date());
		// ����ÿ��ת�ƴ�������ͻ
		ProcessDef procDef = ProcessDefManager.getInstance().getProcessDef(
				processName);
		ActivityDef actDef = procDef.getActivity(defid);
		for (DiversionDef divDef : actDef.getSplits()) {
			ActivityDef tailDef = divDef.getTail();
			
			// �������еı�����ֵ
			this.process.putVar(dataObj);
			
			// �����������ʽ������������һ��
			String expression = divDef.getExpression();
			if(expression != null && !expression.equals("") && 
					!this.process.getExpressionValue(expression)) {
				continue;
			}
			
			// ������Ա���ʽ��������Ա���ձ�
			String exp = tailDef.getPersonExpression();
			if (dataObj.has(tailDef.getName())) {
				String pExp = dataObj.getString(tailDef.getName());
				exp = pExp;
			}
			PersonService.Run(exp, session);
			
			ActivityInstance actIns = new ActivityInstance(tailDef, process,
					exp, username, userid, this);
			
			session.save(actIns);
		}
		session.update(this);
	}
	
	/**
	 * �õ������
	 */
	public String getName() {
		return this.getDefine().getName();
	}

	/**
	 * �õ��������������
	 */
	public String getProcessName() {
		return this.getDefine().getProcess().getName();
	}

	public String getState() {
		return state;
	}

	/**
	 * �õ���׼��ʱ���ʽ����ÿ����4λ�ģ��£� �գ�ʱ���֣��붼��2λ��
	 * 
	 * @param time
	 * @return
	 */
	public String getFormatTime(String time) {
		String result = "";
		if (time.length() < 2) {
			result = "0" + time;
		} else {
			result = time;
		}
		return result;
	}

	/**
	 * ����
	 */
	public void suspend() throws Exception {
		if (this.getState().equals("��ʼ�")) {
			this.setState("����");
		}
	}

	/**
	 * �����
	 */
	public void resume() throws Exception {
		if (this.getState().equals("����")) {
			this.setState("��ʼ�");
		}
	}

	public void setStateForSynchron(String state) {
		this.state = state;
	}

	public ActivityInstance getForeActivityInstance() {
		return this.foreActivityInstance;
	}

	public void setForeActivityInstance(ActivityInstance foreActivityInstance) {
		this.foreActivityInstance = foreActivityInstance;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set getBackActivities() {
		return backActivities;
	}

	public void setBackActivities(Set backActivities) {
		this.backActivities = backActivities;
	}

	public String getDefid() {
		return defid;
	}

	public String getDefname() {
		return defname;
	}

	public void setDefname(String defname) {
		this.defname = defname;
	}

	public String getActorexpression() {
		return actorexpression;
	}

	public void setActorexpression(String actorexpression) {
		this.actorexpression = actorexpression;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public void setDefine(ActivityDef define) {
		this.define = define;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setDefid(String defid) {
		this.defid = defid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setProcess(ProcessInstance process) {
		this.process = process;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String sendId) {
		this.senderId = sendId;
	}
}