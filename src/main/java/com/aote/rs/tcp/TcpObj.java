package com.aote.rs.tcp;

public class TcpObj {
	private String FilePath;
	
	private int Timeout;

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public int getTimeout() {
		return Timeout;
	}

	public void setTimeout(int timeout) {
		Timeout = timeout;
	}

	private String Ip;
	private int Port;


	public String getIp() {
		return Ip;
	}

	public void setIp(String ip) {
		Ip = ip;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}
}
