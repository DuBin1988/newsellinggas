package com.tencent.common;

import com.thoughtworks.xstream.XStream;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * User: rizenguo
 * Date: 2014/10/23
 * Time: 14:59
 */
public class Util {

    //��log��
    private static Log logger = new Log(LoggerFactory.getLogger(Util.class));

    /**
     * ͨ������ķ�ʽ������������Ժ�����ֵ���������
     *
     * @param o Ҫ�����Ķ���
     * @throws Exception
     */
    public static void reflect(Object o) throws Exception {
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            Util.log(f.getName() + " -> " + f.get(o));
        }
    }

    public static byte[] readInput(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


    public static InputStream getStringStream(String sInputString) {
        ByteArrayInputStream tInputStringStream = null;
        if (sInputString != null && !sInputString.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
        }
        return tInputStringStream;
    }

    public static Object getObjectFromXML(String xml, Class tClass) {
        //����API���ص�XML����ӳ�䵽Java����
        XStream xStreamForResponseData = new XStream();
        xStreamForResponseData.alias("xml", tClass);
        xStreamForResponseData.ignoreUnknownElements();//��ʱ���Ե�һЩ�������ֶ�
        return xStreamForResponseData.fromXML(xml);
    }

    public static String getStringFromMap(Map<String, Object> map, String key, String defaultValue) {
        if (key == "" || key == null) {
            return defaultValue;
        }
        String result = (String) map.get(key);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    public static int getIntFromMap(Map<String, Object> map, String key) {
        if (key == "" || key == null) {
            return 0;
        }
        if (map.get(key) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key));
    }

    /**
     * ��log�ӿ�
     * @param log Ҫ��ӡ��log�ַ���
     * @return ����log
     */
    public static String log(Object log){
        logger.i(log.toString());
        //System.out.println(log);
        return log.toString();
    }

    /**
     * ��ȡ���ص�xml���ݣ�һ�������Բ���
     * @param localPath ����xml�ļ�·��
     * @return ������xml�ַ���
     */
    public static String getLocalXMLString(String localPath) throws IOException {
        return Util.inputStreamToString(Util.class.getResourceAsStream(localPath));
    }

}

