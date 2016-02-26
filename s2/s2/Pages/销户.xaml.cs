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
	public partial class 销户 : UserControl
	{
        String f_userid = "";
		public 销户()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        #region saveButton_Click 保存按钮按下时
        private void save_Click(object sender, RoutedEventArgs e)
        {
            ui_apply.IsBusy = true;

            GeneralObject obj = pipeline.DataContext as GeneralObject;
            f_userid = obj.GetPropertyValue("f_userid") + "";
            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            save.Completed += save_Completed;
            save.Invoke();
            obj.New();

        }

        void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            //throw new NotImplementedException();
            string json = "[";
            if (f_userid != null)
            {
                //产生要发送后台的JSON串
                json += ("{customer_code:\"" + f_userid + "\"}");
            }
            json += "]";
            //将产生的json串送后台服务进行处理
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/iesgas/user/status";
            WebClient client = new WebClient();
            client.UploadStringCompleted += client_UploadStringCompleted;
            client.UploadStringAsync(new Uri(uri), json);
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            ui_apply.IsBusy = false;
        }
        #endregion


	}
}