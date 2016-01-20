package com.browsesoft.note;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class MSM {
	private String message;

	private String sendUserNum;

	private String receiverUser;

	private String id;

	private String sendUserName;

	private MSM() {
	}

	public MSM(String id, String sendUserName, String sendUserNum,
			String receiverUser, String message) {
		// this.id=Integer.toString(SequenceFind.getID());
		this.id = id;
		this.sendUserName = sendUserName;
		this.sendUserNum = sendUserNum;
		this.receiverUser = receiverUser;
		this.message = message + "ю╢вт:" + sendUserName + " " + sendUserNum;
	}

	public String getID() {
		return this.id;
	}

	public String getMessage() {
		return this.message;
	}

	public String getReceiverUser() {
		return this.receiverUser;
	}

	public String getSendUserName() {
		return this.sendUserName;
	}

	public String getSendUserNum() {
		return this.sendUserNum;
	}

	public void setFail(String message) {

	}
}