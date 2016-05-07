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
	public partial class 非流程超用气收费 : UserControl
	{
		public 非流程超用气收费()
		{
			// Required to initialize variables
			InitializeComponent();
		}
		
		private void ui_pregas_LostFocus(object sender, RoutedEventArgs e)
        {
            ui_chargeBusy.IsBusy = true;
            string userid = f_userid.Text;
            string pregas = ui_pregas.Text; 
            if (userid.Equals(""))
            {
                MessageBox.Show("请输入表编号！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            else if (pregas.Equals(""))
            {
                MessageBox.Show("请输入预购气量！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["chargeserver"];
            string str = wci.BaseAddress + "/num/" + userid + "/" + pregas + "?uuid=" + System.Guid.NewGuid().ToString();
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);

        }

        private void DisplayPopup(object sender, RoutedEventArgs e)
        {
            if (myPopup.IsOpen == false)
            {
                myPopup.IsOpen = true;
            }
            else
            {
                myPopup.IsOpen = false;
            }
        }

        private void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            ui_chargeBusy.IsBusy = false;
            if (e.Error == null)
            {
                JsonObject items = JsonValue.Parse(e.Result) as JsonObject;
                ui_stair1amont.Text = items["f_stair1amount"].ToString();
                ui_stair2amont.Text = items["f_stair2amount"].ToString();
                ui_stair3amont.Text = items["f_stair3amount"].ToString();
                ui_stair4amont.Text = items["f_stair4amount"].ToString();
                ui_stair1fee.Text = items["f_stair1fee"].ToString();
                ui_stair2fee.Text = items["f_stair2fee"].ToString();
                ui_stair3fee.Text = items["f_stair3fee"].ToString();
                ui_stair4fee.Text = items["f_stair4fee"].ToString();
                ui_stair1price.Text = items["f_stair1price"].ToString();
                ui_stair2price.Text = items["f_stair2price"].ToString();
                ui_stair3price.Text = items["f_stair3price"].ToString();
                ui_stair4price.Text = items["f_stair4price"].ToString();
                ui_allamont.Text = items["f_allamont"].ToString();
                //ui_OrdinaryNum.Text = items["f_allamont"].ToString();
                if (items["f_stardate"] == null)
                {
                    items["f_stardate"] = "2050-12-12";
                }
                if (items["f_enddate"] == null)
                {
                    items["f_enddate"] = "2050-12-12";
                }
                ui_stardate.Text = items["f_stardate"].ToString().Substring(1, 10);
                ui_enddate.Text = items["f_enddate"].ToString().Substring(1, 10);
                ui_grossproceeds.Text = items["f_totalcost"].ToString();
                ui_preamount.Text = Math.Round(double.Parse(items["f_chargenum"].ToString()), 2).ToString();
                ui_totalcost.Text = Math.Round(double.Parse(items["f_totalcost"].ToString()), 2).ToString();
            }
            else
            {
                ui_chargeBusy.IsBusy = false;
                MessageBox.Show(e.Error.Message);
            }
        }
	}
}