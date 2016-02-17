package com.tencent;

import com.tencent.business.DownloadBillBusiness;
import com.tencent.business.RefundBusiness;
import com.tencent.business.RefundQueryBusiness;
import com.tencent.business.ScanPayBusiness;
import com.tencent.common.Configure;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.tencent.protocol.reverse_protocol.ReverseReqData;
import com.tencent.service.*;

/**
 * SDK�����
 */
public class WXPay {

    /**
     * ��ʼ��SDK�����ļ����ؼ�����
     * @param key ǩ���㷨��Ҫ�õ�����Կ
     * @param appID �����˺�ID
     * @param mchID �̻�ID
     * @param sdbMchID ���̻�ID������ģʽ����
     * @param certLocalPath HTTP֤���ڷ������е�·������������֤����
     * @param certPassword HTTP֤������룬Ĭ�ϵ���MCHID
     */
    public static void initSDKConfiguration(String key,String appID,String mchID,String sdbMchID,String certLocalPath,String certPassword){
        Configure.setKey(key);
        Configure.setAppID(appID);
        Configure.setMchID(mchID);
        Configure.setSubMchID(sdbMchID);
        Configure.setCertLocalPath(certLocalPath);
        Configure.setCertPassword(certPassword);
    }

    /**
     * ����֧������
     * @param scanPayReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�����
     * @throws Exception
     */
    public static String requestScanPayService(ScanPayReqData scanPayReqData) throws Exception{
        return new ScanPayService().request(scanPayReqData);
    }

    /**
     * ����֧����ѯ����
     * @param scanPayQueryReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
	public static String requestScanPayQueryService(ScanPayQueryReqData scanPayQueryReqData) throws Exception{
		return new ScanPayQueryService().request(scanPayQueryReqData);
	}

    /**
     * �����˿����
     * @param refundReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
    public static String requestRefundService(RefundReqData refundReqData) throws Exception{
        return new RefundService().request(refundReqData);
    }

    /**
     * �����˿��ѯ����
     * @param refundQueryReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
	public static String requestRefundQueryService(RefundQueryReqData refundQueryReqData) throws Exception{
		return new RefundQueryService().request(refundQueryReqData);
	}

    /**
     * ����������
     * @param reverseReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
	public static String requestReverseService(ReverseReqData reverseReqData) throws Exception{
		return new ReverseService().request(reverseReqData);
	}

    /**
     * ������˵����ط���
     * @param downloadBillReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
    public static String requestDownloadBillService(DownloadBillReqData downloadBillReqData) throws Exception{
        return new DownloadBillService().request(downloadBillReqData);
    }

    /**
     * ֱ��ִ�б�ɨ֧��ҵ���߼����������ʵ�����̣�
     * @param scanPayReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener �̻���Ҫ�Լ�������ɨ֧��ҵ���߼����ܴ����ĸ��ַ�֧�¼��������ú������Ӧ����
     * @throws Exception
     */
    public static void doScanPayBusiness(ScanPayReqData scanPayReqData, ScanPayBusiness.ResultListener resultListener) throws Exception {
        new ScanPayBusiness().run(scanPayReqData, resultListener);
    }

    /**
     * �����˿�ҵ���߼�
     * @param refundReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener ҵ���߼������ߵ��Ľ����֧����Ҫ�̻�����
     * @throws Exception
     */
    public static void doRefundBusiness(RefundReqData refundReqData, RefundBusiness.ResultListener resultListener) throws Exception {
        new RefundBusiness().run(refundReqData,resultListener);
    }

    /**
     * �����˿��ѯ��ҵ���߼�
     * @param refundQueryReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener �̻���Ҫ�Լ�������ɨ֧��ҵ���߼����ܴ����ĸ��ַ�֧�¼��������ú������Ӧ����
     * @throws Exception
     */
    public static void doRefundQueryBusiness(RefundQueryReqData refundQueryReqData,RefundQueryBusiness.ResultListener resultListener) throws Exception {
        new RefundQueryBusiness().run(refundQueryReqData,resultListener);
    }

    /**
     * ������˵����ط���
     * @param downloadBillReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener �̻���Ҫ�Լ�������ɨ֧��ҵ���߼����ܴ����ĸ��ַ�֧�¼��������ú������Ӧ����
     * @return API���ص�XML����
     * @throws Exception
     */
    public static void doDownloadBillBusiness(DownloadBillReqData downloadBillReqData,DownloadBillBusiness.ResultListener resultListener) throws Exception {
        new DownloadBillBusiness().run(downloadBillReqData,resultListener);
    }


}
