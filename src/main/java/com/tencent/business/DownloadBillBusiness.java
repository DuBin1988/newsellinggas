package com.tencent.business;

import com.tencent.WXPay;
import com.tencent.common.Configure;
import com.tencent.common.Log;
import com.tencent.common.Util;
import com.tencent.common.report.ReporterFactory;
import com.tencent.common.report.protocol.ReportReqData;
import com.tencent.common.report.service.ReportService;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.tencent.protocol.downloadbill_protocol.DownloadBillResData;
import com.tencent.service.DownloadBillService;
import com.thoughtworks.xstream.io.StreamException;
import org.slf4j.LoggerFactory;

/**
 * User: rizenguo
 * Date: 2014/12/3
 * Time: 10:45
 */
public class DownloadBillBusiness {

    public DownloadBillBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        downloadBillService = new DownloadBillService();
    }

    public interface ResultListener{
        //API����ReturnCode���Ϸ���֧�������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������
        void onFailByReturnCodeError(DownloadBillResData downloadBillResData);

        //API����ReturnCodeΪFAIL��֧��APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�
        void onFailByReturnCodeFail(DownloadBillResData downloadBillResData);

        //���ض��˵�ʧ��
        void onDownloadBillFail(String response);

        //���ض��˵��ɹ�
        void onDownloadBillSuccess(String response);

    }

    //��log��
    private static Log log = new Log(LoggerFactory.getLogger(DownloadBillBusiness.class));

    //ִ�н��
    private static String result = "";

    private DownloadBillService downloadBillService;

    /**
     * ������˵����ط���
     * @param downloadBillReqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @param resultListener �̻���Ҫ�Լ�������ɨ֧��ҵ���߼����ܴ����ĸ��ַ�֧�¼��������ú������Ӧ����
     * @return API���ص�XML����
     * @throws Exception
     */

    public void run(DownloadBillReqData downloadBillReqData,ResultListener resultListener) throws Exception {

        //--------------------------------------------------------------------
        //�������󡰶��˵�API������Ҫ�ύ������
        //--------------------------------------------------------------------

        //API���ص�����
        String downloadBillServiceResponseString;

        long costTimeStart = System.currentTimeMillis();

        //֧�ּ��ر��ز������ݽ��е���

        log.i("���˵�API���ص��������£�");
        downloadBillServiceResponseString = downloadBillService.request(downloadBillReqData);


        long costTimeEnd = System.currentTimeMillis();
        long totalTimeCost = costTimeEnd - costTimeStart;
        log.i("api�����ܺ�ʱ��" + totalTimeCost + "ms");

        log.i(downloadBillServiceResponseString);

        DownloadBillResData downloadBillResData;

        String returnCode = "";
        String returnMsg = "";

        try {
            //ע�⣬����ʧ�ܵ�ʱ���Ƿ���xml���ݣ��ɹ���ʱ�򷴶����ط�xml����
            downloadBillResData = (DownloadBillResData) Util.getObjectFromXML(downloadBillServiceResponseString, DownloadBillResData.class);

            if (downloadBillResData == null || downloadBillResData.getReturn_code() == null) {
                setResult("Case1:���˵�API�����߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ���������",Log.LOG_TYPE_ERROR);
                resultListener.onFailByReturnCodeError(downloadBillResData);
                return;
            }
            if (downloadBillResData.getReturn_code().equals("FAIL")) {
                ///ע�⣺һ�����ﷵ��FAIL�ǳ���ϵͳ��������������Post��API�������Ƿ�淶�Ϸ�
                setResult("Case2:���˵�APIϵͳ����ʧ�ܣ�����Post��API�������Ƿ�淶�Ϸ�",Log.LOG_TYPE_ERROR);
                resultListener.onFailByReturnCodeFail(downloadBillResData);
                returnCode = "FAIL";
                returnMsg = downloadBillResData.getReturn_msg();
            }
        } catch (StreamException e) {
            //ע�⣬����ɹ���ʱ����ֱ�ӷ��ش��ı��Ķ��˵��ı����ݣ���XML��ʽ
            if (downloadBillServiceResponseString.equals(null) || downloadBillServiceResponseString.equals("")) {
                setResult("Case4:���˵�APIϵͳ��������Ϊ��",Log.LOG_TYPE_ERROR);
                resultListener.onDownloadBillFail(downloadBillServiceResponseString);
            } else {
                setResult("Case3:���˵�APIϵͳ�ɹ���������",Log.LOG_TYPE_INFO);
                resultListener.onDownloadBillSuccess(downloadBillServiceResponseString);
            }
            returnCode = "SUCCESS";
        } finally {

            ReportReqData reportReqData = new ReportReqData(
                    downloadBillReqData.getDevice_info(),
                    Configure.DOWNLOAD_BILL_API,
                    (int) (totalTimeCost),//���������ʱ
                    returnCode,
                    returnMsg,
                    "",
                    "",
                    "",
                    "",
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
        }
    }

    public void setDownloadBillService(DownloadBillService service) {
        downloadBillService = service;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        DownloadBillBusiness.result = result;
    }

    public void setResult(String result,String type){
        setResult(result);
        log.log(type,result);
    }

}
