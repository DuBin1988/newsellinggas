package com.aote.dbf;
import org.codehaus.jettison.json.JSONObject;

import com.aote.dbf.util.DBFUtils;
import com.aote.dbf.util.DateHelper;
import com.linuxense.javadbf.DBFField;


public class CQA188 {
	
	static String[] name=new String[]{"���","���³�����","���³�����","����","��ַ","¥��","����","�ͻ��绰","�û���","�û����","ȼ������","���¶���","���¶���","ʵ������","����","Ӧ�ս��","�Ż�","ʵ�ս��","��һ","���","����","����ά����","�û�����","�����־","Ƿ�ѱ�־","Ƿ������","Ƿ�ѽ��","���ɽ�","����ʱ��","ȼ������","�����˲��","�ɷѷ�ʽ","�û�����","���ɽ����","����Ա����","�����·�","���","������","��״̬","�������ɽ�","��������","����Ա����","����ʽ","�ɷ�������","��Ӧ�˺�","�ʻ�����","�ɷ����б�","�ͻ��绰1"};
	static byte[] type=new byte[]{DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C};
	static int[] length=new int[]{4,11,11,20,50,6,6,12,20,12,12,9,9,9,6,9,6,9,5,5,5,10,9,9,9,9,9,9,9,9,11,15,9,11,11,7,9,9,9,9,20,9,9,9,20,9,11,12};
	
	public static DBFField[] getFields() {
	    return DBFUtils.getFields(name, type, length);
	}
	
	public static Object[] getObjects(int length,JSONObject obj,int xuhao) {
		Object[] rowData = new Object[length];
		try {
	        rowData[0] = (double)xuhao;
	        rowData[1] = (double)Double.parseDouble(DateHelper.longto_Date(obj.getLong("lastinputdate")).replace("-", ""));  //���³�����
	        rowData[2] = "";  
	        rowData[3] = obj.getString("f_districtname");  
	        rowData[4] = obj.getString("f_address");  
	        rowData[5] = "";  
	        rowData[6] = "";  
	        rowData[7] = (double)Double.parseDouble(obj.getString("f_phone").equals("null")?"0":obj.getString("f_phone"));  
	        rowData[8] = obj.getString("f_username");  
	        rowData[9] = obj.getString("f_userid");  
	        rowData[10] = obj.getString("f_meternumber");  
	        rowData[11] = (double)obj.getDouble("lastinputgasnum");  
	        rowData[12] = (double)obj.getDouble("lastinputgasnum");  
	        rowData[13] = (double)0;  
	        rowData[14] = (double)1;  
	        rowData[15] = "";  
	        rowData[16] = "";  
	        rowData[17] = "";  
	        rowData[18] = "";  
	        rowData[19] = "";  
	        rowData[20] = "";  
	        rowData[21] = "";
	        rowData[22] = (double)1; //�û����� 
	        rowData[23] = "0";  
	        rowData[24] = "0";  
	        rowData[25] = "0";  
	        rowData[26] = (double)0;  
	        rowData[27] = "0";  
	        rowData[28] = "";  
	        rowData[29] = "";              
	        rowData[30] = (double)0;  
	        rowData[31] = "ÿ�½���";  
	        rowData[32] = "";  
	        rowData[33] = (double)0;  
	        rowData[34] = "";  
	        rowData[35] = DateHelper.longto_Date(obj.getLong("f_handdate")).replace("-", "").substring(0, 6);//�����·�
	        rowData[36] = "";  
	        rowData[37] = "��ͨ";  
	        rowData[38] = "����";
	        rowData[39] = "0";  
	        rowData[40] = obj.getString("f_stairtype");  
	        rowData[41] = obj.getString("f_inputtor");  
	        rowData[42] = "�ֹ�";  
	        rowData[43] = "";  
	        rowData[44] = "";  
	        rowData[45] = "";  
	        rowData[46] = (double)0;  
	        rowData[47] = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
        return rowData;
	}

}
