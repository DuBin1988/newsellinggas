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
using System.Collections.Generic;

namespace Com.Aote.Pages
{
    public partial class 卡表预存 : UserControl
    {
        

        public 卡表预存()
        {
            InitializeComponent();
        }

        private void ui_grossproceeds_lostfocus(object sender, RoutedEventArgs e)
        {
            f_benqizhye.Text = (double.Parse(ui_grossproceeds.Text) * double.Parse(f_zhye.Text)).ToString();
        }

        private void save12_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject go = kbfee1.DataContext as GeneralObject;
            Dictionary<String, String> dict = go._errors;
            String err = "";
            foreach (String key in dict.Keys)
            {
                err += key + ":" + dict[key] + "\n";
            }
            MessageBox.Show(err);
        }


        private void fapiaoNum1_TextChanged(object sender, TextChangedEventArgs e)
        {
            // FapiaoNum.Text =  (int.Parse(fapiaoNum1.Text)).ToString("D8");
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
            print.TipPrint();
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