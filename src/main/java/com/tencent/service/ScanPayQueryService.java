package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.common.HttpsRequest;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 16:04
 */
public class ScanPayQueryService extends BaseService{

    public ScanPayQueryService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.PAY_QUERY_API);
    }

    /**
     * ����֧����ѯ����
     * @param scanPayQueryReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
    public String request(ScanPayQueryReqData scanPayQueryReqData) throws Exception {

        //--------------------------------------------------------------------
        //����HTTPS��Post����API��ַ
        //--------------------------------------------------------------------
        String responseString = sendPost(scanPayQueryReqData);

        return responseString;
    }


}
