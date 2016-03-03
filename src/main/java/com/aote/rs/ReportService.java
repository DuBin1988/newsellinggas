package com.aote.rs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;


@Path("report")
@Scope("prototype")
@Component
public class ReportService {
	static Logger log = Logger.getLogger(DBService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
   
	/**
	 * �����ļ�����ñ����������Ϣ
	 * 
	 * @param filename
	 * @return
	 */
	@GET
	@Path("{filename}")
	public String getjson(@PathParam("filename") String filename) {
		String result = "";
		try {
			String encoding = "GBK";
			String path = this.getClass().getClassLoader().getResource(
					"/report/").getPath();
			File file = new File(path + filename);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
		return result;
	}

	@POST
	@Path("sql/{pageIndex}/{pageSize}")
	// ��sql��ʽִ�к󣬻�ȡһҳ���ݣ��ֶ�����SQL������
	public JSONObject postSQLPage(@PathParam("pageSize") int pageSize,
			@PathParam("pageIndex") int pageIndex, String query) {
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray(query);
			for (int i = 0; i < array.length(); i++) {
				JSONArray datas = new JSONArray();
				JSONObject object = array.getJSONObject(i);
				String name = object.getString("name");
				String sql = object.getString("sql");
				sql = sql.replace("$", "'");
				HibernateSQLCall sqlCall = new HibernateSQLCall(sql, pageIndex,
						pageSize);
				sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
				List<Map<String, Object>> list = this.hibernateTemplate
						.executeFind(sqlCall);
				for (Map<String, Object> map : list) {
					JSONObject json = (JSONObject) new JsonTransfer()
							.MapToJson(map);
					datas.put(json);
				}
				result.put(name, datas);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	@POST
	@Path("sql/{sumNames}")
	// ������
	public JSONObject postSQLSum(@PathParam("sumNames") String names,
			String query) {
		JSONObject result = null;
		try {
			// ֻȡ��һ��sql
			JSONArray array = new JSONArray(query);
			JSONObject object = array.getJSONObject(0);
			query = object.getString("sql").replace("$", "'");
			// ��֯sums��
			String sums = "";
			String[] snames = names.split(",");
			for (String name : snames) {
				if (!sums.equals(""))
					sums += ",";
				sums += "sum(" + name + ") as " + name;
			}
			String sql = "";
			if (!sums.equals("")) {
				// ǰ������һ��count���Ա�ͳһ�������飬�����ǵ�������
				sql = "select count(*) as Count, count(*) as Count, " + sums
						+ " from (" + query + ") t";
			} else {
				// ǰ������һ��count���Ա�ͳһ�������飬�����ǵ�������
				sql = "select count(*) as Count, count(*) as Count "
						+ " from (" + query + ") t";
			}
			log.debug(sql);
			HibernateSQL call = new HibernateSQL(sql);
			List list = (List) hibernateTemplate.execute(call);
			// ��mapת����json����
			Object[] objs = (Object[]) list.get(0);
			Map<String, Object> map = new HashMap<String, Object>();
			// �Ȱ�Count�Ž�ȥ��ͷһ��������
			map.put("Count", objs[1]);
			for (int i = 2; i < objs.length; i++) {
				map.put(snames[i - 2], objs[i]);
			}
			result = (JSONObject) new JsonTransfer().MapToJson(map);
			log.debug(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	// ������ҳ��ִ��sql��ѯ
	class HibernateSQL implements HibernateCallback {
		String sql;

		public HibernateSQL(String sql) {
			this.sql = sql;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			List result = q.list();
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
	
	// ���ӱ�����excel������ǰ̨�������ı����ϸ
	@POST
	@Path("/excel")
	public String exportexcelpro(String values) {
		try {
			JSONObject object = new JSONObject(values);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFFont font = workbook.createFont();
			font.setColor((short) HSSFFont.COLOR_NORMAL);
			font.setBoldweight((short) HSSFFont.BOLDWEIGHT_BOLD);
			// ���ø�ʽ
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment((short) HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setFont(font);
			HSSFRow row = null;
			HSSFCell cell = null;
			JSONArray _row = object.getJSONArray("Row");
			JSONArray _column = object.getJSONArray("Column");
			JSONArray _cell = object.getJSONArray("Cell");
			// ������
			for (int i = 0; i < _row.length(); i++) {
				row = sheet.createRow((short) i);
				JSONObject r = _row.getJSONObject(i);
				// �����и�
//				row.setHeight((short) (r.getInt("Height")*20));
				for (int j = 0; j < _column.length(); j++) {
					JSONObject col = _column.getJSONObject(j);
					JSONObject item = getCell(_cell, i, j);
					if(item==null)continue;
					// �ϲ���Ԫ��
					Region region = new Region(item.getInt("Row"), (short) item
							.getInt("Column"), item.getInt("Row")
							+ item.getInt("RowSpan") - 1, (short) (item
							.getInt("Column")
							+ item.getInt("ColumnSpan") - 1));
					sheet.addMergedRegion(region);
//					// �����п�
//					sheet
//							.setColumnWidth((short) j, (short)( col
//									.getInt("Width")*20));
					cell = row.createCell((short) (j));
					// ����������
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// �����е��ַ���Ϊ����
					cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
					String Content = item.getString("Content");
					// ��������
					cell.setCellValue(Content);
					cell.setCellStyle(cellStyle);
				}
			}

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
			e.printStackTrace();
		}
		return null;
	}
	
	private JSONObject getCell(JSONArray array, int row, int column) {
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject cell = array.getJSONObject(i);
				int r = cell.getInt("Row");
				int c = cell.getInt("Column");
				if (r == row && c == column) {
					return cell;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ת��������ת���ڼ��������Ƿ��Ѿ�ת��������������ת����������ѭ��
	class JsonTransfer {
		// �����Ѿ�ת�����Ķ���
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		// �ѵ���mapת����JSON����
		public Object MapToJson(Map<String, Object> map) {
			// ת���������ؿն���
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					String key = entry.getKey();
					Object value = entry.getValue();
					// ��ֵת����JSON�Ŀն���
					if (value == null) {
						value = JSONObject.NULL;
					} else if (value instanceof HashMap) {
						value = MapToJson((Map<String, Object>) value);
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						value = ToJson(set);
					}
					// �����$type$����ʾʵ�����ͣ�ת����EntityType
					if (key.equals("$type$")) {
						json.put("EntityType", value);
					} else if (value instanceof Date) {
						Date d1 = (Date) value;
						Calendar c = Calendar.getInstance();
						long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
						json.put(key, time);
					} else if (value instanceof MapProxy) {
						// MapProxyû�м��أ�����
					} else {
						json.put(key, value);
					}
				} catch (JSONException e) {
					throw new WebApplicationException(400);
				}
			}
			return json;
		}

		// �Ѽ���ת����Json����
		public Object ToJson(PersistentSet set) {
			// û���صļ��ϵ�����
			if (!set.wasInitialized()) {
				return JSONObject.NULL;
			}
			JSONArray array = new JSONArray();
			for (Object obj : set) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
				array.put(json);
			}
			return array;
		}

		// �ж��Ѿ�ת�������������Ƿ������������
		public boolean contains(Map<String, Object> obj) {
			for (Map<String, Object> map : this.transed) {
				if (obj == map) {
					return true;
				}
			}
			return false;
		}
	}
}
