package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.common.HttpsRequest;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.reverse_protocol.ReverseReqData;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 16:04
 */
public class ReverseService extends BaseService{

    public ReverseService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.REVERSE_API);
    }

    /**
     * ����������
     * @param reverseReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�XML����
     * @throws Exception
     */
    public String request(ReverseReqData reverseReqData) throws Exception {

        //--------------------------------------------------------------------
        //����HTTPS��Post����API��ַ
        //--------------------------------------------------------------------
        String responseString = sendPost(reverseReqData);

        return responseString;
    }

}
