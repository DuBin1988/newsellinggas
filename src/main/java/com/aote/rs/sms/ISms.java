package com.aote.rs.sms;

import org.codehaus.jettison.json.JSONObject;

/**
 * ��ʱ�Զ����Ͷ��Žӿ�
 * @author Administrator
 *
 */
public interface ISms {

	/**
	 * ���Ͷ��Žӿڣ�
	 * @param phone �绰���룬����绰�����ö��Ÿ���
	 * @param msg ������Ϣ
	 * @param attr ��������
	 * @return ����json����
	 */
	public JSONObject sendsms(String phone,String msg,JSONObject attr);
}
