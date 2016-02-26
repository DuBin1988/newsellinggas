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
	public partial class 换表管理 : UserControl
	{
        String jsonjs="";
        String jsontb = "";
        String url = "";
		public 换表管理()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        #region saveButton_Click 保存按钮按下时
        private void save_Click(object sender, RoutedEventArgs e)
        {            
            ui_userfiles.IsBusy = true;
            jsontb = "[{customer_code:\"" + ur_userid.Text + "\"}]";
            jsonjs = "[{userid:" + ur_userid.Text + ",reading:" + lastinputgasnum_cb.Text + ",lastreading:" + lastinputgasnum.Text + "}]";
            url = "/handcharge/record/batch/" + ui_handdate.SelectedDate + "/" + ui_sgnetwork.Text + "/" + ui_sgoperator.Text + "/" + chaobiaoriqi.SelectedDate + "/" + meter.SelectedValue.ToString() + "/false" + "?uuid=" + System.Guid.NewGuid().ToString();
            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("CreateHandplan") select p).First() as BatchExcuteAction;
            save.Completed += save_Completed;
            save.Invoke();
            
        }

        void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            BatchExcuteAction c = sender as BatchExcuteAction;
            c.Completed -= save_Completed;

            //计算抄表费用
            WebClientInfo wci1 = Application.Current.Resources["server"] as WebClientInfo;
            string uri1 = wci1.BaseAddress + url;
            WebClient client1 = new WebClient();
            client1.UploadStringCompleted += client1_UploadStringCompleted;
            client1.UploadStringAsync(new Uri(uri1), jsonjs);
           
        }

        void client1_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            WebClient client1 = sender as  WebClient;
            client1.UploadStringCompleted -= client1_UploadStringCompleted;

            BatchExcuteAction save1 = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            save1.Completed += save1_Completed;
            save1.Invoke();
        }

        void save1_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            BatchExcuteAction c = sender as BatchExcuteAction;
            c.Completed -= save_Completed;
            //同步 将产生的json串送后台服务进行处理
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/iesgas/user/comand";
            WebClient client = new WebClient();
            client.UploadStringCompleted += client_UploadStringCompleted;
            client.UploadStringAsync(new Uri(uri), jsontb);
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            ui_userfiles.IsBusy = false;
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

        private void TextBox_LostFocus_1(object sender, RoutedEventArgs e)
        {
            //{m:Exp Str=tabletel.IsOld\=True}
            if (null != f_meternumber.Text && !"".Equals(f_meternumber.Text))
            {
                GeneralObject save2 = (from p in loader.Res where p.Name.Equals("tabletel") select p).First() as GeneralObject;
                save2.isBusy = true;
                save2.Path = "one/select new Map(meter_phone as meter_phone,terminal_name as terminal_name) from t_table_tel  where f_meternumber='" + f_meternumber.Text + "'";
                save2.Load();
            }
        }

	}
}