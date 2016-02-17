package com.tencent.business;

import com.tencent.WXPay;
import com.tencent.common.*;
import com.tencent.common.report.ReporterFactory;
import com.tencent.common.report.protocol.ReportReqData;
import com.tencent.common.report.service.ReportService;
import com.tencent.protocol.refund_query_protocol.RefundOrderData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryResData;
import com.tencent.service.RefundQueryService;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * User: rizenguo
 * Date: 2014/12/2
 * Time: 18:51
 */
public class RefundQueryBusiness {

    public RefundQueryBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        refundQueryService = new RefundQueryService();
    }

    public interface ResultListener{
        //API����ReturnCode���Ϸ���֧�������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������
        void onFailByReturnCodeError(RefundQueryResData refundQueryResData);

        //API����ReturnCodeΪFAIL��֧��APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�
        void onFailByReturnCodeFail(RefundQueryResData refundQueryResData);

        //֧������API���ص�����ǩ����֤ʧ�ܣ��п������ݱ��۸���
        void onFailBySignInvalid(RefundQueryResData refundQueryResData);

        //�˿��ѯʧ��
        void onRefundQueryFail(RefundQueryResData refundQueryResData);

        //�˿��ѯ�ɹ�
        void onRefundQuerySuccess(RefundQueryResData refundQueryResData);

    }

    //��log��
    private static Log log = new Log(LoggerFactory.getLogger(RefundQueryBusiness.class));

    //ִ�н��
    private static String result = "";

    //��ѯ���Ľ��
    private static String orderListResult = "";

    private RefundQueryService refundQueryService;

    public String getOrderListResult() {
        return orderListResult;
    }

    public void setOrderListResult(String orderListResult) {
        RefundQueryBusiness.orderListResult = orderListResult;
    }

    /**
     * �����˿��ѯ��ҵ���߼�
     * @param refundQueryReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener �̻���Ҫ�Լ�������ɨ֧��ҵ���߼����ܴ����ĸ��ַ�֧�¼��������ú������Ӧ����
     * @throws Exception
     */
    public void run(RefundQueryReqData refundQueryReqData,RefundQueryBusiness.ResultListener resultListener) throws Exception {

        //--------------------------------------------------------------------
        //���������˿��ѯAPI������Ҫ�ύ������
        //--------------------------------------------------------------------

        //����API����
        String refundQueryServiceResponseString;

        long costTimeStart = System.currentTimeMillis();

        //��ʾ�Ǳ��ز�������
        log.i("�˿��ѯAPI���ص��������£�");
        refundQueryServiceResponseString = refundQueryService.request(refundQueryReqData);

        long costTimeEnd = System.currentTimeMillis();
        long totalTimeCost = costTimeEnd - costTimeStart;
        log.i("api�����ܺ�ʱ��" + totalTimeCost + "ms");

        log.i(refundQueryServiceResponseString);

        //����API���ص�XML����ӳ�䵽Java����
        RefundQueryResData refundQueryResData = (RefundQueryResData) Util.getObjectFromXML(refundQueryServiceResponseString, RefundQueryResData.class);

        ReportReqData reportReqData = new ReportReqData(
                refundQueryReqData.getDevice_info(),
                Configure.REFUND_QUERY_API,
                (int) (totalTimeCost),//���������ʱ
                refundQueryResData.getReturn_code(),
                refundQueryResData.getReturn_msg(),
                refundQueryResData.getResult_code(),
                refundQueryResData.getErr_code(),
                refundQueryResData.getErr_code_des(),
                refundQueryResData.getOut_trade_no(),
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


        if (refundQueryResData == null || refundQueryResData.getReturn_code() == null) {
            setResult("Case1:�˿��ѯAPI�����߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������",Log.LOG_TYPE_ERROR);
            resultListener.onFailByReturnCodeError(refundQueryResData);
            return;
        }

        //Debug:�鿴�����Ƿ���������䵽scanPayResponseData���������
        //Util.reflect(refundQueryResData);

        if (refundQueryResData.getReturn_code().equals("FAIL")) {
            ///ע�⣺һ�����ﷵ��FAIL�ǳ���ϵͳ��������������Post��API�������Ƿ�淶�Ϸ�
            setResult("Case2:�˿��ѯAPIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�",Log.LOG_TYPE_ERROR);
            resultListener.onFailByReturnCodeFail(refundQueryResData);
        } else {
            log.i("�˿��ѯAPIϵͳ�ɹ���������");
            //--------------------------------------------------------------------
            //�յ�API�ķ������ݵ�ʱ�������֤һ��������û�б��������۸ģ�ȷ����ȫ
            //--------------------------------------------------------------------

            if (!Signature.checkIsSignValidFromResponseString(refundQueryServiceResponseString)) {
                setResult("Case3:�˿��ѯAPI���ص�����ǩ����֤ʧ�ܣ��п������ݱ��۸���",Log.LOG_TYPE_ERROR);
                resultListener.onFailBySignInvalid(refundQueryResData);
                return;
            }

            if (refundQueryResData.getResult_code().equals("FAIL")) {
                Util.log("���������룺" + refundQueryResData.getErr_code() + "     ������Ϣ��" + refundQueryResData.getErr_code_des());
                setResult("Case4:���˿��ѯʧ�ܡ�",Log.LOG_TYPE_ERROR);
                resultListener.onRefundQueryFail(refundQueryResData);
                //�˿�ʧ��ʱ����ô��ʱ��ѯ�˿�״̬��û�����壬���ʱ�佨��Ҫô���ֶ�����һ�Σ���Ȼʧ�ܵĻ�����Ͷ����������Ͷ��
            } else {
                //�˿�ɹ�
                getRefundOrderListResult(refundQueryServiceResponseString);
                setResult("Case5:���˿��ѯ�ɹ���",Log.LOG_TYPE_INFO);
                resultListener.onRefundQuerySuccess(refundQueryResData);
            }
        }
    }

    /**
     * ��ӡ�����������صĶ�����ѯ���
     * @param refundQueryResponseString �˿��ѯ����API���ص�����
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    private void getRefundOrderListResult(String refundQueryResponseString) throws ParserConfigurationException, SAXException, IOException {
        List<RefundOrderData> refundOrderList = XMLParser.getRefundOrderList(refundQueryResponseString);
        int count = 1;
        for(RefundOrderData refundOrderData : refundOrderList){
            Util.log("�˿������NO" + count + ":");
            Util.log(refundOrderData.toMap());
            orderListResult += refundOrderData.toMap().toString();
            count++;
        }
        log.i("��ѯ���Ľ�����£�");
        log.i(orderListResult);
    }

    public void setRefundQueryService(RefundQueryService service) {
        refundQueryService = service;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        RefundQueryBusiness.result = result;
    }

    public void setResult(String result,String type){
        setResult(result);
        log.log(type,result);
    }

}
