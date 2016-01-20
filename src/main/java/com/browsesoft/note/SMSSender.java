/**
 *
 *  SMSSender处理短消息的发送。
 *  里面针对PDU和char格式的短消息分别提供了方法。
 *	其中PDU格式涉及到了电话号码和文本的分别转化工作，也增添了两个私有方法。
 *
 *
 *
 */
package com.browsesoft.note;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.JTextArea;

public class SMSSender {
	private static int MAXRECVSIZE = 1024;

	private byte[] recvBuffer = new byte[MAXRECVSIZE]; // 接收缓冲区

	private int recvpos; // 接收缓冲区指针

	private String recvMessage = ""; // 接收到的字符串

	public CommPortIdentifier portID;

	private SerialPort comport;

	private OutputStream out;

	private InputStream in;

	// String pdu_str = SCA + PDU_MR + DA + PID + DCS + VP + UDL + UD + CTRL_Z;
	String SCA = "00";

	// String SCA = "0891683108200905F0";

	String PDU_MR = "1100";

	String DA = ""; // 包括手机号的长度（数个数，一般是11位：0B），地址类型和号码。长度一般为8

	String PID = "00";

	String DCS = "08"; // 中文编码

	String VP = "A7";

	String UDL = ""; // 占用一个字节

	String UD = "";

	String CTRL_Z = "\u001A";

	private static SMSSender SMSSender = null;

	public static SMSSender getInstance() {
		if (SMSSender == null) {
			SMSSender = new SMSSender();
			SMSSender.init("COM4");
		}
		return SMSSender;
	}

	public boolean init(String comportName) {
		try {
			// 获取指定端口
			portID = CommPortIdentifier.getPortIdentifier(comportName);
			comport = (SerialPort) portID.open("Test", 2000);
			// 设定端口的通讯参数
			// 参数：波特率，数据位，停止位，校验位
			comport.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			out = comport.getOutputStream();
			in = comport.getInputStream();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}

	// 发送汉字格式的SMS
	public void send_pdu_chi(String phone, String message, JTextArea area) {
		DA = getUpperCaseHexString(phone.length()) + "81" + convertData(phone);
		UDL = getHexUDLength(message);
		UD = getHexUD(message);
		// 长度计算法则是13＋用户数据长度UDL再加UDL本身。即14＋UDL。
		String atcmgs = "AT+CMGS=" + (14 + message.length() * 2) + "\r";
		String pdu_str = SCA + PDU_MR + DA + PID + DCS + VP + UDL + UD + CTRL_Z;
		try {
			out.write("AT+CMGF=0\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			area.append(getMessage() + "\n");
			out.write(atcmgs.getBytes()); // 这个要计算。除了SCA都是
			waitMessage(">".getBytes(), 2000);
			area.append(getMessage() + "\n");
			out.write(pdu_str.getBytes());
			out.flush();
			waitMessage("+CMGS".getBytes(), 7000);
			area.append(getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public synchronized void send_pdu_chi(String phone, String message) {
		DA = getUpperCaseHexString(phone.length()) + "91" + convertData(phone);
		UDL = getHexUDLength(message);
		UD = getHexUD(message);
		// 长度计算法则是13＋用户数据长度UDL再加UDL本身。即14＋UDL。
		String atcmgs = "AT+CMGS=" + (15 + message.length() * 2) + "\r";
		String pdu_str = SCA + PDU_MR + DA + PID + DCS + VP + UDL + UD + CTRL_Z
				+ '\r';
		System.out.println(atcmgs);
		try {
			out.write("AT+CMGF=0\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			System.out.println(getMessage());
			out.write(atcmgs.getBytes()); // 这个要计算。除了SCA都是
			waitMessage(">".getBytes(), 1000);
			System.out.println(getMessage());
			out.write(pdu_str.getBytes());
			out.flush();
			waitMessage("+CMGS".getBytes(), 4000);
			System.out.println(getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void send_pdu_eng(String phone, String message, JTextArea area) {
	}

	public void send_pdu_eng(String phone, String message) {
	}

	/**
	 * 下面这个方法不处理针对GUI平台的相关输出操作。
	 * 
	 */
	public void send_Text(String phonenumber, String message) {
		try {
			out.write("AT+CMGF=1\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			out.write(("AT+CMGS=" + phonenumber + "\r").getBytes());
			waitMessage("<".getBytes(), 2000);
			out.write((message + "\u001A").getBytes());
			out.flush();
			waitMessage("+CMGS".getBytes(), 7000);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 下面这个方法处理GUI平台的相关输出操作,JTextArea can be null.
	 */
	public void send_Text(String phonenumber, String message, JTextArea area) {
		try {
			if (area == null) {
				send_Text(phonenumber, message);
				return;
			} else {
				out.write("AT+CMGF=1\r".getBytes());
				waitMessage("OK".getBytes(), 2000);
				area.append(getMessage() + "\n");
				out.write(("AT+CMGS=" + phonenumber + "\r").getBytes());
				waitMessage("<".getBytes(), 2000);
				area.append(getMessage() + "\n");
				out.write((message + "\u001A").getBytes());
				out.flush();
				waitMessage("+CMGS".getBytes(), 7000);
				area.append(getMessage() + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	// 把SCA和DA转换成正确形式。这里都假设前面有‘86’两字,经测试通过。
	private String convertData(String number) {
		String num = new String();
		if (number.length() % 2 == 1) {
			number = number + "F";
			for (int i = 0; i < number.length(); i = i + 2)
				num += Character.toString(number.charAt(i + 1))
						+ Character.toString(number.charAt(i));
			return num;
		} else {
			for (int i = 0; i < number.length(); i = i + 2)
				num += Character.toString(number.charAt(i + 1))
						+ Character.toString(number.charAt(i));
			return num;
		}
	}

	/**
	 * 由名称也可以看出来，这个方法是针对计算用户数据的长度用的。编程思想是这样的:
	 * 我是以统一码的角度来写的。如果是其他的想七位八位编码方式这个函数就不成立。
	 * 由于Integer.toHexString()返回的十六进制是小写形式如0a而不是如0A所以就在switch 中进行了转换。
	 */
	private String getHexUDLength(String number) {
		int num = number.length() * 2;
		String hexStr = getUpperCaseHexString(num);
		return hexStr;
	}

	/*
	 * 这个函数只是把Integer.toHexString(int)转换过来的像0a这种形式转换成0A这种形式。
	 * 同时如果十六进制长度为奇数，就在最前面加0。
	 */
	private String getUpperCaseHexString(int num) {
		String hexNum = Integer.toHexString(num);
		String hexStr = "";
		for (int i = 0; i < hexNum.length(); i++)
			switch (hexNum.charAt(i)) {
			case 'a':
				hexStr += "A";
				break;
			case 'b':
				hexStr += "B";
				break;
			case 'c':
				hexStr += "C";
				break;
			case 'd':
				hexStr += "D";
				break;
			case 'e':
				hexStr += "E";
				break;
			case 'f':
				hexStr += "F";
				break;
			default:
				hexStr += hexNum.charAt(i);
			}
		if (hexStr.length() % 2 == 1)
			hexStr = 0 + hexStr;
		return hexStr;
	}

	/**
	 * 这个函数只针对汉语发送形式,测试成功！！ 这个函数是算UD即用户数据的十六进制的字符串表示形式。
	 * 先把得到的字符串分成一个个char，再转换成数字形式， 然后再换算成十六进制形式。
	 */
	private String getHexUD(String ud) {
		String hexUD = "";
		// 取得char的十六进制形式
		for (int i = 0; i < ud.length(); i++) {
			String prehexud = "";
			short c = (short) ud.charAt(i);
			prehexud = Integer.toHexString(c);
			if (prehexud.length() < 4) {
				String temp = "0000" + prehexud;
				prehexud = temp.substring(temp.length() - 4, temp.length());
			}
			// 出去十六进制中多余的ffff
			if (prehexud.startsWith("ffff"))
				prehexud = prehexud.substring(4);
			hexUD += prehexud;
		}
		// 把十六进制中的小写形式转换成大写形式
		String theud = hexUD;
		hexUD = "";
		for (int j = 0; j < theud.length(); j++) {
			switch (theud.charAt(j)) {
			case 'a':
				hexUD += "A";
				break;
			case 'b':
				hexUD += "B";
				break;
			case 'c':
				hexUD += "C";
				break;
			case 'd':
				hexUD += "D";
				break;
			case 'e':
				hexUD += "E";
				break;
			case 'f':
				hexUD += "F";
				break;
			default:
				hexUD += theud.charAt(j);
			}
		}
		return hexUD;
	}

	// 等待指定的字节序列出现
	public boolean waitMessage(byte[] msgWait, int timeout) {
		long time1 = System.currentTimeMillis();
		long time2 = 0L;
		int c;
		int pos = 0; // indicator for check waitMessage.
		recvpos = 0;
		while (true) {
			try {
				if (in.available() > 0) {
					c = in.read();
					if (recvpos >= MAXRECVSIZE) {
						recvMessage += new String(recvBuffer);
						recvpos = 0;
						time1 = time2;
					}
					recvBuffer[recvpos++] = (byte) c;
					// System.out.println(c + " " + (char) c);
					if (msgWait[pos] == (byte) c)
						pos++;
					else
						pos = 0;
					if (pos == msgWait.length)
						return true;
				}
				time2 = System.currentTimeMillis();
				if (time2 - time1 > timeout) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
	}

	// 返回接收缓冲区中的数据
	public String getMessage() {
		if (recvMessage.equals(""))
			return new String(recvBuffer, 0, recvpos);
		else if (recvpos > 0)
			return recvMessage + new String(recvBuffer, 0, recvpos);
		else
			return recvMessage;
	}

	// 接受短信
	public void sendReceive() {
		try {
			out.write("AT+CMGF=0\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			out.write("AT+CMGL=0\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			out.write("AT+CMGL=1\r".getBytes());
			waitMessage("+CMGS".getBytes(), 4000);
			System.out.println(getMessage());
			// 0891683108200905F0040D91 683186292668F9 0000 11114251712523 0131
			// 683186292668F9 电话号
			// F0040D91683186292668F90000
			// 11114251712523 日期
			// 0131 信息
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//	
	public String NtoString(String str) {
		String result = "";
		if (str.length() < 4) {
			char c = (char) Integer.parseInt(str, 16);
			result += c;
			return result;
		}
		for (int i = 0; i < str.length(); i += 4) {
			String temp = str.substring(i, i + 4);
			char c = (char) Integer.parseInt(temp, 16);
			result += c;
		}
		return result;
	}

	// 解析电话信息
	public void PraseData(String data) {
		// 电话
		String phone = data.substring(24, 38);
		phone = this.convertData(phone);
		phone = phone.replace("F", "");
		phone = phone.substring(phone.length() - 11, phone.length());
		// 日期
		String date = data.substring(42, 56);
		date = this.convertData(date);
		date = "20" + date.substring(0, 2) + "年" + date.substring(2, 4) + "月"
				+ date.substring(4, 6) + "日" + date.substring(6, 8) + ":"
				+ date.substring(8, 10) + ":" + date.substring(10, 12);
		// 信息
		String message = data.substring(58, data.length());
		message = NtoString(message);
		System.out.println("电话：" + phone + " 日期：" + date + " 信息：" + message);
	}

	public static void main(String[] args) {
		SMSSender sm = new SMSSender();
		// sm.PraseData("0891683108200905F0040D91683186292668F900081111427160052308621100316D4B8BD5");
		// System.out.println(sm.NtoString("11114251712523"));
		// System.out.println(sm.NtoString("00316D4B8BD5"));
		// System.out.println(sm.convertData("11114251712523"));
		// String phone = sm.convertData("683186292668F9");
		// System.out.println(phone);
		// System.out.println(sm.getHexUD("00316D4B8BD5"));
		sm.init("COM4");
		// while (true) {
		// sm.sendReceive();
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		sm.send_pdu_chi("8613689262869", "1222aaa测试");
	}
}