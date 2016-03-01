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

namespace Com.Aote.Pages
{
	public partial class 用户事物过户管理 : UserControl
	{
        String userid = "";

		public 用户事物过户管理()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        #region saveButton_Click 保存按钮按下时
        private void save_Click(object sender, RoutedEventArgs e)
        {
            ui_apply.IsBusy = true;

            GeneralObject obj = apply.DataContext as GeneralObject;
            userid = obj.GetPropertyValue("f_userid") +"";

            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            save.Completed += save_Completed;
            save.Invoke();

        }
        void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            BatchExcuteAction save = sender as BatchExcuteAction;
            save.Completed -= save_Completed;

            if (userid != null)
            {
                //将产生的json串送后台服务进行处理
                WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
                string uri = wci.BaseAddress + "/iesgas/user/comand";
                WebClient client = new WebClient();
                client.UploadStringCompleted += client_UploadStringCompleted;
                client.UploadStringAsync(new Uri(uri), "[{customer_code:\"" + userid + "\"}]");
            }
            else
            {
                ui_apply.IsBusy = false;
            }
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            ui_apply.IsBusy = false;
            if (e.Error == null)
            {
                //弹出错误信息
                if (e.Result != "")
                {
                    // MessageBox.Show(e.Result);
                }

            }
        }
        #endregion

	}
}