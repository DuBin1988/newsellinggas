package com.aote.rs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("file")
@Scope("prototype")
@Component
public class FileService {
	static Logger log = Logger.getLogger(FileService.class);
	
	// 获得文件内容
	@GET
	@Path("/{name}")
	public String downLoad(
			@Context HttpServletResponse response,
			@PathParam("name") String name) {
		name = name.replace('^', '\\');
		log.debug("name=" + name);
		try {
			// 清缓存
			response.setStatus(HttpServletResponse.SC_OK);
			response
					.setContentType("application/octet-stream;charset=\"gb2312\"");
			response.addHeader("Content-Disposition", "attachment;filename=\" "
					+ name + "\"");
			// 把文件的内容送入响应流中
			byte[] b = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(name));
			BufferedOutputStream bos = new BufferedOutputStream(response
					.getOutputStream());
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
	
	
	
}
