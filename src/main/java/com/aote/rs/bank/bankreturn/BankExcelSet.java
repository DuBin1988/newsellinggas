package com.aote.rs.bank.bankreturn;

/**
 * 银行返单Excel信息
 * @author Administrator
 *
 */
public class BankExcelSet {

	public BankExcelSet(){
		
	}
	
	/**
	 * 定义的字段列名称，与Excel文件对应
	 */
	public String fields = "";

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}
	
	/**
	 * 读取Excel起始行
	 */
	public int startRow ;

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow =startRow;
	}
	
}
