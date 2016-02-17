package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.common.HttpsRequest;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 16:04
 */
public class RefundQueryService extends BaseService{

    public RefundQueryService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.REFUND_QUERY_API);
    }

    /**
     * �����˿��ѯ����
     * @param refundQueryReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
    public String request(RefundQueryReqData refundQueryReqData) throws Exception {

        //--------------------------------------------------------------------
        //����HTTPS��Post����API��ַ
        //--------------------------------------------------------------------
        String responseString = sendPost(refundQueryReqData);

        return responseString;
    }




}
