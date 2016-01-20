package com.aote.rs.db;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.springframework.context.annotation.Scope;

import com.browsesoft.note.SMSSender;

/**
 * 短信猫发送短信
 * 
 * @author hyde
 * 
 */
@Path("/db/msm")
@Scope("prototype")
public class MSMService {
	// 发送短信
	@SuppressWarnings("finally")
	@GET
	@Path("{phone}/{message}")
	public String send(@PathParam("phone") String phone,
			@PathParam("message") String message) {
		String result = "true";
		try {
			SMSSender sender = SMSSender.getInstance();
			// 解析电话
			String[] phones = phone.split(",");
			for (int i = 0; i < phones.length; i++) {
				String p = phones[i];
				System.out.println("发送号码" + p);
				sender.send_pdu_chi("86" + p, message);
				Thread.currentThread().sleep(10000);
			}
		} catch (Exception e) {
			result = "false";
			e.printStackTrace();
		} finally {
			return result;
		}

	}
}
