package com.aote.rs.bank.bankreturn;

/**
 * ���з���Excel��Ϣ
 * @author Administrator
 *
 */
public class BankExcelSet {

	public BankExcelSet(){
		
	}
	
	/**
	 * ������ֶ������ƣ���Excel�ļ���Ӧ
	 */
	public String fields = "";

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}
	
	/**
	 * ��ȡExcel��ʼ��
	 */
	public int startRow ;

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow =startRow;
	}
	
}
