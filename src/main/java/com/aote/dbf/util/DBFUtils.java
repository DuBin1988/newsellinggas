package com.aote.dbf.util;
import com.linuxense.javadbf.DBFField;


public class DBFUtils {
	
	public static DBFField[] getFields(String[] name,byte[] type,int[] length) {	
		// 定义DBF文件字段  
	    DBFField[] fields = new DBFField[name.length];
	    for(int i=0;i<name.length;i++){
	    	Fields(fields,i,name[i],type[i],length[i]);
	    }	    
	    return fields;
		}
	
	public static void Fields(DBFField[] fields,int i,String name,byte type,int length) {		
		fields[i] = new DBFField();
	    fields[i].setName(name);  
	    fields[i].setDataType(type);  
	    fields[i].setFieldLength(length);
		}

}
