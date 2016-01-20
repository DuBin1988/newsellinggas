package com.aote.rs;

import java.net.URLEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * JSON service 调用隧道
 * 
 * @author lgy
 *
 */
@Path("tunnel")
@Scope("prototype")
@Component
public class Tunnel {
	static Logger log = Logger.getLogger(DBService.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test() {
		return "Hello Tunnel";
	}
	/**
	 * 只处理get方式请求
	 * @return
	 */
	@GET
	@Path("{url}")
	@Produces(MediaType.APPLICATION_JSON)
	public String urlFallThrough(@PathParam("url") String url) {
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    try {
	      url = url.replace("|", "/");
	      String host = url.substring(0, url.lastIndexOf('/')+1);
	      url = host + URLEncoder.encode(url.substring(url.lastIndexOf('/')+1), "UTF8").replace("+", "%20");
	      HttpGet getRequest = new HttpGet(url);
	      HttpResponse httpResponse = httpclient.execute(getRequest);
	      HttpEntity entity = httpResponse.getEntity();
	      if(entity != null)
	    	  return EntityUtils.toString(entity,"UTF8");
	      else
	    	  return "";

	    } catch (Exception e) {
	    	throw new WebApplicationException(400);
	    } finally {
	      httpclient.getConnectionManager().shutdown();
	    }
	}

	@GET
	@Path("{pattern}/{payload}")
	@Produces(MediaType.APPLICATION_JSON)
	public String urlFallThrough(@PathParam("pattern") String host, @PathParam("payload") String sql) {
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    try {
	      host = host.replace("|", "/");
	      sql = URLEncoder.encode(sql, "UTF-8").replace("+", "%20");
	      sql = sql.replace("%7C", "/").replace("%3F", "?");
	      String url = host + sql;
	      HttpGet getRequest = new HttpGet(url);
	      HttpResponse httpResponse = httpclient.execute(getRequest);
	      HttpEntity entity = httpResponse.getEntity();
	      if(entity != null)
	    	  return EntityUtils.toString(entity,"UTF8");
	      else
	    	  return "";

	    } catch (Exception e) {
	    	throw new WebApplicationException(400);
	    } finally {
	      httpclient.getConnectionManager().shutdown();
	    }
	}
	
}
