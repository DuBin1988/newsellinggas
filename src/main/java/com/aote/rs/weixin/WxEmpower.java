package com.aote.rs.weixin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import org.codehaus.jettison.json.JSONObject;

/**
 * �豸��Ȩ
 * @author Administrator
 *
 */
public class WxEmpower {
	public static String product_id = "28640"; // �����˺ŵĲ�Ʒid����ʽ����Ҫ�ĵ�
	public static String deviceid = "";
	public static String appid = "wx792911e3847493a9";
	public static String appsecret = "69a3f6984900192382e6cd08bac34d45";
	public static String mac="884AEA45CBE8";
	// ����access_token����Чʱ��Ϊ7000�룩��product_id������豸ʱ�������ˣ�
	/**
	 * ��ȡdeviceid
	 * @return deviceid ��������ִ����ǣ���΢�ŷ��صĴ�����Ϣ����
	 */
	public static String getDeviceid() {
		// ��ȡ��ǰʱ����������ж�access_token�Ƿ����
		String newtime = WxSign.getTimeStamp();
		String oldtime = WxCertificate.oldtime;
		int timediffent = Integer.valueOf(newtime) - Integer.valueOf(oldtime);
		System.out.println("�����ʱ���Ϊ-->" + timediffent);
		// ʧЧ��������
		String access_token  = "";
		if(timediffent > 7000) {
			access_token  = WxCertificate.getToken(appid, appsecret);
		}else{
			access_token  = WxCertificate.token;
		}
		
		String url = "https://api.weixin.qq.com/device/getqrcode?"
				+ "access_token="+access_token 
				+ "&product_id="+product_id;
		
		try {
			JSONObject obj = WxCertificate.doGet(url);
			if(obj.getJSONObject("base_resp").getString("errmsg") == "ok") {
				return "������" + obj.getJSONObject("base_resp").getString("errcode") 
						+ "������Ϣ" + obj.getJSONObject("base_resp").getString("errmsg");
			}
			deviceid = obj.getString("deviceid");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("��Ȩ�豸����deviceidʱ�쳣");
		}
		return deviceid;
	}
	
	/**
	 * �豸��Ȩ
	 * @param access_token
	 * @param deviceId
	 * @return
	 */
	public void empower(String access_token, String deviceId) {
        String params="{\"device_num\":\"1\",\"device_list\":[{"
                   +"\"id\":\""+deviceId+"\","
                   +"\"mac\":\""+mac+"\","
                    +"\"connect_protocol\":\"3\","
                    +"\"auth_key\":\"\","
                    +"\"close_strategy\":\"1\","
                    +"\"conn_strategy\":\"1\","
                    +"\"crypt_method\":\"0\","
                    +"\"auth_ver\":\"0\","
                    +"\"manu_mac_pos\":\"-1\","
                    +"\"ser_mac_pos\":\"-2\","
                    +"\"ble_simple_protocol\": \"0\""
                    + "}],"
                    +"\"op_type\":\"1\""
                   + "}";
                   
              String s=sendPost("https://api.weixin.qq.com/device/authorize_device?access_token="+access_token, params);
                  System.out.println("���أ�"+s);
	}
	
	public String sendPost(String requrl,String param){
        URL url;
         String sTotalString="";  
       try {
           url = new URL(requrl);
            URLConnection connection = url.openConnection(); 
             
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "text/xml");
           // connection.setRequestProperty("Content-Length", body.getBytes().length+"");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
             
             
               connection.setDoOutput(true);  
               OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");  
               out.write(param); // ��ҳ�洫�����ݡ�post�Ĺؼ����ڣ�  
               out.flush();  
               out.close();  
               // һ�����ͳɹ��������·����Ϳ��Եõ��������Ļ�Ӧ��  
               String sCurrentLine;  
              
               sCurrentLine = "";  
               sTotalString = "";  
               InputStream l_urlStream;  
               l_urlStream = connection.getInputStream();  
               // ��˵�е������װ����  
               BufferedReader l_reader = new BufferedReader(new InputStreamReader(  
                       l_urlStream));  
               while ((sCurrentLine = l_reader.readLine()) != null) {  
                   sTotalString += sCurrentLine + "\r\n";  
          
               }  
                
	       } catch (Exception e) {
	           e.printStackTrace();
	           throw new RuntimeException("�豸��Ȩ�쳣");
	       }  
	           
	           System.out.println(sTotalString);  
	           return sTotalString;
	    }
	}
