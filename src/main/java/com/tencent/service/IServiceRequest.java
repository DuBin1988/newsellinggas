package com.tencent.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * User: rizenguo
 * Date: 2014/12/10
 * Time: 15:16
 * ���ﶨ��������Ҫ��������׼�ӿ�
 */
public interface IServiceRequest {

    //Service�����ĵײ�https����������ʵ����ôһ���ӿ�
    public String sendPost(String api_url,Object xmlObj) throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

}
