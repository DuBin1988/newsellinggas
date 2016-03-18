﻿using Com.Aote.ObjectTools;
using Com.Aote.Utils;
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

namespace Com.Aote.Pages
{
    public partial class 散户通气 : UserControl
    {
        public 散户通气()
        {
            InitializeComponent();
        }
        //提及时的处理过程
        private void submittip2_OK(object sender, EventArgs e)
        {
            ObjectList ObjectList = FrameworkElementExtension.FindResource(this.searchbutton, "dangans") as ObjectList;
            WebClientInfo server = FrameworkElementExtension.FindResource(this.searchbutton, "server") as WebClientInfo;
            GeneralObject user = FrameworkElementExtension.FindResource(this.searchbutton, "LoginUser") as GeneralObject;
            string uuid = System.Guid.NewGuid().ToString();
            Uri uri = new Uri(server.BaseAddress + "/files/touinfo/用户编号/表编号/" + user.GetPropertyValue("id") + "?uuid=" + uuid);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.UploadStringAsync(uri, ObjectList.ToJson().ToString());
        }

        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                MessageBox.Show("提交成功");
            }
            else
            {
                MessageBox.Show(e.Error.Message);
            }
        }
    }
}