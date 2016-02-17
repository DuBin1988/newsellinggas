package com.tencent.business;

import com.tencent.WXPay;
import com.tencent.common.Configure;
import com.tencent.common.Log;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.common.report.ReporterFactory;
import com.tencent.common.report.protocol.ReportReqData;
import com.tencent.common.report.service.ReportService;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.tencent.service.RefundService;
import org.slf4j.LoggerFactory;

/**
 * User: rizenguo
 * Date: 2014/12/2
 * Time: 17:51
 */
public class RefundBusiness {

    public RefundBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        refundService = new RefundService();
    }

    public interface ResultListener{
        //API����ReturnCode���Ϸ���֧�������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������
        void onFailByReturnCodeError(RefundResData refundResData);

        //API����ReturnCodeΪFAIL��֧��APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�
        void onFailByReturnCodeFail(RefundResData refundResData);

        //֧������API���ص�����ǩ����֤ʧ�ܣ��п������ݱ��۸���
        void onFailBySignInvalid(RefundResData refundResData);

        //�˿�ʧ��
        void onRefundFail(RefundResData refundResData);

        //�˿�ɹ�
        void onRefundSuccess(RefundResData refundResData);

    }

    //��log��
    private static Log log = new Log(LoggerFactory.getLogger(RefundBusiness.class));

    //ִ�н��
    private static String result = "";

    private RefundService refundService;

    /**
     * �����˿�ҵ���߼�
     * @param refundReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener ҵ���߼������ߵ��Ľ����֧����Ҫ�̻�����
     * @throws Exception
     */
    public void run(RefundReqData refundReqData,ResultListener resultListener) throws Exception {

        //--------------------------------------------------------------------
        //���������˿�API������Ҫ�ύ������
        //--------------------------------------------------------------------

        //API���ص�����
        String refundServiceResponseString;

        long costTimeStart = System.currentTimeMillis();


        log.i("�˿��ѯAPI���ص��������£�");
        refundServiceResponseString = refundService.request(refundReqData);


        long costTimeEnd = System.currentTimeMillis();
        long totalTimeCost = costTimeEnd - costTimeStart;
        log.i("api�����ܺ�ʱ��" + totalTimeCost + "ms");

        log.i(refundServiceResponseString);

        //����API���ص�XML����ӳ�䵽Java����
        RefundResData refundResData = (RefundResData) Util.getObjectFromXML(refundServiceResponseString, RefundResData.class);


        ReportReqData reportReqData = new ReportReqData(
                refundResData.getDevice_info(),
                Configure.REFUND_API,
                (int) (totalTimeCost),//���������ʱ
                refundResData.getReturn_code(),
                refundResData.getReturn_msg(),
                refundResData.getResult_code(),
                refundResData.getErr_code(),
                refundResData.getErr_code_des(),
                refundResData.getOut_trade_no(),
                Configure.getIP()
        );

        long timeAfterReport;
        if(Configure.isUseThreadToDoReport()){
            ReporterFactory.getReporter(reportReqData).run();
            timeAfterReport = System.currentTimeMillis();
            Util.log("pay+report�ܺ�ʱ���첽��ʽ�ϱ�����"+(timeAfterReport-costTimeStart) + "ms");
        }else{
            ReportService.request(reportReqData);
            timeAfterReport = System.currentTimeMillis();
            Util.log("pay+report�ܺ�ʱ��ͬ����ʽ�ϱ�����"+(timeAfterReport-costTimeStart) + "ms");
        }

        if (refundResData == null || refundResData.getReturn_code() == null) {
            setResult("Case1:�˿�API�����߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������",Log.LOG_TYPE_ERROR);
            resultListener.onFailByReturnCodeError(refundResData);
            return;
        }

        //Debug:�鿴�����Ƿ���������䵽scanPayResponseData���������
        //Util.reflect(refundResData);

        if (refundResData.getReturn_code().equals("FAIL")) {
            ///ע�⣺һ�����ﷵ��FAIL�ǳ���ϵͳ��������������Post��API�������Ƿ�淶�Ϸ�
            setResult("Case2:�˿�APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�",Log.LOG_TYPE_ERROR);
            resultListener.onFailByReturnCodeFail(refundResData);
        } else {
            log.i("�˿�APIϵͳ�ɹ���������");
            //--------------------------------------------------------------------
            //�յ�API�ķ������ݵ�ʱ�������֤һ��������û�б��������۸ģ�ȷ����ȫ
            //--------------------------------------------------------------------

            if (!Signature.checkIsSignValidFromResponseString(refundServiceResponseString)) {
                setResult("Case3:�˿�����API���ص�����ǩ����֤ʧ�ܣ��п������ݱ��۸���",Log.LOG_TYPE_ERROR);
                resultListener.onFailBySignInvalid(refundResData);
                return;
            }

            if (refundResData.getResult_code().equals("FAIL")) {
                log.i("���������룺" + refundResData.getErr_code() + "     ������Ϣ��" + refundResData.getErr_code_des());
                setResult("Case4:���˿�ʧ�ܡ�",Log.LOG_TYPE_ERROR);
                //�˿�ʧ��ʱ����ô��ʱ��ѯ�˿�״̬��û�����壬���ʱ�佨��Ҫô���ֶ�����һ�Σ���Ȼʧ�ܵĻ�����Ͷ����������Ͷ��
                resultListener.onRefundFail(refundResData);
            } else {
                //�˿�ɹ�
                setResult("Case5:���˿�ɹ���",Log.LOG_TYPE_INFO);
                resultListener.onRefundSuccess(refundResData);
            }
        }
    }

    public void setRefundService(RefundService service) {
        refundService = service;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        RefundBusiness.result = result;
    }

    public void setResult(String result,String type){
        setResult(result);
        log.log(type,result);
    }
}
