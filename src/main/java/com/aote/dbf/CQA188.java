package com.aote.dbf;
import org.codehaus.jettison.json.JSONObject;

import com.aote.dbf.util.DBFUtils;
import com.aote.dbf.util.DateHelper;
import com.linuxense.javadbf.DBFField;


public class CQA188 {
	
	static String[] name=new String[]{"序号","上月抄表日","本月抄表日","区域","地址","楼栋","房号","客户电话","用户名","用户编号","燃气表编号","本月读数","上月读数","实收气量","单价","应收金额","优惠","实收金额","表一","表二","表三","管网维护费","用户表数","抄表标志","欠费标志","欠费天数","欠费金额","滞纳金","抄表时间","燃气种类","抄表账册号","缴费方式","用户类型","滞纳金比例","抄表员编码","所属月份","表号","表类型","表状态","免收滞纳金","用气类型","抄表员名称","抄表方式","缴费银行名","对应账号","帐户名称","缴费银行编","客户电话1"};
	static byte[] type=new byte[]{DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C};
	static int[] length=new int[]{4,11,11,20,50,6,6,12,20,12,12,9,9,9,6,9,6,9,5,5,5,10,9,9,9,9,9,9,9,9,11,15,9,11,11,7,9,9,9,9,20,9,9,9,20,9,11,12};
	
	public static DBFField[] getFields() {
	    return DBFUtils.getFields(name, type, length);
	}
	
	public static Object[] getObjects(int length,JSONObject obj,int xuhao) {
		Object[] rowData = new Object[length];
		try {
	        rowData[0] = (double)xuhao;
	        rowData[1] = (double)Double.parseDouble(DateHelper.longto_Date(obj.getLong("lastinputdate")).replace("-", ""));  //上月抄表日
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
	        rowData[22] = (double)1; //用户表数 
	        rowData[23] = "0";  
	        rowData[24] = "0";  
	        rowData[25] = "0";  
	        rowData[26] = (double)0;  
	        rowData[27] = "0";  
	        rowData[28] = "";  
	        rowData[29] = "";              
	        rowData[30] = (double)0;  
	        rowData[31] = "每月结账";  
	        rowData[32] = "";  
	        rowData[33] = (double)0;  
	        rowData[34] = "";  
	        rowData[35] = DateHelper.longto_Date(obj.getLong("f_handdate")).replace("-", "").substring(0, 6);//所属月份
	        rowData[36] = "";  
	        rowData[37] = "普通";  
	        rowData[38] = "正常";
	        rowData[39] = "0";  
	        rowData[40] = obj.getString("f_stairtype");  
	        rowData[41] = obj.getString("f_inputtor");  
	        rowData[42] = "手工";  
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
