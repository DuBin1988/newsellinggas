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
 * ת��http������tcp����
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
	 * ����tcp����
	 * 
	 * @param msg
	 *            ��������
	 * @return ������Ϣ
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
	 * @return 1��ͷ˵��
	 */
	private JSONObject tcp(String msg) throws JSONException {
		TcpObj bean = null;
		Socket socket = null;
		OutputStream os = null;
		log.debug("������Ϣ��" + msg);
		String result = "";
		JSONObject ret = new JSONObject();
		try {
			bean = (TcpObj) BeanUtil.getBean(TcpObj.class);
			InetAddress addr = InetAddress.getByName(bean.getIp());
			socket = new Socket();
			socket.connect(new InetSocketAddress(addr, bean.getPort()),
					bean.getTimeout());
			log.debug("���������ӳɹ���");
			os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			log.debug("������Ϣ�ɹ�");
			byte[] bs = new byte[1000];
			int len = socket.getInputStream().read(bs);
			result = new String(bs, 0, len);
			log.debug("������Ϣ:" + result);
		} catch (SocketTimeoutException e) {
			ret.put("error", "���ӳ�ʱ" + bean.getTimeout());
			// ��ʱ
			log.debug("���ӳ�ʱ");
			throw new RuntimeException(e);
		} catch (Exception e) {
			ret.put("error", e.getMessage());
			log.debug(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				log.debug("�ر�socket����");
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
		//��ѯ
		String msg = "00000065"
				+ "1001"
				+ "52935     "
				+ "0000000020"
				+ "20160226000000000001"
				+ "0"
				+ "0000000001"
				+ "0000000001";
		//����
		msg = "00000075"
				+ "1002"
				+ "00016     "
				+ "0000000020"
				+ "20160226000000000001"
				+ "0"
				+ "0000000001"
				+ "0000000001"
				+ "0000100000";//��λ��
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
			System.out.println("���������ӳɹ���");
			os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.flush();
			System.out.println("������Ϣ�ɹ�");
			byte[] bs = new byte[20000];
			int len = socket.getInputStream().read(bs);
			result = new String(bs, 0, len);
			System.out.println("������Ϣ:" + result);
		} catch (SocketTimeoutException e) {
			// ��ʱ
			System.out.println("���ӳ�ʱ");
			throw new RuntimeException(e);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				System.out.println("�ر�socket����");
				os.close();
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
