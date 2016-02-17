package com.tencent.service;

import com.tencent.common.*;
import com.tencent.protocol.pay_protocol.ScanPayReqData;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 16:03
 */
public class ScanPayService extends BaseService{

    public ScanPayService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.PAY_API);
    }

    /**
     * ����֧������
     * @param scanPayReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�����
     * @throws Exception
     */
    public String request(ScanPayReqData scanPayReqData) throws Exception {

        //--------------------------------------------------------------------
        //����HTTPS��Post����API��ַ
        //--------------------------------------------------------------------
        String responseString = sendPost(scanPayReqData);

        return responseString;
    }
}
