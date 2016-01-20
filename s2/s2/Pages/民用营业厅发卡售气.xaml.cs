using Com.Aote.ObjectTools;
using System;
using System.Json;
using System.Net;
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
	public partial class 民用营业厅发卡售气 : UserControl
	{
		public 民用营业厅发卡售气()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        private void ui_pregas_LostFocus(object sender, RoutedEventArgs e)
        {
            ui_busy.IsBusy = true;
            string userid = ui_userid.Text;
            string pregas = ui_pregas.Text;
            if(userid.Equals(""))
            {
                MessageBox.Show("请先查询!");
                ui_busy.IsBusy = false;
                return;
            }
            else if (pregas.Equals(""))
            {
                MessageBox.Show("请输入气量!");
                ui_busy.IsBusy = false;
                return;
            }
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["chargeserver"];
            string str = wci.BaseAddress + "/num/" + userid + "/" + pregas;
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);
        }

        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            if(e.Error == null)
            {
                JsonObject items = JsonValue.Parse(e.Result) as JsonObject;
                ui_preamount.Text = items["f_totalcost"].ToString();
                ui_busy.IsBusy = false;
            }
            else
            {
                ui_busy.IsBusy = false;
                MessageBox.Show(e.Error.Message);
            }
        }
	}
}