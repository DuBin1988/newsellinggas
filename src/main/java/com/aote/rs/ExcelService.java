package com.aote.rs;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Path("excel")
@Scope("prototype")
@Component
public class ExcelService {
	static Logger log = Logger.getLogger(ExcelService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	//��excel
	@POST
	@Path("/{count}/{cols}")
	public String exporttoexcel(String query, @PathParam("count") int count,
			@PathParam("cols") String cols) {
		log.debug("in to excel count=" + count + ", cols=" + cols);
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			int n = count;
			int total = 0;
			HSSFSheet sheet;
			String[] colsStr = cols.split("\\|");
			while(n > 0)
			{
				sheet = workbook.createSheet();
				HSSFFont font = workbook.createFont();
				font.setColor((short) HSSFFont.COLOR_NORMAL);
				font.setBoldweight((short) HSSFFont.BOLDWEIGHT_BOLD);
				// ���ø�ʽ
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setAlignment((short) HSSFCellStyle.ALIGN_CENTER);
				cellStyle.setFont(font);
				HSSFRow row = null;
				HSSFCell cell = null;
				// ������0�� ����
				int rowNum = 0;
				row = sheet.createRow((short) rowNum);
				for (int titleCol = 0; titleCol < colsStr.length; titleCol++) {
					cell = row.createCell((short) (titleCol));
					// ����������
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// �����е��ַ���Ϊ����
					cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
					// ��������
					String[] names = colsStr[titleCol].split(":");
					// �б���,��������Ϊ����������Ϊ�ֶ�����
					if (names.length > 1) {
						cell.setCellValue(names[1]);
					} else {
						cell.setCellValue(colsStr[titleCol]);
					}
					cell.setCellStyle(cellStyle);
				} 
				
				n -= 50000;
			}
			
			sheet = workbook.getSheetAt(total);
			int rowNum = 0;
			
			StatelessSession session = this.hibernateTemplate.getSessionFactory().openStatelessSession();
			ScrollableResults sr;
			if (query.startsWith("sql:")) {
				String sql = query.substring(4);
				SQLQuery sq = session.createSQLQuery(sql);
				sr = sq.setFetchSize(1000).setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
				
				Field f = sr.getClass().getSuperclass().getDeclaredField("loader");
				f.setAccessible(true);
				Object obj = f.get(sr);
				f = obj.getClass().getDeclaredField("transformerAliases");
				f.setAccessible(true);
				String[] colNames = (String[])f.get(obj);
				int[] idx = new int[colsStr.length];
				for (int j = 0; j < idx.length; j++) {
					 	idx[j] = -1;
					 
				 
	 			}
				
				for (int i = 0; i < colNames.length; i++) {
					for (int j = 0; j < idx.length; j++) {
						if (colsStr[j].split(":")[0].equals(colNames[i])) {
							idx[j] = i;
						}
					 
		 			}
				}
				while (sr.next()) {
					Object[] map = sr.get();
					rowNum++;
					if(rowNum % 50001 == 0)
					{
						sheet = workbook.getSheetAt(++total);
						rowNum = 1;
					}
					
					HSSFRow row = sheet.createRow((short) rowNum);
					
					for (int z = 0; z < colsStr.length; z++) {

						// �õ�����
						String[] names = colsStr[z].split(":");
						// �б������ֶ���Ϊ��һ������������ֶ���
						String colName = colsStr[z];
						if (names.length > 1) {
							colName = names[0];
						}
						String data = "";
						if(idx[z] == -1)
						{
							data = colsStr[z].split(":")[0];
							if(data.equals("index"))
							{
								data =rowNum+"";
							}
						}
						else if (map[idx[z]] != null) {
							data = map[idx[z]].toString();
						}
						
						
				 /**
						// �����ѯ�к��и��ֶ����������ֶ���ȡ���ݣ����򣬽�����������������
						if (map.containsKey(colName)) {
								if (map[idx[z]] != null) {
								data = map[idx[z]].toString();
							else
					    	  	data = "";
							
						} else  if (colName.equals("index")){
							data =rowNum+"";
						}
						else
						{
							data = colName;
						}
						**/
						HSSFCell cell = row.createCell((short) (z));
						// ����������
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// �����е��ַ���Ϊ����
						cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(data);
					}
				}				
			} else {
				sr = session.createQuery(query).setFetchSize(1000).setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
				while (sr.next()) {
					Map<String, Object> map = (Map<String, Object>)sr.get(0);
					rowNum++;
					
					if(rowNum % 50000 == 0)
					{
						sheet = workbook.getSheetAt(++total);
						rowNum = 1;
					}
					HSSFRow row = sheet.createRow((short) rowNum);
					
					for (int z = 0; z < colsStr.length; z++) {
						// �õ�����
						String[] names = colsStr[z].split(":");
						// �б������ֶ���Ϊ��һ������������ֶ���
						String colName = colsStr[z];
						if (names.length > 1) {
							colName = names[0];
						}
						String data = "";
						if (map.get(colName) != null) {
							data = map.get(colName).toString();
						}
						HSSFCell cell = row.createCell((short) (z));
						// ����������
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// �����е��ַ���Ϊ����
						cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(data);
					}
				}
			}
			
			session.close();

			// ������ʱ�ļ�
			File file = File.createTempFile("temp", ".xls");
			file.deleteOnExit();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// д�ļ�
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			// �����ļ���
			String result = file.getAbsolutePath();
			log.debug(result);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//��excel
	@POST
	@Path("/{count}")
	public String exporttoexcel2(String json, @PathParam("count") int count) {
		log.debug("in to excel count=" + count);
		try {
			String[] strs = json.split("~");
			String query = strs[0];
			String cols = strs[1];
			int n = count;
			int total = 0;
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet;
			String[] colsStr = cols.split("\\|");
			while(n > 0)
			{
				sheet = workbook.createSheet();
				HSSFFont font = workbook.createFont();
				font.setColor((short) HSSFFont.COLOR_NORMAL);
				font.setBoldweight((short) HSSFFont.BOLDWEIGHT_BOLD);
				// ���ø�ʽ
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setAlignment((short) HSSFCellStyle.ALIGN_CENTER);
				cellStyle.setFont(font);
				HSSFRow row = null;
				HSSFCell cell = null;
				// ������0�� ����
				int rowNum = 0;
				row = sheet.createRow((short) rowNum);
				for (int titleCol = 0; titleCol < colsStr.length; titleCol++) {
					cell = row.createCell((short) (titleCol));
					// ����������
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// �����е��ַ���Ϊ����
					cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
					// ��������
					String[] names = colsStr[titleCol].split(":");
					// �б���,��������Ϊ����������Ϊ�ֶ�����
					if (names.length > 1) {
						cell.setCellValue(names[1]);
					} else {
						cell.setCellValue(colsStr[titleCol]);
					}
					cell.setCellStyle(cellStyle);
				}
				
				n -= 50000;
			}
			
			sheet = workbook.getSheetAt(total);
			int rowNum = 0;

			StatelessSession session = this.hibernateTemplate.getSessionFactory().openStatelessSession();
			ScrollableResults sr;
			if (query.startsWith("sql:")) {
				String sql = query.substring(4);
				sr = session.createSQLQuery(sql).setFetchSize(1000).setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
				
				Field f = sr.getClass().getSuperclass().getDeclaredField("loader");
				f.setAccessible(true);
				Object obj = f.get(sr);
				f = obj.getClass().getDeclaredField("transformerAliases");
				f.setAccessible(true);
				String[] colNames = (String[])f.get(obj);
				int[] idx = new int[colsStr.length];
				
				for (int i = 0; i < colNames.length; i++) {
					for (int j = 0; j < idx.length; j++) {
						if (colsStr[j].split(":")[0].equals(colNames[i])) {
							idx[j] = i;
						}
					}
				}
				
				while (sr.next()) {
					Object[] map = sr.get();
					rowNum++;
					
					if(rowNum % 50001 == 0)
					{
						sheet = workbook.getSheetAt(++total);
						rowNum = 1;
					}
					
					HSSFRow row = sheet.createRow((short) rowNum);
						
					for (int z = 0; z < colsStr.length; z++) {
						// �õ�����
						String[] names = colsStr[z].split(":");
						// �б������ֶ���Ϊ��һ������������ֶ���
						String colName = colsStr[z];
						if (names.length > 1) {
							colName = names[0];
						}
						String data = "";
						if (map[idx[z]] != null) {
							data = map[idx[z]].toString();
						}
						HSSFCell cell = row.createCell((short) (z));
						// ����������
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// �����е��ַ���Ϊ����
						cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(data);
					}
				}				
			} else {
				sr = session.createQuery(query).setFetchSize(1000).setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
				while (sr.next()) {
					Map<String, Object> map = (Map<String, Object>)sr.get(0);
					rowNum++;
					
					if(rowNum % 50000 == 0)
					{
						sheet = workbook.getSheetAt(++total);
						rowNum = 1;
					}
					
					HSSFRow row = sheet.createRow((short) rowNum);
						
					for (int z = 0; z < colsStr.length; z++) {
						// �õ�����
						String[] names = colsStr[z].split(":");
						// �б������ֶ���Ϊ��һ������������ֶ���
						String colName = colsStr[z];
						if (names.length > 1) {
							colName = names[0];
						}
						String data = "";
						if (map.get(colName) != null) {
							data = map.get(colName).toString();
						}
						HSSFCell cell = row.createCell((short) (z));
						// ����������
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// �����е��ַ���Ϊ����
						cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(data);
					}
				}
			}
			session.close();
			// ������ʱ�ļ�
			File file = File.createTempFile("temp", ".xls");
			file.deleteOnExit();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// д�ļ�
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			// �����ļ���
			String result = file.getAbsolutePath();
			log.debug(result);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ִ�з�ҳ��ѯ
	class HibernateCall implements HibernateCallback {
		String hql;
		int page;
		int rows;

		public HibernateCall(String hql, int page, int rows) {
			this.hql = hql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createQuery(hql);
			List result = q.setFirstResult(page * rows).setMaxResults(rows)
					.list();
			return result;
		}
	}

	// ִ��sql��ҳ��ѯ���������ʽ��������
	class HibernateSQLCall implements HibernateCallback {
		String sql;
		int page;
		int rows;
		// ��ѯ���ת����������ת����Map�ȡ�
		public ResultTransformer transformer = null;

		public HibernateSQLCall(String sql, int page, int rows) {
			this.sql = sql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			// ��ת����������ת����
			if (transformer != null) {
				q.setResultTransformer(transformer);
			}
			List result = q.setFirstResult(page * rows).setMaxResults(rows)
					.list();
			return result;
		}
	}
}