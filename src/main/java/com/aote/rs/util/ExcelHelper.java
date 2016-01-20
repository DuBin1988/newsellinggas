package com.aote.rs.util;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.codehaus.jettison.json.JSONArray;

/**
 * Excel文件帮助类
 * 
 * @author Administrator
 *
 */
public class ExcelHelper {

	/**
	 * 从文件读取信息，转换为json数据
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static JSONArray convertFromFile(String fileName, int startRow,
			String fields) throws FileNotFoundException, IOException {
		JSONArray result = getData(fileName, startRow, fields);
		return result;
	}

	/**
	 * 
	 * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
	 * 
	 * @param file
	 *            读取数据的源Excel
	 * @param startRow
	 *            从第几行开始读取，前面的忽略
	 * @return 读出的Excel中数据的内容
	 */
	public static JSONArray getData(String fileName, int startRow, String fields) {
		JSONArray datas = new JSONArray();
		try {
			// 字段名
			String[] fieldsArray = fields.split("\\|");
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(fileName));
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFCell cell = null;
			HSSFSheet st = wb.getSheetAt(0);
			// excel从0行开始，startrow减去1为开始行号
			startRow = startRow - 1;
			for (int rowIndex = startRow; rowIndex <= st.getLastRowNum(); rowIndex++) {
				HashMap<String, Object> oneData = new HashMap<String, Object>();
				HSSFRow row = st.getRow(rowIndex);
				for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					String fieldName = fieldsArray[columnIndex];
					String value = "";
					cell = row.getCell(columnIndex);
					if (cell == null) {
						oneData.put(fieldName, value);
						continue;
					}
					int cellType = cell.getCellType();
					if (cellType == HSSFCell.CELL_TYPE_STRING) {
						value = cell.getStringCellValue();
						value = rightTrim(value);
						oneData.put(fieldName, value);
					} else if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = (Date) cell.getDateCellValue();
							if (date != null) {
								oneData.put(fieldName, date);
							}
						} else {
							BigDecimal num = new BigDecimal(
									cell.getNumericCellValue());
							oneData.put(fieldName, num);
						}
					} else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
						boolean bool = cell.getBooleanCellValue();
						oneData.put(fieldName, bool);
					} else {
						oneData.put(fieldName, value);
					}
				}
				datas.put(oneData);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

	/**
	 * 去掉字符串右边的空格
	 */
	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}
}
