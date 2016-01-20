package com.aote.rs;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Fields;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.codehaus.jettison.json.JSONObject;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

@Path("wordmb")
@Component
public class DOCReplaceText {
	
	static Logger log = Logger.getLogger(DOCReplaceText.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	WebApplicationContext webApplicationContext =null;
	ServletContext servletContext = null;

		
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
	
	/**
	 * execute sql in hibernate
	 * @param sql 返回影响的条数
	 */
	private Object execSQLnum(final String sql) {
		try {
			return hibernateTemplate.execute(new HibernateCallback() {
				 public Object doInHibernate(Session session)
		                    throws HibernateException {
					 return session.createSQLQuery(sql).executeUpdate();
		            }
	        });
		} catch (Exception e) {
			return null;
		}		
	}
	
	/**
	 * List
	 * execute sql in hibernate
	 * @param sql 
	 */
	private List relList(final String sql) {
		try {
			return (List)hibernateTemplate.execute(new HibernateCallback() {
				public Object doInHibernate(Session session)throws HibernateException {
					SQLQuery query = session.createSQLQuery(sql);
					return query.list();
				}
			});
		} catch (Exception e) {
			return null;
		}		
	}

	/*
	@Path("thdoc")
	@POST
	@Produces("application/json")
	public String docmbp(@Context HttpServletResponse response,String Obj,
			@QueryParam("mbName") String mbName,@QueryParam("htcode") String htcode,@QueryParam("fileds") String fileds) throws Exception{
		log.debug("thdoc：" + Obj);
		//应用根路径
		webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
	    servletContext = webApplicationContext.getServletContext();
		File filepath = new File(servletContext.getRealPath("/")+"word"+File.separatorChar+"mb");
		File filepathnews = new File(servletContext.getRealPath("/")+"word"+File.separatorChar+"news");
        //判断上传文件的保存目录是否存在
        if (!filepath.exists() && !filepath.isDirectory())
        			filepath.mkdirs();   
        if (!filepathnews.exists() && !filepathnews.isDirectory())
        			filepathnews.mkdirs(); 
        try{
        	FileOutputStream out = new FileOutputStream(filepathnews + File.separator +htcode+".doc");
        	HWPFDocument doc = new HWPFDocument(new POIFSFileSystem(new FileInputStream(filepath + File.separator + mbName+".doc")));
            saveWord(out, replaceCharTextp(doc, fileds, Obj));
           	response.setStatus(HttpServletResponse.SC_OK);
 			response.setContentType("application/msword");
 			response.setHeader("Content-Disposition", "attachment;filename=\""+ htcode+".doc" + "\"");
 			// 把文件的内容送入响应流中
 			InputStream is = new FileInputStream(filepathnews + File.separator +htcode+".doc");
 			OutputStream os = new BufferedOutputStream(response.getOutputStream());
 			transformStream(is, os);
 			is.close();
 			os.close();
        }catch (Exception e) {
            e.printStackTrace();
            return "err";
        }			
		return "ok";
	} */
	
	@Path("thdoc/{mbName}/{htcode}/{fileds}/{table}")
	@GET
	@Produces("application/json")
	public String docmbg(@Context HttpServletResponse response,@PathParam("mbName") String mbName,
			@PathParam("htcode") String htcode,
			@PathParam("fileds") String fileds,
			@PathParam("table") String table) throws Exception{
		log.debug("name：" + htcode+"_"+mbName);
//		Date now = new Date();
//		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
//		String docdate=fmt.format(now);
		//应用根路径
		webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
	    servletContext = webApplicationContext.getServletContext();
		File filepath = new File(servletContext.getRealPath("/")+"word"+File.separatorChar+"mb");
		File filepathnews = new File(servletContext.getRealPath("/")+"word"+File.separatorChar+"news");
        //判断上传文件的保存目录是否存在
        if (!filepath.exists() && !filepath.isDirectory())
        			filepath.mkdirs();   
        if (!filepathnews.exists() && !filepathnews.isDirectory())
        			filepathnews.mkdirs(); 
        try{
        	FileOutputStream out = new FileOutputStream(filepathnews + File.separator +htcode+"_"+mbName+".doc");
        	HWPFDocument doc = new HWPFDocument(new POIFSFileSystem(new FileInputStream(filepath + File.separator + mbName+".doc")));
            saveWord(out, replaceCharTextg(doc, fileds, table,htcode));
           	response.setStatus(HttpServletResponse.SC_OK);
 			response.setContentType("application/msword");
 			response.setHeader("Content-Disposition", "attachment;filename=\""+ htcode+"_"+mbName+".doc" + "\"");
 			// 把文件的内容送入响应流中
 			InputStream is = new FileInputStream(filepathnews + File.separator +htcode+"_"+mbName+".doc");
 			OutputStream os = new BufferedOutputStream(response.getOutputStream());
 			transformStream(is, os);
 			is.close();
 			os.close();
        }catch (Exception e) {
            e.printStackTrace();
            return "err";
        }			
		return "ok";
	}
	
	//获得文件内容
	@GET
	@Path("{mbName}/{htcode}")
	public String downLoad(@Context HttpServletResponse response,@PathParam("htcode") String htcode,@PathParam("mbName") String mbName) {
		htcode = htcode.replace('^', '\\');
		log.debug("name=" + htcode+"_"+mbName);
		try {
			webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
		    servletContext = webApplicationContext.getServletContext();
			File filepathnews = new File(servletContext.getRealPath("/")+"word"+File.separatorChar+"news");
			// 清缓存
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/msword;charset=\"gb2312\"");
			response.addHeader("Content-Disposition", "attachment;filename=\""+ htcode+"_"+mbName+".doc" + "\"");
			//把文件的内容送入响应流中
			byte[] b = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filepathnews + File.separator +htcode+"_"+mbName+".doc"));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			while (bis.read(b) != -1) {
				bos.write(b);
			}
			bis.close();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
    //保存方法
    private void saveWord(FileOutputStream out, HWPFDocument doc) throws FileNotFoundException, IOException{
        try{
            doc.write(out);
        }
        finally{
            out.close();
        }
    }    
    
    //替换字符方法 post
    private HWPFDocument replaceCharTextp(HWPFDocument doc, String fileds, String replaceText) throws Exception{
    	Map map = this.findsinglevalue(fileds);
		if(map != null){
			JSONObject row = new JSONObject(replaceText);
			String[] old=map.get("value").toString().split(",");			
	        Range r1 = doc.getRange();
	        for(int i=0;i<old.length;i++){	        	
	        	r1.replaceText("<!--"+old[i]+"-->", row.getString(old[i]));
	        }
		}
        return doc;
    }
    
  //替换字符方法,get
    private HWPFDocument replaceCharTextg(HWPFDocument doc, String fileds, String table, String htcode) throws Exception{
    	Map map = this.findsinglevalue(fileds);
		if(map != null){
			String[] old=map.get("value").toString().split(",");
			List list=relList("select "+map.get("value").toString()+" from "+table+" where "+old[0]+"='"+htcode+"'");
			if (null != list && list.size() >= 1){
				Object[] data=(Object[])list.get(0);
				 Range r1 = doc.getRange();
			     for(int i=1;i<old.length;i++){	        	
			    	 r1.replaceText("<!--"+old[i]+"-->", (data[i]==null)?"":(data[i]+"").replace(" 00:00:00.0", ""));
			     }
			}	       
		}
        return doc;
    }
    
  	//查找单值
  	private Map<String, Object> findsinglevalue(String name) {
  			String singlevalue = "from t_singlevalue where name='" + name + "'";
  			List<Object> singlevalueList = this.hibernateTemplate.find(singlevalue);
  			if (singlevalueList.size() != 1) {
  				return null;
  			}
  			return (Map<String, Object>) singlevalueList.get(0);
  	}
  	
  	//读取数据
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
  	
}
