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
	 * 根据文件名获得报表的配置信息
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
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return result;
	}

	@POST
	@Path("sql/{pageIndex}/{pageSize}")
	// 按sql方式执行后，获取一页数据，字段名由SQL语句决定
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
	// 求总数
	public JSONObject postSQLSum(@PathParam("sumNames") String names,
			String query) {
		JSONObject result = null;
		try {
			// 只取第一条sql
			JSONArray array = new JSONArray(query);
			JSONObject object = array.getJSONObject(0);
			query = object.getString("sql").replace("$", "'");
			// 组织sums串
			String sums = "";
			String[] snames = names.split(",");
			for (String name : snames) {
				if (!sums.equals(""))
					sums += ",";
				sums += "sum(" + name + ") as " + name;
			}
			String sql = "";
			if (!sums.equals("")) {
				// 前面多添加一个count，以便统一返回数组，而不是单个对象
				sql = "select count(*) as Count, count(*) as Count, " + sums
						+ " from (" + query + ") t";
			} else {
				// 前面多添加一个count，以便统一返回数组，而不是单个对象
				sql = "select count(*) as Count, count(*) as Count "
						+ " from (" + query + ") t";
			}
			log.debug(sql);
			HibernateSQL call = new HibernateSQL(sql);
			List list = (List) hibernateTemplate.execute(call);
			// 把map转换成json对象
			Object[] objs = (Object[]) list.get(0);
			Map<String, Object> map = new HashMap<String, Object>();
			// 先把Count放进去，头一个抛弃掉
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

	// 不带分页的执行sql查询
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

	// 执行sql分页查询，结果集形式可以设置
	class HibernateSQLCall implements HibernateCallback {
		String sql;
		int page;
		int rows;
		// 查询结果转换器，可以转换成Map等。
		public ResultTransformer transformer = null;

		public HibernateSQLCall(String sql, int page, int rows) {
			this.sql = sql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			// 有转换器，设置转换器
			if (transformer != null) {
				q.setResultTransformer(transformer);
			}
			List result = q.setFirstResult(page * rows).setMaxResults(rows)
					.list();
			return result;
		}
	}
	
	// 复杂报表导出excel，根据前台传过来的表格明细
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
			// 设置格式
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment((short) HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setFont(font);
			HSSFRow row = null;
			HSSFCell cell = null;
			JSONArray _row = object.getJSONArray("Row");
			JSONArray _column = object.getJSONArray("Column");
			JSONArray _cell = object.getJSONArray("Cell");
			// 创建行
			for (int i = 0; i < _row.length(); i++) {
				row = sheet.createRow((short) i);
				JSONObject r = _row.getJSONObject(i);
				// 设置行高
//				row.setHeight((short) (r.getInt("Height")*20));
				for (int j = 0; j < _column.length(); j++) {
					JSONObject col = _column.getJSONObject(j);
					JSONObject item = getCell(_cell, i, j);
					if(item==null)continue;
					// 合并单元格
					Region region = new Region(item.getInt("Row"), (short) item
							.getInt("Column"), item.getInt("Row")
							+ item.getInt("RowSpan") - 1, (short) (item
							.getInt("Column")
							+ item.getInt("ColumnSpan") - 1));
					sheet.addMergedRegion(region);
//					// 设置列宽
//					sheet
//							.setColumnWidth((short) j, (short)( col
//									.getInt("Width")*20));
					cell = row.createCell((short) (j));
					// 设置列类型
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// 设置列的字符集为中文
					cell.setEncoding((short) HSSFCell.ENCODING_UTF_16);
					String Content = item.getString("Content");
					// 设置内容
					cell.setCellValue(Content);
					cell.setCellStyle(cellStyle);
				}
			}

			// 产生临时文件
			File file = File.createTempFile("temp", ".xls");
			file.deleteOnExit();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// 写文件
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			// 返回文件名
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

	// 转换器，在转换期间会检查对象是否已经转换过，避免重新转换，产生死循环
	class JsonTransfer {
		// 保存已经转换过的对象
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		// 把单个map转换成JSON对象
		public Object MapToJson(Map<String, Object> map) {
			// 转换过，返回空对象
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					String key = entry.getKey();
					Object value = entry.getValue();
					// 空值转换成JSON的空对象
					if (value == null) {
						value = JSONObject.NULL;
					} else if (value instanceof HashMap) {
						value = MapToJson((Map<String, Object>) value);
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						value = ToJson(set);
					}
					// 如果是$type$，表示实体类型，转换成EntityType
					if (key.equals("$type$")) {
						json.put("EntityType", value);
					} else if (value instanceof Date) {
						Date d1 = (Date) value;
						Calendar c = Calendar.getInstance();
						long time = d1.getTime() + c.get(Calendar.ZONE_OFFSET);
						json.put(key, time);
					} else if (value instanceof MapProxy) {
						// MapProxy没有加载，不管
					} else {
						json.put(key, value);
					}
				} catch (JSONException e) {
					throw new WebApplicationException(400);
				}
			}
			return json;
		}

		// 把集合转换成Json数组
		public Object ToJson(PersistentSet set) {
			// 没加载的集合当做空
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

		// 判断已经转换过的内容里是否包含给定对象
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
