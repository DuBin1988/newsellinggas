package com.tencent;

import com.tencent.common.Signature;
import com.tencent.common.Util;

import java.util.Date;

public class Main {

    public static void main(String[] args) {

        try {

            //--------------------------------------------------------------------
            //��ܰ��ʾ����һ��ʹ�ø�SDKʱ�뵽com.tencent.common.Configure�������������
            //--------------------------------------------------------------------



            //--------------------------------------------------------------------
            //PART One:�����������
            //--------------------------------------------------------------------

            //1��https��������Բ���
            //HTTPSPostRquestWithCert.test();

            //2��������Ŀ�õ���XStream���������Ŀ������������Java����ת����XML����Post��API
            //XStreamTest.test();


            //--------------------------------------------------------------------
            //PART Two:�����������
            //--------------------------------------------------------------------

            //1�����Ա�ɨ֧��API
            //PayServiceTest.test();

            //2�����Ա�ɨ������ѯAPI
            //PayQueryServiceTest.test();

            //3�����Գ���API
            //��ܰ��ʾ������֧��API�ɹ��۵�Ǯ֮�󣬿���ͨ������PayQueryServiceTest.test()����֧���ɹ����ص�transaction_id��out_trade_no��������ȥ����ɳ�����������Ǯ�˻��� ^_^v
            //ReverseServiceTest.test();

            //4�������˿�����API
            //RefundServiceTest.test();

            //5�������˿��ѯAPI
            //RefundQueryServiceTest.test();

            //6�����Զ��˵�API
            //DownloadBillServiceTest.test();


            //����ͨ��xml����API����ģ���ʱ���Ȱ����ֶ��޸�xml�����ڵ��ֵ��Ȼ��ͨ�����·���������µ�xml���ݽ���ǩ���õ�һ���Ϸ���ǩ���������⴮ǩ���ŵ����xml�����sign�ֶ����������ģ���ʱ��Ϳ���ͨ��ǩ����֤��
           // Util.log(Signature.getSignFromResponseString(Util.getLocalXMLString("/test/com/tencent/business/refundqueryserviceresponsedata/refundquerysuccess2.xml")));

            //Util.log(new Date().getTime());
            //Util.log(System.currentTimeMillis());

        } catch (Exception e){
            Util.log(e.getMessage());
        }

    }

}
