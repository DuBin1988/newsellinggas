/**
 *
 *  SMSSender�������Ϣ�ķ��͡�
 *  �������PDU��char��ʽ�Ķ���Ϣ�ֱ��ṩ�˷�����
 *	����PDU��ʽ�漰���˵绰������ı��ķֱ�ת��������Ҳ����������˽�з�����
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

	private byte[] recvBuffer = new byte[MAXRECVSIZE]; // ���ջ�����

	private int recvpos; // ���ջ�����ָ��

	private String recvMessage = ""; // ���յ����ַ���

	public CommPortIdentifier portID;

	private SerialPort comport;

	private OutputStream out;

	private InputStream in;

	// String pdu_str = SCA + PDU_MR + DA + PID + DCS + VP + UDL + UD + CTRL_Z;
	String SCA = "00";

	// String SCA = "0891683108200905F0";

	String PDU_MR = "1100";

	String DA = ""; // �����ֻ��ŵĳ��ȣ���������һ����11λ��0B������ַ���ͺͺ��롣����һ��Ϊ8

	String PID = "00";

	String DCS = "08"; // ���ı���

	String VP = "A7";

	String UDL = ""; // ռ��һ���ֽ�

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
			// ��ȡָ���˿�
			portID = CommPortIdentifier.getPortIdentifier(comportName);
			comport = (SerialPort) portID.open("Test", 2000);
			// �趨�˿ڵ�ͨѶ����
			// �����������ʣ�����λ��ֹͣλ��У��λ
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

	// ���ͺ��ָ�ʽ��SMS
	public void send_pdu_chi(String phone, String message, JTextArea area) {
		DA = getUpperCaseHexString(phone.length()) + "81" + convertData(phone);
		UDL = getHexUDLength(message);
		UD = getHexUD(message);
		// ���ȼ��㷨����13���û����ݳ���UDL�ټ�UDL������14��UDL��
		String atcmgs = "AT+CMGS=" + (14 + message.length() * 2) + "\r";
		String pdu_str = SCA + PDU_MR + DA + PID + DCS + VP + UDL + UD + CTRL_Z;
		try {
			out.write("AT+CMGF=0\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			area.append(getMessage() + "\n");
			out.write(atcmgs.getBytes()); // ���Ҫ���㡣����SCA����
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
		// ���ȼ��㷨����13���û����ݳ���UDL�ټ�UDL������14��UDL��
		String atcmgs = "AT+CMGS=" + (15 + message.length() * 2) + "\r";
		String pdu_str = SCA + PDU_MR + DA + PID + DCS + VP + UDL + UD + CTRL_Z
				+ '\r';
		System.out.println(atcmgs);
		try {
			out.write("AT+CMGF=0\r".getBytes());
			waitMessage("OK".getBytes(), 2000);
			System.out.println(getMessage());
			out.write(atcmgs.getBytes()); // ���Ҫ���㡣����SCA����
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
	 * ��������������������GUIƽ̨��������������
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
	 * ���������������GUIƽ̨������������,JTextArea can be null.
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

	// ��SCA��DAת������ȷ��ʽ�����ﶼ����ǰ���С�86������,������ͨ����
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
	 * ������Ҳ���Կ������������������Լ����û����ݵĳ����õġ����˼����������:
	 * ������ͳһ��ĽǶ���д�ġ����������������λ��λ���뷽ʽ��������Ͳ�������
	 * ����Integer.toHexString()���ص�ʮ��������Сд��ʽ��0a��������0A���Ծ���switch �н�����ת����
	 */
	private String getHexUDLength(String number) {
		int num = number.length() * 2;
		String hexStr = getUpperCaseHexString(num);
		return hexStr;
	}

	/*
	 * �������ֻ�ǰ�Integer.toHexString(int)ת����������0a������ʽת����0A������ʽ��
	 * ͬʱ���ʮ�����Ƴ���Ϊ������������ǰ���0��
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
	 * �������ֻ��Ժ��﷢����ʽ,���Գɹ����� �����������UD���û����ݵ�ʮ�����Ƶ��ַ�����ʾ��ʽ��
	 * �Ȱѵõ����ַ����ֳ�һ����char����ת����������ʽ�� Ȼ���ٻ����ʮ��������ʽ��
	 */
	private String getHexUD(String ud) {
		String hexUD = "";
		// ȡ��char��ʮ��������ʽ
		for (int i = 0; i < ud.length(); i++) {
			String prehexud = "";
			short c = (short) ud.charAt(i);
			prehexud = Integer.toHexString(c);
			if (prehexud.length() < 4) {
				String temp = "0000" + prehexud;
				prehexud = temp.substring(temp.length() - 4, temp.length());
			}
			// ��ȥʮ�������ж����ffff
			if (prehexud.startsWith("ffff"))
				prehexud = prehexud.substring(4);
			hexUD += prehexud;
		}
		// ��ʮ�������е�Сд��ʽת���ɴ�д��ʽ
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

	// �ȴ�ָ�����ֽ����г���
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

	// ���ؽ��ջ������е�����
	public String getMessage() {
		if (recvMessage.equals(""))
			return new String(recvBuffer, 0, recvpos);
		else if (recvpos > 0)
			return recvMessage + new String(recvBuffer, 0, recvpos);
		else
			return recvMessage;
	}

	// ���ܶ���
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
			// 683186292668F9 �绰��
			// F0040D91683186292668F90000
			// 11114251712523 ����
			// 0131 ��Ϣ
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

	// �����绰��Ϣ
	public void PraseData(String data) {
		// �绰
		String phone = data.substring(24, 38);
		phone = this.convertData(phone);
		phone = phone.replace("F", "");
		phone = phone.substring(phone.length() - 11, phone.length());
		// ����
		String date = data.substring(42, 56);
		date = this.convertData(date);
		date = "20" + date.substring(0, 2) + "��" + date.substring(2, 4) + "��"
				+ date.substring(4, 6) + "��" + date.substring(6, 8) + ":"
				+ date.substring(8, 10) + ":" + date.substring(10, 12);
		// ��Ϣ
		String message = data.substring(58, data.length());
		message = NtoString(message);
		System.out.println("�绰��" + phone + " ���ڣ�" + date + " ��Ϣ��" + message);
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
		sm.send_pdu_chi("8613689262869", "1222aaa����");
	}
}