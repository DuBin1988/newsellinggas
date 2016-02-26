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
using System.Windows.Controls.Primitives;
using Com.Aote.ObjectTools;
using Com.Aote.Behaviors;
using System.Net;

namespace Com.Aote.Pages
{
    public partial class 物联表欠费查询 : UserControl
	{
        String f_userid = "";
		public 物联表欠费查询()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                GeneralObject go = daninfos.SelectedItem as GeneralObject;
                if (go != null)
                {
                    f_userid = go.GetPropertyValue("f_userid") + "";
                    string sql = "update t_userfiles set f_operate_zl='欠费', f_returnvalueoperate=" + "'1' " +
                                 " where f_gasmeterstyle in ('物联网表','物联表','积成远传气表','远传表','短信表') and f_userid='" + f_userid + "'";
                    HQLAction action = new HQLAction();
                    action.HQL = sql;
                    action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    action.Name = "t_userfiles";
                    action.Completed += action_Completed;
                    action.Invoke();
                }
            }
            catch (Exception ex)
            {
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
                client.UploadStringAsync(new Uri(uri), "[{\"search\":\"f_userid like '" + f_userid + "'\"}]");

            }

        }
        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {

        }
	}
}