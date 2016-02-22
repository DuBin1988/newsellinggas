﻿using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
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
    public partial class 用户状态更改 : UserControl
    {
        public 用户状态更改()
        {
            // Required to initialize variables
            InitializeComponent();
        }
        private void save12_Click(object sender, RoutedEventArgs e)
        {
            GeneralObject go = userfile.DataContext as GeneralObject;
            Dictionary<String, String> dict = go._errors;
            String err = "";
            foreach (String key in dict.Keys)
            {
                err += key + ":" + dict[key] + "\n";
            }
            MessageBox.Show(err);
        }
    }
}