package com.aote.rs.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aote.listener.ContextListener;
import com.aote.rs.util.BeanUtil;

/**
 * 转发http，请求到tcp服务
 * 
 * @author hyde
 *
 */
@Path("tcp")
@Scope("prototype")
@Component
public class TcpService {

	static Logger log = Logger.getLogger(TcpService.class);

	@Autowired
	private HibernateTemplate hibernateTemplate;

	/**
	 * 发送tcp请求
	 * 
	 * @param msg
	 *            请求内容
	 * @return 返回信息
	 */
	@GET
	@Path("/send/{msg}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject send(@PathParam("msg") String msg) {
		JSONObject result = new JSONObject();
		try {
			result = tcp(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param msg
	 * @return 1开头说明
	 */
	private JSONObject tcp(String msg) throws JSONException {
		TcpObj bean = null;
		Socket socket = null;
		OutputStream os = null;
		log.debug("请求信息：" + msg);
		String result = "";
		JSONObject ret = new JSONObject();
		try {
			bean = (TcpObj) BeanUtil.getBean(TcpObj.class);
			InetAddress addr = InetAddress.getByName(bean.getIp());
			socket = new Socket();
			socket.connect(new InetSocketAddress(addr, bean.getPort()),
					bean.getTimeout());
			log.debug("与服务端链接成功！");
			os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			log.debug("发送信息成功");
			byte[] bs = new byte[1000];
			int len = socket.getInputStream().read(bs);
			result = new String(bs, 0, len);
			log.debug("接收信息:" + result);
		} catch (SocketTimeoutException e) {
			ret.put("error", "链接超时" + bean.getTimeout());
			// 超时
			log.debug("链接超时");
			throw new RuntimeException(e);
		} catch (Exception e) {
			ret.put("error", e.getMessage());
			log.debug(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				log.debug("关闭socket链接");
				os.close();
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		ret.put("return", result);
		return ret;
	}

	public static void main(String[] args) {
		System.out.println("tcp start");
		//查询
		String msg = "00000065"
				+ "1001"
				+ "52935     "
				+ "0000000020"
				+ "20160226000000000001"
				+ "0"
				+ "0000000001"
				+ "0000000001";
		//交费
		msg = "00000075"
				+ "1002"
				+ "00016     "
				+ "0000000020"
				+ "20160226000000000001"
				+ "0"
				+ "0000000001"
				+ "0000000001"
				+ "0000100000";//单位分
		//msg="00000065100100020     111111111111111111111111111111M11111111111111111111";
		TcpObj bean = new TcpObj();
		bean.setIp("192.168.1.106");
		bean.setPort(7777);
		bean.setTimeout(3000);
		Socket socket = null;
		OutputStream os = null;
		String result = "";
		try {
			InetAddress addr = InetAddress.getByName(bean.getIp());
			socket = new Socket();
			socket.connect(new InetSocketAddress(addr, bean.getPort()),
					bean.getTimeout());
			System.out.println("与服务端链接成功！");
			os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			System.out.println("发送信息成功");
			byte[] bs = new byte[20000];
			int len = socket.getInputStream().read(bs);
			result = new String(bs, 0, len);
			System.out.println("接收信息:" + result);
		} catch (SocketTimeoutException e) {
			// 超时
			System.out.println("链接超时");
			throw new RuntimeException(e);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				System.out.println("关闭socket链接");
				os.close();
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
