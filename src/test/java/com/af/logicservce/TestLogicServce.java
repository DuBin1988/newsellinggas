package com.af.logicservce;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestLogicServce extends TestCase {
	public void testOne(){
		try {
			String path="http://127.0.0.1:8080/rs/logic/"+ URLEncoder.encode("name.txt").replace("+", "%20");
			// 创建POSTMethod  
			HttpPost postMethod =new HttpPost(path);/*建立HTTP Post连线*/
			StringEntity se = new StringEntity("","UTF-8");
			postMethod.setEntity(se);
			// 执行POSTMethod
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(postMethod);
			int code = response.getStatusLine().getStatusCode();
			// 如果成功
			if (code == 200) {
				String actual = EntityUtils.toString(response.getEntity(), "UTF8");
			} 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
