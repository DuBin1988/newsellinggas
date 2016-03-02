package com.aote.rs.sms;

import java.util.List;
import java.util.UUID;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mascloud.model.MoModel;
import com.mascloud.model.StatusReportModel;
import com.mascloud.model.SubmitReportModel;
import com.mascloud.sdkclient.Client;

public class LaSaSms {
	public static void main(String[] args) {
		try {
			final Client client = Client.getInstance();
			// 正式环境IP，登录验证URL，用户名，密码，集团客户名称
			//client.login("http://mas.ecloud.10086.cn/app/sdk/login",
			//		"SDK账号名称（不是页面端账号）", "密码", "集团客户名称");
			// 测试环境IP
			client.login("http://112.33.1.13/app/sdk/login", "sdk2",
			 "123","光谷信息");
			int sendResult = client.sendDSMS(new String[] { "13689262869" },
					"sdk短信发送内容测试", "", 1, "短信签名ID", UUID.randomUUID()
							.toString(), true);
			System.out.println("推送结果: " + sendResult);

			// 获取提交报告线程
			Thread t1 = new Thread() {
				public void run() {
					while (true) {
						List<SubmitReportModel> list = client.getSubmitReport();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			t1.start();

			// 获取状态报告线程
			Thread t2 = new Thread() {
				public void run() {
					while (true) {
						List<StatusReportModel> StatusReportlist = client
								.getReport();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			t2.start();

			// 获取上行线程
			Thread t3 = new Thread() {
				public void run() {
					while (true) {
						List<MoModel> lis = client.getMO();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			t3.start();

			for (;;)
				;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
