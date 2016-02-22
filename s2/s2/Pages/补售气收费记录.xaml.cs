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
    public partial class 补售气收费记录 : UserControl
    {
        public 补售气收费记录()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        private void TextBox_LostFocus(object sender, RoutedEventArgs e)
        {
            ui_chargeBusy.IsBusy = true;
            string userid = f_userid.Text;
            string pregas = ui_pregas.Text;
            string usertype = f_usertype.Text;
            if (userid.Equals(""))
            {
                MessageBox.Show("请先读卡！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            else if (pregas.Equals(""))
            {
                MessageBox.Show("请输入预购气量！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["priceserver"];
            string str = wci.BaseAddress + "/num/" + "2" + "/" + userid + "/" + pregas + "/" + usertype + "?uuid=" + System.Guid.NewGuid().ToString();
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);
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
                //ui_stair4amont.Text = items["f_stair4amount"].ToString();
                ui_stair1fee.Text = items["f_stair1fee"].ToString();
                ui_stair2fee.Text = items["f_stair2fee"].ToString();
                ui_stair3fee.Text = items["f_stair3fee"].ToString();
                //ui_stair4fee.Text = items["f_stair4fee"].ToString();
                ui_stair1price.Text = items["f_stair1price"].ToString();
                ui_stair2price.Text = items["f_stair2price"].ToString();
                ui_stair3price.Text = items["f_stair3price"].ToString();


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