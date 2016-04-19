﻿using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Linq;
using Com.Aote.ObjectTools;
using System.Net;
using System.Collections.Generic;
using System.Json;
using Com.Aote.Utils;
using System.IO;

namespace Com.Aote.Pages
{
    public partial class DBF抄表上传下载 : UserControl
    {
        ObjectList list = new ObjectList();
        public DBF抄表上传下载()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        #region saveButton_Click 保存按钮按下时
        private void saveButton_Click(object sender, RoutedEventArgs e)
        {
            // 调用服务
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/DBFService/readfile/" + f_inputtor.SelectedValue + "/" + f_device.SelectedValue;
            Com.Aote.Controls.FileLoad fl=new Com.Aote.Controls.FileLoad();
            fl.Filter="(*.dbf)|*.dbf" ;
            fl.Path = uri;
            fl.UpLoadNOFileName();
            fl.Completed+=fl_Completed;
                                                            
        }

        void fl_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            MessageBox.Show("文件上传成功,后台生成抄表记录!");
        }

        #endregion

        #region 查询按钮处理过程
        // 查询按钮处理过程
        private void dansearchbutton_Click(object sender, RoutedEventArgs e)
        {
            // 调用服务
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/DBFService/savefile/" + f_inputtor.SelectedValue + "/" + f_device.SelectedValue;
            Com.Aote.Controls.DownLoad dl = new Com.Aote.Controls.DownLoad();
            dl.Filter="(*.dbf)|*.dbf" ;
            dl.Path = uri;
            dl.Down();
            dl.Completed += dl_Completed;
        }

        void dl_Completed(object sender, EventArgs e)
        {
            MessageBox.Show("下载完成!");
        }
        #endregion


    }
}
