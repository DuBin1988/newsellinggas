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
    public partial class 短信信息 : UserControl
    {
        public 短信信息()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        private void oneSend_Click(object sender, RoutedEventArgs e)
        {
            BaseObjectList list = daninfos.ItemsSource as BaseObjectList;


            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                if (go == daninfos.SelectedItem)
                {
                    // id
                    String smsId = go.GetPropertyValue("id").ToString();

                    String sql = "update t_sms set f_state='未发' where id=" + smsId + "";
                    HQLAction action = new HQLAction();
                    action.Name = "abc";
                    action.HQL = sql;
                    action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    action.Invoke();
                }
            }
        }

        private void allSend_Click(object sender, RoutedEventArgs e)
        {
            BaseObjectList list = daninfos.ItemsSource as BaseObjectList;


            //对于每一条记录
            foreach (GeneralObject go in list)
            {
                // id
                String smsId = go.GetPropertyValue("id").ToString();

                String sql = "update t_sms set f_state='未发' where id="+ smsId +"";
                HQLAction action = new HQLAction();
                action.Name = "abc";
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Invoke();
            }
        }
    }
}