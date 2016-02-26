using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Com.Aote.Pages
{
    public partial class 物联表阀门操作 : UserControl
    {
        SearchObject search = new SearchObject();

        public 物联表阀门操作()
        {
            InitializeComponent();
            ui_SearchUser.DataContext = search;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            ui_searchBusy.IsBusy = true;
            search.Search();
            if (!"1=1".Equals(search.Condition.Trim()) && null != search.Condition)
            {
                //提交更改
                string sql = "update t_userfiles set f_operate_zl='" + f_operate_zl_zl.SelectedValue + "', f_returnvalueoperate=" + "'1' " +
                                 " where f_gasmeterstyle in ('物联网表','物联表','积成远传气表','远传表','短信表') and " + search.Condition + "";
                HQLAction action = new HQLAction();
                action.HQL = sql;
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Name = "t_userfiles";
                action.Completed += action_Completed;
                action.Invoke();              
            }
            else {
                MessageBox.Show("请先按照条件筛选出进行阀门操作的用户，再确认执行！");
                ui_searchBusy.IsBusy = false;
            }

        }

        private void action_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            HQLAction action = sender as HQLAction;
            action.Completed -= action_Completed;

            if (e.Error == null)
            {
                //将产生的json串送后台服务进行处理
                WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
                string uri = wci.BaseAddress + "/iesgas/table/comand";
                WebClient client = new WebClient();
                client.UploadStringCompleted += client_UploadStringCompleted;
                client.UploadStringAsync(new Uri(uri), "[{\"search\":\"" + search.Condition + "\"}]");

            }
            else {
                ui_searchBusy.IsBusy = false;
            }
            
       }

       void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
       {
           ui_searchBusy.IsBusy = false;
       }

    }
}
