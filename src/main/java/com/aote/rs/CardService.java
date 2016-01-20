package com.aote.rs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

//������
@Path("card")
@Scope("prototype")
public class CardService {
	static Logger log = Logger.getLogger(CardService.class);

	//��ȡ���п��ļ�������޸�ʱ��
	@GET
	public String files(@QueryParam("path") String path1) {
		String result = "";
		String path = this.getClass().getClassLoader().getResource("/card").getPath();
		path="D:/card";
		log.debug(path);
		//�ݹ��ȡ�����ļ�������޸�����
		File file = new File(path);
		List<File> files = list(file);
		for(File f : files) {
			if(!result.equals("")) {
				result += "|";
			}
			result += f.getPath() + "," + f.lastModified();
		}
		log.debug(result);
		return result;
	}
	
	// ����ļ�
	@Path("{filename}")
	public String getimage(@Context HttpServletResponse response,
			@PathParam("filename") String fileName) {
		try {
			fileName = fileName.replace('^', '\\');
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/octet-stream");
			// ���ļ�������������Ӧ����
			log.debug(fileName);
			InputStream is = new FileInputStream(fileName);
			OutputStream os = new BufferedOutputStream(response
					.getOutputStream());
			transformStream(is, os);
			is.close();
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
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
	
	//�ݹ��ȡĿ¼�������ļ���������Ŀ¼
	private List<File> list(File dir) {
		List<File> result = new ArrayList<File>();
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				result.addAll(list(file));
			} else {
				result.add(file);
			}
		}
		return result;
	}
}
