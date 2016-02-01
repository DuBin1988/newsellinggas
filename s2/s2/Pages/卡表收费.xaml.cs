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
using System.Linq;
using System.Net;
using System.Json;
using Com.Aote.Behaviors;
using Com.Aote.Controls;

namespace Com.Aote.Pages
{
    public partial class 卡表收费 : UserControl
    {
        GeneralObject goPopup = new GeneralObject();

        PagedList listwh = new PagedList();

        public 卡表收费()
        {
            InitializeComponent();
            goPopup.AddProperty("PreGasOnCard");
            goPopup.AddProperty("GasAddedOn");
            goPopup.AddProperty("CurGasOnCard");
            goPopup.AddProperty("GasPurchased");
            goPopup.AddProperty("IsBalanced");
            goPopup.AddProperty("IsNotBalanced");
            goPopup.AddProperty("IsOccupied");
            goPopup.AddProperty("Hint");
        }


        string userid = "";

        private void ui_pregas_LostFocus(object sender, RoutedEventArgs e)
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
                ui_OrdinaryNum.Text = items["f_allamont"].ToString();
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

        private void ui_grossproceeds_LostFocus(object sender, RoutedEventArgs e)
        {
            ui_chargeBusy.IsBusy = true;
            string grossproceeds = ui_grossproceeds.Text;
            if (f_userid.Text.Equals(""))
            {
                MessageBox.Show("请先读卡！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            else if (grossproceeds.Equals(""))
            {
                MessageBox.Show("请输入预购金额！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            userid = f_userid.Text;
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["chargeserver"];
            string str = wci.BaseAddress + "/fee/" + userid + "/" + grossproceeds + "?uuid=" + System.Guid.NewGuid().ToString();
            Uri uri = new Uri(str);
            WebClient client1 = new WebClient();
            client1.DownloadStringCompleted += client1_DownloadStringCompleted;
            client1.DownloadStringAsync(uri);
        }

        double pregas = 0;
        private void client1_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            ui_chargeBusy.IsBusy = false;
            if (e.Error == null)
            {
                JsonObject items = JsonValue.Parse(e.Result) as JsonObject;
                pregas = Math.Floor(double.Parse(items["chargeamont"].ToString()));
                WebClientInfo wci = (WebClientInfo)Application.Current.Resources["chargeserver"];
                string str = wci.BaseAddress + "/num/" + userid + "/" + pregas + "?uuid=" + System.Guid.NewGuid().ToString();
                Uri uri = new Uri(str);
                WebClient client2 = new WebClient();
                client2.DownloadStringCompleted += client2_DownloadStringCompleted;
                client2.DownloadStringAsync(uri);

            }
            else
            {
                ui_chargeBusy.IsBusy = false;
                MessageBox.Show(e.Error.Message);
            }
        }

        private void client2_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
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
                ui_preamount.Text = Math.Round(double.Parse(items["f_chargenum"].ToString()), 2).ToString();
                ui_totalcost.Text = Math.Round(double.Parse(items["f_totalcost"].ToString()), 2).ToString();
                ui_pregas.Text = pregas.ToString();

            }
            else
            {
                ui_chargeBusy.IsBusy = false;
                MessageBox.Show(e.Error.Message);
            }
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



        private void fapiaoNum1_TextChanged(object sender, TextChangedEventArgs e)
        {
            // FapiaoNum.Text =  (int.Parse(fapiaoNum1.Text)).ToString("D8");
        }

        /// <summary>
        /// 界面写卡成功后
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void NewGeneralICCard_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            //显示校验框
            VerificationPopUp.Visibility = Visibility.Visible;
            goPopup.SetPropertyValue("PreGasOnCard", TextGasOnCard.Text, true);
            GeneralObject go = kbfee1.DataContext as GeneralObject;
            goPopup.SetPropertyValue("GasPurchased", go.GetPropertyValue("f_pregas").ToString(), true);
            if (go.GetPropertyValue("f_surplusgas") == null)
                goPopup.SetPropertyValue("GasAddedOn", "0", true);
            else
                goPopup.SetPropertyValue("GasAddedOn", go.GetPropertyValue("f_surplusgas").ToString(), true);
            goPopup.SetPropertyValue("IsBalanced", "False", true);
            goPopup.SetPropertyValue("IsNotBalanced", "False", true);
            goPopup.SetPropertyValue("IsOccupied", "True", true);
            goPopup.SetPropertyValue("Hint", "", true);
            VerificationPopUp.DataContext = goPopup;
            //读卡
            ReadChip(e, null);
        }

        //写卡
        private void WriteChip(object sender, RoutedEventArgs e)
        {
            goPopup.SetPropertyValue("IsOccupied", "True", true);
            NewGeneralICCard chip = (from p in loader.Res where p.Name.Equals("chip") select p).First() as NewGeneralICCard;
            chip.BuyTimes = card.BuyTimes;
            chip.Completed += chip_Completed;
            chip.SellGas();
        }

        void chip_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            NewGeneralICCard card = (from p in loader.Res where p.Name.Equals("chip") select p).First() as NewGeneralICCard;
            chip.Completed -= chip_Completed;
            //继续读卡
            ReadChip(e, null);
        }

        //读卡
        private void ReadChip(object sender, RoutedEventArgs e)
        {
            goPopup.SetPropertyValue("IsOccupied", "True", true);
            NewGeneralICCard chip = (from p in loader.Res where p.Name.Equals("chip") select p).First() as NewGeneralICCard;
            NewGeneralICCard card = (from p in loader.Res where p.Name.Equals("card") select p).First() as NewGeneralICCard;
            chip.ReadCompleted += chip_ReadCompleted;
            chip.ReadCard();
        }

        void chip_ReadCompleted(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            NewGeneralICCard chip = (from p in loader.Res where p.Name.Equals("chip") select p).First() as NewGeneralICCard;
            chip.ReadCompleted -= chip_ReadCompleted;
            goPopup.SetPropertyValue("IsOccupied", "False", true);
            if (chip.State == State.LoadError)
            {
                goPopup.SetPropertyValue("Hint", "卡操作失败，请重试。", true);
                ButtonReadChip.IsEnabled = true;
            }
            else
            {
                goPopup.SetPropertyValue("Hint", "", true);
                goPopup.SetPropertyValue("CurGasOnCard", chip.Gas.ToString(), true);
                int PreGasOnCard = int.Parse(goPopup.GetPropertyValue("PreGasOnCard").ToString());
                int GasPurchased = int.Parse(goPopup.GetPropertyValue("GasPurchased").ToString());
                int preGasOnCard = int.Parse(goPopup.GetPropertyValue("GasAddedOn").ToString());
                if (chip.Gas == PreGasOnCard + GasPurchased + preGasOnCard)
                {
                    goPopup.SetPropertyValue("IsBalanced", "True", true);
                    goPopup.SetPropertyValue("IsNotBalanced", "False", true);
                }
                else
                {
                    goPopup.SetPropertyValue("IsBalanced", "False", true);
                    goPopup.SetPropertyValue("IsNotBalanced", "True", true);
                }
                //为下次写做准备
                chip.Gas = PreGasOnCard + GasPurchased + preGasOnCard;
            }
        }

        void Print(object sender, RoutedEventArgs e)
        {
            WebClientInfo wci = Application.Current.Resources["dbclient"] as WebClientInfo;
            WebClient client = new WebClient();
            client.UploadStringCompleted += wc_UploadStringCompleted;
            client.UploadStringAsync(new Uri(wci.BaseAddress), String.Format("[{{\"operator\":\"sql\",\"data\":\"update t_fapiaoinfos set f_fapiaostatue='已用' where f_invoicenum={0}\"}}]", fapiaoNum1.Text));
        }

        void wc_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            (sender as WebClient).UploadStringCompleted -= wc_UploadStringCompleted;
            print.State = State.Start;
            print.Print();
            VerificationPopUp.Visibility = Visibility.Collapsed;
        }

        void NoPrint(object sender, RoutedEventArgs e)
        {
            //打印状态变成End，清界面
            print.State = State.Start;
            print.State = State.End;
            VerificationPopUp.Visibility = Visibility.Collapsed;
        }
    }
}