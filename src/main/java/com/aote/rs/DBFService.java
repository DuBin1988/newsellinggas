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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

	// ���泭��ƻ�DBF
	@Path("savefile/{f_inputtor}/{type}")
	@GET
	public String savefile(
			@Context HttpServletResponse response,
			@PathParam("f_inputtor") String f_inputtor,
			@PathParam("type") String type) {
		String result = null;
		String filename=f_inputtor+"_"+DateHelper.getdateymd()+".dbf";
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
	    ServletContext servletContext = webApplicationContext.getServletContext(); 
		//�õ��ϴ��ļ��ı���Ŀ¼
		File filepath = new File(servletContext.getRealPath("/")+"files"+File.separatorChar+"dbf");
		try{
			//�ж��ϴ��ļ��ı���Ŀ¼�Ƿ����
            if (!filepath.exists() && !filepath.isDirectory())
            		filepath.mkdirs();
            String filepaths=servletContext.getRealPath("/")+"files"+File.separatorChar+"dbf"+File.separator + filename;
            OutputStream fos = null;
            try {
            	DBFField[] fields=null;
            	//��ͬA188��������
            	if(type.equals("CQA188"))
            		fields=CQA188.getFields();
            	//��ͬA188��������
            	
            	
	            DBFWriter writer = new DBFWriter();
	            writer.setCharactersetName("GBK");  
	            writer.setFields(fields);
	            //д���¼
	            HandCharge hCharge= new HandCharge();
	            hCharge.setHibernateTemplate(hibernateTemplate);
	            JSONArray rows=hCharge.ReadRecordInput(f_inputtor);			
				for(int i=0;i<rows.length();i++){
					//��ͬA188��������
					if(type.equals("CQA188"))
						writer.addRecord(CQA188.getObjects(fields.length,rows.getJSONObject(i),i+1));
					//��ͬA188��������
					
				}  
	            fos = new FileOutputStream(filepaths); 
	            writer.write(fos);  
	            // writer.write();
            } catch (Exception e) {  
                e.printStackTrace();  
            } finally { 
                    fos.close();
            }
            //�����ļ�
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
	
	// ���泭��ƻ�DBF
	@Path("readfile/{f_inputtor}/{type}")
	@POST
	public String readfile(byte[] file,
			@PathParam("f_inputtor") String f_inputtor,
			@PathParam("type") String type) {
		String result = null;
		try{
			InputStream fis = null;  
	        try {
	            fis = new ByteArrayInputStream(file);  
	            DBFReader reader = new DBFReader(fis);  
	            reader.setCharactersetName("GBK");
	            Object[] rowValues;
	            // �ļ��м�¼
	            JSONArray rows=new JSONArray();
	            while ((rowValues = reader.nextRecord()) != null) {
	            	JSONObject obj=new JSONObject();
	                for (int i = 0; i < rowValues.length; i++) {	                
	                	//��ͬA188��������
	                	if(type.equals("CQA188")){
	            		   if(i==9)
	            			   obj.put("userid", rowValues[9].toString().trim());
	            		   if(i==11)
	            			   obj.put("reading", rowValues[11]);
	            		   if(i==12)
	            			   obj.put("lastreading", rowValues[12]);
	                	}
	                	//��ͬA188��������
	            	   
	                }
	                rows.put(obj);
	            }
	            //�Զ�����
	            HandCharge hCharge= new HandCharge();
	            hCharge.setHibernateTemplate(hibernateTemplate);
	            Date now = new Date();
	    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		SimpleDateFormat handdate = new SimpleDateFormat("yyyy-MM");
	    		//SimpleDateFormat cbdate = new SimpleDateFormat("yyyy-MM-dd");
	    		hCharge.afRecordInputForMore(rows.toString(),"��ȼ����˾",f_inputtor,fmt.format(now),handdate.format(now),"����ʹ��","��ȼ����˾");
	        } catch (Exception e) {
	            e.printStackTrace();  
	        } finally {
	        	fis.close();
	        }  
			result="ok";
        }catch (Exception e) {
            e.printStackTrace();
            result="err";
        }			
		return result;
	}
	
	public void transformStream(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[1024];
			// ��ȡ��ʵ�ʳ���
			int length = is.read(buffer);
			while (length != -1) {
				os.write(buffer, 0, length);
				length = is.read(buffer);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}