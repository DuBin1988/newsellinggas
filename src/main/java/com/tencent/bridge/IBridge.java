package com.tencent.bridge;

/**
 * User: rizenguo
 * Date: 2014/12/1
 * Time: 17:11
 */
public interface IBridge {

    /**
     * ��ȡauth_code�������ɨ���ն��豸���û��ֻ���ɨȡ����֧����Ȩ�ţ�������Ǹ��û�����֧�������п��󶨵ģ���Ч����1����
     * @return ��Ȩ��
     */
    public String getAuthCode();

    /**
     * ��ȡout_trade_no��������̻�ϵͳ���Լ���������Ψһ��ʶ�ñʶ������ַ��������԰�����ĸ�����֣�������32λ
     * @return ������
     */
    public String getOutTradeNo();

    /**
     * ��ȡbody:Ҫ֧������Ʒ��������Ϣ���û�����֧���ɹ�ҳ���￴�������Ϣ
     * @return ������Ϣ
     */
    public String getBody();


    /**
     * ��ȡattach:֧���������������ĸ������ݣ�API�Ὣ�ύ�������������ԭ�����أ��������̻��Լ�����ע���ñ����ѵľ������ݣ������������Ӫ�ͼ�¼
     * @return ��������
     */
    public String getAttach();

    /**
     * ��ȡ�����ܶ�
     * @return �����ܶ�
     */
    public int getTotalFee();

    /**
     * ��ȡdevice_info:�̻��Լ������ɨ��֧���ն��豸�ţ�����׷����ʽ��׷�������̨�ն��豸��
     * @return ֧���ն��豸��
     */
    public String getDeviceInfo();

    /**
     * ��ȡ������ip��ַ
     * @return �����豸��ip��ַ
     */
    public String getUserIp();

    /**
     * ��ȡspBillCreateIP:�������ɵĻ���IP
     * @return �������ɵĻ���IP
     */
    public String getSpBillCreateIP();

    /**
     * ��ȡtime_start:��������ʱ��
     * @return ��������ʱ��
     */
    public String getTimeStart();

    /**
     * ��ȡtime_end:��������ʱ��
     * @return ����ʧЧʱ��
     */
    public String getTimeExpire();

    /**
     * ��ȡgoods_tag:��Ʒ��ǣ�΢��ƽ̨���õ���Ʒ��ǣ������Ż�ȯ��������ʹ��
     * @return ��Ʒ���
     */
    public String getGoodsTag();

    /**
     * ��ȡtransaction_id:΢��ƽ̨֧���ɹ�ʱ�������Ψһ���׺ţ�һ��ֻҪ�����tracnsacion_id�������Ĳ�ѯ���������˿����������������������̻��Լ����Ǹ�out_trade_no
     * @return ΢��ƽ̨�ٷ�����Ľ��׺�
     */
    public String getTransactionID();

    /**
     * ��ȡout_refund_no:�̻�ϵͳ�ڲ����˿�ţ��̻�ϵͳ�ڲ�Ψһ��ͬһ�˿�Ŷ������ֻ��һ��
     * @return �̻�ϵͳ�ڲ����˿��
     */
    public String getOutRefundNo();

    /**
     * ��ȡrefund_fee:��ȡ�����˿�������Ҫ�˵ľ����������ܱ����������total_fee���ܽ�����
     * @return �����˿�������Ҫ�˵ľ�����
     */
    public int getRefundFee();

    /**
     * ��ȡrefund_id:΢��ƽ̨�˿�ɹ�ʱ�������Ψһ�˿�ţ�һ��ֻҪ�����refund_id�������Ĳ�ѯ�������������
     * @return ΢��ƽ̨�ٷ�������˿��
     */
    public String getRefundID();

    /**
     * ��ȡbill_date:��ȡ���˵�API��Ҫ�����ڣ���ʽ��yyyyMMdd
     * @return Ҫ��ѯ���˵�������
     */
    public String getBillDate();

    /**
     * ��ȡbill_type:��ȡ���˵�API��Ҫ���������ͣ���Щ������DownloadBillService�����ж���
     * @return Ҫ��ѯ���˵�������
     */
    public String getBillType();

    /**
     * ��ȡ����Ա��ID��Ĭ�ϵ����̻���
     * @return ���ز���Ա��ID
     */
    public String getOpUserID();

    /**
     * ��ȡ�˿�������ͣ�����ISO 4217��׼����λ��ĸ���룬Ĭ��ΪCNY������ң�
     * @return ��ȡ�˿��������
     */
    public String getRefundFeeType();

}
