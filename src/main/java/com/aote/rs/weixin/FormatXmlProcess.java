package com.aote.rs.weixin;

import java.util.Date;
/** 
 * ��װ���յ�xml��ʽ��� 
 * @author pamchen-1 
 * 
 */  
public class FormatXmlProcess {  
    /** 
     * ��װ������ķ�����Ϣ 
     * @param to 
     * @param from 
     * @param content 
     * @return 
     */  
    public String formatXmlAnswer(String to, String from, String content) {  
        StringBuffer sb = new StringBuffer();  
        Date date = new Date();  
        sb.append("<xml><ToUserName><![CDATA[");  
        sb.append(to);  
        sb.append("]]></ToUserName><FromUserName><![CDATA[");  
        sb.append(from);  
        sb.append("]]></FromUserName><CreateTime>");  
        sb.append(date.getTime());  
        sb.append("</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[");  
        sb.append(content);  
        sb.append("]]></Content><FuncFlag>0</FuncFlag></xml>");  
        return sb.toString();  
    }  
}  