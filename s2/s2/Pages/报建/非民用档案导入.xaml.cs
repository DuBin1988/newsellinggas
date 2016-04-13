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
using Com.Aote.Utils;
using System.Net;

namespace Com.Aote.Pages
{
	public partial class 非民用档案导入 : UserControl
	{
		public 非民用档案导入()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void AllImportSubmitAction_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ObjectList ObjectList = FrameworkElementExtension.FindResource(this.searchbutton, "dangans") as ObjectList;
            WebClientInfo server = FrameworkElementExtension.FindResource(this.searchbutton, "server") as WebClientInfo;
            GeneralObject user = FrameworkElementExtension.FindResource(this.searchbutton, "LoginUser") as GeneralObject;
            string uuid = System.Guid.NewGuid().ToString();
            Uri uri = new Uri(server.BaseAddress + "/files/touinfo/用户编号/表编号/" + user.GetPropertyValue("id") + "?uuid=" + uuid);
            WebClient client = new WebClient();
            client.UploadStringCompleted += client_UploadStringCompleted;
            client.UploadStringAsync(uri, ObjectList.ToJson().ToString());

        }

    

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
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