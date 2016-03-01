using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.ObjectTools;
using Com.Aote.Behaviors;
using System.Net;
using System.Linq;
using Com.Aote.Utils;
using System.Text.RegularExpressions;


namespace Com.Aote.Pages
{
	public partial class 机表冲正 : UserControl
	{
        String id = "";
        String userid = "";
		public 机表冲正()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        #region SaveClick 提交按钮操作过程
        // 提交数据到后台服务器
        private void SaveClick(object sender, RoutedEventArgs e)
        {
            busy.IsBusy = true;

            GeneralObject obj = kbfee1.DataContext as GeneralObject;
            userid = obj.GetPropertyValue("f_userid") + "";
            id = obj.GetPropertyValue("id1")+"";

            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            save.Completed += save_Completed;
            save.Invoke();
            busy.IsBusy = false;
            
        }

        void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            busy.IsBusy = true;

            BatchExcuteAction save = sender as BatchExcuteAction;
            save.Completed -= save_Completed;
            
            //取出登陆用户id，后台根据id查找登陆用户放入分公司等信息
            GeneralObject loginUser = (GeneralObject)FrameworkElementExtension.FindResource(this, "LoginUser");
            if (loginUser == null)
            {
                MessageBox.Show("无法获取当前登陆用户信息,请重新登陆后操作!");
                return;
            }
            string loginid = (string)loginUser.GetPropertyValue("id");
            //获取基础地址
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["server"];
            // 提交
            string str = wci.BaseAddress + "/sell/" + userid + "/" + id + "/" + loginid;
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);
        }
        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            WebClient client = sender as WebClient;
            client.DownloadStringCompleted -= client_DownloadStringCompleted;

            // 没有出错
            if (e.Error == null)
            {
                try
                {
                    String retid = e.Result as String;
                    if (!"noid".Equals(retid) && userid != null)
                    {
                        //产生要发送后台的JSON串
                        WebClientInfo wci1 = Application.Current.Resources["server"] as WebClientInfo;
                        string uri1 = wci1.BaseAddress + "/iesgas/gascz/comand";
                        WebClient client1 = new WebClient();
                        client1.UploadStringCompleted += client1_UploadStringCompleted;
                        client1.UploadStringAsync(new Uri(uri1), ("[{customer_code:\"" + userid + "\",id:\"" + retid + "\"}]"));
                    }
                }
                catch (Exception ex) { }
            }
            busy.IsBusy = false;
        }

        void client1_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            busy.IsBusy = false;
        }
        #endregion

	}
}