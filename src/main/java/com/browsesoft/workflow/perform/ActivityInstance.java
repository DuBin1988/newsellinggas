package com.browsesoft.workflow.perform;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;

/**
 * 活动实例
 */
public class ActivityInstance {
	/**
	 * 活动ID号
	 */
	private String id;

	/**
	 * 活动定义
	 */
	private ActivityDef define = null;

	private String defid;

	/**
	 * 活动定义名称
	 */
	private String defname;

	/**
	 * 执行者表达式
	 */
	private String actorexpression = null;

	/**
	 * 流程实例
	 */
	private ProcessInstance process = null;

	/**
	 * 流程名称
	 */
	private String processName = null;

	/**
	 * 活动状态
	 */
	private String state = "";

	private Date sendTime;

	/**
	 * 发送人id
	 */
	private String senderId = null;

	/**
	 * 发送人名称
	 */
	private String sender = null;

	private Date finishTime;

	/**
	 * 活动执行人id
	 */
	private String userid;

	/**
	 * 活动执行人名称
	 */
	private String person;

	/**
	 * 上一个活动实例
	 */
	private ActivityInstance foreActivityInstance = null;

	/**
	 * 后面的活动实例列表
	 */
	private Set backActivities = new HashSet();

	/**
	 * 默认构造，Hibernate必须
	 * 
	 * @return
	 */
	public ActivityInstance() {

	}

	public String getSender() {
		return sender;
	}

	/**
	 * 采用活动定义、流程实例以及活动参与者ID号构造
	 * 
	 * @param define
	 *            活动定义
	 * @param process
	 *            流程实例
	 * @param person
	 *            活动参与者
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

		// 活动发送人，发送时间
		this.setSender(sender);
		this.setSenderId(sendid);
		this.setSendTime(new Date());

		this.foreActivityInstance = foreActivityIns;
		// 设置活动状态
		state = "开始活动";
		// 添加到流程中
		process.add(this);
	}

	/**
	 * 设置时间
	 * 
	 * @param date
	 *            时间
	 */
	public void setTime(java.util.Date date) {
		// gc.setTime(date);
	}

	/**
	 * 得到后继活动
	 * 
	 * @return 后继活动列表
	 */
	public Set getActivities() {
		return backActivities;
	}

	/**
	 * 添加后继活动
	 * 
	 * @param activity
	 *            后继活动
	 */
	public void add(ActivityInstance activity) {
		if (!this.backActivities.contains(activity)) {
			this.backActivities.add(activity);
		}
	}

	/**
	 * 设置状态
	 * 
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 得到流程实例
	 */
	public ProcessInstance getProcess() {
		return process;
	}

	/**
	 * 得到活动定义
	 * 
	 * @return 活动定义
	 */
	public ActivityDef getDefine() {
		if (this.define != null) {
			return this.define;
		}
		// 如果为空重新装在，根据流程定义ｉｄ得到流程定义
		ProcessDef pDef = this.getProcess().getDefine();
		if (pDef == null) {
			throw new RuntimeException("活动实例找不到流程定义");
		}
		return pDef.getActivity(this.defid);
	}

	/**
	 * 结束活动
	 */
	public synchronized void finish(Session session, String personExpression,
			String username, String userid) throws Exception {
		// 解析传递过来的对象属性
		JSONArray array = new JSONArray(personExpression);
		JSONObject firstObj = array.getJSONObject(0);
		final String data = firstObj.getString("data");
		JSONObject dataObj = new JSONObject(data);

		// 把前台传过来的，活动对应的人员，转换成json串
		state = "结束";
		// 设置完成人，完成时间
		this.setPerson(username);
		this.setUserid(userid);
		this.setFinishTime(new Date());
		// 根据每个转移创建任务和活动
		ProcessDef procDef = ProcessDefManager.getInstance().getProcessDef(
				processName);
		ActivityDef actDef = procDef.getActivity(defid);
		for (DiversionDef divDef : actDef.getSplits()) {
			ActivityDef tailDef = divDef.getTail();
			
			// 给流程中的变量赋值
			this.process.putVar(dataObj);
			
			// 如果不满足表达式条件，继续下一个
			String expression = divDef.getExpression();
			if(expression != null && !expression.equals("") && 
					!this.process.getExpressionValue(expression)) {
				continue;
			}
			
			// 根据人员表达式，产生人员对照表
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
	 * 得到活动名称
	 */
	public String getName() {
		return this.getDefine().getName();
	}

	/**
	 * 得到活动所属流程名称
	 */
	public String getProcessName() {
		return this.getDefine().getProcess().getName();
	}

	public String getState() {
		return state;
	}

	/**
	 * 得到标准的时间格式，即每年是4位的，月， 日，时，分，秒都是2位的
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
	 * 挂起活动
	 */
	public void suspend() throws Exception {
		if (this.getState().equals("开始活动")) {
			this.setState("挂起");
		}
	}

	/**
	 * 重启活动
	 */
	public void resume() throws Exception {
		if (this.getState().equals("挂起")) {
			this.setState("开始活动");
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