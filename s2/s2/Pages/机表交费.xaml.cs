﻿using Com.Aote.ObjectTools;
using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Linq;
using Com.Aote.Utils;
using Com.Aote.Controls;

namespace Com.Aote.Pages
{
	public partial class 机表交费 : UserControl
	{
		public 机表交费()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void f_userid_LostFocus(object sender, RoutedEventArgs e)
        {

        }

        #region SaveClick 提交按钮操作过程
        // 提交数据到后台服务器
        private void SaveClick(object sender, RoutedEventArgs e)
        {
            //显示正在工作
            busy.IsBusy = true;
            //取出登陆用户id，后台根据id查找登陆用户放入分公司等信息
            GeneralObject loginUser = (GeneralObject) FrameworkElementExtension.FindResource(this, "LoginUser");
            if (loginUser == null)
            {
                MessageBox.Show("无法获取当前登陆用户信息,请重新登陆后操作!");
                return;
            }
            string loginid = (string)loginUser.GetPropertyValue("id");
               //获取基础地址
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["server"];
             // 提交
            string str = wci.BaseAddress + "/sell/" + f_userid.Text + "/"  + shoukuan.Text + "/"
                + f_zhinajin.Text + "/" + f_payment.SelectedValue + "/" + loginid;
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);
        }

        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
           busy.IsBusy = false;
           // 没有出错
           if (e.Error == null)
           {
               //弹出错误信息
               if (e.Result != "")
               {
                   MessageBox.Show(e.Result);
                   return;
               }
               // 调用打印
               print.Completed += print_Completed;
               print.TipPrint();
			
           }
           else
           {
               // 提示出错
               MessageBox.Show("连接服务器失败，请重试！如果继续失败，请联系系统管理员。");
               // 清除界面数据
               GeneralObject kbfee = (GeneralObject)(from r in loader.Res where r.Name.Equals("kbfee") select r).First();
               kbfee.New();
           }
        }

        private void print_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            print.Completed -= print_Completed;
            //打印折子
            PrintObj zzprint = FrameworkElementExtension.FindResource(this.save2, "zzprint") as PrintObj;
            zzprint.Completed += zzprint_Completed;
            zzprint.TipPrint();
        }

        void zzprint_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            PrintObj zzprint = sender as PrintObj;
            zzprint.Completed -= zzprint_Completed;
            //打印完成清空
            GeneralObject kbfee = (GeneralObject)(from r in loader.Res where r.Name.Equals("kbfee") select r).First();
            kbfee.New();
            MessageBox.Show("交费完成！");
        }
        #endregion

    }
}