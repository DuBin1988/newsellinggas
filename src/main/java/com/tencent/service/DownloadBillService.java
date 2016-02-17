package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.common.HttpsRequest;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 16:04
 */
public class DownloadBillService extends BaseService{

    public DownloadBillService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.DOWNLOAD_BILL_API);
    }

    //ALL�����ص������ж�����Ϣ��Ĭ��ֵ
    public static final String BILL_TYPE_ALL = "ALL";

    //SUCCESS�����ص��ճɹ�֧���Ķ���
    public static final String BILL_TYPE_SUCCESS = "SUCCESS";

    //REFUND�����ص����˿��
    public static final String BILL_TYPE_REFUND = "REFUND";

    //REVOKED���ѳ����Ķ���
    public static final String BILL_TYPE_REVOKE = "REVOKE";


    /**
     * ������˵����ط���
     * @param downloadBillReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
    public String request(DownloadBillReqData downloadBillReqData) throws Exception {

        //--------------------------------------------------------------------
        //����HTTPS��Post����API��ַ
        //--------------------------------------------------------------------
        String responseString = sendPost(downloadBillReqData);

        return responseString;
    }

}
