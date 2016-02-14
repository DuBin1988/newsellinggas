package com.aote.rs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * �ļ������࣬����
 * 
 * @author Administrator
 *
 */
public class FileHelper {

	private static FileHelper self = new FileHelper();

 
	private FileHelper() {

	}

 

	/**
	 * ������ʱ�ļ����ú���Ϊϵͳ�ں������̶���t_blob���л�ȡ������
	 */
	public  static String createFile(String fileFullPathName, byte[] fileContent)
			throws Exception {
		File file = new File(fileFullPathName);
		if(!file.exists())
		{
			file.createNewFile();
		}
		FileOutputStream fileOutStream = new FileOutputStream(file);
		fileOutStream.write(fileContent);
		fileOutStream.flush();
		fileOutStream.close();
		return file.getName();
	}
	
	
	/*���ļ�
	 */
	
	public static String readToEnd(String fn) throws Exception
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
		int numRead = 0;
		StringBuffer sb = new StringBuffer();
		while(true){
			String line = in.readLine();
			if(line == null)
				break;
			else
			{
				if(line.trim().length() == 0)
					continue;
				sb.append(line);
			}
		}
		
		
		
		in.close();
		return sb.toString();

	}

	

}
