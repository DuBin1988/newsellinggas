package com.tencent.common;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:40
 * ������ø�����������
 */
public class Configure {
//��������Լ�Ҫ���ܺõ�˽��Key�ˣ��м�ֻ�ܷ����Լ��ĺ�̨��������ܷ����κο��ܱ�����Դ����Ŀͻ��˳����У�
	// ÿ���Լ�Post���ݸ�API��ʱ��Ҫ�����key���������ֶν���ǩ�������ɵ�ǩ�������Sign����ֶΣ�API�յ�Post���ݵ�ʱ��Ҳ����ͬ����ǩ���㷨��Post���������ݽ���ǩ������֤
	// �յ�API�ķ��ص�ʱ��ҲҪ�����key���Է��ص���������ǩ������API��Sign���ݽ��бȽϣ����ֵ��һ�£��п������ݱ����������۸�

	private static String key = "";

	//΢�ŷ���Ĺ��ں�ID����ͨ���ں�֮����Ի�ȡ����
	private static String appID = "";

	//΢��֧��������̻���ID����ͨ���ںŵ�΢��֧������֮����Ի�ȡ����
	private static String mchID = "";

	//����ģʽ�¸����̻���������̻���
	private static String subMchID = "";

	//HTTPS֤��ı���·��
	private static String certLocalPath = "";

	//HTTPS֤�����룬Ĭ����������̻���MCHID
	private static String certPassword = "";

	//�Ƿ�ʹ���첽�̵߳ķ�ʽ���ϱ�API���٣�Ĭ��Ϊ�첽ģʽ
	private static boolean useThreadToDoReport = true;

	//����IP
	private static String ip = "";

	//�����Ǽ���API��·����
	//1����ɨ֧��API
	public static String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

	//2����ɨ֧����ѯAPI
	public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	//3���˿�API
	public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	//4���˿��ѯAPI
	public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	//5������API
	public static String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

	//6�����ض��˵�API
	public static String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

	//7) ͳ���ϱ�API
	public static String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

	public static boolean isUseThreadToDoReport() {
		return useThreadToDoReport;
	}

	public static void setUseThreadToDoReport(boolean useThreadToDoReport) {
		Configure.useThreadToDoReport = useThreadToDoReport;
	}

	public static String HttpsRequestClassName = "com.tencent.common.HttpsRequest";

	public static void setKey(String key) {
		Configure.key = key;
	}

	public static void setAppID(String appID) {
		Configure.appID = appID;
	}

	public static void setMchID(String mchID) {
		Configure.mchID = mchID;
	}

	public static void setSubMchID(String subMchID) {
		Configure.subMchID = subMchID;
	}

	public static void setCertLocalPath(String certLocalPath) {
		Configure.certLocalPath = certLocalPath;
	}

	public static void setCertPassword(String certPassword) {
		Configure.certPassword = certPassword;
	}

	public static void setIp(String ip) {
		Configure.ip = ip;
	}

	public static String getKey(){
		return key;
	}
	
	public static String getAppid(){
		return appID;
	}
	
	public static String getMchid(){
		return mchID;
	}

	public static String getSubMchid(){
		return subMchID;
	}
	
	public static String getCertLocalPath(){
		return certLocalPath;
	}
	
	public static String getCertPassword(){
		return certPassword;
	}

	public static String getIP(){
		return ip;
	}

	public static void setHttpsRequestClassName(String name){
		HttpsRequestClassName = name;
	}

}
