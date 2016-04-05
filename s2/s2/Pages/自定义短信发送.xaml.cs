using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
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
    public partial class 自定义短信发送 : UserControl
    {
        public 自定义短信发送()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        private void oneSend_Click(object sender, RoutedEventArgs e)
        {
            BaseObjectList list = daninfos.ItemsSource as BaseObjectList;
            String content = f_content.Text.ToString();
            String templateName = templatename.SelectedValue.ToString();

            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                if (go == daninfos.SelectedItem)
                {
                    // id
                    String username = go.GetPropertyValue("f_username").ToString();
                    String f_phone = go.GetPropertyValue("f_phone").ToString();

                    WebClientInfo wci = (WebClientInfo)Application.Current.Resources["smsserver"];
                    string str = wci.BaseAddress + "/send/" + username + "/"+f_phone +"/"+ content +"/" + templateName;
                    Uri uri = new Uri(str);
                    WebClient client = new WebClient();
                    client.DownloadStringAsync(uri);            
                }
            }
        }

        private void allSend_Click(object sender, RoutedEventArgs e)
        {
            BaseObjectList list = daninfos.ItemsSource as BaseObjectList;
            String content = f_content.Text.ToString();
            String templateName = templatename.SelectedValue.ToString();

            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                // id
                String username = go.GetPropertyValue("f_username").ToString();
                String f_phone = go.GetPropertyValue("f_phone").ToString();

                WebClientInfo wci = (WebClientInfo)Application.Current.Resources["smsserver"];
                string str = wci.BaseAddress + "/send/" + username + "/" + f_phone + "/" + content + "/" + templateName;
                Uri uri = new Uri(str);
                WebClient client = new WebClient();
                client.DownloadStringAsync(uri);
            }
        }
    }
}