package com.aote.rs.db;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.springframework.context.annotation.Scope;

import com.browsesoft.note.SMSSender;

/**
 * ����è���Ͷ���
 * 
 * @author hyde
 * 
 */
@Path("/db/msm")
@Scope("prototype")
public class MSMService {
	// ���Ͷ���
	@SuppressWarnings("finally")
	@GET
	@Path("{phone}/{message}")
	public String send(@PathParam("phone") String phone,
			@PathParam("message") String message) {
		String result = "true";
		try {
			SMSSender sender = SMSSender.getInstance();
			// �����绰
			String[] phones = phone.split(",");
			for (int i = 0; i < phones.length; i++) {
				String p = phones[i];
				System.out.println("���ͺ���" + p);
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
