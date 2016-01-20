package com.browsesoft.note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class Log {
	public Log() {
	}

	public static void log(MSM message, boolean isSuccessful) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("YYYY年MM月DD日 hh:mm:ss");
		String time = format.format(new Date());
		String id = message.getID();
		String receiver = message.getReceiverUser();
		String sender = message.getSendUserName();
		String senderNum = message.getSendUserNum();
		format = new SimpleDateFormat("YYYY年MM月DD日");
		String date = format.format(new Date());
		File file = new File("./log/" + date + ".log");
		if (!file.exists()) {
			file.mkdirs();
			file.createNewFile();
		}
		OutputStream os = new FileOutputStream(file, true);
		String content = time + "  " + sender + "  " + senderNum + "  "
				+ receiver + "  " + id + "  " + message + "\n";
		os.write(content.getBytes());
		os.flush();
		os.close();
	}
}