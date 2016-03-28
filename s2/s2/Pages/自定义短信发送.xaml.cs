using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
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

            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                if (go == daninfos.SelectedItem)
                {
                    // id
                    String username = go.GetPropertyValue("f_useranme").ToString();
                    String f_phone = go.GetPropertyValue("f_phone").ToString();

                    sql = "update t_sms set f_state='未发' where id=" + smsId + "";
                    action.HQL = sql;
                    action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    action.Invoke();
                }
            }
        }

        private void allSend_Click(object sender, RoutedEventArgs e)
        {
            BaseObjectList list = daninfos.ItemsSource as BaseObjectList;
            HQLAction action = new HQLAction();
            action.Name = "abc";
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Invoke();

            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                // id
                String username = go.GetPropertyValue("f_useranme").ToString();
                String f_phone = go.GetPropertyValue("f_phone").ToString();

                sql = "update t_sms set f_state='未发' where id=" + smsId + "";
                HQLAction action = new HQLAction();
                action.Name = "abc";
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Invoke();
            }
        }
    }
}