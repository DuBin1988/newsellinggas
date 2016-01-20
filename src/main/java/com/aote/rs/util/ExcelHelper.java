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
 * Excel�ļ�������
 * 
 * @author Administrator
 *
 */
public class ExcelHelper {

	/**
	 * ���ļ���ȡ��Ϣ��ת��Ϊjson����
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
	 * ��ȡExcel�����ݣ���һά����洢����һ���и��е�ֵ����ά����洢���Ƕ��ٸ���
	 * 
	 * @param file
	 *            ��ȡ���ݵ�ԴExcel
	 * @param startRow
	 *            �ӵڼ��п�ʼ��ȡ��ǰ��ĺ���
	 * @return ������Excel�����ݵ�����
	 */
	public static JSONArray getData(String fileName, int startRow, String fields) {
		JSONArray datas = new JSONArray();
		try {
			// �ֶ���
			String[] fieldsArray = fields.split("\\|");
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(fileName));
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFCell cell = null;
			HSSFSheet st = wb.getSheetAt(0);
			// excel��0�п�ʼ��startrow��ȥ1Ϊ��ʼ�к�
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
	 * ȥ���ַ����ұߵĿո�
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
