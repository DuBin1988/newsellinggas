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
    public partial class GasMainPage : UserControl
    {
        private int clickcount=0;
        public GasMainPage()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject go = (GeneralObject)Application.Current.Resources["LoginUser"];
            go.GetPropertyValue("functions");
            clickcount = 0;
        }
		// tab关闭
		private void Button_Click1(object sender, RoutedEventArgs e)
        {
             Button b = sender as Button;
             if (this.tab.Items.Count > 1)
             {
                 //this.tab.Items.RemoveAt(Convert.ToInt32(b.Tag));
                 this.tab.Items.RemoveAt(Convert.ToInt32(b.Tag));
                 clickcount = 0;
             }
             else {
                 clickcount++;
             }
            if(clickcount>18)
             MessageBox.Show("这个，你是关闭不了的！你都点击了"+clickcount+"次了！");
        }
    }
}