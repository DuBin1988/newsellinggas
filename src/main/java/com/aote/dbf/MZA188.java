package com.aote.dbf;
import org.codehaus.jettison.json.JSONObject;

import com.aote.dbf.util.DBFUtils;
import com.aote.dbf.util.DateHelper;
import com.linuxense.javadbf.DBFField;


public class MZA188 {
	
	static String[] name=new String[]{"id","sortindex","memberid","meterid","meterprnid","caliber","maxreading","mname","maddress","mtele","arrcount","arrsum","ispositive","lastread","lastread1","lastread2","newread","newread1","newread2","resultread","charge","tname1","tprice1","tcount1","tname2","tprice2","tcount2","tname3","tprice3","tcount3","tname4","tprice4","tcount4","tname5","tprice5","tcount5","tname6","tprice6","tcount6","feesystem","tresult1","tresult2","tresult3","tresult4","tresult5","tresult6","tcharge1","tcharge2","tcharge3","tcharge4","tcharge5","tcharge6","lbalance"};
	static byte[] type=new byte[]{DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_N,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C,DBFField.FIELD_TYPE_C};
	static int[] length=new int[]{10,50,50,10,50,20,13,100,100,50,5,12,10,13,13,13,13,13,13,13,12,20,10,10,20,10,10,20,10,10,20,10,10,20,10,10,20,10,10,1,10,10,10,10,10,10,10,10,10,10,10,10,10};
	
	public static DBFField[] getFields() {
	    return DBFUtils.getFields(name, type, length);
	}
	
	public static Object[] getObjects(int length,JSONObject obj,int xuhao) {
		Object[] rowData = new Object[length];
		try {			
	        rowData[0] = xuhao+"";
	        rowData[1] = obj.getString("f_userid");
	        rowData[2] = obj.getString("f_userid");  
	        rowData[3] = obj.getString("f_userid");  
	        rowData[4] = "";  
	        rowData[5] = "2.5";
	        rowData[6] = "99999";  
	        rowData[7] = obj.getString("f_username");  
	        rowData[8] = obj.getString("f_districtname")+obj.getString("f_address");  
	        rowData[9] = "1";  
	        rowData[10] = "1";  
	        rowData[11] = "0";
	        rowData[12] = "正向";  
	        rowData[13] = "0";
	        rowData[14] = "0";  
	        rowData[15] = (double)obj.getDouble("lastinputgasnum");  
	        rowData[16] = (double)0;  
	        rowData[17] = "0";  
	        rowData[18] = (double)0;  
	        rowData[19] = "0";  
	        rowData[20] = "0";  
	        rowData[21] = "0";
	        rowData[22] = obj.getString("f_stairtype"); //用户表数 
	        rowData[23] = "0";  
	        rowData[24] = "0";  
	        rowData[25] = "0";  
	        rowData[26] = "0";  
	        rowData[27] = "0";  
	        rowData[28] = "0";  
	        rowData[29] = "0";              
	        rowData[30] = "0";  
	        rowData[31] = "0";  
	        rowData[32] = "0";  
	        rowData[33] = "0";  
	        rowData[34] = "0";  
	        rowData[35] = "0";
	        rowData[36] = "0";  
	        rowData[37] = "0";  
	        rowData[38] = "0";
	        rowData[39] = "0";  
	        rowData[40] = "1";  
	        rowData[41] = "0";  
	        rowData[42] = "0";  
	        rowData[43] = "0";
	        rowData[44] = "0";  
	        rowData[45] = "0";  
	        rowData[46] = "0";  
	        rowData[47] = "0";
	        rowData[48] = "0";
	        rowData[49] = "0";
	        rowData[50] = "0";
	        rowData[51] = "0";
	        rowData[52] = "0";
		} catch (Exception e) {
			e.printStackTrace();
		}
        return rowData;
	}

}
