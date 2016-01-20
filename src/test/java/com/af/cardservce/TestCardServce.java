package com.af.cardservce;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import junit.framework.TestCase;

public class TestCardServce extends TestCase {

	public void testOne(){
		try
		{
			String path="http://127.0.0.1:8080/rs/card?path=" + URLEncoder.encode("d:\\card").replace("+", "%20");
			HttpGet getMethod = new HttpGet(path);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse res = httpClient.execute(getMethod);
			int code = res.getStatusLine().getStatusCode();
			if (code == 200) {
				String strResult = EntityUtils.toString(res.getEntity(), "UTF8");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}

