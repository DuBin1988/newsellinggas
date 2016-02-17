package com.tencent.business;

import com.tencent.common.Configure;
import com.tencent.common.Log;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.common.report.ReporterFactory;
import com.tencent.common.report.protocol.ReportReqData;
import com.tencent.common.report.service.ReportService;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.ScanPayResData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.protocol.reverse_protocol.ReverseReqData;
import com.tencent.protocol.reverse_protocol.ReverseResData;
import com.tencent.service.ReverseService;
import com.tencent.service.ScanPayQueryService;
import com.tencent.service.ScanPayService;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * User: rizenguo
 * Date: 2014/12/1
 * Time: 17:05
 */
public class ScanPayBusiness {

    public ScanPayBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        scanPayService = new ScanPayService();
        scanPayQueryService = new ScanPayQueryService();
        reverseService = new ReverseService();
    }

    public interface ResultListener {

        //API����ReturnCode���Ϸ���֧�������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������
        void onFailByReturnCodeError(ScanPayResData scanPayResData);

        //API����ReturnCodeΪFAIL��֧��APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�
        void onFailByReturnCodeFail(ScanPayResData scanPayResData);

        //֧������API���ص�����ǩ����֤ʧ�ܣ��п������ݱ��۸���
        void onFailBySignInvalid(ScanPayResData scanPayResData);


        //�û�����֧���Ķ�ά���Ѿ����ڣ���ʾ����Ա����ɨһ���û�΢�š�ˢ��������Ķ�ά��
        void onFailByAuthCodeExpire(ScanPayResData scanPayResData);

        //��Ȩ����Ч����ʾ�û�ˢ��һά��/��ά�룬֮������ɨ��֧��"
        void onFailByAuthCodeInvalid(ScanPayResData scanPayResData);

        //�û����㣬��������֧���������ֽ�֧��
        void onFailByMoneyNotEnough(ScanPayResData scanPayResData);

        //֧��ʧ��
        void onFail(ScanPayResData scanPayResData);

        //֧���ɹ�
        void onSuccess(ScanPayResData scanPayResData);

    }

    //��log��
    private static Log log = new Log(LoggerFactory.getLogger(ScanPayBusiness.class));

    //ÿ�ε��ö�����ѯAPIʱ�ĵȴ�ʱ�䣬��Ϊ������֧��ʧ�ܵ�ʱ��������Ϸ����ѯ��һ�����ܲ鵽������������ｨ���ȵȴ�һ��ʱ���ٷ����ѯ

    private int waitingTimeBeforePayQueryServiceInvoked = 5000;

    //ѭ�����ö�����ѯAPI�Ĵ���
    private int payQueryLoopInvokedCount = 3;

    //ÿ�ε��ó���API�ĵȴ�ʱ��
    private int waitingTimeBeforeReverseServiceInvoked = 5000;

    private ScanPayService scanPayService;

    private ScanPayQueryService scanPayQueryService;

    private ReverseService reverseService;

    /**
     * ֱ��ִ�б�ɨ֧��ҵ���߼����������ʵ�����̣�
     *
     * @param scanPayReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener �̻���Ҫ�Լ�������ɨ֧��ҵ���߼����ܴ����ĸ��ַ�֧�¼��������ú������Ӧ����
     * @throws Exception
     */
    public void run(ScanPayReqData scanPayReqData, ResultListener resultListener) throws Exception {

        //--------------------------------------------------------------------
        //�������󡰱�ɨ֧��API������Ҫ�ύ������
        //--------------------------------------------------------------------

        String outTradeNo = scanPayReqData.getOut_trade_no();

        //����API����
        String payServiceResponseString;

        long costTimeStart = System.currentTimeMillis();


        log.i("֧��API���ص��������£�");
        payServiceResponseString = scanPayService.request(scanPayReqData);

        long costTimeEnd = System.currentTimeMillis();
        long totalTimeCost = costTimeEnd - costTimeStart;
        log.i("api�����ܺ�ʱ��" + totalTimeCost + "ms");

        //��ӡ�ذ�����
        log.i(payServiceResponseString);

        //����API���ص�XML����ӳ�䵽Java����
        ScanPayResData scanPayResData = (ScanPayResData) Util.getObjectFromXML(payServiceResponseString, ScanPayResData.class);

        //�첽����ͳ������
        //*

        ReportReqData reportReqData = new ReportReqData(
                scanPayReqData.getDevice_info(),
                Configure.PAY_API,
                (int) (totalTimeCost),//���������ʱ
                scanPayResData.getReturn_code(),
                scanPayResData.getReturn_msg(),
                scanPayResData.getResult_code(),
                scanPayResData.getErr_code(),
                scanPayResData.getErr_code_des(),
                scanPayResData.getOut_trade_no(),
                scanPayReqData.getSpbill_create_ip()
        );
        long timeAfterReport;
        if (Configure.isUseThreadToDoReport()) {
            ReporterFactory.getReporter(reportReqData).run();
            timeAfterReport = System.currentTimeMillis();
            log.i("pay+report�ܺ�ʱ���첽��ʽ�ϱ�����" + (timeAfterReport - costTimeStart) + "ms");
        } else {
            ReportService.request(reportReqData);
            timeAfterReport = System.currentTimeMillis();
            log.i("pay+report�ܺ�ʱ��ͬ����ʽ�ϱ�����" + (timeAfterReport - costTimeStart) + "ms");
        }

        if (scanPayResData == null || scanPayResData.getReturn_code() == null) {
            log.e("��֧��ʧ�ܡ�֧�������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������");
            resultListener.onFailByReturnCodeError(scanPayResData);
            return;
        }

        if (scanPayResData.getReturn_code().equals("FAIL")) {
            //ע�⣺һ�����ﷵ��FAIL�ǳ���ϵͳ��������������Post��API�������Ƿ�淶�Ϸ�
            log.e("��֧��ʧ�ܡ�֧��APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�");
            resultListener.onFailByReturnCodeFail(scanPayResData);
            return;
        } else {
            log.i("֧��APIϵͳ�ɹ���������");
            //--------------------------------------------------------------------
            //�յ�API�ķ������ݵ�ʱ�������֤һ��������û�б��������۸ģ�ȷ����ȫ
            //--------------------------------------------------------------------
            if (!Signature.checkIsSignValidFromResponseString(payServiceResponseString)) {
                log.e("��֧��ʧ�ܡ�֧������API���ص�����ǩ����֤ʧ�ܣ��п������ݱ��۸���");
                resultListener.onFailBySignInvalid(scanPayResData);
                return;
            }

            //��ȡ������
            String errorCode = scanPayResData.getErr_code();
            //��ȡ��������
            String errorCodeDes = scanPayResData.getErr_code_des();

            if (scanPayResData.getResult_code().equals("SUCCESS")) {

                //--------------------------------------------------------------------
                //1)ֱ�ӿۿ�ɹ�
                //--------------------------------------------------------------------

                log.i("��һ����֧���ɹ���");
                resultListener.onSuccess(scanPayResData);
            }else{

                //����ҵ�����
                log.i("ҵ�񷵻�ʧ��");
                log.i("err_code:" + errorCode);
                log.i("err_code_des:" + errorCodeDes);

                //ҵ�����ʱ�������кü��֣��̻��ص���ʾ���¼���
                if (errorCode.equals("AUTHCODEEXPIRE") || errorCode.equals("AUTH_CODE_INVALID") || errorCode.equals("NOTENOUGH")) {

                    //--------------------------------------------------------------------
                    //2)�ۿ���ȷʧ��
                    //--------------------------------------------------------------------

                    //���ڿۿ���ȷʧ�ܵ����ֱ���߳����߼�
                    doReverseLoop(outTradeNo);

                    //���¼������������ȷ��ʾ�û���ָ���������Ĺ���
                    if (errorCode.equals("AUTHCODEEXPIRE")) {
                        //��ʾ�û�����֧���Ķ�ά���Ѿ����ڣ���ʾ����Ա����ɨһ���û�΢�š�ˢ��������Ķ�ά��
                        log.w("��֧���ۿ���ȷʧ�ܡ�ԭ���ǣ�" + errorCodeDes);
                        resultListener.onFailByAuthCodeExpire(scanPayResData);
                    } else if (errorCode.equals("AUTH_CODE_INVALID")) {
                        //��Ȩ����Ч����ʾ�û�ˢ��һά��/��ά�룬֮������ɨ��֧��
                        log.w("��֧���ۿ���ȷʧ�ܡ�ԭ���ǣ�" + errorCodeDes);
                        resultListener.onFailByAuthCodeInvalid(scanPayResData);
                    } else if (errorCode.equals("NOTENOUGH")) {
                        //��ʾ�û����㣬��������֧���������ֽ�֧��
                        log.w("��֧���ۿ���ȷʧ�ܡ�ԭ���ǣ�" + errorCodeDes);
                        resultListener.onFailByMoneyNotEnough(scanPayResData);
                    }
                } else if (errorCode.equals("USERPAYING")) {

                    //--------------------------------------------------------------------
                    //3)��Ҫ��������
                    //--------------------------------------------------------------------

                    //��ʾ�п��ܵ������ѳ���300Ԫ�����������������Ѵ����Ѿ����������������ƣ����ʱ����ʾ�û��������룬�̻��Լ���һ��ʱ��ȥ�鵥����ѯһ�����������û��Ƿ��Ѿ�����������
                    if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo)) {
                        log.i("����Ҫ�û��������롢��ѯ��֧���ɹ���");
                        resultListener.onSuccess(scanPayResData);
                    } else {
                        log.i("����Ҫ�û��������롢��һ��ʱ����û�в�ѯ��֧���ɹ����߳������̡�");
                        doReverseLoop(outTradeNo);
                        resultListener.onFail(scanPayResData);
                    }
                } else {

                    //--------------------------------------------------------------------
                    //4)�ۿ�δ֪ʧ��
                    //--------------------------------------------------------------------

                    if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo)) {
                        log.i("��֧���ۿ�δ֪ʧ�ܡ���ѯ��֧���ɹ���");
                        resultListener.onSuccess(scanPayResData);
                    } else {
                        log.i("��֧���ۿ�δ֪ʧ�ܡ���һ��ʱ����û�в�ѯ��֧���ɹ����߳������̡�");
                        doReverseLoop(outTradeNo);
                        resultListener.onFail(scanPayResData);
                    }
                }
            }
        }
    }

    /**
     * ����һ��֧��������ѯ����
     *
     * @param outTradeNo    �̻�ϵͳ�ڲ��Ķ�����,32���ַ��ڿɰ�����ĸ, [ȷ�����̻�ϵͳΨһ]
     * @return �ö����Ƿ�֧���ɹ�
     * @throws Exception
     */
    private boolean doOnePayQuery(String outTradeNo) throws Exception {

        sleep(waitingTimeBeforePayQueryServiceInvoked);//�ȴ�һ��ʱ���ٽ��в�ѯ������״̬��û���ü�������

        String payQueryServiceResponseString;

        ScanPayQueryReqData scanPayQueryReqData = new ScanPayQueryReqData("",outTradeNo);
        payQueryServiceResponseString = scanPayQueryService.request(scanPayQueryReqData);

        log.i("֧��������ѯAPI���ص��������£�");
        log.i(payQueryServiceResponseString);

        //����API���ص�XML����ӳ�䵽Java����
        ScanPayQueryResData scanPayQueryResData = (ScanPayQueryResData) Util.getObjectFromXML(payQueryServiceResponseString, ScanPayQueryResData.class);
        if (scanPayQueryResData == null || scanPayQueryResData.getReturn_code() == null) {
            log.i("֧��������ѯ�����߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ�");
            return false;
        }

        if (scanPayQueryResData.getReturn_code().equals("FAIL")) {
            //ע�⣺һ�����ﷵ��FAIL�ǳ���ϵͳ��������������Post��API�������Ƿ�淶�Ϸ�
            log.i("֧��������ѯAPIϵͳ����ʧ�ܣ�ʧ����ϢΪ��" + scanPayQueryResData.getReturn_msg());
            return false;
        } else {
            if (scanPayQueryResData.getResult_code().equals("SUCCESS")) {//ҵ���ɹ�
                if (scanPayQueryResData.getTrade_state().equals("SUCCESS")) {
                    //��ʾ�鵥���Ϊ��֧���ɹ���
                    log.i("��ѯ������֧���ɹ�");
                    return true;
                } else {
                    //֧�����ɹ�
                    log.i("��ѯ������֧�����ɹ�");
                    return false;
                }
            } else {
                log.i("��ѯ���������룺" + scanPayQueryResData.getErr_code() + "     ������Ϣ��" + scanPayQueryResData.getErr_code_des());
                return false;
            }
        }
    }

    /**
     * �����е�ʱ������Ϊ������ʱ��������Ҫ�̻�ÿ��һ��ʱ�䣨����5�룩���ٽ��в�ѯ���������Լ��Σ�����3�Σ�
     *
     * @param loopCount     ѭ������������һ��
     * @param outTradeNo    �̻�ϵͳ�ڲ��Ķ�����,32���ַ��ڿɰ�����ĸ, [ȷ�����̻�ϵͳΨһ]
     * @return �ö����Ƿ�֧���ɹ�
     * @throws InterruptedException
     */
    private boolean doPayQueryLoop(int loopCount, String outTradeNo) throws Exception {
        //���ٲ�ѯһ��
        if (loopCount == 0) {
            loopCount = 1;
        }
        //����ѭ����ѯ
        for (int i = 0; i < loopCount; i++) {
            if (doOnePayQuery(outTradeNo)) {
                return true;
            }
        }
        return false;
    }

    //�Ƿ���Ҫ�ٵ�һ�γ��������ֵ�ɳ���API�ذ���recall�ֶξ���
    private boolean needRecallReverse = false;

    /**
     * ����һ�γ�������
     *
     * @param outTradeNo    �̻�ϵͳ�ڲ��Ķ�����,32���ַ��ڿɰ�����ĸ, [ȷ�����̻�ϵͳΨһ]
     * @return �ö����Ƿ�֧���ɹ�
     * @throws Exception
     */
    private boolean doOneReverse(String outTradeNo) throws Exception {

        sleep(waitingTimeBeforeReverseServiceInvoked);//�ȴ�һ��ʱ���ٽ��в�ѯ������״̬��û���ü�������

        String reverseResponseString;

        ReverseReqData reverseReqData = new ReverseReqData("",outTradeNo);
        reverseResponseString = reverseService.request(reverseReqData);

        log.i("����API���ص��������£�");
        log.i(reverseResponseString);
        //����API���ص�XML����ӳ�䵽Java����
        ReverseResData reverseResData = (ReverseResData) Util.getObjectFromXML(reverseResponseString, ReverseResData.class);
        if (reverseResData == null) {
            log.i("֧���������������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ�");
            return false;
        }
        if (reverseResData.getReturn_code().equals("FAIL")) {
            //ע�⣺һ�����ﷵ��FAIL�ǳ���ϵͳ��������������Post��API�������Ƿ�淶�Ϸ�
            log.i("֧����������APIϵͳ����ʧ�ܣ�ʧ����ϢΪ��" + reverseResData.getReturn_msg());
            return false;
        } else {
            if (reverseResData.getResult_code().equals("FAIL")) {
                log.i("�������������룺" + reverseResData.getErr_code() + "     ������Ϣ��" + reverseResData.getErr_code_des());
                if (reverseResData.getRecall().equals("Y")) {
                    //��ʾ��Ҫ����
                    needRecallReverse = true;
                    return false;
                } else {
                    //��ʾ����Ҫ���ԣ�Ҳ���Ե����ǳ����ɹ�
                    needRecallReverse = false;
                    return true;
                }
            } else {
                //��ѯ�ɹ�����ӡ����״̬
                log.i("֧�����������ɹ�");
                return true;
            }
        }
    }


    /**
     * �����е�ʱ������Ϊ������ʱ��������Ҫ�̻�ÿ��һ��ʱ�䣨����5�룩���ٽ��в�ѯ�������Ƿ���Ҫ����ѭ�����ó���API�ɳ���API�ذ������recall�ֶξ�����
     *
     * @param outTradeNo    �̻�ϵͳ�ڲ��Ķ�����,32���ַ��ڿɰ�����ĸ, [ȷ�����̻�ϵͳΨһ]
     * @throws InterruptedException
     */
    private void doReverseLoop(String outTradeNo) throws Exception {
        //��ʼ��������
        needRecallReverse = true;
        //����ѭ��������ֱ�������ɹ�������API����recall�ֶ�Ϊ"Y"
        while (needRecallReverse) {
            if (doOneReverse(outTradeNo)) {
                return;
            }
        }
    }

    /**
     * ����ѭ����ε��ö�����ѯAPI��ʱ����
     *
     * @param duration ʱ������Ĭ��Ϊ10��
     */
    public void setWaitingTimeBeforePayQueryServiceInvoked(int duration) {
        waitingTimeBeforePayQueryServiceInvoked = duration;
    }

    /**
     * ����ѭ����ε��ö�����ѯAPI�Ĵ���
     *
     * @param count ���ô�����Ĭ��Ϊ����
     */
    public void setPayQueryLoopInvokedCount(int count) {
        payQueryLoopInvokedCount = count;
    }

    /**
     * ����ѭ����ε��ó���API��ʱ����
     *
     * @param duration ʱ������Ĭ��Ϊ5��
     */
    public void setWaitingTimeBeforeReverseServiceInvoked(int duration) {
        waitingTimeBeforeReverseServiceInvoked = duration;
    }

    public void setScanPayService(ScanPayService service) {
        scanPayService = service;
    }

    public void setScanPayQueryService(ScanPayQueryService service) {
        scanPayQueryService = service;
    }

    public void setReverseService(ReverseService service) {
        reverseService = service;
    }

}
