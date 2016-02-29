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
using Com.Aote.Utils;


namespace Com.Aote.Pages
{
    public partial class 民用小区管理 : UserControl
    {
        private void save12_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject go = ybuserfile.DataContext as GeneralObject;
            Dictionary<String, String> dict = go._errors;
            String err = "";
            foreach (String key in dict.Keys)
            {
                err += key + ":" + dict[key] + "\n";
            }
            MessageBox.Show(err);
        }

        public 民用小区管理()
        {
            // Required to initialize variables
            InitializeComponent();
            this.Loaded += 民用小区管理_Loaded;
            kbfee = (ObjectList)(from r in loader.Res where r.Name.Equals("SecondStairlist") select r).First();
            ThirdStairStairlist1 = (ObjectList)(from r in loader.Res where r.Name.Equals("ThirdStairStairlist") select r).First();
            FourthStairlist1 = (ObjectList)(from r in loader.Res where r.Name.Equals("FourthStairlist") select r).First();
        }
        ObjectList kbfee;
        ObjectList ThirdStairStairlist1;
        ObjectList FourthStairlist1;
        //ObjectList czylistnull1;
        GeneralObject loginUser;
        void 民用小区管理_Loaded(object sender, RoutedEventArgs e)
        {
            loginUser = (GeneralObject)FrameworkElementExtension.FindResource(this, "LoginUser");

            kbfee.DataLoaded += kbfee_DataLoaded;
            ThirdStairStairlist1.DataLoaded += ThirdStairStairlist1_DataLoaded;
            FourthStairlist1.DataLoaded += FourthStairlist1_DataLoaded;
        }
        void ThirdStairStairlist1_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (ThirdStairStairlist1.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 3)
                {
                    ThirdStair.SelectedValue = str[2];
                }

            }
        }
        void FourthStairlist1_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (FourthStairlist1.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 4)
                {
                    FourthStair.SelectedValue = str[3];
                }

            }
        }
        void kbfee_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (kbfee.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 2)
                {
                    SecondStair.SelectedValue = str[1];
                }

            }
        }
    }
}