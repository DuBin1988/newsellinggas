using Com.Aote.ObjectTools;
using System;
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
using Com.Aote.Behaviors;

namespace Com.Aote.Pages
{
    public partial class 短信功能管理: UserControl
    {
        public 短信功能管理()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        private void start_Click(object sender, RoutedEventArgs e)
        {
            String flagName = name.Text.ToString();
            String state = value.Text.ToString();

            if (flagName == "短信功能开关")
            {
                //执行sql开关 状态 改为 开启 短信表里所有待发改为 未发
                String sql = "update t_smstemplate set f_state='开启' where f_name='短信功能开关'";
                HQLAction action = new HQLAction();
                action.Name = "abc";
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Invoke();

                sql = "update t_sms set f_state = '未发' where f_state = '待发'";
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Invoke();
            }
            else
            {
                //开关状态改为开启， 短信表里 此类 待发短信 状态改为 未发
                String sql = "update t_smstemplate set f_state='开启' where f_name='" + flagName + "'";
                HQLAction action = new HQLAction();
                action.Name = "abc";
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Invoke();

                sql = "update t_sms set f_state = '未发' where f_state = '待发' and f_templatename='"+ flagName +"'";
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Invoke();
            }
            value.Text = "开启";
        }
    }
}