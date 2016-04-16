package com.aote.rs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.aote.dbf.CQA188;
import com.aote.dbf.util.DateHelper;
import com.aote.rs.charge.HandCharge;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;

@Path("DBFService")
@Component
public class DBFService {
	static Logger log = Logger.getLogger(DBFService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	// 保存抄表计划DBF
	@Path("savefile/{f_inputtor}/{type}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String savefile(
			@Context HttpServletResponse response,
			@PathParam("f_inputtor") String f_inputtor,
			@PathParam("type") String type) {
		String result = null;
		String filename=f_inputtor+"_"+DateHelper.getdateymd()+".dbf";
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
	    ServletContext servletContext = webApplicationContext.getServletContext(); 
		//得到上传文件的保存目录
		File filepath = new File(servletContext.getRealPath("/")+"files"+File.separatorChar+"dbf");
		try{
			//判断上传文件的保存目录是否存在
            if (!filepath.exists() && !filepath.isDirectory())
            		filepath.mkdirs();
            String filepaths=servletContext.getRealPath("/")+"files"+File.separatorChar+"dbf"+File.separator + filename;
            OutputStream fos = null;
            try {
            	DBFField[] fields=null;
            	//不同A188抄表适配
            	if(type.equals("CQA188"))
            		fields=CQA188.getFields();
            	//不同A188抄表适配
            	
            	
	            DBFWriter writer = new DBFWriter();
	            writer.setCharactersetName("GBK");  
	            writer.setFields(fields);
	            //写入记录
	            HandCharge hCharge= new HandCharge();
	            hCharge.setHibernateTemplate(hibernateTemplate);
	            JSONArray rows=hCharge.ReadRecordInput(f_inputtor);			
				for(int i=0;i<rows.length();i++){
					//不同A188抄表适配
					if(type.equals("CQA188"))
						writer.addRecord(CQA188.getObjects(fields.length,rows.getJSONObject(i),i+1));
					//不同A188抄表适配
					
				}  
	            fos = new FileOutputStream(filepaths); 
	            writer.write(fos);  
	            // writer.write();
            } catch (Exception e) {  
                e.printStackTrace();  
            } finally { 
                    fos.close();
            }
            //下载文件
			filename = URLEncoder.encode(filename, "UTF-8");
			InputStream is=new FileInputStream(filepaths);
			response.setHeader("Pragma","No-cache"); 
			response.setHeader("Cache-Control","no-cache"); 
			response.setDateHeader("Expires", 0); 
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\""+ filename + "\"");
			OutputStream os = new BufferedOutputStream(response.getOutputStream());
			transformStream(is, os);
			is.close();
			os.close();
			result="ok";
        }catch (Exception e) {
            e.printStackTrace();
            result="err";
        }			
		return result;
	}
	
	// 保存抄表计划DBF
	@Path("readfile/{f_inputtor}/{type}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String readfile(byte[] file,
			@PathParam("f_inputtor") String f_inputtor,
			@PathParam("type") String type) {
		JSONArray result = new JSONArray();
		try{
			InputStream fis = null;  
	        try {
	            fis = new ByteArrayInputStream(file);  
	            DBFReader reader = new DBFReader(fis);  
	            reader.setCharactersetName("GBK");
	            Object[] rowValues;
	            // 文件中记录
	            JSONArray rows=new JSONArray();
	            while ((rowValues = reader.nextRecord()) != null) {
	            	JSONObject obj=new JSONObject();
	            	try {
	            		for (int i = 0; i < rowValues.length; i++) {	                
		                	//不同A188抄表适配
		                	if(type.equals("CQA188")){
		                	   if(i==2)
			            		   obj.put("f_gasdate", rowValues[2].toString().trim());
		            		   if(i==9)
		            			   obj.put("userid", rowValues[9].toString().trim());
		            		   if(i==11)
		            			   obj.put("reading", rowValues[11]);
		            		   if(i==12)
		            			   obj.put("lastreading", rowValues[12]);
		                	}
		                	//不同A188抄表适配
		            	   
		                }
		                if(obj.getDouble("reading")>=obj.getDouble("lastreading"))
		                	rows.put(obj);
		                else
		                	result.put(obj);
		                //保存抄表指数一份档案
		    			execSQL("insert into t_cbnum (f_userid,f_gasdate,f_gasnum,jval,f_inertdate,f_meternumber,f_gaswatchbrand,f_aliasname) "
								+ "(select '"+obj.getString("userid")+"','"+obj.getString("f_gasdate")+"',"+obj.getDouble("reading")+","+0+",'"+DateHelper.getdatetime()+"',f_meternumber,f_gaswatchbrand,f_aliasname from t_userfiles where f_userid='"+obj.getString("userid")+"')");
					} catch (Exception e) {
					}	                
	            }
	            //自动下账
	            HandCharge hCharge= new HandCharge();
	            hCharge.setHibernateTemplate(hibernateTemplate);
	            Date now = new Date();
	    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		SimpleDateFormat handdate = new SimpleDateFormat("yyyy-MM");
	    		//SimpleDateFormat cbdate = new SimpleDateFormat("yyyy-MM-dd");
	    		hCharge.afRecordInputForMore(rows.toString(),"天燃气公司",f_inputtor,fmt.format(now),handdate.format(now),"正常使用","天燃气公司");
	        } catch (Exception e) {
	            e.printStackTrace();  
	        } finally {
	        	fis.close();
	        }
        }catch (Exception e) {
            e.printStackTrace();
        }			
		return result.toString();
	}
	
	public void transformStream(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[1024];
			// 读取的实际长度
			int length = is.read(buffer);
			while (length != -1) {
				os.write(buffer, 0, length);
				length = is.read(buffer);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * execute sql in hibernate
	 * @param sql
	 */
	private void execSQL(final String sql) {
		try {
			hibernateTemplate.execute(new HibernateCallback() {
	            public Object doInHibernate(Session session)
	                    throws HibernateException {
	                session.createSQLQuery(sql).executeUpdate();
	                return null;
	            }
	        });
		} catch (Exception e) {
		}        		
	}
	
}