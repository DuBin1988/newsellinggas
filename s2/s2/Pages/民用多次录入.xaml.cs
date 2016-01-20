using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Windows.Browser;
using Com.Aote.ObjectTools;

namespace Com.Aote.Pages
{
    public partial class 民用多次录入 : UserControl
    {
        public 民用多次录入()
        {
            InitializeComponent();
        }

        private void save_Click(object sender, RoutedEventArgs e)
        {
            LeftGridBusy.IsBusy = true;
            GeneralObject go = handUserUnit.DataContext as GeneralObject;
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string str = "";
            Uri uri= new Uri(str);
            WebClient client = new WebClient();
            client.UploadStringCompleted += client_UploadStringCompleted;
            client.UploadStringAsync(uri, "aa");
            
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            throw new NotImplementedException();
        }

        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            throw new NotImplementedException();
        }
    }
}